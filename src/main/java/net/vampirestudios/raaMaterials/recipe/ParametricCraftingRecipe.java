package net.vampirestudios.raaMaterials.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.registry.YItems;

import java.util.Optional;

public final class ParametricCraftingRecipe extends CustomRecipe {
	private final Kind kind;

	public ParametricCraftingRecipe(Kind kind) {
		this.kind = kind;
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		if (!matchesShape(input)) return false;

		var material = ParametricRecipeUtil.firstMaterial(input);
		return material.isPresent()
				&& ParametricRecipeUtil.hasForm(level, material.get(), kind.inputForm)
				&& (kind.extraInputForm == null || ParametricRecipeUtil.hasForm(level, material.get(), kind.extraInputForm))
				&& ParametricRecipeUtil.hasForm(level, material.get(), kind.outputForm)
				&& kind.family.matches(level, material.get());
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		return ParametricRecipeUtil.firstMaterial(input)
				.map(material -> ParametricRecipeUtil.stack(kind.output(), material, kind.outputCount))
				.orElse(ItemStack.EMPTY);
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
		return CraftingRecipe.defaultCraftingReminder(input);
	}

	@Override
	public RecipeSerializer<? extends CustomRecipe> getSerializer() {
		return switch (kind) {
			case BLOCK_FROM_INGOTS -> ParametricRecipes.BLOCK_FROM_INGOTS;
			case INGOTS_FROM_BLOCK -> ParametricRecipes.INGOTS_FROM_BLOCK;
			case INGOT_FROM_NUGGETS -> ParametricRecipes.INGOT_FROM_NUGGETS;
			case NUGGETS_FROM_INGOT -> ParametricRecipes.NUGGETS_FROM_INGOT;
			case RAW_BLOCK_FROM_RAW -> ParametricRecipes.RAW_BLOCK_FROM_RAW;
			case RAW_FROM_RAW_BLOCK -> ParametricRecipes.RAW_FROM_RAW_BLOCK;
			case SLAB_FROM_BLOCK -> ParametricRecipes.SLAB_FROM_BLOCK;
			case STAIRS_FROM_BLOCK -> ParametricRecipes.STAIRS_FROM_BLOCK;
			case WALL_FROM_BLOCK -> ParametricRecipes.WALL_FROM_BLOCK;
			case SANDSTONE_SLAB_FROM_SANDSTONE -> ParametricRecipes.SANDSTONE_SLAB_FROM_SANDSTONE;
			case SANDSTONE_STAIRS_FROM_SANDSTONE -> ParametricRecipes.SANDSTONE_STAIRS_FROM_SANDSTONE;
			case SANDSTONE_WALL_FROM_SANDSTONE -> ParametricRecipes.SANDSTONE_WALL_FROM_SANDSTONE;
			case BRICK_SLAB_FROM_BRICKS -> ParametricRecipes.BRICK_SLAB_FROM_BRICKS;
			case BRICK_STAIRS_FROM_BRICKS -> ParametricRecipes.BRICK_STAIRS_FROM_BRICKS;
			case BRICK_WALL_FROM_BRICKS -> ParametricRecipes.BRICK_WALL_FROM_BRICKS;
			case POLISHED_SLAB_FROM_POLISHED -> ParametricRecipes.POLISHED_SLAB_FROM_POLISHED;
			case POLISHED_STAIRS_FROM_POLISHED -> ParametricRecipes.POLISHED_STAIRS_FROM_POLISHED;
			case POLISHED_WALL_FROM_POLISHED -> ParametricRecipes.POLISHED_WALL_FROM_POLISHED;
			case METAL_DOOR_FROM_INGOTS -> ParametricRecipes.METAL_DOOR_FROM_INGOTS;
			case WOOD_DOOR_FROM_BLOCKS -> ParametricRecipes.WOOD_DOOR_FROM_BLOCKS;
			case METAL_TRAPDOOR_FROM_INGOTS -> ParametricRecipes.METAL_TRAPDOOR_FROM_INGOTS;
			case WOOD_TRAPDOOR_FROM_BLOCKS -> ParametricRecipes.WOOD_TRAPDOOR_FROM_BLOCKS;
			case METAL_FENCE_FROM_INGOTS -> ParametricRecipes.METAL_FENCE_FROM_INGOTS;
			case WOOD_FENCE_FROM_BLOCKS -> ParametricRecipes.WOOD_FENCE_FROM_BLOCKS;
			case METAL_FENCE_GATE_FROM_INGOTS -> ParametricRecipes.METAL_FENCE_GATE_FROM_INGOTS;
			case WOOD_FENCE_GATE_FROM_BLOCKS -> ParametricRecipes.WOOD_FENCE_GATE_FROM_BLOCKS;
			case CHAIN_FROM_INGOT_AND_NUGGETS -> ParametricRecipes.CHAIN_FROM_INGOT_AND_NUGGETS;
			case LAMP_FROM_BLOCK -> ParametricRecipes.LAMP_FROM_BLOCK;
			case LANTERN_FROM_LAMP_AND_NUGGETS -> ParametricRecipes.LANTERN_FROM_LAMP_AND_NUGGETS;
		};
	}

	private boolean matchesShape(CraftingInput input) {
		return switch (kind) {
			case BLOCK_FROM_INGOTS, INGOT_FROM_NUGGETS, RAW_BLOCK_FROM_RAW ->
					ParametricRecipeUtil.allSameItemAndMaterial(input, kind.input, 9);
			case INGOTS_FROM_BLOCK, NUGGETS_FROM_INGOT, RAW_FROM_RAW_BLOCK ->
					ParametricRecipeUtil.allSameItemAndMaterial(input, kind.input, 1);
			case SLAB_FROM_BLOCK, SANDSTONE_SLAB_FROM_SANDSTONE, BRICK_SLAB_FROM_BRICKS, POLISHED_SLAB_FROM_POLISHED ->
					blockRow(input, kind.input, 3);
			case STAIRS_FROM_BLOCK, SANDSTONE_STAIRS_FROM_SANDSTONE, BRICK_STAIRS_FROM_BRICKS, POLISHED_STAIRS_FROM_POLISHED ->
					stairs(input, kind.input);
			case WALL_FROM_BLOCK, SANDSTONE_WALL_FROM_SANDSTONE, BRICK_WALL_FROM_BRICKS, POLISHED_WALL_FROM_POLISHED ->
					blockRows(input, kind.input, 2, 3);
			case METAL_DOOR_FROM_INGOTS, WOOD_DOOR_FROM_BLOCKS ->
					blockRows(input, kind.input, 3, 2);
			case WOOD_TRAPDOOR_FROM_BLOCKS, METAL_FENCE_FROM_INGOTS, WOOD_FENCE_FROM_BLOCKS ->
					blockRows(input, kind.input, 2, 3);
			case METAL_TRAPDOOR_FROM_INGOTS, METAL_FENCE_GATE_FROM_INGOTS, WOOD_FENCE_GATE_FROM_BLOCKS ->
					blockRows(input, kind.input, 2, 2);
			case CHAIN_FROM_INGOT_AND_NUGGETS ->
					chain(input);
			case LAMP_FROM_BLOCK ->
					ParametricRecipeUtil.allSameItemAndMaterial(input, kind.input, 1);
			case LANTERN_FROM_LAMP_AND_NUGGETS ->
					lantern(input);
		};
	}

	private static boolean blockRow(CraftingInput input, Item item, int width) {
		if (!ParametricRecipeUtil.allSameItemAndMaterial(input, item, width)) return false;
		var box = bounds(input);
		return box.width() == width && box.height() == 1;
	}

	private static boolean blockRows(CraftingInput input, Item item, int height, int width) {
		if (!ParametricRecipeUtil.allSameItemAndMaterial(input, item, width * height)) return false;
		var box = bounds(input);
		return box.width() == width && box.height() == height;
	}

	private static boolean stairs(CraftingInput input, Item item) {
		if (!ParametricRecipeUtil.allSameItemAndMaterial(input, item, 6)) return false;
		var box = bounds(input);
		if (box.width() != 3 || box.height() != 3) return false;

		return matchesStair(input, box, false) || matchesStair(input, box, true);
	}

	private static boolean matchesStair(CraftingInput input, Bounds box, boolean mirrored) {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				int localX = mirrored ? 2 - x : x;
				boolean shouldBeFilled = localX <= y;
				boolean filled = !input.getItem(box.minX + x, box.minY + y).isEmpty();
				if (filled != shouldBeFilled) return false;
			}
		}
		return true;
	}

	private static boolean chain(CraftingInput input) {
		if (input.ingredientCount() != 3) return false;
		var box = bounds(input);
		if (box.width() != 1 || box.height() != 3) return false;

		var top = input.getItem(box.minX, box.minY);
		var middle = input.getItem(box.minX, box.minY + 1);
		var bottom = input.getItem(box.minX, box.minY + 2);

		return top.is(YItems.PARAM_NUGGET)
				&& middle.is(YItems.PARAM_INGOT)
				&& bottom.is(YItems.PARAM_NUGGET)
				&& sameMaterial(top, middle, bottom);
	}

	private static boolean lantern(CraftingInput input) {
		if (input.ingredientCount() != 9) return false;
		var box = bounds(input);
		if (box.width() != 3 || box.height() != 3) return false;

		ItemStack center = input.getItem(box.minX + 1, box.minY + 1);
		if (!center.is(YItems.PARAM_LAMP_ITEM)) return false;

		Optional<net.minecraft.resources.Identifier> material = ParametricRecipeUtil.material(center);
		if (material.isEmpty()) return false;

		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (x == 1 && y == 1) continue;
				ItemStack stack = input.getItem(box.minX + x, box.minY + y);
				if (!stack.is(YItems.PARAM_NUGGET)) return false;
				if (!ParametricRecipeUtil.material(stack).filter(material.get()::equals).isPresent()) return false;
			}
		}

		return true;
	}

	private static boolean sameMaterial(ItemStack first, ItemStack... rest) {
		Optional<net.minecraft.resources.Identifier> material = ParametricRecipeUtil.material(first);
		if (material.isEmpty()) return false;

		for (ItemStack stack : rest) {
			if (!ParametricRecipeUtil.material(stack).filter(material.get()::equals).isPresent()) {
				return false;
			}
		}

		return true;
	}

	private static Bounds bounds(CraftingInput input) {
		int minX = input.width();
		int minY = input.height();
		int maxX = -1;
		int maxY = -1;

		for (int y = 0; y < input.height(); y++) {
			for (int x = 0; x < input.width(); x++) {
				if (input.getItem(x, y).isEmpty()) continue;
				minX = Math.min(minX, x);
				minY = Math.min(minY, y);
				maxX = Math.max(maxX, x);
				maxY = Math.max(maxY, y);
			}
		}

		if (maxX < minX || maxY < minY) return new Bounds(0, 0, 0, 0);
		return new Bounds(minX, minY, maxX - minX + 1, maxY - minY + 1);
	}

	private record Bounds(int minX, int minY, int width, int height) {
	}

	public enum Kind {
		BLOCK_FROM_INGOTS("block_from_ingots", Form.INGOT, Form.BLOCK, YItems.PARAM_INGOT, YItems.PARAM_BLOCK_ITEM, 1),
		INGOTS_FROM_BLOCK("ingots_from_block", Form.BLOCK, Form.INGOT, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_INGOT, 9),
		INGOT_FROM_NUGGETS("ingot_from_nuggets", Form.NUGGET, Form.INGOT, YItems.PARAM_NUGGET, YItems.PARAM_INGOT, 1),
		NUGGETS_FROM_INGOT("nuggets_from_ingot", Form.INGOT, Form.NUGGET, YItems.PARAM_INGOT, YItems.PARAM_NUGGET, 9),
		RAW_BLOCK_FROM_RAW("raw_block_from_raw", Form.RAW, Form.RAW_BLOCK, YItems.PARAM_RAW, YItems.PARAM_RAW_BLOCK_ITEM, 1),
		RAW_FROM_RAW_BLOCK("raw_from_raw_block", Form.RAW_BLOCK, Form.RAW, YItems.PARAM_RAW_BLOCK_ITEM, YItems.PARAM_RAW, 9),
		SLAB_FROM_BLOCK("slab_from_block", Form.BLOCK, Form.SLAB, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_SLAB_ITEM, 6),
		STAIRS_FROM_BLOCK("stairs_from_block", Form.BLOCK, Form.STAIRS, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_STAIRS_ITEM, 4),
		WALL_FROM_BLOCK("wall_from_block", Form.BLOCK, Form.WALL, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_WALL_ITEM, 6),
		SANDSTONE_SLAB_FROM_SANDSTONE("sandstone_slab_from_sandstone", Form.SANDSTONE, Form.SLAB, YItems.PARAM_SANDSTONE_BLOCK_ITEM, YItems.PARAM_SANDSTONE_SLAB_ITEM, 6),
		SANDSTONE_STAIRS_FROM_SANDSTONE("sandstone_stairs_from_sandstone", Form.SANDSTONE, Form.STAIRS, YItems.PARAM_SANDSTONE_BLOCK_ITEM, YItems.PARAM_SANDSTONE_STAIRS_ITEM, 4),
		SANDSTONE_WALL_FROM_SANDSTONE("sandstone_wall_from_sandstone", Form.SANDSTONE, Form.WALL, YItems.PARAM_SANDSTONE_BLOCK_ITEM, YItems.PARAM_SANDSTONE_WALL_ITEM, 6),
		BRICK_SLAB_FROM_BRICKS("brick_slab_from_bricks", Form.BRICKS, Form.SLAB, YItems.PARAM_BRICKS_BLOCK_ITEM, YItems.PARAM_BRICK_SLAB_ITEM, 6),
		BRICK_STAIRS_FROM_BRICKS("brick_stairs_from_bricks", Form.BRICKS, Form.STAIRS, YItems.PARAM_BRICKS_BLOCK_ITEM, YItems.PARAM_BRICK_STAIRS_ITEM, 4),
		BRICK_WALL_FROM_BRICKS("brick_wall_from_bricks", Form.BRICKS, Form.WALL, YItems.PARAM_BRICKS_BLOCK_ITEM, YItems.PARAM_BRICK_WALL_ITEM, 6),
		POLISHED_SLAB_FROM_POLISHED("polished_slab_from_polished", Form.POLISHED, Form.SLAB, YItems.PARAM_POLISHED_BLOCK_ITEM, YItems.PARAM_POLISHED_SLAB_ITEM, 6),
		POLISHED_STAIRS_FROM_POLISHED("polished_stairs_from_polished", Form.POLISHED, Form.STAIRS, YItems.PARAM_POLISHED_BLOCK_ITEM, YItems.PARAM_POLISHED_STAIRS_ITEM, 4),
		POLISHED_WALL_FROM_POLISHED("polished_wall_from_polished", Form.POLISHED, Form.WALL, YItems.PARAM_POLISHED_BLOCK_ITEM, YItems.PARAM_POLISHED_WALL_ITEM, 6),
		METAL_DOOR_FROM_INGOTS("metal_door_from_ingots", Form.INGOT, Form.DOOR, YItems.PARAM_INGOT, YItems.PARAM_DOOR_METAL_ITEM, 3, Family.METAL_LIKE),
		WOOD_DOOR_FROM_BLOCKS("wood_door_from_blocks", Form.BLOCK, Form.DOOR, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_DOOR_WOOD_ITEM, 3, Family.WOOD),
		METAL_TRAPDOOR_FROM_INGOTS("metal_trapdoor_from_ingots", Form.INGOT, Form.TRAPDOOR, YItems.PARAM_INGOT, YItems.PARAM_TRAPDOOR_METAL_ITEM, 1, Family.METAL_LIKE),
		WOOD_TRAPDOOR_FROM_BLOCKS("wood_trapdoor_from_blocks", Form.BLOCK, Form.TRAPDOOR, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_TRAPDOOR_WOOD_ITEM, 2, Family.WOOD),
		METAL_FENCE_FROM_INGOTS("metal_fence_from_ingots", Form.INGOT, Form.FENCE, YItems.PARAM_INGOT, YItems.PARAM_FENCE_ITEM, 3, Family.METAL_LIKE),
		WOOD_FENCE_FROM_BLOCKS("wood_fence_from_blocks", Form.BLOCK, Form.FENCE, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_FENCE_ITEM, 3, Family.WOOD),
		METAL_FENCE_GATE_FROM_INGOTS("metal_fence_gate_from_ingots", Form.INGOT, Form.FENCE_GATE, YItems.PARAM_INGOT, YItems.PARAM_FENCE_GATE_ITEM, 1, Family.METAL_LIKE),
		WOOD_FENCE_GATE_FROM_BLOCKS("wood_fence_gate_from_blocks", Form.BLOCK, Form.FENCE_GATE, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_FENCE_GATE_ITEM, 1, Family.WOOD),
		CHAIN_FROM_INGOT_AND_NUGGETS("chain_from_ingot_and_nuggets", Form.INGOT, Form.CHAIN, YItems.PARAM_INGOT, YItems.PARAM_CHAIN_ITEM, 1, Family.METAL_LIKE, Form.NUGGET),
		LAMP_FROM_BLOCK("lamp_from_block", Form.BLOCK, Form.LAMP, YItems.PARAM_BLOCK_ITEM, YItems.PARAM_LAMP_ITEM, 1),
		LANTERN_FROM_LAMP_AND_NUGGETS("lantern_from_lamp_and_nuggets", Form.LAMP, Form.LANTERN, YItems.PARAM_LAMP_ITEM, YItems.PARAM_LANTERN_ITEM, 1, Family.METAL_LIKE, Form.NUGGET);

		private final String id;
		private final Form inputForm;
		private final Form extraInputForm;
		private final Form outputForm;
		private final Item input;
		private final Item output;
		private final int outputCount;
		private final Family family;

		Kind(String id, Form inputForm, Form outputForm, Item input, Item output, int outputCount) {
			this(id, inputForm, outputForm, input, output, outputCount, Family.ANY);
		}

		Kind(String id, Form inputForm, Form outputForm, Item input, Item output, int outputCount, Family family) {
			this(id, inputForm, outputForm, input, output, outputCount, family, null);
		}

		Kind(String id, Form inputForm, Form outputForm, Item input, Item output, int outputCount, Family family, Form extraInputForm) {
			this.id = id;
			this.inputForm = inputForm;
			this.extraInputForm = extraInputForm;
			this.outputForm = outputForm;
			this.input = input;
			this.output = output;
			this.outputCount = outputCount;
			this.family = family;
		}

		public String id() {
			return id;
		}

		Item output() {
			return output;
		}
	}

	private enum Family {
		ANY,
		METAL_LIKE,
		WOOD;

		boolean matches(Level level, net.minecraft.resources.Identifier material) {
			return switch (this) {
				case ANY -> true;
				case METAL_LIKE ->
						ParametricRecipeUtil.hasKind(level, material, MaterialKind.METAL)
								|| ParametricRecipeUtil.hasKind(level, material, MaterialKind.ALLOY);
				case WOOD -> ParametricRecipeUtil.hasKind(level, material, MaterialKind.WOOD);
			};
		}
	}
}
