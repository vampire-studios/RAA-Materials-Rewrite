package net.vampirestudios.raaMaterials.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.material.SpawnInfo;

/**
 * Handles ore blob, disk, and terrain-patch placement for {@link ParametricOreFeature}.
 */
final class ParametricOrePlacer {

    private ParametricOrePlacer() {}

    static boolean placeOre(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos pos,
            ParametricOreFeature.GenerationPlan plan,
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

    static boolean placeDisk(
            FeaturePlaceContext<NoneFeatureConfiguration> ctx,
            BlockPos pos,
            ParametricOreFeature.GenerationPlan plan,
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

    // -------------------------------------------------------------------------
    // Sizing helpers
    // -------------------------------------------------------------------------

    static int oreSize(RandomSource rng, ParametricOreFeature.GenerationPlan plan, SpawnInfo spawn) {
        int min = spawn.veinMin();
        int max = spawn.veinMax();

        if (plan.terrainBlock()) {
            min = terrainPatchMin(plan.kind(), spawn);
            max = terrainPatchMax(plan.kind(), spawn);
        }

        return min + rng.nextInt(Math.max(1, max - min + 1));
    }

    private static int terrainPatchMin(MaterialKind kind, SpawnInfo spawn) {
        return switch (kind) {
            case STONE    -> Math.max(spawn.veinMin() * 4, 32);
            case VOLCANIC -> Math.max(spawn.veinMin() * 4, 28);
            case SALT     -> Math.max(spawn.veinMin() * 3, 24);
            default       -> Math.max(spawn.veinMin() * 2, 12);
        };
    }

    private static int terrainPatchMax(MaterialKind kind, SpawnInfo spawn) {
        return switch (kind) {
            case STONE    -> Math.clamp(spawn.veinMax() * 5L, 96, 160);
            case VOLCANIC -> Math.clamp(spawn.veinMax() * 4L, 72, 128);
            case SALT     -> Math.clamp(spawn.veinMax() * 4L, 56, 96);
            default       -> Math.clamp(spawn.veinMax() * 2L, 32, 48);
        };
    }

    private static int diskRadiusMin(MaterialKind kind, SpawnInfo spawn) {
        return switch (kind) {
            case SAND, GRAVEL, CLAY, MUD, SOIL -> Math.min(spawn.veinMin(), 2);
            default -> spawn.veinMin();
        };
    }

    private static int diskRadiusMax(MaterialKind kind, SpawnInfo spawn) {
        return switch (kind) {
            case SAND, GRAVEL -> Math.min(spawn.veinMax(), 4);
            case CLAY, MUD, SOIL -> Math.min(spawn.veinMax(), 3);
            default -> spawn.veinMax();
        };
    }

    // -------------------------------------------------------------------------
    // Block target helpers
    // -------------------------------------------------------------------------

    private static BlockPredicate diskTarget(MaterialDef def) {
        return switch (def.kind()) {
            case SAND -> BlockPredicate.matchesBlocks(Blocks.SAND, Blocks.RED_SAND);
            case GRAVEL -> BlockPredicate.matchesBlocks(Blocks.GRAVEL);
            case CLAY -> BlockPredicate.matchesBlocks(Blocks.CLAY);
            case MUD -> BlockPredicate.matchesBlocks(Blocks.MUD);
            case SOIL -> BlockPredicate.matchesBlocks(
                    Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT,
                    Blocks.PODZOL, Blocks.ROOTED_DIRT, Blocks.MYCELIUM
            );
            case SALT -> BlockPredicate.anyOf(
                    targetPredicate(def.host().target()),
                    BlockPredicate.matchesBlocks(
                            Blocks.SAND, Blocks.RED_SAND,
                            Blocks.SANDSTONE, Blocks.RED_SANDSTONE,
                            Blocks.GRAVEL
                    )
            );
            default -> targetPredicate(def.host().target());
        };
    }

    private static BlockPredicate targetPredicate(MaterialDef.OreHost.Target target) {
        if (target.isTag()) {
            return BlockPredicate.matchesTag(TagKey.create(Registries.BLOCK, target.id()));
        }
        return BlockPredicate.matchesBlocks(BuiltInRegistries.BLOCK.getValue(target.id()));
    }
}
