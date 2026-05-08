package net.vampirestudios.raaMaterials.worldgen;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;
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
            } else if (forms.contains(Form.BLOCK) && generatesTerrainBlock(def.kind())) {
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
                    case ORE_DISK               -> placeDisk(level, rng, center, block, matIdx, spawn, def);
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
                              Block block, int matIdx, SpawnInfo spawn, MaterialDef def) {
        int radius = spawn.veinMin() + rng.nextInt(Math.max(1, spawn.veinMax() - spawn.veinMin() + 1));
        var test = def.host().toRuleTest();
        var state = block.defaultBlockState().setValue(ParametricBlock.MAT, matIdx);
        boolean placed = false;
        int r2 = radius * radius;
        boolean surfacePatch = isSurfacePatch(def.kind());
        boolean undergroundSaltPatch = def.kind() == MaterialKind.SALT && rng.nextBoolean();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                if (dx * dx + dz * dz > r2) continue;

                if (surfacePatch && !undergroundSaltPatch) {
                    int x = center.getX() + dx;
                    int z = center.getZ() + dz;
                    int topY = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z) - 1;
                    if (topY < spawn.y().minY() || topY > spawn.y().maxY()) continue;

                    for (int dy = 0; dy >= -1; dy--) {
                        var pos = new BlockPos(x, topY + dy, z);
                        if (canReplaceSurface(def.kind(), level.getBlockState(pos))) {
                            level.setBlock(pos, state, 2);
                            placed = true;
                        }
                    }
                } else {
                    for (int dy = -1; dy <= 0; dy++) {
                        var pos = center.offset(dx, dy, dz);
                        if (test.test(level.getBlockState(pos), rng)) {
                            level.setBlock(pos, state, 2);
                            placed = true;
                        }
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

    private boolean generatesTerrainBlock(MaterialKind kind) {
        return switch (kind) {
            case STONE, SAND, GRAVEL, CLAY, MUD, SOIL, SALT, VOLCANIC -> true;
            default -> false;
        };
    }

    private boolean isSurfacePatch(MaterialKind kind) {
        return switch (kind) {
            case SAND, GRAVEL, CLAY, MUD, SOIL, SALT -> true;
            default -> false;
        };
    }

    private boolean canReplaceSurface(MaterialKind kind, BlockState state) {
        return switch (kind) {
            case SAND -> state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.SANDSTONE)
                    || state.is(Blocks.RED_SANDSTONE);
            case GRAVEL -> state.is(Blocks.GRAVEL);
            case CLAY -> state.is(Blocks.CLAY);
            case MUD -> state.is(Blocks.MUD);
            case SOIL -> state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.COARSE_DIRT)
                    || state.is(Blocks.PODZOL) || state.is(Blocks.ROOTED_DIRT) || state.is(Blocks.MYCELIUM);
            case SALT -> state.is(Blocks.SAND) || state.is(Blocks.RED_SAND) || state.is(Blocks.SANDSTONE)
                    || state.is(Blocks.RED_SANDSTONE) || state.is(Blocks.GRAVEL) || state.is(Blocks.STONE)
                    || state.is(Blocks.GRANITE) || state.is(Blocks.DIORITE) || state.is(Blocks.ANDESITE)
                    || state.is(Blocks.TUFF) || state.is(Blocks.DEEPSLATE) || state.is(Blocks.CALCITE);
            default -> false;
        };
    }
}
