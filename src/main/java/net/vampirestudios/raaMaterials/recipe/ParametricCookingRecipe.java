package net.vampirestudios.raaMaterials.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.registry.YItems;

public final class ParametricCookingRecipe extends AbstractCookingRecipe {
	private final Kind kind;

	public ParametricCookingRecipe(Kind kind) {
		super(
				new CommonInfo(true),
				new CookingBookInfo(CookingBookCategory.MISC, "raa_materials"),
				Ingredient.of(kind.input()),
				new ItemStackTemplate(kind.output()),
				kind.experience,
				kind.cookingTime
		);
		this.kind = kind;
	}

	@Override
	public boolean matches(SingleRecipeInput input, Level level) {
		var stack = input.item();
		if (stack.isEmpty() || !stack.is(kind.input())) return false;
		var material = ParametricRecipeUtil.material(stack);
		return material.isPresent()
				&& ParametricRecipeUtil.hasForm(level, material.get(), kind.inputForm)
				&& ParametricRecipeUtil.hasForm(level, material.get(), kind.outputForm)
				&& (kind.requiredKind == null || ParametricRecipeUtil.hasKind(level, material.get(), kind.requiredKind));
	}

	@Override
	public ItemStack assemble(SingleRecipeInput input) {
		return ParametricRecipeUtil.material(input.item())
				.map(material -> ParametricRecipeUtil.stack(kind.output(), material, 1))
				.orElse(ItemStack.EMPTY);
	}

	@Override
	public RecipeSerializer<? extends AbstractCookingRecipe> getSerializer() {
		return switch (kind) {
			case SMELT_ORE_TO_INGOT -> ParametricRecipes.SMELT_ORE_TO_INGOT;
			case BLAST_ORE_TO_INGOT -> ParametricRecipes.BLAST_ORE_TO_INGOT;
			case SMELT_RAW_TO_INGOT -> ParametricRecipes.SMELT_RAW_TO_INGOT;
			case BLAST_RAW_TO_INGOT -> ParametricRecipes.BLAST_RAW_TO_INGOT;
			case SMELT_SAND_TO_GLASS -> ParametricRecipes.SMELT_SAND_TO_GLASS;
		};
	}

	@Override
	public RecipeType<? extends AbstractCookingRecipe> getType() {
		return kind.blasting ? RecipeType.BLASTING : RecipeType.SMELTING;
	}

	@Override
	public RecipeBookCategory recipeBookCategory() {
		return kind.blasting ? RecipeBookCategories.BLAST_FURNACE_MISC : RecipeBookCategories.FURNACE_MISC;
	}

	@Override
	protected Item furnaceIcon() {
		return kind.blasting ? Items.BLAST_FURNACE : Items.FURNACE;
	}

	public enum Kind {
		SMELT_ORE_TO_INGOT("smelt_ore_to_ingot", Form.ORE, YItems.PARAM_ORE_ITEM, Form.INGOT, YItems.PARAM_INGOT, false, 0.7f, 200, null),
		BLAST_ORE_TO_INGOT("blast_ore_to_ingot", Form.ORE, YItems.PARAM_ORE_ITEM, Form.INGOT, YItems.PARAM_INGOT, true, 0.7f, 100, null),
		SMELT_RAW_TO_INGOT("smelt_raw_to_ingot", Form.RAW, YItems.PARAM_RAW, Form.INGOT, YItems.PARAM_INGOT, false, 0.7f, 200, null),
		BLAST_RAW_TO_INGOT("blast_raw_to_ingot", Form.RAW, YItems.PARAM_RAW, Form.INGOT, YItems.PARAM_INGOT, true, 0.7f, 100, null),
		SMELT_SAND_TO_GLASS("smelt_sand_to_glass", Form.BLOCK, YItems.PARAM_BLOCK_ITEM, Form.GLASS, YItems.PARAM_GLASS_ITEM, false, 0.1f, 200, MaterialKind.SAND);

		private final String id;
		private final Form inputForm;
		private final Item input;
		private final Form outputForm;
		private final Item output;
		private final boolean blasting;
		private final float experience;
		private final int cookingTime;
		private final MaterialKind requiredKind;

		Kind(String id, Form inputForm, Item input, Form outputForm, Item output, boolean blasting, float experience, int cookingTime, MaterialKind requiredKind) {
			this.id = id;
			this.inputForm = inputForm;
			this.input = input;
			this.outputForm = outputForm;
			this.output = output;
			this.blasting = blasting;
			this.experience = experience;
			this.cookingTime = cookingTime;
			this.requiredKind = requiredKind;
		}

		public String id() {
			return id;
		}

		Item input() {
			return input;
		}

		Item output() {
			return output;
		}
	}
}
