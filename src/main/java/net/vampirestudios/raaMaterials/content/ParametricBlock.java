package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.ClientMaterialCache;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;

import java.util.List;

public class ParametricBlock extends Block {
	public static final int MAX_MATERIAL_STATE = 150;
	public static final IntegerProperty MAT = IntegerProperty.create("mat", 0, MAX_MATERIAL_STATE);

	public static int clampMaterialIndex(int idx) {
		return Math.clamp(idx, 0, MAX_MATERIAL_STATE);
	}

	public ParametricBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(MAT, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
		b.add(MAT);
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader view, BlockPos pos, BlockState state, boolean bl) {
		ItemStack stack = new ItemStack(this.asItem());
		int idx = state.getValue(MAT);

		// Resolve the material id from the index
		ClientMaterialCache.byIndex(idx).ifPresent(def -> stack.set(YComponents.MATERIAL, def.nameInformation().id()));

		// Also stamp the exact blockstate so placement preserves MAT even without lookups
		stack.set(net.minecraft.core.component.DataComponents.BLOCK_STATE,
				net.minecraft.world.item.component.BlockItemStateProperties.EMPTY.with(MAT, idx));

		return stack;
	}

	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		List<ItemStack> drops = super.getDrops(state, params);

		int idx = state.getValue(MAT);

		for (ItemStack stack : drops) {
			if (stack.getItem() == this.asItem()) {
				MaterialRegistry.byIndex(params.getLevel(), idx).ifPresent(def ->
						stack.set(YComponents.MATERIAL, def.nameInformation().id())
				);

				stack.set(
						DataComponents.BLOCK_STATE,
						BlockItemStateProperties.EMPTY.with(MAT, idx)
				);
			}
		}

		return drops;
	}
}
