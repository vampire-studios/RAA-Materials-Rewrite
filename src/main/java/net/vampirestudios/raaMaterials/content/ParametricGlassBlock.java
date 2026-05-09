package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ParametricGlassBlock extends ParametricBlock {
	public ParametricGlassBlock(Properties props) {
		super(props.noOcclusion().sound(SoundType.GLASS));
	}

	@Override
	protected boolean skipRendering(final BlockState state, final BlockState neighborState, final Direction direction) {
		return neighborState.is(this) || super.skipRendering(state, neighborState, direction);
	}

	@Override
	protected int getLightDampening(BlockState state) {
		return 15;
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
	protected boolean propagatesSkylightDown(final BlockState state) {
		return true;
	}
}