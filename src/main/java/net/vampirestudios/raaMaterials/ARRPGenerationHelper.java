package net.vampirestudios.raaMaterials;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.json.loot.JFunction;
import net.vampirestudios.arrp.json.models.JModel;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.recipe.ParametricCraftingRecipe;
import net.vampirestudios.raaMaterials.recipe.ParametricStonecuttingRecipe;

public class ARRPGenerationHelper {
    private static final String[] PARAMETRIC_BLOCK_DROPS = {
            "material_block",
            "material_raw_block",
            "material_plate_block",
            "material_ceramic",
            "material_shingles",
            "material_sandstone",
            "material_cut",
            "material_smooth",
            "material_chiseled",
            "material_bricks",
            "material_polished",
            "material_dried",
            "material_stairs",
            "material_wall",
            "material_sandstone_stairs",
            "material_sandstone_wall",
            "material_brick_stairs",
            "material_brick_wall",
            "material_polished_stairs",
            "material_polished_wall",
            "material_crystal_block",
            "material_crystal_bricks",
            "material_crystal_cluster",
            "material_glass",
            "material_tinted_glass",
            "material_basalt_lamp",
            "material_calcite_lamp",
            "material_pillar",
            "material_tiles",
            "material_mosaic",
            "material_mossy",
            "material_cracked",
            "material_cobbled",
            "material_bars",
            "material_grate",
            "material_button_metal",
            "material_button_stone",
            "material_button_wood",
            "material_pressure_plate_metal",
            "material_pressure_plate_stone",
            "material_pressure_plate_wood",
            "material_trapdoor_metal",
            "material_trapdoor_wood",
            "material_fence",
            "material_fence_gate",
            "material_chain",
            "material_lantern",
            "material_lamp",
            "material_pane",
            "material_rod_block",
            "material_spike",
            "material_log",
            "material_wood",
            "material_stripped_log",
            "material_stripped_wood",
            "material_planks",
            "material_beam"
    };

    private static final String[] PARAMETRIC_SLAB_DROPS = {
            "material_slab",
            "material_sandstone_slab",
            "material_brick_slab",
            "material_polished_slab"
    };

    private static String blockTexture(Identifier id) {
        String path = id.getPath();
        if (path.endsWith(".png")) path = path.substring(0, path.length() - 4);
        if (!path.startsWith("block/")) path = "block/" + path;
        return Identifier.fromNamespaceAndPath(id.getNamespace(), path).toString();
    }

    public static void generateParametricBlockLootTables(RuntimeResourcePack pack) {
        addOreLootTable(pack);
        addDoorLootTable(pack, "material_door_metal");
        addDoorLootTable(pack, "material_door_wood");
        for (String blockId : PARAMETRIC_BLOCK_DROPS) {
            addSelfDropLootTable(pack, blockId, false);
        }
        for (String blockId : PARAMETRIC_SLAB_DROPS) {
            addSelfDropLootTable(pack, blockId, true);
        }
    }

    public static void generateParametricRecipes(RuntimeResourcePack pack) {
        for (ParametricCraftingRecipe.Kind kind : ParametricCraftingRecipe.Kind.values()) {
            addSpecialRecipe(pack, kind.id());
        }

        for (ParametricStonecuttingRecipe.Kind kind : ParametricStonecuttingRecipe.Kind.values()) {
            addSpecialRecipe(pack, "stonecutting_" + kind.id());
        }

        addSpecialRecipe(pack, "smelt_ore_to_ingot");
        addSpecialRecipe(pack, "blast_ore_to_ingot");
        addSpecialRecipe(pack, "smelt_raw_to_ingot");
        addSpecialRecipe(pack, "blast_raw_to_ingot");
        addSpecialRecipe(pack, "smelt_sand_to_glass");
    }



    private static void addSpecialRecipe(RuntimeResourcePack pack, String id) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", RAAMaterials.id(id).toString());
        pack.addData(RAAMaterials.id("recipe/" + id + ".json"), recipe.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private static void addSelfDropLootTable(RuntimeResourcePack pack, String blockId, boolean slab) {
        var id = RAAMaterials.id(blockId);
        var entry = net.vampirestudios.arrp.json.loot.JLootTable.entry()
                .type("minecraft:item")
                .name(id.toString())
                .function(copyMatStateFunction(id));

        if (slab) {
            entry.function(doubleSlabCountFunction(id));
            entry.function(net.vampirestudios.arrp.json.loot.JLootTable.function("minecraft:explosion_decay"));
        }

        var pool = net.vampirestudios.arrp.json.loot.JLootTable.pool()
                .rolls(1)
                .bonus(0)
                .entry(entry);

        if (!slab) {
            pool.condition(net.vampirestudios.arrp.json.loot.JLootTable.predicate("minecraft:survives_explosion"));
        }

        pack.addLootTable(RAAMaterials.id("blocks/" + blockId),
                net.vampirestudios.arrp.json.loot.JLootTable.loot("minecraft:block")
                        .pool(pool)
                        .randomSequence(RAAMaterials.id("blocks/" + blockId)));
    }

    private static void addDoorLootTable(RuntimeResourcePack pack, String blockId) {
        var id = RAAMaterials.id(blockId);
        JsonObject properties = new JsonObject();
        properties.addProperty("half", "lower");

        var lowerHalf = net.vampirestudios.arrp.json.loot.JLootTable.predicate("minecraft:block_state_property")
                .parameter("block", id)
                .parameter("properties", properties);

        var entry = net.vampirestudios.arrp.json.loot.JLootTable.entry()
                .type("minecraft:item")
                .name(id.toString())
                .condition(lowerHalf)
                .function(copyMatStateFunction(id));

        var pool = net.vampirestudios.arrp.json.loot.JLootTable.pool()
                .rolls(1)
                .bonus(0)
                .condition(net.vampirestudios.arrp.json.loot.JLootTable.predicate("minecraft:survives_explosion"))
                .entry(entry);

        pack.addLootTable(RAAMaterials.id("blocks/" + blockId),
                net.vampirestudios.arrp.json.loot.JLootTable.loot("minecraft:block")
                        .pool(pool)
                        .randomSequence(RAAMaterials.id("blocks/" + blockId)));
    }

    private static void addOreLootTable(RuntimeResourcePack pack) {
        var ore = RAAMaterials.id("material_ore");
        var raw = RAAMaterials.id("material_raw");

        var silkTouch = net.vampirestudios.arrp.json.loot.JLootTable.entry()
                .type("minecraft:item")
                .name(ore.toString())
                .condition(silkTouchCondition())
                .function(copyMatStateFunction(ore));

        var rawDrop = net.vampirestudios.arrp.json.loot.JLootTable.entry()
                .type("minecraft:item")
                .name(raw.toString())
                .condition(net.vampirestudios.arrp.json.loot.JLootTable.predicate("minecraft:survives_explosion"));

        var alternatives = net.vampirestudios.arrp.json.loot.JLootTable.entry()
                .type("minecraft:alternatives")
                .child(silkTouch)
                .child(rawDrop);

        var pool = net.vampirestudios.arrp.json.loot.JLootTable.pool()
                .rolls(1)
                .bonus(0)
                .entry(alternatives);

        pack.addLootTable(RAAMaterials.id("blocks/material_ore"),
                net.vampirestudios.arrp.json.loot.JLootTable.loot("minecraft:block")
                        .pool(pool)
                        .randomSequence(RAAMaterials.id("blocks/material_ore")));
    }

    private static net.vampirestudios.arrp.json.loot.JCondition silkTouchCondition() {
        JsonObject levels = new JsonObject();
        levels.addProperty("min", 1);

        JsonObject enchantment = new JsonObject();
        enchantment.addProperty("enchantments", "minecraft:silk_touch");
        enchantment.add("levels", levels);

        JsonArray enchantments = new JsonArray();
        enchantments.add(enchantment);

        JsonObject predicates = new JsonObject();
        predicates.add("minecraft:enchantments", enchantments);

        JsonObject predicate = new JsonObject();
        predicate.add("predicates", predicates);

        return net.vampirestudios.arrp.json.loot.JLootTable.predicate("minecraft:match_tool")
                .parameter("predicate", predicate);
    }

    private static JFunction copyMatStateFunction(Identifier blockId) {
        JsonArray properties = new JsonArray();
        properties.add(ParametricBlock.MAT.getName());

        return net.vampirestudios.arrp.json.loot.JLootTable.function("minecraft:copy_state")
                .parameter("block", blockId)
                .parameter("properties", properties);
    }

    private static JFunction doubleSlabCountFunction(Identifier blockId) {
        JsonObject properties = new JsonObject();
        properties.addProperty("type", "double");

        var condition = net.vampirestudios.arrp.json.loot.JLootTable.predicate("minecraft:block_state_property")
                .parameter("block", blockId)
                .parameter("properties", properties);

        return net.vampirestudios.arrp.json.loot.JLootTable.function("minecraft:set_count")
                .parameter("count", 2.0)
                .parameter("add", false)
                .condition(condition);
    }

    public static void generateAllTintedBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier texture) {
        var model = JModel.model("minecraft:block/cube_all")
                .textures(JModel.textures().var("all", blockTexture(texture)))
                .element(JModel.element()
                        .bounds(0, 0, 0, 16, 16, 16)
                        .faces(JModel.faces()
                                .north(JModel.face("all").uv(0, 0, 16, 16).cullface(Direction.NORTH).tintIndex(0))
                                .east(JModel.face("all").uv(0, 0, 16, 16).cullface(Direction.EAST).tintIndex(0))
                                .south(JModel.face("all").uv(0, 0, 16, 16).cullface(Direction.SOUTH).tintIndex(0))
                                .west(JModel.face("all").uv(0, 0, 16, 16).cullface(Direction.WEST).tintIndex(0))
                                .up(JModel.face("all").uv(0, 0, 16, 16).cullface(Direction.UP).tintIndex(0))
                                .down(JModel.face("all").uv(0, 0, 16, 16).cullface(Direction.DOWN).tintIndex(0))
                        )
                );
        clientResourcePackBuilder.addModel(model, name);
    }

    public static void generateColumnBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier endTexture, Identifier sideTexture) {
        var model = JModel.model("minecraft:block/block")
                .textures(JModel.textures()
                        .var("end", blockTexture(endTexture))
                        .var("side", blockTexture(sideTexture)))
                .element(JModel.element()
                        .bounds(0, 0, 0, 16, 16, 16)
                        .faces(JModel.faces()
                                .down(JModel.face("end").uv(0, 0, 16, 16).cullface(Direction.DOWN).tintIndex(0))
                                .up(JModel.face("end").uv(0, 0, 16, 16).cullface(Direction.UP).tintIndex(0))
                                .north(JModel.face("side").uv(0, 0, 16, 16).cullface(Direction.NORTH).tintIndex(0))
                                .south(JModel.face("side").uv(0, 0, 16, 16).cullface(Direction.SOUTH).tintIndex(0))
                                .west(JModel.face("side").uv(0, 0, 16, 16).cullface(Direction.WEST).tintIndex(0))
                                .east(JModel.face("side").uv(0, 0, 16, 16).cullface(Direction.EAST).tintIndex(0))
                        )
                );
        clientResourcePackBuilder.addModel(model, name);
    }

    public static void generateTopBottomBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier topTexture, Identifier bottomTexture, Identifier sideTexture) {
        var model = JModel.model("minecraft:block/block")
                .textures(JModel.textures()
                        .var("top",    blockTexture(topTexture))
                        .var("bottom", blockTexture(bottomTexture))
                        .var("side",   blockTexture(sideTexture)))
                .element(JModel.element()
                        .bounds(0, 0, 0, 16, 16, 16)
                        .faces(JModel.faces()
                                .up(   JModel.face("top").uv(0,0,16,16).cullface(Direction.UP).tintIndex(0))
                                .down( JModel.face("bottom").uv(0,0,16,16).cullface(Direction.DOWN).tintIndex(0))
                                .north(JModel.face("side").uv(0,0,16,16).cullface(Direction.NORTH).tintIndex(0))
                                .south(JModel.face("side").uv(0,0,16,16).cullface(Direction.SOUTH).tintIndex(0))
                                .east( JModel.face("side").uv(0,0,16,16).cullface(Direction.EAST).tintIndex(0))
                                .west( JModel.face("side").uv(0,0,16,16).cullface(Direction.WEST).tintIndex(0))
                        )
                );
        clientResourcePackBuilder.addModel(model, name);
    }
}
