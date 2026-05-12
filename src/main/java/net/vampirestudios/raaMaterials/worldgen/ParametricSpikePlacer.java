package net.vampirestudios.raaMaterials.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.registry.YBlocks;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Handles speleothem-style spike cluster placement for {@link ParametricOreFeature}.
 * <p>
 * Each cluster probes the cave column at several XZ offsets around the origin,
 * then delegates to {@link SpeleothemUtils#growSpeleothem} so that stalactites
 * and stalagmites are built with proper BASE→MIDDLE→FRUSTUM→TIP thickness
 * sequences while preserving the {@code MAT} property on every placed block.
 */
final class ParametricSpikePlacer {

    private ParametricSpikePlacer() {}

    /**
     * Attempts to place a small speleothem cluster centred near {@code origin}.
     *
     * @param ctx      feature placement context (provides level, rng, etc.)
     * @param origin   starting position (must be in open cave air or water)
     * @param matIdx   material index written to every placed spike/block
     * @param stoneTag replaceable-block set used by {@link SpeleothemUtils} to
     *                 check that the attachment surface is stone-like
     */
    static boolean placeCluster(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos origin,
            int matIdx,
            HolderSet<Block> stoneTag
    ) {
        WorldGenLevel level = ctx.level();
        RandomSource rng = ctx.random();

        if (!SpeleothemUtils.isEmptyOrWater(level, origin)) return false;

        // Base block with MAT set — used as the "anchor" in isBase checks
        BlockState blockState = YBlocks.PARAM_BLOCK.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);
        // Pointed block with MAT set — TIP_DIRECTION and THICKNESS are overwritten per segment
        BlockState spikeState = YBlocks.PARAM_SPIKE.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);

        int maxHeight = RAAConfig.active().formChances().spikeMaxHeight();
        float density = 0.35f + rng.nextFloat() * 0.45f; // 0.35–0.80
        int radius    = 1 + rng.nextInt(6);              // cluster radius 1–3 blocks

        boolean placedAny = false;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                placedAny |= placeColumn(
                        level, rng, origin.offset(dx, 0, dz),
                        dx, dz, radius, maxHeight, density,
                        blockState, spikeState, stoneTag
                );
            }
        }
        return placedAny;
    }

    /**
     * Scans for a floor/ceiling pair at the given XZ position and grows stalactite
     * and/or stalagmite columns via {@link SpeleothemUtils}.
     */
    private static boolean placeColumn(
            WorldGenLevel level,
            RandomSource rng,
            BlockPos pos,
            int dx, int dz,
            int radius,
            int maxHeight,
            float density,
            BlockState blockState,
            BlockState spikeState,
            HolderSet<Block> stoneTag
    ) {
        Optional<Column> columnOpt = Column.scan(
                level, pos, 12,
                SpeleothemUtils::isEmptyOrWater,
                SpeleothemUtils::isNeitherEmptyNorWater
        );
        if (columnOpt.isEmpty()) return false;

        Column column       = columnOpt.get();
        OptionalInt ceiling = column.getCeiling();
        OptionalInt floor   = column.getFloor();
        if (ceiling.isEmpty() && floor.isEmpty()) return false;

        // Chance of anything here falls off toward cluster edges
        double chance = clusterChance(radius, dx, dz);

        int stalactiteH = 0;
        int stalagmiteH = 0;

        if (ceiling.isPresent() && rng.nextDouble() < chance) {
            int maxH = floor.isPresent()
                    ? Math.min(maxHeight, ceiling.getAsInt() - floor.getAsInt() - 1)
                    : maxHeight;
            stalactiteH = biasedHeight(rng, dx, dz, density, maxH);
        }

        if (floor.isPresent() && rng.nextDouble() < chance) {
            if (ceiling.isPresent() && stalactiteH > 0) {
                // Mirror height with small random variation
                stalagmiteH = Math.max(0, stalactiteH + Mth.randomBetweenInclusive(rng, -1, 1));
            } else {
                int maxH = ceiling.isPresent()
                        ? Math.min(maxHeight, ceiling.getAsInt() - floor.getAsInt() - 1)
                        : maxHeight;
                stalagmiteH = biasedHeight(rng, dx, dz, density, maxH);
            }
        }

        // Clamp so the two columns never overlap
        if (ceiling.isPresent() && floor.isPresent()) {
            int gap = ceiling.getAsInt() - floor.getAsInt();
            if (stalactiteH + stalagmiteH >= gap) {
                int half = Math.max(0, (gap - 1) / 2);
                stalactiteH = half;
                stalagmiteH = gap - 1 - half;
            }
        }

        boolean mergeTips = stalactiteH > 0 && stalagmiteH > 0
                && column.getHeight().isPresent()
                && stalactiteH + stalagmiteH == column.getHeight().getAsInt()
                && rng.nextBoolean();

        boolean placed = false;
        if (ceiling.isPresent() && stalactiteH > 0) {
            SpeleothemUtils.growSpeleothem(
                    level, pos.atY(ceiling.getAsInt() - 1),
                    Direction.DOWN, stalactiteH, mergeTips,
                    blockState, spikeState, stoneTag
            );
            placed = true;
        }
        if (floor.isPresent() && stalagmiteH > 0) {
            SpeleothemUtils.growSpeleothem(
                    level, pos.atY(floor.getAsInt() + 1),
                    Direction.UP, stalagmiteH, mergeTips,
                    blockState, spikeState, stoneTag
            );
            placed = true;
        }
        return placed;
    }

    /** Placement probability at this offset — 1.0 at centre, 0.3 at the edge. */
    private static double clusterChance(int radius, int dx, int dz) {
        int xEdgeDist = radius - Math.abs(dx);
        int zEdgeDist = radius - Math.abs(dz);
        int edgeDist  = Math.min(xEdgeDist, zEdgeDist);
        return Mth.clampedMap(edgeDist, 0.0f, Math.max(1.0f, radius / 2.0f), 0.3f, 1.0f);
    }

    /** Height from a biased distribution — taller toward centre, shorter at edges. */
    private static int biasedHeight(RandomSource rng, int dx, int dz, float density, int maxHeight) {
        if (maxHeight <= 0 || rng.nextFloat() > density) return 0;
        int distFromCenter = Math.abs(dx) + Math.abs(dz);
        float mean = (float) Mth.clampedMap(distFromCenter, 0.0, 4.0, maxHeight / 2.0, 0.0);
        return Math.max(1, (int) ClampedNormalFloat.sample(rng, mean, Math.max(1, maxHeight / 3.0f), 0, maxHeight));
    }
}
