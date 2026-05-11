package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;
import net.vampirestudios.raaMaterials.material.SpikeGrowthLiquid;

public class ParametricSpikeBlock extends ParametricBlock implements SimpleWaterloggedBlock, Fallable {
    public static final EnumProperty<Direction> VERTICAL_DIRECTION = BlockStateProperties.VERTICAL_DIRECTION;
    public static final EnumProperty<DripstoneThickness> THICKNESS = BlockStateProperties.DRIPSTONE_THICKNESS;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final double MIN_TRIDENT_VELOCITY_TO_BREAK_SPIKE = 0.6;
    private static final float STALACTITE_DAMAGE_PER_FALL_DISTANCE_AND_SIZE = 1.0F;
    private static final int STALACTITE_MAX_DAMAGE = 40;
    private static final int MAX_STALACTITE_HEIGHT_FOR_DAMAGE_CALCULATION = 6;
    private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778F;
    private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;

    private static final VoxelShape TIP_MERGE = Block.box(5, 0, 5, 11, 16, 11);
    private static final VoxelShape TIP_UP   = Block.box(5, 0, 5, 11, 11, 11);
    private static final VoxelShape TIP_DOWN = Block.box(5, 5, 5, 11, 16, 11);
    private static final VoxelShape FRUSTUM  = Block.box(4, 0, 4, 12, 16, 12);
    private static final VoxelShape MIDDLE   = Block.box(3, 0, 3, 13, 16, 13);
    private static final VoxelShape BASE     = Block.box(2, 0, 2, 14, 16, 14);
    private static final float MAX_HORIZONTAL_OFFSET = (float) BASE.min(Axis.X);

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
        LevelAccessor level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        int matIdx = materialIndexForPlacement(ctx);
        Direction preferredDir = ctx.getNearestLookingVerticalDirection().getOpposite();
        Direction dir = calculateTipDirection(level, pos, preferredDir, matIdx);
        if (dir == null) return null;

        boolean mergeOpposingTips = !ctx.isSecondaryUseActive();
        DripstoneThickness thickness = calculateSpikeThickness(level, pos, dir, matIdx, mergeOpposingTips);
        boolean waterlogged = ctx.getLevel().getFluidState(ctx.getClickedPos()).is(Fluids.WATER);
        return defaultBlockState()
                .setValue(VERTICAL_DIRECTION, dir)
                .setValue(THICKNESS, thickness)
                .setValue(WATERLOGGED, waterlogged);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction dir = state.getValue(VERTICAL_DIRECTION);
        return isValidSpikePlacement(level, pos, dir, state.getValue(MAT));
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduled,
                                     BlockPos pos, Direction d, BlockPos neighborPos, BlockState neighborState,
                                     RandomSource rng) {
        if (state.getValue(WATERLOGGED)) {
            scheduled.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        Direction dir = state.getValue(VERTICAL_DIRECTION);
        if (d != Direction.UP && d != Direction.DOWN) {
            return state;
        }
        if (d == dir.getOpposite() && !state.canSurvive(level, pos)) {
            scheduled.scheduleTick(pos, this, dir == Direction.DOWN ? 2 : 1);
            return state;
        }
        boolean mergeOpposingTips = state.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
        return state.setValue(THICKNESS, calculateSpikeThickness(level, pos, dir, state.getValue(MAT), mergeOpposingTips));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (isStalagmite(state) && !state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        } else {
            spawnFallingStalactite(state, level, pos);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
        if (rng.nextFloat() >= GROWTH_PROBABILITY_PER_RANDOM_TICK) {
            return;
        }
        if (!isTip(state, false)) {
            return;
        }

        int matIdx = state.getValue(MAT);
        var defOpt = MaterialRegistry.byIndex(level, matIdx);
        if (defOpt.isEmpty()) return;

        SpikeGrowthLiquid growthReq = defOpt.get().spikeGrowth();
        Direction dir = state.getValue(VERTICAL_DIRECTION);

        if (!canGrow(growthReq, level, pos, dir)) return;
        if (columnHeight(level, pos, dir, matIdx) >= RAAConfig.active().formChances().spikeMaxHeight()) return;

        if (dir == Direction.DOWN && rng.nextBoolean()) {
            growStalagmiteBelow(level, pos, matIdx);
        } else {
            grow(level, pos, dir, matIdx);
        }
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

    private int materialIndexForPlacement(BlockPlaceContext ctx) {
        var stack = ctx.getItemInHand();
        var props = stack.get(DataComponents.BLOCK_STATE);
        if (props != null && props.get(MAT) != null) {
            return clampMaterialIndex(props.get(MAT));
        }

        var materialId = stack.get(YComponents.MATERIAL);
        if (materialId != null && ctx.getLevel() instanceof ServerLevel serverLevel) {
            return MaterialRegistry.indexOf(serverLevel, materialId)
                    .map(ParametricBlock::clampMaterialIndex)
                    .orElse(0);
        }

        return 0;
    }

    private Direction calculateTipDirection(LevelReader level, BlockPos pos, Direction preferredDir, int matIdx) {
        if (isValidSpikePlacement(level, pos, preferredDir, matIdx)) {
            return preferredDir;
        }
        Direction oppositeDir = preferredDir.getOpposite();
        return isValidSpikePlacement(level, pos, oppositeDir, matIdx) ? oppositeDir : null;
    }

    private static boolean isValidSpikePlacement(LevelReader level, BlockPos pos, Direction dir, int matIdx) {
        BlockPos anchor = pos.relative(dir.getOpposite());
        BlockState anchorState = level.getBlockState(anchor);
        return anchorState.isFaceSturdy(level, anchor, dir) || isSameSpikeSegment(anchorState, dir, matIdx);
    }

    private static DripstoneThickness calculateSpikeThickness(
            LevelReader level,
            BlockPos pos,
            Direction dir,
            int matIdx,
            boolean mergeOpposingTips
    ) {
        Direction baseDir = dir.getOpposite();
        BlockState inFrontState = level.getBlockState(pos.relative(dir));
        if (isSameSpikeSegment(inFrontState, baseDir, matIdx)) {
            return !mergeOpposingTips && inFrontState.getValue(THICKNESS) != DripstoneThickness.TIP_MERGE
                    ? DripstoneThickness.TIP
                    : DripstoneThickness.TIP_MERGE;
        }
        if (!isSameSpikeSegment(inFrontState, dir, matIdx)) {
            return DripstoneThickness.TIP;
        }

        DripstoneThickness inFrontThickness = inFrontState.getValue(THICKNESS);
        if (inFrontThickness == DripstoneThickness.TIP || inFrontThickness == DripstoneThickness.TIP_MERGE) {
            return DripstoneThickness.FRUSTUM;
        }

        BlockState behindState = level.getBlockState(pos.relative(baseDir));
        return !isSameSpikeSegment(behindState, dir, matIdx) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
    }

    private void grow(ServerLevel level, BlockPos growFromPos, Direction growDir, int matIdx) {
        BlockPos targetPos = growFromPos.relative(growDir);
        BlockState targetState = level.getBlockState(targetPos);
        if (isUnmergedTipWithDirection(targetState, growDir.getOpposite(), matIdx)) {
            createMergedTips(level, targetPos, matIdx);
        } else if (targetState.isAir() && targetState.getFluidState().isEmpty()) {
            createSpike(level, targetPos, growDir, DripstoneThickness.TIP, matIdx);
        } else {
            return;
        }

        updateColumnContaining(level, growFromPos);
        updateColumnContaining(level, targetPos);
    }

    private void growStalagmiteBelow(ServerLevel level, BlockPos posAboveStalagmite, int matIdx) {
        MutableBlockPos pos = posAboveStalagmite.mutable();

        for (int i = 0; i < MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING; i++) {
            pos.move(Direction.DOWN);
            BlockState state = level.getBlockState(pos);
            if (!state.getFluidState().isEmpty()) {
                return;
            }

            if (isUnmergedTipWithDirection(state, Direction.UP, matIdx) && canTipGrow(state, level, pos)) {
                grow(level, pos, Direction.UP, matIdx);
                return;
            }

            if (isValidSpikePlacement(level, pos, Direction.UP, matIdx) && level.getFluidState(pos.below()).isEmpty()) {
                grow(level, pos.below(), Direction.UP, matIdx);
                return;
            }

            if (!state.isAir() && !isSameSpikeSegment(state, Direction.UP, matIdx)) {
                return;
            }
        }
    }

    private boolean canTipGrow(BlockState tipState, ServerLevel level, BlockPos tipPos) {
        Direction growDir = tipState.getValue(VERTICAL_DIRECTION);
        BlockPos growPos = tipPos.relative(growDir);
        BlockState growState = level.getBlockState(growPos);
        if (!growState.getFluidState().isEmpty()) {
            return false;
        }
        return growState.isAir() || isUnmergedTipWithDirection(growState, growDir.getOpposite(), tipState.getValue(MAT));
    }

    private void createSpike(LevelAccessor level, BlockPos pos, Direction dir, DripstoneThickness thickness, int matIdx) {
        BlockState state = defaultBlockState()
                .setValue(MAT, matIdx)
                .setValue(VERTICAL_DIRECTION, dir)
                .setValue(THICKNESS, thickness)
                .setValue(WATERLOGGED, level.getFluidState(pos).is(Fluids.WATER));
        level.setBlock(pos, state, 3);
    }

    private void createMergedTips(LevelAccessor level, BlockPos tipPos, int matIdx) {
        BlockState tipState = level.getBlockState(tipPos);
        BlockPos stalactitePos;
        BlockPos stalagmitePos;
        if (tipState.getValue(VERTICAL_DIRECTION) == Direction.UP) {
            stalagmitePos = tipPos;
            stalactitePos = tipPos.above();
        } else {
            stalactitePos = tipPos;
            stalagmitePos = tipPos.below();
        }

        createSpike(level, stalactitePos, Direction.DOWN, DripstoneThickness.TIP_MERGE, matIdx);
        createSpike(level, stalagmitePos, Direction.UP, DripstoneThickness.TIP_MERGE, matIdx);
    }

    private static int columnHeight(ServerLevel level, BlockPos tipPos, Direction dir, int matIdx) {
        BlockPos basePos = spikeBasePos(level, tipPos, dir, matIdx);
        int height = 0;
        while (isSameSpikeSegment(level.getBlockState(basePos.relative(dir, height)), dir, matIdx)) {
            height++;
        }
        return height;
    }

    private static void updateColumnThickness(ServerLevel level, BlockPos tipPos, Direction dir, int matIdx) {
        BlockPos basePos = spikeBasePos(level, tipPos, dir, matIdx);

        int height = columnHeight(level, tipPos, dir, matIdx);
        for (int i = 0; i < height; i++) {
            BlockPos segmentPos = basePos.relative(dir, i);
            BlockState segmentState = level.getBlockState(segmentPos);
            boolean mergeOpposingTips = segmentState.getValue(THICKNESS) == DripstoneThickness.TIP_MERGE;
            DripstoneThickness thickness = calculateSpikeThickness(level, segmentPos, dir, matIdx, mergeOpposingTips);
            level.setBlock(segmentPos, segmentState.setValue(THICKNESS, thickness), 3);
        }
    }

    private static void updateColumnContaining(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof ParametricSpikeBlock)) {
            return;
        }
        updateColumnThickness(level, pos, state.getValue(VERTICAL_DIRECTION), state.getValue(MAT));
    }

    private static BlockPos spikeBasePos(ServerLevel level, BlockPos tipPos, Direction dir, int matIdx) {
        BlockPos basePos = tipPos;
        while (isSameSpikeSegment(level.getBlockState(basePos.relative(dir.getOpposite())), dir, matIdx)) {
            basePos = basePos.relative(dir.getOpposite());
        }
        return basePos;
    }

    private static boolean isSameSpikeSegment(BlockState state, Direction dir, int matIdx) {
        return state.getBlock() instanceof ParametricSpikeBlock
                && state.getValue(VERTICAL_DIRECTION) == dir
                && (matIdx < 0 || state.getValue(MAT) == matIdx);
    }

    private static boolean isUnmergedTipWithDirection(BlockState state, Direction dir, int matIdx) {
        return isTip(state, false) && state.getValue(VERTICAL_DIRECTION) == dir && (matIdx < 0 || state.getValue(MAT) == matIdx);
    }

    private static boolean isTip(BlockState state, boolean includeMergedTip) {
        if (!(state.getBlock() instanceof ParametricSpikeBlock)) {
            return false;
        }
        DripstoneThickness thickness = state.getValue(THICKNESS);
        return thickness == DripstoneThickness.TIP || includeMergedTip && thickness == DripstoneThickness.TIP_MERGE;
    }

    private static boolean isStalagmite(BlockState state) {
        return isSameSpikeSegment(state, Direction.UP, -1);
    }

    private static boolean isStalactite(BlockState state) {
        return isSameSpikeSegment(state, Direction.DOWN, -1);
    }

    private static boolean isFreeHangingStalactite(BlockState state) {
        return isStalactite(state) && state.getValue(THICKNESS) == DripstoneThickness.TIP && !state.getValue(WATERLOGGED);
    }

    private static void spawnFallingStalactite(BlockState state, ServerLevel level, BlockPos pos) {
        MutableBlockPos fallPos = pos.mutable();
        BlockState fallState = state;

        while (isStalactite(fallState)) {
            FallingBlockEntity entity = FallingBlockEntity.fall(level, fallPos, fallState);
            if (isTip(fallState, true)) {
                int size = Math.min(1 + pos.getY() - fallPos.getY(), MAX_STALACTITE_HEIGHT_FOR_DAMAGE_CALCULATION);
                float damagePerFallDistance = STALACTITE_DAMAGE_PER_FALL_DISTANCE_AND_SIZE * Math.max(size, 1);
                entity.setHurtsEntities(damagePerFallDistance, STALACTITE_MAX_DAMAGE);
                break;
            }

            fallPos.move(Direction.DOWN);
            fallState = level.getBlockState(fallPos);
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hit, Projectile projectile) {
        if (!level.isClientSide()
                && level instanceof ServerLevel serverLevel
                && projectile.mayInteract(serverLevel, hit.getBlockPos())
                && projectile.mayBreak(serverLevel)
                && projectile instanceof ThrownTrident
                && projectile.getDeltaMovement().length() > MIN_TRIDENT_VELOCITY_TO_BREAK_SPIKE) {
            level.destroyBlock(hit.getBlockPos(), true);
        }
    }

    @Override
    public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity entity) {
        if (!entity.isSilent()) {
            level.levelEvent(LevelEvent.SOUND_POINTED_DRIPSTONE_LAND, pos, 0);
        }
    }

    @Override
    public DamageSource getFallDamageSource(Entity entity) {
        return entity.damageSources().fallingStalactite(entity);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext ctx) {
        Direction dir = state.getValue(VERTICAL_DIRECTION);
        VoxelShape shape = switch (state.getValue(THICKNESS)) {
            case TIP_MERGE -> TIP_MERGE;
            case TIP -> dir == Direction.UP ? TIP_UP : TIP_DOWN;
            case FRUSTUM -> FRUSTUM;
            case MIDDLE -> MIDDLE;
            case BASE -> BASE;
        };
        return shape.move(state.getOffset(pos));
    }

    @Override
    protected boolean isCollisionShapeFullBlock(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    protected float getMaxHorizontalOffset() {
        return MAX_HORIZONTAL_OFFSET;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
