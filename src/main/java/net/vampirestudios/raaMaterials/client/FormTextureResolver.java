// src/main/java/net/vampirestudios/raaMaterials/material/FormTextureResolver.java
package net.vampirestudios.raaMaterials.client;

import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;

import java.util.Optional;
import java.util.Random;

import static net.minecraft.resources.Identifier.withDefaultNamespace;

public interface FormTextureResolver {
    /** Returns a sheet for a given Form, with fallback to storage/base. */
    Optional<Identifier> sheet(AssetsTheme.Slot form, MaterialDef mat);

    /** Optional convenience: pillar parts as separate lookups. */
    default Optional<Identifier> pillarSide(MaterialDef mat) { return sheet(AssetsTheme.Slot.PILLAR_SIDE, mat); }
    default Optional<Identifier> pillarTop (MaterialDef mat) { return sheet(AssetsTheme.Slot.PILLAR_TOP,  mat); }

    static FormTextureResolver of(AssetsTheme theme) { return new Default(theme); }

    final class Default implements FormTextureResolver {
        private final AssetsTheme theme;
        public Default(AssetsTheme theme) { this.theme = theme; }

        @Override
        public Optional<Identifier> sheet(AssetsTheme.Slot slot, MaterialDef mat) {
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

        private Optional<Identifier> themeResolveSlot(MaterialKind k, AssetsTheme.Slot s, Random rnd) {
            return theme.resolveSlot(k, s, rnd);
        }

        private Identifier baseFallback(MaterialKind k) {
            return switch (k) {
                case SAND     -> withDefaultNamespace("block/sandstone");
                case CLAY     -> withDefaultNamespace("block/clay");
                case MUD      -> withDefaultNamespace("block/packed_mud");
                case GRAVEL   -> withDefaultNamespace("block/gravel");
				case SOIL     -> withDefaultNamespace("block/dirt");
                case SALT     -> withDefaultNamespace("block/calcite");
                case VOLCANIC -> withDefaultNamespace("block/basalt_top");
                case WOOD     -> withDefaultNamespace("block/oak_planks");
                case CRYSTAL  -> withDefaultNamespace("block/amethyst_block");
                case GEM      -> withDefaultNamespace("block/diamond_block");
                case METAL    -> withDefaultNamespace("block/iron_block");
                case ALLOY    -> withDefaultNamespace("block/copper_block");
                default       -> withDefaultNamespace("block/stone");
            };
        }
    }
}
