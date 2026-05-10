package net.vampirestudios.raaMaterials.material;

import java.util.List;

public final class FormGroups {
    public static final List<Form> TOOLS = List.of(Form.PICKAXE, Form.AXE, Form.SWORD, Form.SHOVEL, Form.HOE, Form.SPEAR);

    public static final List<Form> ORE_CHAIN = List.of(Form.ORE, Form.RAW, Form.DUST, Form.NUGGET, Form.INGOT);
    public static final List<Form> METAL_DECOR = List.of(Form.BLOCK, Form.RAW_BLOCK, Form.PLATE_BLOCK, Form.SHINGLES, Form.SHEET, Form.GEAR);

    public static final List<Form> CRYSTAL_SET = List.of(
        Form.CLUSTER, Form.BLOCK, Form.CRYSTAL, Form.SHARD, Form.DUST,
        Form.BUDDING, Form.BUD_SMALL, Form.BUD_MEDIUM, Form.BUD_LARGE, Form.CALCITE_LAMP, Form.BASALT_LAMP, Form.CHIME
    );

    public static final List<Form> STONE_DECOR = List.of(Form.BLOCK, Form.BRICKS, Form.STAIRS, Form.SLAB, Form.WALL, Form.CHISELED, Form.POLISHED);
    public static final List<Form> SAND_SET    = List.of(Form.BLOCK, Form.SANDSTONE, Form.CUT, Form.CHISELED, Form.SMOOTH, Form.SLAB, Form.STAIRS, Form.WALL);
    public static final List<Form> GRAVEL_SET  = List.of(Form.BLOCK, Form.POLISHED, Form.SLAB, Form.STAIRS, Form.WALL);
    public static final List<Form> CLAY_SET    = List.of(Form.BLOCK, Form.BALL, Form.CERAMIC);
    public static final List<Form> MUD_SET     = List.of(Form.BLOCK, Form.DRIED, Form.BRICKS, Form.SLAB, Form.STAIRS, Form.WALL);
    public static final List<Form> SOIL_SET    = List.of(Form.BLOCK, Form.PACKED_SOIL);
    public static final List<Form> SALT_SET    = List.of(Form.BLOCK, Form.DUST);
    public static final List<Form> VOLCANIC_SET= List.of(Form.BLOCK);

    // used to clamp “shape” noise
    public static final List<Form> SHAPES = List.of(Form.SLAB, Form.STAIRS, Form.WALL);
}
