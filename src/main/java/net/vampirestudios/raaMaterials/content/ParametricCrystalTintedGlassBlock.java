package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;

import java.util.List;

public class ParametricCrystalTintedGlassBlock extends ParametricBlock {
	public ParametricCrystalTintedGlassBlock(Properties props) {
		super(props.noOcclusion().sound(SoundType.GLASS));
	}

	@Override
	protected VoxelShape getVisualShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	protected float getShadeBrightness(final BlockState state, final BlockGetter level, final BlockPos pos) {
		return 1.0F;
	}

	@Override
	protected boolean skipRendering(final BlockState state, final BlockState neighborState, final Direction direction) {
		return neighborState.is(this) || super.skipRendering(state, neighborState, direction);
	}

	@Override
	protected boolean propagatesSkylightDown(final BlockState state) {
		return false;
	}

	@Override
	protected int getLightDampening(final BlockState state) {
		return 15;
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
