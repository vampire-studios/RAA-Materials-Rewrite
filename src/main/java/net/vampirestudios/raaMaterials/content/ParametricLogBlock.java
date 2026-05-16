package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.level.block.Block;
import net.fabricmc.fabric.api.registry.StrippableBlockRegistry;

public class ParametricLogBlock extends ParametricPillarBlock {
    public ParametricLogBlock(Properties properties) {
        super(properties);
    }

    public void linkStripped(Block stripped) {
        StrippableBlockRegistry.register(this, stripped, StrippableBlockRegistry.StrippingTransformer.copyOf(ParametricBlock.MAT));
    }
}
