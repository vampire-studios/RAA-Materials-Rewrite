package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.ResourceLocation;
import net.vampirestudios.raaMaterials.client.AssetsTheme;

import java.util.Optional;

/** Simple manager for generated material assets. */
public final class MaterialAssets {
    private MaterialAssets() {}

    /** Generate assets for a material */
    public static MaterialAssetsDef generate(MaterialDef material) {
        AssetsTheme generator = AssetsTheme.defaultTheme(); // or create world-specific one
        return generator.resolve(material);
    }

    /** Get texture for a specific form */
    public static Optional<ResourceLocation> texture(Form form, MaterialDef material) {
        AssetsTheme generator = AssetsTheme.defaultTheme();
        return generator.formTexture(material.kind(), form, material.assetSeed());
    }
}
