// src/main/java/net/vampirestudios/raaMaterials/material/GeoNameGen.java
package net.vampirestudios.raaMaterials;

import net.vampirestudios.raaMaterials.material.*;
import net.vampirestudios.raaMaterials.material.MaterialDef.NameInformation;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.*;

public final class GeoNameGen {
	private GeoNameGen() {}

	private enum CoreForm {
		FANTASY_STEM,
		REAL_MINERAL,
		LOOSE,
		WOOD,
		ALLOY
	}

	private enum LooseFamily {
		NONE,
		SOIL,
		SAND,
		GRAVEL,
		CLAY,
		MUD,
		WOOD
	}

	private enum NameStyle {
		GEO_FANTASY,   // Velite, Smarag Orilite
		SCI_FI,        // Kryonite, Vexium, Neutryl
		STUPID,        // Beanite, Wobblestone, Goobium
		FICTIONAL,     // Eldrathite, Moonsteel, Voidglass
		REALISH        // Argento Hematite
	}

	private record PrefixEntry(String token, String display) {
		static final PrefixEntry NONE = new PrefixEntry("", "");
	}

	private record CoreEntry(
			String token,
			String display,
			CoreForm form,
			LooseFamily looseFamily
	) {
		static CoreEntry fantasy(String token, String display) {
			return new CoreEntry(token, display, CoreForm.FANTASY_STEM, LooseFamily.NONE);
		}

		static CoreEntry real(String token, String display) {
			return new CoreEntry(token, display, CoreForm.REAL_MINERAL, LooseFamily.NONE);
		}

		static CoreEntry loose(String token, String display, LooseFamily family) {
			return new CoreEntry(token, display, CoreForm.LOOSE, family);
		}

		static CoreEntry wood(String token, String display) {
			return new CoreEntry(token, display, CoreForm.WOOD, LooseFamily.WOOD);
		}

		static CoreEntry alloy(String token, String display) {
			return new CoreEntry(token, display, CoreForm.ALLOY, LooseFamily.NONE);
		}
	}

	private record NameParts(
			MaterialKind kind,
			PrefixEntry prefix,
			CoreEntry core,
			String suffix
	) {}

	private record Choice<T>(T[] bank, int weight) {}

	private static final PrefixEntry[] COLOR_GREEN = p("chloro", "Chloro", "viridi", "Viridi", "smarag", "Smarag", "prasi", "Prasi");
	private static final PrefixEntry[] COLOR_BLUE = p("cyano", "Cyano", "azure", "Azure", "lazu", "Lazu", "caerul", "Caerul", "sapph", "Sappha");
	private static final PrefixEntry[] COLOR_RED = p("erythro", "Erythro", "rubri", "Rubri", "rhodo", "Rhodo", "carmi", "Carmi");
	private static final PrefixEntry[] COLOR_YELLOW = p("xantho", "Xantho", "flavo", "Flavo", "auri", "Auri", "fulvo", "Fulvo");
	private static final PrefixEntry[] COLOR_BLACK = p("melano", "Melano", "nigri", "Nigri", "anthra", "Anthra");
	private static final PrefixEntry[] COLOR_WHITE = p("leuco", "Leuco", "alba", "Alba", "argento", "Argento");
	private static final PrefixEntry[] COLOR_MISC = p("irido", "Irido", "opal", "Opal", "luci", "Luci", "phospho", "Phospho");
	private static final PrefixEntry[] COLOR_ORANGE = p("croco", "Croco", "auran", "Auran", "citrin", "Citrin", "pyro", "Pyro", "tawny", "Tawny");
	private static final PrefixEntry[] COLOR_PURPLE = p("porphyr", "Porphyr", "purpur", "Purpur", "amethyst", "Amethyst", "violac", "Violac");
	private static final PrefixEntry[] COLOR_BROWN = p("brun", "Bruno", "umbr", "Umbra", "fulig", "Fulig", "ruf", "Rufe", "murrh", "Murrh");
	private static final PrefixEntry[] COLOR_GRAY = p("cinere", "Cinere", "argill", "Argill", "plumbe", "Plumbe", "grise", "Grise");
	private static final PrefixEntry[] COLOR_PINK = p("roseo", "Roseo", "rhodon", "Rhodon", "candi", "Candi", "rubell", "Rubell");
	private static final PrefixEntry[] COLOR_TEAL = p("teal", "Teal", "turqu", "Turqu", "viridcyan", "Viridcyan", "aqua", "Aqua");

	private static final PrefixEntry[] CHEM = p(
			"silic", "Silico",
			"calc", "Calci",
			"dolom", "Dolomo",
			"magnes", "Magneso",
			"ferr", "Ferro",
			"cupr", "Cupro",
			"stann", "Stanno",
			"plumb", "Plumbo",
			"mangan", "Mangano",
			"cobalt", "Cobalto",
			"nickel", "Nickelo",
			"argent", "Argento",
			"aur", "Auro",
			"bor", "Boro",
			"fluor", "Fluoro",
			"sulf", "Sulfo",
			"lith", "Lithio",
			"sod", "Sodo",
			"potass", "Potasso"
	);

	private static final PrefixEntry[] DESCRIPTOR = p(
			"ancient", "Ancient",
			"deep",    "Deep",
			"buried",  "Buried",
			"forge",   "Forge",
			"dawn",    "Dawn",
			"dusk",    "Dusk",
			"storm",   "Storm",
			"ember",   "Ember",
			"frost",   "Frost",
			"sky",     "Sky",
			"void",    "Void",
			"prime",   "Prime",
			"elder",   "Elder",
			"shadow",  "Shadow",
			"blaze",   "Blaze",
			"tide",    "Tide",
			"crest",   "Crest",
			"hollow",  "Hollow"
	);

	private static final CoreEntry[] METAL_CORES = c(
			f("vyr", "Vyr"), f("orik", "Orik"), f("kael", "Kael"), f("durn", "Durn"),
			f("tharn", "Tharn"), f("veltr", "Veltr"), f("nyr", "Nyr"), f("korv", "Korv"),
			f("aurel", "Aurel"), f("myr", "Myr"), f("brask", "Brask"), f("zarn", "Zarn"),
			f("ulth", "Ulth"), f("kryx", "Kryx"), f("mord", "Mord"), f("varn", "Varn"),
			f("cald", "Cald"), f("vorn", "Vorn"), f("mirk", "Mirk"), f("teld", "Teld"), f("garm", "Garm"),

			r("chalc", "Chalcopyrite"), r("sulf", "Sulfide"), r("magnet", "Magnetite"),
			r("hemat", "Hematite"), r("cupr", "Cuprite"), r("ferr", "Ferrite"),
			r("argent", "Argentite"), r("stann", "Stannite"), r("plumb", "Plumbite"),
			r("nickel", "Nickelite"), r("galena", "Galena"), r("pyrite", "Pyrite"),
			r("cinnabar", "Cinnabar")
	);

	private static final CoreEntry[] GEM_CORES = c(
			f("vel", "Vel"), f("nyx", "Nyx"), f("aurel", "Aurel"), f("myr", "Myr"),
			f("syl", "Syl"), f("oril", "Oril"), f("thar", "Thar"), f("vyr", "Vyr"),
			f("lun", "Lun"), f("elun", "Elun"), f("vael", "Vael"), f("ir", "Ir"),
			f("cyr", "Cyr"), f("zel", "Zel"), f("lyr", "Lyr"), f("ser", "Ser"),
			f("aura", "Aura"), f("vasha", "Vasha"), f("mir", "Mir"), f("sova", "Sova"),
			f("oran", "Oran"), f("tael", "Tael"), f("kira", "Kira"), f("zela", "Zela"), f("nuv", "Nuv"),

			r("garnet", "Garnet"), r("beryl", "Beryl"), r("topaz", "Topaz"),
			r("jade", "Jadeite"), r("opal", "Opal"), r("agate", "Agate"),
			r("tourmal", "Tourmaline"), r("corund", "Corundum"), r("spinel", "Spinel"),
			r("zircon", "Zircon"), r("malach", "Malachite"), r("azur", "Azurite"),
			r("rhodo", "Rhodolite"), r("quartz", "Quartz"), r("amethyst", "Amethyst")
	);

	private static final CoreEntry[] CRYSTAL_CORES = c(
			f("lume", "Lume"), f("crys", "Crys"), f("shael", "Shael"), f("vitr", "Vitr"),
			f("sel", "Sel"), f("aeth", "Aeth"), f("glac", "Glac"), f("oril", "Oril"),
			f("prism", "Prism"), f("thal", "Thal"), f("nyra", "Nyra"), f("eil", "Eil"),
			f("solen", "Solen"), f("laz", "Laz"), f("vaur", "Vaur"), f("candra", "Candra"),
			f("zeph", "Zeph"), f("rael", "Rael"), f("miru", "Miru"), f("sora", "Sora"), f("aelm", "Aelm"),

			r("quartz", "Quartz"), r("feldspar", "Feldspar"), r("calc", "Calcite"),
			r("dolom", "Dolomite"), r("halit", "Halite"), r("fluor", "Fluorite"),
			r("selen", "Selenite"), r("aragon", "Aragonite"), r("apat", "Apatite"),
			r("barit", "Barite"), r("mica", "Mica"), r("blende", "Blende")
	);

	private static final CoreEntry[] STONE_CORES = c(
			f("gran", "Gran"), f("dral", "Dral"), f("karn", "Karn"), f("morth", "Morth"),
			f("vulk", "Vulk"), f("thaur", "Thaur"), f("brun", "Brun"), f("eld", "Eld"),
			f("skarn", "Skarn"), f("uln", "Uln"), f("rav", "Rav"), f("grom", "Grom"),
			f("marn", "Marn"), f("kaur", "Kaur"), f("dusk", "Dusk"), f("solm", "Solm"),

			r("quartzite", "Quartzite"), r("feldspar", "Feldspar"), r("mica", "Mica"),
			r("calcite", "Calcite"), r("dolomite", "Dolomite"), r("hematite", "Hematite"),
			r("magnetite", "Magnetite"), r("malachite", "Malachite"), r("azurite", "Azurite"),
			r("barite", "Barite"), r("gypsum", "Gypsum"), r("fluorite", "Fluorite"),
			r("apatite", "Apatite"), r("olivine", "Olivine"), r("pyrite", "Pyrite"),
			r("galena", "Galena"), r("cinnabar", "Cinnabar"), r("orpiment", "Orpiment"),
			r("realgar", "Realgar"), r("serpentin", "Serpentinite"),
			r("marble", "Marble"), r("slate", "Slate"), r("schist", "Schist"),
			r("gneiss", "Gneiss"), r("hornfels", "Hornfels"), r("phyllite", "Phyllite")
	);

	private static final CoreEntry[] SALT_CORES = c(
			f("saln", "Saln"), f("brin", "Brin"), f("mir", "Mir"), f("syl", "Syl"),
			f("hal", "Hal"), f("eps", "Eps"), f("trun", "Trun"), f("carn", "Carn"),
			f("sodr", "Sodr"), f("alk", "Alk"),

			r("halit", "Halite"), r("sylv", "Sylvite"), r("gyps", "Gypsum"),
			r("anhydr", "Anhydrite"), r("carnal", "Carnalite"), r("mirab", "Mirabilite"),
			r("trona", "Trona"), r("epsom", "Epsomite")
	);

	private static final CoreEntry[] VOLCANIC_CORES = c(
			f("pyra", "Pyra"), f("scor", "Scor"), f("vitr", "Vitr"), f("ashr", "Ashr"),
			f("magma", "Magma"), f("kald", "Kald"), f("obs", "Obs"), f("teph", "Teph"),
			f("rhyx", "Rhyx"), f("dacr", "Dacr"), f("basr", "Basr"), f("ign", "Ign"),

			r("tuff", "Tuff"), r("pumic", "Pumice"), r("obsid", "Obsidian"),
			r("scori", "Scoria"), r("basalt", "Basalt"), r("andes", "Andesite"),
			r("dacite", "Dacite"), r("rhyol", "Rhyolite"), r("tephr", "Tephra"),
			r("tachylite", "Tachylite"), r("vitrophyre", "Vitrophyre")
	);

	private static final CoreEntry[] ALLOY_CORES = c(
			CoreEntry.alloy("ferro", "Ferro"),
			CoreEntry.alloy("stanno", "Stanno"),
			CoreEntry.alloy("cuprio", "Cuprio"),
			CoreEntry.alloy("nickelo", "Nickelo"),
			CoreEntry.alloy("argento", "Argento"),
			CoreEntry.alloy("auro", "Auro"),
			CoreEntry.alloy("vyr", "Vyr"),
			CoreEntry.alloy("kael", "Kael"),
			CoreEntry.alloy("orik", "Orik"),
			CoreEntry.alloy("durn", "Durn"),
			CoreEntry.alloy("tharn", "Tharn"),
			CoreEntry.alloy("mord", "Mord"),
			CoreEntry.alloy("zarn", "Zarn"),
			CoreEntry.alloy("ulth", "Ulth"),
			CoreEntry.alloy("nyr", "Nyr"),
			CoreEntry.alloy("brask", "Brask"),
			CoreEntry.alloy("cald", "Cald")
	);

	private static final CoreEntry[] SOIL_CORES = c(
			CoreEntry.loose("argill", "Argill", LooseFamily.SOIL),
			CoreEntry.loose("humic", "Humic", LooseFamily.SOIL),
			CoreEntry.loose("loess", "Loess", LooseFamily.SOIL),
			CoreEntry.loose("peat", "Peat", LooseFamily.SOIL),
			CoreEntry.loose("gelisol", "Gelisol", LooseFamily.SOIL),
			CoreEntry.loose("andosol", "Andosol", LooseFamily.SOIL),
			CoreEntry.loose("vertis", "Vertis", LooseFamily.SOIL),
			CoreEntry.loose("ultis", "Ultis", LooseFamily.SOIL),
			CoreEntry.loose("myr", "Myr", LooseFamily.SOIL),
			CoreEntry.loose("dusk", "Dusk", LooseFamily.SOIL)
	);

	private static final CoreEntry[] SAND_CORES = c(
			CoreEntry.loose("quartz", "Quartz", LooseFamily.SAND),
			CoreEntry.loose("aren", "Aren", LooseFamily.SAND),
			CoreEntry.loose("silic", "Silica", LooseFamily.SAND),
			CoreEntry.loose("feld", "Feldspar", LooseFamily.SAND),
			CoreEntry.loose("lith", "Lithic", LooseFamily.SAND),
			CoreEntry.loose("psamm", "Psamm", LooseFamily.SAND),
			CoreEntry.loose("arkos", "Arkosic", LooseFamily.SAND),
			CoreEntry.loose("vyr", "Vyr", LooseFamily.SAND),
			CoreEntry.loose("lume", "Lume", LooseFamily.SAND)
	);

	private static final CoreEntry[] GRAVEL_CORES = c(
			CoreEntry.loose("rud", "Rudite", LooseFamily.GRAVEL),
			CoreEntry.loose("brecc", "Breccia", LooseFamily.GRAVEL),
			CoreEntry.loose("conglom", "Conglomerate", LooseFamily.GRAVEL),
			CoreEntry.loose("lith", "Lithic", LooseFamily.GRAVEL),
			CoreEntry.loose("quartz", "Quartz", LooseFamily.GRAVEL),
			CoreEntry.loose("gruss", "Gruss", LooseFamily.GRAVEL),
			CoreEntry.loose("dral", "Dral", LooseFamily.GRAVEL),
			CoreEntry.loose("karn", "Karn", LooseFamily.GRAVEL)
	);

	private static final CoreEntry[] CLAY_CORES = c(
			CoreEntry.loose("kaolin", "Kaolin", LooseFamily.CLAY),
			CoreEntry.loose("illite", "Illite", LooseFamily.CLAY),
			CoreEntry.loose("smect", "Smectite", LooseFamily.CLAY),
			CoreEntry.loose("montmor", "Montmorillonite", LooseFamily.CLAY),
			CoreEntry.loose("chlorit", "Chlorite", LooseFamily.CLAY),
			CoreEntry.loose("benton", "Bentonite", LooseFamily.CLAY),
			CoreEntry.loose("argill", "Argill", LooseFamily.CLAY),
			CoreEntry.loose("myr", "Myr", LooseFamily.CLAY),
			CoreEntry.loose("syl", "Syl", LooseFamily.CLAY)
	);

	private static final CoreEntry[] MUD_CORES = c(
			CoreEntry.loose("pelit", "Pelite", LooseFamily.MUD),
			CoreEntry.loose("lutit", "Lutite", LooseFamily.MUD),
			CoreEntry.loose("silt", "Silt", LooseFamily.MUD),
			CoreEntry.loose("argill", "Argill", LooseFamily.MUD),
			CoreEntry.loose("turbid", "Turbid", LooseFamily.MUD),
			CoreEntry.loose("mire", "Mire", LooseFamily.MUD),
			CoreEntry.loose("dusk", "Dusk", LooseFamily.MUD)
	);

	private static final CoreEntry[] WOOD_CORES = c(
			CoreEntry.wood("querc", "Querc"),
			CoreEntry.wood("betul", "Betul"),
			CoreEntry.wood("pice", "Pice"),
			CoreEntry.wood("abies", "Abies"),
			CoreEntry.wood("laric", "Laric"),
			CoreEntry.wood("pin", "Pin"),
			CoreEntry.wood("salix", "Salix"),
			CoreEntry.wood("acac", "Acac"),
			CoreEntry.wood("ulmus", "Ulmus"),
			CoreEntry.wood("juglan", "Juglan"),
			CoreEntry.wood("coryl", "Coryl"),
			CoreEntry.wood("acer", "Acer"),
			CoreEntry.wood("tilia", "Tilia"),
			CoreEntry.wood("cedr", "Cedr"),
			CoreEntry.wood("mahog", "Mahog"),
			CoreEntry.wood("ebon", "Ebon"),
			CoreEntry.wood("bambu", "Bambu"),
			CoreEntry.wood("mangr", "Mangr"),
			CoreEntry.wood("cherri", "Cherri"),
			CoreEntry.wood("darkoak", "Dark Oak"),
			CoreEntry.wood("baoba", "Baoba"),
			CoreEntry.wood("teak", "Teak"),
			CoreEntry.wood("ironw", "Ironwood")
	);

	private static final String[] FANTASY_METAL_SUFFIXES = {"ium", "ite", "or", "en", "yx", "ar", "on", "yl", "al", "ux", "eld", "urn"};
	private static final String[] FANTASY_GEM_SUFFIXES = {"ite", "ine", "elle", "ara", "oryn", "el", "on", "alia", "al", "yx", "eira", "iel"};
	private static final String[] FANTASY_CRYSTAL_SUFFIXES = {"ite", "ine", "cryst", "shard", "spar", "lume", "el", "al", "spike", "point"};
	private static final String[] FANTASY_STONE_SUFFIXES = {"ite", "stone", "rock", "ar", "on", "olith", "en", "marble", "slate", "schist", "shale", "flint", "gneiss"};
	private static final String[] FANTASY_SALT_SUFFIXES = {"salt", "ite", "ine", "cryst", "brine", "on", "halite", "evaporite"};
	private static final String[] FANTASY_VOLCANIC_SUFFIXES = {"ite", "glass", "tuff", "ash", "scoria", "tephra", "stone", "lava", "cinder"};

	private static final String[] ALLOY_SUFFIXES = {"Alloy", "Steel", "Bronze", "Metal", "Amalgam", "Composite", "Weave", "Cast", "Blend"};
	private static final String[] WOOD_SUFFIXES = {"Wood", "Bark", "Heartwood", "Timber", "Plank", "Lumber", "Grain"};

	private static final String[] SCI_FI_STEMS = {
			"vex", "neutro", "ion", "quant", "xeno", "cryon", "plasma", "radon",
			"tachy", "nex", "zeron", "flux", "positr", "aether",
			"nano", "proto", "hyper", "ultra", "meta", "chrono", "photon", "helix",
			"axiom", "synth", "vertex", "cipher", "helix", "sonar"
	};

	private static final String[] STUPID_STEMS = {
			"bean", "goob", "wobble", "bonk", "squish", "mlem", "bloop", "yoink",
			"nug", "chonko", "glorp", "snorf",
			"plop", "zorp", "splat", "bork", "doot", "gloop", "florp", "shlorp"
	};

	private static final String[] FICTION_STEMS = {
			"eldr", "void", "moon", "star", "dragon", "wyrm", "shadow", "sun",
			"myth", "rune", "fae", "dread", "ember", "frost",
			"arcane", "astral", "chaos", "primal", "spectral", "nexus",
			"hollow", "titan", "wyrd", "riven", "sable", "azure"
	};

	private static final String[] MICRO = {
			"oi", "xa", "vu", "ae", "ny", "qo", "zi", "ka",
			"ul", "ix", "za", "vo", "qi", "xe"
	};

	private static final Set<String> BLOCKED_NAMES = Set.of(
			"stone", "dirt", "sand", "gravel", "clay", "mud", "wood"
	);

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

		String display = "Mineralite";

		for (int tries = 0; tries < 20; tries++) {
			display = genDisplay(rng, kind, primaryColor, spawn);
			String key = display.toLowerCase(Locale.ROOT);

			if (usedDisplayNames.add(key)) {
				break;
			}
		}

		String id = slug(display) + "_" + shortHash(mix(worldSeed, index, kind.ordinal()));
		return new NameInformation(RAAMaterials.id(id), display);
	}

	private static String genDisplay(Random rng, MaterialKind kind, int rgb, SpawnInfo spawn) {
		NameStyle style = pickStyle(rng);

		if (rng.nextInt(100) < 2) {
			return titleWords(pick(rng, MICRO));
		}

		String raw;

		if (style == NameStyle.GEO_FANTASY || style == NameStyle.REALISH) {
			NameParts parts = pickNameParts(rng, kind, rgb, spawn);
			raw = renderDisplay(rng, parts);
		} else {
			raw = renderStyledFantasy(rng, kind, style);
		}

		String key = raw.toLowerCase(Locale.ROOT).replace(" ", "");
		if (BLOCKED_NAMES.contains(key)) {
			raw = raw + "ite";
		}

		return titleWords(raw);
	}

	private static String renderStyledFantasy(Random rng, MaterialKind kind, NameStyle style) {
		String stem = switch (style) {
			case SCI_FI -> pick(rng, SCI_FI_STEMS);
			case STUPID -> pick(rng, STUPID_STEMS);
			case FICTIONAL -> pick(rng, FICTION_STEMS);
			default -> pick(rng, coresFor(kind)).display();
		};

		String suffix = switch (kind) {
			case METAL   -> pick(rng, FANTASY_METAL_SUFFIXES);
			case ALLOY   -> pick(rng, "alloy", "steel", "bronze", "metal", "amalgam", "composite");
			case GEM     -> pick(rng, FANTASY_GEM_SUFFIXES);
			case CRYSTAL -> pick(rng, FANTASY_CRYSTAL_SUFFIXES);
			case STONE, OTHER -> pick(rng, FANTASY_STONE_SUFFIXES);
			case SALT    -> pick(rng, FANTASY_SALT_SUFFIXES);
			case VOLCANIC -> pick(rng, FANTASY_VOLCANIC_SUFFIXES);
			default      -> pick(rng, "ite", "ium", "stone");
		};

		return smoothJoin(stem, suffix);
	}

	private static NameStyle pickStyle(Random rng) {
		int roll = rng.nextInt(100);

		if (roll < 55) return NameStyle.GEO_FANTASY;
		if (roll < 75) return NameStyle.SCI_FI;
		if (roll < 92) return NameStyle.FICTIONAL;
		if (roll < 97) return NameStyle.REALISH;

		return NameStyle.STUPID;
	}

	private static NameParts pickNameParts(Random rng, MaterialKind kind, int rgb, SpawnInfo spawn) {
		CoreEntry core = pickCore(rng, kind);

		PrefixEntry prefix = PrefixEntry.NONE;
		if (shouldUsePrefix(rng, kind, core)) {
			prefix = pickPrefix(rng, kind, rgb, spawn);
		}

		String suffix = switch (kind) {
			case ALLOY -> pick(rng, ALLOY_SUFFIXES);
			case WOOD -> pick(rng, WOOD_SUFFIXES);
			default -> "";
		};

		return new NameParts(kind, prefix, core, suffix);
	}

	private static CoreEntry pickCore(Random rng, MaterialKind kind) {
		CoreEntry[] cores = coresFor(kind);

		int realChance = switch (kind) {
			case METAL -> 20;
			case GEM -> 18;
			case CRYSTAL -> 22;
			case STONE -> 20;
			case SALT -> 35;
			case VOLCANIC -> 30;
			default -> 0;
		};

		if (realChance <= 0) {
			return pick(rng, cores);
		}

		boolean wantReal = rng.nextInt(100) < realChance;

		for (int tries = 0; tries < 12; tries++) {
			CoreEntry core = pick(rng, cores);
			if ((core.form() == CoreForm.REAL_MINERAL) == wantReal) {
				return core;
			}
		}

		return pick(rng, cores);
	}

	private static String renderDisplay(Random rng, NameParts parts) {
		return switch (parts.kind()) {
			case ALLOY -> renderAlloy(parts);
			case WOOD -> renderWood(parts);
			case SAND, GRAVEL, CLAY, MUD, SOIL -> renderLooseMaterial(rng, parts);
			default -> renderMineral(rng, parts);
		};
	}

	private static String renderMineral(Random rng, NameParts parts) {
		CoreEntry core = parts.core();

		String base = switch (core.form()) {
			case REAL_MINERAL -> core.display();
			case FANTASY_STEM -> deriveFantasyName(rng, parts.kind(), core);
			default -> core.display();
		};

		if (parts.prefix() == PrefixEntry.NONE) {
			return base;
		}

		return parts.prefix().display() + " " + base;
	}

	private static String renderLooseMaterial(Random rng, NameParts parts) {
		CoreEntry core = parts.core();
		PrefixEntry prefix = parts.prefix();

		String base = core.display();

		String material = switch (parts.kind()) {
			case SOIL -> pick(rng, "Soil", "Dirt", "Humus");
			case SAND -> switch (core.token()) {
				case "aren" -> pick(rng, "Arenite", "Sandstone");
				case "psamm" -> "Psammite";
				case "silic" -> "Sand";
				default -> pick(rng, "Sand", "Sandstone");
			};
			case GRAVEL -> switch (core.token()) {
				case "brecc" -> pick(rng, "Gravel", "Breccia");
				case "conglom" -> pick(rng, "Gravel", "Conglomerate");
				case "rud" -> pick(rng, "Gravel", "Rudite");
				default -> "Gravel";
			};
			case CLAY -> switch (core.token()) {
				case "kaolin" -> pick(rng, "Clay", "Kaolinite");
				case "benton" -> pick(rng, "Clay", "Bentonite");
				case "argill" -> pick(rng, "Clay", "Argillite", "Shale");
				default -> pick(rng, "Clay", "Mudstone", "Shale");
			};
			case MUD -> switch (core.token()) {
				case "pelit" -> "Pelite";
				case "lutit" -> "Lutite";
				case "silt" -> pick(rng, "Mud", "Mudstone");
				default -> pick(rng, "Mud", "Mudstone");
			};
			default -> "";
		};

		String rendered;
		if (material.equals(base)) {
			rendered = base;
		} else if (isStandaloneLooseMaterial(material)) {
			rendered = material;
		} else {
			rendered = base + " " + material;
		}

		if (prefix == PrefixEntry.NONE) {
			return rendered;
		}

		return prefix.display() + " " + rendered;
	}

	private static String renderAlloy(NameParts parts) {
		return parts.core().display() + " " + parts.suffix();
	}

	private static String renderWood(NameParts parts) {
		CoreEntry core = parts.core();

		if (core.token().equals("ironw")) {
			return parts.suffix().equals("Wood") ? "Ironwood" : "Ironwood " + parts.suffix();
		}

		return core.display() + " " + parts.suffix();
	}

	private static String deriveFantasyName(Random rng, MaterialKind kind, CoreEntry core) {
		String suffix = switch (kind) {
			case METAL -> pick(rng, FANTASY_METAL_SUFFIXES);
			case GEM -> pick(rng, FANTASY_GEM_SUFFIXES);
			case CRYSTAL -> pick(rng, FANTASY_CRYSTAL_SUFFIXES);
			case STONE -> pick(rng, FANTASY_STONE_SUFFIXES);
			case SALT -> pick(rng, FANTASY_SALT_SUFFIXES);
			case VOLCANIC -> pick(rng, FANTASY_VOLCANIC_SUFFIXES);
			default -> pick(rng, "ite", "ine", "on", "ar");
		};

		return smoothJoin(core.display(), suffix);
	}

	// Suffixes that are standalone words and should be space-joined rather than concatenated.
	private static final Set<String> STANDALONE_SUFFIXES = Set.of(
			"marble", "slate", "schist", "shale", "flint", "gneiss", "stone", "rock",
			"glass", "tuff", "ash", "scoria", "tephra", "lava", "cinder",
			"salt", "brine", "halite", "evaporite",
			"alloy", "steel", "bronze", "metal", "amalgam", "composite", "weave", "cast", "blend",
			"wood", "bark", "timber", "plank", "lumber", "grain",
			"spike", "point", "shard", "cryst", "spar"
	);

	private static String smoothJoin(String base, String suffix) {
		if (base == null || base.isEmpty()) return suffix;
		if (suffix == null || suffix.isEmpty()) return base;

		String b = base.toLowerCase(Locale.ROOT);
		String s = suffix.toLowerCase(Locale.ROOT);

		if (b.endsWith(s)) return titleWords(b);

		if (STANDALONE_SUFFIXES.contains(s)) {
			return titleWords(b + " " + s);
		}

		char last = b.charAt(b.length() - 1);
		char first = s.charAt(0);

		if (isVowel(last) && isVowel(first)) {
			return titleWords(b + s.substring(1));
		}

		if (b.endsWith("y") && s.startsWith("i")) {
			return titleWords(b.substring(0, b.length() - 1) + s);
		}

		return titleWords(b + s);
	}

	private static boolean shouldUsePrefix(Random rng, MaterialKind kind, CoreEntry core) {
		if (kind == MaterialKind.ALLOY || kind == MaterialKind.WOOD) {
			return false;
		}

		if (core.form() == CoreForm.REAL_MINERAL) {
			return true;
		}

		if (isLooseMaterial(kind)) {
			return rng.nextInt(100) < 30;
		}

		return rng.nextInt(100) < switch (kind) {
			case METAL -> 25;
			case GEM -> 35;
			case CRYSTAL -> 35;
			case STONE -> 25;
			case SALT -> 30;
			case VOLCANIC -> 30;
			default -> 25;
		};
	}

	private static PrefixEntry pickPrefix(Random rng, MaterialKind kind, int rgb, SpawnInfo spawn) {
		List<Choice<PrefixEntry>> pool = new ArrayList<>();

		float[] hsv = rgbToHsv(rgb);
		addHueBuckets(pool, hsv[0]);

		boolean lowSat = hsv[1] < 0.18f;
		boolean dark = hsv[2] < 0.28f;
		boolean bright = hsv[2] > 0.80f;

		if (lowSat) add(pool, COLOR_GRAY, 2);
		if (dark) add(pool, COLOR_BLACK, 2);
		if (bright) add(pool, COLOR_MISC, 1);

		if (!isLooseMaterial(kind)) {
			add(pool, CHEM, switch (kind) {
				case METAL, SALT -> 3;
				case GEM, CRYSTAL, STONE, VOLCANIC -> 1;
				default -> 1;
			});
			add(pool, DESCRIPTOR, 2);
		}

		addDepthBias(pool, kind, spawn.y().minY(), spawn.y().maxY());

		PrefixEntry[] bank = pickWeightedBank(rng, pool);
		return pick(rng, bank);
	}

	private static void addHueBuckets(List<Choice<PrefixEntry>> pool, float h) {
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

	private static void addDepthBias(List<Choice<PrefixEntry>> pool, MaterialKind kind, int minY, int maxY) {
		int mid = (minY + maxY) / 2;

		if (mid <= 0) {
			add(pool, COLOR_BLACK, 3);
			add(pool, COLOR_GRAY, 2);
			if (!isLooseMaterial(kind)) add(pool, CHEM, 1);
		} else if (mid >= 80) {
			add(pool, COLOR_WHITE, 3);
			add(pool, COLOR_BLUE, 2);
			add(pool, COLOR_GRAY, 1);
		} else {
			add(pool, COLOR_BROWN, 2);
			add(pool, COLOR_GREEN, 1);
		}
	}

	private static CoreEntry[] coresFor(MaterialKind kind) {
		return switch (kind) {
			case METAL -> METAL_CORES;
			case GEM -> GEM_CORES;
			case CRYSTAL -> CRYSTAL_CORES;
			case ALLOY -> ALLOY_CORES;
			case STONE, OTHER -> STONE_CORES;
			case SOIL -> SOIL_CORES;
			case SAND -> SAND_CORES;
			case GRAVEL -> GRAVEL_CORES;
			case CLAY -> CLAY_CORES;
			case MUD -> MUD_CORES;
			case SALT -> SALT_CORES;
			case VOLCANIC -> VOLCANIC_CORES;
			case WOOD -> WOOD_CORES;
		};
	}

	private static boolean isStandaloneLooseMaterial(String material) {
		return switch (material) {
			case "Arenite", "Psammite", "Breccia", "Conglomerate", "Rudite",
			     "Kaolinite", "Bentonite", "Argillite", "Shale", "Pelite", "Lutite" -> true;
			default -> false;
		};
	}

	private static boolean isLooseMaterial(MaterialKind kind) {
		return kind == MaterialKind.SAND
				|| kind == MaterialKind.GRAVEL
				|| kind == MaterialKind.CLAY
				|| kind == MaterialKind.MUD
				|| kind == MaterialKind.SOIL;
	}

	private static boolean isVowel(char c) {
		return "aeiouy".indexOf(Character.toLowerCase(c)) >= 0;
	}

	private static String cleanDisplay(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
		s = s.replaceAll("[^A-Za-z ]", "");
		s = s.replaceAll("\\s+", " ").trim();
		return s.isEmpty() ? "Mineralite" : s;
	}

	private static String titleWords(String s) {
		if (s == null || s.isBlank()) return "Mineralite";

		String[] parts = s.trim().split("\\s+");
		StringBuilder out = new StringBuilder();

		for (String part : parts) {
			if (part.isEmpty()) continue;
			if (!out.isEmpty()) out.append(' ');
			out.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1).toLowerCase(Locale.ROOT));
		}

		return out.toString();
	}

	private static String slug(String display) {
		String x = Normalizer.normalize(display, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
		x = x.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("_+", "_");
		x = x.replaceAll("^_+|_+$", "");
		return x.isEmpty() ? "mat" : x;
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

	@SafeVarargs
	private static <T> T[] c(T... values) {
		return values;
	}

	private static CoreEntry f(String token, String display) {
		return CoreEntry.fantasy(token, display);
	}

	private static CoreEntry r(String token, String display) {
		return CoreEntry.real(token, display);
	}

	private static PrefixEntry[] p(String... values) {
		if (values.length % 2 != 0) {
			throw new IllegalArgumentException("Prefix entries must be token/display pairs.");
		}

		PrefixEntry[] out = new PrefixEntry[values.length / 2];

		for (int i = 0; i < values.length; i += 2) {
			out[i / 2] = new PrefixEntry(values[i], values[i + 1]);
		}

		return out;
	}

	@SafeVarargs
	private static <T> T pick(Random rng, T... arr) {
		return arr[rng.nextInt(arr.length)];
	}

	private static <T> void add(List<Choice<T>> list, T[] bank, int weight) {
		list.add(new Choice<>(bank, Math.max(1, weight)));
	}

	private static <T> T[] pickWeightedBank(Random rng, List<Choice<T>> pool) {
		int sum = 0;
		for (Choice<T> choice : pool) sum += choice.weight;

		int roll = rng.nextInt(Math.max(sum, 1));
		int acc = 0;

		for (Choice<T> choice : pool) {
			acc += choice.weight;
			if (roll < acc) return choice.bank;
		}

		return pool.getFirst().bank;
	}

	public static void debugSample(long seed, MaterialKind kind, int color, SpawnInfo spawn, int count) {
		for (int i = 0; i < count; i++) {
			NameInformation name = generate(seed, i, kind, color, spawn);
			System.out.println(kind + " -> " + name.displayName() + " | " + name.id());
		}
	}

	public static void main() {
		long seed = new SecureRandom().nextLong();
		Random rng = new Random(seed);

		// A generic mid-depth spawn used only to drive prefix depth bias in debug output.
		SpawnInfo debugSpawn = new SpawnInfo(
				8, 1.0f, 3, 8, SpawnInfo.VeinShape.ORE_BLOB,
				new SpawnInfo.YDistribution(-32, 64, 16, 32),
				new SpawnInfo.NoiseGate(0.01f, -0.5f),
				new SpawnInfo.NoiseGate(0.05f, -0.3f),
				false, false, false
		);

		// Debug mode: generate one name per kind to verify every type produces valid output.
		Set<String> used = new HashSet<>();
		int index = 0;
		for (MaterialKind kind : MaterialKind.values()) {
			int color = rng.nextInt(0x1000000);
			NameInformation name = generateUnique(seed, index++, kind, color, debugSpawn, used);
			System.out.println(kind.getSerializedName() + " -> " + name.displayName() + "  [" + name.id() + "]");
		}
	}
}