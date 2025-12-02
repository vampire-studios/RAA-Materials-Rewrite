package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public final class ParametricStairsBlock extends StairBlock {
	public ParametricStairsBlock(Properties p) {
		super(Blocks.STONE.defaultBlockState(), p);
		this.registerDefaultState(this.defaultBlockState().setValue(ParametricBlock.MAT, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
		super.createBlockStateDefinition(b);
		b.add(ParametricBlock.MAT);
	}
}
