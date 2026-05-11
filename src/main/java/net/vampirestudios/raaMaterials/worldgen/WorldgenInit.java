package net.vampirestudios.raaMaterials.worldgen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.worldgen.config.SpeleothemClusterConfiguration;

public final class WorldgenInit {
    public static final ResourceKey<PlacedFeature> PARAMETRIC_ORE_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, RAAMaterials.id("parametric_ore"));
    public static final ResourceKey<PlacedFeature> SPELEOTHEM_CLUSTER_PLACED =
            ResourceKey.create(Registries.PLACED_FEATURE, RAAMaterials.id("speleothem_cluster"));

    public static Feature<NoneFeatureConfiguration> PARAMETRIC_ORE_FEATURE;
    public static Feature<SpeleothemClusterConfiguration> SPELEOTHEM_CLUSTER;

    public static void init() {
        PARAMETRIC_ORE_FEATURE = Registry.register(
                BuiltInRegistries.FEATURE,
                RAAMaterials.id("parametric_ore"),
                new ParametricOreFeature(NoneFeatureConfiguration.CODEC)
        );

        SPELEOTHEM_CLUSTER = Registry.register(
                BuiltInRegistries.FEATURE,
                RAAMaterials.id("speleothem_cluster"),
                new SpeleothemClusterFeature(SpeleothemClusterConfiguration.CODEC)
        );

        BiomeModifications.addFeature(
                BiomeSelectors.tag(BiomeTags.IS_OVERWORLD),
                GenerationStep.Decoration.UNDERGROUND_ORES,
                PARAMETRIC_ORE_PLACED
        );
    }
}
