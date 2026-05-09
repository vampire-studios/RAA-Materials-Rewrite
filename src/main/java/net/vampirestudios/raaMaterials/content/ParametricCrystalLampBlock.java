// content/ParametricCrystalLampBlock.java
package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;

import java.util.List;

public class ParametricCrystalLampBlock extends ParametricBlock {
    public ParametricCrystalLampBlock(Properties props) {
        super(props);
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
