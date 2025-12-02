package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.level.block.WaterloggedTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/** Grate = bars-like connectible lattice; texture/model differentiate it. */
public class ParametricGrateBlock extends WaterloggedTransparentBlock {
    public ParametricGrateBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ParametricBlock.MAT, 0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ParametricBlock.MAT);
    }
}
