package net.vampirestudios.raaMaterials.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.content.ParametricCrystalClusterBlock;
import net.vampirestudios.raaMaterials.content.ParametricSpikeBlock;
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

        // Second pass: spike columns for materials with Form.SPIKE
        var fc = RAAConfig.active().formChances();
        for (int matIdx = 0; matIdx < max; matIdx++) {
            MaterialDef def = materials.get(matIdx);
            if (!def.forms().contains(Form.SPIKE)) continue;

            SpawnInfo spawn = def.spawn();
            int attempts = Math.min(spawn.attemptsPerChunk(), 3);
            for (int attempt = 0; attempt < attempts; attempt++) {
                if (ctx.random().nextFloat() >= fc.spikeWorldgenChance()) continue;
                int x = ctx.origin().getX() + ctx.random().nextInt(16);
                int z = ctx.origin().getZ() + ctx.random().nextInt(16);
                int y = sampleY(ctx.random(), spawn.y());
                placedAny |= placeSpikeColumn(ctx, new BlockPos(x, y, z), def, matIdx);
            }
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
            if (!passesEnvironmentRules(ctx.level(), pos, spawn, plan.geode())) {
                continue;
            }

            placedAny |= plan.geode()          ? placeGeode(ctx, pos, plan, spawn)
                       : plan.crystalCluster() ? placeCrystalCluster(ctx, pos, plan, spawn)
                       : plan.disk()           ? placeDisk(ctx, pos, plan, spawn)
                       :                         placeOre(ctx, pos, plan, spawn);
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

    private boolean placeSpikeColumn(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos origin,
            MaterialDef def,
            int matIdx
    ) {
        WorldGenLevel level = ctx.level();
        RandomSource rng = ctx.random();

        // Randomly hang from ceiling (DOWN) or grow from floor (UP)
        boolean fromCeiling = rng.nextBoolean();
        Direction searchDir = fromCeiling ? Direction.UP : Direction.DOWN;
        Direction growDir = fromCeiling ? Direction.DOWN : Direction.UP;

        // Scan up to 16 blocks to find a solid surface with air on the growth side
        BlockPos surface = null;
        for (int i = 0; i < 16; i++) {
            BlockPos test = origin.relative(searchDir, i);
            BlockPos airSide = test.relative(growDir);
            if (level.getBlockState(test).isSolid() && level.isEmptyBlock(airSide)) {
                surface = test;
                break;
            }
        }
        if (surface == null) return false;

        int height = 1 + rng.nextInt(RAAConfig.active().formChances().spikeMaxHeight());
        BlockState spikeBase = YBlocks.PARAM_SPIKE.defaultBlockState()
                .setValue(ParametricBlock.MAT, matIdx)
                .setValue(ParametricSpikeBlock.VERTICAL_DIRECTION, growDir)
                .setValue(ParametricSpikeBlock.WATERLOGGED, false);

        for (int i = 0; i < height; i++) {
            BlockPos bp = surface.relative(growDir, i + 1);
            if (!level.isEmptyBlock(bp)) return false;
            DripstoneThickness thickness = spikeThickness(height, i);
            level.setBlock(bp, spikeBase.setValue(ParametricSpikeBlock.THICKNESS, thickness), 2);
        }
        return true;
    }

    private static DripstoneThickness spikeThickness(int height, int index) {
        if (index == height - 1) return DripstoneThickness.TIP;
        if (index == 0 && height > 1) return DripstoneThickness.BASE;
        if (index == height - 2) return DripstoneThickness.FRUSTUM;
        return DripstoneThickness.MIDDLE;
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

    // Geode: hollow sphere with smooth basalt shell → calcite ring → crystal block inner wall → air cavity.
    // Radius is rolled per-spawn from [veinMin, veinMax], giving natural size variation.
    private boolean placeGeode(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos center,
            GenerationPlan plan,
            SpawnInfo spawn
    ) {
        RandomSource rng = ctx.random();
        WorldGenLevel level = ctx.level();

        int radius = spawn.veinMin() + rng.nextInt(Math.max(1, spawn.veinMax() - spawn.veinMin() + 1));

        // Layer boundaries from the center outward.
        // For small geodes (radius < 6) the shell is thinner so there's still an air cavity.
        double shellInner   = radius - (radius >= 6 ? 2.0 : 1.5);
        double calciteInner = shellInner - 1.0;
        double crystalInner = Math.max(0.0, calciteInner - 1.0);

        BlockState crystalBlock = plan.state(); // PARAM_CRYSTAL_BLOCK with correct MAT index

        // Phase 1: carve out the layered sphere, overwriting whatever is there.
        for (var bp : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            double dist = Math.sqrt(center.distSqr(bp));
            if (dist > radius) continue;

            BlockState target;
            if (dist > shellInner) {
                target = Blocks.SMOOTH_BASALT.defaultBlockState();
            } else if (dist > calciteInner) {
                target = Blocks.CALCITE.defaultBlockState();
            } else if (crystalInner > 0 && dist > crystalInner) {
                // Inner crystal wall: geodeCrystalWallChance crystal block, rest bare calcite for variety
                target = rng.nextFloat() < RAAConfig.active().formChances().geodeCrystalWallChance() ? crystalBlock : Blocks.CALCITE.defaultBlockState();
            } else {
                target = Blocks.AIR.defaultBlockState();
            }

            level.setBlock(bp, target, 2);
        }

        // Phase 2: grow crystal clusters on any air block that borders the crystal/calcite wall.
        // The cluster's FACING points away from its anchor (toward the open cavity interior).
        BlockState clusterBase = YBlocks.PARAM_CLUSTER.defaultBlockState()
                .setValue(ParametricBlock.MAT, plan.matIdx());

        for (var bp : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            if (!level.isEmptyBlock(bp)) continue;

            for (Direction dir : Direction.values()) {
                BlockPos wall = bp.relative(dir);
                BlockState wallState = level.getBlockState(wall);
                if (wallState.is(YBlocks.PARAM_CRYSTAL_BLOCK) || wallState.is(Blocks.CALCITE)) {
                    if (rng.nextFloat() < RAAConfig.active().formChances().geodeCrystalClusterChance()) {
                        // dir is from the air block toward the wall; the crystal grows the other way
                        level.setBlock(bp, clusterBase
                                .setValue(ParametricCrystalClusterBlock.FACING, dir.getOpposite()), 2);
                    }
                    break; // at most one cluster per air block
                }
            }
        }

        return true;
    }

    // Crystal cluster patch: an ore-sized blob of crystal blocks with clusters sprouting on exposed faces.
    private boolean placeCrystalCluster(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos center,
            GenerationPlan plan,
            SpawnInfo spawn
    ) {
        RandomSource rng = ctx.random();
        WorldGenLevel level = ctx.level();

        var config = new OreConfiguration(
                plan.def().host().toRuleTest(),
                plan.state(), // PARAM_CRYSTAL_BLOCK with correct MAT
                oreSize(rng, plan, spawn),
                0.0f
        );

        if (!Feature.ORE.place(config, level, ctx.chunkGenerator(), rng, center)) {
            return false;
        }

        // Scan for placed crystal blocks and grow clusters on air-facing faces.
        int scan = Math.max(spawn.veinMin(), spawn.veinMax() / 2);
        BlockState clusterBase = YBlocks.PARAM_CLUSTER.defaultBlockState()
                .setValue(ParametricBlock.MAT, plan.matIdx());

        for (var bp : BlockPos.betweenClosed(
                center.offset(-scan, -scan, -scan),
                center.offset(scan, scan, scan))) {
            BlockState bs = level.getBlockState(bp);
            if (!bs.is(YBlocks.PARAM_CRYSTAL_BLOCK)) continue;
            if (bs.getValue(ParametricBlock.MAT) != plan.matIdx()) continue;

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = bp.relative(dir);
                if (level.isEmptyBlock(neighbor) && rng.nextFloat() < RAAConfig.active().formChances().crystalClusterFillChance()) {
                    // dir is from the crystal block toward the air; cluster grows that way
                    level.setBlock(neighbor, clusterBase
                            .setValue(ParametricCrystalClusterBlock.FACING, dir), 2);
                }
            }
        }

        return true;
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

    private boolean passesEnvironmentRules(WorldGenLevel level, BlockPos pos, SpawnInfo spawn, boolean isGeode) {
        // Geodes create their own hollow interior, so the mustTouchAir check is skipped for them.
        return (isGeode || !spawn.mustTouchAir() || nearAir(level, pos))
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
            int matIdx,
            BlockState state,
            boolean terrainBlock,
            boolean disk,
            boolean geode,
            boolean crystalCluster
    ) {
        static Optional<GenerationPlan> create(MaterialDef def, int matIdx) {
            boolean hasOre         = def.forms().contains(Form.ORE);
            boolean hasTerrainBlock = def.forms().contains(Form.BLOCK) && generatesTerrainBlock(def.kind());
            boolean hasCrystal     = def.forms().contains(Form.CLUSTER);
            boolean isGeode        = hasCrystal && def.spawn().shape() == SpawnInfo.VeinShape.GEODE;
            boolean isCrystalCluster = hasCrystal && def.spawn().shape() == SpawnInfo.VeinShape.CRYSTAL_CLUSTER;

            if (!hasOre && !hasTerrainBlock && !isGeode && !isCrystalCluster) {
                return Optional.empty();
            }

            Block block;
            if (hasOre)              block = YBlocks.PARAM_ORE;
            else if (hasTerrainBlock) block = YBlocks.PARAM_BLOCK;
            else                     block = YBlocks.PARAM_CRYSTAL_BLOCK;

            BlockState state = block.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);
            boolean terrainBlock = !hasOre && hasTerrainBlock && !isGeode && !isCrystalCluster;
            boolean disk = def.spawn().shape() == SpawnInfo.VeinShape.ORE_DISK
                    && isLooseSurfaceMaterial(def.kind());

            return Optional.of(new GenerationPlan(def, def.kind(), matIdx, state, terrainBlock, disk, isGeode, isCrystalCluster));
        }
    }
}
