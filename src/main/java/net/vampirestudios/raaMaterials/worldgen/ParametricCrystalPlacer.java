package net.vampirestudios.raaMaterials.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.content.ParametricCrystalClusterBlock;
import net.vampirestudios.raaMaterials.material.SpawnInfo;
import net.vampirestudios.raaMaterials.registry.YBlocks;

/**
 * Handles geode sphere and crystal-cluster patch placement for {@link ParametricOreFeature}.
 */
final class ParametricCrystalPlacer {

    private ParametricCrystalPlacer() {}

    /**
     * Places a hollow geode sphere: smooth-basalt shell → calcite ring →
     * crystal-block inner wall → air cavity, then grows crystal clusters on
     * every air block that borders the wall.
     */
    static boolean placeGeode(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos center,
            ParametricOreFeature.GenerationPlan plan,
            SpawnInfo spawn
    ) {
        RandomSource rng = ctx.random();
        WorldGenLevel level = ctx.level();

        int radius = spawn.veinMin() + rng.nextInt(Math.max(1, spawn.veinMax() - spawn.veinMin() + 1));

        // Layer boundaries from centre outward.
        // For small geodes (radius < 6) the shell is thinner so there is still an air cavity.
        double shellInner   = radius - (radius >= 6 ? 2.0 : 1.5);
        double calciteInner = shellInner - 1.0;
        double crystalInner = Math.max(0.0, calciteInner - 1.0);

        BlockState crystalBlock = plan.state(); // PARAM_CRYSTAL_BLOCK with correct MAT index

        // Phase 1: carve the layered sphere.
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
                target = rng.nextFloat() < RAAConfig.active().formChances().geodeCrystalWallChance()
                        ? crystalBlock
                        : Blocks.CALCITE.defaultBlockState();
            } else {
                target = Blocks.AIR.defaultBlockState();
            }

            level.setBlock(bp, target, 2);
        }

        // Phase 2: grow crystal clusters on air blocks that border the crystal/calcite wall.
        BlockState clusterBase = YBlocks.PARAM_CLUSTER.defaultBlockState()
                .setValue(ParametricBlock.MAT, plan.matIdx());

        for (var bp : BlockPos.betweenClosed(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius))) {
            if (!level.isEmptyBlock(bp)) continue;

            for (Direction dir : Direction.values()) {
                BlockState wall = level.getBlockState(bp.relative(dir));
                if (wall.is(YBlocks.PARAM_CRYSTAL_BLOCK) || wall.is(Blocks.CALCITE)) {
                    if (rng.nextFloat() < RAAConfig.active().formChances().geodeCrystalClusterChance()) {
                        // dir points toward the wall; the crystal grows away from it
                        level.setBlock(bp, clusterBase
                                .setValue(ParametricCrystalClusterBlock.FACING, dir.getOpposite()), 2);
                    }
                    break; // at most one cluster per air block
                }
            }
        }

        return true;
    }

    /**
     * Places an ore-sized blob of crystal blocks and then sprouts crystal clusters
     * on every exposed air-facing face.
     */
    static boolean placeCrystalCluster(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos center,
            ParametricOreFeature.GenerationPlan plan,
            SpawnInfo spawn
    ) {
        RandomSource rng = ctx.random();
        WorldGenLevel level = ctx.level();

        var config = new OreConfiguration(
                plan.def().host().toRuleTest(),
                plan.state(),
                ParametricOrePlacer.oreSize(rng, plan, spawn),
                0.0f
        );

        if (!Feature.ORE.place(config, level, ctx.chunkGenerator(), rng, center)) {
            return false;
        }

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
                if (level.isEmptyBlock(neighbor)
                        && rng.nextFloat() < RAAConfig.active().formChances().crystalClusterFillChance()) {
                    level.setBlock(neighbor, clusterBase
                            .setValue(ParametricCrystalClusterBlock.FACING, dir), 2);
                }
            }
        }

        return true;
    }
}
