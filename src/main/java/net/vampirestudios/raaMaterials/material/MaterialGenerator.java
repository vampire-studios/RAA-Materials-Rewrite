// MaterialGenerator.java
package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.ResourceLocation;
import net.vampirestudios.raaMaterials.material.MaterialDef.OreHost;
import net.vampirestudios.raaMaterials.material.SpawnSpec.Mode;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.util.Map.entry;
import static net.vampirestudios.raaMaterials.material.Form.*;

public final class MaterialGenerator {
	public static MaterialSet generate(long worldSeed) {
		var rng = new Random(hash(worldSeed));
		var list = new ArrayList<MaterialDef>();
		int count = rng.nextInt(15, 31);

		for (int i = 0; i < count; i++) {
			var kind = pickKind(rng);

			int color = rng.nextInt(0xFFFFFF);
			var tier = (kind == MaterialKind.METAL || kind == MaterialKind.ALLOY) ? pickTier(rng) : HarvestTier.IRON;

			List<Form> forms = getForms(kind);

			var mode = switch (kind) {
				case METAL, ALLOY, GEM -> Mode.CLUSTER;  // cluster by default
				case CRYSTAL -> rng.nextBoolean() ? Mode.GEODE : Mode.CLUSTER;
				case VOLCANIC -> rng.nextInt(100) < 70 ? Mode.CLUSTER : Mode.MAGMATIC;
				case STONE -> rng.nextInt(100) < 50 ? Mode.STRATA : Mode.VEIN;
				case SAND, GRAVEL -> rng.nextInt(100) < 60 ? Mode.POCKET : Mode.SURFACE_NODE;
				default -> Mode.VEIN;
			};
			SpawnSpec spawn = OverworldSpawnProfiles.pick(kind, mode, rng);

			// >>> NEW: choose host and force its target into replaceables
			OreHost host = pickOreHost(rng, spawn.biomeTags(), spawn.y().minY(), spawn.y().maxY());
//			spawn = spawn.withReplaceables(java.util.List.of(host.target()));

			// --- NEW: generate a plausible displayName (registry-safe id + display) ---
			MaterialDef.NameInformation nm = GeoNameGen.generate(
					worldSeed,
					i,
					kind,
					color,
					spawn
			);
			// Use the slug/id from the generator for the registry ID
			var id = nm.id();

			Optional<ToolMaterialSpec> toolSpec = switch (kind) {
				case METAL, ALLOY -> Optional.of(ToolMaterialPresets.forMetal(tier));
				case GEM -> Optional.of(ToolMaterialPresets.forGem(pickTier(rng)));      // or reuse 'tier'
//				case CRYSTAL      -> toolSpec = ToolMaterialPresets.forCrystal(pickTier(rng));  // if you want crystal tools
				default -> Optional.empty();
			};

			list.add(new MaterialDef(
					nm, kind, color,
					hardnessFor(kind, tier),
					blastFor(kind, tier),
					effFor(kind, tier),
					tier,
					forms, spawn, toolSpec,
					mix(worldSeed, id),
					host
			));
		}
		return new MaterialSet(list);
	}

	private static boolean isNether(List<ResourceLocation> biomes) {
		if (biomes == null) return false;
		for (ResourceLocation b : biomes) if (b.getPath().toLowerCase(Locale.ROOT).contains("nether")) return true;
		return false;
	}
	private static boolean isEnd(List<ResourceLocation> biomes) {
		if (biomes == null) return false;
		for (ResourceLocation b : biomes) if (b.getPath().toLowerCase(Locale.ROOT).contains("end")) return true;
		return false;
	}

	/** Picks a reasonable host based on dimension-ish biome keys + depth band. */
	private static OreHost pickOreHost(Random rng, List<ResourceLocation> biomeTags, int minY, int maxY) {
		// Dimension buckets
		if (isEnd(biomeTags)) {
			return OreHost.END_STONE;
		}
		if (isNether(biomeTags)) {
			int r = rng.nextInt(100);
			if (r < 65) return OreHost.NETHERRACK;
			if (r < 85) return OreHost.BLACKSTONE;
			return OreHost.BASALT;
		}

		// Overworld by depth + cave features
		int mid = (minY + maxY) / 2;
		if (mid <= 0) { // deep
			int r = rng.nextInt(100);
			if (r < 60) return OreHost.DEEPSLATE;
			if (r < 85) return OreHost.TUFF;
			return OreHost.SMOOTH_BASALT;
		} else if (mid >= 80) { // high
			int r = rng.nextInt(100);
			if (r < 40) return OreHost.STONE;
			if (r < 60) return OreHost.GRANITE;
			if (r < 80) return OreHost.DIORITE;
			return OreHost.ANDESITE;
		} else { // mid
			boolean drip = biomeTags != null && biomeTags.stream().anyMatch(s -> s.getPath().toLowerCase(Locale.ROOT).contains("dripstone"));
			int r = rng.nextInt(100);
			if (drip && r < 25) return OreHost.DRIPSTONE;
			if (r < 45) return OreHost.STONE;
			if (r < 63) return OreHost.GRANITE;
			if (r < 81) return OreHost.DIORITE;
			if (r < 93) return OreHost.ANDESITE;
			return OreHost.CALCITE;
		}
	}

	private static long mix(long worldSeed, ResourceLocation id) {
		long h = worldSeed ^ 0x9E3779B97F4A7C15L; // golden ratio
		h ^= id.getNamespace().hashCode() * 0x9E3779B97F4A7C15L;
		h ^= id.getPath().hashCode() * 0xBF58476D1CE4E5B9L;

		// final avalanche (SplitMix64 step)
		h ^= (h >>> 30);
		h *= 0xBF58476D1CE4E5B9L;
		h ^= (h >>> 27);
		h *= 0x94D049BB133111EBL;
		h ^= (h >>> 31);

		return h;
	}

	private static final List<Form> TOOLS = List.of(PICKAXE, AXE, SWORD, SHOVEL, HOE);

	// Helper to build a mutable result while keeping callsites tidy.
	@SafeVarargs
	private static @NotNull List<Form> join(List<Form>... groups) {
		var out = new java.util.ArrayList<Form>();
		for (var g : groups) out.addAll(g);
		return out;
	}

	private static @NotNull List<Form> getForms(MaterialKind kind) {
		var tools = List.of(PICKAXE, AXE, SWORD, SHOVEL, HOE);
		return switch (kind) {
			case METAL -> join(
					// items/crafting
					List.of(RAW, INGOT, DUST, NUGGET, SHEET, GEAR, ROD, WIRE, COIL, RIVET, BOLT, NAIL, RING),
					// blocks/variants
					List.of(ORE, BRICKS, BLOCK, RAW_BLOCK, PLATE_BLOCK, SHINGLES, PILLAR, TILES, CHISELED, BARS, GRATE, BUTTON, PRESSURE_PLATE),
					// tools
					TOOLS
			);
			case GEM -> join(
					List.of(ORE, BLOCK, GEM, SHARD),
					TOOLS
			);
			case CRYSTAL -> join(
					List.of(
							CLUSTER, BLOCK, CRYSTAL, SHARD, DUST,
							BUDDING, BUD_SMALL, BUD_MEDIUM, BUD_LARGE,
							CRYSTAL_BRICKS, GLASS, TINTED_GLASS, PANE, MOSAIC,
							CALCITE_LAMP, BASALT_LAMP, CHIME, ROD_BLOCK
					)
			);
			case ALLOY -> join(
					// items/crafting
					List.of(INGOT, SHEET, GEAR, ROD, WIRE, COIL, RIVET, BOLT, NAIL, RING),
					// blocks/variants
					List.of(BLOCK, BRICKS, PLATE_BLOCK, SHINGLES, PILLAR, TILES, CHISELED, BARS, GRATE, BUTTON, PRESSURE_PLATE),
					// tools
					TOOLS
			);
			case STONE -> join(List.of(
					BLOCK, CHISELED, POLISHED, BRICKS,
					SLAB, STAIRS, WALL,
					PILLAR, TILES, MOSAIC, MOSSY, CRACKED, COBBLED,
					BUTTON, PRESSURE_PLATE
			));
			case SAND -> new ArrayList<>(List.of(BLOCK, SANDSTONE, CUT, CHISELED, SMOOTH, SLAB, STAIRS, WALL));
			case GRAVEL -> new ArrayList<>(List.of(BLOCK, POLISHED, SLAB, STAIRS, WALL));
			case CLAY -> new ArrayList<>(List.of(BLOCK, BALL, CERAMIC));
			case MUD -> new ArrayList<>(List.of(BLOCK, DRIED, BRICKS, SLAB, STAIRS, WALL));
			case SOIL -> new ArrayList<>(List.of(BLOCK, PACKED_SOIL));
			case SALT -> new ArrayList<>(List.of(BLOCK, DUST));
			case VOLCANIC -> new ArrayList<>(List.of(BLOCK, COBBLED, POLISHED, BRICKS, PILLAR, MOSSY, BUTTON, PRESSURE_PLATE));
			case WOOD -> new ArrayList<>(List.of(BLOCK, PILLAR, BUTTON, PRESSURE_PLATE));
			default -> new ArrayList<>(List.of(DUST));
		};
	}

	private static long hash(long seed) {
		return seed ^ 0x9E3779B97F4A7C15L ^ "raa_materials:materials".hashCode();
	}

	// Optional: theme presets (can be removed once config is always present)
	private enum Profile { BALANCED, TECH_HEAVY, GEO_HEAVY }

	/** Core sampler: roulette-wheel pick from a weight map. */
	private static MaterialKind pickKind(Random r, Map<MaterialKind, Integer> weights) {
		// Validate & make a stable, filtered list (non-null, >0)
		var ordered = new ArrayList<Map.Entry<MaterialKind, Integer>>(weights.size());
		for (var e : weights.entrySet()) {
			if (e.getKey() == null) continue;
			int w = e.getValue() == null ? 0 : e.getValue();
			if (w > 0) ordered.add(entry(e.getKey(), w));
		}
		// Stable order by enum ordinal (predictable across runs)
		ordered.sort(Comparator.comparingInt(e -> e.getKey().ordinal()));

		int total = 0;
		for (var e : ordered) total += e.getValue();
		if (total <= 0) return MaterialKind.OTHER; // nothing enabled

		int roll = r.nextInt(total);
		int acc = 0;
		for (var e : ordered) {
			acc += e.getValue();
			if (roll < acc) return e.getKey();
		}
		return MaterialKind.OTHER; // unreachable with positive total
	}

	/** Config-first picker. If config is absent/invalid, falls back to a preset. */
	private static MaterialKind pickKind(Random r) {
		Map<MaterialKind, Integer> cfg = loadWeightsFromConfig(); // fully custom (no multipliers)
		if (cfg != null && !cfg.isEmpty()) {
			return pickKind(r, cfg);
		}
		// Fallback to a preset (choose one):
		return pickKind(r, defaultWeights(Profile.BALANCED));
	}

	/** Preset tables using Map.ofEntries(entry(...)) — easy to read & edit. */
	private static Map<MaterialKind, Integer> defaultWeights(Profile p) {
		return switch (p) {
			case BALANCED   -> Map.ofEntries(
					entry(MaterialKind.METAL, 25),
					entry(MaterialKind.ALLOY, 10),
					entry(MaterialKind.GEM,   12),
					entry(MaterialKind.CRYSTAL,10),
					entry(MaterialKind.STONE, 12),
					entry(MaterialKind.VOLCANIC, 3), // rarer volcanic
					entry(MaterialKind.SAND,   8),
					entry(MaterialKind.GRAVEL, 7),
					entry(MaterialKind.CLAY,   5),
					entry(MaterialKind.MUD,    4),
					entry(MaterialKind.SALT,   2),
					entry(MaterialKind.SOIL,   2)
			);
			case TECH_HEAVY -> Map.ofEntries(
					entry(MaterialKind.METAL, 35),
					entry(MaterialKind.ALLOY, 20),
					entry(MaterialKind.GEM,   10),
					entry(MaterialKind.CRYSTAL, 8),
					entry(MaterialKind.STONE,  8),
					entry(MaterialKind.VOLCANIC, 2),
					entry(MaterialKind.SAND,   5),
					entry(MaterialKind.GRAVEL, 4),
					entry(MaterialKind.CLAY,   4),
					entry(MaterialKind.MUD,    2),
					entry(MaterialKind.SALT,   1),
					entry(MaterialKind.SOIL,   1)
			);
			case GEO_HEAVY  -> Map.ofEntries(
					entry(MaterialKind.METAL, 15),
					entry(MaterialKind.ALLOY,  5),
					entry(MaterialKind.GEM,     8),
					entry(MaterialKind.CRYSTAL, 7),
					entry(MaterialKind.STONE,  22),
					entry(MaterialKind.VOLCANIC, 5),
					entry(MaterialKind.SAND,   12),
					entry(MaterialKind.GRAVEL,  8),
					entry(MaterialKind.CLAY,    8),
					entry(MaterialKind.MUD,     6),
					entry(MaterialKind.SALT,    2),
					entry(MaterialKind.SOIL,    2)
			);
		};
	}

	@SuppressWarnings("unchecked")
	private static Map<MaterialKind, Integer> loadWeightsFromConfig() {
		// Stub: replace with your real config access.
		// Example JSON (weights.json):
		// {
		//   "METAL": 22, "ALLOY": 8, "GEM": 10, "CRYSTAL": 9, "STONE": 18,
		//   "VOLCANIC": 2, "SAND": 9, "GRAVEL": 7, "CLAY": 6, "MUD": 5, "SALT": 2, "SOIL": 2
		// }
		return null;
	}

	private static HarvestTier pickTier(Random r) {
		int roll = r.nextInt(100);
		if (roll < 40) return HarvestTier.STONE;
		if (roll < 80) return HarvestTier.IRON;
		if (roll < 95) return HarvestTier.DIAMOND;
		return HarvestTier.NETHERITE;
	}

	private static float scale(String key, float base, Map<String, Float> mul) {
		return base * mul.getOrDefault(key, 1.0f);
	}

	private static float hardnessFor(MaterialKind k, HarvestTier t) {
		return switch (k) {
			case METAL, ALLOY -> switch (t) {
				case STONE -> 3.0f;
				case IRON -> 4.0f;
				case DIAMOND -> 5.0f;
				case NETHERITE -> 6.0f;
			};
			case GEM -> 5.5f;
			case CRYSTAL -> 4.0f;
			case SAND, MUD -> 0.5f;
			case GRAVEL -> 0.8f;
			case CLAY, SOIL -> 0.6f;
			case SALT -> 1.5f;
			case VOLCANIC -> 2.5f;
			default -> 3.0f;
		};
	}

	private static float blastFor(MaterialKind k, HarvestTier t) {
		return switch (k) {
			case METAL, ALLOY -> 6.0f + (t.ordinal() * 2.0f);
			case GEM -> 8.0f;
			case CRYSTAL -> 5.0f;
			case STONE -> 6.0f;
			case SAND, MUD -> 0.2f;
			case GRAVEL -> 0.4f;
			case CLAY -> 0.5f;
			case SOIL -> 0.3f;
			case SALT -> 1.0f;
			default -> 3.0f;
		};
	}

	private static float effFor(MaterialKind k, HarvestTier t) {
		return (k == MaterialKind.METAL || k == MaterialKind.ALLOY)
				? (1.0f + t.ordinal() * 0.25f)
				: 1.0f;
	}
}
