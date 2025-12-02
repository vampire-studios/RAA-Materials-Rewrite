package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/** Parametric pressure plate; choose sensitivity via BlockSetType (e.g., STONE/IRON/WOOD). */
public class ParametricPressurePlateBlock extends PressurePlateBlock {
    public ParametricPressurePlateBlock(BlockSetType setType, Properties properties) {
        // 1.21 PressurePlateBlock has (Sensitivity, BlockSetType, Properties)
        super(setType, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ParametricBlock.MAT, 0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ParametricBlock.MAT);
    }
}
