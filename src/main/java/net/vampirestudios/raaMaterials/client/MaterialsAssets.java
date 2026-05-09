package net.vampirestudios.raaMaterials.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.json.blockstate.BlockstateTemplates;
import net.vampirestudios.arrp.json.blockstate.JBlockModel;
import net.vampirestudios.arrp.json.blockstate.JState;
import net.vampirestudios.arrp.json.iteminfo.JItemInfo;
import net.vampirestudios.arrp.json.iteminfo.model.JItemModel;
import net.vampirestudios.arrp.json.iteminfo.model.JModelBasic;
import net.vampirestudios.arrp.json.iteminfo.model.JSelectCase;
import net.vampirestudios.arrp.json.iteminfo.property.JPropertyComponent;
import net.vampirestudios.arrp.json.iteminfo.tint.JTint;
import net.vampirestudios.arrp.json.iteminfo.tint.JTintConstant;
import net.vampirestudios.arrp.json.models.JModel;
import net.vampirestudios.arrp.json.recipe.*;
import net.vampirestudios.raaMaterials.ARRPGenerationHelper;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.RRPGen;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.material.*;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MaterialsAssets {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	// ---- Component + shared block ids ----
	private static final JPropertyComponent MAT_COMP = JPropertyComponent.component("raa_materials:material");
	private static final Identifier ORE_SHARED_ID = RAAMaterials.id("material_ore");
	private static final Identifier BLOCK_SHARED_ID = RAAMaterials.id("material_block");
	private static final Identifier RAW_BLOCK_SHARED_ID = RAAMaterials.id("material_raw_block");
	private static final Identifier SHINGLES_SHARED_ID = RAAMaterials.id("material_shingles");
	private static final Identifier PLATE_BLOCK_SHARED_ID = RAAMaterials.id("material_plate_block");
	// Building variant block IDs (WIP)
	private static final Identifier SANDSTONE_SHARED_ID = RAAMaterials.id("material_sandstone");
	private static final Identifier CUT_SANDSTONE_SHARED_ID = RAAMaterials.id("material_cut");
	private static final Identifier SMOOTH_SANDSTONE_SHARED_ID = RAAMaterials.id("material_smooth");
	private static final Identifier CHISELED_SHARED_ID = RAAMaterials.id("material_chiseled");
	private static final Identifier BRICKS_SHARED_ID = RAAMaterials.id("material_bricks");
	private static final Identifier POLISHED_SHARED_ID = RAAMaterials.id("material_polished");
	private static final Identifier DRIED_SHARED_ID = RAAMaterials.id("material_dried");
	private static final Identifier CERAMIC_SHARED_ID = RAAMaterials.id("material_ceramic");
	// Base shapes (BLOCK)
	private static final Identifier SLAB_SHARED_ID = RAAMaterials.id("material_slab");
	private static final Identifier STAIRS_SHARED_ID = RAAMaterials.id("material_stairs");
	private static final Identifier WALL_SHARED_ID = RAAMaterials.id("material_wall");
	// Variant shapes (WIP)
	private static final Identifier SANDSTONE_SLAB_SHARED_ID = RAAMaterials.id("material_sandstone_slab");
	private static final Identifier SANDSTONE_STAIRS_SHARED_ID = RAAMaterials.id("material_sandstone_stairs");
	private static final Identifier SANDSTONE_WALL_SHARED_ID = RAAMaterials.id("material_sandstone_wall");
	private static final Identifier BRICK_SLAB_SHARED_ID = RAAMaterials.id("material_brick_slab");
	private static final Identifier BRICK_STAIRS_SHARED_ID = RAAMaterials.id("material_brick_stairs");
	private static final Identifier BRICK_WALL_SHARED_ID = RAAMaterials.id("material_brick_wall");
	private static final Identifier POLISHED_SLAB_SHARED_ID = RAAMaterials.id("material_polished_slab");
	private static final Identifier POLISHED_STAIRS_SHARED_ID = RAAMaterials.id("material_polished_stairs");
	private static final Identifier POLISHED_WALL_SHARED_ID = RAAMaterials.id("material_polished_wall");

	// ---- Shared item ids ----
	private static final Identifier INGOT_SHARED_ID = RAAMaterials.id("material_ingot");
	private static final Identifier RAW_SHARED_ID = RAAMaterials.id("material_raw");
	private static final Identifier NUGGET_SHARED_ID = RAAMaterials.id("material_nugget");
	private static final Identifier DUST_SHARED_ID = RAAMaterials.id("material_dust");
	private static final Identifier PLATE_SHARED_ID = RAAMaterials.id("material_sheet");
	private static final Identifier CRYSTAL_SHARED_ID = RAAMaterials.id("material_crystal");
	private static final Identifier SHARD_SHARED_ID = RAAMaterials.id("material_shard");
	private static final Identifier GEAR_SHARED_ID = RAAMaterials.id("material_gear");
	private static final Identifier CLUSTER_SHARED_ID = RAAMaterials.id("material_cluster");
	private static final Identifier GEM_SHARED_ID = RAAMaterials.id("material_gem");
	private static final Identifier BALL_SHARED_ID = RAAMaterials.id("material_ball");
	private static final Identifier ROD_SHARED_ID = RAAMaterials.id("material_rod");
	private static final Identifier SHOVEL_SHARED_ID = RAAMaterials.id("material_shovel");
	private static final Identifier HOE_SHARED_ID = RAAMaterials.id("material_hoe");
	private static final Identifier SWORD_SHARED_ID = RAAMaterials.id("material_sword");
	private static final Identifier PICKAXE_SHARED_ID = RAAMaterials.id("material_pickaxe");
	private static final Identifier AXE_SHARED_ID = RAAMaterials.id("material_axe");

	private static boolean DIRTY = false;
	private static boolean reloading = false;

	private static MaterialAssetsDef texture(MaterialDef def) {
		return MaterialAssets.generate(def);
	}

	/** Export all assets as individual JSON files under &lt;gameDir&gt;/dev_export. Only runs in dev. */
	public static void saveDevExport(Path gameDir) throws IOException {
		saveDevExport(gameDir, ClientMaterialCache.all());
	}

	public static void saveDevExport(Path gameDir, List<MaterialDef> materials) throws IOException {
		Path root = gameDir.resolve("dev_export");
		Files.createDirectories(root);

		for (MaterialDef m : materials) {
			MaterialAssetsDef def = MaterialAssets.generate(m);

			Path out = root
					.resolve("data")
					.resolve(m.nameInformation().id().getNamespace())
					.resolve("assets")
					.resolve(m.nameInformation().id().getPath() + ".json");

			Files.createDirectories(out.getParent());
			JsonElement json = encodeOrThrow(MaterialAssetsDef.CODEC, def);
			try (Writer w = Files.newBufferedWriter(out)) {
				GSON.toJson(json, w);
			}
		}
	}

	private static <T> JsonElement encodeOrThrow(Codec<T> codec, T value) {
		DataResult<JsonElement> dr = codec.encodeStart(JsonOps.INSTANCE, value);
		return dr.getOrThrow();
	}

	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!DIRTY || reloading || client.level == null) return;
			DIRTY = false;
			buildAll();
			reloading = true;
			client.execute(() -> {
				client.reloadResourcePacks();
				reloading = false;
			});
			registerBlockColorProviders();
		});

		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(RAAMaterials.id("materials_assets_rebuilder"),
				(_, _, preparationBarrier, _) -> {
					if (!ClientMaterialCache.all().isEmpty()) buildAll();
					return preparationBarrier.wait(null);
				}
		);
	}

	private static void registerBlockColorProviders() {
		var materials = ClientMaterialCache.all();
		registerColorProviderForSharedBlock(ORE_SHARED_ID, materials);
		registerColorProviderForSharedBlock(BLOCK_SHARED_ID, materials);
		registerColorProviderForSharedBlock(RAW_BLOCK_SHARED_ID, materials);
		registerColorProviderForSharedBlock(SHINGLES_SHARED_ID, materials);
		registerColorProviderForSharedBlock(PLATE_BLOCK_SHARED_ID, materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_crystal_cluster"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_crystal_bud_small"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_crystal_bud_medium"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_crystal_bud_large"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_crystal_bricks"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_crystal_glass"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_crystal_tinted_glass"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_basalt_lamp"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_calcite_lamp"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_crystal_pane"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_rod_block"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_cobbled"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_bricks"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_polished"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_sandstone"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_cut"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_smooth"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_chiseled"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_dried"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_ceramic"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_tiles"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_mosaic"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_mossy"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_cracked"), materials);
		registerColorProviderForSharedBlock(RAAMaterials.id("material_pillar"), materials);
	}

	private static void registerColorProviderForSharedBlock(Identifier sharedBlockId, List<MaterialDef> materials) {
		var block = BuiltInRegistries.BLOCK.getValue(sharedBlockId);
		if (block == null) {
			RAAMaterials.LOGGER.warn("[RAA] Block not found for id: {}", sharedBlockId);
			return;
		}
		BlockColorRegistry.register((state, _, _, tintValues) -> {
			int matIndex = state.getValue(ParametricBlock.MAT);
			if (matIndex >= 0 && matIndex < materials.size()) {
				tintValues.add(opaqueColor(materials.get(matIndex).primaryColor()));
			}
		}, block);
	}

	private static int opaqueColor(int color) {
		return color | 0xFF000000;
	}

	private static JModelBasic tintedModel(Identifier modelId, MaterialDef def) {
		return (JModelBasic) JModelBasic.model(modelId.toString()).tint(JTint.constant(opaqueColor(def.primaryColor())));
	}

	public static void markDirty() {
		DIRTY = true;
	}

	private static void buildAll() {
		var rp = RRPGen.PACK;

		// Fallback item models (avoid missing-model flashes)
		rp.addModel(JModel.model("minecraft:item/stone"), RAAMaterials.id("item/material_ore"));
		rp.addModel(JModel.model("minecraft:item/iron_ingot"), RAAMaterials.id("item/material_ingot"));
		rp.addModel(JModel.model("minecraft:item/raw_iron"), RAAMaterials.id("item/material_raw"));
		rp.addModel(JModel.model("minecraft:item/gold_nugget"), RAAMaterials.id("item/material_nugget"));
		rp.addModel(JModel.model("minecraft:item/sugar"), RAAMaterials.id("item/material_dust"));
		rp.addModel(JModel.model("minecraft:item/iron_ingot"), RAAMaterials.id("item/material_plate"));
		rp.addModel(JModel.model("minecraft:item/stick"), RAAMaterials.id("item/material_rod"));

		var def = ClientMaterialCache.all();

		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			try {
				saveDevExport(FabricLoader.getInstance().getGameDir());
			} catch (IOException e) {
				RAAMaterials.LOGGER.error("[RAA] Dev export failed", e);
			}
		}

		// ---- BLOCK FAMILIES (cube-all) ----
		buildOreFamily(ORE_SHARED_ID, "block/material_ore/",
				(idx) -> texture(def.get(idx)).textures1().oreVein().orElseThrow());

		Map<MaterialKind, TexPicker> blockTextures = Map.of(
				MaterialKind.METAL,   (idx) -> RAAMaterials.id("storage_blocks/metals/metal_" + oneIndexed(idx, 23)),
				MaterialKind.CRYSTAL, (idx) -> RAAMaterials.id("crystal/crystal_block_" + oneIndexed(idx, 5)),
				MaterialKind.GEM,     (idx) -> RAAMaterials.id("storage_blocks/gems/gem_" + oneIndexed(idx, 16)),
				MaterialKind.STONE,   (idx) -> RAAMaterials.id("stone/a/stone_" + oneIndexed(idx, 23)),
				MaterialKind.SAND,    (idx) -> RAAMaterials.id("storage_blocks/sand_" + oneIndexed(idx, 3)),
				MaterialKind.MUD,     (idx) -> Identifier.withDefaultNamespace("mud"),
				MaterialKind.GRAVEL,  (idx) -> Identifier.withDefaultNamespace("gravel"),
				MaterialKind.CLAY,    (idx) -> Identifier.withDefaultNamespace("clay")
		);

		buildBlockFamily(List.of(Form.BLOCK), BLOCK_SHARED_ID, "block/material_block/", blockTextures);

		buildBlockFamily(List.of(Form.RAW_BLOCK), RAW_BLOCK_SHARED_ID, "block/material_raw_block/",
				(idx) -> texture(def.get(idx)).textures1().rawBlock().orElseThrow());
		buildBlockFamily(List.of(Form.CRYSTAL_BRICKS), RAAMaterials.id("material_crystal_bricks"), "block/material_crystal_bricks/",
				(idx) -> texture(def.get(idx)).textures2().crystalBricks().orElseThrow());
		buildBlockFamily(List.of(Form.GLASS), RAAMaterials.id("material_crystal_glass"), "block/material_crystal_glass/",
				(idx) -> texture(def.get(idx)).textures2().crystalGlass().orElseThrow());
		buildBlockFamily(List.of(Form.TINTED_GLASS), RAAMaterials.id("material_crystal_tinted_glass"), "block/material_crystal_tinted_glass/",
				(idx) -> texture(def.get(idx)).textures1().tintedGlass().orElseThrow());
		buildBlockFamily(List.of(Form.BASALT_LAMP), RAAMaterials.id("material_basalt_lamp"), "block/material_basalt_lamp/",
				(idx) -> texture(def.get(idx)).textures2().lampBasalt().orElse(RAAMaterials.id("crystal/basalt_lamp")));
		buildBlockFamily(List.of(Form.CALCITE_LAMP), RAAMaterials.id("material_calcite_lamp"), "block/material_calcite_lamp/",
				(idx) -> texture(def.get(idx)).textures2().lampCalcite().orElse(RAAMaterials.id("crystal/calcite_lamp")));
		buildCrystalClusterFamily(RAAMaterials.id("material_crystal_cluster"), Form.CLUSTER, "block/material_crystal_cluster/",
				(idx) -> texture(def.get(idx)).textures1().cluster().orElse(RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, 9))));
		buildCrystalBudFamily(RAAMaterials.id("material_crystal_bud_small"), Form.BUD_SMALL, "block/material_crystal_bud_small/",
				(idx) -> texture(def.get(idx)).textures1().budSmall().orElse(RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, 9))));
		buildCrystalBudFamily(RAAMaterials.id("material_crystal_bud_medium"), Form.BUD_MEDIUM, "block/material_crystal_bud_medium/",
				(idx) -> texture(def.get(idx)).textures1().budMedium().orElse(RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, 9))));
		buildCrystalBudFamily(RAAMaterials.id("material_crystal_bud_large"), Form.BUD_LARGE, "block/material_crystal_bud_large/",
				(idx) -> texture(def.get(idx)).textures1().budLarge().orElse(RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, 9))));
		buildPaneFamily(RAAMaterials.id("material_crystal_pane"), "block/material_crystal_pane/",
				(idx) -> texture(def.get(idx)).textures2().crystalGlass().orElse(RAAMaterials.id("crystal/crystal_glass")));
		buildRodBlockFamily(RAAMaterials.id("material_rod_block"), "block/material_rod_block/",
				(idx) -> RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, 9)));
		buildBlockFamily(List.of(Form.COBBLED), RAAMaterials.id("material_cobbled"), "block/material_cobbled/",
				(idx) -> texture(def.get(idx)).textures3().cobblestone().orElseThrow());
		buildBlockFamily(List.of(Form.BRICKS), RAAMaterials.id("material_bricks"), "block/material_bricks/",
				(idx) -> texture(def.get(idx)).textures3().bricks().orElseThrow());
		buildBlockFamily(List.of(Form.POLISHED), RAAMaterials.id("material_polished"), "block/material_polished/",
				(idx) -> texture(def.get(idx)).textures3().polished().orElseThrow());
		buildBlockFamily(List.of(Form.SHINGLES), SHINGLES_SHARED_ID, "block/material_shingles/",
				(idx) -> texture(def.get(idx)).textures1().shingles().orElseThrow());
		buildBlockFamily(List.of(Form.PLATE_BLOCK), PLATE_BLOCK_SHARED_ID, "block/material_plate_block/",
				(idx) -> texture(def.get(idx)).textures1().plateBlock().orElseThrow());

		// Building families — WIP (uses building atlas textures; wire up when assets are ready)
		buildBlockFamily(List.of(Form.SANDSTONE), SANDSTONE_SHARED_ID, "block/material_sandstone/",
				(idx) -> texture(def.get(idx)).textures1().sandstoneSide()
						.orElse(RAAMaterials.id("storage_blocks/sand_" + oneIndexed(idx, 3))));
		buildBlockFamily(List.of(Form.CUT), CUT_SANDSTONE_SHARED_ID, "block/material_cut/",
				(idx) -> texture(def.get(idx)).textures1().cutSandstoneSide()
						.orElse(RAAMaterials.id("storage_blocks/sand_" + oneIndexed(idx, 3))));
		buildBlockFamily(List.of(Form.SMOOTH), SMOOTH_SANDSTONE_SHARED_ID, "block/material_smooth/",
				(idx) -> texture(def.get(idx)).textures1().sandstoneTop()
						.orElse(RAAMaterials.id("storage_blocks/sand_" + oneIndexed(idx, 3))));
		buildBlockFamily(List.of(Form.CHISELED), CHISELED_SHARED_ID, "block/material_chiseled/",
				(idx) -> texture(def.get(idx)).textures3().chiseled()
						.orElse(RAAMaterials.id("stone/stone_chiseled_" + oneIndexed(idx, 4))));
		buildBlockFamily(List.of(Form.DRIED), DRIED_SHARED_ID, "block/material_dried/",
				(idx) -> RAAMaterials.id("stone/stone_bricks_" + oneIndexed(idx, 24)));
		buildBlockFamily(List.of(Form.CERAMIC), CERAMIC_SHARED_ID, "block/material_ceramic/",
				(idx) -> RAAMaterials.id("stone/stone_tiles_" + oneIndexed(idx, 8)));
		buildBlockFamily(List.of(Form.TILES), RAAMaterials.id("material_tiles"), "block/material_tiles/",
				(idx) -> RAAMaterials.id("stone/stone_tiles_" + oneIndexed(idx, 8)));
		buildBlockFamily(List.of(Form.MOSAIC), RAAMaterials.id("material_mosaic"), "block/material_mosaic/",
				(idx) -> RAAMaterials.id("stone/stone_frame_" + oneIndexed(idx, 13)));
		buildBlockFamily(List.of(Form.MOSSY), RAAMaterials.id("material_mossy"), "block/material_mossy/",
				(idx) -> texture(def.get(idx)).textures3().bricks()
						.orElse(RAAMaterials.id("stone/stone_bricks_" + oneIndexed(idx, 24))));
		buildBlockFamily(List.of(Form.CRACKED), RAAMaterials.id("material_cracked"), "block/material_cracked/",
				(idx) -> texture(def.get(idx)).textures3().cobblestone()
						.orElse(RAAMaterials.id("stone/stone_cobbled_" + oneIndexed(idx, 7))));
		buildPillarFamily(RAAMaterials.id("material_pillar"), "block/material_pillar/",
				(idx) -> RAAMaterials.id("stone/stone_frame_" + oneIndexed(idx, 13)),
				(idx) -> RAAMaterials.id("stone/stone_tiles_" + oneIndexed(idx, 8)));

		// ---- SHAPES (use BLOCK textures) ----
		buildSlabFamilyForBlock(SLAB_SHARED_ID, blockTextures);
		buildStairsFamilyForBlock(STAIRS_SHARED_ID, blockTextures);
		buildWallFamilyForBlock(WALL_SHARED_ID, blockTextures);

		// Variant shapes — WIP (attach to per-variant atlas textures)
//		buildSlabFamilyVariant(SANDSTONE_SLAB_SHARED_ID, "block/material_sandstone/",
//				(idx) -> RAAMaterials.id("building/sandstone/sandstone_" + oneIndexed(idx, 10)),
//				RAAMaterials.id("block/material_sandstone/"));
//		buildStairsFamilyVariant(SANDSTONE_STAIRS_SHARED_ID, "block/material_sandstone/",
//				(idx) -> RAAMaterials.id("building/sandstone/sandstone_" + oneIndexed(idx, 10)));
//		buildWallFamilyVariant(SANDSTONE_WALL_SHARED_ID, "block/material_sandstone/",
//				(idx) -> RAAMaterials.id("building/sandstone/sandstone_" + oneIndexed(idx, 10)));
//		buildSlabFamilyVariant(BRICK_SLAB_SHARED_ID, "block/material_bricks/",
//				(idx) -> RAAMaterials.id("building/bricks/bricks_" + oneIndexed(idx, 12)),
//				RAAMaterials.id("block/material_bricks/"));
//		buildStairsFamilyVariant(BRICK_STAIRS_SHARED_ID, "block/material_bricks/",
//				(idx) -> RAAMaterials.id("building/bricks/bricks_" + oneIndexed(idx, 12)));
//		buildWallFamilyVariant(BRICK_WALL_SHARED_ID, "block/material_bricks/",
//				(idx) -> RAAMaterials.id("building/bricks/bricks_" + oneIndexed(idx, 12)));
//		buildSlabFamilyVariant(POLISHED_SLAB_SHARED_ID, "block/material_polished/",
//				(idx) -> RAAMaterials.id("building/polished/polished_" + oneIndexed(idx, 12)),
//				RAAMaterials.id("block/material_polished/"));
//		buildStairsFamilyVariant(POLISHED_STAIRS_SHARED_ID, "block/material_polished/",
//				(idx) -> RAAMaterials.id("building/polished/polished_" + oneIndexed(idx, 12)));
//		buildWallFamilyVariant(POLISHED_WALL_SHARED_ID, "block/material_polished/",
//				(idx) -> RAAMaterials.id("building/polished/polished_" + oneIndexed(idx, 12)));

		// ---- ITEM FAMILIES ----
		buildItemFamily(List.of(Form.INGOT), INGOT_SHARED_ID, "item/material_ingot/",
				(idx) -> texture(def.get(idx)).textures2().ingot().orElseThrow());
		buildItemFamily(List.of(Form.RAW), RAW_SHARED_ID, "item/material_raw/",
				(idx) -> texture(def.get(idx)).textures2().raw().orElseThrow());
		buildItemFamily(List.of(Form.NUGGET), NUGGET_SHARED_ID, "item/material_nugget/",
				(idx) -> texture(def.get(idx)).textures2().nugget().orElseThrow());
		buildItemFamily(List.of(Form.DUST), DUST_SHARED_ID, "item/material_dust/",
				(idx) -> texture(def.get(idx)).textures2().dust().orElseThrow());
		buildItemFamily(List.of(Form.SHEET), PLATE_SHARED_ID, "item/material_plate/",
				(idx) -> texture(def.get(idx)).textures2().plate().orElseThrow());
//		buildItemFamily(List.of(Form.CRYSTAL), CRYSTAL_SHARED_ID, "item/material_crystal/",
//				(idx) -> texture(def.get(idx)).textures2().crystal().orElseThrow());
		buildItemFamily(List.of(Form.SHARD), SHARD_SHARED_ID, "item/material_shard/",
				(idx) -> texture(def.get(idx)).textures2().shard().orElseThrow());
		buildItemFamily(List.of(Form.GEAR), GEAR_SHARED_ID, "item/material_gear/",
				(idx) -> texture(def.get(idx)).textures2().gear().orElseThrow());
//		buildItemFamily(List.of(Form.CLUSTER), CLUSTER_SHARED_ID, "item/material_cluster/",
//				(idx) -> texture(def.get(idx)).textures2().cluster().orElseThrow());
		buildItemFamily(List.of(Form.GEM), GEM_SHARED_ID, "item/material_gem/",
				(idx) -> texture(def.get(idx)).textures2().gem().orElseThrow());
//		buildItemFamily(List.of(Form.BALL), BALL_SHARED_ID, "item/material_ball/",
//				(idx) -> texture(def.get(idx)).textures2().ball().orElseThrow());
		buildLayeredItemFamily(Form.SHOVEL, SHOVEL_SHARED_ID, "item/material_shovel/",
				(idx) -> texture(def.get(idx)).textures3().toolShovelHead().orElseThrow(),
				(idx) -> texture(def.get(idx)).textures3().toolShovelStick().orElseThrow());
		buildLayeredItemFamily(Form.HOE, HOE_SHARED_ID, "item/material_hoe/",
				(idx) -> texture(def.get(idx)).textures3().toolHoeHead().orElseThrow(),
				(idx) -> texture(def.get(idx)).textures3().toolHoeStick().orElseThrow());
		buildLayeredItemFamily(Form.SWORD, SWORD_SHARED_ID, "item/material_sword/",
				(idx) -> texture(def.get(idx)).textures3().toolSwordBlade().orElseThrow(),
				(idx) -> texture(def.get(idx)).textures3().toolSwordHandle().orElseThrow());
		buildLayeredItemFamily(Form.PICKAXE, PICKAXE_SHARED_ID, "item/material_pickaxe/",
				(idx) -> texture(def.get(idx)).textures2().toolPickaxeHead().orElseThrow(),
				(idx) -> texture(def.get(idx)).textures2().toolPickaxeStick().orElseThrow());
		buildLayeredItemFamily(Form.AXE, AXE_SHARED_ID, "item/material_axe/",
				(idx) -> texture(def.get(idx)).textures3().toolAxeHead().orElseThrow(),
				(idx) -> texture(def.get(idx)).textures3().toolAxeStick().orElseThrow());

		RRPGen.PACK.dump();
	}

	// =========================================================
	// Helpers — BLOCKS
	// =========================================================

	private static int oneIndexed(int idx, int max) {
		return (Math.floorMod(idx, max) + 1);
	}

	private static String blockTexture(Identifier id) {
		String path = id.getPath();
		if (path.endsWith(".png")) path = path.substring(0, path.length() - 4);
		if (!path.startsWith("block/")) path = "block/" + path;
		return Identifier.fromNamespaceAndPath(id.getNamespace(), path).toString();
	}

	/** Resolves a block texture for a material given the kind-to-picker map. */
	private static Identifier pickBlockTex(MaterialDef def, int idx, Map<MaterialKind, TexPicker> pickers) {
		return switch (def.kind()) {
			case METAL, ALLOY, OTHER -> pickers.get(MaterialKind.METAL).pick(idx);
			case GEM -> pickers.get(MaterialKind.GEM).pick(idx);
			case CRYSTAL -> pickers.get(MaterialKind.CRYSTAL).pick(idx);
			case STONE, CLAY, GRAVEL, SOIL, MUD, SALT, VOLCANIC -> pickers.get(MaterialKind.STONE).pick(idx);
			case SAND -> pickers.get(MaterialKind.SAND).pick(idx);
			case WOOD -> Identifier.withDefaultNamespace("block/oak_planks");
		};
	}

	private static void buildBlockFamily(
			List<Form> forms, Identifier sharedBlockId, String perModelPrefix, TexPicker picker
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();

		List<JBlockModelEntry> variants = new ArrayList<>();
		var select = JItemModel.select().property(MAT_COMP);
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), forms)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());
			var tex = picker.pick(idx);
			addCubeAllTintedBlockModel(rp, perModelId, tex);
			variants.add(new JBlockModelEntry(idx, JState.model(perModelId)));
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(),
					tintedModel(perModelId, def)));
			cases++;
		}

		if (cases == 0) {
			rp.addItemModelInfo(
					new JItemInfo().model(JModelBasic.of(sharedBlockId.withPrefix("block/").toString())),
					sharedBlockId
			);
			return;
		}

		var v = JState.variant();
		for (var e : variants) v.put("mat", e.idx, e.model);
		rp.addBlockState(JState.state(v), sharedBlockId);

		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
	}

	private static void buildBlockFamily(
			List<Form> forms, Identifier sharedBlockId, String perModelPrefix,
			Map<MaterialKind, TexPicker> pickers
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();

		List<JBlockModelEntry> variants = new ArrayList<>();
		var select = JItemModel.select().property(MAT_COMP);
		int cases = 0;
		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), forms)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());
			var tex = pickBlockTex(def, idx, pickers);
			addCubeAllTintedBlockModel(rp, perModelId, tex);
			variants.add(new JBlockModelEntry(idx, JState.model(perModelId)));
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(perModelId, def)));
			cases++;
		}

		if (cases == 0) return;
		var v = JState.variant();
		for (var e : variants) v.put("mat", e.idx, e.model);
		rp.addBlockState(JState.state(v), sharedBlockId);

		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
	}

	private static void addCubeAllTintedBlockModel(RuntimeResourcePack rp, Identifier modelId, Identifier texture) {
		ARRPGenerationHelper.generateAllTintedBlockModel(rp, modelId, texture);
	}

	private static void buildOreFamily(Identifier sharedBlockId, String perModelPrefix, TexPicker oreTexture) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();

		List<JBlockModelEntry> variants = new ArrayList<>();
		var select = JItemModel.select().property(MAT_COMP);
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), Form.ORE)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());
			addOreBlockModel(rp, perModelId, def.host().baseTexture(), oreTexture.pick(idx));
			variants.add(new JBlockModelEntry(idx, JState.model(perModelId)));
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(),
					tintedModel(perModelId, def)));
			cases++;
		}

		if (cases == 0) return;
		var v = JState.variant();
		for (var e : variants) v.put("mat", e.idx, e.model);
		rp.addBlockState(JState.state(v), sharedBlockId);

		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
	}

	private static void addOreBlockModel(RuntimeResourcePack rp, Identifier modelId, Identifier baseTexture, Identifier overlayTexture) {
		var base = blockTexture(baseTexture);
		var overlay = blockTexture(overlayTexture);
		var model = JModel.model("minecraft:block/block")
				.textures(JModel.textures()
						.var("particle", base)
						.var("base", base)
						.var("overlay", overlay))
				.element(JModel.element()
						.bounds(0, 0, 0, 16, 16, 16)
						.faces(JModel.faces()
								.north(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.NORTH))
								.east(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.EAST))
								.south(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.SOUTH))
								.west(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.WEST))
								.up(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.UP))
								.down(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.DOWN))))
				.element(JModel.element()
						.bounds(-0.01f, -0.01f, -0.01f, 16.01f, 16.01f, 16.01f)
						.faces(JModel.faces()
								.north(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.NORTH).tintIndex(0))
								.east(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.EAST).tintIndex(0))
								.south(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.SOUTH).tintIndex(0))
								.west(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.WEST).tintIndex(0))
								.up(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.UP).tintIndex(0))
								.down(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.DOWN).tintIndex(0))));
		rp.addModel(model, modelId);
	}

	private static void buildPillarFamily(
			Identifier sharedBlockId, String perModelPrefix, TexPicker endTexture, TexPicker sideTexture
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), Form.PILLAR)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());
			ARRPGenerationHelper.generateColumnBlockModel(rp, perModelId, endTexture.pick(idx), sideTexture.pick(idx));
			v.put(Map.of("mat", idx, "axis", "y"), JState.model(perModelId));
			v.put(Map.of("mat", idx, "axis", "x"), JState.model(perModelId).x(90).y(90));
			v.put(Map.of("mat", idx, "axis", "z"), JState.model(perModelId).x(90));
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(),
					tintedModel(perModelId, def)));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(JState.state(v), sharedBlockId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
	}

	private static void buildCrystalClusterFamily(
			Identifier sharedBlockId, Form form, String perModelPrefix, TexPicker texture
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), form)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());
			rp.addModel(JModel.model("minecraft:block/cross")
					.textures(JModel.textures().var("cross", blockTexture(texture.pick(idx)))), perModelId);
			addFacingVariants(v, idx, perModelId);
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(),
					tintedModel(perModelId, def)));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(JState.state(v), sharedBlockId);
		select.fallback(JModelBasic.of("minecraft:block/amethyst_cluster"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
	}

	private static void buildCrystalBudFamily(
			Identifier sharedBlockId, Form form, String perModelPrefix, TexPicker texture
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var v = JState.variant();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), form)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());
			rp.addModel(JModel.model("minecraft:block/cross")
					.textures(JModel.textures().var("cross", blockTexture(texture.pick(idx)))), perModelId);
			addFacingVariants(v, idx, perModelId);
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(JState.state(v), sharedBlockId);
	}

	private static void buildRodBlockFamily(Identifier sharedBlockId, String perModelPrefix, TexPicker texture) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), Form.ROD_BLOCK)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());
			rp.addModel(JModel.model("minecraft:block/end_rod")
					.textures(JModel.textures().var("end_rod", blockTexture(texture.pick(idx)))), perModelId);
			addFacingVariants(v, idx, perModelId);
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(),
					tintedModel(perModelId, def)));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(JState.state(v), sharedBlockId);
		select.fallback(JModelBasic.of("minecraft:block/end_rod"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
	}

	private static void buildPaneFamily(Identifier sharedBlockId, String perModelPrefix, TexPicker texture) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var state = JState.state();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), Form.PANE)) continue;

			var tex = blockTexture(texture.pick(idx));
			var post = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath() + "_post");
			var side = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath() + "_side");
			var sideAlt = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath() + "_side_alt");
			var noSide = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath() + "_noside");
			var noSideAlt = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath() + "_noside_alt");

			rp.addModel(JModel.model("minecraft:block/template_glass_pane_post")
					.textures(JModel.textures().var("pane", tex)), post);
			rp.addModel(JModel.model("minecraft:block/template_glass_pane_side")
					.textures(JModel.textures().var("pane", tex)), side);
			rp.addModel(JModel.model("minecraft:block/template_glass_pane_side_alt")
					.textures(JModel.textures().var("pane", tex)), sideAlt);
			rp.addModel(JModel.model("minecraft:block/template_glass_pane_noside")
					.textures(JModel.textures().var("pane", tex)), noSide);
			rp.addModel(JModel.model("minecraft:block/template_glass_pane_noside_alt")
					.textures(JModel.textures().var("pane", tex)), noSideAlt);

			state.add(JState.multipart(JState.model(post)).when(Map.of("mat", idx)));
			state.add(JState.multipart(JState.model(side)).when(Map.of("mat", idx, "north", "true")));
			state.add(JState.multipart(JState.model(side).y(90)).when(Map.of("mat", idx, "east", "true")));
			state.add(JState.multipart(JState.model(sideAlt)).when(Map.of("mat", idx, "south", "true")));
			state.add(JState.multipart(JState.model(sideAlt).y(90)).when(Map.of("mat", idx, "west", "true")));
			state.add(JState.multipart(JState.model(noSide)).when(Map.of("mat", idx, "north", "false")));
			state.add(JState.multipart(JState.model(noSideAlt)).when(Map.of("mat", idx, "east", "false")));
			state.add(JState.multipart(JState.model(noSideAlt).y(90)).when(Map.of("mat", idx, "south", "false")));
			state.add(JState.multipart(JState.model(noSide).y(270)).when(Map.of("mat", idx, "west", "false")));
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(),
					tintedModel(post, def)));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(state, sharedBlockId);
		select.fallback(JModelBasic.of("minecraft:block/glass_pane_post"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
	}

	private static void addFacingVariants(net.vampirestudios.arrp.json.blockstate.JVariant v, int mat, Identifier model) {
		v.put(Map.of("mat", mat, "facing", "up"), JState.model(model));
		v.put(Map.of("mat", mat, "facing", "down"), JState.model(model).x(180));
		v.put(Map.of("mat", mat, "facing", "north"), JState.model(model).x(90));
		v.put(Map.of("mat", mat, "facing", "south"), JState.model(model).x(90).y(180));
		v.put(Map.of("mat", mat, "facing", "west"), JState.model(model).x(90).y(270));
		v.put(Map.of("mat", mat, "facing", "east"), JState.model(model).x(90).y(90));
	}

	private static void addWallParts(JState state, Map<String, ?> base, Identifier post, Identifier side, Identifier sideTall) {
		state.add(JState.multipart(JState.model(post)).when(base));
		state.add(JState.multipart(JState.model(side).uvlock().y(0)).when(BlockstateTemplates.plus(base, "north", "low")));
		state.add(JState.multipart(JState.model(side).uvlock().y(90)).when(BlockstateTemplates.plus(base, "east", "low")));
		state.add(JState.multipart(JState.model(side).uvlock().y(180)).when(BlockstateTemplates.plus(base, "south", "low")));
		state.add(JState.multipart(JState.model(side).uvlock().y(270)).when(BlockstateTemplates.plus(base, "west", "low")));
		state.add(JState.multipart(JState.model(sideTall).uvlock().y(0)).when(BlockstateTemplates.plus(base, "north", "tall")));
		state.add(JState.multipart(JState.model(sideTall).uvlock().y(90)).when(BlockstateTemplates.plus(base, "east", "tall")));
		state.add(JState.multipart(JState.model(sideTall).uvlock().y(180)).when(BlockstateTemplates.plus(base, "south", "tall")));
		state.add(JState.multipart(JState.model(sideTall).uvlock().y(270)).when(BlockstateTemplates.plus(base, "west", "tall")));
	}

	private static boolean hasAny(List<Form> actual, List<Form> wanted) {
		for (var f : wanted) if (actual.contains(f)) return true;
		return false;
	}

	private static boolean hasAny(List<Form> actual, Form wanted) {
		return actual.contains(wanted);
	}

	private static void buildSlabFamilyForBlock(Identifier sharedSlabId, Map<MaterialKind, TexPicker> pickers) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			var forms = FormsRuntime.activeForms(Minecraft.getInstance().level, def);
			if (!forms.contains(Form.SLAB) || !forms.contains(Form.BLOCK)) continue;

			var baseTex = pickBlockTex(def, idx, pickers);
			var modelBottom = RAAMaterials.id("block/material_block/" + def.nameInformation().id().getPath() + "_slab");
			var modelTop = RAAMaterials.id("block/material_block/" + def.nameInformation().id().getPath() + "_slab_top");
			var fullModel = RAAMaterials.id("block/material_block/" + def.nameInformation().id().getPath());

			rp.addModel(JModel.model("minecraft:block/slab")
					.textures(JModel.textures()
							.var("bottom", blockTexture(baseTex))
							.var("top", blockTexture(baseTex))
							.var("side", blockTexture(baseTex))), modelBottom);
			rp.addModel(JModel.model("minecraft:block/slab_top")
					.textures(JModel.textures()
							.var("bottom", blockTexture(baseTex))
							.var("top", blockTexture(baseTex))
							.var("side", blockTexture(baseTex))), modelTop);

			BlockstateTemplates.addSlab(v, Map.of("mat", idx), JState.model(modelBottom), JState.model(modelTop), JState.model(fullModel));
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(), JModelBasic.model(modelBottom.toString())));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(JState.state(v), sharedSlabId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedSlabId);
	}

	// =========================================================
	// Helpers — SHAPES (BLOCK-based)
	// =========================================================

	private static void buildStairsFamilyForBlock(Identifier sharedStairsId, Map<MaterialKind, TexPicker> pickers) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			var forms = FormsRuntime.activeForms(Minecraft.getInstance().level, def);
			if (!forms.contains(Form.STAIRS) || !forms.contains(Form.BLOCK)) continue;

			var tex = pickBlockTex(def, idx, pickers);
			var model = RAAMaterials.id("block/material_block/" + def.nameInformation().id().getPath() + "_stairs");
			var modelIn = RAAMaterials.id("block/material_block/" + def.nameInformation().id().getPath() + "_stairs_inner");
			var modelOut = RAAMaterials.id("block/material_block/" + def.nameInformation().id().getPath() + "_stairs_outer");

			var texs = JModel.textures().var("bottom", blockTexture(tex)).var("top", blockTexture(tex)).var("side", blockTexture(tex));
			rp.addModel(JModel.model("minecraft:block/stairs").textures(texs), model);
			rp.addModel(JModel.model("minecraft:block/inner_stairs").textures(texs), modelIn);
			rp.addModel(JModel.model("minecraft:block/outer_stairs").textures(texs), modelOut);

			BlockstateTemplates.addStairs(v, Map.of("mat", idx), JState.model(model), JState.model(modelIn), JState.model(modelOut));
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(), JModelBasic.model(model.toString())));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(JState.state(v), sharedStairsId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedStairsId);
	}

	private static void buildWallFamilyForBlock(Identifier sharedWallId, Map<MaterialKind, TexPicker> pickers) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var state = JState.state();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			var forms = FormsRuntime.activeForms(Minecraft.getInstance().level, def);
			if (!forms.contains(Form.WALL) || !forms.contains(Form.BLOCK)) continue;

			var tex = pickBlockTex(def, idx, pickers);
			var post = RAAMaterials.id("block/material_block/" + def.nameInformation().id().getPath() + "_wall_post");
			var side = RAAMaterials.id("block/material_block/" + def.nameInformation().id().getPath() + "_wall_side");
			var sideT = RAAMaterials.id("block/material_block/" + def.nameInformation().id().getPath() + "_wall_side_tall");

			rp.addModel(JModel.model("minecraft:block/template_wall_post")
					.textures(JModel.textures().var("wall", blockTexture(tex))), post);
			rp.addModel(JModel.model("minecraft:block/template_wall_side")
					.textures(JModel.textures().var("wall", blockTexture(tex))), side);
			rp.addModel(JModel.model("minecraft:block/template_wall_side_tall")
					.textures(JModel.textures().var("wall", blockTexture(tex))), sideT);

			addWallParts(state, Map.of("mat", idx), post, side, sideT);
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(), JModelBasic.model(side.toString())));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(state, sharedWallId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedWallId);
	}

	private static void buildSlabFamilyVariant(Identifier sharedSlabId, String perModelPrefixVariant,
											   TexPicker variantTex, Identifier fullBlockModelsFolder) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.SLAB)) continue;

			var tex = variantTex.pick(idx);
			var modelBottom = RAAMaterials.id(perModelPrefixVariant + def.nameInformation().id().getPath() + "_slab");
			var modelTop = RAAMaterials.id(perModelPrefixVariant + def.nameInformation().id().getPath() + "_slab_top");

			rp.addModel(JModel.model("minecraft:block/slab")
					.textures(JModel.textures()
							.var("bottom", blockTexture(tex))
							.var("top", blockTexture(tex))
							.var("side", blockTexture(tex))), modelBottom);
			rp.addModel(JModel.model("minecraft:block/slab_top")
					.textures(JModel.textures()
							.var("bottom", blockTexture(tex))
							.var("top", blockTexture(tex))
							.var("side", blockTexture(tex))), modelTop);

			v.put(Map.of("mat", idx, "type", "bottom"), JState.model(modelBottom));
			v.put(Map.of("mat", idx, "type", "top"), JState.model(modelTop));
			var fullModel = RAAMaterials.id(fullBlockModelsFolder.getPath() + def.nameInformation().id().getPath());
			v.put(Map.of("mat", idx, "type", "double"), JState.model(fullModel));

			select.addCase(JSelectCase.of(def.nameInformation().id().toString(), JModelBasic.model(modelBottom.toString())));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(JState.state(v), sharedSlabId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedSlabId);
	}

	private static void buildStairsFamilyVariant(Identifier sharedStairsId, String perModelPrefixVariant,
												 TexPicker variantTex) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.STAIRS)) continue;

			var tex = variantTex.pick(idx);
			var model = RAAMaterials.id(perModelPrefixVariant + def.nameInformation().id().getPath() + "_stairs");
			var modelIn = RAAMaterials.id(perModelPrefixVariant + def.nameInformation().id().getPath() + "_stairs_inner");
			var modelOut = RAAMaterials.id(perModelPrefixVariant + def.nameInformation().id().getPath() + "_stairs_outer");

			var texs = JModel.textures().var("bottom", blockTexture(tex)).var("top", blockTexture(tex)).var("side", blockTexture(tex));
			rp.addModel(JModel.model("minecraft:block/stairs").textures(texs), model);
			rp.addModel(JModel.model("minecraft:block/inner_stairs").textures(texs), modelIn);
			rp.addModel(JModel.model("minecraft:block/outer_stairs").textures(texs), modelOut);

			for (var facing : new String[]{"north", "south", "west", "east"}) {
				for (var half : new String[]{"top", "bottom"}) {
					for (var shape : new String[]{"straight", "inner_left", "inner_right", "outer_left", "outer_right"}) {
						var ref = switch (shape) {
							case "inner_left", "inner_right" -> JState.model(modelIn);
							case "outer_left", "outer_right" -> JState.model(modelOut);
							default -> JState.model(model);
						};
						v.put(Map.of("mat", idx, "facing", facing, "half", half, "shape", shape), ref);
					}
				}
			}
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(), JModelBasic.model(model.toString())));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(JState.state(v), sharedStairsId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedStairsId);
	}

	// =========================================================
	// Helpers — SHAPES (Variant-based)
	// =========================================================

	private static void buildWallFamilyVariant(Identifier sharedWallId, String perModelPrefixVariant,
											   TexPicker variantTex) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var state = JState.state();
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.WALL)) continue;

			var tex = variantTex.pick(idx);
			var post = RAAMaterials.id(perModelPrefixVariant + def.nameInformation().id().getPath() + "_wall_post");
			var side = RAAMaterials.id(perModelPrefixVariant + def.nameInformation().id().getPath() + "_wall_side");
			var sideT = RAAMaterials.id(perModelPrefixVariant + def.nameInformation().id().getPath() + "_wall_side_tall");
			var inventory = RAAMaterials.id(perModelPrefixVariant + def.nameInformation().id().getPath() + "_wall_inventory");

			rp.addModel(JModel.model("minecraft:block/template_wall_post")
					.textures(JModel.textures().var("wall", blockTexture(tex))), post);
			rp.addModel(JModel.model("minecraft:block/template_wall_side")
					.textures(JModel.textures().var("wall", blockTexture(tex))), side);
			rp.addModel(JModel.model("minecraft:block/template_wall_side_tall")
					.textures(JModel.textures().var("wall", blockTexture(tex))), sideT);
			rp.addModel(JModel.model("minecraft:block/wall_inventory")
					.textures(JModel.textures().var("wall", blockTexture(tex))), inventory);

			addWallParts(state, Map.of("mat", idx), post, side, sideT);

			select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(inventory, def)));
			cases++;
		}

		if (cases == 0) return;
		rp.addBlockState(state, sharedWallId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedWallId);
	}

	private static void buildItemFamily(
			List<Form> forms, Identifier logicalItemId, String perModelPrefix, TexPicker layer0
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();

		if (mats.isEmpty()) {
			rp.addItemModelInfo(
					new JItemInfo().model(JModelBasic.of(logicalItemId.withPrefix("item/").toString())),
					logicalItemId
			);
			return;
		}

		var select = JItemModel.select().property(MAT_COMP);
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), forms)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());
			addGeneratedItemModel(rp, perModelId, layer0.pick(idx));
			select.addCase(JSelectCase.of(def.nameInformation().id().toString(), JModelBasic.model(perModelId.toString())
					.tint(new JTintConstant(opaqueColor(def.primaryColor())))));
			cases++;
		}

		if (cases == 0) {
			rp.addItemModelInfo(
					new JItemInfo().model(JModelBasic.of(logicalItemId.withPrefix("item/").toString())),
					logicalItemId
			);
			return;
		}

		select.fallback(JModelBasic.of(logicalItemId.withPrefix("item/").toString()));
		rp.addItemModelInfo(new JItemInfo().model(select), logicalItemId);
	}

	private static void buildLayeredItemFamily(
			Form form,
			Identifier logicalItemId,
			String perModelPrefix,
			TexPicker layer0,
			TexPicker layer1
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();

		if (mats.isEmpty()) {
			rp.addItemModelInfo(
					new JItemInfo().model(JModelBasic.of(logicalItemId.withPrefix("item/").toString())),
					logicalItemId
			);
			return;
		}

		var select = JItemModel.select().property(MAT_COMP);
		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), form)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());
			var model = JModel.model("item/handheld")
					.textures(JModel.textures()
							.var("layer0", layer0.pick(idx).toString().replace(".png", ""))
							.var("layer1", layer1.pick(idx).toString().replace(".png", ""))
					);
			rp.addModel(model, perModelId);

			select.addCase(
					JSelectCase.of(
							def.nameInformation().id().toString(),
							JModelBasic.model(perModelId.toString()).tint(new JTintConstant(opaqueColor(def.primaryColor())))
					)
			);
			cases++;
		}

		if (cases == 0) {
			rp.addItemModelInfo(
					new JItemInfo().model(JModelBasic.of(logicalItemId.withPrefix("item/").toString())),
					logicalItemId
			);
			return;
		}

		select.fallback(JModelBasic.of("minecraft:item/stone_axe"));
		rp.addItemModelInfo(new JItemInfo().model(select), logicalItemId);
	}

	// =========================================================
	// Helpers — ITEMS
	// =========================================================

	private static void addGeneratedItemModel(RuntimeResourcePack rp, Identifier modelId, Identifier layer0) {
		rp.addModel(JModel.model("minecraft:item/generated")
						.textures(JModel.textures().var("layer0", layer0.withPrefix("item/").toString().replace(".png", ""))),
				modelId
		);
	}

	// =========================================================
	// Helpers — RECIPES (WIP: uncomment buildRecipes() when recipe system is ready)
	// =========================================================

	private static void addRecipe(Identifier id, JRecipe recipe) {
		var file = Identifier.fromNamespaceAndPath(id.getNamespace(), "recipes/" + id.getPath() + ".json");
		RRPGen.PACK.addRecipe(file, recipe);
	}

	private static JIngredient ingredientWithMaterial(Identifier sharedItemId, Identifier mat) {
		return JIngredient.ingredient().fabricCustom(
				RAAMaterials.id("component"),
				obj -> {
					obj.addProperty("item", sharedItemId.toString());
					var comps = new com.google.gson.JsonObject();
					var matObj = new com.google.gson.JsonObject();
					matObj.addProperty("id", mat.toString());
					comps.add("raa_materials:material", matObj);
					obj.add("components", comps);
					obj.addProperty("count", 3);
				}
		);
	}

	private static JResult resultWithMaterial(Identifier itemId, Identifier mat) {
		var res = JResult.result(itemId);
		res.components(builder -> builder.set(YComponents.MATERIAL, mat));
		return res;
	}

	private static JStackedResult resultWithMaterial(Identifier itemId, Identifier mat, int count) {
		var res = JResult.stackedResult(itemId, count);
		res.components(builder -> builder.set(YComponents.MATERIAL, mat));
		return res;
	}

	private static JShapedRecipe shapedVanilla(Identifier outId, Identifier mat, int count,
											   Identifier inSharedItem, String... pattern) {
		return JRecipe.shaped(
				JPattern.pattern(pattern),
				JKeys.keys().key("#", ingredientWithMaterial(inSharedItem, mat)),
				resultWithMaterial(outId, mat, count)
		);
	}

	private static JShapelessRecipe shapelessVanilla(Identifier outId, Identifier mat, int count,
													 Identifier inSharedItem, int inCount) {
		return JRecipe.shapeless(
				JIngredients.ingredients().add(ingredientWithMaterial(inSharedItem, mat)),
				resultWithMaterial(outId, mat, count)
		);
	}

	private static JStonecuttingRecipe stonecuttingVanilla(Identifier outId, Identifier mat, int count,
														   Identifier inSharedItem) {
		return JRecipe.stonecutting(ingredientWithMaterial(inSharedItem, mat), resultWithMaterial(outId, mat, count));
	}

	private static JCookingRecipe furnaceVanilla(String type, Identifier outId, Identifier mat,
												 float xp, int time, Identifier inSharedItem) {
		JCookingRecipe r;
		if (type.equals("blasting")) {
			r = JRecipe.blasting(ingredientWithMaterial(inSharedItem, mat), resultWithMaterial(outId, mat, 1))
					.experience(xp).cookingTime(time);
		} else {
			r = JRecipe.smelting(ingredientWithMaterial(inSharedItem, mat), resultWithMaterial(outId, mat, 1))
					.experience(xp).cookingTime(time);
		}
		return r;
	}

	/*
	private static void buildRecipes() {
		var mats = ClientMaterialCache.all();

		for (MaterialDef def : mats) {
			var matId = def.nameInformation().id();

			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.INGOT)
					&& FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BLOCK)) {
				addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "compression/block_from_ingots_" + matId.getPath()),
						shapedVanilla(BLOCK_SHARED_ID, matId, 1, INGOT_SHARED_ID, "###", "###", "###"));
				addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "decompression/ingots_from_block_" + matId.getPath()),
						shapelessVanilla(INGOT_SHARED_ID, matId, 9, BLOCK_SHARED_ID, 1));
			}
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.NUGGET)
					&& FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.INGOT)) {
				addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "compression/ingot_from_nuggets_" + matId.getPath()),
						shapedVanilla(INGOT_SHARED_ID, matId, 1, NUGGET_SHARED_ID, "###", "###", "###"));
				addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "decompression/nuggets_from_ingot_" + matId.getPath()),
						shapelessVanilla(NUGGET_SHARED_ID, matId, 9, INGOT_SHARED_ID, 1));
			}
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.SHEET)
					&& FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.INGOT)) {
				addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "processing/plate_from_ingot_" + matId.getPath()),
						shapelessVanilla(PLATE_SHARED_ID, matId, 1, INGOT_SHARED_ID, 1));
			}
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BLOCK)) {
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.SLAB)) {
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "building/slab_" + matId.getPath()),
							shapedVanilla(SLAB_SHARED_ID, matId, 6, BLOCK_SHARED_ID, "###"));
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "stonecutting/slab_" + matId.getPath()),
							stonecuttingVanilla(SLAB_SHARED_ID, matId, 2, BLOCK_SHARED_ID));
				}
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.STAIRS)) {
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "building/stairs_" + matId.getPath()),
							shapedVanilla(STAIRS_SHARED_ID, matId, 4, BLOCK_SHARED_ID, "#  ", "## ", "###"));
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "stonecutting/stairs_" + matId.getPath()),
							stonecuttingVanilla(STAIRS_SHARED_ID, matId, 1, BLOCK_SHARED_ID));
				}
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.WALL)) {
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "building/wall_" + matId.getPath()),
							shapedVanilla(WALL_SHARED_ID, matId, 6, BLOCK_SHARED_ID, "###", "###"));
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "stonecutting/wall_" + matId.getPath()),
							stonecuttingVanilla(WALL_SHARED_ID, matId, 1, BLOCK_SHARED_ID));
				}
			}
			var forms = FormsRuntime.activeForms(Minecraft.getInstance().level, def);
			boolean metalish = def.kind() == MaterialKind.METAL || def.kind() == MaterialKind.ALLOY;
			if (metalish && forms.contains(Form.INGOT)) {
				if (forms.contains(Form.ORE)) {
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "smelting/ingot_from_ore_" + matId.getPath()),
							furnaceVanilla("smelting", INGOT_SHARED_ID, matId, 0.7f, 200, ORE_SHARED_ID));
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "blasting/ingot_from_ore_" + matId.getPath()),
							furnaceVanilla("blasting", INGOT_SHARED_ID, matId, 0.7f, 100, ORE_SHARED_ID));
				}
				if (forms.contains(Form.RAW)) {
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "smelting/ingot_from_raw_" + matId.getPath()),
							furnaceVanilla("smelting", INGOT_SHARED_ID, matId, 0.7f, 200, RAW_SHARED_ID));
					addRecipe(Identifier.fromNamespaceAndPath(matId.getNamespace(), "blasting/ingot_from_raw_" + matId.getPath()),
							furnaceVanilla("blasting", INGOT_SHARED_ID, matId, 0.7f, 100, RAW_SHARED_ID));
				}
			}
		}
	}
	*/

	@FunctionalInterface
	private interface TexPicker {
		Identifier pick(int idx);
	}

	private record JBlockModelEntry(int idx, JBlockModel model) {
	}
}
