package net.vampirestudios.raaMaterials.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.material.*;
import net.vampirestudios.raaMaterials.registry.YBlocks;

import java.util.Optional;

/**
 * The single registered feature that drives all parametric material worldgen per chunk.
 * <p>
 * Placement is split across three focused helpers:
 * <ul>
 *   <li>{@link ParametricOrePlacer}    — ore blobs, disks, terrain patches</li>
 *   <li>{@link ParametricCrystalPlacer} — geode spheres and crystal clusters</li>
 *   <li>{@link ParametricSpikePlacer}  — speleothem (stalactite/stalagmite) clusters</li>
 * </ul>
 */
public class ParametricOreFeature extends Feature<NoneFeatureConfiguration> {

    private static final int MAX_MATERIALS = ParametricBlock.MAX_MATERIAL_STATE + 1;
    private static final int ENV_SCAN_XZ   = 4;
    private static final int ENV_SCAN_Y    = 2;

    public ParametricOreFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    // =========================================================================
    // Main placement loop
    // =========================================================================

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        var materials = MaterialRegistry.all(ctx.level().getLevel());
        if (materials.isEmpty()) return false;

        boolean placedAny = false;
        int max = Math.min(materials.size(), MAX_MATERIALS);

        // Pass 1: ores, terrain blocks, geodes, crystal clusters
        for (int matIdx = 0; matIdx < max; matIdx++) {
            var plan = GenerationPlan.create(materials.get(matIdx), matIdx);
            if (plan.isEmpty()) continue;
            placedAny |= placeMaterial(ctx, plan.get());
        }

        // Pass 2: speleothem clusters for materials that have Form.SPIKE
        // Resolve the stone tag once per decoration call (O(1), shared across all materials)
        HolderSet<Block> stoneTag = ctx.level().registryAccess()
                .lookupOrThrow(Registries.BLOCK)
                .getOrThrow(BlockTags.BASE_STONE_OVERWORLD);

        var fc = RAAConfig.active().formChances();
        for (int matIdx = 0; matIdx < max; matIdx++) {
            MaterialDef def = materials.get(matIdx);
            if (!def.forms().contains(Form.SPIKE)) continue;

            SpawnInfo spawn   = def.spawn();
            int attempts      = Math.min(spawn.attemptsPerChunk(), 3);
            for (int attempt = 0; attempt < attempts; attempt++) {
                if (ctx.random().nextFloat() >= fc.spikeWorldgenChance()) continue;
                int x = ctx.origin().getX() + ctx.random().nextInt(16);
                int z = ctx.origin().getZ() + ctx.random().nextInt(16);
                int y = sampleY(ctx.random(), spawn.y());
                placedAny |= ParametricSpikePlacer.placeCluster(ctx, new BlockPos(x, y, z), matIdx, stoneTag);
            }
        }

        return placedAny;
    }

    private boolean placeMaterial(FeaturePlaceContext<NoneFeatureConfiguration> ctx, GenerationPlan plan) {
        SpawnInfo spawn    = plan.def().spawn();
        int attempts       = attempts(plan, spawn);
        float chance       = successChance(plan, spawn);
        boolean placedAny  = false;

        for (int attempt = 0; attempt < attempts; attempt++) {
            if (ctx.random().nextFloat() >= chance) continue;

            BlockPos pos = plan.geode()
                    ? sampleGeodePosition(ctx.level(), ctx.origin(), ctx.random(), spawn)
                    : samplePosition(ctx.level(), ctx.origin(), ctx.random(), plan, spawn);

            if (!passesEnvironmentRules(ctx.level(), pos, spawn, plan.geode())) continue;

            placedAny |= plan.geode()          ? ParametricCrystalPlacer.placeGeode(ctx, pos, plan, spawn)
                       : plan.crystalCluster() ? ParametricCrystalPlacer.placeCrystalCluster(ctx, pos, plan, spawn)
                       : plan.disk()           ? ParametricOrePlacer.placeDisk(ctx, pos, plan, spawn)
                       :                         ParametricOrePlacer.placeOre(ctx, pos, plan, spawn);
        }

        return placedAny;
    }

    // =========================================================================
    // Position sampling
    // =========================================================================

    private BlockPos samplePosition(
            WorldGenLevel level, BlockPos origin, RandomSource rng,
            GenerationPlan plan, SpawnInfo spawn
    ) {
        int x = origin.getX() + rng.nextInt(16);
        int z = origin.getZ() + rng.nextInt(16);

        if (plan.disk()) {
            int topY = surfaceY(level, x, z);
            if (withinY(topY, spawn)) return new BlockPos(x, topY, z);
        }

        int y = sampleY(rng, spawn.y());

        if (plan.terrainBlock()) {
            int topY = surfaceY(level, x, z);
            y = Math.min(y, topY - undergroundPadding(plan.kind()));
            y = Math.max(y, spawn.y().minY());
        }

        return new BlockPos(x, y, z);
    }

    private BlockPos sampleGeodePosition(
            WorldGenLevel level, BlockPos origin, RandomSource rng, SpawnInfo spawn
    ) {
        int x         = origin.getX() + rng.nextInt(16);
        int z         = origin.getZ() + rng.nextInt(16);
        int maxRadius = spawn.veinMax();
        int minY      = Math.max(spawn.y().minY(), level.getMinY() + maxRadius + 5);
        int maxY      = Math.min(spawn.y().maxY(), level.getMaxY() - maxRadius - 1);
        int y         = sampleY(rng, spawn.y());

        y = maxY < minY ? spawn.y().centerY() : Mth.clamp(y, minY, maxY);
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

    // =========================================================================
    // Attempt / chance scaling
    // =========================================================================

    private int attempts(GenerationPlan plan, SpawnInfo spawn) {
        if (isLooseSurfaceMaterial(plan.kind())) return 1;
        if (plan.terrainBlock()) return Math.min(spawn.attemptsPerChunk(), 2);
        return spawn.attemptsPerChunk();
    }

    private float successChance(GenerationPlan plan, SpawnInfo spawn) {
        if (isLooseSurfaceMaterial(plan.kind())) return spawn.successChance() * 0.08f;
        if (plan.terrainBlock()) return spawn.successChance() * 0.35f;
        return spawn.successChance();
    }

    // =========================================================================
    // Environment rules
    // =========================================================================

    private boolean passesEnvironmentRules(WorldGenLevel level, BlockPos pos, SpawnInfo spawn, boolean isGeode) {
        // Geodes create their own interior, so mustTouchAir is skipped for them
        return (isGeode || !spawn.mustTouchAir() || nearAir(level, pos))
                && (!spawn.nearWater() || nearBlock(level, pos, Blocks.WATER))
                && (!spawn.nearLava()  || nearBlock(level, pos, Blocks.LAVA));
    }

    private boolean nearAir(WorldGenLevel level, BlockPos pos) {
        for (BlockPos scan : scanArea(pos)) {
            if (level.isEmptyBlock(scan)) return true;
        }
        return false;
    }

    private boolean nearBlock(WorldGenLevel level, BlockPos pos, Block block) {
        for (BlockPos scan : scanArea(pos)) {
            if (level.getBlockState(scan).is(block)) return true;
        }
        return false;
    }

    private Iterable<BlockPos> scanArea(BlockPos pos) {
        return BlockPos.betweenClosed(
                pos.offset(-ENV_SCAN_XZ, -ENV_SCAN_Y, -ENV_SCAN_XZ),
                pos.offset(ENV_SCAN_XZ, ENV_SCAN_Y, ENV_SCAN_XZ)
        );
    }

    // =========================================================================
    // Material-kind helpers (package-private so placer classes can access them)
    // =========================================================================

    static int undergroundPadding(MaterialKind kind) {
        return switch (kind) {
            case STONE, VOLCANIC, SALT -> 10;
            default -> 4;
        };
    }

    static boolean generatesTerrainBlock(MaterialKind kind) {
        return switch (kind) {
            case STONE, SAND, GRAVEL, CLAY, MUD, SOIL, SALT, VOLCANIC -> true;
            default -> false;
        };
    }

    static boolean isLooseSurfaceMaterial(MaterialKind kind) {
        return switch (kind) {
            case SAND, GRAVEL, CLAY, MUD, SOIL -> true;
            default -> false;
        };
    }

    // =========================================================================
    // GenerationPlan — per-material dispatch record
    // =========================================================================

    /**
     * Captures the decision of what to place for a single material this chunk:
     * which block, which placement strategy, and the material index.
     */
    record GenerationPlan(
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
            boolean hasOre          = def.forms().contains(Form.ORE);
            boolean hasTerrainBlock = def.forms().contains(Form.BLOCK) && generatesTerrainBlock(def.kind());
            boolean hasCrystal      = def.forms().contains(Form.CLUSTER);
            boolean isGeode         = hasCrystal && def.spawn().shape() == SpawnInfo.VeinShape.GEODE;
            boolean isCrystalCluster = hasCrystal && def.spawn().shape() == SpawnInfo.VeinShape.CRYSTAL_CLUSTER;

            if (!hasOre && !hasTerrainBlock && !isGeode && !isCrystalCluster) {
                return Optional.empty();
            }

            Block block;
            if (hasOre)               block = YBlocks.PARAM_ORE;
            else if (hasTerrainBlock) block = YBlocks.PARAM_BLOCK;
            else                      block = YBlocks.PARAM_CRYSTAL_BLOCK;

            BlockState state = block.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);
            boolean terrainBlock = !hasOre && hasTerrainBlock && !isGeode && !isCrystalCluster;
            boolean disk = def.spawn().shape() == SpawnInfo.VeinShape.ORE_DISK
                    && isLooseSurfaceMaterial(def.kind());

            return Optional.of(new GenerationPlan(
                    def, def.kind(), matIdx, state, terrainBlock, disk, isGeode, isCrystalCluster
            ));
        }
    }
}
