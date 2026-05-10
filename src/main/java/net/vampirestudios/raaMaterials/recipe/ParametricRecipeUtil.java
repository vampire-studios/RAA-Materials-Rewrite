package net.vampirestudios.raaMaterials.recipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.content.ParametricToolItem;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;

import java.util.Optional;

final class ParametricRecipeUtil {
	static Optional<Identifier> material(ItemStack stack) {
		var material = stack.get(YComponents.MATERIAL);
		return Optional.ofNullable(material);
	}

	static boolean hasForm(Level level, Identifier material, Form form) {
		if (level == null || level.isClientSide()) return true;
		var set = MaterialRegistry.get(level.dimension());
		if (set == null) return true;
		return set.all().stream()
				.filter(def -> def.nameInformation().id().equals(material))
				.findFirst()
				.map(def -> def.forms().contains(form))
				.orElse(false);
	}

	static boolean hasKind(Level level, Identifier material, MaterialKind kind) {
		if (level == null || level.isClientSide()) return true;
		var set = MaterialRegistry.get(level.dimension());
		if (set == null) return true;
		return set.all().stream()
				.filter(def -> def.nameInformation().id().equals(material))
				.findFirst()
				.map(def -> def.kind() == kind)
				.orElse(false);
	}

	static ItemStack stack(Item item, Identifier material, int count) {
		var out = new ItemStack(item, count);
		out.set(YComponents.MATERIAL, material);
		if (item instanceof ParametricToolItem toolItem) {
			MaterialRegistry.byId(material).ifPresent(def -> toolItem.applyComponents(out, def));
		}
		return out;
	}

	static boolean allSameItemAndMaterial(CraftingInput input, Item item, int count) {
		if (input.ingredientCount() != count) return false;

		Identifier material = null;
		for (int i = 0; i < input.size(); i++) {
			var stack = input.getItem(i);
			if (stack.isEmpty()) continue;
			if (!stack.is(item)) return false;

			var stackMaterial = material(stack);
			if (stackMaterial.isEmpty()) return false;
			if (material == null) {
				material = stackMaterial.get();
			} else if (!material.equals(stackMaterial.get())) {
				return false;
			}
		}
		return material != null;
	}

	static Optional<Identifier> firstMaterial(CraftingInput input) {
		for (int i = 0; i < input.size(); i++) {
			var stack = input.getItem(i);
			if (!stack.isEmpty()) return material(stack);
		}
		return Optional.empty();
	}

	private ParametricRecipeUtil() {
	}
}
