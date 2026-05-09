package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.List;

import static net.vampirestudios.raaMaterials.content.ParametricBlock.MAT;

public class ParametricChainBlock extends ChainBlock {
	public ParametricChainBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(MAT, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(MAT);
	}

	@Override
	protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return ParametricBlockStats.destroyProgress(this, state, player, level, pos);
	}

	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		return ParametricDropHelper.stampSelfDrops(this, state, params, super.getDrops(state, params));
	}
}
