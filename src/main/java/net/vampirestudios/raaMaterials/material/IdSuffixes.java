package net.vampirestudios.raaMaterials.material;

import java.util.List;
import java.util.Locale;

import static net.vampirestudios.raaMaterials.material.Form.*;

public final class IdSuffixes {
    private IdSuffixes() {}

    /** Which variant SLAB/STAIRS/WALL attach to. */
    public static Form shapeBaseFor(List<Form> forms) {
        if (forms.contains(SANDSTONE)) return SANDSTONE;
        if (forms.contains(BRICKS))    return BRICKS;
        if (forms.contains(POLISHED))  return POLISHED;
        return BLOCK;
    }

    /** Registry ID suffix for any form, considering base when needed. */
    public static String suffixFor(Form form, MaterialKind kind, List<Form> forms) {
        if (form == SLAB || form == STAIRS || form == WALL) {
            return shapeSuffix(form, shapeBaseFor(forms));
        }
        return switch (form) {
            case BLOCK -> "block";
            case ORE -> "ore";
            case RAW_BLOCK -> "raw_block";
            case RAW -> "raw";
            case DUST -> "dust";
            case NUGGET -> "nugget";
            case INGOT -> "ingot";
            case SHEET -> "sheet";
            case GEAR -> "gear";
            case PLATE_BLOCK -> "plate";
            case SHINGLES -> "shingles";

            case GEM -> "gem";
            case SHARD -> "shard";
            case CRYSTAL -> "crystal";
            case CLUSTER -> "cluster";
            case BUDDING -> "budding";
            case BUD_SMALL -> "bud_small";
            case BUD_MEDIUM -> "bud_medium";
            case BUD_LARGE -> "bud_large";
            case CALCITE_LAMP -> "calcite_lamp";
            case BASALT_LAMP -> "basalt_lamp";
            case CHIME -> "chime";

            case SANDSTONE -> "sandstone";
            case CUT -> (kind == MaterialKind.SAND) ? "cut_sandstone" : "cut";
            case SMOOTH -> (kind == MaterialKind.SAND) ? "smooth_sandstone" : "smooth";
            case CHISELED -> (kind == MaterialKind.SAND) ? "chiseled_sandstone" : "chiseled";
            case POLISHED -> "polished";
            case BRICKS -> "bricks";
            case CRYSTAL_BRICKS -> "crystal_bricks";
            case CERAMIC -> "ceramic";
            case DRIED -> "dried";
            case PACKED_SOIL -> "packed_soil";

            default -> form.name().toLowerCase();
        };
    }

    /** Shape id suffix given a base variant. */
    public static String shapeSuffix(Form shape, Form base) {
        // Only valid for these 3 shapes
        if (shape != Form.SLAB && shape != Form.STAIRS && shape != Form.WALL) {
            // If someone calls this with a non-shape, just return the base token
            return switch (base) {
                case SANDSTONE -> "sandstone";
                case CUT       -> "cut";
                case SMOOTH    -> "smooth";
                case CHISELED  -> "chiseled";
                case BRICKS    -> "bricks";   // IMPORTANT: return plural here when used alone
                case POLISHED  -> "polished";
                case BLOCK     -> "";
                default        -> base.name().toLowerCase(Locale.ROOT);
            };
        }
        String baseToken = switch (base) {
            // sandstone line wants the full token
            case SANDSTONE -> "sandstone";
            case CUT       -> "cut_sandstone";
            case SMOOTH    -> "smooth_sandstone";

            // generic block variants that can act as bases
            case CHISELED  -> "chiseled";
            case BRICKS    -> "brick";   // singular only when combined with a SHAPE
            case POLISHED  -> "polished";

            // plain base
            case BLOCK     -> "";
            default        -> base.name().toLowerCase(Locale.ROOT);
        };
        String shapeToken = switch (shape) {
            // classic shapes
            case SLAB -> "slab";
            case STAIRS -> "stairs";
            case WALL -> "wall";
            default -> shape.name().toLowerCase(Locale.ROOT);
        };
        return baseToken.isEmpty() ? shapeToken : String.format("%s_", baseToken) + shapeToken;
    }
}
