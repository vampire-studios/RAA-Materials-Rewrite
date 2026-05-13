package net.vampirestudios.raaMaterials.recipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.registry.YItems;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public final class ParametricStonecuttingRecipe extends SingleItemRecipe {
	private final Kind kind;

	public ParametricStonecuttingRecipe(Kind kind) {
		super(new CommonInfo(true), Ingredient.of(kind.input), new ItemStackTemplate(kind.output));
		this.kind = kind;
	}

	@Override
	public boolean matches(SingleRecipeInput input, @NonNull Level level) {
		ItemStack stack = input.item();

		if (!stack.is(kind.input)) return false;

		Optional<Identifier> material = ParametricRecipeUtil.material(stack);
		return material.isPresent()
				&& ParametricRecipeUtil.hasForm(level, material.get(), kind.inputForm)
				&& ParametricRecipeUtil.hasForm(level, material.get(), kind.outputForm);
	}

	@Override
	public @NonNull ItemStack assemble(SingleRecipeInput input) {
		return ParametricRecipeUtil.material(input.item())
				.map(material -> ParametricRecipeUtil.stack(kind.output, material, kind.outputCount))
				.orElse(ItemStack.EMPTY);
	}

	@Override
	public @NonNull String group() {
		return "";
	}

	@Override
	public @NonNull RecipeSerializer<ParametricStonecuttingRecipe> getSerializer() {
		return switch (kind) {
			case SLAB -> ParametricRecipes.STONECUTTER_SLAB;
			case STAIRS -> ParametricRecipes.STONECUTTER_STAIRS;
			case WALL -> ParametricRecipes.STONECUTTER_WALL;
			case POLISHED -> ParametricRecipes.STONECUTTER_POLISHED;
			case BRICKS -> ParametricRecipes.STONECUTTER_BRICKS;
			case CUT -> ParametricRecipes.STONECUTTER_CUT;
			case CHISELED -> ParametricRecipes.STONECUTTER_CHISELED;
			case PILLAR -> ParametricRecipes.STONECUTTER_PILLAR;
		};
	}

	@Override
	public @NonNull RecipeType<? extends SingleItemRecipe> getType() {
		return RecipeType.STONECUTTING;
	}

	@Override
	public @NonNull RecipeBookCategory recipeBookCategory() {
		return RecipeBookCategories.STONECUTTER;
	}

	public enum Kind {
		SLAB("slab", Form.BLOCK, Form.SLAB, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_SLAB_ITEM, 2),
		STAIRS("stairs", Form.BLOCK, Form.STAIRS, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_STAIRS_ITEM, 1),
		WALL("wall", Form.BLOCK, Form.WALL, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_WALL_ITEM, 1),
		POLISHED("polished", Form.BLOCK, Form.POLISHED, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_POLISHED_BLOCK_ITEM, 1),
		BRICKS("bricks", Form.BLOCK, Form.BRICKS, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_BRICKS_BLOCK_ITEM, 1),
		CUT("cut", Form.BLOCK, Form.CUT, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_CUT_BLOCK_ITEM, 1),
		CHISELED("chiseled", Form.BLOCK, Form.CHISELED, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_CHISELED_BLOCK_ITEM, 1),
		PILLAR("pillar", Form.BLOCK, Form.PILLAR, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_PILLAR_ITEM, 1);

		private final String id;
		private final Form inputForm;
		private final Form outputForm;
		private final Item input;
		private final Item output;
		private final int outputCount;

		Kind(String id, Form inputForm, Form outputForm, Item input, Item output, int outputCount) {
			this.id = id;
			this.inputForm = inputForm;
			this.outputForm = outputForm;
			this.input = input;
			this.output = output;
			this.outputCount = outputCount;
		}

		public String id() {
			return id;
		}
	}
}