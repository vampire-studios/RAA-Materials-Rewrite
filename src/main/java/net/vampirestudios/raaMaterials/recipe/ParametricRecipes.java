package net.vampirestudios.raaMaterials.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
	public static RecipeSerializer<ParametricCraftingRecipe> DOOR_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> TRAPDOOR_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> FENCE_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> FENCE_GATE_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> CHAIN_FROM_ROD;
	public static RecipeSerializer<ParametricCraftingRecipe> LAMP_FROM_BLOCK;
	public static RecipeSerializer<ParametricCraftingRecipe> LANTERN_FROM_LAMP;

	public static RecipeSerializer<ParametricCookingRecipe> SMELT_ORE_TO_INGOT;
	public static RecipeSerializer<ParametricCookingRecipe> BLAST_ORE_TO_INGOT;
	public static RecipeSerializer<ParametricCookingRecipe> SMELT_RAW_TO_INGOT;
	public static RecipeSerializer<ParametricCookingRecipe> BLAST_RAW_TO_INGOT;
	public static RecipeSerializer<ParametricCookingRecipe> SMELT_SAND_TO_GLASS;

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
		DOOR_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.DOOR_FROM_BLOCK);
		TRAPDOOR_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.TRAPDOOR_FROM_BLOCK);
		FENCE_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.FENCE_FROM_BLOCK);
		FENCE_GATE_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.FENCE_GATE_FROM_BLOCK);
		CHAIN_FROM_ROD = registerCrafting(ParametricCraftingRecipe.Kind.CHAIN_FROM_ROD);
		LAMP_FROM_BLOCK = registerCrafting(ParametricCraftingRecipe.Kind.LAMP_FROM_BLOCK);
		LANTERN_FROM_LAMP = registerCrafting(ParametricCraftingRecipe.Kind.LANTERN_FROM_LAMP);

		SMELT_ORE_TO_INGOT = registerCooking(ParametricCookingRecipe.Kind.SMELT_ORE_TO_INGOT);
		BLAST_ORE_TO_INGOT = registerCooking(ParametricCookingRecipe.Kind.BLAST_ORE_TO_INGOT);
		SMELT_RAW_TO_INGOT = registerCooking(ParametricCookingRecipe.Kind.SMELT_RAW_TO_INGOT);
		BLAST_RAW_TO_INGOT = registerCooking(ParametricCookingRecipe.Kind.BLAST_RAW_TO_INGOT);
		SMELT_SAND_TO_GLASS = registerCooking(ParametricCookingRecipe.Kind.SMELT_SAND_TO_GLASS);
	}

	private static RecipeSerializer<ParametricCraftingRecipe> registerCrafting(ParametricCraftingRecipe.Kind kind) {
		var recipe = new ParametricCraftingRecipe(kind);
		var serializer = new RecipeSerializer<>(
				MapCodec.unit(recipe),
				StreamCodec.<RegistryFriendlyByteBuf, ParametricCraftingRecipe>unit(recipe)
		);
		return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, RAAMaterials.id(kind.id()), serializer);
	}

	private static RecipeSerializer<ParametricCookingRecipe> registerCooking(ParametricCookingRecipe.Kind kind) {
		var recipe = new ParametricCookingRecipe(kind);
		var serializer = new RecipeSerializer<>(
				MapCodec.unit(recipe),
				StreamCodec.<RegistryFriendlyByteBuf, ParametricCookingRecipe>unit(recipe)
		);
		return Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, RAAMaterials.id(kind.id()), serializer);
	}

	private ParametricRecipes() {
	}
}
