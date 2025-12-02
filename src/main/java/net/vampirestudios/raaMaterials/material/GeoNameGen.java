// src/main/java/net/vampirestudios/raaMaterials/material/GeoNameGen.java
package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.ResourceLocation;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.material.MaterialDef.NameInformation;
import net.vampirestudios.raaMaterials.worldgen.SpawnMode;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.*;
import java.util.function.BiFunction;

/**
 * GeoNameGen — geology-leaning material displayName generator (modular).
 * Deterministic by (worldSeed, kind, index).
 *
 * Assumption: MaterialKind contains METAL, GEM, CRYSTAL, ALLOY, STONE, SOIL, SAND, GRAVEL, CLAY, MUD, SALT, VOLCANIC, OTHER.
 * If yours differs, map your kinds to these buckets in KIND_CFG_INIT below.
 */
public final class GeoNameGen {

	// color/appearance
	private static final String[] COLOR_GREEN = {"chloro", "viridi", "smarag", "prasi"};

	// ===== Public API =====
	private static final String[] COLOR_BLUE = {"cyano", "azure", "lazu", "caerul", "sapph"};
	private static final String[] COLOR_RED = {"erythro", "rubri", "rhodo", "carmi"};
	private static final String[] COLOR_YELL = {"xantho", "flavo", "auri", "fulvo"};

	// ===== Morphemes (shared pools) =====
	private static final String[] COLOR_BLACK = {"melano", "nigri", "anthra"};
	private static final String[] COLOR_WHITE = {"leuco", "alba", "argento"};
	private static final String[] COLOR_MISC = {"irido", "opal", "luci", "phospho"};
	private static final String[] COLOR_ORANGE = {"croco", "auran", "citrin", "pyro", "tawny"};
	private static final String[] COLOR_PURPLE = {"porphyr", "purpur", "amethyst", "violac"};
	private static final String[] COLOR_BROWN = {"brun", "umbr", "fulig", "ruf", "murrh"};
	private static final String[] COLOR_GRAY = {"cinere", "argill", "plumbe", "grise"};
	private static final String[] COLOR_PINK = {"roseo", "rhodon", "candi", "rubell"};
	private static final String[] COLOR_TEAL = {"teal", "turqu", "viridcyan", "aqua"};
	// chemistry/element stems
	private static final String[] CHEM = {
			"silic", "calc", "dolom", "magnes", "ferr", "cupr", "stann", "plumb", "mangan",
			"cobalt", "nickel", "argent", "aur", "bor", "fluor", "sulf", "lith", "sod", "potass"
	};
	// structural cores (broad)
	private static final String[] CORES_GENERIC = {
			"spar", "blende", "feld", "chalc", "pyrox", "amphib", "oliv", "gran", "tourmal",
			"topaz", "beryl", "garnet", "quartz", "agate", "opal", "jade", "corund", "azur",
			"malach", "dolom", "rhodo", "serpentin", "barit", "halit", "graph", "mica"
	};
	// type-focused cores
	private static final String[] CORES_METAL = {"chalc", "sulf", "magnet", "hemat", "cupr", "ferr", "argent", "stann", "plumb", "nickel"};
	private static final String[] CORES_GEM = {"garnet", "beryl", "topaz", "jade", "opal", "agate", "tourmal", "corund", "spinel", "zircon"};
	private static final String[] CORES_CRYSTAL = {"quartz", "spar", "cryst", "calc", "dolom", "halit", "fluor", "selen", "aragon", "apat"};
	private static final String[] CORES_ALLOY = {"ferro", "stanno", "cuprio", "nickelo", "argento", "auro"};
	private static final String[] CORES_SOIL = {"argill", "humic", "loess", "peat", "gelisol", "andosol", "vertis", "ultis"};
	private static final String[] CORES_SAND = {"quartz", "aren", "silic", "feld", "lith", "psamm", "arkos"};
	private static final String[] CORES_GRAVEL = {"rud", "brecc", "conglom", "lith", "quartz", "gruss"};
	private static final String[] CORES_CLAY = {"kaolin", "illite", "smect", "montmor", "chlorit", "benton", "argill"};
	private static final String[] CORES_MUD = {"pelit", "lutit", "silt", "argill", "turbid"};
	private static final String[] CORES_SALT = {"halit", "sylv", "gyps", "anhydr", "carnal", "mirab", "trona", "epsom"};
	private static final String[] CORES_VOLC = {"tuff", "pumic", "obsid", "scori", "basalt", "andes", "dacite", "rhyol", "tephr"};
	private static final String[] CORES_WOOD = {"querc", "betul", "pice", "abies", "laric", "pin", "salix", "acac", "ulmus", "juglan", "coryl", "acer", "tilia", "cedr", "mahog", "ebon", "bambu", "mangr", "cherri", "darkoak", "baoba", "teak", "ironw",};
	// suffix families
	private static final String[] SUF_ORE = {"ite", "ite", "ite", "ide", "ate", "anite", "onite", "lite", "blende"};
	private static final String[] SUF_CRYSTAL = {"ite", "lite", "spar", "cryst", "ine", "ite", "lite"};
	private static final String[] SUF_GEM = {"ite", "ine", "el", "ar", "on", "olite", "aline"};
	private static final String[] SUF_ALLOY = {"alloy", "steel", "bronze", "tine", "metal"};
	private static final String[] SUF_SOIL = {"sol", "soil", "dirt", "humus", "rite", "lite"};
	private static final String[] SUF_SAND = {"sand", "arenite", "psammite", "aren", "sandstone"};
	private static final String[] SUF_GRAVEL = {"gravel", "rudite", "conglomerate", "breccia"};
	private static final String[] SUF_CLAY = {"clay", "argillite", "mudstone", "shale", "illite", "kaolinite"};
	private static final String[] SUF_MUD = {"mud", "mudstone", "pelite", "lutite"};
	private static final String[] SUF_SALT = {"salt", "halite", "evaporite", "sylvite"};
	private static final String[] SUF_VOLC = {"tuff", "tephra", "pumice", "scoria", "glass", "obsidian"};
	private static final String[] SUF_WOOD = {"wood", "bark", "heartwood", "timber", "plank"};
	private static final String[] LINKS = {"o", "a", "i", "e", "", "", ""};
	private static final String[] VOW = {"a", "e", "i", "o", "u", "y", "ae", "ia", "io", "ou", "ui", "ea"};
	private static final String[] CON = {"b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "r", "s", "t", "v", "z",
			"br", "cr", "dr", "fr", "gr", "kr", "pr", "tr", "str", "pl", "gl", "cl", "sl"};
	private static final String[] BAD = {"jj", "vv", "ww", "yy", "qq", "hhh", "kkk", "zzz", "xxx", "aaae", "iii", "uuu"};
	private static final Map<MaterialKind, KindCfg> KIND_CFG = new EnumMap<>(MaterialKind.class);

	// Initialize reasonable defaults. Adjust mapping if your enum differs.
	static {
		// Metals: chem-leaning, ore suffix, limited color
		KIND_CFG.put(MaterialKind.METAL, KindCfg.of(
				merge(CORES_METAL, CORES_GENERIC),
				SUF_ORE, SUF_ORE, SUF_ORE,
				25, 55, 20, true, true, GeoNameGen::coerceSuffixSmart
		));
		// Alloys: special suffix bank, chem-heavy
		KIND_CFG.put(MaterialKind.ALLOY, KindCfg.of(
				CORES_ALLOY, SUF_ALLOY, SUF_ALLOY, SUF_ALLOY,
				15, 65, 20, true, false, GeoNameGen::coerceSuffixSmart
		));
		// Gems: color-forward + gem endings
		KIND_CFG.put(MaterialKind.GEM, KindCfg.of(
				merge(CORES_GEM, CORES_GENERIC),
				SUF_GEM, SUF_GEM, SUF_GEM,
				55, 25, 20, false, true, GeoNameGen::coerceSuffixSmart
		));
		// Crystals: crystal cores + geode bias
		KIND_CFG.put(MaterialKind.CRYSTAL, KindCfg.of(
				merge(CORES_CRYSTAL, CORES_GENERIC),
				SUF_ORE, SUF_CRYSTAL, SUF_CRYSTAL,
				50, 25, 25, false, true, GeoNameGen::coerceSuffixSmart
		));
		// Stone: generic mineral style
		KIND_CFG.put(MaterialKind.STONE, KindCfg.of(
				CORES_GENERIC, SUF_ORE, SUF_CRYSTAL, SUF_ORE,
				40, 35, 25, false, true, GeoNameGen::coerceSuffixSmart
		));
		// Soil/Dirt: earthy/colorable, soil suffixes
		KIND_CFG.put(MaterialKind.SOIL, KindCfg.of(
				CORES_SOIL, SUF_SOIL, SUF_SOIL, SUF_SOIL,
				55, 15, 30, false, true, GeoNameGen::coerceSuffixSmart
		));
		// Sand/Gravel families
		KIND_CFG.put(MaterialKind.SAND, KindCfg.of(
				CORES_SAND, SUF_SAND, SUF_SAND, SUF_SAND,
				50, 10, 40, false, true, GeoNameGen::coerceSuffixSmart
		));
		KIND_CFG.put(MaterialKind.GRAVEL, KindCfg.of(
				CORES_GRAVEL, SUF_GRAVEL, SUF_GRAVEL, SUF_GRAVEL,
				45, 10, 45, false, true, GeoNameGen::coerceSuffixSmart
		));
		// Clay/Mud
		KIND_CFG.put(MaterialKind.CLAY, KindCfg.of(
				CORES_CLAY, SUF_CLAY, SUF_CLAY, SUF_CLAY,
				50, 15, 35, false, true, GeoNameGen::coerceSuffixSmart
		));
		KIND_CFG.put(MaterialKind.MUD, KindCfg.of(
				CORES_MUD, SUF_MUD, SUF_MUD, SUF_MUD,
				50, 10, 40, false, true, GeoNameGen::coerceSuffixSmart
		));
		// Salts / Evaporites
		KIND_CFG.put(MaterialKind.SALT, KindCfg.of(
				CORES_SALT, SUF_SALT, SUF_SALT, SUF_SALT,
				40, 30, 30, true, true, GeoNameGen::coerceSuffixSmart
		));
		// Volcanics / Ash / Glasses
		KIND_CFG.put(MaterialKind.VOLCANIC, KindCfg.of(
				CORES_VOLC, SUF_VOLC, SUF_VOLC, SUF_VOLC,
				45, 20, 35, false, true, GeoNameGen::coerceSuffixSmart
		));
		// Fallback
		KIND_CFG.put(MaterialKind.OTHER, KindCfg.of(
				CORES_GENERIC, SUF_ORE, SUF_CRYSTAL, SUF_ORE,
				45, 35, 20, false, true, GeoNameGen::coerceSuffixSmart
		));
		KIND_CFG.put(MaterialKind.WOOD, KindCfg.of(
				CORES_WOOD, SUF_WOOD, SUF_WOOD, SUF_WOOD,
				50, 20, 30, false, true, GeoNameGen::coerceSuffixSmart
		));
	}

	public static NameInformation generate(long worldSeed, int index, MaterialDef def) {
		Random rng = new Random(mix(worldSeed, 0x6A09E667F3BCC909L, def.kind().ordinal(), index));
		var spawn = def.spawn();
		String display = genDisplay(
				rng, def.kind(), def.primaryColor(),
				spawn.mode(), spawn.biomeTag(), spawn.replaceableTag(), spawn.minY(), spawn.maxY()
		);
		String id = slug(display) + "_" + shortHash(mix(worldSeed, display.hashCode(), def.kind().ordinal()));
		return new NameInformation(RAAMaterials.id(id), display);
	}

	public static NameInformation generate(long worldSeed, int index, MaterialKind kind, int primaryColor, SpawnSpec spawn) {
		Random rng = new Random(mix(worldSeed, 0x6A09E667F3BCC909L, kind.ordinal(), index));
		String display = genDisplay(
				rng, kind, primaryColor,
				spawn.mode(), spawn.biomeTags(), spawn.replaceables(), spawn.y().minY(), spawn.y().maxY()
		);
		String id = slug(display) + "_" + shortHash(mix(worldSeed, display.hashCode(), kind.ordinal()));
		return new NameInformation(RAAMaterials.id(id), display);
	}

	// ===== Kind configuration =====

	private static String genDisplay(Random rng, MaterialKind kind, Integer rgb, SpawnMode mode,
									 List<ResourceLocation> biomeTags, List<ResourceLocation> replaceables,
									 int minY, int maxY) {
		final KindCfg cfg = KIND_CFG.getOrDefault(kind, KIND_CFG.get(MaterialKind.OTHER));

		// Archetype: color/chem + core + suffix | chem + core + suffix | core + suffix
		int arche = weighted(rng, cfg.archetypeWeights);

		String prefix = switch (arche) {
			case 0 -> pickWeightedPrefix(rng, rgb, kind, biomeTags, replaceables, minY, maxY, cfg);
			case 1 -> cfg.preferChemPrefix ? pick(rng, CHEM) : pick(rng, CHEM);
			default -> "";
		};
		prefix = normalizeCombiningPrefix(prefix);

		// --- CHANGED: core selection (species bias for WOOD) ---
		String core = (kind == MaterialKind.WOOD)
				? pickWoodCore(rng, biomeTags)
				: pick(rng, cfg.cores);

		if (prefix.isEmpty() && rng.nextInt(100) < 35) core = smooth(core, rng);

		String link = pick(rng, LINKS);
		String suffix = pickSuffixWithMode(rng, kind, mode, cfg);
		suffix = coerceSuffixSmart(suffix, kind); // coarse coercion on suffix token
		String raw = compose(prefix, link, core, suffix, kind);
		raw = mineralOrthography(raw);

		// final per-kind tweak
		if (cfg.postSuffixCoercion != null) raw = cfg.postSuffixCoercion.apply(raw, kind);

		return Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
	}

	private static int weighted(Random r, int... weights) {
		int sum = 0;
		for (int w : weights) sum += w;
		int roll = r.nextInt(Math.max(sum, 1));
		int acc = 0;
		for (int i = 0; i < weights.length; i++) {
			acc += weights[i];
			if (roll < acc) return i;
		}
		return weights.length - 1;
	}

	private static String pickSuffixWithMode(Random rng, MaterialKind kind, SpawnMode mode, KindCfg cfg) {
		String[] bank;
		if (mode == null) {
			bank = cfg.suffixesDefault;
		} else if (mode == SpawnMode.GEODE || mode == SpawnMode.CLUSTER) {
			bank = cfg.suffixesCrystalLike != null ? cfg.suffixesCrystalLike : cfg.suffixesDefault;
		} else {
			bank = cfg.suffixesOreLike != null ? cfg.suffixesOreLike : cfg.suffixesDefault;
		}
		if (kind == MaterialKind.WOOD) bank = SUF_WOOD;
		return pick(rng, bank);
	}

	// ===== Core generation =====

	private static String pickWeightedPrefix(Random rng, Integer rgb, MaterialKind kind,
											 List<ResourceLocation> biomeTags, List<ResourceLocation> replaceables,
											 int minY, int maxY, KindCfg cfg) {
		List<Choice> pool = new ArrayList<>();

		if (rgb != null && rgb >= 0 && cfg.allowColorPrefix) {
			float[] hsv = rgbToHsv(rgb);
			addHueBuckets(pool, hsv[0]);
			boolean lowSat = hsv[1] < 0.18f, dark = hsv[2] < 0.28f, bright = hsv[2] > 0.80f;
			if (lowSat) add(pool, COLOR_GRAY, 2);
			if (dark) add(pool, COLOR_BLACK, 2);
			if (bright) add(pool, COLOR_MISC, 1);
		}

		// Metals, salts benefit from chem even when color present
		if (cfg.preferChemPrefix) add(pool, CHEM, 4);
		else add(pool, CHEM, 1);

		addDepthBias(pool, minY, maxY);
		if (replaceables != null) for (ResourceLocation rep : replaceables) addReplaceableBias(pool, rep);
		if (biomeTags != null) for (ResourceLocation tag : biomeTags) addBiomeBias(pool, tag);

		String[] bank = pickWeighted(rng, pool);
		return bank[rng.nextInt(bank.length)];
	}

	private static void addHueBuckets(List<Choice> pool, float h) {
		if (h >= 75 && h < 170) add(pool, COLOR_GREEN, 5);
		else if (h >= 170 && h < 205) add(pool, COLOR_TEAL, 4);
		else if (h >= 205 && h < 255) add(pool, COLOR_BLUE, 5);
		else if (h >= 255 && h < 295) add(pool, COLOR_PURPLE, 4);
		else if (h >= 295 && h < 335) add(pool, COLOR_PINK, 3);
		else if (h >= 335 || h < 20) add(pool, COLOR_RED, 4);
		else if (h >= 20 && h < 45) add(pool, COLOR_ORANGE, 4);
		else if (h >= 45 && h < 65) add(pool, COLOR_YELL, 4);
		else add(pool, COLOR_BROWN, 2);
	}

	// ===== Prefix selection with biases =====

	private static void add(List<Choice> list, String[] bank, int w) {
		list.add(new Choice(bank, Math.max(1, w)));
	}

	private static String[] pickWeighted(Random r, List<Choice> pool) {
		int sum = 0;
		for (Choice c : pool) sum += c.weight;
		int roll = r.nextInt(Math.max(sum, 1));
		int acc = 0;
		for (Choice c : pool) {
			acc += c.weight;
			if (roll < acc) return c.bank;
		}
		return pool.isEmpty() ? CHEM : pool.get(0).bank;
	}

	// ===== Utilities & biases =====

	private static float[] rgbToHsv(int rgb) {
		int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
		float rf = r / 255f, gf = g / 255f, bf = b / 255f;
		float max = Math.max(rf, Math.max(gf, bf));
		float min = Math.min(rf, Math.min(gf, bf));
		float delta = max - min;

		float h;
		if (delta == 0) h = 0;
		else if (max == rf) h = 60f * (((gf - bf) / delta) % 6f);
		else if (max == gf) h = 60f * (((bf - rf) / delta) + 2f);
		else h = 60f * (((rf - gf) / delta) + 4f);
		if (h < 0) h += 360f;

		float s = max == 0 ? 0 : delta / max;
		float v = max;
		return new float[]{h, s, v};
	}

	private static String smooth(String core, Random rng) {
		String cv = pick(rng, CON) + pick(rng, VOW);
		String s = cv + core;
		return s.length() < 5 ? s : core;
	}

	private static String mineralOrthography(String s) {
		s = s.replaceAll("([bcdfghjklmnpqrstvwxyz])\\1{2,}", "$1$1");
		s = s.replaceAll("([aeiou])\\1{2,}", "$1$1");
		s = s.replaceAll("ite(ite)+$", "ite");
		s = s.replaceAll("(tephra)(tephra)+$", "tephra");
		s = s.replaceAll("(pumice)(pumice)+$", "pumice");
		s = s.replaceAll("(scoria)(scoria)+$", "scoria");
		s = s.replaceAll("(tuff)(tuff)+$", "tuff");
		s = s.replaceAll("(halite)(halite)+$", "halite");
		s = s.replaceAll("(sylvite)(sylvite)+$", "sylvite");
		if (s.matches("^[bcdfghjklmnpqrstvwxyz]{2}.*") && !s.matches("^(ch|bl|gr|pr|tr|cr|st|gl|pl).*"))
			s = s.substring(1);
		return s;
	}

	private static String tidy(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
		s = s.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");
		for (String b : BAD) s = s.replace(b, b.substring(0, Math.min(2, b.length())));
		if (s.isEmpty()) s = "mineralite";
		return s;
	}

	private static <T> T pick(Random r, T[] arr) {
		return arr[r.nextInt(arr.length)];
	}

	private static String pickWoodCore(Random rng, List<ResourceLocation> biomeTags) {
		if (biomeTags != null) {
			for (ResourceLocation tag : biomeTags) {
				String key = tag.getPath().toLowerCase(Locale.ROOT);
				// direct species hints by biome key
				if (key.contains("birch"))   return "betul";
				if (key.contains("oak"))     return key.contains("dark") ? "darkoak" : "querc";
				if (key.contains("spruce"))  return "pice";
				if (key.contains("taiga"))   return rng.nextBoolean() ? "pice" : "pin";
				if (key.contains("cherry"))  return "cherri";
				if (key.contains("savanna")) return "acac";
				if (key.contains("jungle"))  return rng.nextBoolean() ? "mahog" : "bambu";
				if (key.contains("bamboo"))  return "bambu";
				if (key.contains("mangrove"))return "mangr";
				if (key.contains("swamp"))   return rng.nextBoolean() ? "salix" : "mangr";
				if (key.contains("desert"))  return rng.nextBoolean() ? "acac" : "teak";
				if (key.contains("grove"))   return rng.nextBoolean() ? "abies" : "pice";
			}
		}
		return pick(rng, CORES_WOOD);
	}

	private static String slug(String display) {
		String x = Normalizer.normalize(display, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
		x = x.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("_+", "_");
		x = x.replaceAll("^_+|_+$", "");
		return x.isEmpty() ? "mat" : x;
	}

	private static String shortHash(long v) {
		long h = v ^ (v >>> 33) ^ 0x9E3779B97F4A7C15L;
		String hex = Long.toHexString(h);
		return hex.substring(Math.max(0, hex.length() - 4));
	}

	private static long mix(long a, long b, long c, long d) {
		return mix(mix(a, b, c), d, 0xD6E8FEB86659FD93L);
	}

	private static long mix(long a, long b) {
		return mix(a, b, 0x9E3779B97F4A7C15L);
	}

	private static long mix(long a, long b, long c) {
		long x = a ^ b;
		x ^= Long.rotateLeft(x, 27);
		x = (x + c) * 0x9E3779B97F4A7C15L;
		x ^= x >>> 33;
		x *= 0xC2B2AE3D27D4EB4FL;
		return x ^ (x >>> 29);
	}

	// Depth band: use SpawnRules minY/maxY
	private static void addDepthBias(List<Choice> pool, int minY, int maxY) {
		int mid = (minY + maxY) / 2;
		if (mid <= 0) {
			add(pool, COLOR_BLACK, 3);
			add(pool, COLOR_GRAY, 2);
			add(pool, CHEM, 2);
		} else if (mid >= 80) {
			add(pool, COLOR_WHITE, 3);
			add(pool, COLOR_BLUE, 2);
			add(pool, COLOR_GRAY, 1);
		} else {
			add(pool, COLOR_BROWN, 2);
			add(pool, COLOR_GREEN, 1);
			add(pool, CHEM, 1);
		}
	}

	// Replaceables (stone, deepslate, sand, gravel, etc.)
	private static void addReplaceableBias(List<Choice> pool, ResourceLocation replaceableTag) {
		if (replaceableTag == null) return;
		String key = replaceableTag.getPath().toLowerCase(Locale.ROOT);

		if (key.contains("deepslate")) {
			add(pool, COLOR_BLACK, 2);
			add(pool, COLOR_GRAY, 2);
		}
		if (key.contains("stone")) {
			add(pool, COLOR_GRAY, 1);
			add(pool, COLOR_BROWN, 1);
		}
		if (key.contains("sand")) {
			add(pool, COLOR_YELL, 2);
			add(pool, COLOR_ORANGE, 1);
		}
		if (key.contains("gravel")) {
			add(pool, COLOR_GRAY, 2);
		}
		if (key.contains("terracotta") || key.contains("red_sand")) {
			add(pool, COLOR_RED, 1);
			add(pool, COLOR_ORANGE, 1);
		}
	}

	private static void addBiomeBias(List<Choice> pool, ResourceLocation biomeKey) {
		if (biomeKey == null) return;
		String key = biomeKey.getPath().toLowerCase(Locale.ROOT);

		if (key.contains("snow") || key.contains("frozen") || key.contains("ice") || key.contains("mountain") || key.contains("grove")) {
			add(pool, COLOR_WHITE, 2);
			add(pool, COLOR_BLUE, 2);
			add(pool, COLOR_GRAY, 1);
		}
		if (key.contains("desert") || key.contains("badlands") || key.contains("mesa") || key.contains("savanna")) {
			add(pool, COLOR_YELL, 2);
			add(pool, COLOR_ORANGE, 2);
			add(pool, COLOR_BROWN, 1);
		}
		if (key.contains("jungle") || key.contains("forest") || key.contains("swamp") || key.contains("mangrove") || key.contains("bamboo")) {
			add(pool, COLOR_GREEN, 3);
			add(pool, COLOR_TEAL, 1);
		}
		if (key.contains("cave") || key.contains("deep") || key.contains("dripstone") || key.contains("lush_caves")) {
			add(pool, COLOR_GRAY, 2);
			add(pool, COLOR_GREEN, 1);
		}
		if (key.contains("ocean") || key.contains("river") || key.contains("beach") || key.contains("reef")) {
			add(pool, COLOR_BLUE, 3);
			add(pool, COLOR_TEAL, 2);
		}
		if (key.contains("nether") || key.contains("basalt") || key.contains("crimson") || key.contains("warped") || key.contains("soul")) {
			add(pool, CHEM, 3);
			add(pool, COLOR_RED, 2);
			add(pool, COLOR_ORANGE, 1);
		}
		if (key.contains("end")) {
			add(pool, COLOR_MISC, 3);
			add(pool, COLOR_PURPLE, 2);
			add(pool, COLOR_GRAY, 1);
		}

		// --- NEW: micro-bias for cherry/birch towards light/pink naming prefixes ---
		if (key.contains("cherry") || key.contains("birch")) {
			add(pool, COLOR_PINK, 2);
			add(pool, COLOR_WHITE, 1);
		}
	}

	// Normalize common combining forms
	private static String normalizeCombiningPrefix(String p) {
		if (p == null || p.isEmpty()) return p;
		return switch (p) {
			case "ferr" -> "ferro";
			case "cupr" -> "cupro";
			case "argent" -> "argento";
			case "aur" -> "auro";
			case "silic" -> "silico";
			case "calc" -> "calci";
			case "magnes" -> "magneso";
			case "mangan" -> "mangano";
			case "stann" -> "stanno";
			case "plumb" -> "plumbo";
			case "cobalt" -> "cobalto";
			case "nickel" -> "nickelo";
			case "sulf" -> "sulfo";
			case "bor" -> "boro";
			case "fluor" -> "fluoro";
			case "lith" -> "lithio";
			case "sod" -> "sodo";
			case "potass" -> "potasso";
			default -> p;
		};
	}

	/** Coerce suffix in gem/crystal edge cases; light touch so it works for all kinds. */
	private static String coerceSuffixSmart(String s, MaterialKind kind) {
		if (kind == MaterialKind.WOOD) {
			if (s == null || s.isEmpty()) return "wood";
			if (s.equals("log")) return "wood";
			return s;
		}
		return s;
	}

    /**
	* Compose final name from parts with per-kind canonicalization:
	* - Avoid 'conglomconglomerate', 'rudrudite', 'sylvsylvite', 'tephrtephra', etc.
	* - Normalize volcanic products (obsidian/pumice/scoria/tephra/tuff).
	* - Normalize salt products (halite/sylvite/gypsum/anhydrite).
	*/
	private static String compose(String prefix, String link, String core, String suffix, MaterialKind kind) {
		String p = prefix == null ? "" : prefix;
		String l = (p.isEmpty() ? "" : link == null ? "" : link);
		String c = core == null ? "" : core;
		String s = suffix == null ? "" : suffix;

		String raw;
		switch (kind) {
			case GRAVEL -> {
				if (c.startsWith("conglom") && s.contains("conglomerate")) return "conglomerate";
				if (c.startsWith("brecc")   && (s.contains("breccia") || s.contains("rudite"))) return "breccia";
				if (c.startsWith("rud")     && s.contains("rudite")) return "rudite";
				if (c.startsWith("gruss") && s.contains("gravel")) return (p.isEmpty() ? "" : p + l) + "gruss";
				if (c.startsWith("rud") && s.contains("conglomerate")) return (p.isEmpty() ? "" : p + l) + "conglomerate";
				raw = p + l + c + s;
			}
			case SAND -> {
				if (c.startsWith("aren") && s.contains("sandstone")) return (p.isEmpty() ? "" : p + l) + "sandstone";
				if (c.startsWith("aren") && s.contains("arenite")) return (p.isEmpty() ? "" : p + l) + "arenite";
				if (c.contains("psamm") && s.equals("sand")) return (p.isEmpty() ? "" : p + l) + "psammite";
				raw = p + l + c + s;
			}
			case CLAY -> {
				if (c.startsWith("kaolin") && s.contains("kaolinite"))
					return (p.isEmpty() ? "" : p + l) + "kaolinite";
				if (c.startsWith("argill") && (s.contains("clay") || s.contains("argillite")))
					return (p.isEmpty() ? "" : p + l) + (s.contains("argillite") ? "argillite" : "clay");
				if (c.endsWith("illite") && s.contains("argillite"))
					return (p.isEmpty() ? "" : p + l) + "argillite";
				if (c.startsWith("benton")) return (p.isEmpty() ? "" : p + l) + "bentonite";
				raw = p + l + c + s;
			}
			case MUD -> {
				if (c.startsWith("pelit") && s.contains("pelite"))
					return (p.isEmpty() ? "" : p + l) + "pelite";
				if (c.startsWith("lutit") && s.contains("lutite"))
					return (p.isEmpty() ? "" : p + l) + "lutite";
				if (c.startsWith("silt") && s.contains("mudstone"))
					return (p.isEmpty() ? "" : p + l) + "mudstone";
				raw = p + l + c + s;
			}
			case SALT -> {
				if (c.startsWith("halit") && (s.contains("halite") || s.contains("salt") || s.contains("evaporite")))
					return (p.isEmpty() ? "" : p + l) + "halite";
				if (c.startsWith("sylv") && (s.contains("sylvite") || s.contains("salt") || s.contains("evaporite")))
					return (p.isEmpty() ? "" : p + l) + "sylvite";
				if (c.startsWith("gyps") && (s.contains("gypsum") || s.contains("salt") || s.contains("evaporite")))
					return (p.isEmpty() ? "" : p + l) + "gypsum";
				if (c.startsWith("anhydr") && (s.contains("anhydrite") || s.contains("salt") || s.contains("evaporite")))
					return (p.isEmpty() ? "" : p + l) + "anhydrite";
				if (c.startsWith("epsom") && (s.contains("salt") || s.contains("evaporite")))
					return (p.isEmpty() ? "" : p + l) + "epsomite";
				if (c.startsWith("mirab") && (s.contains("salt") || s.contains("evaporite") || s.contains("halite")))
					return (p.isEmpty() ? "" : p + l) + "mirabilite";
				if (c.startsWith("carnal") && (s.contains("salt") || s.contains("evaporite") || s.contains("halite")))
					return (p.isEmpty() ? "" : p + l) + "carnalite";
				if (c.startsWith("trona") && (s.contains("salt") || s.contains("evaporite")))
					return (p.isEmpty() ? "" : p + l) + "trona";
				raw = p + l + c + s;
			}
			case VOLCANIC -> {
				// Block "pumice": redirect to tephra
				if (c.startsWith("pumic")) return (p.isEmpty() ? "" : p + l) + "tephra";
				if (c.startsWith("scori")) return (p.isEmpty() ? "" : p + l) + "scoria";
				if (c.startsWith("tephr")) return (p.isEmpty() ? "" : p + l) + "tephra";
				if (c.startsWith("tuff"))  return (p.isEmpty() ? "" : p + l) + "tuff";

				// Block "obsidian": redirect to vitrophyre
				if (c.startsWith("obsid")) return (p.isEmpty() ? "" : p + l) + "vitrophyre";

				// Glass handling
				if (s.contains("glass")) {
					if (c.startsWith("basalt")) return (p.isEmpty() ? "" : p + l) + "tachylite";
					return (p.isEmpty() ? "" : p + l) + "vitrophyre";
				}
				raw = p + l + c + s;
			}
			case SOIL -> {
				if (c.endsWith("sol") && (s.contains("soil") || s.contains("sol") || s.contains("dirt")))
					return (p.isEmpty() ? "" : p + l) + c;
				if (c.startsWith("loess") && (s.contains("soil") || s.contains("sol") || s.contains("lite")))
					return (p.isEmpty() ? "" : p + l) + "loess";
				raw = p + l + c + s;
			}
			case WOOD -> {
				// collapse redundant endings like "woodwood"
				if (c.endsWith("wood")) c = c.replaceAll("wood$", "");
				raw = p + l + c + s;
				// soften awkward clusters (e.g., "quercwood" -> "querwood")
				raw = raw.replaceAll("rcw", "rw").replaceAll("cwood$", "wood");
				// special names: "ironw" + wood-like suffix => "ironwood"
				raw = raw.replaceAll("^ironw(timber|plank|bark|heartwood|wood)$", "ironwood");
			}
			default -> raw = p + l + c + s;
		}

		// Post-build cleanups
		raw = raw.replaceFirst("^recc", "brecc");
		if (raw.startsWith("brecc") && raw.endsWith("rudite")) raw = raw.replaceAll("rudite$", "ia");

		raw = raw
				.replaceAll("(aren)\\1(ite)", "$1$2")
				.replaceAll("(rud)\\1(ite)", "$1$2")
				.replaceAll("(conglom)\\1(erate)", "$1$2")
				.replaceAll("(sylv)\\1(ite)", "$1$2")
				.replaceAll("(halit)\\1(e)", "$1$2")
				.replaceAll("(gyps)\\1(um)", "$1$2")
				.replaceAll("(tephr)\\1(a)", "$1$2")
				.replaceAll("(pumic)\\1(e)", "$1$2")
				.replaceAll("(brecc)\\1(ia)", "$1$2");

		raw = raw
				.replaceAll("solsoil$", "sol")
				.replaceAll("soilsol$", "sol")
				.replaceAll("(sol)(?:\\1)+$", "$1")
				.replaceAll("(loess)(?:sol|soil|lite)$", "$1");

		raw = raw.replaceAll("^chloro?chlorit", "chlorit");

		// Block vanilla names globally
		raw = raw.replaceAll("obsidian$", "vitrophyre")
				.replaceAll("obsidian", "vitrophyre")
				.replaceAll("pumice$", "tephra")
				.replaceAll("pumice", "tephra");

		return tidy(raw);
	}

	// helpers
	private static String[] merge(String[] a, String[] b) {
		String[] out = Arrays.copyOf(a, a.length + b.length);
		System.arraycopy(b, 0, out, a.length, b.length);
		return out;
	}

	// Quick manual test
	public static void main() {
		long seed = new SecureRandom().nextLong();
		MaterialSet set = MaterialGenerator.generate(seed);
		for (int i = 0; i < set.all().size(); i++) {
			var mat = set.all().get(i);
			System.out.println("Type: " + mat.kind().getSerializedName() + " " + generate(seed, i, mat) + " " + mat.spawn().biomeTag());
		}
		// Extra smoke test for new families:
		var kinds = List.of(MaterialKind.SAND, MaterialKind.GRAVEL, MaterialKind.CLAY, MaterialKind.MUD, MaterialKind.SALT, MaterialKind.VOLCANIC, MaterialKind.SOIL);
		for (int i = 0; i < 8; i++) {
			for (var k : kinds) {
				// SpawnRules(SpawnMode mode, int veinSize, int veinsPerChunk, int minY, int maxY, int yPeak, List<String> biomeTag, List<String> replaceableTag)
				var n = generate(seed, i, k, 0x7fbf5f,
						new VeinSpec(
								List.of(ResourceLocation.withDefaultNamespace("desert")),
								List.of(SpawnSpec.Target.block(ResourceLocation.withDefaultNamespace("sand"))),
								SpawnSpec.YBand.triangle(32, 64, 48),
								7,
								18
						)
				);
				System.out.println(k + " -> " + n.displayName() + " [" + n.id() + "]");
			}
		}
	}

	/** Per-kind configurable name generation behavior. */
	public static final class KindCfg {
		final String[] cores;
		final String[] suffixesOreLike;     // when SpawnMode suggests ore vein
		final String[] suffixesCrystalLike; // when geode/cluster
		final String[] suffixesDefault;     // fallback
		final int[] archetypeWeights;       // [color/chem + core, chem + core, core only]
		final boolean preferChemPrefix;     // e.g., metals
		final boolean allowColorPrefix;     // e.g., gems/crystals/soils/sands
		final BiFunction<String, MaterialKind, String> postSuffixCoercion; // optional post-processing

		private KindCfg(String[] cores,
						String[] suffixesOreLike,
						String[] suffixesCrystalLike,
						String[] suffixesDefault,
						int[] archetypeWeights,
						boolean preferChemPrefix,
						boolean allowColorPrefix,
						BiFunction<String, MaterialKind, String> postSuffixCoercion) {
			this.cores = cores;
			this.suffixesOreLike = suffixesOreLike;
			this.suffixesCrystalLike = suffixesCrystalLike;
			this.suffixesDefault = suffixesDefault;
			this.archetypeWeights = archetypeWeights;
			this.preferChemPrefix = preferChemPrefix;
			this.allowColorPrefix = allowColorPrefix;
			this.postSuffixCoercion = postSuffixCoercion;
		}

		public static KindCfg of(String[] cores, String[] sufOre, String[] sufCrystal, String[] sufDefault,
								 int w0, int w1, int w2, boolean preferChem, boolean allowColor,
								 BiFunction<String, MaterialKind, String> post) {
			return new KindCfg(cores, sufOre, sufCrystal, sufDefault, new int[]{w0, w1, w2}, preferChem, allowColor, post);
		}
	}

	private record Choice(String[] bank, int weight) {
	}
}
