package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Locale;

public class ParametricCrystalBudBlock extends ParametricBlock {
	public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
	public static final EnumProperty<Size> SIZE = EnumProperty.create("size", Size.class);
	private static final VoxelShape UP_S = Block.box(7, 0, 7, 9, 5, 9);
	private static final VoxelShape UP_M = Block.box(6, 0, 6, 10, 8, 10);
	private static final VoxelShape UP_L = Block.box(5, 0, 5, 11, 12, 11);
	private static final VoxelShape DOWN_S = Block.box(7, 11, 7, 9, 16, 9);
	private static final VoxelShape DOWN_M = Block.box(6, 8, 6, 10, 16, 10);
	private static final VoxelShape DOWN_L = Block.box(5, 4, 5, 11, 16, 11);
	private static final VoxelShape NORTH_S = Block.box(7, 7, 11, 9, 9, 16);
	private static final VoxelShape NORTH_M = Block.box(6, 6, 8, 10, 10, 16);
	private static final VoxelShape NORTH_L = Block.box(5, 5, 5, 11, 11, 16);
	private static final VoxelShape SOUTH_S = Block.box(7, 7, 0, 9, 9, 5);
	private static final VoxelShape SOUTH_M = Block.box(6, 6, 0, 10, 10, 8);
	private static final VoxelShape SOUTH_L = Block.box(5, 5, 0, 11, 11, 11);
	private static final VoxelShape WEST_S = Block.box(11, 7, 7, 16, 9, 9);
	private static final VoxelShape WEST_M = Block.box(8, 6, 6, 16, 10, 10);
	private static final VoxelShape WEST_L = Block.box(5, 5, 5, 16, 11, 11);
	private static final VoxelShape EAST_S = Block.box(0, 7, 7, 5, 9, 9);
	private static final VoxelShape EAST_M = Block.box(0, 6, 6, 8, 10, 10);
	private static final VoxelShape EAST_L = Block.box(0, 5, 5, 11, 11, 11);
	public ParametricCrystalBudBlock(Properties props, Size defaultSize) {
		super(props.noOcclusion().sound(SoundType.AMETHYST_CLUSTER));
		registerDefaultState(defaultBlockState()
				.setValue(FACING, Direction.UP)
				.setValue(SIZE, defaultSize));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
		super.createBlockStateDefinition(b);
		b.add(FACING, SIZE);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		// Buds aren’t meant to be obtainable; if ever placed via setblock, orient to face clicked.
		return defaultBlockState().setValue(FACING, ctx.getClickedFace());
	}

	@Override
	public VoxelShape getShape(BlockState s, net.minecraft.world.level.BlockGetter g, net.minecraft.core.BlockPos p, CollisionContext c) {
		Direction f = s.getValue(FACING);
		Size z = s.getValue(SIZE);
		return switch (f) {
			case UP -> switch (z) {
				case SMALL -> UP_S;
				case MEDIUM -> UP_M;
				case LARGE -> UP_L;
			};
			case DOWN -> switch (z) {
				case SMALL -> DOWN_S;
				case MEDIUM -> DOWN_M;
				case LARGE -> DOWN_L;
			};
			case NORTH -> switch (z) {
				case SMALL -> NORTH_S;
				case MEDIUM -> NORTH_M;
				case LARGE -> NORTH_L;
			};
			case SOUTH -> switch (z) {
				case SMALL -> SOUTH_S;
				case MEDIUM -> SOUTH_M;
				case LARGE -> SOUTH_L;
			};
			case WEST -> switch (z) {
				case SMALL -> WEST_S;
				case MEDIUM -> WEST_M;
				case LARGE -> WEST_L;
			};
			case EAST -> switch (z) {
				case SMALL -> EAST_S;
				case MEDIUM -> EAST_M;
				case LARGE -> EAST_L;
			};
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

	public enum Size implements StringRepresentable {
		SMALL, MEDIUM, LARGE;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	// Buds have no item; let loot table handle “drop nothing”.
}
