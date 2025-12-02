// content/ParametricCrystalClusterBlock.java
package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;

public class ParametricCrystalClusterBlock extends ParametricBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    // A bit larger than buds; tweak sizes to your model
    private static final VoxelShape UP    = Block.box(5, 0, 5, 11, 16, 11);
    private static final VoxelShape DOWN  = Block.box(5, 0, 5, 11, 16, 11);
    private static final VoxelShape NORTH = Block.box(5, 5, 0, 11, 11, 16);
    private static final VoxelShape SOUTH = Block.box(5, 5, 0, 11, 11, 16);
    private static final VoxelShape WEST  = Block.box(0, 5, 5, 16, 11, 11);
    private static final VoxelShape EAST  = Block.box(0, 5, 5, 16, 11, 11);

    public ParametricCrystalClusterBlock(Properties props) {
        super(props.noOcclusion().sound(SoundType.AMETHYST_CLUSTER));
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.UP));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(FACING);
    }

    @Override public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getClickedFace());
    }

    @Override public VoxelShape getShape(BlockState s, BlockGetter g, net.minecraft.core.BlockPos p, CollisionContext c) {
        return switch (s.getValue(FACING)) {
            case DOWN -> DOWN; case UP -> UP; case NORTH -> NORTH; case SOUTH -> SOUTH; case WEST -> WEST; case EAST -> EAST;
        };
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction d = state.getValue(FACING);
        net.minecraft.core.BlockPos anchor = pos.relative(d.getOpposite());
        return level.getBlockState(anchor).isFaceSturdy(level, anchor, d);
    }

    @Override
    protected BlockState updateShape(BlockState s, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction d, BlockPos blockPos2, BlockState blockState2, RandomSource randomSource) {
        return d.getOpposite() == s.getValue(FACING) && !s.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : s;
    }
}
