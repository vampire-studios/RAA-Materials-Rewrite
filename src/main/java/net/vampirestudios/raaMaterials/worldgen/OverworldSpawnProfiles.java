package net.vampirestudios.raaMaterials.worldgen;

import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.material.SpawnRules;

import java.util.List;
import java.util.Random;

public final class OverworldSpawnProfiles {
	private static final List<String> OVERWORLD = List.of("#minecraft:is_overworld");
	private static final List<String> STONE_ONLY = List.of("#minecraft:stone_ore_replaceables");
	private static final List<String> MIXED_STONE_DEEPSLATE = List.of(
			"#minecraft:stone_ore_replaceables", "#minecraft:deepslate_ore_replaceables");

	// Biome groups (tags used only for biasing/name-gen; adjust to your registry as needed)
	private static final List<String> DESERTS = List.of("#minecraft:is_overworld", "#minecraft:is_desert", "#minecraft:is_badlands");
	private static final List<String> BEACHES = List.of("#minecraft:is_overworld", "#minecraft:is_beach", "#minecraft:is_ocean");
	private static final List<String> RIVERS = List.of("#minecraft:is_overworld", "#minecraft:is_river");
	private static final List<String> SWAMPS = List.of("#minecraft:is_overworld", "#minecraft:is_swamp", "#minecraft:is_mangrove");
	private static final List<String> MOUNTAINS = List.of("#minecraft:is_overworld", "#minecraft:is_mountain");

	// Replaceable groups (block tags; align with your placer)
	private static final List<String> SAND_REPL = List.of("#minecraft:sand", "#minecraft:sandstone");
	private static final List<String> GRAVEL_REPL = List.of("#minecraft:gravel");
	private static final List<String> DIRT_REPL = List.of("#minecraft:dirt", "#minecraft:grass_block");
	private static final List<String> CLAY_REPL = List.of("#minecraft:clay");
	private static final List<String> MUD_REPL = List.of("#minecraft:mud");
	private static final List<String> SALT_REPL = List.of("#minecraft:stone_ore_replaceables", "#minecraft:sandstone");
	private static final List<String> STONE_REPL = STONE_ONLY;

	public static SpawnRules pick(MaterialKind kind, SpawnMode mode, Random rng) {
		return switch (mode) {
			case GEODE -> geode(kind, rng);
			case CLUSTER -> cluster(kind, rng);
			default -> oreVein(kind, rng);
		};
	}

	private static SpawnRules oreVein(MaterialKind kind, Random rng) {
		int band = weighted(rng,
				switch (kind) {
					case METAL -> new int[]{20, 40, 40};     // shallow, mid, deep
					case GEM -> new int[]{35, 45, 20};
					case CRYSTAL -> new int[]{25, 50, 25};
					default -> new int[]{30, 40, 30};
				});
		int minY, maxY, peak;
		if (band == 0) {
			minY = 16;  maxY = 128; peak = 48 + rng.nextInt(40);
		} else if (band == 1) {
			minY = -16; maxY = 64;  peak = 8 + rng.nextInt(24);
		} else {
			minY = -59; maxY = 16;  peak = -32 + rng.nextInt(20);
		}
		int veinSize = 6 + rng.nextInt(7);      // 6–12
		int veinsPerChunk = 6 + rng.nextInt(8); // 6–13

		return new SpawnRules(
				SpawnMode.ORE_VEIN,
				veinSize,
				veinsPerChunk,
				minY, maxY,
				peak,
				OVERWORLD,
				STONE_ONLY
		);
	}

	private static SpawnRules geode(MaterialKind kind, Random rng) {
		// Geodes are rarer, larger features; fudge “vein” fields as budget frequency knobs
		int minY = -32;
		int maxY = 70;
		if (kind == MaterialKind.CRYSTAL && rng.nextBoolean()) {
			minY = -16;
			maxY = 40;
		}
		int peak = 8 + rng.nextInt(24);
		int geodesPerChunkEquivalent = 1 + rng.nextInt(2); // very rare
		int dummyVeinSize = 1; // your geode feature ignores this
		return new SpawnRules(
				SpawnMode.GEODE,
				dummyVeinSize,
				geodesPerChunkEquivalent,
				minY, maxY,
				peak,
				OVERWORLD,
				MIXED_STONE_DEEPSLATE
		);
	}

	private static SpawnRules cluster(MaterialKind kind, Random rng) {
		// Small clusters on cave walls; favor mid-depth
		int minY = -32, maxY = 48, peak = 0;
		int clustersPerChunk = 3 + rng.nextInt(4);
		List<String> biomes = OVERWORLD;
		List<String> repl = MIXED_STONE_DEEPSLATE;

		if (kind == MaterialKind.VOLCANIC) {
			// Make volcanic clusters a bit more frequent & deeper
			minY = -48;
			maxY = 32;
			peak = -8 + rng.nextInt(12);
			clustersPerChunk = 4 + rng.nextInt(4);
		}

		return new SpawnRules(
				SpawnMode.CLUSTER,
				1,
				clustersPerChunk,
				minY, maxY,
				peak,
				biomes,
				repl
		);
	}

	private static int weighted(Random r, int[] w) {
		int sum = 0;
		for (int x : w) sum += x;
		int roll = r.nextInt(Math.max(1, sum)), acc = 0;
		for (int i = 0; i < w.length; i++) {
			acc += w[i];
			if (roll < acc) return i;
		}
		return w.length - 1;
	}
}
