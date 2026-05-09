package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;

import java.util.List;

import static net.vampirestudios.raaMaterials.content.ParametricBlock.MAT;

/** Parametric pressure plate; choose sensitivity via BlockSetType (e.g., STONE/IRON/WOOD). */
public class ParametricMetalPressurePlateBlock extends WeightedPressurePlateBlock {
    public ParametricMetalPressurePlateBlock(Properties properties) {
        super(50, BlockSetType.IRON, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(MAT, 0));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(MAT);
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
