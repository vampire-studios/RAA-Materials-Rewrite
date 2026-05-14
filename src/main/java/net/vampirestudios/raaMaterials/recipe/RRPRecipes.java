package net.vampirestudios.raaMaterials.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.json.recipe.*;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.registry.YItems;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class RRPRecipes {
	public static void recipes(RuntimeResourcePack pack, List<MaterialDef> materials) {
		for (MaterialDef def : materials) {
			Identifier mat = def.nameInformation().id();
			String id = mat.getPath();

			if (has(def, Form.INGOT, Form.BLOCK)) {
				addShapedComponentRecipe(pack, "block_from_ingots/" + id, "misc",
						new String[]{"XXX", "XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_BLOCK_ITEM, mat, 1
				);
			}

			if (has(def, Form.BLOCK, Form.INGOT)) {
				addShapelessComponentRecipe(pack, "ingots_from_block/" + id, "misc",
						List.of(componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_INGOT, mat, 9
				);
			}

			if (has(def, Form.NUGGET, Form.INGOT)) {
				addShapedComponentRecipe(pack, "ingot_from_nuggets/" + id, "misc",
						new String[]{"XXX", "XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_NUGGET, mat)),
						YItems.PARAM_INGOT, mat, 1
				);
			}

			if (has(def, Form.INGOT, Form.NUGGET)) {
				addShapelessComponentRecipe(pack, "nuggets_from_ingot/" + id, "misc",
						List.of(componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_NUGGET, mat, 9
				);
			}

			if (has(def, Form.RAW, Form.RAW_BLOCK)) {
				addShapedComponentRecipe(pack, "raw_block_from_raw/" + id, "misc",
						new String[]{"XXX", "XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_RAW, mat)),
						YItems.PARAM_RAW_BLOCK_ITEM, mat, 1
				);
			}

			if (has(def, Form.RAW_BLOCK, Form.RAW)) {
				addShapelessComponentRecipe(pack, "raw_from_raw_block/" + id, "misc",
						List.of(componentIngredient(YItems.PARAM_RAW_BLOCK_ITEM, mat)),
						YItems.PARAM_RAW, mat, 9
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.PICKAXE)) {
				addShapedComponentRecipe(pack, "pickaxe/" + id, "equipment",
						new String[]{"XXX", " # ", " # "},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_PICKAXE, mat, 1
				);
			}
			if (isGem(def) && has(def, Form.GEM, Form.PICKAXE)) {
				addShapedComponentRecipe(pack, "gem_pickaxe/" + id, "equipment",
						new String[]{"XXX", " # ", " # "},
						Map.of('X', componentIngredient(YItems.PARAM_GEM, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_PICKAXE, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.AXE)) {
				addShapedComponentRecipe(pack, "axe/" + id, "equipment",
						new String[]{"XX", "X#", " #"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_AXE, mat, 1
				);
			}
			if (isGem(def) && has(def, Form.GEM, Form.AXE)) {
				addShapedComponentRecipe(pack, "gem_axe/" + id, "equipment",
						new String[]{"XX", "X#", " #"},
						Map.of('X', componentIngredient(YItems.PARAM_GEM, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_AXE, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.SHOVEL)) {
				addShapedComponentRecipe(pack, "shovel/" + id, "equipment",
						new String[]{"X", "#", "#"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_SHOVEL, mat, 1
				);
			}
			if (isGem(def) && has(def, Form.GEM, Form.SHOVEL)) {
				addShapedComponentRecipe(pack, "gem_shovel/" + id, "equipment",
						new String[]{"X", "#", "#"},
						Map.of('X', componentIngredient(YItems.PARAM_GEM, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_SHOVEL, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.HOE)) {
				addShapedComponentRecipe(pack, "hoe/" + id, "equipment",
						new String[]{"XX", " #", " #"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_HOE, mat, 1
				);
			}
			if (isGem(def) && has(def, Form.GEM, Form.HOE)) {
				addShapedComponentRecipe(pack, "gem_hoe/" + id, "equipment",
						new String[]{"XX", " #", " #"},
						Map.of('X', componentIngredient(YItems.PARAM_GEM, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_HOE, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.SWORD)) {
				addShapedComponentRecipe(pack, "sword/" + id, "equipment",
						new String[]{"X", "X", "#"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_SWORD, mat, 1
				);
			}
			if (isGem(def) && has(def, Form.GEM, Form.SWORD)) {
				addShapedComponentRecipe(pack, "gem_sword/" + id, "equipment",
						new String[]{"X", "X", "#"},
						Map.of('X', componentIngredient(YItems.PARAM_GEM, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_SWORD, mat, 1
				);
			}

			// Spear: 1 head at tip + 2 sticks diagonal. Vanilla auto-mirrors the pattern.
			if (isMetal(def) && has(def, Form.INGOT, Form.SPEAR)) {
				addShapedComponentRecipe(pack, "spear/" + id, "equipment",
						new String[]{"  X", " # ", "#  "},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_SPEAR, mat, 1
				);
			}
			if (isGem(def) && has(def, Form.GEM, Form.SPEAR)) {
				addShapedComponentRecipe(pack, "gem_spear/" + id, "equipment",
						new String[]{"  X", " # ", "#  "},
						Map.of('X', componentIngredient(YItems.PARAM_GEM, mat), '#', itemIngredient(Items.STICK)),
						YItems.PARAM_SPEAR, mat, 1
				);
			}

			if (has(def, Form.BLOCK, Form.POLISHED)) {
				addShapedComponentRecipe(pack, "polished/" + id, "building",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_POLISHED_BLOCK_ITEM, mat, 4
				);
			}

			if (has(def, Form.BLOCK, Form.BRICKS)) {
				addShapedComponentRecipe(pack, "bricks/" + id, "building",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_BRICKS_BLOCK_ITEM, mat, 4
				);
			}

			if (has(def, Form.BLOCK, Form.CUT)) {
				addShapedComponentRecipe(pack, "cut/" + id, "building",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_CUT_BLOCK_ITEM, mat, 4
				);
			}

			if (has(def, Form.SLAB, Form.CHISELED)) {
				addShapedComponentRecipe(pack, "chiseled/" + id, "building",
						new String[]{"X", "X"},
						Map.of('X', componentIngredient(YItems.PARAM_SLAB_ITEM, mat)),
						YItems.PARAM_CHISELED_BLOCK_ITEM, mat, 1
				);
			}

			if (has(def, Form.BLOCK, Form.PILLAR)) {
				addShapedComponentRecipe(pack, "pillar/" + id, "building",
						new String[]{"X", "X"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_PILLAR_ITEM, mat, 2
				);
			}

			if (has(def, Form.BLOCK, Form.SLAB, Form.STAIRS, Form.WALL)) {
				addBlockSetRecipes(pack, id, mat, "base",
						YItems.PARAM_BLOCK_ITEM,
						YItems.PARAM_SLAB_ITEM,
						YItems.PARAM_STAIRS_ITEM,
						YItems.PARAM_WALL_ITEM
				);
			}

			if (has(def, Form.SANDSTONE, Form.SLAB, Form.STAIRS, Form.WALL)) {
				addBlockSetRecipes(pack, id, mat, "sandstone",
						YItems.PARAM_SANDSTONE_BLOCK_ITEM,
						YItems.PARAM_SANDSTONE_SLAB_ITEM,
						YItems.PARAM_SANDSTONE_STAIRS_ITEM,
						YItems.PARAM_SANDSTONE_WALL_ITEM
				);
			}

			if (has(def, Form.BRICKS, Form.SLAB, Form.STAIRS, Form.WALL)) {
				addBlockSetRecipes(pack, id, mat, "brick",
						YItems.PARAM_BRICKS_BLOCK_ITEM,
						YItems.PARAM_BRICK_SLAB_ITEM,
						YItems.PARAM_BRICK_STAIRS_ITEM,
						YItems.PARAM_BRICK_WALL_ITEM
				);
			}

			if (has(def, Form.POLISHED, Form.SLAB, Form.STAIRS, Form.WALL)) {
				addBlockSetRecipes(pack, id, mat, "polished",
						YItems.PARAM_POLISHED_BLOCK_ITEM,
						YItems.PARAM_POLISHED_SLAB_ITEM,
						YItems.PARAM_POLISHED_STAIRS_ITEM,
						YItems.PARAM_POLISHED_WALL_ITEM
				);
			}

			if (has(def, Form.GLASS, Form.PANE)) {
				addShapedComponentRecipe(pack, "pane/" + id, "building",
						new String[]{"XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_GLASS_ITEM, mat)),
						YItems.PARAM_PANE_ITEM, mat, 16
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.SHEET)) {
				addShapedComponentRecipe(pack, "sheets/" + id, "misc",
						new String[]{"XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_SHEET, mat, 6
				);
			}

			if (isMetal(def) && has(def, Form.SHEET, Form.PLATE_BLOCK)) {
				addShapedComponentRecipe(pack, "plate_block/" + id, "building",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_SHEET, mat)),
						YItems.PARAM_PLATE_BLOCK_ITEM, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.BARS)) {
				addShapedComponentRecipe(pack, "bars/" + id, "building",
						new String[]{"XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_BARS_ITEM, mat, 16
				);
			}

			if (isMetal(def) && has(def, Form.BLOCK, Form.GRATE)) {
				addShapedComponentRecipe(pack, "grate/" + id, "building",
						new String[]{" X ", "X X", " X "},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_GRATE_ITEM, mat, 4
				);
			}

			if (isMetal(def) && has(def, Form.NUGGET, Form.INGOT, Form.CHAIN)) {
				addShapedComponentRecipe(pack, "chain/" + id, "decorations",
						new String[]{"X", "Y", "X"},
						Map.of(
								'X', componentIngredient(YItems.PARAM_NUGGET, mat),
								'Y', componentIngredient(YItems.PARAM_INGOT, mat)
						),
						YItems.PARAM_CHAIN_ITEM, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.NUGGET, Form.LANTERN)) {
				addShapedComponentRecipe(pack, "lantern/" + id, "decorations",
						new String[]{"XXX", "X#X", "XXX"},
						Map.of(
								'X', componentIngredient(YItems.PARAM_NUGGET, mat),
								'#', itemIngredient(Items.TORCH)
						),
						YItems.PARAM_LANTERN_ITEM, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.DOOR)) {
				addShapedComponentRecipe(pack, "metal_door/" + id, "redstone",
						new String[]{"XX", "XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_DOOR_METAL_ITEM, mat, 3
				);
			}

			if (isWood(def) && has(def, Form.BLOCK, Form.DOOR)) {
				addShapedComponentRecipe(pack, "wood_door/" + id, "redstone",
						new String[]{"XX", "XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_DOOR_WOOD_ITEM, mat, 3
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.TRAPDOOR)) {
				addShapedComponentRecipe(pack, "metal_trapdoor/" + id, "redstone",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_TRAPDOOR_METAL_ITEM, mat, 1
				);
			}

			if (isWood(def) && has(def, Form.BLOCK, Form.TRAPDOOR)) {
				addShapedComponentRecipe(pack, "wood_trapdoor/" + id, "redstone",
						new String[]{"XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_TRAPDOOR_WOOD_ITEM, mat, 2
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.FENCE)) {
				addShapedComponentRecipe(pack, "metal_fence/" + id, "building",
						new String[]{"XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_FENCE_ITEM, mat, 3
				);
			}

			if (isWood(def) && has(def, Form.BLOCK, Form.FENCE)) {
				addShapedComponentRecipe(pack, "wood_fence/" + id, "building",
						new String[]{"XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_FENCE_ITEM, mat, 3
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.FENCE_GATE)) {
				addShapedComponentRecipe(pack, "metal_fence_gate/" + id, "redstone",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_FENCE_GATE_ITEM, mat, 1
				);
			}

			if (isWood(def) && has(def, Form.BLOCK, Form.FENCE_GATE)) {
				addShapedComponentRecipe(pack, "wood_fence_gate/" + id, "redstone",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_FENCE_GATE_ITEM, mat, 1
				);
			}

			if (isStoneLike(def) && has(def, Form.BLOCK, Form.BUTTON)) {
				addShapelessComponentRecipe(pack, "stone_button/" + id, "redstone",
						List.of(componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_BUTTON_STONE_ITEM, mat, 1
				);
			}

			if (isWood(def) && has(def, Form.BLOCK, Form.BUTTON)) {
				addShapelessComponentRecipe(pack, "wood_button/" + id, "redstone",
						List.of(componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_BUTTON_WOOD_ITEM, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.BUTTON)) {
				addShapelessComponentRecipe(pack, "metal_button/" + id, "redstone",
						List.of(componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_BUTTON_METAL_ITEM, mat, 1
				);
			}

			if (isStoneLike(def) && has(def, Form.BLOCK, Form.PRESSURE_PLATE)) {
				addShapedComponentRecipe(pack, "stone_pressure_plate/" + id, "redstone",
						new String[]{"XX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_PRESSURE_PLATE_STONE_ITEM, mat, 1
				);
			}

			if (isWood(def) && has(def, Form.BLOCK, Form.PRESSURE_PLATE)) {
				addShapedComponentRecipe(pack, "wood_pressure_plate/" + id, "redstone",
						new String[]{"XX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_PRESSURE_PLATE_WOOD_ITEM, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.PRESSURE_PLATE)) {
				addShapedComponentRecipe(pack, "metal_pressure_plate/" + id, "redstone",
						new String[]{"XX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_PRESSURE_PLATE_METAL_ITEM, mat, 1
				);
			}

			// ── Craft parts ──────────────────────────────────────────────────────────

			if (isMetal(def) && has(def, Form.INGOT, Form.ROD)) {
				addShapedComponentRecipe(pack, "rod_from_ingots/" + id, "misc",
						new String[]{"X", "X"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_ROD, mat, 4
				);
			}

			// 4 ingots in a cross → 1 gear (no nugget needed)
			if (isMetal(def) && has(def, Form.INGOT, Form.GEAR)) {
				addShapedComponentRecipe(pack, "gear_from_ingots/" + id, "misc",
						new String[]{" X ", "X X", " X "},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_GEAR, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.ROD, Form.WIRE)) {
				addShapedComponentRecipe(pack, "wire_from_rod/" + id, "misc",
						new String[]{"XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_ROD, mat)),
						YItems.PARAM_WIRE, mat, 4
				);
			}

			if (isMetal(def) && has(def, Form.WIRE, Form.COIL)) {
				addShapedComponentRecipe(pack, "coil_from_wire/" + id, "misc",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_WIRE, mat)),
						YItems.PARAM_COIL, mat, 1
				);
			}

			// 4 nuggets in a diamond ring pattern → 2 rings
			if (isMetal(def) && has(def, Form.NUGGET, Form.RING)) {
				addShapedComponentRecipe(pack, "ring_from_nuggets/" + id, "misc",
						new String[]{" X ", "X X", " X "},
						Map.of('X', componentIngredient(YItems.PARAM_NUGGET, mat)),
						YItems.PARAM_RING, mat, 2
				);
			}

			// 3 nuggets in a horizontal row → 6 rivets
			if (isMetal(def) && has(def, Form.NUGGET, Form.RIVET)) {
				addShapedComponentRecipe(pack, "rivet_from_nuggets/" + id, "misc",
						new String[]{"XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_NUGGET, mat)),
						YItems.PARAM_RIVET, mat, 6
				);
			}

			// 3 nuggets in a column → 6 nails
			if (isMetal(def) && has(def, Form.NUGGET, Form.NAIL)) {
				addShapedComponentRecipe(pack, "nail_from_nuggets/" + id, "misc",
						new String[]{"X", "X", "X"},
						Map.of('X', componentIngredient(YItems.PARAM_NUGGET, mat)),
						YItems.PARAM_NAIL, mat, 6
				);
			}

			// nugget–ingot–nugget column → 4 bolts
			if (isMetal(def) && has(def, Form.INGOT, Form.NUGGET, Form.BOLT)) {
				addShapedComponentRecipe(pack, "bolt_from_ingots_and_nuggets/" + id, "misc",
						new String[]{"X", "Y", "X"},
						Map.of('X', componentIngredient(YItems.PARAM_NUGGET, mat),
								'Y', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_BOLT, mat, 4
				);
			}

			// Ball: 2×2 of the primary crafting unit → 1 ball
			if (isMetal(def) && has(def, Form.INGOT, Form.BALL)) {
				addShapedComponentRecipe(pack, "ball_from_ingots/" + id, "misc",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_BALL, mat, 1
				);
			}
			if (isGem(def) && has(def, Form.GEM, Form.BALL)) {
				addShapedComponentRecipe(pack, "ball_from_gems/" + id, "misc",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_GEM, mat)),
						YItems.PARAM_BALL, mat, 1
				);
			}
			if (isCrystal(def) && has(def, Form.CRYSTAL, Form.BALL)) {
				addShapedComponentRecipe(pack, "ball_from_crystals/" + id, "misc",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_CRYSTAL, mat)),
						YItems.PARAM_BALL, mat, 1
				);
			}

			// ── Building blocks ───────────────────────────────────────────────────────

			if (isCrystal(def) && has(def, Form.BLOCK, Form.CRYSTAL_BRICKS)) {
				addShapedComponentRecipe(pack, "crystal_bricks/" + id, "building",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_CRYSTAL_BLOCK_ITEM, mat)),
						YItems.PARAM_CRYSTAL_BRICKS_ITEM, mat, 4
				);
			}

			if (has(def, Form.POLISHED, Form.TILES)) {
				addShapedComponentRecipe(pack, "tiles_from_polished/" + id, "building",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_POLISHED_BLOCK_ITEM, mat)),
						YItems.PARAM_TILES_ITEM, mat, 4
				);
			}

			if (has(def, Form.TILES, Form.MOSAIC)) {
				addShapedComponentRecipe(pack, "mosaic_from_tiles/" + id, "building",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_TILES_ITEM, mat)),
						YItems.PARAM_MOSAIC_ITEM, mat, 4
				);
			}

			if (has(def, Form.POLISHED, Form.SHINGLES)) {
				addShapedComponentRecipe(pack, "shingles_from_polished/" + id, "building",
						new String[]{"XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_POLISHED_BLOCK_ITEM, mat)),
						YItems.PARAM_SHINGLES_BLOCK_ITEM, mat, 4
				);
			}

			// 6 rods (2×3) → 1 rod block
			if (isMetal(def) && has(def, Form.ROD, Form.ROD_BLOCK)) {
				addShapedComponentRecipe(pack, "rod_block_from_rods/" + id, "building",
						new String[]{"XX", "XX", "XX"},
						Map.of('X', componentIngredient(YItems.PARAM_ROD, mat)),
						YItems.PARAM_ROD_BLOCK_ITEM, mat, 1
				);
			}

			// Block + vine → mossy variant (shapeless; vine is a vanilla item)
			if (has(def, Form.BLOCK, Form.MOSSY)) {
				addShapelessComponentRecipe(pack, "mossy_from_block/" + id, "building",
						List.of(componentIngredient(YItems.PARAM_BLOCK_ITEM, mat),
								itemIngredient(Items.VINE)),
						YItems.PARAM_MOSSY_ITEM, mat, 1
				);
			}

			// Bricks + vine → mossy bricks (shapeless)
			if (has(def, Form.BRICKS, Form.MOSSY)) {
				addShapelessComponentRecipe(pack, "mossy_bricks_from_bricks/" + id, "building",
						List.of(componentIngredient(YItems.PARAM_BRICKS_BLOCK_ITEM, mat),
								itemIngredient(Items.VINE)),
						YItems.PARAM_MOSSY_ITEM, mat, 1
				);
			}

			// 3×3 blocks → 1 packed soil (soil / sand / clay / mud / gravel)
			if (isSoilLike(def) && has(def, Form.BLOCK, Form.PACKED_SOIL)) {
				addShapedComponentRecipe(pack, "packed_soil_from_block/" + id, "building",
						new String[]{"XXX", "XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_BLOCK_ITEM, mat)),
						YItems.PARAM_PACKED_SOIL_ITEM, mat, 1
				);
			}

			// Glass block + 4 shards in a cross → 2 tinted glass (crystal / gem)
			if ((isCrystal(def) || isGem(def)) && has(def, Form.GLASS, Form.SHARD, Form.TINTED_GLASS)) {
				addShapedComponentRecipe(pack, "tinted_glass/" + id, "building",
						new String[]{" Y ", "YXY", " Y "},
						Map.of('X', componentIngredient(YItems.PARAM_GLASS_ITEM, mat),
								'Y', componentIngredient(YItems.PARAM_SHARD, mat)),
						YItems.PARAM_TINTED_GLASS_ITEM, mat, 2
				);
			}

			// ── Equipment ─────────────────────────────────────────────────────────────

			if (isMetal(def) && has(def, Form.INGOT, Form.HORSE_ARMOR)) {
				addShapedComponentRecipe(pack, "horse_armor/" + id, "equipment",
						new String[]{"X X", "XXX", "X X"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_HORSE_ARMOR_ITEM, mat, 1
				);
			}

			if (isMetal(def) && has(def, Form.INGOT, Form.WOLF_ARMOR)) {
				addShapedComponentRecipe(pack, "wolf_armor/" + id, "equipment",
						new String[]{"X ", "XXX", "XXX"},
						Map.of('X', componentIngredient(YItems.PARAM_INGOT, mat)),
						YItems.PARAM_WOLF_ARMOR_ITEM, mat, 1
				);
			}
		}
	}

	private static void addBlockSetRecipes(
			RuntimeResourcePack pack,
			String materialPath,
			Identifier material,
			String set,
			Item block,
			Item slab,
			Item stairs,
			Item wall
	) {
		addShapedComponentRecipe(pack, set + "_slab/" + materialPath, "building",
				new String[]{"XXX"},
				Map.of('X', componentIngredient(block, material)),
				slab, material, 6
		);

		addShapedComponentRecipe(pack, set + "_stairs/" + materialPath, "building",
				new String[]{"X  ", "XX ", "XXX"},
				Map.of('X', componentIngredient(block, material)),
				stairs, material, 4
		);

		addShapedComponentRecipe(pack, set + "_wall/" + materialPath, "building",
				new String[]{"XXX", "XXX"},
				Map.of('X', componentIngredient(block, material)),
				wall, material, 6
		);
	}

	private static void addShapedComponentRecipe(
			RuntimeResourcePack pack,
			String recipePath,
			String category,
			String[] pattern,
			Map<Character, JIngredient> keys,
			Item output,
			Identifier material,
			int count
	) {
		var jKeys = JKeys.keys();
		keys.forEach((character, jIngredient) -> jKeys.key(String.valueOf(character), jIngredient));

		var recipe = JRecipe.shaped(
				JPattern.pattern(pattern),
				jKeys,
				componentResult(output, material, count)
		).group(category);
		pack.addRecipe(RAAMaterials.id(recipePath), recipe);
	}

	private static void addShapelessComponentRecipe(
			RuntimeResourcePack pack,
			String recipePath,
			String category,
			List<JIngredient> ingredients,
			Item output,
			Identifier material,
			int count
	) {
		var recipe = JRecipe.shapeless(
				JIngredients.ingredients().addAll(ingredients),
				componentResult(output, material, count)
		).group(category);
		pack.addRecipe(RAAMaterials.id(recipePath), recipe);
	}

	private static JIngredient componentIngredient(Item item, Identifier material) {
		return JIngredient.fabricComponents(item, components -> materialComponent(material, components));
	}

	private static JIngredient itemIngredient(Item item) {
		return JIngredient.ingredient().item(item);
	}

	private static JResult componentResult(Item item, Identifier material, int count) {
		return JResult.stackedResult(BuiltInRegistries.ITEM.getKey(item), count)
				.components(builder -> materialComponent(material, builder));
	}

	private static void materialComponent(Identifier material, JsonObject jsonObject) {
		jsonObject.addProperty("raa_materials:material", material.toString());
	}

	private static void writeRecipe(RuntimeResourcePack pack, String recipePath, JsonObject recipe) {
		pack.addData(
				RAAMaterials.id("recipe/" + recipePath + ".json"),
				recipe.toString().getBytes(StandardCharsets.UTF_8)
		);
	}

	private static boolean has(MaterialDef def, Form... forms) {
		for (Form form : forms) {
			if (!def.forms().contains(form)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isMetal(MaterialDef def) {
		return def.kind() == MaterialKind.METAL
				|| def.kind() == MaterialKind.ALLOY;
	}

	private static boolean isGem(MaterialDef def) {
		return def.kind() == MaterialKind.GEM;
	}

	private static boolean isCrystal(MaterialDef def) {
		return def.kind() == MaterialKind.CRYSTAL;
	}

	private static boolean isWood(MaterialDef def) {
		return def.kind() == MaterialKind.WOOD;
	}

	private static boolean isStoneLike(MaterialDef def) {
		return def.kind() == MaterialKind.STONE
				|| def.kind() == MaterialKind.VOLCANIC;
	}

	private static boolean isSoilLike(MaterialDef def) {
		return switch (def.kind()) {
			case SOIL, SAND, CLAY, MUD, GRAVEL -> true;
			default -> false;
		};
	}
}