package net.vampirestudios.raaMaterials;

import net.vampirestudios.raaMaterials.material.Form;
import java.util.EnumSet;
import java.util.List;
import static net.vampirestudios.raaMaterials.material.Form.*;

public final class FormResolver {
    // Define each group's implied forms once.
    public static List<Form> formsFor(FormGroup g) {
        return switch (g) {
            case TOOLS -> List.of(PICKAXE, AXE, SWORD, SHOVEL, HOE);
            case ORE_CHAIN -> List.of(ORE, RAW, RAW_BLOCK);
            case METAL_DECOR -> List.of(PLATE_BLOCK, SHINGLES, PILLAR, TILES, MOSAIC);
            case STONE_DECOR -> List.of(BRICKS, PILLAR, TILES, MOSAIC, MOSSY, CRACKED, COBBLED, CHISELED, POLISHED,
                                        SLAB, STAIRS, WALL, CUT, SMOOTH);
            case SAND_SET -> List.of(SANDSTONE, SLAB, STAIRS, WALL, CHISELED);
            case GRAVEL_SET -> List.of(POLISHED, SLAB, STAIRS, WALL);
            case CLAY_SET -> List.of(BLOCK, BALL, CERAMIC);
            case MUD_SET -> List.of(DRIED, BRICKS, SLAB, STAIRS, WALL);
            case SOIL_SET -> List.of(BLOCK, PACKED_SOIL);
            case SALT_SET -> List.of(BLOCK, DUST);
            case VOLCANIC_SET -> List.of(BLOCK, BRICKS, PILLAR, COBBLED, POLISHED, MOSSY, BUTTON, PRESSURE_PLATE);
            case CRYSTAL_SET -> List.of(CLUSTER, CRYSTAL, SHARD, DUST, CRYSTAL_BRICKS, GLASS, TINTED_GLASS, PANE, ROD_BLOCK);
            case CRYSTAL_LAMPS -> List.of(CALCITE_LAMP, BASALT_LAMP, CHIME);
        };
    }

    /** Compute final allowed forms for a kind given defaults + config KindPolicy. */
    public static EnumSet<Form> computeAllowed(java.util.List<FormGroup> baseGroups, KindPolicy policy) {
        var out = EnumSet.noneOf(Form.class);
        // Base groups
        for (var g : baseGroups) out.addAll(formsFor(g));
        // Apply +/- groups from policy
        for (var g : policy.groupsPlus()) out.addAll(formsFor(g));
        for (var g : policy.groupsMinus()) formsFor(g).forEach(out::remove);
        // Explicit adds/removes
        out.addAll(policy.add());
        policy.remove().forEach(out::remove);
        // Shape clamp
        if (policy.maxShapeForms() >= 0) {
            var shapes = out.stream().filter(f -> f == SLAB || f == STAIRS || f == WALL).toList();
            if (shapes.size() > policy.maxShapeForms()) {
                shapes.stream().skip(policy.maxShapeForms()).forEach(out::remove);
            }
        }
        return out;
    }
}
