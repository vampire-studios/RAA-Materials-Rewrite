package net.vampirestudios.raaMaterials.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;
import net.vampirestudios.raaMaterials.material.SpawnInfo;
import net.vampirestudios.raaMaterials.registry.YBlocks;

public class ParametricOreFeature extends Feature<NoneFeatureConfiguration> {
    private static final int MAT_MAX = 150;

    public ParametricOreFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();
        RandomSource rng = ctx.random();

        var materials = MaterialRegistry.all(level.getLevel());
        if (materials.isEmpty()) return false;

        boolean placedAny = false;
        for (int matIdx = 0; matIdx < materials.size(); matIdx++) {
            if (matIdx > MAT_MAX) break;

            var def = materials.get(matIdx);
            var forms = def.forms();
            Block block;
            if (forms.contains(Form.ORE)) {
                block = YBlocks.PARAM_ORE;
            } else if (forms.contains(Form.BLOCK)) {
                block = YBlocks.PARAM_BLOCK;
            } else {
                continue;
            }

            var spawn = def.spawn();

            for (int a = 0; a < spawn.attemptsPerChunk(); a++) {
                if (rng.nextFloat() >= spawn.successChance()) continue;

                int y = sampleY(rng, spawn.y());
                var center = new BlockPos(
                    origin.getX() + rng.nextInt(16),
                    y,
                    origin.getZ() + rng.nextInt(16)
                );
                placedAny |= switch (spawn.shape()) {
                    case ORE_BLOB, ORE_SHEET    -> placeBlob(level, rng, center, block, matIdx, spawn, def.host());
                    case ORE_STRING             -> placeString(level, rng, center, block, matIdx, spawn, def.host());
                    case ORE_DISK               -> placeDisk(level, rng, center, block, matIdx, spawn, def.host());
                    case CRYSTAL_CLUSTER, GEODE -> placeCluster(level, rng, center, block, matIdx, spawn, def.host());
                };
            }
        }
        return placedAny;
    }

    private int sampleY(RandomSource rng, SpawnInfo.YDistribution dist) {
        int offset = rng.nextInt(dist.spread() * 2 + 1) - dist.spread();
        return Math.clamp(dist.centerY() + offset, dist.minY(), dist.maxY());
    }

    private boolean placeBlob(WorldGenLevel level, RandomSource rng, BlockPos center,
                              Block block, int matIdx, SpawnInfo spawn,
                              MaterialDef.OreHost host) {
        int size = spawn.veinMin() + rng.nextInt(Math.max(1, spawn.veinMax() - spawn.veinMin() + 1));
        var test = host.toRuleTest();
        var state = block.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);
        boolean placed = false;
        for (int i = 0; i < size; i++) {
            var pos = center.offset(rng.nextInt(5) - 2, rng.nextInt(3) - 1, rng.nextInt(5) - 2);
            if (test.test(level.getBlockState(pos), rng)) {
                level.setBlock(pos, state, 2);
                placed = true;
            }
        }
        return placed;
    }

    private boolean placeString(WorldGenLevel level, RandomSource rng, BlockPos start,
                                Block block, int matIdx, SpawnInfo spawn,
                                MaterialDef.OreHost host) {
        int size = spawn.veinMin() + rng.nextInt(Math.max(1, spawn.veinMax() - spawn.veinMin() + 1));
        var test = host.toRuleTest();
        var state = block.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);
        var pos = start.mutable();
        boolean placed = false;
        for (int i = 0; i < size; i++) {
            if (test.test(level.getBlockState(pos), rng)) {
                level.setBlock(pos, state, 2);
                placed = true;
            }
            // random walk one step in a cardinal direction
            int dir = rng.nextInt(6);
            pos.move(
                    dir == 0 ? 1 : (dir == 1 ? -1 : 0),
                    dir == 2 ? 1 : (dir == 3 ? -1 : 0),
                    dir == 4 ? 1 : (dir == 5 ? -1 : 0)
            );
        }
        return placed;
    }

    /** Flat circular disk — veinMin/veinMax are treated as the radius in blocks. */
    private boolean placeDisk(WorldGenLevel level, RandomSource rng, BlockPos center,
                              Block block, int matIdx, SpawnInfo spawn, MaterialDef.OreHost host) {
        int radius = spawn.veinMin() + rng.nextInt(Math.max(1, spawn.veinMax() - spawn.veinMin() + 1));
        var test = host.toRuleTest();
        var state = block.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);
        boolean placed = false;
        int r2 = radius * radius;
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > r2) continue;
                // two-block-deep patch so it looks like a real deposit, not just a surface stain
                for (int dy = -1; dy <= 0; dy++) {
                    var pos = center.offset(dx, dy, dz);
                    if (test.test(level.getBlockState(pos), rng)) {
                        level.setBlock(pos, state, 2);
                        placed = true;
                    }
                }
            }
        }
        return placed;
    }

    private boolean placeCluster(WorldGenLevel level, RandomSource rng, BlockPos center,
                                 Block block, int matIdx, SpawnInfo spawn,
                                 MaterialDef.OreHost host) {
        int size = spawn.veinMin() + rng.nextInt(Math.max(1, spawn.veinMax() - spawn.veinMin() + 1));
        var test = host.toRuleTest();
        var state = block.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);
        boolean placed = false;
        for (int i = 0; i < size; i++) {
            var pos = center.offset(rng.nextInt(7) - 3, rng.nextInt(5) - 2, rng.nextInt(7) - 3);
            if (test.test(level.getBlockState(pos), rng)) {
                level.setBlock(pos, state, 2);
                placed = true;
            }
        }
        return placed;
    }
}
