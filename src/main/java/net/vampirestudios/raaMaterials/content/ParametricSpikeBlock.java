package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;
import net.vampirestudios.raaMaterials.material.SpikeGrowthLiquid;

public class ParametricSpikeBlock extends ParametricBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<Direction> VERTICAL_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
    public static final EnumProperty<DripstoneThickness> THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape TIP_UP   = Block.box(5, 0, 5, 11, 11, 11);
    private static final VoxelShape TIP_DOWN = Block.box(5, 5, 5, 11, 16, 11);
    private static final VoxelShape FRUSTUM  = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape MIDDLE   = Block.box(3, 0, 3, 13, 16, 13);
    private static final VoxelShape BASE     = Block.box(2, 0, 2, 14, 16, 14);

    public ParametricSpikeBlock(Properties props) {
        super(props);
        registerDefaultState(defaultBlockState()
                .setValue(VERTICAL_DIRECTION, Direction.UP)
                .setValue(THICKNESS, DripstoneThickness.TIP)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
        super.createBlockStateDefinition(b);
        b.add(VERTICAL_DIRECTION, THICKNESS, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction dir = ctx.getClickedFace() == Direction.DOWN ? Direction.DOWN : Direction.UP;
        boolean waterlogged = ctx.getLevel().getFluidState(ctx.getClickedPos()).is(Fluids.WATER);
        return defaultBlockState()
                .setValue(VERTICAL_DIRECTION, dir)
                .setValue(THICKNESS, DripstoneThickness.TIP)
                .setValue(WATERLOGGED, waterlogged);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction dir = state.getValue(VERTICAL_DIRECTION);
        BlockPos anchor = pos.relative(dir.getOpposite());
        return level.getBlockState(anchor).isFaceSturdy(level, anchor, dir);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduled,
                                     BlockPos pos, Direction d, BlockPos neighborPos, BlockState neighborState,
                                     RandomSource rng) {
        if (state.getValue(WATERLOGGED)) {
            scheduled.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        Direction dir = state.getValue(VERTICAL_DIRECTION);
        if (d == dir.getOpposite() && !state.canSurvive(level, pos)) {
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        if (state.getValue(THICKNESS) != DripstoneThickness.TIP) {
            return;
        }

        int matIdx = state.getValue(MAT);
        var defOpt = MaterialRegistry.byIndex(level, matIdx);
        if (defOpt.isEmpty()) return;

        SpikeGrowthLiquid growthReq = defOpt.get().spikeGrowth();
        Direction dir = state.getValue(VERTICAL_DIRECTION);

        if (!canGrow(growthReq, level, pos, dir)) return;

        BlockPos tipPos = pos.relative(dir);
        if (!level.isEmptyBlock(tipPos)) return;

        // Convert current TIP to FRUSTUM and place new TIP
        level.setBlock(pos, state.setValue(THICKNESS, DripstoneThickness.FRUSTUM), 3);
        level.setBlock(tipPos, state.setValue(THICKNESS, DripstoneThickness.TIP).setValue(WATERLOGGED, false), 3);
    }

    private static boolean canGrow(SpikeGrowthLiquid req, ServerLevel level, BlockPos pos, Direction dir) {
        if (req == SpikeGrowthLiquid.NONE) return true;

        Block liquid = req == SpikeGrowthLiquid.LAVA ? Blocks.LAVA : Blocks.WATER;
        Direction searchDir = dir.getOpposite();
        for (int i = 1; i <= 12; i++) {
            if (level.getBlockState(pos.relative(searchDir, i)).is(liquid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        Direction dir = state.getValue(VERTICAL_DIRECTION);
        return switch (state.getValue(THICKNESS)) {
            case TIP_MERGE, TIP -> dir == Direction.UP ? TIP_UP : TIP_DOWN;
            case FRUSTUM -> FRUSTUM;
            case MIDDLE -> MIDDLE;
            case BASE -> BASE;
        };
    }
}
