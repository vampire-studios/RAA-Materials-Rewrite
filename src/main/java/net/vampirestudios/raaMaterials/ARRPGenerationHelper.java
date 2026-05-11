package net.vampirestudios.raaMaterials;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.json.blockstate.JBlockModel;
import net.vampirestudios.arrp.json.blockstate.JState;
import net.vampirestudios.arrp.json.blockstate.JVariant;
import net.vampirestudios.arrp.json.iteminfo.JItemInfo;
import net.vampirestudios.arrp.json.iteminfo.model.JModelBasic;
import net.vampirestudios.arrp.json.loot.JFunction;
import net.vampirestudios.arrp.json.models.JModel;
import net.vampirestudios.arrp.json.models.JTextures;
import net.vampirestudios.raaMaterials.content.ParametricBlock;

import java.util.Map;

import static net.vampirestudios.arrp.json.blockstate.JState.variant;
import static net.vampirestudios.arrp.json.models.JModel.model;
import static net.vampirestudios.arrp.json.models.JModel.textures;

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
            "material_crystal_pane",
            "material_rod_block"
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
        addSpecialRecipe(pack, "block_from_ingots");
        addSpecialRecipe(pack, "ingots_from_block");
        addSpecialRecipe(pack, "ingot_from_nuggets");
        addSpecialRecipe(pack, "nuggets_from_ingot");
        addSpecialRecipe(pack, "raw_block_from_raw");
        addSpecialRecipe(pack, "raw_from_raw_block");
        addSpecialRecipe(pack, "slab_from_block");
        addSpecialRecipe(pack, "stairs_from_block");
        addSpecialRecipe(pack, "wall_from_block");
        addSpecialRecipe(pack, "sandstone_slab_from_sandstone");
        addSpecialRecipe(pack, "sandstone_stairs_from_sandstone");
        addSpecialRecipe(pack, "sandstone_wall_from_sandstone");
        addSpecialRecipe(pack, "brick_slab_from_bricks");
        addSpecialRecipe(pack, "brick_stairs_from_bricks");
        addSpecialRecipe(pack, "brick_wall_from_bricks");
        addSpecialRecipe(pack, "polished_slab_from_polished");
        addSpecialRecipe(pack, "polished_stairs_from_polished");
        addSpecialRecipe(pack, "polished_wall_from_polished");
        addSpecialRecipe(pack, "metal_door_from_ingots");
        addSpecialRecipe(pack, "wood_door_from_blocks");
        addSpecialRecipe(pack, "metal_trapdoor_from_ingots");
        addSpecialRecipe(pack, "wood_trapdoor_from_blocks");
        addSpecialRecipe(pack, "metal_fence_from_ingots");
        addSpecialRecipe(pack, "wood_fence_from_blocks");
        addSpecialRecipe(pack, "metal_fence_gate_from_ingots");
        addSpecialRecipe(pack, "wood_fence_gate_from_blocks");
        addSpecialRecipe(pack, "chain_from_ingot_and_nuggets");
        addSpecialRecipe(pack, "lamp_from_block");
        addSpecialRecipe(pack, "lantern_from_lamp_and_nuggets");
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

    public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addBlockState(JState.state(variant(JState.model(Utils.prependToPath(name, "block/")))), name);
    }

    public static void generateBasicBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addBlockState(JState.state(variant(JState.model(modelId))), name);
    }

    public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        Identifier modelPath = Utils.prependToPath(name, "block/");
        JState hangingModel = JState.state(
                variant().put("hanging=false", JState.model(modelPath)),
                variant().put("hanging=true", JState.model(Utils.prependToPath(modelPath, "_hanging")))
        );
        clientResourcePackBuilder.addBlockState(hangingModel, name);
    }

    public static void generateLanternBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name,
                                                 Identifier modelId, Identifier hangingModel) {
        JState model = JState.state(
                variant().put("hanging=false", JState.model(modelId)),
                variant().put("hanging=true", JState.model(hangingModel))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateLanternBlockModels(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent,
                                                  Map<String, Identifier> textures, Identifier parentHanging,
                                                  Map<String, Identifier> texturesHanging) {
        JModel model = JModel.model(parent);
        JTextures textures1 = JModel.textures();
        if (textures != null)
            textures.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(model.textures(textures1), name);

        JModel hangingModel = JModel.model(parentHanging);
        JTextures textures2 = JModel.textures();
        if (texturesHanging != null)
            texturesHanging.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(hangingModel.textures(textures2), Utils.appendToPath(name, "_hanging"));
    }

    public static void generatePillarBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        Identifier modelPath = Utils.prependToPath(name, "block/");
        JState model = JState.state(
                variant().put("axis=y", JState.model(modelPath)),
                variant().put("axis=x", JState.model(modelPath).x(90).y(90)),
                variant().put("axis=z", JState.model(modelPath).x(90))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generatePillarBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        JState model = JState.state(new JVariant()
                        .put("axis=y", JState.model(modelId))
                        .put("axis=x", JState.model(modelId).x(90).y(90))
                .put("axis=z", JState.model(modelId).x(90))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        Identifier modelPath = Utils.prependToPath(name, "block/");
        JState model = JState.state(new JVariant()
            .put("facing=north", JState.model(modelPath))
            .put("facing=south", JState.model(modelPath).y(180))
            .put("facing=east", JState.model(modelPath).y(90))
            .put("facing=west", JState.model(modelPath).y(270))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        JState model = JState.state(new JVariant()
            .put("facing=north", JState.model(modelId))
            .put("facing=south", JState.model(modelId).y(180))
            .put("facing=east", JState.model(modelId).y(90))
            .put("facing=west", JState.model(modelId).y(270))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateHorizontalFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId, Identifier extraStateModelId, String extraState) {
        JState model = JState.state(new JVariant()
                .put("facing=north", JState.model(modelId))
                .put("facing=south", JState.model(modelId).y(180))
                .put("facing=east", JState.model(modelId).y(90))
                .put("facing=west", JState.model(modelId).y(270))
                .put(String.format("facing=north,%s", extraState), JState.model(extraStateModelId))
                .put(String.format("facing=south,%s", extraState), JState.model(extraStateModelId).y(180))
                .put(String.format("facing=east,%s", extraState), JState.model(extraStateModelId).y(90))
                .put(String.format("facing=west,%s", extraState), JState.model(extraStateModelId).y(270))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        Identifier modelPath = Utils.prependToPath(name, "block/");
        JState model = JState.state(
                variant().put("facing=north", JState.model(modelPath).x(90)),
                variant().put("facing=south", JState.model(modelPath).y(180).x(90)),
                variant().put("facing=east", JState.model(modelPath).y(90).x(90)),
                variant().put("facing=west", JState.model(modelPath).y(270).x(90)),
                variant().put("facing=up", JState.model(modelPath)),
                variant().put("facing=down", JState.model(modelPath).x(180))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateFacingBlockState(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        JState model = JState.state(new JVariant()
                .put("facing=north", JState.model(modelId).x(90))
                .put("facing=south", JState.model(modelId).y(180).x(90))
                .put("facing=east", JState.model(modelId).y(90).x(90))
                .put("facing=west", JState.model(modelId).y(270).x(90))
                .put("facing=up", JState.model(modelId))
                .put("facing=down", JState.model(modelId).x(180))
        );
        clientResourcePackBuilder.addBlockState(model, name);
    }

    public static void generateAllBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addModel(model("block/cube_all").textures(textures()
                .var("all", Utils.prependToPath(name, "block/").toString())
        ), name);
    }

    public static void generateAllBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier texture) {
        clientResourcePackBuilder.addModel(model("block/cube_all").textures(textures()
                .var("all", blockTexture(texture))
        ), name);
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

    public static void generateCrossBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addModel(model("block/cross").textures(textures()
                .var("cross", Utils.prependToPath(name, "block/").toString())
        ), name);
    }

    public static void generateCrossBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier texture) {
        clientResourcePackBuilder.addModel(model("block/cross").textures(textures()
                .var("cross", blockTexture(texture))
        ), name);
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

    public static void generateModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures) {
        JModel itemModel = JModel.model(parent);
        if (textures != null)
            textures.forEach((s, location) -> itemModel.textures(JModel.textures().var(s, location.toString())));
        clientResourcePackBuilder.addModel(itemModel, Utils.prependToPath(name, "block/"));
    }

    public static void generateTopBottomBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier topTexture, Identifier bottomTexture, Identifier sideTexture) {
        clientResourcePackBuilder.addModel(model("block/cube_top_bottom").textures(textures()
                .var("top", blockTexture(topTexture))
                .var("bottom", blockTexture(bottomTexture))
                .var("side", blockTexture(sideTexture))
        ), name);
    }

    public static void generateLadderBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addModel(model("block/ladder").textures(textures()
                .var("texture", Utils.prependToPath(name, "block/").toString())
        ), name);
    }

    public static void generateBlockModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures) {
        JModel model = JModel.model(parent);
        JTextures textures1 = JModel.textures();
        if (textures != null)
            textures.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(model.textures(textures1), Utils.prependToPath(name, "block/"));
    }

    public static void generateBlockItemModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name) {
        clientResourcePackBuilder.addModel(JModel.model(Utils.prependToPath(name, "block/")), Utils.prependToPath(name, "item/"));
    }

    public static void generateBlockItemModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addModel(JModel.model(Utils.prependToPath(modelId, "block/")), Utils.prependToPath(name, "item/"));

    }

    public static void generateBlockItemModel1(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier modelId) {
        clientResourcePackBuilder.addModel(JModel.model(modelId), Utils.prependToPath(name, "item/"));
    }

    public static void generateItemModel(RuntimeResourcePack clientResourcePackBuilder, Identifier name, Identifier parent, Map<String, Identifier> textures) {
        if (name == null || parent == null) return;
        JModel itemModel = JModel.model(parent);
        JTextures textures1 = JModel.textures();
        if (textures != null)
            textures.forEach((s, location) -> textures1.var(s, location.toString()));
        clientResourcePackBuilder.addModel(itemModel.textures(textures1), Utils.prependToPath(name, "item/"));
    }

    public static void generateSlabBlockState(RuntimeResourcePack pack, Identifier name, Identifier doubleBlockName) {
        JState state = JState.state();
        for (SlabType t : SlabType.values()) {
            JBlockModel var = switch (t) {
                case BOTTOM -> JState.model(Utils.prependToPath(name, "block/"));
                case TOP -> JState.model(Utils.appendAndPrependToPath(name, "block/", "_top"));
                case DOUBLE -> JState.model(Utils.prependToPath(doubleBlockName, "block/"));
            };
            state.add(variant().put("type=" + t.name(), var));
        }
        pack.addBlockState(state, name);
    }

    public static void generateBasicItemDefinition(RuntimeResourcePack pack, Identifier name) {
        JItemInfo itemInfo = new JItemInfo()
                .model(JModelBasic.model(Utils.prependToPath(name, "item/").toString()));
        pack.addItemModelInfo(itemInfo, name);
    }

    public static void generateBasicItemDefinition(RuntimeResourcePack pack, Identifier name, Identifier model) {
        JItemInfo itemInfo = new JItemInfo()
                .model(JModelBasic.model(model.toString()));
        pack.addItemModelInfo(itemInfo, name);
    }

}
