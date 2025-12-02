package net.vampirestudios.raaMaterials.material;// OverworldSpawnProfiles.java (sketch)

import net.minecraft.resources.ResourceLocation;
import net.vampirestudios.raaMaterials.material.SpawnSpec.Target;
import net.vampirestudios.raaMaterials.material.SpawnSpec.YBand;

import java.util.List;
import java.util.Random;

import static net.vampirestudios.raaMaterials.RAAMaterials.id;

public final class OverworldSpawnProfiles {
	private static final ResourceLocation OW = ResourceLocation.withDefaultNamespace("tags/biomes/is_overworld"); // or just minecraft:is_overworld
	private static final ResourceLocation T_STONE = ResourceLocation.withDefaultNamespace("stone_ore_replaceables");
	private static final ResourceLocation T_DEEPSLATE = ResourceLocation.withDefaultNamespace("deepslate_ore_replaceables");
	private static final ResourceLocation T_SAND      = ResourceLocation.withDefaultNamespace("sand");
	private static final ResourceLocation T_DIRT      = ResourceLocation.withDefaultNamespace("dirt");
	private static final ResourceLocation T_GRAVEL    = ResourceLocation.withDefaultNamespace("gravel");

	public static SpawnSpec pick(MaterialKind kind, SpawnSpec.Mode mode, Random rng) {
		return switch (mode) {
			case GEODE -> geode(kind, rng);
			case CLUSTER -> cluster(kind, rng);
			case VEIN -> ore(kind, rng);
			case METEORITE    -> meteorite(kind, rng);
			case STRATA      -> strata(kind, rng);
			case POCKET      -> pocket(kind, rng);
			case COLUMN      -> column(kind, rng);
			case DRIP_NODULE -> dripNodule(kind, rng);
			case SHEET_VEIN  -> sheetVein(kind, rng);
			case GIANT_NODE  -> giantNode(kind, rng);
			case MAGMATIC    -> magmatic(kind, rng);
			case CAVE_LINING -> caveLining(kind, rng);
			case SURFACE_NODE-> surfaceNode(kind, rng);
		};
	}

	private static VeinSpec ore(MaterialKind kind, Random rng) {
		var y = switch (weighted(rng, kind)) {
			case 0 -> YBand.triangle( 16, 128,  64);
			case 1 -> YBand.triangle(-16,  64,  16);
			default -> YBand.triangle(-59,  16, -32);
		};
		int size = 6 + rng.nextInt(7);
		int freq = 6 + rng.nextInt(8);

		return new VeinSpec(
				List.of(OW),
				List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE)),
				y, size, freq,
				/* wobbleScale */ 8.0f + rng.nextFloat() * 8.0f,
				/* verticality */ (kind == MaterialKind.GEM ? 0.15f : 0.35f),
				/* branchCount  */ (kind == MaterialKind.METAL ? 3 : 2),
				/* falloff      */ 0.45f + rng.nextFloat() * 0.25f
		);
	}

	private static ClusterSpec cluster(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-32, 48, 0);
		int size = 3 + rng.nextInt(3);
		int freq = 3 + rng.nextInt(4);
		return new ClusterSpec(
				List.of(OW),
				List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE)),
				y, size, freq,
				/* spread */ 8 + rng.nextInt(8),
				/* attachToCave */ (kind == MaterialKind.CRYSTAL || kind == MaterialKind.GEM),
				/* sphericity */ 0.7f + rng.nextFloat() * 0.2f
		);
	}

	private static GeodeSpec geode(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-32, 70, 8 + rng.nextInt(24));
		int attempts = 1 + rng.nextInt(2);
		var repl = List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE));
		var biomes = List.of(OW);
		return new GeodeSpec(biomes, repl, y, attempts);
	}

	// -------- NEW MODES (same pattern) --------------------------------------

	private static StrataSpec strata(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-24, 40, 8);        // thin sheets mid-depth
		int thickness = 1 + rng.nextInt(2);        // 1..2 blocks
		int sheets = 2 + rng.nextInt(2);           // 2..3 per chunk
		var repl = List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE));
		var biomes = List.of(OW);
		return new StrataSpec(biomes, repl, y, thickness, sheets);
	}

	private static PocketSpec pocket(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-32, 48, 0);
		int pocketSize = 4 + rng.nextInt(4);       // 4..7
		int pockets = 5 + rng.nextInt(5);          // 5..9
		var repl = List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE));
		var biomes = List.of(OW);
		return new PocketSpec(biomes, repl, y, pocketSize, pockets);
	}

	private static ColumnSpec column(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-48, 32, -8);
		int radius = 1 + rng.nextInt(2);           // 1..2
		int minH = 4 + rng.nextInt(4);             // 4..7
		int maxH = minH + 6 + rng.nextInt(8);      // ~10..15
		int cols = 2 + rng.nextInt(2);             // 2..3 per chunk
		var repl = List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE));
		var biomes = List.of(OW);
		return new ColumnSpec(biomes, repl, y, radius, minH, maxH, cols);
	}

	private static DripNoduleSpec dripNodule(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-48, 32, -4);
		int size = 1 + rng.nextInt(3);             // 1..3
		int perChunk = 16 + rng.nextInt(16);       // 16..31
		var repl = List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE));
		var biomes = List.of(OW);
		return new DripNoduleSpec(biomes, repl, y, size, perChunk);
	}

	private static SheetVeinSpec sheetVein(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-40, 24, -10);
		int thickness = 1;                          // razor thin seam
		int branchiness = 2 + rng.nextInt(3);       // 2..4 small offshoots
		int veins = 3 + rng.nextInt(3);             // 3..5 per chunk
		var repl = List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE));
		var biomes = List.of(OW);
		return new SheetVeinSpec(biomes, repl, y, thickness, branchiness, veins);
	}

	private static GiantNodeSpec giantNode(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-48, 24, -16);
		int attempts = 1 + rng.nextInt(2);          // rare
		int radius = 5 + rng.nextInt(4);            // 5..8
		boolean halo = rng.nextBoolean();           // scatter pebbles around
		var repl = List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE));
		var biomes = List.of(OW);
		return new GiantNodeSpec(biomes, repl, y, attempts, radius, halo);
	}

	private static MagmaticSpec magmatic(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-60, 16, -24);       // deep near lava
		int clusters = 3 + rng.nextInt(3);          // 3..5
		int lavaRadius = 6;                         // within 6 blocks of lava
		var repl = List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE));
		var biomes = List.of(OW);
		return new MagmaticSpec(biomes, repl, y, clusters, lavaRadius);
	}

	private static CaveLiningSpec caveLining(MaterialKind kind, Random rng) {
		var y = YBand.triangle(-48, 48, 0);
		int density = 24 + rng.nextInt(24);         // 24..47 tries across cave surfaces
		var repl = List.of(Target.tag(T_STONE), Target.tag(T_DEEPSLATE));
		var biomes = List.of(OW);
		return new CaveLiningSpec(biomes, repl, y, density);
	}

	private static SurfaceNodeSpec surfaceNode(MaterialKind kind, Random rng) {
		// Still restricted to stone-ish by default; bump Y high to bias to surface.
		var y = YBand.triangle(60, 128, 80);
		int nodes = 6 + rng.nextInt(6);             // 6..11 per chunk
		int size = 2 + rng.nextInt(3);              // 2..4
		var repl = List.of(Target.tag(T_STONE));    // keep it simple/consistent
		var biomes = List.of(OW);
		return new SurfaceNodeSpec(biomes, repl, y, nodes, size);
	}

	/** NEW: Meteorite generator profile */
	private static MeteoriteSpec meteorite(MaterialKind kind, Random rng) {
		// Height: center around surface; allow above/below to catch hills/valleys
		var y = YBand.triangle(-64, 192, 72);

		// Targets: allow carving + alteration on common surface blocks
		var repl = List.of(
				Target.tag(T_STONE),
				Target.tag(T_DEEPSLATE),
				Target.tag(T_SAND),
				Target.tag(T_DIRT),
				Target.tag(T_GRAVEL)
		);
		var biomes = List.of(OW);

		// Tunables vary gently by kind to create world variety
		int minR, maxR, strewn;
		double depth, coreRatio, ellipticity, glassChance, slagChance, ancientChance;
		switch (kind) {
			case METAL -> {
				minR = 6 + rng.nextInt(2);             // 6–7
				maxR = 12 + rng.nextInt(3);            // 12–14
				depth = 0.80;
				coreRatio = 0.42;                      // bigger iron-nickel core vibe
				ellipticity = 0.18 + rng.nextDouble()*0.10;
				strewn = 24 + rng.nextInt(10);
				glassChance = 0.45;
				slagChance  = 0.45;
				ancientChance = 0.40;
			}
			case GEM, CRYSTAL -> {
				minR = 5 + rng.nextInt(2);             // 5–6
				maxR = 10 + rng.nextInt(3);            // 10–12
				depth = 0.75;
				coreRatio = 0.33;                      // smaller gemmy core
				ellipticity = 0.22 + rng.nextDouble()*0.12;
				strewn = 28 + rng.nextInt(10);
				glassChance = 0.60;                    // deserts make nice glass fields
				slagChance  = 0.35;
				ancientChance = 0.55;
			}
			default -> {
				minR = 5 + rng.nextInt(3);             // 5–7
				maxR = 11 + rng.nextInt(3);            // 11–13
				depth = 0.80;
				coreRatio = 0.38;
				ellipticity = 0.20 + rng.nextDouble()*0.10;
				strewn = 26 + rng.nextInt(10);
				glassChance = 0.55;
				slagChance  = 0.40;
				ancientChance = 0.50;
			}
		}

		var params = new MeteoriteParams(
				new MeteoriteParams.Sampling(1, 0.12),                  // attempts/chance
				new MeteoriteParams.Geometry(minR, maxR, depth, coreRatio, ellipticity),
				new MeteoriteParams.Thermal(glassChance, slagChance, strewn),
				new MeteoriteParams.Burial(true, 16),                    // surface-first
				new MeteoriteParams.Ancient(ancientChance, true, 4, 2, 0.10, 0.06)
		);

		// Pick your block IDs here (can be overridden by data)
		var blocks = new MeteoriteBlocks(
				id("yourmod:meteor_metal_ore"),                          // core
				java.util.Optional.of(id("minecraft:deepslate_iron_ore")),// optional halo
				id("yourmod:slag"),                                       // slag
				id("yourmod:tektite_glass"),                              // glass
				id("minecraft:tuff")                                      // breccia
		);

		return new MeteoriteSpec(biomes, repl, y, params, blocks);
	}

	private static int weighted(Random r, MaterialKind k) {
		int[] w = switch (k) {
			case METAL -> new int[]{20, 40, 40};
			case GEM -> new int[]{35, 45, 20};
			case CRYSTAL -> new int[]{25, 50, 25};
			default -> new int[]{30, 40, 30};
		};
		int sum = 0;
		for (int x : w) sum += x;
		int roll = r.nextInt(sum), acc = 0;
		for (int i = 0; i < w.length; i++) {
			acc += w[i];
			if (roll < acc) return i;
		}
		return w.length - 1;
	}
}
