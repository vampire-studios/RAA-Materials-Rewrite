package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.client.AssetsTheme;

import java.util.Optional;

public final class MaterialAssets {
    private MaterialAssets() {}

    private static final AssetsTheme DEFAULT_THEME = AssetsTheme.defaultTheme();

    public static MaterialAssetsDef generate(MaterialDef material) {
        return DEFAULT_THEME.resolve(material);
    }

    /** Get texture for a specific form */
    public static Optional<Identifier> texture(Form form, MaterialDef material) {
        return DEFAULT_THEME.formTexture(material.kind(), form, material.assetSeed());
    }
}
