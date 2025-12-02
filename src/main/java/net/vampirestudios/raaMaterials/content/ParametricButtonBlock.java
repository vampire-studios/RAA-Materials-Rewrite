package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/**
 * Parametric button; choose the set type in registry (e.g., BlockSetType.STONE / BlockSetType.IRON).
 * Duration in ticks is the vanilla-style press duration.
 */
public class ParametricButtonBlock extends ButtonBlock {
	public ParametricButtonBlock(BlockSetType setType, int ticks, Properties properties) {
		super(setType, ticks, properties);
		this.registerDefaultState(this.defaultBlockState().setValue(ParametricBlock.MAT, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(ParametricBlock.MAT);
	}
}
