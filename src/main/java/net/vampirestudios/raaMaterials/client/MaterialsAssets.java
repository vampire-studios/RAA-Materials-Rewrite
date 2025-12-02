package net.vampirestudios.raaMaterials.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.blockstate.BlockstateTemplates;
import net.devtech.arrp.json.blockstate.JBlockModel;
import net.devtech.arrp.json.blockstate.JState;
import net.devtech.arrp.json.iteminfo.JItemInfo;
import net.devtech.arrp.json.iteminfo.model.JItemModel;
import net.devtech.arrp.json.iteminfo.model.JModelBasic;
import net.devtech.arrp.json.iteminfo.model.JSelectCase;
import net.devtech.arrp.json.iteminfo.property.JPropertyComponent;
import net.devtech.arrp.json.iteminfo.tint.JTint;
import net.devtech.arrp.json.iteminfo.tint.JTintConstant;
import net.devtech.arrp.json.iteminfo.tint.JTintDye;
import net.devtech.arrp.json.models.JModel;
import net.devtech.arrp.json.recipe.*;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
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

	private static final java.util.Map<ResourceLocation, MaterialAssetsDef> ASSETS = new java.util.HashMap<>();
	// ---- Component + shared ids ----
	private static final JPropertyComponent MAT_COMP = JPropertyComponent.component("raa_materials:material");
	private static final ResourceLocation ORE_SHARED_ID = RAAMaterials.id("material_ore");
	private static final ResourceLocation BLOCK_SHARED_ID = RAAMaterials.id("material_block");
	private static final ResourceLocation RAW_BLOCK_SHARED_ID = RAAMaterials.id("material_raw_block");
	private static final ResourceLocation SHINGLES_SHARED_ID = RAAMaterials.id("material_shingles");
	private static final ResourceLocation PLATE_BLOCK_SHARED_ID = RAAMaterials.id("material_plate_block");
	// New shared block IDs (cube-all)
	private static final ResourceLocation SANDSTONE_SHARED_ID = RAAMaterials.id("material_sandstone");
	private static final ResourceLocation CUT_SANDSTONE_SHARED_ID = RAAMaterials.id("material_cut_sandstone");
	private static final ResourceLocation SMOOTH_SANDSTONE_SHARED_ID = RAAMaterials.id("material_smooth_sandstone");
	private static final ResourceLocation CHISELED_SHARED_ID = RAAMaterials.id("material_chiseled");
	private static final ResourceLocation BRICKS_SHARED_ID = RAAMaterials.id("material_bricks");
	private static final ResourceLocation POLISHED_SHARED_ID = RAAMaterials.id("material_polished");
	private static final ResourceLocation DRIED_SHARED_ID = RAAMaterials.id("material_dried");
	private static final ResourceLocation CERAMIC_SHARED_ID = RAAMaterials.id("material_ceramic");
	// Base shapes (BLOCK)
	private static final ResourceLocation SLAB_SHARED_ID = RAAMaterials.id("material_slab");
	private static final ResourceLocation STAIRS_SHARED_ID = RAAMaterials.id("material_stairs");
	private static final ResourceLocation WALL_SHARED_ID = RAAMaterials.id("material_wall");
	// Variant shapes
	private static final ResourceLocation SANDSTONE_SLAB_SHARED_ID = RAAMaterials.id("material_sandstone_slab");
	private static final ResourceLocation SANDSTONE_STAIRS_SHARED_ID = RAAMaterials.id("material_sandstone_stairs");
	private static final ResourceLocation SANDSTONE_WALL_SHARED_ID = RAAMaterials.id("material_sandstone_wall");
	private static final ResourceLocation BRICK_SLAB_SHARED_ID = RAAMaterials.id("material_brick_slab");
	private static final ResourceLocation BRICK_STAIRS_SHARED_ID = RAAMaterials.id("material_brick_stairs");
	private static final ResourceLocation BRICK_WALL_SHARED_ID = RAAMaterials.id("material_brick_wall");
	private static final ResourceLocation POLISHED_SLAB_SHARED_ID = RAAMaterials.id("material_polished_slab");
	private static final ResourceLocation POLISHED_STAIRS_SHARED_ID = RAAMaterials.id("material_polished_stairs");
	private static final ResourceLocation POLISHED_WALL_SHARED_ID = RAAMaterials.id("material_polished_wall");
	// ---- Shared item IDs ----
	private static final ResourceLocation INGOT_SHARED_ID = RAAMaterials.id("material_ingot");
	private static final ResourceLocation RAW_SHARED_ID = RAAMaterials.id("material_raw");
	private static final ResourceLocation NUGGET_SHARED_ID = RAAMaterials.id("material_nugget");
	private static final ResourceLocation DUST_SHARED_ID = RAAMaterials.id("material_dust");
	private static final ResourceLocation PLATE_SHARED_ID = RAAMaterials.id("material_sheet");
	private static final ResourceLocation CRYSTAL_SHARED_ID = RAAMaterials.id("material_crystal");
	private static final ResourceLocation SHARD_SHARED_ID = RAAMaterials.id("material_shard");
	private static final ResourceLocation GEAR_SHARED_ID = RAAMaterials.id("material_gear");
	private static final ResourceLocation CLUSTER_SHARED_ID = RAAMaterials.id("material_cluster");
	private static final ResourceLocation GEM_SHARED_ID = RAAMaterials.id("material_gem");
	private static final ResourceLocation BALL_SHARED_ID = RAAMaterials.id("material_ball");
	private static final ResourceLocation ROD_SHARED_ID = RAAMaterials.id("material_rod");
	private static final ResourceLocation SHOVEL_SHARED_ID = RAAMaterials.id("material_shovel");
	private static final ResourceLocation HOE_SHARED_ID = RAAMaterials.id("material_hoe");
	private static final ResourceLocation SWORD_SHARED_ID = RAAMaterials.id("material_sword");
	private static final ResourceLocation PICKAXE_SHARED_ID = RAAMaterials.id("material_pickaxe");
	private static final ResourceLocation AXE_SHARED_ID = RAAMaterials.id("material_axe");
	private static boolean DIRTY = false;
	private static boolean reloading = false;

	private static ResourceLocation texture(AssetsTheme.Slot slot, MaterialDef def) {
		return FormTextureResolver.of(AssetsTheme.defaultTheme()).sheet(slot, def).orElseThrow();
	}

	private static MaterialAssetsDef texture(MaterialDef def) {
		return MaterialAssets.generate(def);
	}


	/** Export all assets as individual JSON files under <gameDir>/dev_export. */
	// 1) Simple one-liner you can call from buildAll()
	public static void saveDevExport(Path gameDir) throws IOException {
		saveDevExport(gameDir, ClientMaterialCache.all());
	}

	// 2) Overload that works over any list of materials (no Pools / no Map)
	public static void saveDevExport(Path gameDir, List<MaterialDef> materials) throws IOException {
		Path root = gameDir.resolve("dev_export");
		Files.createDirectories(root);

		for (MaterialDef m : materials) {
			// Generate using the new system
			MaterialAssetsDef def = MaterialAssets.generate(m);

			// dev-only path; keep it namespaced per material id
			Path out = root
					.resolve("data")
					.resolve(m.id().getNamespace())
					.resolve("raa_materials")   // keep a stable bucket
					.resolve("assets")
					.resolve(m.id().getPath() + ".json");

			Files.createDirectories(out.getParent());
			JsonElement json = encodeOrThrow(MaterialAssetsDef.CODEC, def);
			try (Writer w = Files.newBufferedWriter(out)) {
				GSON.toJson(json, w);
			}
		}
	}

	// ---------- helpers ----------

	private static <T> JsonElement encodeOrThrow(Codec<T> codec, T value) {
		DataResult<JsonElement> dr = codec.encodeStart(JsonOps.INSTANCE, value);
		return dr.getOrThrow();
	}

	public static void init() {
		// Rebuild once per tick if marked dirty
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!DIRTY || reloading || client.level == null) return;
			DIRTY = false;
			buildAll();           // write to RRPGen.PACK
			// trigger one reload on main thread
			reloading = true;
			client.execute(() -> {
				client.reloadResourcePacks();
				reloading = false;
			});
			registerBlockColorProviders();
		});

		// Also rebuild whenever resources reload (F3+T etc.)
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				new SimpleSynchronousResourceReloadListener() {
					@Override
					public ResourceLocation getFabricId() {
						return RAAMaterials.id("materials_assets_rebuilder");
					}

					@Override
					public void onResourceManagerReload(ResourceManager rm) {
						// If cache already loaded, rebuild so JSON matches current cache
						if (!ClientMaterialCache.all().isEmpty()) buildAll();
					}
				}
		);
	}

	private static void registerBlockColorProviders() {
		var materials = ClientMaterialCache.all();

		// Register color provider for each tinted block type
		registerColorProviderForSharedBlock(ORE_SHARED_ID, materials);
		registerColorProviderForSharedBlock(BLOCK_SHARED_ID, materials);
		registerColorProviderForSharedBlock(RAW_BLOCK_SHARED_ID, materials);
		registerColorProviderForSharedBlock(SHINGLES_SHARED_ID, materials);
		registerColorProviderForSharedBlock(PLATE_BLOCK_SHARED_ID, materials);
		// Add other tinted blocks as needed
	}

	private static void registerColorProviderForSharedBlock(ResourceLocation sharedBlockId, List<MaterialDef> materials) {
		var block = BuiltInRegistries.BLOCK.getValue(sharedBlockId);
		if (block == null) {
			System.err.println("[RAA] Block not found for id: " + sharedBlockId);
			return;
		}

		ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) -> {
			if (tintIndex == 0 && world != null && pos != null) {
				// Get the material INDEX from the block state (it's an int, not ResourceLocation)
				int matIndex = state.getValue(ParametricBlock.MAT); // This returns an Integer
				if (matIndex >= 0 && matIndex < materials.size()) {
					// Use the index to get the material definition and return its primary color
					var material = materials.get(matIndex);
					return material.primaryColor();
				}
			}
			return -1; // Default to white/no tint
		}, block);
	}

	public static void markDirty() {
		DIRTY = true;
	}

	// Core generator: single place that writes blockstates/models/selectors
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

		try {
			saveDevExport(FabricLoader.getInstance().getGameDir());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// ---- BLOCK FAMILIES (cube-all) ----
		buildBlockFamily(List.of(Form.ORE), ORE_SHARED_ID, "block/material_ore/",
				(idx) -> texture(def.get(idx)).textures1().oreVein().orElseThrow());

		Map<MaterialKind, TexPicker> blockTextures = Map.of(
				MaterialKind.METAL, (idx) -> RAAMaterials.id(STR."storage_blocks/metals/metal_\{oneIndexed(idx, 23)}"),
				MaterialKind.CRYSTAL, (idx) -> RAAMaterials.id(STR."crystal/crystal_block_\{oneIndexed(idx, 5)}"),
				MaterialKind.GEM, (idx) -> RAAMaterials.id(STR."storage_blocks/gems/gem_\{oneIndexed(idx, 16)}"),
				MaterialKind.STONE, (idx) -> RAAMaterials.id(STR."stone/a/stone_\{oneIndexed(idx, 23)}"),
				MaterialKind.SAND, (idx) -> RAAMaterials.id(STR."storage_blocks/sand_\{oneIndexed(idx, 3)}"),
				MaterialKind.MUD, (idx) -> ResourceLocation.withDefaultNamespace("mud"),
				MaterialKind.GRAVEL, (idx) -> ResourceLocation.withDefaultNamespace("gravel"),
				MaterialKind.CLAY, (idx) -> ResourceLocation.withDefaultNamespace("clay")
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

		// Building families
//		buildBlockFamily(List.of(Form.SANDSTONE), SANDSTONE_SHARED_ID, "block/material_sandstone/",
//				(idx) -> RAAMaterials.id("building/sandstone/sandstone_" + oneIndexed(idx, 10)));
//		buildBlockFamily(List.of(Form.CUT), CUT_SANDSTONE_SHARED_ID, "block/material_cut_sandstone/",
//				(idx) -> RAAMaterials.id("building/sandstone/cut_sandstone_" + oneIndexed(idx, 10)));
//		buildBlockFamily(List.of(Form.SMOOTH), SMOOTH_SANDSTONE_SHARED_ID, "block/material_smooth_sandstone/",
//				(idx) -> RAAMaterials.id("building/sandstone/smooth_sandstone_" + oneIndexed(idx, 10)));
//		buildBlockFamily(List.of(Form.CHISELED), CHISELED_SHARED_ID, "block/material_chiseled/",
//				(idx) -> RAAMaterials.id("building/chiseled/chiseled_" + oneIndexed(idx, 12)));
//		buildBlockFamily(List.of(Form.BRICKS), BRICKS_SHARED_ID, "block/material_bricks/",
//				(idx) -> RAAMaterials.id("building/bricks/bricks_" + oneIndexed(idx, 12)));
//		buildBlockFamily(List.of(Form.POLISHED), POLISHED_SHARED_ID, "block/material_polished/",
//				(idx) -> RAAMaterials.id("building/polished/polished_" + oneIndexed(idx, 12)));
//		buildBlockFamily(List.of(Form.DRIED), DRIED_SHARED_ID, "block/material_dried/",
//				(idx) -> RAAMaterials.id("building/dried/dried_" + oneIndexed(idx, 8)));
//		buildBlockFamily(List.of(Form.CERAMIC), CERAMIC_SHARED_ID, "block/material_ceramic/",
//				(idx) -> RAAMaterials.id("building/ceramic/ceramic_" + oneIndexed(idx, 8)));

		// ---- BASE SHAPES (BLOCK textures) ----
		buildSlabFamilyForBlock(SLAB_SHARED_ID, "block/material_block/", blockTextures);
		buildStairsFamilyForBlock(STAIRS_SHARED_ID, "block/material_block/", blockTextures);
		buildWallFamilyForBlock(WALL_SHARED_ID, "block/material_block/", blockTextures);

		// ---- VARIANT SHAPES (per-variant textures) ----
		// Sandstone shapes
//		buildSlabFamilyVariant(SANDSTONE_SLAB_SHARED_ID,   "block/material_sandstone/",
//				(idx) -> RAAMaterials.id(STR."building/sandstone/sandstone_\{oneIndexed(idx, 10)}"),
//				RAAMaterials.id("block/material_sandstone/"));
//		buildStairsFamilyVariant(SANDSTONE_STAIRS_SHARED_ID, "block/material_sandstone/",
//				(idx) -> RAAMaterials.id(STR."building/sandstone/sandstone_\{oneIndexed(idx, 10)}"));
//		buildWallFamilyVariant(SANDSTONE_WALL_SHARED_ID,   "block/material_sandstone/",
//				(idx) -> RAAMaterials.id(STR."building/sandstone/sandstone_\{oneIndexed(idx, 10)}"));
//
//		// Brick shapes
//		buildSlabFamilyVariant(BRICK_SLAB_SHARED_ID,   "block/material_bricks/",
//				(idx) -> RAAMaterials.id("building/bricks/bricks_" + oneIndexed(idx, 12)),
//				RAAMaterials.id("block/material_bricks/"));
//		buildStairsFamilyVariant(BRICK_STAIRS_SHARED_ID, "block/material_bricks/",
//				(idx) -> RAAMaterials.id("building/bricks/bricks_" + oneIndexed(idx, 12)));
//		buildWallFamilyVariant(BRICK_WALL_SHARED_ID,   "block/material_bricks/",
//				(idx) -> RAAMaterials.id("building/bricks/bricks_" + oneIndexed(idx, 12)));
//
//		// Polished shapes
//		buildSlabFamilyVariant(POLISHED_SLAB_SHARED_ID,   "block/material_polished/",
//				(idx) -> RAAMaterials.id("building/polished/polished_" + oneIndexed(idx, 12)),
//				RAAMaterials.id("block/material_polished/"));
//		buildStairsFamilyVariant(POLISHED_STAIRS_SHARED_ID, "block/material_polished/",
//				(idx) -> RAAMaterials.id("building/polished/polished_" + oneIndexed(idx, 12)));
//		buildWallFamilyVariant(POLISHED_WALL_SHARED_ID,   "block/material_polished/",
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
//		buildItemFamily(List.of(Form.CRYSTAL),CRYSTAL_SHARED_ID,"item/material_crystal/",
//				(idx) -> assets(def.get(idx)).textures().flatMap(MaterialDef.TextureDef::).orElseThrow());
		buildItemFamily(List.of(Form.SHARD), SHARD_SHARED_ID, "item/material_shard/",
				(idx) -> texture(def.get(idx)).textures2().shard().orElseThrow());
		buildItemFamily(List.of(Form.GEAR), GEAR_SHARED_ID, "item/material_gear/",
				(idx) -> texture(def.get(idx)).textures2().gear().orElseThrow());
//		buildItemFamily(List.of(Form.CLUSTER),CLUSTER_SHARED_ID,"item/material_cluster/",
//				(idx) -> assets(def.get(idx)).textures().flatMap(MaterialDef.TextureDef::cluster).orElseThrow());
		buildItemFamily(List.of(Form.GEM), GEM_SHARED_ID, "item/material_gem/",
				(idx) -> texture(def.get(idx)).textures2().gem().orElseThrow());
//		buildItemFamily(List.of(Form.BALL),   BALL_SHARED_ID,   "item/material_ball/",
//				(idx) -> assets(def.get(idx)).textures2().ball().orElseThrow());
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
		// 1..max, wraps deterministically
		return (Math.floorMod(idx, max) + 1);
	}

	private static void buildBlockFamily(
			List<Form> forms, ResourceLocation sharedBlockId, String perModelPrefix, TexPicker picker
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();

		List<JBlockModelEntry> variants = new ArrayList<>();
		var select = JItemModel.select().property(MAT_COMP);

		int cases = 0;

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), forms)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.id().getPath());
			var tex = picker.pick(idx);
			addCubeAllTintedBlockModel(rp, perModelId, tex);
			variants.add(new JBlockModelEntry(idx, JState.model(perModelId)));
			select.addCase(JSelectCase.of(def.id().toString(), JModelBasic.model(perModelId.toString())));
			cases++;
		}

		if (cases == 0) {
			// Nothing to select on → emit a simple model
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
			List<Form> forms, ResourceLocation sharedBlockId, String perModelPrefix,
			Map<MaterialKind, TexPicker> pickers
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();

		List<JBlockModelEntry> variants = new ArrayList<>();
		var select = JItemModel.select().property(MAT_COMP);
		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), forms)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.id().getPath());
			var tex = switch (def.kind()) {
				case METAL, ALLOY, OTHER -> pickers.get(MaterialKind.METAL).pick(idx);
				case GEM -> pickers.get(MaterialKind.GEM).pick(idx);
				case CRYSTAL -> pickers.get(MaterialKind.CRYSTAL).pick(idx);
				case STONE -> pickers.get(MaterialKind.STONE).pick(idx);
				// If you have SAND or other kinds that don't get a block texture prefilled:
				case SAND -> pickers.get(MaterialKind.SAND).pick(idx);
				case MUD -> pickers.get(MaterialKind.MUD).pick(idx);
				case GRAVEL -> pickers.get(MaterialKind.GRAVEL).pick(idx);
				case CLAY -> pickers.get(MaterialKind.CLAY).pick(idx);
				default -> ResourceLocation.withDefaultNamespace("stone");
//				default -> {
//					throw new IllegalStateException("No block texture for " + def.id() + " (kind=" + def.kind() + ")");
//				}
			};
			addCubeAllTintedBlockModel(rp, perModelId, tex);
			variants.add(new JBlockModelEntry(idx, JState.model(perModelId)));
			select.addCase(JSelectCase.of(def.id().toString(), JModelBasic.model(perModelId.toString()).tint(JTint.constant(def.primaryColor()))));
		}

		var v = JState.variant();
		for (var e : variants) v.put("mat", e.idx, e.model);
		rp.addBlockState(JState.state(v), sharedBlockId);

		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
	}

	private static void addCubeAllBlockModel(RuntimeResourcePack rp, ResourceLocation modelId, ResourceLocation texture) {
		ARRPGenerationHelper.generateAllBlockModel(rp, modelId, texture);
	}

	private static void addCubeAllTintedBlockModel(RuntimeResourcePack rp, ResourceLocation modelId, ResourceLocation texture) {
		ARRPGenerationHelper.generateAllTintedBlockModel(rp, modelId, texture);
	}

	private static void addOreModel(RuntimeResourcePack rp, ResourceLocation modelId, TextureDef1 def, ResourceLocation host) {
		rp.addModel(JModel.model("minecraft:block/ore").textures(JModel.textures()
				.var("base", host.toString())
				.var("overlay", def.oreVein().toString())
		), modelId);
	}

	private static boolean hasAny(List<Form> actual, List<Form> wanted) {
		for (var f : wanted) if (actual.contains(f)) return false;
		return true;
	}

	private static boolean hasAny(List<Form> actual, Form wanted) {
		return !actual.contains(wanted);
	}

	private static void buildSlabFamilyForBlock(ResourceLocation sharedSlabId, String perModelPrefixBlockTex, Map<MaterialKind, TexPicker> pickers) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.SLAB) || !FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BLOCK))
				continue;

			ResourceLocation baseTex = switch (def.kind()) {
				case METAL, ALLOY, OTHER -> pickers.get(MaterialKind.METAL).pick(idx);
				case GEM -> pickers.get(MaterialKind.GEM).pick(idx);
				case CRYSTAL -> pickers.get(MaterialKind.CRYSTAL).pick(idx);
				case STONE, CLAY, GRAVEL, SOIL, MUD, SALT, VOLCANIC -> pickers.get(MaterialKind.STONE).pick(idx);
				case SAND -> RAAMaterials.id(STR."building/sandstone/sandstone_\{oneIndexed(idx, 10)}");
				// If WOOD can have slabs, map it here, or skip:
				case WOOD -> ResourceLocation.withDefaultNamespace("block/oak_planks"); // Assumption: placeholder
			};
			var modelBottom = RAAMaterials.id("block/material_block/" + def.id().getPath() + "_slab");
			var modelTop = RAAMaterials.id("block/material_block/" + def.id().getPath() + "_slab_top");
			var fullModel = RAAMaterials.id("block/material_block/" + def.id().getPath());

			rp.addModel(JModel.model("minecraft:block/slab")
					.textures(JModel.textures()
							.var("bottom", baseTex.toString())
							.var("top", baseTex.toString())
							.var("side", baseTex.toString())), modelBottom);
			rp.addModel(JModel.model("minecraft:block/slab_top")
					.textures(JModel.textures()
							.var("bottom", baseTex.toString())
							.var("top", baseTex.toString())
							.var("side", baseTex.toString())), modelTop);

			BlockstateTemplates.addSlab(v, Map.of("mat", idx), JState.model(modelBottom), JState.model(modelTop), JState.model(fullModel));

			select.addCase(JSelectCase.of(def.id().toString(), JModelBasic.model(modelBottom.toString())));
		}

		rp.addBlockState(JState.state(v), sharedSlabId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedSlabId);
	}

	// =========================================================
	// Helpers — SHAPES (BLOCK-based)
	// =========================================================

	private static void buildStairsFamilyForBlock(ResourceLocation sharedStairsId, String perModelPrefixBlockTex, Map<MaterialKind, TexPicker> pickers) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.STAIRS) || !FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BLOCK))
				continue;

			ResourceLocation tex = switch (def.kind()) {
				case METAL, ALLOY, OTHER -> pickers.get(MaterialKind.METAL).pick(idx);
				case GEM -> pickers.get(MaterialKind.GEM).pick(idx);
				case CRYSTAL -> pickers.get(MaterialKind.CRYSTAL).pick(idx);
				case STONE, CLAY, GRAVEL, SOIL, MUD, SALT, VOLCANIC -> pickers.get(MaterialKind.STONE).pick(idx);
				case SAND -> RAAMaterials.id(STR."building/sandstone/sandstone_\{oneIndexed(idx, 10)}");
				// If WOOD can have slabs, map it here, or skip:
				case WOOD -> ResourceLocation.withDefaultNamespace("block/oak_planks"); // Assumption: placeholder
			};
			var model = RAAMaterials.id("block/material_block/" + def.id().getPath() + "_stairs");
			var modelIn = RAAMaterials.id("block/material_block/" + def.id().getPath() + "_stairs_inner");
			var modelOut = RAAMaterials.id("block/material_block/" + def.id().getPath() + "_stairs_outer");

			var texs = JModel.textures().var("bottom", tex.toString()).var("top", tex.toString()).var("side", tex.toString());
			rp.addModel(JModel.model("minecraft:block/stairs").textures(texs), model);
			rp.addModel(JModel.model("minecraft:block/inner_stairs").textures(texs), modelIn);
			rp.addModel(JModel.model("minecraft:block/outer_stairs").textures(texs), modelOut);

			BlockstateTemplates.addStairs(v, Map.of("mat", idx), JState.model(model), JState.model(modelIn), JState.model(modelOut));

			select.addCase(JSelectCase.of(def.id().toString(), JModelBasic.model(model.toString())));
		}

		rp.addBlockState(JState.state(v), sharedStairsId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedStairsId);
	}

	private static void buildWallFamilyForBlock(ResourceLocation sharedWallId, String perModelPrefixBlockTex, Map<MaterialKind, TexPicker> pickers) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var multipart = JState.multipart();

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.WALL) || !FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BLOCK))
				continue;

			ResourceLocation tex = switch (def.kind()) {
				case METAL, ALLOY, OTHER -> pickers.get(MaterialKind.METAL).pick(idx);
				case GEM -> pickers.get(MaterialKind.GEM).pick(idx);
				case CRYSTAL -> pickers.get(MaterialKind.CRYSTAL).pick(idx);
				case STONE, CLAY, GRAVEL, SOIL, MUD, SALT, VOLCANIC -> pickers.get(MaterialKind.STONE).pick(idx);
				case SAND -> RAAMaterials.id(STR."building/sandstone/sandstone_\{oneIndexed(idx, 10)}");
				// If WOOD can have slabs, map it here, or skip:
				case WOOD -> ResourceLocation.withDefaultNamespace("block/oak_planks"); // Assumption: placeholder
			};
			var post = RAAMaterials.id("block/material_block/" + def.id().getPath() + "_wall_post");
			var side = RAAMaterials.id("block/material_block/" + def.id().getPath() + "_wall_side");
			var sideT = RAAMaterials.id("block/material_block/" + def.id().getPath() + "_wall_side_tall");

			rp.addModel(JModel.model("minecraft:block/template_wall_post")
					.textures(JModel.textures().var("wall", tex.toString())), post);
			rp.addModel(JModel.model("minecraft:block/template_wall_side")
					.textures(JModel.textures().var("wall", tex.toString())), side);
			rp.addModel(JModel.model("minecraft:block/template_wall_side_tall")
					.textures(JModel.textures().var("wall", tex.toString())), sideT);

			BlockstateTemplates.addWall(multipart, Map.of("mat", idx), JState.model(post), JState.model(side), JState.model(sideT));

			select.addCase(JSelectCase.of(def.id().toString(), JModelBasic.model(side.toString())));
		}

		rp.addBlockState(JState.state(multipart), sharedWallId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedWallId);
	}

	private static void buildSlabFamilyVariant(ResourceLocation sharedSlabId, String perModelPrefixVariant,
											   TexPicker variantTex, ResourceLocation fullBlockModelsFolder) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.SLAB)) continue;

			var tex = variantTex.pick(idx);
			var modelBottom = RAAMaterials.id(perModelPrefixVariant + def.id().getPath() + "_slab");
			var modelTop = RAAMaterials.id(perModelPrefixVariant + def.id().getPath() + "_slab_top");

			rp.addModel(JModel.model("minecraft:block/slab")
					.textures(JModel.textures()
							.var("bottom", tex.toString())
							.var("top", tex.toString())
							.var("side", tex.toString())), modelBottom);
			rp.addModel(JModel.model("minecraft:block/slab_top")
					.textures(JModel.textures()
							.var("bottom", tex.toString())
							.var("top", tex.toString())
							.var("side", tex.toString())), modelTop);

			v.put(Map.of("mat", idx, "type", "bottom"), JState.model(modelBottom));
			v.put(Map.of("mat", idx, "type", "top"), JState.model(modelTop));
			var fullModel = RAAMaterials.id(fullBlockModelsFolder.getPath() + def.id().getPath());
			v.put(Map.of("mat", idx, "type", "double"), JState.model(fullModel));

			select.addCase(JSelectCase.of(def.id().toString(), JModelBasic.model(modelBottom.toString())));
		}

		rp.addBlockState(JState.state(v), sharedSlabId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedSlabId);
	}

	private static void buildStairsFamilyVariant(ResourceLocation sharedStairsId, String perModelPrefixVariant,
												 TexPicker variantTex) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var v = JState.variant();

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.STAIRS)) continue;

			var tex = variantTex.pick(idx);
			var model = RAAMaterials.id(perModelPrefixVariant + def.id().getPath() + "_stairs");
			var modelIn = RAAMaterials.id(perModelPrefixVariant + def.id().getPath() + "_stairs_inner");
			var modelOut = RAAMaterials.id(perModelPrefixVariant + def.id().getPath() + "_stairs_outer");

			var texs = JModel.textures().var("bottom", tex.toString()).var("top", tex.toString()).var("side", tex.toString());
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
			select.addCase(JSelectCase.of(def.id().toString(), JModelBasic.model(model.toString())));
		}

		rp.addBlockState(JState.state(v), sharedStairsId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedStairsId);
	}

	// =========================================================
	// Helpers — SHAPES (Variant-based)
	// =========================================================

	private static void buildWallFamilyVariant(ResourceLocation sharedWallId, String perModelPrefixVariant,
											   TexPicker variantTex) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();
		var select = JItemModel.select().property(MAT_COMP);
		var multipart = JState.multipart();

		for (int idx = 0; idx < mats.size(); idx++) {
			var def = mats.get(idx);
			if (!FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.WALL)) continue;

			var tex = variantTex.pick(idx);
			var post = RAAMaterials.id(perModelPrefixVariant + def.id().getPath() + "_wall_post");
			var side = RAAMaterials.id(perModelPrefixVariant + def.id().getPath() + "_wall_side");
			var sideT = RAAMaterials.id(perModelPrefixVariant + def.id().getPath() + "_wall_side_tall");
			var inventory = RAAMaterials.id(perModelPrefixVariant + def.id().getPath() + "_wall_inventory");

			rp.addModel(JModel.model("minecraft:block/template_wall_post")
					.textures(JModel.textures().var("wall", tex.toString())), post);
			rp.addModel(JModel.model("minecraft:block/template_wall_side")
					.textures(JModel.textures().var("wall", tex.toString())), side);
			rp.addModel(JModel.model("minecraft:block/template_wall_side_tall")
					.textures(JModel.textures().var("wall", tex.toString())), sideT);
			rp.addModel(JModel.model("minecraft:block/wall_inventory")
					.textures(JModel.textures().var("wall", tex.toString())), inventory);

			multipart.when(Map.of("mat", idx)).addModel(JState.model(post));
			multipart.when(Map.of("mat", idx, "north", "low")).addModel(JState.model(side).uvlock().y(0));
			multipart.when(Map.of("mat", idx, "east", "low")).addModel(JState.model(side).uvlock().y(90));
			multipart.when(Map.of("mat", idx, "south", "low")).addModel(JState.model(side).uvlock().y(180));
			multipart.when(Map.of("mat", idx, "west", "low")).addModel(JState.model(side).uvlock().y(270));
			multipart.when(Map.of("mat", idx, "north", "tall")).addModel(JState.model(sideT).uvlock().y(0));
			multipart.when(Map.of("mat", idx, "east", "tall")).addModel(JState.model(sideT).uvlock().y(90));
			multipart.when(Map.of("mat", idx, "south", "tall")).addModel(JState.model(sideT).uvlock().y(180));
			multipart.when(Map.of("mat", idx, "west", "tall")).addModel(JState.model(sideT).uvlock().y(270));

			select.addCase(JSelectCase.of(def.id().toString(), JModelBasic.model(inventory.toString()).tint(JTint.constant(def.primaryColor()))));
		}

		rp.addBlockState(JState.state(multipart), sharedWallId);
		select.fallback(JModelBasic.of("minecraft:block/stone"));
		rp.addItemModelInfo(new JItemInfo().model(select), sharedWallId);
	}

	private static void buildItemFamily(
			List<Form> forms, ResourceLocation logicalItemId, String perModelPrefix, TexPicker layer0
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();

		// Guard: 0 materials → just use the fallback model
		if (mats == null || mats.isEmpty()) {
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
			if (hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), forms)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.id().getPath());
			addGeneratedItemModel(rp, perModelId, layer0.pick(idx));
			select.addCase(JSelectCase.of(def.id().toString(), JModelBasic.model(perModelId.toString())
					.tint(new JTintConstant(def.primaryColor()))));
			cases++;
		}

		if (cases == 0) {
			// Nothing to select on → emit a simple model
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
			Form forms,
			ResourceLocation logicalItemId,
			String perModelPrefix,
			TexPicker layer0,
			TexPicker layer1
	) {
		var rp = RRPGen.PACK;
		var mats = ClientMaterialCache.all();

		// Guard: no materials → plain fallback
		if (mats == null || mats.isEmpty()) {
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
			if (hasAny(FormsRuntime.activeForms(Minecraft.getInstance().level, def), forms)) continue;

			var perModelId = RAAMaterials.id(perModelPrefix + def.id().getPath());

			// Build a generated model with 2 layers
			var model = JModel.model("item/generated")
					.textures(JModel.textures()
							.var("layer0", layer0.pick(idx).toString())
							.var("layer1", layer1.pick(idx).toString())
					);

			rp.addModel(model, perModelId);

			select.addCase(
					JSelectCase.of(
							def.id().toString(),
							JModelBasic.model(perModelId.toString()).tint(new JTintDye(0xFF00FF))
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

		select.fallback(JModelBasic.of(logicalItemId.withPrefix("item/").toString()));
		rp.addItemModelInfo(new JItemInfo().model(select), logicalItemId);
	}

	// =========================================================
	// Helpers — ITEMS
	// =========================================================

	private static void addGeneratedItemModel(RuntimeResourcePack rp, ResourceLocation modelId, ResourceLocation layer0) {
		rp.addModel(JModel.model("minecraft:item/generated")
						.textures(JModel.textures().var("layer0", layer0.withPrefix("item/").toString().replace(".png", ""))),
				modelId
		);
	}

	private static void addRecipe(ResourceLocation id, JRecipe recipe) {
		var file = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "recipes/" + id.getPath() + ".json");
		RRPGen.PACK.addRecipe(file, recipe);
	}

	// ---- Custom ingredient matching a specific material component ----
	// Shape: { "type":"raa_materials:component", "item":"<shared>", "components":{"raa_materials:material":{"id":"<mat>"}} }
	private static JIngredient ingredientWithMaterial(ResourceLocation sharedItemId, ResourceLocation mat) {
		return JIngredient.ingredient().fabricCustom(
				RAAMaterials.id("component"), // ← MUST equal your CustomIngredientSerializer id
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

	/*private static void buildRecipes() {
		var mats = ClientMaterialCache.all();

		for (MaterialDef def : mats) {
			var matId = def.id(); // raa_materials:<material_registry_key>

			// ---------- Compression: ingot <-> block, nugget <-> ingot ----------
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.INGOT) && FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BLOCK)) {
				// 9 ingots -> 1 block
				addRecipe(
						recipeId("compression/block_from_ingots", BLOCK_SHARED_ID, matId),
						shaped(BLOCK_SHARED_ID, matId, 1,
								"###", "###", "###")
				);
				// 1 block -> 9 ingots
				addRecipe(
						recipeId("decompression/ingots_from_block", INGOT_SHARED_ID, matId),
						shapeless(INGOT_SHARED_ID, matId, 9, BLOCK_SHARED_ID, 1)
				);
			}
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.NUGGET) && FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.INGOT)) {
				// 9 nuggets -> 1 ingot
				JsonObject nineNuggets = new JsonObject();
				nineNuggets.addProperty("type", "raa_materials:parametric_shaped");
				JsonArray pat = new JsonArray();
				pat.add("###");
				pat.add("###");
				pat.add("###");
				nineNuggets.add("pattern", pat);
				JsonObject key = new JsonObject();
				key.add("#", componentHolder(NUGGET_SHARED_ID, matId));
				nineNuggets.add("key", key);
				nineNuggets.add("result", resultWithCount(INGOT_SHARED_ID, matId, 1));
				addRecipe(recipeId("compression/ingot_from_nuggets", INGOT_SHARED_ID, matId), nineNuggets);

				// 1 ingot -> 9 nuggets
				addRecipe(
						recipeId("decompression/nuggets_from_ingot", NUGGET_SHARED_ID, matId),
						shapeless(NUGGET_SHARED_ID, matId, 9, INGOT_SHARED_ID, 1)
				);
			}

			// ---------- Plates & Gears (optional simple patterns) ----------
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.PLATE) && FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.INGOT)) {
				// 1 ingot -> 1 plate (shapeless) — swap to hammer recipe if you have a tool
				addRecipe(
						recipeId("processing/plate_from_ingot", PLATE_SHARED_ID, matId),
						shapeless(PLATE_SHARED_ID, matId, 1, INGOT_SHARED_ID, 1)
				);
			}
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.GEAR) && FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.PLATE) && FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.INGOT)) {
				// #
				//#X#
				// #
				JsonObject gear = new JsonObject();
				gear.addProperty("type", "raa_materials:parametric_shaped");
				JsonArray pat = new JsonArray();
				pat.add(" # ");
				pat.add("#X#");
				pat.add(" # ");
				gear.add("pattern", pat);
				JsonObject key = new JsonObject();
				key.add("#", componentHolder(INGOT_SHARED_ID, matId));
				key.add("X", componentHolder(PLATE_SHARED_ID, matId));
				gear.add("key", key);
				gear.add("result", resultWithCount(GEAR_SHARED_ID, matId, 1));
				JRecipe recipe = JRecipe.shaped(JPattern.pattern(" # ", "###", " # "), JKeys.keys().key("#", JIngredient.ingredient().item(INGOT_SHARED_ID)));
				addRecipe(recipeId("processing/gear", GEAR_SHARED_ID, matId), gear);
			}

			// ---------- Slab / Stairs / Wall from BLOCK ----------
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BLOCK)) {
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.SLAB)) {
					// 3 blocks -> 6 slabs
					JsonObject r = shaped(SLAB_SHARED_ID, matId, 6, "###");
					addRecipe(recipeId("building/slab_from_block", SLAB_SHARED_ID, matId), r);
					// stonecutting 1 block -> 2 slabs
					addRecipe(recipeId("stonecutting/slab_from_block", SLAB_SHARED_ID, matId),
							stonecutting(SLAB_SHARED_ID, matId, 2, BLOCK_SHARED_ID));
				}
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.STAIRS)) {
					// 6 blocks -> 4 stairs
					JsonObject r = shaped(STAIRS_SHARED_ID, matId, 4,
							"#  ", "## ", "###");
					addRecipe(recipeId("building/stairs_from_block", STAIRS_SHARED_ID, matId), r);
					// stonecutting 1 block -> 1 stairs
					addRecipe(recipeId("stonecutting/stairs_from_block", STAIRS_SHARED_ID, matId),
							stonecutting(STAIRS_SHARED_ID, matId, 1, BLOCK_SHARED_ID));
				}
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.WALL)) {
					// 6 blocks -> 6 walls
					JsonObject r = shaped(WALL_SHARED_ID, matId, 6,
							"###", "###");
					addRecipe(recipeId("building/wall_from_block", WALL_SHARED_ID, matId), r);
					// stonecutting 1 block -> 1 wall
					addRecipe(recipeId("stonecutting/wall_from_block", WALL_SHARED_ID, matId),
							stonecutting(WALL_SHARED_ID, matId, 1, BLOCK_SHARED_ID));
				}
				// Polished / Bricks / Chiseled (generic)
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.POLISHED)) {
					// 4 block -> 4 polished
					addRecipe(recipeId("processing/polished_from_block", POLISHED_SHARED_ID, matId),
							shaped(POLISHED_SHARED_ID, matId, 4, "##", "##"));
					// stonecutting 1 block -> 1 polished
					addRecipe(recipeId("stonecutting/polished_from_block", POLISHED_SHARED_ID, matId),
							stonecutting(POLISHED_SHARED_ID, matId, 1, BLOCK_SHARED_ID));
				}
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BRICKS)) {
					addRecipe(recipeId("processing/bricks_from_block", BRICKS_SHARED_ID, matId),
							shaped(BRICKS_SHARED_ID, matId, 4, "##", "##"));
					addRecipe(recipeId("stonecutting/bricks_from_block", BRICKS_SHARED_ID, matId),
							stonecutting(BRICKS_SHARED_ID, matId, 1, BLOCK_SHARED_ID));
				}
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.CHISELED)) {
					// 2 slabs (vertical) -> 1 chiseled (mirrors vanilla stone chiseled)
					JsonObject chis = new JsonObject();
					chis.addProperty("type", "raa_materials:parametric_shaped");
					JsonArray pat = new JsonArray();
					pat.add("#");
					pat.add("#");
					chis.add("pattern", pat);
					JsonObject key = new JsonObject();
					key.add("#", componentHolder(SLAB_SHARED_ID, matId));
					chis.add("key", key);
					chis.add("result", resultWithCount(CHISELED_SHARED_ID, matId, 1));
					addRecipe(recipeId("processing/chiseled_from_slabs", CHISELED_SHARED_ID, matId), chis);
				}
			}

			// ---------- Sandstone line (SAND → SANDSTONE → CUT/SMOOTH) ----------
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.SANDSTONE)) {
				// 4 sand -> 1 sandstone
				JsonObject ss = new JsonObject();
				ss.addProperty("type", "raa_materials:parametric_shaped");
				JsonArray pat = new JsonArray();
				pat.add("##");
				pat.add("##");
				ss.add("pattern", pat);
				JsonObject key = new JsonObject();
				key.add("#", componentHolder(BLOCK_SHARED_ID, matId)); // assuming your SAND block uses BLOCK_SHARED_ID
				ss.add("key", key);
				ss.add("result", resultWithCount(SANDSTONE_SHARED_ID, matId, 1));
				addRecipe(recipeId("processing/sandstone_from_sand", SANDSTONE_SHARED_ID, matId), ss);

				// stonecutting: 1 sand -> 1 sandstone (QoL)
				addRecipe(recipeId("stonecutting/sandstone_from_sand", SANDSTONE_SHARED_ID, matId),
						stonecutting(SANDSTONE_SHARED_ID, matId, 1, BLOCK_SHARED_ID));
			}
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.CUT_SANDSTONE)) {
				// from sandstone via stonecutting
				addRecipe(recipeId("stonecutting/cut_sandstone", CUT_SANDSTONE_SHARED_ID, matId),
						stonecutting(CUT_SANDSTONE_SHARED_ID, matId, 1, SANDSTONE_SHARED_ID));
			}
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.SMOOTH_SANDSTONE)) {
				// smelt sandstone -> smooth sandstone
				addRecipe(recipeId("smelting/smooth_sandstone", SMOOTH_SANDSTONE_SHARED_ID, matId),
						smelting(SMOOTH_SANDSTONE_SHARED_ID, matId, 0.1f, 200, SANDSTONE_SHARED_ID, "raa_materials:parametric_smelting"));
				addRecipe(recipeId("blasting/smooth_sandstone", SMOOTH_SANDSTONE_SHARED_ID, matId),
						smelting(SMOOTH_SANDSTONE_SHARED_ID, matId, 0.1f, 100, SANDSTONE_SHARED_ID, "raa_materials:parametric_blasting"));
			}

			// ---------- Mud / Dried / Bricks ----------
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.DRIED) && FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BLOCK)) {
				// smelt block -> dried
				addRecipe(recipeId("smelting/dried_from_block", DRIED_SHARED_ID, matId),
						smelting(DRIED_SHARED_ID, matId, 0.1f, 200, BLOCK_SHARED_ID, "raa_materials:parametric_smelting"));
				addRecipe(recipeId("blasting/dried_from_block", DRIED_SHARED_ID, matId),
						smelting(DRIED_SHARED_ID, matId, 0.1f, 100, BLOCK_SHARED_ID, "raa_materials:parametric_blasting"));
			}
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BRICKS) && FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.DRIED)) {
				// 4 dried -> 4 bricks
				addRecipe(recipeId("processing/bricks_from_dried", BRICKS_SHARED_ID, matId),
						shaped(BRICKS_SHARED_ID, matId, 4, "##", "##"));
			}

			// ---------- Clay / Ceramic ----------
			if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.CERAMIC) && FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.BALL)) {
				// smelt clay ball -> ceramic block (vanilla-ish)
				addRecipe(recipeId("smelting/ceramic_from_ball", CERAMIC_SHARED_ID, matId),
						smelting(CERAMIC_SHARED_ID, matId, 0.3f, 200, BALL_SHARED_ID, "raa_materials:parametric_smelting"));
				addRecipe(recipeId("blasting/ceramic_from_ball", CERAMIC_SHARED_ID, matId),
						smelting(CERAMIC_SHARED_ID, matId, 0.3f, 100, BALL_SHARED_ID, "raa_materials:parametric_blasting"));
			}

			// ---------- Ore/raw → ingot (metals/alloys) ----------
			boolean metalish = switch (def.kind()) {
				case METAL, ALLOY -> true;
				default -> false;
			};
			if (metalish && FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.INGOT)) {
				// ORE -> INGOT (xp 0.7)
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.ORE)) {
					addRecipe(recipeId("smelting/ingot_from_ore", INGOT_SHARED_ID, matId),
							smelting(INGOT_SHARED_ID, matId, 0.7f, 200, ORE_SHARED_ID, "raa_materials:parametric_smelting"));
					addRecipe(recipeId("blasting/ingot_from_ore", INGOT_SHARED_ID, matId),
							smelting(INGOT_SHARED_ID, matId, 0.7f, 100, ORE_SHARED_ID, "raa_materials:parametric_blasting"));
				}
				// RAW -> INGOT (xp 0.7)
				if (FormsRuntime.activeForms(Minecraft.getInstance().level, def).contains(Form.RAW)) {
					addRecipe(recipeId("smelting/ingot_from_raw", INGOT_SHARED_ID, matId),
							smelting(INGOT_SHARED_ID, matId, 0.7f, 200, RAW_SHARED_ID, "raa_materials:parametric_smelting"));
					addRecipe(recipeId("blasting/ingot_from_raw", INGOT_SHARED_ID, matId),
							smelting(INGOT_SHARED_ID, matId, 0.7f, 100, RAW_SHARED_ID, "raa_materials:parametric_blasting"));
				}
			}
		}
	}*/

	// =========================================================
	// Helpers — RECIPES (generic JSON emitters)
	// =========================================================

	// ---- Result with components (vanilla supports this in 1.20.5+) ----
	private static JResult resultWithMaterial(ResourceLocation itemId, ResourceLocation mat) {
		var res = JResult.result(itemId);
		res.components(builder -> builder.set(YComponents.MATERIAL, mat));
		return res;
	}

	private static JStackedResult resultWithMaterial(ResourceLocation itemId, ResourceLocation mat, int count) {
		var res = JResult.stackedResult(itemId, count);
		res.components(builder -> builder.set(YComponents.MATERIAL, mat));
		return res;
	}

	// ---- Vanilla shaped (single key char '#') using our custom ingredient ----
	private static JShapedRecipe shapedVanilla(ResourceLocation outId, ResourceLocation mat, int count,
											   ResourceLocation inSharedItem, String... pattern) {
		return JRecipe.shaped(
				JPattern.pattern(pattern),
				JKeys.keys().key("#", ingredientWithMaterial(inSharedItem, mat)),
				resultWithMaterial(outId, mat, count)
		);
	}

	// ---- Vanilla shapeless (optionally with count on the ingredient) ----
	private static JShapelessRecipe shapelessVanilla(ResourceLocation outId, ResourceLocation mat, int count,
													 ResourceLocation inSharedItem, int inCount) {
		return JRecipe.shapeless(
				JIngredients.ingredients().add(ingredientWithMaterial(inSharedItem, mat)),
				resultWithMaterial(outId, mat, count)
		);
	}

	// ---- Vanilla stonecutting ----
	private static JStonecuttingRecipe stonecuttingVanilla(ResourceLocation outId, ResourceLocation mat, int count,
														   ResourceLocation inSharedItem) {
		return JRecipe.stonecutting(ingredientWithMaterial(inSharedItem, mat), resultWithMaterial(outId, mat, count));
	}

	// ---- Vanilla smelting / blasting ----
	private static JCookingRecipe furnaceVanilla(String type, ResourceLocation outId, ResourceLocation mat,
												 float xp, int time, ResourceLocation inSharedItem) {
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

	// Namespace-friendly recipe id helper
	private static ResourceLocation recipeId(String family, ResourceLocation sharedId, ResourceLocation mat) {
		return RAAMaterials.id(STR."\{STR."\{family}/" + sharedId.getPath()}/" + mat.getPath());
	}

	@FunctionalInterface
	private interface TexPicker {
		ResourceLocation pick(int idx);
	}

	private record JBlockModelEntry(int idx, JBlockModel model) {
	}
}
