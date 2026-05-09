package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;

import java.util.List;

final class ParametricDropHelper {
	static List<ItemStack> stampSelfDrops(Block block, BlockState state, LootParams.Builder params, List<ItemStack> drops) {
		int idx = state.getValue(ParametricBlock.MAT);

		for (ItemStack stack : drops) {
			if (stack.getItem() != block.asItem()) {
				continue;
			}

			MaterialRegistry.byIndex(params.getLevel(), idx).ifPresent(def ->
					stack.set(YComponents.MATERIAL, def.nameInformation().id())
			);

			stack.set(
					DataComponents.BLOCK_STATE,
					BlockItemStateProperties.EMPTY.with(ParametricBlock.MAT, idx)
			);
		}

		return drops;
	}

	private ParametricDropHelper() {
	}
}
