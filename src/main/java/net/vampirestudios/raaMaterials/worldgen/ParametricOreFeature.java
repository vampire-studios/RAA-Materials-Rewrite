package net.vampirestudios.raaMaterials.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;
import net.vampirestudios.raaMaterials.material.SpawnInfo;
import net.vampirestudios.raaMaterials.registry.YBlocks;

import java.util.Optional;

public class ParametricOreFeature extends Feature<NoneFeatureConfiguration> {
    private static final int MAX_MATERIALS = ParametricBlock.MAX_MATERIAL_STATE + 1;
    private static final int ENV_SCAN_XZ = 4;
    private static final int ENV_SCAN_Y = 2;

    public ParametricOreFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        var materials = MaterialRegistry.all(ctx.level().getLevel());
        if (materials.isEmpty()) {
            return false;
        }

        boolean placedAny = false;
        int max = Math.min(materials.size(), MAX_MATERIALS);

        for (int matIdx = 0; matIdx < max; matIdx++) {
            var plan = GenerationPlan.create(materials.get(matIdx), matIdx);
            if (plan.isEmpty()) {
                continue;
            }

            placedAny |= placeMaterial(ctx, plan.get());
        }

        return placedAny;
    }

    private boolean placeMaterial(FeaturePlaceContext<NoneFeatureConfiguration> ctx, GenerationPlan plan) {
        SpawnInfo spawn = plan.def().spawn();

        boolean placedAny = false;
        int attempts = attempts(plan, spawn);
        float chance = successChance(plan, spawn);

        for (int attempt = 0; attempt < attempts; attempt++) {
            if (ctx.random().nextFloat() >= chance) {
                continue;
            }

            BlockPos pos = samplePosition(ctx.level(), ctx.origin(), ctx.random(), plan, spawn);
            if (!passesEnvironmentRules(ctx.level(), pos, spawn)) {
                continue;
            }

            placedAny |= plan.disk()
                    ? placeDisk(ctx, pos, plan, spawn)
                    : placeOre(ctx, pos, plan, spawn);
        }

        return placedAny;
    }

    private BlockPos samplePosition(
            WorldGenLevel level,
            BlockPos origin,
            RandomSource rng,
            GenerationPlan plan,
            SpawnInfo spawn
    ) {
        int x = origin.getX() + rng.nextInt(16);
        int z = origin.getZ() + rng.nextInt(16);

        if (plan.disk()) {
            int topY = surfaceY(level, x, z);
            if (withinY(topY, spawn)) {
                return new BlockPos(x, topY, z);
            }
        }

        int y = sampleY(rng, spawn.y());

        if (plan.terrainBlock()) {
            int topY = surfaceY(level, x, z);
            y = Math.min(y, topY - undergroundPadding(plan.kind()));
            y = Math.max(y, spawn.y().minY());
        }

        return new BlockPos(x, y, z);
    }

    private int sampleY(RandomSource rng, SpawnInfo.YDistribution dist) {
        int spread = dist.spread();
        int offset = rng.nextInt(spread * 2 + 1) - spread;
        return Mth.clamp(dist.centerY() + offset, dist.minY(), dist.maxY());
    }

    private int surfaceY(WorldGenLevel level, int x, int z) {
        return level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
    }

    private boolean withinY(int y, SpawnInfo spawn) {
        return y >= spawn.y().minY() && y <= spawn.y().maxY();
    }

    private boolean placeOre(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos pos,
            GenerationPlan plan,
            SpawnInfo spawn
    ) {
        var config = new OreConfiguration(
                plan.def().host().toRuleTest(),
                plan.state(),
                oreSize(ctx.random(), plan, spawn),
                0.0f
        );

        return Feature.ORE.place(config, ctx.level(), ctx.chunkGenerator(), ctx.random(), pos);
    }

    private boolean placeDisk(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos pos,
            GenerationPlan plan,
            SpawnInfo spawn
    ) {
        var config = new DiskConfiguration(
                BlockStateProvider.simple(plan.state()),
                diskTarget(plan.def()),
                UniformInt.of(diskRadiusMin(plan.kind(), spawn), diskRadiusMax(plan.kind(), spawn)),
                1
        );

        return Feature.DISK.place(config, ctx.level(), ctx.chunkGenerator(), ctx.random(), pos);
    }

    private int oreSize(RandomSource rng, GenerationPlan plan, SpawnInfo spawn) {
        int min = spawn.veinMin();
        int max = spawn.veinMax();

        if (plan.terrainBlock()) {
            min = terrainPatchMin(plan.kind(), spawn);
            max = terrainPatchMax(plan.kind(), spawn);
        }

        return min + rng.nextInt(Math.max(1, max - min + 1));
    }

    private int terrainPatchMin(MaterialKind kind, SpawnInfo spawn) {
        return switch (kind) {
            case STONE -> Math.max(spawn.veinMin() * 2, 16);
            case VOLCANIC -> Math.max(spawn.veinMin() * 2, 14);
            case SALT -> Math.max(spawn.veinMin() * 2, 12);
            default -> Math.max(spawn.veinMin(), 6);
        };
    }

    private int terrainPatchMax(MaterialKind kind, SpawnInfo spawn) {
        return switch (kind) {
            case STONE -> Math.min(Math.max(spawn.veinMax() * 2, 36), 56);
            case VOLCANIC -> Math.min(Math.max(spawn.veinMax() * 2, 30), 46);
            case SALT -> Math.min(Math.max(spawn.veinMax() * 2, 26), 40);
            default -> Math.min(Math.max(spawn.veinMax(), 18), 28);
        };
    }

    private int attempts(GenerationPlan plan, SpawnInfo spawn) {
        if (isLooseSurfaceMaterial(plan.kind())) {
            return 1;
        }

        if (plan.terrainBlock()) {
            return Math.min(spawn.attemptsPerChunk(), 2);
        }

        return spawn.attemptsPerChunk();
    }

    private float successChance(GenerationPlan plan, SpawnInfo spawn) {
        if (isLooseSurfaceMaterial(plan.kind())) {
            return spawn.successChance() * 0.08f;
        }

        if (plan.terrainBlock()) {
            return spawn.successChance() * 0.35f;
        }

        return spawn.successChance();
    }

    private int diskRadiusMin(MaterialKind kind, SpawnInfo spawn) {
        return switch (kind) {
            case SAND, GRAVEL, CLAY, MUD, SOIL -> Math.min(spawn.veinMin(), 2);
            default -> spawn.veinMin();
        };
    }

    private int diskRadiusMax(MaterialKind kind, SpawnInfo spawn) {
        return switch (kind) {
            case SAND, GRAVEL -> Math.min(spawn.veinMax(), 4);
            case CLAY, MUD, SOIL -> Math.min(spawn.veinMax(), 3);
            default -> spawn.veinMax();
        };
    }

    private BlockPredicate diskTarget(MaterialDef def) {
        return switch (def.kind()) {
            case SAND -> BlockPredicate.matchesBlocks(Blocks.SAND, Blocks.RED_SAND);
            case GRAVEL -> BlockPredicate.matchesBlocks(Blocks.GRAVEL);
            case CLAY -> BlockPredicate.matchesBlocks(Blocks.CLAY);
            case MUD -> BlockPredicate.matchesBlocks(Blocks.MUD);
            case SOIL -> BlockPredicate.matchesBlocks(
                    Blocks.DIRT,
                    Blocks.GRASS_BLOCK,
                    Blocks.COARSE_DIRT,
                    Blocks.PODZOL,
                    Blocks.ROOTED_DIRT,
                    Blocks.MYCELIUM
            );
            case SALT -> BlockPredicate.anyOf(
                    targetPredicate(def.host().target()),
                    BlockPredicate.matchesBlocks(
                            Blocks.SAND,
                            Blocks.RED_SAND,
                            Blocks.SANDSTONE,
                            Blocks.RED_SANDSTONE,
                            Blocks.GRAVEL
                    )
            );
            default -> targetPredicate(def.host().target());
        };
    }

    private BlockPredicate targetPredicate(MaterialDef.OreHost.Target target) {
        if (target.isTag()) {
            return BlockPredicate.matchesTag(TagKey.create(Registries.BLOCK, target.id()));
        }

        return BlockPredicate.matchesBlocks(BuiltInRegistries.BLOCK.getValue(target.id()));
    }

    private boolean passesEnvironmentRules(WorldGenLevel level, BlockPos pos, SpawnInfo spawn) {
        return (!spawn.mustTouchAir() || nearAir(level, pos))
                && (!spawn.nearWater() || nearBlock(level, pos, Blocks.WATER))
                && (!spawn.nearLava() || nearBlock(level, pos, Blocks.LAVA));
    }

    private boolean nearAir(WorldGenLevel level, BlockPos pos) {
        for (BlockPos scan : scanArea(pos)) {
            if (level.isEmptyBlock(scan)) {
                return true;
            }
        }

        return false;
    }

    private boolean nearBlock(WorldGenLevel level, BlockPos pos, Block block) {
        for (BlockPos scan : scanArea(pos)) {
            if (level.getBlockState(scan).is(block)) {
                return true;
            }
        }

        return false;
    }

    private Iterable<BlockPos> scanArea(BlockPos pos) {
        return BlockPos.betweenClosed(
                pos.offset(-ENV_SCAN_XZ, -ENV_SCAN_Y, -ENV_SCAN_XZ),
                pos.offset(ENV_SCAN_XZ, ENV_SCAN_Y, ENV_SCAN_XZ)
        );
    }

    private static int undergroundPadding(MaterialKind kind) {
        return switch (kind) {
            case STONE, VOLCANIC, SALT -> 10;
            default -> 4;
        };
    }

    private static boolean generatesTerrainBlock(MaterialKind kind) {
        return switch (kind) {
            case STONE, SAND, GRAVEL, CLAY, MUD, SOIL, SALT, VOLCANIC -> true;
            default -> false;
        };
    }

    private static boolean isLooseSurfaceMaterial(MaterialKind kind) {
        return switch (kind) {
            case SAND, GRAVEL, CLAY, MUD, SOIL -> true;
            default -> false;
        };
    }

    private record GenerationPlan(
            MaterialDef def,
            MaterialKind kind,
            BlockState state,
            boolean terrainBlock,
            boolean disk
    ) {
        static Optional<GenerationPlan> create(MaterialDef def, int matIdx) {
            boolean hasOre = def.forms().contains(Form.ORE);
            boolean hasTerrainBlock = def.forms().contains(Form.BLOCK) && generatesTerrainBlock(def.kind());

            if (!hasOre && !hasTerrainBlock) {
                return Optional.empty();
            }

            Block block = hasOre ? YBlocks.PARAM_ORE : YBlocks.PARAM_BLOCK;
            BlockState state = block.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);

            boolean terrainBlock = !hasOre && hasTerrainBlock;
            boolean disk = def.spawn().shape() == SpawnInfo.VeinShape.ORE_DISK
                    && isLooseSurfaceMaterial(def.kind());

            return Optional.of(new GenerationPlan(def, def.kind(), state, terrainBlock, disk));
        }
    }
}
