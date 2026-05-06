/*
package net.vampirestudios.raaMaterials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

*/
/**
 * Main JSON config for RAA Materials.
 * Adds per-kind "formControls" (toggles + explicit add/remove) while preserving your existing fields.
 *//*

public final class RAAConfigOld {
	public static ConfigClassHandler<RAAConfigOld> HANDLER = ConfigClassHandler.createBuilder(RAAConfigOld.class)
			.id(RAAMaterials.id("raa_materials"))
			.serializer(config -> GsonConfigSerializerBuilder.create(config)
					.setPath(FabricLoader.getInstance().getConfigDir().resolve("raa_materials.json5"))
					.appendGsonBuilder(GsonBuilder::setPrettyPrinting) // not needed, pretty print by default
					.setJson5(true)
					.build())
			.build();
	public static final String FILE_NAME = "raa_materials.json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	// -------- Generation counts --------
	@SerialEntry
	public int materialsMin = 8;
	@SerialEntry
	public int materialsMax = 13;

	// Per-kind weights (sum not required to be 100)
	@SerialEntry
	public Map<String, Integer> kindWeights = Map.ofEntries(
			Map.entry("METAL", 40), Map.entry("GEM", 18), Map.entry("CRYSTAL", 12),
			Map.entry("ALLOY", 8), Map.entry("STONE", 6), Map.entry("SAND", 4),
			Map.entry("GRAVEL", 3), Map.entry("CLAY", 3), Map.entry("MUD", 3),
			Map.entry("SALT", 2), Map.entry("VOLCANIC", 1), Map.entry("SOIL", 2)
	);

	@SerialEntry
	public TierWeights tiers = new TierWeights();

	@SerialEntry
	public Map<String, ModeWeights> spawnMode = Map.of(
			"GEM", new ModeWeights(50, 50, 0),
			"CRYSTAL", new ModeWeights(0, 50, 50),
			"VOLCANIC", new ModeWeights(30, 0, 70)
	);

	@SerialEntry
	public Map<String, Float> hardnessMul = Map.of(); // "METAL":1.1, "SAND":0.8
	@SerialEntry
	public Map<String, Float> blastMul = Map.of();
	@SerialEntry
	public Map<String, Float> effMul = Map.of();

	@SerialEntry
	public Map<String, DepthBand> depthWeights = Map.of(
			"METAL", new DepthBand(20, 40, 40),
			"GEM",   new DepthBand(35, 45, 20)
	);

	@SerialEntry
	public Map<String, YRange> shallowRange = Map.of("DEFAULT", band(16, 128, 48, 88));
	@SerialEntry
	public Map<String, YRange> midRange     = Map.of("DEFAULT", band(-16, 64, 8, 32));
	@SerialEntry
	public Map<String, YRange> deepRange    = Map.of("DEFAULT", band(-59, 16, -32, -12));

	// Replaceable/biome tag overrides (optional)
	@SerialEntry
	public Map<String, List<String>> replaceables = Map.of(
			"DEEPSLATE", List.of("#minecraft:deepslate_ore_replaceables"),
			"STONE",     List.of("#minecraft:stone_ore_replaceables")
	);

	@SerialEntry
	public NameGen nameGen = new NameGen();

	// (Legacy simple toggle; kept for compatibility. The richer controls live in formControls below.)
	@SerialEntry
	public Map<String, Boolean> allowToolsByKind = Map.of("METAL", true, "ALLOY", true, "GEM", true, "CRYSTAL", false);

	@SerialEntry
	public int toolChancePercent = 100; // 0..100

	*/
/* =========================================================================================
	   NEW: Per-kind form controls (toggles + explicit add/remove + shape clamp)
	   Keys = MaterialKind.name() (e.g., "METAL", "SAND", ...)
	   ========================================================================================= *//*

	@SerialEntry
	public Map<String, KindFormControls> formControls = defaultFormControls();

	*/
/** Default per-kind form controls (sane, permissive). *//*

	private static Map<String, KindFormControls> defaultFormControls() {
		Map<String, KindFormControls> m = new LinkedHashMap<>();

		// Metals & alloys
		m.put("METAL", controls()
				.enableTools(true).enableOreChain(true).enableMetalDecor(true));
		m.put("ALLOY", controls()
				.enableTools(true).enableOreChain(false).enableMetalDecor(true)); // alloys: no ore/raw by default

		// Gem & crystal
		m.put("GEM", controls()
				.enableTools(true).enableOreChain(true));
		m.put("CRYSTAL", controls()
				.enableTools(false).enableCrystalSet(true).enableCrystalLamps(true));

		// Rocky stuff
		m.put("STONE", controls().enableStoneDecor(true));
		m.put("SAND",  controls().enableSandSet(true).enableTools(false));
		m.put("GRAVEL",controls().enableGravelSet(true).enableTools(false));
		m.put("CLAY",  controls().enableClaySet(true).enableTools(false));
		m.put("MUD",   controls().enableMudSet(true).enableTools(false));
		m.put("SOIL",  controls().enableSoilSet(true).enableTools(false));
		m.put("SALT",  controls().enableSaltSet(true).enableTools(false));
		m.put("VOLCANIC", controls().enableVolcanic(true).enableTools(false));

		// Fallback
		m.putIfAbsent("OTHER", controls());
		return m;
	}

	*/
/** Fluent factory with all booleans defaulted to false (explicit opt-in per-kind above). *//*

	private static KindFormControls controls() { return new KindFormControls(); }

	*/
/* =========================================================================================
	   Helpers
	   ========================================================================================= *//*


	// helper (in config class)
	private static YRange band(int a, int b, int c, int d) {
		return new YRange(a, b, c, d);
	}

	// --- runtime ---
	public static Path configPath(Path gameDir) {
		return gameDir.resolve("config").resolve(FILE_NAME);
	}

	public static RAAConfigOld load(Path gameDir) {
		Path path = configPath(gameDir);
		try {
			if (Files.isRegularFile(path)) {
				RAAConfigOld cfg = GSON.fromJson(Files.readString(path), RAAConfigOld.class);

				// Backfill new fields when missing
				if (cfg.formControls == null || cfg.formControls.isEmpty()) {
					cfg.formControls = defaultFormControls();
				} else {
					// Ensure expected kinds exist
					for (String k : List.of("METAL","GEM","CRYSTAL","ALLOY","STONE","SAND","GRAVEL","CLAY","MUD","SALT","VOLCANIC","SOIL","OTHER")) {
						cfg.formControls.putIfAbsent(k, controls());
					}
				}

				// Migrate legacy allowToolsByKind into formControls if present
				if (cfg.allowToolsByKind != null && !cfg.allowToolsByKind.isEmpty()) {
					cfg.allowToolsByKind.forEach((k, v) -> {
						var c = cfg.formControls.computeIfAbsent(k, __ -> controls());
						// only set if not explicitly configured
						if (c.enableTools == null) c.enableTools = v;
						if (c.enableOreChain == null) c.enableOreChain = v;
						if (c.enableMetalDecor == null) c.enableMetalDecor = v;
						if (c.enableCrystalSet == null) c.enableCrystalSet = v;
						if (c.enableCrystalLamps == null) c.enableCrystalLamps = v;
						if (c.enableStoneDecor == null) c.enableStoneDecor = v;
						if (c.enableSandSet == null) c.enableSandSet = v;
						if (c.enableGravelSet == null) c.enableGravelSet = v;
						if (c.enableClaySet == null) c.enableClaySet = v;
						if (c.enableMudSet == null) c.enableMudSet = v;
						if (c.enableSoilSet == null) c.enableSoilSet = v;
						if (c.enableSaltSet == null) c.enableSaltSet = v;
						if (c.enableVolcanic == null) c.enableVolcanic = v;
					});
				}

				return cfg;
			}
		} catch (Exception ignored) { }
		return new RAAConfigOld();
	}

	public static void save(Path gameDir, RAAConfigOld cfg) {
		Path path = configPath(gameDir);
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(cfg));
		} catch (IOException e) {
			// log if you want
		}
	}

	*/
/* =========================================================================================
	   Existing nested types (unchanged)
	   ========================================================================================= *//*

	public record NameGen(boolean useColorPrefixes, boolean useBiomeBias, boolean useReplaceableBias, int hashLen, List<String> banned) {
		public NameGen() {
			this(true, true, true, 4, List.of("obsidian", "pumice"));
		}
	}

	public record DepthBand(int shallow, int mid, int deep) {
		public DepthBand() {
			this(30, 40, 30);
		}
	}

	public record YRange(int minY, int maxY, int peakMin, int peakMax) {}

	public record TierWeights(int stone, int iron, int diamond, int netherite) {
		public TierWeights() {
			this(40, 40, 15, 5);
		}
	}

	public record ModeWeights(int ore, int geode, int cluster) {
		public ModeWeights() {
			this(70, 15, 15);
		}
	}

	*/
/* =========================================================================================
	   NEW: Per-kind form controls bean
	   ========================================================================================= *//*

	@SerialEntry
	public static final class KindFormControls {
		// Group toggles (nullable to aid migration; treat null as false unless set by defaults)
		public Boolean enableTools;
		public Boolean enableOreChain;
		public Boolean enableMetalDecor;
		public Boolean enableCrystalSet;
		public Boolean enableCrystalLamps;
		public Boolean enableStoneDecor;
		public Boolean enableSandSet;
		public Boolean enableGravelSet;
		public Boolean enableClaySet;
		public Boolean enableMudSet;
		public Boolean enableSoilSet;
		public Boolean enableSaltSet;
		public Boolean enableVolcanic;

		*/
/** Explicit add/remove: list of Form enum ids as strings, e.g., ["SLAB","STAIRS","WALL"] *//*

		public List<String> add = List.of();
		public List<String> remove = List.of();

		*/
/** Clamp number of shape forms (SLAB/STAIRS/WALL). -1 = unlimited. *//*

		public int maxShapeForms = -1;

		// ---- Fluent helpers for building defaults ----
		public KindFormControls enableTools(boolean v)         { this.enableTools = v; return this; }
		public KindFormControls enableOreChain(boolean v)      { this.enableOreChain = v; return this; }
		public KindFormControls enableMetalDecor(boolean v)    { this.enableMetalDecor = v; return this; }
		public KindFormControls enableCrystalSet(boolean v)    { this.enableCrystalSet = v; return this; }
		public KindFormControls enableCrystalLamps(boolean v)  { this.enableCrystalLamps = v; return this; }
		public KindFormControls enableStoneDecor(boolean v)    { this.enableStoneDecor = v; return this; }
		public KindFormControls enableSandSet(boolean v)       { this.enableSandSet = v; return this; }
		public KindFormControls enableGravelSet(boolean v)     { this.enableGravelSet = v; return this; }
		public KindFormControls enableClaySet(boolean v)       { this.enableClaySet = v; return this; }
		public KindFormControls enableMudSet(boolean v)        { this.enableMudSet = v; return this; }
		public KindFormControls enableSoilSet(boolean v)       { this.enableSoilSet = v; return this; }
		public KindFormControls enableSaltSet(boolean v)       { this.enableSaltSet = v; return this; }
		public KindFormControls enableVolcanic(boolean v)      { this.enableVolcanic = v; return this; }
	}
}
*/
