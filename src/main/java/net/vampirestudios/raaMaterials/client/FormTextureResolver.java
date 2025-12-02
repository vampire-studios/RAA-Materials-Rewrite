// src/main/java/net/vampirestudios/raaMaterials/material/FormTextureResolver.java
package net.vampirestudios.raaMaterials.client;

import net.minecraft.resources.ResourceLocation;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;

import java.util.Optional;
import java.util.Random;

import static net.minecraft.resources.ResourceLocation.withDefaultNamespace;

public interface FormTextureResolver {
    /** Returns a sheet for a given Form, with fallback to storage/base. */
    Optional<ResourceLocation> sheet(AssetsTheme.Slot form, MaterialDef mat);

    /** Optional convenience: pillar parts as separate lookups. */
    default Optional<ResourceLocation> pillarSide(MaterialDef mat) { return sheet(AssetsTheme.Slot.PILLAR_SIDE, mat); }
    default Optional<ResourceLocation> pillarTop (MaterialDef mat) { return sheet(AssetsTheme.Slot.PILLAR_TOP,  mat); }

    static FormTextureResolver of(AssetsTheme theme) { return new Default(theme); }

    final class Default implements FormTextureResolver {
        private final AssetsTheme theme;
        public Default(AssetsTheme theme) { this.theme = theme; }

        @Override
        public Optional<ResourceLocation> sheet(AssetsTheme.Slot slot, MaterialDef mat) {
            var kind = mat.kind();
            var rnd  = new Random(mat.assetSeed());

            // 1) Try to resolve the slot directly through the theme
            return theme.resolveSlot(kind, slot, rnd);
        }

        private AssetsTheme.Slot mapFormToSlot(Form f) {
            return switch (f) {
                case BRICKS -> AssetsTheme.Slot.BRICKS;
                case POLISHED -> AssetsTheme.Slot.POLISHED;
                case TILES -> AssetsTheme.Slot.TILES;
                case MOSAIC -> AssetsTheme.Slot.MOSAIC;
                case PILLAR -> AssetsTheme.Slot.PILLAR_SIDE;
                case PLATE_BLOCK -> AssetsTheme.Slot.PLATE_SHEET;
                case SHINGLES -> AssetsTheme.Slot.SHINGLES_SHEET;
                default -> null; // others use direct-per-form or fall back to storage
            };
        }

        private Optional<ResourceLocation> themeResolveSlot(MaterialKind k, AssetsTheme.Slot s, Random rnd) {
            return theme.resolveSlot(k, s, rnd);
        }

        private ResourceLocation baseFallback(MaterialKind k) {
            return switch (k) {
                case SAND     -> withDefaultNamespace("sandstone.png");
                case CLAY     -> withDefaultNamespace("clay.png");
                case MUD      -> withDefaultNamespace("packed_mud.png");
                case GRAVEL   -> withDefaultNamespace("gravel.png");
				case SOIL     -> withDefaultNamespace("dirt.png");
                case SALT     -> withDefaultNamespace("calcite.png");
                case VOLCANIC -> withDefaultNamespace("basalt_top.png");
                case WOOD     -> withDefaultNamespace("oak_planks.png");
                case CRYSTAL  -> withDefaultNamespace("amethyst_block.png");
                case GEM      -> withDefaultNamespace("diamond_block.png");
                case METAL    -> withDefaultNamespace("iron_block.png");
                case ALLOY    -> withDefaultNamespace("copper_block.png");
                default       -> withDefaultNamespace("stone.png");
            };
        }
    }
}
