package net.vampirestudios.raaMaterials;

import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.json.blockstate.JBlockModel;
import net.vampirestudios.arrp.json.blockstate.JState;
import net.vampirestudios.arrp.json.blockstate.JVariant;
import net.vampirestudios.arrp.json.iteminfo.JItemInfo;
import net.vampirestudios.arrp.json.iteminfo.model.JModelBasic;
import net.vampirestudios.arrp.json.models.JModel;
import net.vampirestudios.arrp.json.models.JTextures;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.SlabType;

import java.util.Map;

import static net.vampirestudios.arrp.json.blockstate.JState.variant;
import static net.vampirestudios.arrp.json.models.JModel.model;
import static net.vampirestudios.arrp.json.models.JModel.textures;

public class ARRPGenerationHelper {
    private static String blockTexture(Identifier id) {
        String path = id.getPath();
        if (path.endsWith(".png")) path = path.substring(0, path.length() - 4);
        if (!path.startsWith("block/")) path = "block/" + path;
        return Identifier.fromNamespaceAndPath(id.getNamespace(), path).toString();
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
                                .north(JModel.face("all").uv(0, 0, 16, 16).tintIndex(0))
                                .east(JModel.face("all").uv(0, 0, 16, 16).tintIndex(0))
                                .south(JModel.face("all").uv(0, 0, 16, 16).tintIndex(0))
                                .west(JModel.face("all").uv(0, 0, 16, 16).tintIndex(0))
                                .up(JModel.face("all").uv(0, 0, 16, 16).tintIndex(0))
                                .down(JModel.face("all").uv(0, 0, 16, 16).tintIndex(0))
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
        clientResourcePackBuilder.addModel(model("block/cube_column").textures(textures()
                .var("end", blockTexture(endTexture))
                .var("side", blockTexture(sideTexture))
        ), name);
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
