package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.ClientMaterialCache;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;

import java.util.List;
import java.util.Optional;

public class ParametricBlock extends Block {
	public static final int MAX_MATERIAL_STATE = RAAConfig.active().materialsMax();
	public static final IntegerProperty MAT = IntegerProperty.create("mat", 0, MAX_MATERIAL_STATE);

	public static int clampMaterialIndex(int idx) {
		return Math.clamp(idx, 0, MAX_MATERIAL_STATE);
	}

	public ParametricBlock(Properties props) {
		super(props);
		this.registerDefaultState(this.stateDefinition.any().setValue(MAT, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> b) {
		b.add(MAT);
	}

	@Override
	protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return ParametricBlockStats.destroyProgress(this, state, player, level, pos);
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader view, BlockPos pos, BlockState state, boolean bl) {
		ItemStack stack = new ItemStack(this.asItem());
		int idx = state.getValue(MAT);

		// Resolve the material id — level-aware so this works on dedicated servers too.
		// MaterialRegistry.byIndex dispatches to ClientMaterialCache on the client side.
		if (view instanceof net.minecraft.world.level.Level level) {
			MaterialRegistry.byIndex(level, idx).ifPresent(def -> stack.set(YComponents.MATERIAL, def.nameInformation().id()));
		}

		// Also stamp the exact blockstate so placement preserves MAT even without lookups
		stack.set(net.minecraft.core.component.DataComponents.BLOCK_STATE,
				net.minecraft.world.item.component.BlockItemStateProperties.EMPTY.with(MAT, idx));

		return stack;
	}

	@Override
	protected SoundType getSoundType(BlockState state) {
		Optional<MaterialDef> material = ClientMaterialCache.byIndex(state.getValue(ParametricBlock.MAT));
		if (material.isEmpty()) return super.getSoundType(state);

		MaterialDef def = material.get();
		if (def.kind() == MaterialKind.METAL || def.kind() == MaterialKind.ALLOY || def.kind() == MaterialKind.GEM) {
			return SoundType.METAL;
		} else if (def.kind() == MaterialKind.SAND) {
			return SoundType.SAND;
		} else if (def.kind() == MaterialKind.CRYSTAL) {
			return SoundType.AMETHYST;
		} else if (def.kind() == MaterialKind.CLAY || def.kind() == MaterialKind.GRAVEL) {
			return SoundType.GRAVEL;
		}

		return super.getSoundType(state);
	}

	@Override
	protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
		List<ItemStack> drops = super.getDrops(state, params);

		int idx = state.getValue(MAT);

		var material = MaterialRegistry.byIndex(params.getLevel(), idx);
		for (ItemStack stack : drops) {
			material.ifPresent(def -> stack.set(YComponents.MATERIAL, def.nameInformation().id()));

			if (stack.getItem() == this.asItem()) {
				stack.set(
						DataComponents.BLOCK_STATE,
						BlockItemStateProperties.EMPTY.with(MAT, idx)
				);
			}
		}

		return drops;
	}
}
