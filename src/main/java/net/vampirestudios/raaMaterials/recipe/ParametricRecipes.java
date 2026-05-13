package net.vampirestudios.raaMaterials.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.vampirestudios.raaMaterials.RAAMaterials;

public final class ParametricRecipes {
	public static RecipeSerializer<ParametricCraftingRecipe> BLOCK_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> INGOTS_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> INGOT_FROM_NUGGETS;
	public static RecipeSerializer<ParametricCraftingRecipe> NUGGETS_FROM_INGOT;
	public static RecipeSerializer<ParametricCraftingRecipe> RAW_BLOCK_FROM_RAW;
	public static RecipeSerializer<ParametricCraftingRecipe> RAW_FROM_RAW_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> SLAB_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> STAIRS_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> WALL_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> SANDSTONE_SLAB_FROM_SANDSTONE;
	public static RecipeSerializer<ParametricCraftingRecipe> SANDSTONE_STAIRS_FROM_SANDSTONE;
	public static RecipeSerializer<ParametricCraftingRecipe> SANDSTONE_WALL_FROM_SANDSTONE;
	public static RecipeSerializer<ParametricCraftingRecipe> BRICK_SLAB_FROM_BRICKS;
	public static RecipeSerializer<ParametricCraftingRecipe> BRICK_STAIRS_FROM_BRICKS;
	public static RecipeSerializer<ParametricCraftingRecipe> BRICK_WALL_FROM_BRICKS;
	public static RecipeSerializer<ParametricCraftingRecipe> POLISHED_SLAB_FROM_POLISHED;
	public static RecipeSerializer<ParametricCraftingRecipe> POLISHED_STAIRS_FROM_POLISHED;
	public static RecipeSerializer<ParametricCraftingRecipe> POLISHED_WALL_FROM_POLISHED;
	public static RecipeSerializer<ParametricCraftingRecipe> METAL_DOOR_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> WOOD_DOOR_FROM_BLOCKS;
	public static RecipeSerializer<ParametricCraftingRecipe> METAL_TRAPDOOR_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> WOOD_TRAPDOOR_FROM_BLOCKS;
	public static RecipeSerializer<ParametricCraftingRecipe> METAL_FENCE_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> WOOD_FENCE_FROM_BLOCKS;
	public static RecipeSerializer<ParametricCraftingRecipe> METAL_FENCE_GATE_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> WOOD_FENCE_GATE_FROM_BLOCKS;
	public static RecipeSerializer<ParametricCraftingRecipe> CHAIN_FROM_INGOT_AND_NUGGETS;
	public static RecipeSerializer<ParametricCraftingRecipe> LANTERN_FROM_TORCH_AND_NUGGETS;

	public static RecipeSerializer<ParametricCookingRecipe> SMELT_ORE_TO_INGOT;
	public static RecipeSerializer<ParametricCookingRecipe> BLAST_ORE_TO_INGOT;
	public static RecipeSerializer<ParametricCookingRecipe> SMELT_RAW_TO_INGOT;
	public static RecipeSerializer<ParametricCookingRecipe> BLAST_RAW_TO_INGOT;
	public static RecipeSerializer<ParametricCookingRecipe> SMELT_SAND_TO_GLASS;

	public static RecipeSerializer<ParametricCraftingRecipe> PICKAXE_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> AXE_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> SHOVEL_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> HOE_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> SWORD_FROM_INGOTS;

	public static RecipeSerializer<ParametricCraftingRecipe> POLISHED_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> BRICKS_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> CUT_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> CHISELED_FROM_SLABS;
	public static RecipeSerializer<ParametricCraftingRecipe> PILLAR_FROM_BLOCKS;

	public static RecipeSerializer<ParametricCraftingRecipe> STONE_BUTTON_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> WOOD_BUTTON_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> METAL_BUTTON_FROM_INGOT;

	public static RecipeSerializer<ParametricCraftingRecipe> STONE_PRESSURE_PLATE_FROM_BLOCKS;
	public static RecipeSerializer<ParametricCraftingRecipe> WOOD_PRESSURE_PLATE_FROM_BLOCKS;
	public static RecipeSerializer<ParametricCraftingRecipe> METAL_PRESSURE_PLATE_FROM_INGOTS;

	public static RecipeSerializer<ParametricCraftingRecipe> PANE_FROM_GLASS;
	public static RecipeSerializer<ParametricCraftingRecipe> SHEETS_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> PLATE_BLOCK_FROM_SHEETS;
	public static RecipeSerializer<ParametricCraftingRecipe> BARS_FROM_INGOTS;
	public static RecipeSerializer<ParametricCraftingRecipe> GRATE_FROM_INGOTS;

	public static RecipeSerializer<ParametricStonecuttingRecipe> STONECUTTER_SLAB;
	public static RecipeSerializer<ParametricStonecuttingRecipe> STONECUTTER_STAIRS;
	public static RecipeSerializer<ParametricStonecuttingRecipe> STONECUTTER_WALL;
	public static RecipeSerializer<ParametricStonecuttingRecipe> STONECUTTER_POLISHED;
	public static RecipeSerializer<ParametricStonecuttingRecipe> STONECUTTER_BRICKS;
	public static RecipeSerializer<ParametricStonecuttingRecipe> STONECUTTER_CUT;
	public static RecipeSerializer<ParametricStonecuttingRecipe> STONECUTTER_CHISELED;
	public static RecipeSerializer<ParametricStonecuttingRecipe> STONECUTTER_PILLAR;

	public static void init() {
		BLOCK_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.BLOCK_FROM_INGOTS);
		INGOTS_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.INGOTS_FROM_BLOCK);
		INGOT_FROM_NUGGETS = registerCrafting(ParametricCraftingRecipe.Kind.INGOT_FROM_NUGGETS);
		NUGGETS_FROM_INGOT = registerCrafting(ParametricCraftingRecipe.Kind.NUGGETS_FROM_INGOT);
		RAW_BLOCK_FROM_RAW = registerCrafting(ParametricCraftingRecipe.Kind.RAW_BLOCK_FROM_RAW);
		RAW_FROM_RAW_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.RAW_FROM_RAW_BLOCK);
		SLAB_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.SLAB_FROM_BLOCK);
		STAIRS_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.STAIRS_FROM_BLOCK);
		WALL_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.WALL_FROM_BLOCK);
		SANDSTONE_SLAB_FROM_SANDSTONE = registerCrafting(ParametricCraftingRecipe.Kind.SANDSTONE_SLAB_FROM_SANDSTONE);
		SANDSTONE_STAIRS_FROM_SANDSTONE = registerCrafting(ParametricCraftingRecipe.Kind.SANDSTONE_STAIRS_FROM_SANDSTONE);
		SANDSTONE_WALL_FROM_SANDSTONE = registerCrafting(ParametricCraftingRecipe.Kind.SANDSTONE_WALL_FROM_SANDSTONE);
		BRICK_SLAB_FROM_BRICKS = registerCrafting(ParametricCraftingRecipe.Kind.BRICK_SLAB_FROM_BRICKS);
		BRICK_STAIRS_FROM_BRICKS = registerCrafting(ParametricCraftingRecipe.Kind.BRICK_STAIRS_FROM_BRICKS);
		BRICK_WALL_FROM_BRICKS = registerCrafting(ParametricCraftingRecipe.Kind.BRICK_WALL_FROM_BRICKS);
		POLISHED_SLAB_FROM_POLISHED = registerCrafting(ParametricCraftingRecipe.Kind.POLISHED_SLAB_FROM_POLISHED);
		POLISHED_STAIRS_FROM_POLISHED = registerCrafting(ParametricCraftingRecipe.Kind.POLISHED_STAIRS_FROM_POLISHED);
		POLISHED_WALL_FROM_POLISHED = registerCrafting(ParametricCraftingRecipe.Kind.POLISHED_WALL_FROM_POLISHED);
		METAL_DOOR_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.METAL_DOOR_FROM_INGOTS);
		WOOD_DOOR_FROM_BLOCKS = registerCrafting(ParametricCraftingRecipe.Kind.WOOD_DOOR_FROM_BLOCKS);
		METAL_TRAPDOOR_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.METAL_TRAPDOOR_FROM_INGOTS);
		WOOD_TRAPDOOR_FROM_BLOCKS = registerCrafting(ParametricCraftingRecipe.Kind.WOOD_TRAPDOOR_FROM_BLOCKS);
		METAL_FENCE_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.METAL_FENCE_FROM_INGOTS);
		WOOD_FENCE_FROM_BLOCKS = registerCrafting(ParametricCraftingRecipe.Kind.WOOD_FENCE_FROM_BLOCKS);
		METAL_FENCE_GATE_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.METAL_FENCE_GATE_FROM_INGOTS);
		WOOD_FENCE_GATE_FROM_BLOCKS = registerCrafting(ParametricCraftingRecipe.Kind.WOOD_FENCE_GATE_FROM_BLOCKS);
		CHAIN_FROM_INGOT_AND_NUGGETS = registerCrafting(ParametricCraftingRecipe.Kind.CHAIN_FROM_INGOT_AND_NUGGETS);
		LANTERN_FROM_TORCH_AND_NUGGETS = registerCrafting(ParametricCraftingRecipe.Kind.LANTERN_FROM_TORCH_AND_NUGGETS);

		SMELT_ORE_TO_INGOT = registerCooking(ParametricCookingRecipe.Kind.SMELT_ORE_TO_INGOT);
		BLAST_ORE_TO_INGOT = registerCooking(ParametricCookingRecipe.Kind.BLAST_ORE_TO_INGOT);
		SMELT_RAW_TO_INGOT = registerCooking(ParametricCookingRecipe.Kind.SMELT_RAW_TO_INGOT);
		BLAST_RAW_TO_INGOT = registerCooking(ParametricCookingRecipe.Kind.BLAST_RAW_TO_INGOT);
		SMELT_SAND_TO_GLASS = registerCooking(ParametricCookingRecipe.Kind.SMELT_SAND_TO_GLASS);

		PICKAXE_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.PICKAXE_FROM_INGOTS);
		AXE_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.AXE_FROM_INGOTS);
		SHOVEL_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.SHOVEL_FROM_INGOTS);
		HOE_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.HOE_FROM_INGOTS);
		SWORD_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.SWORD_FROM_INGOTS);

		POLISHED_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.POLISHED_FROM_BLOCK);
		BRICKS_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.BRICKS_FROM_BLOCK);
		CUT_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.CUT_FROM_BLOCK);
		CHISELED_FROM_SLABS = registerCrafting(ParametricCraftingRecipe.Kind.CHISELED_FROM_SLABS);
		PILLAR_FROM_BLOCKS = registerCrafting(ParametricCraftingRecipe.Kind.PILLAR_FROM_BLOCKS);

		STONE_BUTTON_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.STONE_BUTTON_FROM_BLOCK);
		WOOD_BUTTON_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.WOOD_BUTTON_FROM_BLOCK);
		METAL_BUTTON_FROM_INGOT = registerCrafting(ParametricCraftingRecipe.Kind.METAL_BUTTON_FROM_INGOT);

		STONE_PRESSURE_PLATE_FROM_BLOCKS = registerCrafting(ParametricCraftingRecipe.Kind.STONE_PRESSURE_PLATE_FROM_BLOCKS);
		WOOD_PRESSURE_PLATE_FROM_BLOCKS = registerCrafting(ParametricCraftingRecipe.Kind.WOOD_PRESSURE_PLATE_FROM_BLOCKS);
		METAL_PRESSURE_PLATE_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.METAL_PRESSURE_PLATE_FROM_INGOTS);

		PANE_FROM_GLASS = registerCrafting(ParametricCraftingRecipe.Kind.PANE_FROM_GLASS);
		SHEETS_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.SHEETS_FROM_INGOTS);
		PLATE_BLOCK_FROM_SHEETS = registerCrafting(ParametricCraftingRecipe.Kind.PLATE_BLOCK_FROM_SHEETS);
		BARS_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.BARS_FROM_INGOTS);
		GRATE_FROM_INGOTS = registerCrafting(ParametricCraftingRecipe.Kind.GRATE_FROM_BLOCKS);

		STONECUTTER_SLAB = registerStonecutting(ParametricStonecuttingRecipe.Kind.SLAB);
		STONECUTTER_STAIRS = registerStonecutting(ParametricStonecuttingRecipe.Kind.STAIRS);
		STONECUTTER_WALL = registerStonecutting(ParametricStonecuttingRecipe.Kind.WALL);
		STONECUTTER_POLISHED = registerStonecutting(ParametricStonecuttingRecipe.Kind.POLISHED);
		STONECUTTER_BRICKS = registerStonecutting(ParametricStonecuttingRecipe.Kind.BRICKS);
		STONECUTTER_CUT = registerStonecutting(ParametricStonecuttingRecipe.Kind.CUT);
		STONECUTTER_CHISELED = registerStonecutting(ParametricStonecuttingRecipe.Kind.CHISELED);
		STONECUTTER_PILLAR = registerStonecutting(ParametricStonecuttingRecipe.Kind.PILLAR);
	}

	private static RecipeSerializer<ParametricCraftingRecipe> registerCrafting(ParametricCraftingRecipe.Kind kind) {
		var recipe = new ParametricCraftingRecipe(kind);
		var serializer = new RecipeSerializer<>(
				MapCodec.unit(recipe),
				StreamCodec.unit(recipe)
		);
		return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, RAAMaterials.id(kind.id()), serializer);
	}

	private static RecipeSerializer<ParametricCookingRecipe> registerCooking(ParametricCookingRecipe.Kind kind) {
		var recipe = new ParametricCookingRecipe(kind);
		var serializer = new RecipeSerializer<>(
				MapCodec.unit(recipe),
				StreamCodec.unit(recipe)
		);
		return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, RAAMaterials.id(kind.id()), serializer);
	}

	private static RecipeSerializer<ParametricStonecuttingRecipe> registerStonecutting(ParametricStonecuttingRecipe.Kind kind) {
		var recipe = new ParametricStonecuttingRecipe(kind);
		var serializer = new RecipeSerializer<>(
				MapCodec.unit(recipe),
				StreamCodec.unit(recipe)
		);
		return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, RAAMaterials.id("stonecutting_" + kind.id()), serializer);
	}

	private ParametricRecipes() {
	}
}
