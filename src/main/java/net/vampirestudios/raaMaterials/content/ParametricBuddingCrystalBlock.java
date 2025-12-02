// content/ParametricBuddingCrystalBlock.java
package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.vampirestudios.raaMaterials.registry.YBlocks;

public class ParametricBuddingCrystalBlock extends ParametricBlock {
    public ParametricBuddingCrystalBlock(Properties props) {
        super(props.randomTicks().strength(1.5f).sound(SoundType.AMETHYST));
    }

    @Override public void randomTick(BlockState s, ServerLevel level, BlockPos pos, RandomSource r) {
        Direction dir = Direction.getRandom(r);
        BlockPos target = pos.relative(dir);
        BlockState at = level.getBlockState(target);

        if (!at.isAir() && !(at.getBlock() instanceof ParametricCrystalBudBlock) && !(at.getBlock() instanceof ParametricCrystalClusterBlock)) return;

        // Upgrade path: AIR -> SMALL -> MEDIUM -> LARGE -> CLUSTER
        if (at.isAir()) {
            level.setBlock(target,
                YBlocks.PARAM_CRYSTAL_BUD_SMALL.defaultBlockState()
                    .setValue(ParametricCrystalBudBlock.FACING, dir),
                Block.UPDATE_CLIENTS);
        } else if (at.getBlock() instanceof ParametricCrystalBudBlock bud) {
            var size = at.getValue(ParametricCrystalBudBlock.SIZE);
            BlockState next = null;
            if (size == ParametricCrystalBudBlock.Size.SMALL)  next = YBlocks.PARAM_CRYSTAL_BUD_MEDIUM.defaultBlockState();
            else if (size == ParametricCrystalBudBlock.Size.MEDIUM) next = YBlocks.PARAM_CRYSTAL_BUD_LARGE.defaultBlockState();
            else next = YBlocks.PARAM_CLUSTER.defaultBlockState();

            level.setBlock(target, next.setValue(ParametricCrystalBudBlock.FACING, at.getValue(ParametricCrystalBudBlock.FACING)),
                Block.UPDATE_CLIENTS);
        }
    }
}
