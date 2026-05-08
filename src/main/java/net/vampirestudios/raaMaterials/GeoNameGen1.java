// src/main/java/net/vampirestudios/raaMaterials/material/GeoNameGen.java
package net.vampirestudios.raaMaterials;

import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.material.*;
import net.vampirestudios.raaMaterials.material.MaterialDef.NameInformation;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.*;
import java.util.function.BiFunction;

public final class GeoNameGen1 {
	private GeoNameGen1() {}

	private static final String[] COLOR_GREEN = {"chloro", "viridi", "smarag", "prasi"};
	private static final String[] COLOR_BLUE = {"cyano", "azure", "lazu", "caerul", "sapph"};
	private static final String[] COLOR_RED = {"erythro", "rubri", "rhodo", "carmi"};
	private static final String[] COLOR_YELLOW = {"xantho", "flavo", "auri", "fulvo"};
	private static final String[] COLOR_BLACK = {"melano", "nigri", "anthra"};
	private static final String[] COLOR_WHITE = {"leuco", "alba", "argento"};
	private static final String[] COLOR_MISC = {"irido", "opal", "luci", "phospho"};
	private static final String[] COLOR_ORANGE = {"croco", "auran", "citrin", "pyro", "tawny"};
	private static final String[] COLOR_PURPLE = {"porphyr", "purpur", "amethyst", "violac"};
	private static final String[] COLOR_BROWN = {"brun", "umbr", "fulig", "ruf", "murrh"};
	private static final String[] COLOR_GRAY = {"cinere", "argill", "plumbe", "grise"};
	private static final String[] COLOR_PINK = {"roseo", "rhodon", "candi", "rubell"};
	private static final String[] COLOR_TEAL = {"teal", "turqu", "viridcyan", "aqua"};

	private static final String[] CHEM = {
			"silic", "calc", "dolom", "magnes", "ferr", "cupr", "stann", "plumb", "mangan",
			"cobalt", "nickel", "argent", "aur", "bor", "fluor", "sulf", "lith", "sod", "potass"
	};

	private static final String[] CORES_GENERIC = {
			"spar", "blende", "feld", "chalc", "pyrox", "amphib", "oliv", "gran", "tourmal",
			"topaz", "beryl", "garnet", "quartz", "agate", "opal", "jade", "corund", "azur",
			"malach", "dolom", "rhodo", "serpentin", "barit", "halit", "graph", "mica"
	};

	private static final String[] CORES_METAL = {"chalc", "sulf", "magnet", "hemat", "cupr", "ferr", "argent", "stann", "plumb", "nickel"};
	private static final String[] CORES_GEM = {
			"garnet", "beryl", "topaz", "jade", "opal",
			"agate", "tourmal", "corund", "spinel", "zircon"
	};
	private static final String[] CORES_CRYSTAL = {"quartz", "spar", "cryst", "calc", "dolom", "halit", "fluor", "selen", "aragon", "apat"};
	private static final String[] CORES_ALLOY = {"ferro", "stanno", "cuprio", "nickelo", "argento", "auro"};
	private static final String[] CORES_SOIL = {"argill", "humic", "loess", "peat", "gelisol", "andosol", "vertis", "ultis"};
	private static final String[] CORES_SAND = {"quartz", "aren", "silic", "feld", "lith", "psamm", "arkos"};
	private static final String[] CORES_GRAVEL = {"rud", "brecc", "conglom", "lith", "quartz", "gruss"};
	private static final String[] CORES_CLAY = {"kaolin", "illite", "smect", "montmor", "chlorit", "benton", "argill"};
	private static final String[] CORES_MUD = {"pelit", "lutit", "silt", "argill", "turbid"};
	private static final String[] CORES_SALT = {"halit", "sylv", "gyps", "anhydr", "carnal", "mirab", "trona", "epsom"};
	private static final String[] CORES_VOLC = {"tuff", "pumic", "obsid", "scori", "basalt", "andes", "dacite", "rhyol", "tephr"};
	private static final String[] CORES_WOOD = {
			"querc", "betul", "pice", "abies", "laric", "pin", "salix", "acac", "ulmus", "juglan",
			"coryl", "acer", "tilia", "cedr", "mahog", "ebon", "bambu", "mangr", "cherri",
			"darkoak", "baoba", "teak", "ironw"
	};
	private static final String[] CORES_REAL_MINERAL = {
			"quartz", "feldspar", "mica", "calcite", "dolomite",
			"hematite", "magnetite", "malachite", "azurite", "barite",
			"halite", "gypsum", "fluorite", "apatite", "olivine",
			"pyrite", "galena", "cinnabar", "orpiment", "realgar"
	};

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
	private static final String[] CON = {
			"b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "r", "s", "t", "v", "z",
			"br", "cr", "dr", "fr", "gr", "kr", "pr", "tr", "str", "pl", "gl", "cl", "sl"
	};

	private static final String[] BAD = {"jj", "vv", "ww", "yy", "qq", "hhh", "kkk", "zzz", "xxx", "aaae", "iii", "uuu"};

	private static final Set<String> BLOCKED_NAMES = Set.of(
			"stone", "dirt", "sand", "gravel", "clay", "mud", "wood"
	);

	private static final Map<MaterialKind, KindCfg> KIND_CFG = new EnumMap<>(MaterialKind.class);

	static {
		KIND_CFG.put(MaterialKind.METAL, KindCfg.of(
				CORES_METAL,
				SUF_ORE, SUF_ORE, SUF_ORE,
				25, 55, 20, true, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.ALLOY, KindCfg.of(
				CORES_ALLOY,
				SUF_ALLOY, SUF_ALLOY, SUF_ALLOY,
				0, 0, 100, false, false, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.GEM, KindCfg.of(
				merge(CORES_GEM, merge(CORES_GENERIC, CORES_REAL_MINERAL)),
				SUF_GEM, SUF_GEM, SUF_GEM,
				55, 25, 20, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.CRYSTAL, KindCfg.of(
				merge(CORES_CRYSTAL, CORES_GENERIC),
				SUF_ORE, SUF_CRYSTAL, SUF_CRYSTAL,
				50, 25, 25, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.STONE, KindCfg.of(
				merge(CORES_GENERIC, CORES_REAL_MINERAL),
				SUF_ORE, SUF_CRYSTAL, SUF_ORE,
				40, 35, 25, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.SOIL, KindCfg.of(
				CORES_SOIL,
				SUF_SOIL, SUF_SOIL, SUF_SOIL,
				55, 15, 30, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.SAND, KindCfg.of(
				CORES_SAND,
				SUF_SAND, SUF_SAND, SUF_SAND,
				50, 10, 40, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.GRAVEL, KindCfg.of(
				CORES_GRAVEL,
				SUF_GRAVEL, SUF_GRAVEL, SUF_GRAVEL,
				45, 10, 45, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.CLAY, KindCfg.of(
				CORES_CLAY,
				SUF_CLAY, SUF_CLAY, SUF_CLAY,
				50, 15, 35, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.MUD, KindCfg.of(
				CORES_MUD,
				SUF_MUD, SUF_MUD, SUF_MUD,
				25, 5, 70, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.SALT, KindCfg.of(
				CORES_SALT,
				SUF_SALT, SUF_SALT, SUF_SALT,
				40, 30, 30, true, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.VOLCANIC, KindCfg.of(
				CORES_VOLC,
				SUF_VOLC, SUF_VOLC, SUF_VOLC,
				45, 20, 35, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.WOOD, KindCfg.of(
				CORES_WOOD,
				SUF_WOOD, SUF_WOOD, SUF_WOOD,
				50, 20, 30, false, true, GeoNameGen1::coerceSuffixSmart
		));

		KIND_CFG.put(MaterialKind.OTHER, KindCfg.of(
				CORES_GENERIC,
				SUF_ORE, SUF_CRYSTAL, SUF_ORE,
				45, 35, 20, false, true, GeoNameGen1::coerceSuffixSmart
		));
	}

	public static NameInformation generate(long worldSeed, int index, MaterialDef def) {
		return generate(worldSeed, index, def.kind(), def.primaryColor(), def.spawn());
	}

	public static NameInformation generate(long worldSeed, int index, MaterialKind kind, int primaryColor, SpawnInfo spawn) {
		Random rng = new Random(mix(worldSeed, 0x6A09E667F3BCC909L, kind.ordinal(), index));

		String display = genDisplay(rng, kind, primaryColor, spawn);
		String id = slug(display) + "_" + shortHash(mix(worldSeed, index, kind.ordinal()));

		return new NameInformation(RAAMaterials.id(id), display);
	}

	public static NameInformation generateUnique(
			long worldSeed,
			int index,
			MaterialKind kind,
			int primaryColor,
			SpawnInfo spawn,
			Set<String> usedDisplayNames
	) {
		Random rng = new Random(mix(worldSeed, 0x6A09E667F3BCC909L, kind.ordinal(), index));

		String display = null;

		for (int tries = 0; tries < 12; tries++) {
			display = genDisplay(rng, kind, primaryColor, spawn);
			String key = display.toLowerCase(Locale.ROOT);

			if (usedDisplayNames.add(key)) break;
		}

		if (display == null) {
			display = genDisplay(rng, kind, primaryColor, spawn);
		}

		String id = slug(display) + "_" + shortHash(mix(worldSeed, index, kind.ordinal()));
		return new NameInformation(RAAMaterials.id(id), display);
	}

	private static String genDisplay(Random rng, MaterialKind kind, Integer rgb, SpawnInfo spawn) {
		KindCfg cfg = KIND_CFG.getOrDefault(kind, KIND_CFG.get(MaterialKind.OTHER));

		NameParts parts = pickNameParts(rng, kind, rgb, spawn, cfg);

		String raw = renderDisplay(parts);
		raw = displayOrthography(raw);

		if (cfg.postSuffixCoercion != null) {
			raw = cfg.postSuffixCoercion.apply(raw, kind);
		}

		raw = collapseRealMineralSuffix(raw);
		raw = cleanDisplay(raw);

		String key = raw.toLowerCase(Locale.ROOT).replace(" ", "");
		if (BLOCKED_NAMES.contains(key)) {
			raw = raw + "ite";
		}

		return titleWords(raw);
	}

	private static String collapseRealMineralSuffix(String s) {
		return s
				.replaceAll("(ite)(ite|lite|ine|aline|olite)$", "$1")
				.replaceAll("(ite)(ide|ate|anite|onite)$", "$1")
				.replaceAll("(ine)(ite|ine)$", "$1");
	}

	private static boolean isLooseMaterial(MaterialKind kind) {
		return kind == MaterialKind.SAND
				|| kind == MaterialKind.GRAVEL
				|| kind == MaterialKind.CLAY
				|| kind == MaterialKind.MUD
				|| kind == MaterialKind.SOIL;
	}

	private static NameParts pickNameParts(Random rng, MaterialKind kind, Integer rgb, SpawnInfo spawn, KindCfg cfg) {
		int archetype = weighted(rng, cfg.archetypeWeights);

		int minY = spawn.y().minY();
		int maxY = spawn.y().maxY();

		String prefix = switch (archetype) {
			case 0 -> pickWeightedPrefix(rng, rgb, kind, minY, maxY, cfg);
			case 1 -> isLooseMaterial(kind) ? "" : pick(rng, CHEM);
			default -> "";
		};

		prefix = normalizeCombiningPrefix(prefix);

		String core = kind == MaterialKind.WOOD
				? pick(rng, CORES_WOOD)
				: pick(rng, cfg.cores);

		if (prefix.isEmpty() && rng.nextInt(100) < 35) {
			core = smooth(core, rng);
		}

		String suffix;
		int tries = 0;

		do {
			suffix = pickSuffixWithMode(rng, kind, cfg, spawn);
			suffix = coerceSuffixSmart(suffix, kind);
			tries++;
		} while (isBadCombination(kind, core, suffix) && tries < 8);

		String link = pick(rng, LINKS);

		return new NameParts(prefix, link, core, suffix, kind);
	}

	private static String pickSuffixWithMode(Random rng, MaterialKind kind, KindCfg cfg, SpawnInfo spawn) {
		if (kind == MaterialKind.WOOD) {
			return pick(rng, SUF_WOOD);
		}

		SpawnInfo.VeinShape shape = spawn.shape();

		if (shape == SpawnInfo.VeinShape.ORE_BLOB) {
			return pick(rng, cfg.suffixesOreLike != null ? cfg.suffixesOreLike : cfg.suffixesDefault);
		}

		return pick(rng, cfg.suffixesDefault != null ? cfg.suffixesDefault : cfg.suffixesOreLike);
	}

	private static String renderDisplay(NameParts parts) {
		String stem = joinMorphemes(parts.prefix(), parts.link(), parts.core());

		return switch (parts.kind()) {
			case SAND, GRAVEL, CLAY, MUD, SOIL -> renderLooseMaterial(stem, parts.suffix());
			case WOOD -> renderWood(stem, parts.suffix());
			default -> compose(parts.prefix(), parts.link(), parts.core(), parts.suffix(), parts.kind());
		};
	}

	private static String renderLooseMaterial(String stem, String suffix) {
		stem = tidy(stem);

		return switch (suffix) {
			case "sand", "gravel", "clay", "mud", "soil", "dirt", "mudstone", "shale" -> stem + " " + suffix;
			default -> tidy(stem + suffix);
		};
	}

	private static String renderWood(String stem, String suffix) {
		stem = tidy(stem);

		if (stem.endsWith("wood")) {
			stem = stem.replaceAll("wood$", "");
		}

		String raw = switch (suffix) {
			case "wood", "bark", "heartwood", "timber", "plank" -> stem + " " + suffix;
			default -> tidy(stem + suffix);
		};

		raw = raw.replaceAll("\\bironw (wood|timber|plank|bark|heartwood)\\b", "ironwood");
		raw = raw.replaceAll("cwood$", "wood");

		return raw;
	}

	private static String joinMorphemes(String a, String link, String b) {
		if (a == null || a.isEmpty()) return b == null ? "" : b;
		if (b == null || b.isEmpty()) return a;

		char last = a.charAt(a.length() - 1);
		char first = b.charAt(0);

		boolean lastVowel = isVowel(last);
		boolean firstVowel = isVowel(first);

		if (lastVowel && firstVowel) return a + b;
		if (!lastVowel && !firstVowel) return a + link + b;

		return a + b;
	}

	private static boolean isVowel(char c) {
		return "aeiouy".indexOf(Character.toLowerCase(c)) >= 0;
	}

	private static boolean isBadCombination(MaterialKind kind, String core, String suffix) {
		return switch (kind) {
			case GEM -> suffix.equals("blende")
					|| suffix.equals("ate")
					|| core.equals("blende")
					|| core.equals("rhodo") && (suffix.equals("ar") || suffix.equals("on") || suffix.equals("el"))
					|| core.equals("malach") && (suffix.equals("on") || suffix.equals("ar") || suffix.equals("el"))
					|| core.equals("agate") && suffix.equals("el");

			case STONE -> core.equals("fluorite") && suffix.equals("blende")
					|| core.equals("oliv") && suffix.equals("ite");

			case CRYSTAL -> core.equals("fluor") && suffix.equals("lite")
					|| core.equals("oliv") && suffix.equals("cryst")
					|| core.equals("barit") && suffix.equals("lite");

			case SAND -> core.equals("silic") && suffix.equals("aren");

			case WOOD -> suffix.equals("heartwood") && core.equals("bambu");
			case SALT -> suffix.equals("salt") && core.equals("halit");

			case MUD -> core.equals("turbid") && suffix.equals("mud")
					|| core.equals("lutit") && suffix.equals("lutite")
					|| core.equals("pelit") && suffix.equals("pelite");

			case CLAY -> core.equals("benton") && !suffix.equals("kaolinite");

			default -> false;
		};
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

	private static String pickWeightedPrefix(Random rng, Integer rgb, MaterialKind kind, int minY, int maxY, KindCfg cfg) {
		List<Choice> pool = new ArrayList<>();

		if (rgb != null && rgb >= 0 && cfg.allowColorPrefix) {
			float[] hsv = rgbToHsv(rgb);
			addHueBuckets(pool, hsv[0]);

			boolean lowSat = hsv[1] < 0.18f;
			boolean dark = hsv[2] < 0.28f;
			boolean bright = hsv[2] > 0.80f;

			if (lowSat) add(pool, COLOR_GRAY, 2);
			if (dark) add(pool, COLOR_BLACK, 2);
			if (bright) add(pool, COLOR_MISC, 1);
		}

		if (!isLooseMaterial(kind)) {
			if (cfg.preferChemPrefix) add(pool, CHEM, 4);
			else add(pool, CHEM, 1);
		}

		addDepthBias(pool, kind, minY, maxY);

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
		else if (h >= 45 && h < 65) add(pool, COLOR_YELLOW, 4);
		else add(pool, COLOR_BROWN, 2);
	}

	private static void add(List<Choice> list, String[] bank, int weight) {
		list.add(new Choice(bank, Math.max(1, weight)));
	}

	private static String[] pickWeighted(Random rng, List<Choice> pool) {
		if (pool.isEmpty()) return CHEM;

		int sum = 0;
		for (Choice choice : pool) sum += choice.weight;

		int roll = rng.nextInt(Math.max(sum, 1));
		int acc = 0;

		for (Choice choice : pool) {
			acc += choice.weight;
			if (roll < acc) return choice.bank;
		}

		return pool.get(0).bank;
	}

	private static float[] rgbToHsv(int rgb) {
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;

		float rf = r / 255f;
		float gf = g / 255f;
		float bf = b / 255f;

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

	private static String displayOrthography(String s) {
		s = s.replaceAll("([bcdfghjklmnpqrstvwxyz])\\1{2,}", "$1$1");
		s = s.replaceAll("([aeiou])\\1{2,}", "$1$1");

		s = s.replaceAll("ite(ite)+$", "ite");

		// real/mineral suffix cleanup
		s = s.replaceAll("(ite)(ite|lite|ine|aline|olite|ide|ate|anite|onite)$", "$1");
		s = s.replaceAll("(graph)(cryst)$", "$1ite");
		s = s.replaceAll("(opal)(ite)$", "$1");
		s = s.replaceAll("(oliv)(ate)$", "$1ine");

		// sediment duplicate cleanup
		s = s.replaceAll("(lutit)(lutite)$", "$2");
		s = s.replaceAll("(pelit)(pelite)$", "$2");
		s = s.replaceAll("(benton)(shale|clay|mudstone|argillite)$", "bentonite");

		s = s.replaceAll("(tephra)(tephra)+$", "tephra");
		s = s.replaceAll("(pumice)(pumice)+$", "pumice");
		s = s.replaceAll("(scoria)(scoria)+$", "scoria");
		s = s.replaceAll("(tuff)(tuff)+$", "tuff");
		s = s.replaceAll("(halite)(halite)+$", "halite");
		s = s.replaceAll("(sylvite)(sylvite)+$", "sylvite");

		s = s.replaceAll("(ferro)(ferro)(metal|alloy|steel|bronze|tine)$", "$1$3");
		s = s.replaceAll("(stanno)(stanno)(metal|alloy|steel|bronze|tine)$", "$1$3");
		s = s.replaceAll("(cuprio)(cuprio)(metal|alloy|steel|bronze|tine)$", "$1$3");
		s = s.replaceAll("(nickelo)(nickelo)(metal|alloy|steel|bronze|tine)$", "$1$3");
		s = s.replaceAll("(argento)(argento)(metal|alloy|steel|bronze|tine)$", "$1$3");
		s = s.replaceAll("(auro)(auro)(metal|alloy|steel|bronze|tine)$", "$1$3");

		s = s.replaceAll("(serpentin)(ate|ite|ide|onite)$", "serpentinite");
		s = s.replaceAll("(barit)(ite|ate|ide)$", "barite");
		s = s.replaceAll("(rhodo)(ite|ide|ate|onite)$", "rhodonite");
		s = s.replaceAll("(tourmal)(ide|ite|ate|onite)$", "tourmaline");
		s = s.replaceAll("(quartz)(onite|ite|ide|ate)$", "quartzite");
		s = s.replaceAll("(cryst)(ite|lite|ine)$", "crystal");

		// awkward gem endings
		s = s.replaceAll("(rhodo)(ar|on|el|aline|olite)$", "rhodolite");
		s = s.replaceAll("(malach)(on|ar|el|aline|olite)$", "malachite");
		s = s.replaceAll("(agate)(el|ar|on|aline|olite)$", "agate");

		// awkward crystal/stone endings
		s = s.replaceAll("(fluor)(lite|ite|ine|cryst)$", "fluorite");
		s = s.replaceAll("(oliv)(ite|cryst|lite)$", "olivine");
		s = s.replaceAll("(barit)(lite|ite|cryst)$", "barite");

		// bad full-material suffixes
		s = s.replaceAll("(fluorite)blende$", "$1");
		s = s.replaceAll("(magnet)ite$", "magnetite");

		// sand cleanup
		s = s.replaceAll("(silic)aren$", "silica sand");
		s = s.replaceAll("(aren)aren$", "arenite");
		s = s.replaceAll("(quartz)aren$", "quartz sand");

		// color-prefix cleanup
		s = s.replaceAll("^smarago", "smarag");
		s = s.replaceAll("^prasibarit", "prasi barit");

		// mineral-specific normalization
		s = s.replaceAll("(azur)(lite|ite|ine|cryst)$", "azurite");
		s = s.replaceAll("(pyrox)(ine|ite|lite)$", "pyroxene");
		s = s.replaceAll("(argent)(ide|ite|ate|anite|onite)$", "argentite");
		s = s.replaceAll("(sulf)(anite|onite|ite|ide|ate)$", "sulfide");
		s = s.replaceAll("(magnet)(onite|anite|ite|ide|ate)$", "magnetite");
		s = s.replaceAll("(chalc)(ite|ide|ate|onite)$", "chalcopyrite");
		s = s.replaceAll("(corund)(aline|el|ar|on|ite|ine)$", "corundum");

		// awkward bare chemical compounds
		s = s.replaceAll("^ferrate$", "ferrite");

		// gravel duplicate cleanup
		s = s.replaceAll("(rud)(rudite)$", "$2");

		// alloy duplicate/prefix cleanup
		s = s.replaceAll("^(boro)(ferro|stanno|cuprio|nickelo|argento|auro)(metal|alloy|steel|bronze)$", "$2$3");

		// gem cleanup
		s = s.replaceAll("(topaz)(olite|on|el|ar|aline|ine|ite)$", "topaz");
		s = s.replaceAll("(zircon)(on|el|ar|aline|olite|ine)$", "zircon");
		s = s.replaceAll("(dolomite)(el|ar|on|aline|olite|ine)$", "dolomite");
		s = s.replaceAll("(barit)(ine|el|ar|on|aline|olite)$", "barite");

		// metal cleanup
		s = s.replaceAll("(chalc)(lite|ite|ide|ate|onite|anite)$", "chalcopyrite");
		s = s.replaceAll("(nickel)(blende)$", "nickelblende");

		// clay duplicate cleanup
		s = s.replaceAll("(argill)(argillite)$", "$2");

		// volcanic readability
		s = s.replaceAll("(calci)(tuff)$", "calci tuff");

		// alloy readability
		s = s.replaceAll("(ferro|stanno|cuprio|nickelo|argento|auro)alloy$", "$1 alloy");
		s = s.replaceAll("(ferro|stanno|cuprio|nickelo|argento|auro)(steel|bronze|metal)$", "$1 $2");

		// gravel cleanup
		s = s.replaceAll("(gruss)(conglomerate)$", "$1");
		s = s.replaceAll("(argille)(gruss)$", "argillgruss");

		// salt cleanup
		s = s.replaceAll("(mirab)(sylvite)$", "mirabilite");
		s = s.replaceAll("(carnal)(sylvite)$", "carnalite");

		// nickel/stann cleanup
		s = s.replaceAll("(nickel)(ate|ite|lite|ide)$", "nickelite");
		s = s.replaceAll("(stann)(lite|ite|ide|ate)$", "stannite");

		// gem cleanup
		s = s.replaceAll("(quartz)(ine|el|on|ar|aline|olite)$", "quartz");
		s = s.replaceAll("(mica)(el|on|ar|aline|olite|ine)$", "mica");

		// gem/stone cleanup
		s = s.replaceAll("(agate)$", "agatine");
		s = s.replaceAll("(beryl)(ide|ite|ate|onite|anite|lite)$", "beryl");
		s = s.replaceAll("(oliv)(on|ar|el|aline|olite)$", "olivine");

		// crystal cleanup
		s = s.replaceAll("(blende)(ine|ite|lite|cryst|spar)$", "blende");
		s = s.replaceAll("(halit)(ite|lite|ine|cryst|spar)$", "halite");

		// gravel/sediment cleanup
		s = s.replaceAll("(gruss)(breccia|conglomerate|rudite)$", "$1");
		s = s.replaceAll("(argille)(gruss)$", "argillgruss");
		s = s.replaceAll("(aren)(psammite)$", "$2");

		// gem/crystal/stone cleanup
		s = s.replaceAll("(opal)(ine|ite|lite|cryst|spar)$", "opal");
		s = s.replaceAll("(jade)(lite|ite|ine|blende|cryst|spar)$", "jadeite");
		s = s.replaceAll("(mica)(ide|ite|lite|ine|blende)$", "mica");

		// gravel duplicate cleanup
		s = s.replaceAll("(brecc)(breccia)$", "$2");

		// optional readability spacing before long real mineral names
		s = s.replaceAll("^(ruf|ruf[aioe]?)(chalcopyrite)$", "rufa $2");
		s = s.replaceAll("^(calci)(chalcopyrite)$", "$1 $2");

		// clay/mud cleanup
		s = s.replaceAll("(chlorit)(argillite)$", "chlorite");
		s = s.replaceAll("(pelit)(lutite)$", "pelite");

		// mineral normalization
		s = s.replaceAll("(magnet)(blende)$", "magnetite");
		s = s.replaceAll("(feldspar)(ine|ite|olite|aline|on|el|ar)$", "feldspar");
		s = s.replaceAll("(gypsum)(olite|aline|ine|ite|on|el)$", "gypsum");

		// chemistry normalization
		s = s.replaceAll("(plumb)(lite|ite|ide|ate|onite)$", "plumbite");

		// readability spacing
		s = s.replaceAll("^(sapph)(chlorite)$", "$1a $2");
		s = s.replaceAll("^(doloma)(magnetite)$", "$1 $2");

		// full-mineral suffix cleanup
		s = s.replaceAll("(halite)(el|ar|on|ine|alite|olite|ite)$", "halite");
		s = s.replaceAll("(apatite)(el|ar|on|ine|aline|olite|ite)$", "apatite");
		s = s.replaceAll("(agate)(ite|lite|ine|el|ar|on|aline|olite)$", "agate");

		// metal cleanup
		s = s.replaceAll("(nickel)(onite|anite|ate|ide|lite|ite)$", "nickelite");
		s = s.replaceAll("(cupr)(blende)$", "cuprite");

		// optional spacing before long real mineral names
		s = s.replaceAll("^(fulig)(tourmaline)$", "$1 $2");
		s = s.replaceAll("^(xantho)(rhodonite)$", "$1 $2");
		s = s.replaceAll("^(argento)(magnetite)$", "$1 $2");
		s = s.replaceAll("^(silico)(gypsum)$", "$1 $2");

		// mineral normalization
		s = s.replaceAll("(calc)(ine|ite|lite|ar|el|on)$", "calcite");
		s = s.replaceAll("(garnet)(ate|ide|onite|anite|lite)$", "garnet");
		s = s.replaceAll("(ferr)(lite|ite|ide|ate|onite)$", "ferrite");

		// readability spacing
		s = s.replaceAll("^(azure)(barite)$", "$1 $2");
		s = s.replaceAll("^(murrh)(calcite)$", "$1 $2");
		s = s.replaceAll("^(argill)(magnetite)$", "$1 $2");
		s = s.replaceAll("^(nickelo)(ferrite)$", "$1 $2");

		// loose material readability
		s = s.replaceAll("(rud) gravel$", "rudite gravel");
		s = s.replaceAll("(peat)(humus)$", "$1 humus");
		s = s.replaceAll("(conglom)(rudite)$", "conglomerate");

		// gem/mineral cleanup
		s = s.replaceAll("(jade)(on|el|ar|aline|olite|ine)$", "jadeite");
		s = s.replaceAll("^(teal)(topaz)$", "$1 $2");
		s = s.replaceAll("^(purpur)(magnetite)$", "$1 $2");
		s = s.replaceAll("^(mangano)(malachite)$", "$1 $2");
		s = s.replaceAll("^(calci)(plumbite)$", "$1 $2");

		// metal cleanup
		s = s.replaceAll("(ferr)(blende)$", "ferrite");
		s = s.replaceAll("^(plumbo)(ferrite)$", "$1 $2");

		// crystal/mineral cleanup
		s = s.replaceAll("(oliv)(spar|cryst|lite|ite)$", "olivine");
		s = s.replaceAll("(halit)(on|el|ar|aline|olite|ine|ite)$", "halite");

		// readability spacing
		s = s.replaceAll("^(magneso)(chalcopyrite)$", "$1 $2");
		s = s.replaceAll("^(xantho)(olivine)$", "$1 $2");
		s = s.replaceAll("^(calci)(mirabilite)$", "$1 $2");
		s = s.replaceAll("^(rufe)(feldspar)$", "$1 $2");
		s = s.replaceAll("^(tealo)(halite)$", "$1 $2");

		// mineral normalization
		s = s.replaceAll("(feld)(olite|aline|ar|on|el|ine|ite)$", "feldspar");
		s = s.replaceAll("(chlorit)(kaolinite)$", "kaolinite");

		// readability spacing
		s = s.replaceAll("^(ferro)(dolomite)$", "$1 $2");
		s = s.replaceAll("^(calci)(nickelite)$", "$1 $2");
		s = s.replaceAll("^(chloro)(psammite)$", "$1 $2");
		s = s.replaceAll("^(nigri)(feldspar)$", "$1 $2");

		return s;
	}

	private static String cleanDisplay(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
		s = s.toLowerCase(Locale.ROOT);
		s = s.replaceAll("[^a-z ]", "");
		s = s.replaceAll("\\s+", " ").trim();

		for (String bad : BAD) {
			s = s.replace(bad, bad.substring(0, Math.min(2, bad.length())));
		}

		return s.isEmpty() ? "mineralite" : s;
	}

	private static String titleWords(String s) {
		if (s == null || s.isBlank()) return "Mineralite";

		String[] parts = s.trim().split("\\s+");
		StringBuilder out = new StringBuilder();

		for (String part : parts) {
			if (part.isEmpty()) continue;

			if (!out.isEmpty()) {
				out.append(' ');
			}

			out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1));
		}

		return out.toString();
	}

	private static String tidy(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
		s = s.toLowerCase(Locale.ROOT).replaceAll("[^a-z]", "");

		for (String bad : BAD) {
			s = s.replace(bad, bad.substring(0, Math.min(2, bad.length())));
		}

		return s.isEmpty() ? "mineralite" : s;
	}

	private static <T> T pick(Random rng, T[] arr) {
		return arr[rng.nextInt(arr.length)];
	}

	private static String pickWoodCore(Random rng, List<Identifier> biomeTags) {
		if (biomeTags != null) {
			for (Identifier tag : biomeTags) {
				String key = tag.getPath().toLowerCase(Locale.ROOT);

				if (key.contains("birch")) return "betul";
				if (key.contains("oak")) return key.contains("dark") ? "darkoak" : "querc";
				if (key.contains("spruce")) return "pice";
				if (key.contains("taiga")) return rng.nextBoolean() ? "pice" : "pin";
				if (key.contains("cherry")) return "cherri";
				if (key.contains("savanna")) return "acac";
				if (key.contains("jungle")) return rng.nextBoolean() ? "mahog" : "bambu";
				if (key.contains("bamboo")) return "bambu";
				if (key.contains("mangrove")) return "mangr";
				if (key.contains("swamp")) return rng.nextBoolean() ? "salix" : "mangr";
				if (key.contains("desert")) return rng.nextBoolean() ? "acac" : "teak";
				if (key.contains("grove")) return rng.nextBoolean() ? "abies" : "pice";
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

	private static void addDepthBias(List<Choice> pool, MaterialKind kind, int minY, int maxY) {
		int mid = (minY + maxY) / 2;

		if (mid <= 0) {
			add(pool, COLOR_BLACK, 3);
			add(pool, COLOR_GRAY, 2);
			if (!isLooseMaterial(kind)) add(pool, CHEM, 2);
		} else if (mid >= 80) {
			add(pool, COLOR_WHITE, 3);
			add(pool, COLOR_BLUE, 2);
			add(pool, COLOR_GRAY, 1);
		} else {
			add(pool, COLOR_BROWN, 2);
			add(pool, COLOR_GREEN, 1);
			if (!isLooseMaterial(kind)) add(pool, CHEM, 1);
		}
	}

	private static void addReplaceableBias(List<Choice> pool, Identifier replaceableTag) {
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
			add(pool, COLOR_YELLOW, 2);
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

	private static void addBiomeBias(List<Choice> pool, Identifier biomeKey) {
		if (biomeKey == null) return;

		String key = biomeKey.getPath().toLowerCase(Locale.ROOT);

		if (key.contains("snow") || key.contains("frozen") || key.contains("ice") || key.contains("mountain") || key.contains("grove")) {
			add(pool, COLOR_WHITE, 2);
			add(pool, COLOR_BLUE, 2);
			add(pool, COLOR_GRAY, 1);
		}

		if (key.contains("desert") || key.contains("badlands") || key.contains("mesa") || key.contains("savanna")) {
			add(pool, COLOR_YELLOW, 2);
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

		if (key.contains("cherry") || key.contains("birch")) {
			add(pool, COLOR_PINK, 2);
			add(pool, COLOR_WHITE, 1);
		}
	}

	static String normalizeCombiningPrefix(String p) {
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

	private static String coerceSuffixSmart(String s, MaterialKind kind) {
		if (kind == MaterialKind.WOOD) {
			if (s == null || s.isEmpty()) return "wood";
			if (s.equals("log")) return "wood";
		}

		return s;
	}

	private static String compose(String prefix, String link, String core, String suffix, MaterialKind kind) {
		String p = prefix == null ? "" : prefix;
		String l = p.isEmpty() ? "" : (link == null ? "" : link);
		String c = core == null ? "" : core;
		String s = suffix == null ? "" : suffix;

		String raw;

		switch (kind) {
			case GRAVEL -> {
				if (c.startsWith("conglom") && s.contains("conglomerate")) return "conglomerate";
				if (c.startsWith("brecc") && (s.contains("breccia") || s.contains("rudite"))) return "breccia";
				if (c.startsWith("rud") && s.contains("rudite")) return "rudite";
				if (c.startsWith("gruss") && s.contains("gravel")) return joinMorphemes(p, l, "gruss");
				if (c.startsWith("rud") && s.contains("conglomerate")) return joinMorphemes(p, l, "conglomerate");
				raw = joinMorphemes(p, l, c) + s;
			}
			case SAND -> {
				if (c.startsWith("aren") && s.contains("sandstone")) return joinMorphemes(p, l, "sandstone");
				if (c.startsWith("aren") && s.contains("arenite")) return joinMorphemes(p, l, "arenite");
				if (c.contains("psamm") && s.equals("sand")) return joinMorphemes(p, l, "psammite");
				raw = joinMorphemes(p, l, c) + s;
			}
			case CLAY -> {
				if (c.startsWith("kaolin") && s.contains("kaolinite")) return joinMorphemes(p, l, "kaolinite");
				if (c.startsWith("argill") && (s.contains("clay") || s.contains("argillite"))) {
					return joinMorphemes(p, l, s.contains("argillite") ? "argillite" : "clay");
				}
				if (c.endsWith("illite") && s.contains("argillite")) return joinMorphemes(p, l, "argillite");
				if (c.startsWith("benton")) return joinMorphemes(p, l, "bentonite");
				raw = joinMorphemes(p, l, c) + s;
			}
			case MUD -> {
				if (c.startsWith("pelit") && s.contains("pelite")) return joinMorphemes(p, l, "pelite");
				if (c.startsWith("lutit") && s.contains("lutite")) return joinMorphemes(p, l, "lutite");
				if (c.startsWith("silt") && s.contains("mudstone")) return joinMorphemes(p, l, "mudstone");
				raw = joinMorphemes(p, l, c) + s;
			}
			case SALT -> {
				if (c.startsWith("halit") && (s.contains("halite") || s.contains("salt") || s.contains("evaporite"))) return joinMorphemes(p, l, "halite");
				if (c.startsWith("sylv") && (s.contains("sylvite") || s.contains("salt") || s.contains("evaporite"))) return joinMorphemes(p, l, "sylvite");
				if (c.startsWith("gyps") && (s.contains("gypsum") || s.contains("salt") || s.contains("evaporite"))) return joinMorphemes(p, l, "gypsum");
				if (c.startsWith("anhydr") && (s.contains("anhydrite") || s.contains("salt") || s.contains("evaporite"))) return joinMorphemes(p, l, "anhydrite");
				if (c.startsWith("epsom") && (s.contains("salt") || s.contains("evaporite"))) return joinMorphemes(p, l, "epsomite");
				if (c.startsWith("mirab") && (s.contains("salt") || s.contains("evaporite") || s.contains("halite"))) return joinMorphemes(p, l, "mirabilite");
				if (c.startsWith("carnal") && (s.contains("salt") || s.contains("evaporite") || s.contains("halite"))) return joinMorphemes(p, l, "carnalite");
				if (c.startsWith("trona") && (s.contains("salt") || s.contains("evaporite"))) return joinMorphemes(p, l, "trona");
				raw = joinMorphemes(p, l, c) + s;
			}
			case VOLCANIC -> {
				if (c.startsWith("pumic")) return joinMorphemes(p, l, "tephra");
				if (c.startsWith("scori")) return joinMorphemes(p, l, "scoria");
				if (c.startsWith("tephr")) return joinMorphemes(p, l, "tephra");
				if (c.startsWith("tuff")) return joinMorphemes(p, l, "tuff");
				if (c.startsWith("obsid")) return joinMorphemes(p, l, "vitrophyre");

				if (s.contains("glass")) {
					if (c.startsWith("basalt")) return joinMorphemes(p, l, "tachylite");
					return joinMorphemes(p, l, "vitrophyre");
				}

				raw = joinMorphemes(p, l, c) + s;
			}
			case SOIL -> {
				if (c.endsWith("sol") && (s.contains("soil") || s.contains("sol") || s.contains("dirt"))) {
					return joinMorphemes(p, l, c);
				}
				if (c.startsWith("loess") && (s.contains("soil") || s.contains("sol") || s.contains("lite"))) {
					return joinMorphemes(p, l, "loess");
				}
				raw = joinMorphemes(p, l, c) + s;
			}
			case WOOD -> {
				if (c.endsWith("wood")) c = c.replaceAll("wood$", "");
				raw = joinMorphemes(p, l, c) + s;
				raw = raw.replaceAll("rcw", "rw").replaceAll("cwood$", "wood");
				raw = raw.replaceAll("^ironw(timber|plank|bark|heartwood|wood)$", "ironwood");
			}
			default -> raw = joinMorphemes(p, l, c) + s;
		}

		raw = raw.replaceFirst("^recc", "brecc");

		if (raw.startsWith("brecc") && raw.endsWith("rudite")) {
			raw = raw.replaceAll("rudite$", "ia");
		}

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
				.replaceAll("(sol)\\1+$", "$1")
				.replaceAll("(loess)(?:sol|soil|lite)$", "$1");

		raw = raw.replaceAll("^chloro?chlorit", "chlorit");

		if (raw.equals("obsidian")) raw = "vitrophyre";
		if (raw.equals("pumice")) raw = "tephra";

		return tidy(raw);
	}

	private static String[] merge(String[] a, String[] b) {
		String[] out = Arrays.copyOf(a, a.length + b.length);
		System.arraycopy(b, 0, out, a.length, b.length);
		return out;
	}

	public static void debugSample(long seed, MaterialKind kind, int color, SpawnInfo spawn, int count) {
		for (int i = 0; i < count; i++) {
			NameInformation name = generate(seed, i, kind, color, spawn);
			System.out.println(kind + " -> " + name.displayName() + " | " + name.id());
		}
	}

	public static void main() {
		long seed = new SecureRandom().nextLong();
		MaterialSet set = MaterialGenerator.generate(seed);

		for (int i = 0; i < set.all().size(); i++) {
			var mat = set.all().get(i);
			System.out.println("Type: " + mat.kind().getSerializedName() + " " + generate(seed, i, mat));
		}
	}

	private record NameParts(
			String prefix,
			String link,
			String core,
			String suffix,
			MaterialKind kind
	) {}

	public record KindCfg(
			String[] cores,
			String[] suffixesOreLike,
			String[] suffixesCrystalLike,
			String[] suffixesDefault,
			int[] archetypeWeights,
			boolean preferChemPrefix,
			boolean allowColorPrefix,
			BiFunction<String, MaterialKind, String> postSuffixCoercion
	) {
		public static KindCfg of(
				String[] cores,
				String[] sufOre,
				String[] sufCrystal,
				String[] sufDefault,
				int w0,
				int w1,
				int w2,
				boolean preferChem,
				boolean allowColor,
				BiFunction<String, MaterialKind, String> post
		) {
			return new KindCfg(
					cores,
					sufOre,
					sufCrystal,
					sufDefault,
					new int[]{w0, w1, w2},
					preferChem,
					allowColor,
					post
			);
		}
	}

	private record Choice(String[] bank, int weight) {}
}