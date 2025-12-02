// YTabs.java
package net.vampirestudios.raaMaterials.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.material.ClientMaterialCache;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;

public final class YTabs {
	public static CreativeModeTab NATURAL;
	public static CreativeModeTab RESOURCES;
	public static CreativeModeTab TOOLS;
	public static CreativeModeTab WEAPONS;
	public static CreativeModeTab BUILDING;

	// --- helpers -------------------------------------------------------------

	private static void addIf(CreativeModeTab.Output out, MaterialDef def, Form form, Item item) {
		if (!def.forms().contains(form)) return;
		add(out, def, item);
	}

	private static void add(CreativeModeTab.Output out, MaterialDef def, Item item) {
		ItemStack s = new ItemStack(item);
		s.set(YComponents.MATERIAL, def.id());
		out.accept(s);
	}

	private static void addBlock(CreativeModeTab.Output output, MaterialDef def, int idx, Form form, Item item) {
		if (def.forms().contains(form)) {
			ItemStack stack = new ItemStack(item);
			stack.set(YComponents.MATERIAL, def.id());
			stack.set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(ParametricBlock.MAT, idx));
			output.accept(stack);
		}
	}

	private static boolean hasAll(MaterialDef def, Form... forms) {
		for (Form f : forms) if (!def.forms().contains(f)) return false;
		return true;
	}

	private static void addBlocksIf(CreativeModeTab.Output out, MaterialDef def, int idx, Object[][] formsAndItems) {
		for (var pair : formsAndItems) addBlock(out, def, idx, (Form) pair[0], (Item) pair[1]);
	}

	private static void addShapeSet(CreativeModeTab.Output out, MaterialDef def, int idx,
									Form base, Item slab, Item stairs, Item wall) {
		if (!def.forms().contains(base)) return;
		addBlock(out, def, idx, Form.SLAB, slab);
		addBlock(out, def, idx, Form.STAIRS, stairs);
		addBlock(out, def, idx, Form.WALL, wall);
	}

	private static boolean is(MaterialDef def, MaterialKind... kinds) {
		for (var k : kinds) if (def.kind() == k) return true;
		return false;
	}

	private static boolean isStoneLike(MaterialDef def) {
		return is(def,
				MaterialKind.STONE, MaterialKind.SAND, MaterialKind.GRAVEL,
				MaterialKind.CLAY, MaterialKind.MUD, MaterialKind.VOLCANIC
		);
	}

	private static boolean isSandFamily(MaterialDef def) {
		return def.kind() == MaterialKind.SAND;
	}

	private static boolean isMetalLike(MaterialDef def) {
		return is(def, MaterialKind.METAL, MaterialKind.ALLOY);
	}

	private static boolean isWoodLike(MaterialDef def) {
		return def.kind() == MaterialKind.WOOD;
	}

	private static boolean isGemLike(MaterialDef def) {
		return def.kind() == MaterialKind.GEM;
	}

	private static boolean isCrystalLike(MaterialDef def) {
		return def.kind() == MaterialKind.CRYSTAL;
	}

	private static void addForms(CreativeModeTab.Output out, MaterialDef def, int idx, Object[][] pairs) {
		for (var p : pairs) addBlock(out, def, idx, (Form) p[0], (Item) p[1]);
	}

	private static void addIfHas(CreativeModeTab.Output out, MaterialDef def, Item item, Form f) {
		if (def.forms().contains(f)) add(out, def, item);
	}

	// --- tabs ----------------------------------------------------------------

	public static void init() {
		// ORES: base ore/storage + metal utility; no structural/decor (avoid duplicates)
		NATURAL = Registry.register(
				BuiltInRegistries.CREATIVE_MODE_TAB,
				RAAMaterials.id("natural"),
				FabricItemGroup.builder()
						.title(Component.translatable("tab.raa_materials.natural"))
						.icon(() -> new ItemStack(YItems.PARAM_ORE_ITEM))
						.displayItems((params, output) -> {
							var mats = ClientMaterialCache.all();
							for (int idx = 0; idx < mats.size(); idx++) {
								var def = mats.get(idx);
								addBlock(output, def, idx, Form.ORE, YItems.PARAM_ORE_ITEM);
								if (!isMetalLike(def) && !isGemLike(def)) {
									addBlock(output, def, idx, Form.BLOCK, YItems.PARAM_BLOCK_ITEM);
								}
								addBlock(output, def, idx, Form.RAW_BLOCK, YItems.PARAM_RAW_BLOCK_ITEM);
								addBlock(output, def, idx, Form.CLUSTER, YItems.PARAM_CLUSTER_ITEM);
							}
						})
						.build()
		);

		// RESOURCES: items only (no block-forms)
		RESOURCES = Registry.register(
				BuiltInRegistries.CREATIVE_MODE_TAB,
				RAAMaterials.id("resources"),
				FabricItemGroup.builder()
						.title(Component.translatable("tab.raa_materials.resources"))
						.icon(() -> new ItemStack(YItems.PARAM_INGOT))
						.displayItems((params, output) -> {
							var mats = ClientMaterialCache.all();
							for (MaterialDef def : mats) {
								addIf(output, def, Form.INGOT, YItems.PARAM_INGOT);
								addIf(output, def, Form.DUST, YItems.PARAM_DUST);
								addIf(output, def, Form.NUGGET, YItems.PARAM_NUGGET);
								addIf(output, def, Form.RAW, YItems.PARAM_RAW);
								addIf(output, def, Form.GEM, YItems.PARAM_GEM);
								addIf(output, def, Form.CRYSTAL, YItems.PARAM_CRYSTAL);
								addIf(output, def, Form.SHARD, YItems.PARAM_SHARD);
								addIf(output, def, Form.SHEET, YItems.PARAM_SHEET);
								addIf(output, def, Form.GEAR, YItems.PARAM_GEAR);
								addIf(output, def, Form.BALL, YItems.PARAM_BALL);
								addIfHas(output, def, YItems.PARAM_ROD, Form.ROD);
								addIfHas(output, def, YItems.PARAM_WIRE, Form.WIRE);
								addIfHas(output, def, YItems.PARAM_COIL, Form.COIL);
								addIfHas(output, def, YItems.PARAM_RIVET, Form.RIVET);
								addIfHas(output, def, YItems.PARAM_BOLT, Form.BOLT);
								addIfHas(output, def, YItems.PARAM_NAIL, Form.NAIL);
								addIfHas(output, def, YItems.PARAM_RING, Form.RING);
							}
						})
						.build()
		);

		// BUILDING: structural shapes/variants only
		BUILDING = Registry.register(
				BuiltInRegistries.CREATIVE_MODE_TAB,
				RAAMaterials.id("building"),
				FabricItemGroup.builder()
						.title(Component.translatable("tab.raa_materials.building"))
						.icon(() -> new ItemStack(YItems.PARAM_BLOCK_ITEM))
						.displayItems((params, output) -> {
							var mats = ClientMaterialCache.all();
							for (int idx = 0; idx < mats.size(); idx++) {
								var def = mats.get(idx);

								// --- Core structural set
								addBlocksIf(output, def, idx, new Object[][]{
										{Form.BLOCK, YItems.PARAM_BLOCK_ITEM},
										{Form.SLAB, YItems.PARAM_SLAB_ITEM},
										{Form.STAIRS, YItems.PARAM_STAIRS_ITEM},
										{Form.WALL, YItems.PARAM_WALL_ITEM},
										{Form.PILLAR, YItems.PARAM_PILLAR_ITEM},
										{Form.POLISHED, YItems.PARAM_POLISHED_BLOCK_ITEM},
										{Form.CHISELED, YItems.PARAM_CHISELED_BLOCK_ITEM},
										{Form.TILES, YItems.PARAM_TILES_ITEM},
										{Form.MOSAIC, YItems.PARAM_MOSAIC_ITEM},
										{Form.CUT, YItems.PARAM_CUT_BLOCK_ITEM},
										{Form.SMOOTH, YItems.PARAM_SMOOTH_BLOCK_ITEM}
								});

								// Bricks base block (extend to shapes if you want)
								addBlock(output, def, idx, Form.BRICKS, YItems.PARAM_BRICKS_BLOCK_ITEM);

								// Optional stone-like variants
								if (isStoneLike(def)) {
									addBlocksIf(output, def, idx, new Object[][]{
											{Form.MOSSY, YItems.PARAM_MOSSY_ITEM},
											{Form.CRACKED, YItems.PARAM_CRACKED_ITEM},
											{Form.COBBLED, YItems.PARAM_COBBLED_ITEM}
									});
								}

								// Redstone-y buttons/plates by material family
								if (isMetalLike(def)) {
									addBlocksIf(output, def, idx, new Object[][]{
											{Form.BUTTON, YItems.PARAM_BUTTON_METAL_ITEM},
											{Form.PRESSURE_PLATE, YItems.PARAM_PRESSURE_PLATE_METAL_ITEM}
									});
								} else if (isWoodLike(def)) {
									addBlocksIf(output, def, idx, new Object[][]{
											{Form.BUTTON, YItems.PARAM_BUTTON_WOOD_ITEM},
											{Form.PRESSURE_PLATE, YItems.PARAM_PRESSURE_PLATE_WOOD_ITEM}
									});
								} else if (isStoneLike(def)) {
									addBlocksIf(output, def, idx, new Object[][]{
											{Form.BUTTON, YItems.PARAM_BUTTON_STONE_ITEM},
											{Form.PRESSURE_PLATE, YItems.PARAM_PRESSURE_PLATE_STONE_ITEM}
									});
								}

								// Doors / Trapdoors (already present)
								addBlocksIf(output, def, idx, new Object[][]{
										{Form.DOOR, YItems.PARAM_DOOR_ITEM},
										{Form.TRAPDOOR, YItems.PARAM_TRAPDOOR_ITEM}
								});

								// NEW: Fences (by family)
//								if (isWoodLike(def)) {
//									addBlocksIf(output, def, idx, new Object[][]{
//											{Form.FENCE, YItems.PARAM_FENCE_WOOD_ITEM},
//											{Form.FENCE_GATE, YItems.PARAM_FENCE_GATE_WOOD_ITEM}
//									});
//								} else if (isMetalLike(def)) {
//									addBlocksIf(output, def, idx, new Object[][]{
//											{Form.FENCE, YItems.PARAM_FENCE_METAL_ITEM},
//											{Form.FENCE_GATE, YItems.PARAM_FENCE_GATE_METAL_ITEM}
//									});
//								}
//
//								// Signs (wood-like)
//								if (isWoodLike(def)) {
//									addBlocksIf(output, def, idx, new Object[][]{
//											{Form.SIGN, YItems.PARAM_SIGN_ITEM},
//											{Form.HANGING_SIGN, YItems.PARAM_HANGING_SIGN_ITEM}
//									});
//								}

								// Sandstone family (SAND only)
								if (isSandFamily(def)) {
									addBlocksIf(output, def, idx, new Object[][]{
											{Form.SANDSTONE, YItems.PARAM_SANDSTONE_BLOCK_ITEM},
											{Form.SLAB, YItems.PARAM_SANDSTONE_SLAB_ITEM},
											{Form.STAIRS, YItems.PARAM_SANDSTONE_STAIRS_ITEM},
											{Form.WALL, YItems.PARAM_SANDSTONE_WALL_ITEM}
									});
								}

								// Mud / Clay specifics
								if (def.kind() == MaterialKind.MUD) {
									addBlocksIf(output, def, idx, new Object[][]{
											{Form.DRIED, YItems.PARAM_DRIED_BLOCK_ITEM}
									});
									// If you want mud bricks shapes too:
									if (hasAll(def, Form.BRICKS, Form.SLAB, Form.STAIRS, Form.WALL)) {
										addBlocksIf(output, def, idx, new Object[][]{
												{Form.SLAB, YItems.PARAM_BRICK_SLAB_ITEM},
												{Form.STAIRS, YItems.PARAM_BRICK_STAIRS_ITEM},
												{Form.WALL, YItems.PARAM_BRICK_WALL_ITEM}
										});
									}
								}
								if (def.kind() == MaterialKind.CLAY) {
									addBlocksIf(output, def, idx, new Object[][]{
											{Form.CERAMIC, YItems.PARAM_CERAMIC_BLOCK_ITEM}
									});
								}
//								if (def.kind() == MaterialKind.SOIL) {
//									addBlocksIf(output, def, idx, new Object[][]{
//											{Form.PACKED_SOIL, YItems.PARAM_PACKED_SOIL_ITEM}
//									});
//								}

								// Metal structural extras
								if (isMetalLike(def)) {
									addBlocksIf(output, def, idx, new Object[][]{
											{Form.SHINGLES, YItems.PARAM_SHINGLES_BLOCK_ITEM},
											{Form.PLATE_BLOCK, YItems.PARAM_PLATE_BLOCK_ITEM},
											{Form.BARS, YItems.PARAM_BARS_ITEM},
											{Form.GRATE, YItems.PARAM_GRATE_ITEM}
									});
								}

								// Crystal / decor & lighting (moved from DECOR)
								addBlocksIf(output, def, idx, new Object[][]{
										{Form.CALCITE_LAMP, YItems.PARAM_CALCITE_LAMP_ITEM},
										{Form.BASALT_LAMP, YItems.PARAM_BASALT_LAMP_ITEM},
//										{Form.LAMP, YItems.PARAM_LAMP_ITEM} // generic lamp if present
								});
								if (isCrystalLike(def)) {
									addBlocksIf(output, def, idx, new Object[][]{
											{Form.CRYSTAL_BRICKS, YItems.PARAM_CRYSTAL_BRICKS_ITEM},
											{Form.PANE, YItems.PARAM_CRYSTAL_PANE_ITEM},
											{Form.GLASS, YItems.PARAM_CRYSTAL_GLASS_ITEM},
											{Form.TINTED_GLASS, YItems.PARAM_CRYSTAL_TINTED_GLASS_ITEM},
											{Form.ROD_BLOCK, YItems.PARAM_ROD_BLOCK_ITEM},
//											{Form.BUDDING, YItems.PARAM_BUDDING_BLOCK_ITEM},
//											{Form.BUD_SMALL, YItems.PARAM_BUD_SMALL_ITEM},
//											{Form.BUD_MEDIUM, YItems.PARAM_BUD_MEDIUM_ITEM},
//											{Form.BUD_LARGE, YItems.PARAM_BUD_LARGE_ITEM},
											// {Form.CHIME, YItems.PARAM_CHIME_ITEM}, // enable if you have it
									});
								}

								// General decor (non-crystal): chain / lantern / torch (if the material defines them)
//								addBlocksIf(output, def, idx, new Object[][]{
//										{Form.CHAIN, YItems.PARAM_CHAIN_ITEM},
//										{Form.LANTERN, YItems.PARAM_LANTERN_ITEM},
//										{Form.TORCH, YItems.PARAM_TORCH_ITEM}
//								});
							}
						}).build()
		);
		// TOOLS
		TOOLS = Registry.register(
				BuiltInRegistries.CREATIVE_MODE_TAB,
				RAAMaterials.id("tools"),
				FabricItemGroup.builder()
						.title(Component.translatable("tab.raa_materials.tools"))
						.icon(() -> new ItemStack(YItems.PARAM_PICKAXE))
						.displayItems((params, output) -> {
							var mats = ClientMaterialCache.all();
							for (MaterialDef def : mats) {
								addIf(output, def, Form.PICKAXE, YItems.PARAM_PICKAXE);
								addIf(output, def, Form.AXE, YItems.PARAM_AXE);
								addIf(output, def, Form.SWORD, YItems.PARAM_SWORD);
								addIf(output, def, Form.SHOVEL, YItems.PARAM_SHOVEL);
								addIf(output, def, Form.HOE, YItems.PARAM_HOE);
							}
						})
						.build()
		);

		// WEAPONS
		WEAPONS = Registry.register(
				BuiltInRegistries.CREATIVE_MODE_TAB,
				RAAMaterials.id("weapons"),
				FabricItemGroup.builder()
						.title(Component.translatable("tab.raa_materials.weapons"))
						.icon(() -> new ItemStack(YItems.PARAM_SWORD))
						.displayItems((params, output) -> {
							var mats = ClientMaterialCache.all();
							for (MaterialDef def : mats) {
								addIf(output, def, Form.AXE, YItems.PARAM_AXE);
								addIf(output, def, Form.SWORD, YItems.PARAM_SWORD);
							}
						})
						.build()
		);
	}
}
