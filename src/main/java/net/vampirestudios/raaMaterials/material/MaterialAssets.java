package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.client.AssetsTheme;

import java.util.Optional;

public final class MaterialAssets {
    private MaterialAssets() {}

    public static MaterialAssetsDef generate(MaterialDef material) {
        AssetsTheme generator = AssetsTheme.defaultTheme();
        return generator.resolve(material);
    }

    /** Get texture for a specific form */
    public static Optional<Identifier> texture(Form form, MaterialDef material) {
        AssetsTheme generator = AssetsTheme.defaultTheme();
        return generator.formTexture(material.kind(), form, material.assetSeed());
    }
}
