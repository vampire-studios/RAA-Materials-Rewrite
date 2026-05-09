package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.Forms;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;

import java.util.Optional;

final class ParametricBlockStats {
	static float destroyProgress(Block block, BlockState state, Player player, BlockGetter level, BlockPos pos) {
		float hardness = hardness(block, state, level);
		if (hardness < 0.0f) return 0.0f;

		int divisor = player.hasCorrectToolForDrops(state) ? 30 : 100;
		return player.getDestroySpeed(state) / hardness / divisor;
	}

	private static float hardness(Block block, BlockState state, BlockGetter level) {
		if (!state.hasProperty(ParametricBlock.MAT) || !(level instanceof Level world)) {
			return block.defaultDestroyTime();
		}

		Optional<MaterialDef> material = MaterialRegistry.byIndex(world, state.getValue(ParametricBlock.MAT));
		if (material.isEmpty()) {
			return block.defaultDestroyTime();
		}

		Form form = Forms.fromDescriptionId(block.getDescriptionId());
		float hardness = material.get().hardness() * formMultiplier(form, material.get().kind());
		return Math.clamp(hardness, minimumHardness(form), maximumHardness(form, material.get().kind()));
	}

	private static float formMultiplier(Form form, MaterialKind kind) {
		return switch (form) {
			case GLASS, TINTED_GLASS, PANE -> 0.08f;
			case CLUSTER -> 0.35f;
			case BUDDING, BUD_SMALL, BUD_MEDIUM, BUD_LARGE -> 0.25f;
			case BUTTON, PRESSURE_PLATE -> 0.25f;
			case BARS, GRATE -> kind == MaterialKind.METAL || kind == MaterialKind.ALLOY ? 0.85f : 0.55f;
			case SHINGLES, PLATE_BLOCK -> 0.85f;
			case RAW_BLOCK -> 1.1f;
			case BRICKS, TILES, PILLAR, MOSAIC -> 1.05f;
			case POLISHED, CHISELED, SMOOTH, CUT -> 1.0f;
			case COBBLED, MOSSY -> 0.9f;
			case CRACKED -> 0.75f;
			case CERAMIC -> 0.8f;
			case DRIED -> 0.7f;
			case CALCITE_LAMP, BASALT_LAMP, ROD_BLOCK -> 0.65f;
			default -> 1.0f;
		};
	}

	private static float minimumHardness(Form form) {
		return switch (form) {
			case GLASS, TINTED_GLASS, PANE -> 0.3f;
			case BUTTON, PRESSURE_PLATE -> 0.5f;
			case BUDDING, BUD_SMALL, BUD_MEDIUM, BUD_LARGE -> 0.5f;
			case CLUSTER -> 0.8f;
			default -> 0.4f;
		};
	}

	private static float maximumHardness(Form form, MaterialKind kind) {
		return switch (form) {
			case GLASS, TINTED_GLASS, PANE -> 0.6f;
			case BUTTON, PRESSURE_PLATE -> 1.5f;
			case CLUSTER, BUDDING, BUD_SMALL, BUD_MEDIUM, BUD_LARGE -> 1.5f;
			case SLAB, STAIRS, WALL -> kind == MaterialKind.METAL || kind == MaterialKind.ALLOY ? 7.0f : 4.0f;
			default -> kind == MaterialKind.METAL || kind == MaterialKind.ALLOY ? 8.0f : 6.0f;
		};
	}

	private ParametricBlockStats() {
	}
}
