package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public final class ParametricSlabBlock extends SlabBlock {
	public ParametricSlabBlock(Properties p) {
		super(p);
		this.registerDefaultState(this.defaultBlockState().setValue(ParametricBlock.MAT, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
		super.createBlockStateDefinition(b);
		b.add(ParametricBlock.MAT);
	}
}
