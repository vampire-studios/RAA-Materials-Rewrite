package net.vampirestudios.raaMaterials.client;

import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.material.MaterialAssets;
import net.vampirestudios.raaMaterials.material.MaterialAssetsDef;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;

import java.util.Map;

public final class MaterialTexturePickers {
    private MaterialTexturePickers() {
    }

    public static final Map<MaterialKind, TexPicker> BLOCK_TEXTURES = Map.of(
            MaterialKind.METAL, idx -> RAAMaterials.id("storage_blocks/metals/metal_" + oneIndexed(idx, 23)),
            MaterialKind.CRYSTAL, idx -> RAAMaterials.id("crystal/crystal_block_" + oneIndexed(idx, 5)),
            MaterialKind.GEM, idx -> RAAMaterials.id("storage_blocks/gems/gem_" + oneIndexed(idx, 16)),
            MaterialKind.STONE, idx -> RAAMaterials.id("stone/a/stone_" + oneIndexed(idx, 23)),
            MaterialKind.SAND, idx -> RAAMaterials.id("storage_blocks/sand_" + oneIndexed(idx, 3)),
            MaterialKind.MUD, idx -> Identifier.withDefaultNamespace("mud"),
            MaterialKind.GRAVEL, idx -> Identifier.withDefaultNamespace("gravel"),
            MaterialKind.CLAY, idx -> Identifier.withDefaultNamespace("clay")
    );

    public static Identifier pickBlockTexture(MaterialDef def, int idx) {
        return switch (def.kind()) {
            case METAL, ALLOY, OTHER -> BLOCK_TEXTURES.get(MaterialKind.METAL).pick(idx);
            case GEM -> BLOCK_TEXTURES.get(MaterialKind.GEM).pick(idx);
            case CRYSTAL -> BLOCK_TEXTURES.get(MaterialKind.CRYSTAL).pick(idx);
            case STONE, CLAY, GRAVEL, SOIL, MUD, SALT, VOLCANIC -> BLOCK_TEXTURES.get(MaterialKind.STONE).pick(idx);
            case SAND -> BLOCK_TEXTURES.get(MaterialKind.SAND).pick(idx);
            case WOOD -> Identifier.withDefaultNamespace("block/oak_planks");
        };
    }

    public static MaterialAssetsDef textures(MaterialDef def) {
        return MaterialAssets.generate(def);
    }

    public static int oneIndexed(int idx, int max) {
        return Math.floorMod(idx, max) + 1;
    }

    @FunctionalInterface
    public interface TexPicker {
        Identifier pick(int idx);
    }
}