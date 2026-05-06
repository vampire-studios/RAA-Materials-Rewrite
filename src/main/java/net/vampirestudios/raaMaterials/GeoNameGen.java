package net.vampirestudios.raaMaterials;

import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.material.SpawnInfo;

import java.text.Normalizer;
import java.util.*;

import static java.util.Locale.ROOT;

/**
 * GeoNameGen — geology-inspired material name generator.
 * Deterministic based on (worldSeed, kind, index).
 */
public final class GeoNameGen {

    // ========================================================================
    // Color morphemes (centralized)
    // ========================================================================
    private enum ColorMorpheme {
        GREEN("chloro", "viridi", "smarag", "prasi"),
        BLUE("cyano", "azure", "lazu", "caerul", "sapph"),
        RED("erythro", "rubri", "rhodo", "carmi"),
        YELLOW("xantho", "flavo", "auri", "fulvo"),
        BLACK("melano", "nigri", "anthra"),
        WHITE("leuco", "alba", "argento"),
        MISC("irido", "opal", "luci", "phospho"),
        ORANGE("croco", "auran", "citrin", "pyro", "tawny"),
        PURPLE("porphyr", "purpur", "amethyst", "violac"),
        BROWN("brun", "umbr", "fulig", "ruf", "murrh"),
        GRAY("cinere", "argill", "plumbe", "grise"),
        PINK("roseo", "rhodon", "candi", "rubell"),
        TEAL("teal", "turqu", "viridcyan", "aqua");

        final String[] morphemes;

        ColorMorpheme(String... morphemes) {
            this.morphemes = morphemes;
        }
    }

    // ========================================================================
    // Shared morpheme pools
    // ========================================================================
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
    private static final String[] CORES_GEM = {"garnet", "beryl", "topaz", "jade", "opal", "agate", "tourmal", "corund", "spinel", "zircon"};
    private static final String[] CORES_CRYSTAL = {"quartz", "spar", "cryst", "calc", "dolom", "halit", "fluor", "selen", "aragon", "apat"};
    private static final String[] CORES_ALLOY = {"ferro", "stanno", "cuprio", "nickelo", "argento", "auro"};
    private static final String[] CORES_SOIL = {"argill", "humic", "loess", "peat", "gelisol", "andosol", "vertis", "ultis"};
    private static final String[] CORES_SAND = {"quartz", "aren", "silic", "feld", "lith", "psamm", "arkos"};
    private static final String[] CORES_GRAVEL = {"rud", "brecc", "conglom", "lith", "quartz", "gruss"};
    private static final String[] CORES_CLAY = {"kaolin", "illite", "smect", "montmor", "chlorit", "benton", "argill"};
    private static final String[] CORES_MUD = {"pelit", "lutit", "silt", "argill", "turbid"};
    private static final String[] CORES_SALT = {"halit", "sylv", "gyps", "anhydr", "carnal", "mirab", "trona", "epsom"};
    private static final String[] CORES_VOLCANIC = {"tuff", "pumic", "obsid", "scori", "basalt", "andes", "dacite", "rhyol", "tephr"};
    private static final String[] CORES_WOOD = {
        "querc", "betul", "pice", "abies", "laric", "pin", "salix", "acac", "ulmus",
        "juglan", "coryl", "acer", "tilia", "cedr", "mahog", "ebon", "bambu", "mangr",
        "cherri", "darkoak", "baoba", "teak", "ironw"
    };

    // ========================================================================
    // Suffix pools
    // ========================================================================
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
    private static final String[] SUF_VOLCANIC = {"tuff", "tephra", "pumice", "scoria", "glass", "obsidian"};
    private static final String[] SUF_WOOD = {"wood", "bark", "heartwood", "timber", "plank"};

    private static final String[] LINKS = {"o", "a", "i", "e", "", "", ""};
    private static final String[] BAD_PATTERNS = {"jj", "vv", "ww", "yy", "qq", "hhh", "kkk", "zzz", "xxx", "aaae", "iii", "uuu"};

    // ========================================================================
    // Configuration per kind
    // ========================================================================
    private record KindConfig(
        String[] cores,
        String[] defaultSuffixes,
        int colorWeight,
        int chemWeight,
        int coreOnlyWeight,
        boolean preferChemPrefix,
        boolean allowColorPrefix
    ) {}

    // ========================================================================
    // Configuration per kind – using static block for clarity
    // ========================================================================
    private static final Map<MaterialKind, KindConfig> KIND_CONFIGS;

    static {
        Map<MaterialKind, KindConfig> map = new EnumMap<>(MaterialKind.class);

        map.put(MaterialKind.METAL,    new KindConfig(merge(CORES_METAL, CORES_GENERIC), SUF_ORE,
                25, 55, 20, true,  true));
        map.put(MaterialKind.ALLOY,    new KindConfig(CORES_ALLOY, SUF_ALLOY,
                15, 65, 20, true,  false));
        map.put(MaterialKind.GEM,      new KindConfig(merge(CORES_GEM, CORES_GENERIC), SUF_GEM,
                55, 25, 20, false, true));
        map.put(MaterialKind.CRYSTAL,  new KindConfig(merge(CORES_CRYSTAL, CORES_GENERIC), merge(SUF_ORE, SUF_CRYSTAL),
                50, 25, 25, false, true));
        map.put(MaterialKind.STONE,    new KindConfig(CORES_GENERIC, merge(SUF_ORE, SUF_CRYSTAL),
                40, 35, 25, false, true));
        map.put(MaterialKind.SOIL,     new KindConfig(CORES_SOIL, SUF_SOIL,
                55, 15, 30, false, true));
        map.put(MaterialKind.SAND,     new KindConfig(CORES_SAND, SUF_SAND,
                50, 10, 40, false, true));
        map.put(MaterialKind.GRAVEL,   new KindConfig(CORES_GRAVEL, SUF_GRAVEL,
                45, 10, 45, false, true));
        map.put(MaterialKind.CLAY,     new KindConfig(CORES_CLAY, SUF_CLAY,
                50, 15, 35, false, true));
        map.put(MaterialKind.MUD,      new KindConfig(CORES_MUD, SUF_MUD,
                50, 10, 40, false, true));
        map.put(MaterialKind.SALT,     new KindConfig(CORES_SALT, SUF_SALT,
                40, 30, 30, true,  true));
        map.put(MaterialKind.VOLCANIC, new KindConfig(CORES_VOLCANIC, SUF_VOLCANIC,
                45, 20, 35, false, true));
        map.put(MaterialKind.WOOD,     new KindConfig(CORES_WOOD, SUF_WOOD,
                50, 20, 30, false, true));
        map.put(MaterialKind.OTHER,    new KindConfig(CORES_GENERIC, merge(SUF_ORE, SUF_CRYSTAL),
                45, 35, 20, false, true));

        KIND_CONFIGS = Map.copyOf(map); // immutable view
    }

    // ========================================================================
    // Hash mixing utilities (all overloads restored)
    // ========================================================================
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

    private static long mix(long a, long b, long c, long d) {
        return mix(mix(a, b, c), d, 0xD6E8FEB86659FD93L);
    }

    private static long mix(long a, long b, long c, long d, long e) {
        long x = mix(a, b, c, d);
        x ^= Long.rotateLeft(x, 23);
        x *= 0x9E3779B97F4A7C15L;
        x ^= x >>> 31;
        return x ^ e;
    }

    // ========================================================================
    // Public API
    // ========================================================================
    public static MaterialDef.NameInformation generate(long worldSeed, int index, MaterialDef def) {
        Random rng = new Random(mix(worldSeed, 0x6A09E667F3BCC909L, def.kind().ordinal(), index));
        String display = generateDisplayName(rng, def.kind(), def.primaryColor(), def.spawn().y());
        String id = slug(display) + "_" + shortHash(mix(worldSeed, display.hashCode(), def.kind().ordinal()));
        return new MaterialDef.NameInformation(RAAMaterials.id(id), display);
    }

    public static MaterialDef.NameInformation generate(long worldSeed, int index, MaterialKind kind, int primaryColor, SpawnInfo spawn) {
        Random rng = new Random(mix(worldSeed, 0x6A09E667F3BCC909L, kind.ordinal(), index));
        String display = generateDisplayName(rng, kind, primaryColor, spawn.y());
        String id = slug(display) + "_" + shortHash(mix(worldSeed, display.hashCode(), kind.ordinal()));
        return new MaterialDef.NameInformation(RAAMaterials.id(id), display);
    }

    // ========================================================================
    // Core generation
    // ========================================================================
    private static String generateDisplayName(Random rng, MaterialKind kind, Integer rgb, SpawnInfo.YDistribution yDist) {
        KindConfig cfg = KIND_CONFIGS.getOrDefault(kind, KIND_CONFIGS.get(MaterialKind.OTHER));

        int archetype = weightedChoice(rng, cfg.colorWeight, cfg.chemWeight, cfg.coreOnlyWeight);

        String prefix = switch (archetype) {
            case 0 -> pickWeightedPrefix(rng, rgb, kind, yDist.minY(), yDist.maxY(), cfg);
            case 1 -> pick(rng, CHEM);
            default -> "";
        };
        prefix = normalizePrefix(prefix);

        String core = kind == MaterialKind.WOOD ? pick(rng, CORES_WOOD) : pick(rng, cfg.cores);
        if (prefix.isEmpty() && rng.nextInt(100) < 35) {
            core = smoothCore(core, rng);
        }

        String link = pick(rng, LINKS);
        String suffix = pick(rng, cfg.defaultSuffixes);

        String raw = composeName(prefix, link, core, suffix, kind);
        raw = applyMineralOrthography(raw);

        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
    }

    private static String pickWeightedPrefix(Random rng, Integer rgb, MaterialKind kind,
                                            int minY, int maxY, KindConfig cfg) {
        List<Weighted<String[]>> pool = new ArrayList<>();

        if (rgb != null && cfg.allowColorPrefix) {
            float[] hsv = rgbToHsv(rgb);
            addHueBasedColors(pool, hsv[0]);
            if (hsv[1] < 0.18f) addWeighted(pool, ColorMorpheme.GRAY.morphemes, 2);
            if (hsv[2] < 0.28f) addWeighted(pool, ColorMorpheme.BLACK.morphemes, 2);
            if (hsv[2] > 0.80f) addWeighted(pool, ColorMorpheme.MISC.morphemes, 1);
        }

        addWeighted(pool, CHEM, cfg.preferChemPrefix ? 4 : 1);
        addDepthBias(pool, minY, maxY);

        String[] bank = pickFromWeighted(rng, pool);
        return bank[rng.nextInt(bank.length)];
    }

    private static void addHueBasedColors(List<Weighted<String[]>> pool, float hue) {
        if (hue >= 75 && hue < 170)  addWeighted(pool, ColorMorpheme.GREEN.morphemes, 5);
        else if (hue >= 170 && hue < 205) addWeighted(pool, ColorMorpheme.TEAL.morphemes, 4);
        else if (hue >= 205 && hue < 255) addWeighted(pool, ColorMorpheme.BLUE.morphemes, 5);
        else if (hue >= 255 && hue < 295) addWeighted(pool, ColorMorpheme.PURPLE.morphemes, 4);
        else if (hue >= 295 && hue < 335) addWeighted(pool, ColorMorpheme.PINK.morphemes, 3);
        else if (hue < 20 || hue >= 335) addWeighted(pool, ColorMorpheme.RED.morphemes, 4);
        else if (hue >= 20 && hue < 45) addWeighted(pool, ColorMorpheme.ORANGE.morphemes, 4);
        else if (hue >= 45 && hue < 65) addWeighted(pool, ColorMorpheme.YELLOW.morphemes, 4);
        else addWeighted(pool, ColorMorpheme.BROWN.morphemes, 2);
    }

    private static void addDepthBias(List<Weighted<String[]>> pool, int minY, int maxY) {
        int mid = (minY + maxY) / 2;
        if (mid <= 0) {
            addWeighted(pool, ColorMorpheme.BLACK.morphemes, 3);
            addWeighted(pool, ColorMorpheme.GRAY.morphemes, 2);
            addWeighted(pool, CHEM, 2);
        } else if (mid >= 80) {
            addWeighted(pool, ColorMorpheme.WHITE.morphemes, 3);
            addWeighted(pool, ColorMorpheme.BLUE.morphemes, 2);
            addWeighted(pool, ColorMorpheme.GRAY.morphemes, 1);
        } else {
            addWeighted(pool, ColorMorpheme.BROWN.morphemes, 2);
            addWeighted(pool, ColorMorpheme.GREEN.morphemes, 1);
            addWeighted(pool, CHEM, 1);
        }
    }

    // ========================================================================
    // Composition & cleanup
    // ========================================================================
    private static String composeName(String prefix, String link, String core, String suffix, MaterialKind kind) {
        String base = prefix + (prefix.isEmpty() ? "" : link) + core + suffix;

        return switch (kind) {
            case GRAVEL -> normalizeGravel(base, prefix, link);
            case SAND -> normalizeSand(base, prefix, link);
            case CLAY -> normalizeClay(base, prefix, link);
            case MUD -> normalizeMud(base, prefix, link);
            case SALT -> normalizeSalt(base, prefix, link);
            case VOLCANIC -> normalizeVolcanic(base, prefix, link, core);
            case SOIL -> normalizeSoil(base, core);
            case WOOD -> normalizeWood(base, core);
            default -> base;
        };
    }

    private static String tidy(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        s = s.toLowerCase(ROOT).replaceAll("[^a-z]", "");
        for (String bad : BAD_PATTERNS) {
            s = s.replaceAll(bad, bad.substring(0, Math.min(2, bad.length())));
        }
        return s.isEmpty() ? "mineralite" : s;
    }

    // ========================================================================
    // Utilities
    // ========================================================================
    private static int weightedChoice(Random r, int... weights) {
        int sum = Arrays.stream(weights).sum();
        int roll = r.nextInt(Math.max(sum, 1));
        int acc = 0;
        for (int i = 0; i < weights.length; i++) {
            acc += weights[i];
            if (roll < acc) return i;
        }
        return weights.length - 1;
    }

    private static <T> T pick(Random r, T[] array) {
        return array[r.nextInt(array.length)];
    }

    private static String[] pickFromWeighted(Random r, List<Weighted<String[]>> pool) {
        int total = pool.stream().mapToInt(Weighted::weight).sum();
        int roll = r.nextInt(Math.max(total, 1));
        int acc = 0;
        for (Weighted<String[]> w : pool) {
            acc += w.weight;
            if (roll < acc) return w.value;
        }
        return CHEM;
    }

    private static void addWeighted(List<Weighted<String[]>> list, String[] bank, int weight) {
        list.add(new Weighted<>(bank, Math.max(1, weight)));
    }

    private record Weighted<T>(T value, int weight) {}

    private static String[] merge(String[] a, String[] b) {
        String[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static String slug(String display) {
        String s = Normalizer.normalize(display, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(ROOT)
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("(^_+|_+$)", "");
        return s.isEmpty() ? "mat" : s;
    }

    private static String shortHash(long v) {
        long h = v ^ (v >>> 33) ^ 0x9E3779B97F4A7C15L;
        String hex = Long.toHexString(h);
        return hex.substring(Math.max(0, hex.length() - 4));
    }

    private static String smoothCore(String core, Random rng) {
        String cv = pick(rng, new String[]{
                "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "r", "s", "t", "v", "z",
                "br", "cr", "dr", "fr", "gr", "kr", "pr", "tr", "str", "pl", "gl", "cl", "sl"
        }) + pick(rng, new String[]{"a", "e", "i", "o", "u", "y", "ae", "ia", "io", "ou", "ui", "ea"});
        String candidate = cv + core;
        return candidate.length() < 5 ? candidate : core;
    }

    private static String normalizePrefix(String prefix) {
        if (prefix == null || prefix.isEmpty()) return prefix;
        return switch (prefix) {
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
            default -> prefix;
        };
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

        float h = 0;
        if (delta != 0) {
            if (max == rf) {
                h = 60f * (((gf - bf) / delta) % 6f);
            } else if (max == gf) {
                h = 60f * (((bf - rf) / delta) + 2f);
            } else {
                h = 60f * (((rf - gf) / delta) + 4f);
            }
        }
        if (h < 0) h += 360f;

        float s = max == 0 ? 0 : delta / max;
        float v = max;

        return new float[]{h, s, v};
    }

    // ========================================================================
    // Per-kind name normalization (replaces the huge switch in original compose)
    // ========================================================================

    private static String normalizeGravel(String base, String prefix, String link) {
        String p = prefix.isEmpty() ? "" : prefix + link;
        if (base.contains("conglom") && base.contains("conglomerate")) return "conglomerate";
        if (base.contains("brecc") && (base.contains("breccia") || base.contains("rudite"))) return "breccia";
        if (base.contains("rud") && base.contains("rudite")) return "rudite";
        if (base.contains("gruss") && base.contains("gravel")) return p + "gruss";
        if (base.contains("rud") && base.contains("conglomerate")) return p + "conglomerate";
        return base;
    }

    private static String normalizeSand(String base, String prefix, String link) {
        String p = prefix.isEmpty() ? "" : prefix + link;
        if (base.contains("aren") && base.contains("sandstone")) return p + "sandstone";
        if (base.contains("aren") && base.contains("arenite")) return p + "arenite";
        if (base.contains("psamm") && base.contains("sand")) return p + "psammite";
        return base;
    }

    private static String normalizeClay(String base, String prefix, String link) {
        String p = prefix.isEmpty() ? "" : prefix + link;
        if (base.contains("kaolin") && base.contains("kaolinite")) return p + "kaolinite";
        if (base.contains("argill") && (base.contains("clay") || base.contains("argillite")))
            return p + (base.contains("argillite") ? "argillite" : "clay");
        if (base.contains("illite") && base.contains("argillite")) return p + "argillite";
        if (base.contains("benton")) return p + "bentonite";
        return base;
    }

    private static String normalizeMud(String base, String prefix, String link) {
        String p = prefix.isEmpty() ? "" : prefix + link;
        if (base.contains("pelit") && base.contains("pelite")) return p + "pelite";
        if (base.contains("lutit") && base.contains("lutite")) return p + "lutite";
        if (base.contains("silt") && base.contains("mudstone")) return p + "mudstone";
        return base;
    }

    private static String normalizeSalt(String base, String prefix, String link) {
        String p = prefix.isEmpty() ? "" : prefix + link;
        if (base.contains("halit") && (base.contains("halite") || base.contains("salt") || base.contains("evaporite")))
            return p + "halite";
        if (base.contains("sylv") && (base.contains("sylvite") || base.contains("salt") || base.contains("evaporite")))
            return p + "sylvite";
        if (base.contains("gyps") && (base.contains("gypsum") || base.contains("salt") || base.contains("evaporite")))
            return p + "gypsum";
        if (base.contains("anhydr") && (base.contains("anhydrite") || base.contains("salt") || base.contains("evaporite")))
            return p + "anhydrite";
        if (base.contains("epsom")) return p + "epsomite";
        if (base.contains("mirab")) return p + "mirabilite";
        if (base.contains("carnal")) return p + "carnalite";
        if (base.contains("trona")) return p + "trona";
        return base;
    }

    private static String normalizeVolcanic(String base, String prefix, String link, String core) {
        String p = prefix.isEmpty() ? "" : prefix + link;
        if (core.startsWith("pumic")) return p + "tephra";
        if (core.startsWith("scori")) return p + "scoria";
        if (core.startsWith("tephr")) return p + "tephra";
        if (core.startsWith("tuff")) return p + "tuff";
        if (core.startsWith("obsid")) return p + "vitrophyre";
        if (base.contains("glass")) {
            if (core.startsWith("basalt")) return p + "tachylite";
            return p + "vitrophyre";
        }
        return base;
    }

    private static String normalizeSoil(String base, String core) {
        if (core.endsWith("sol") && (base.contains("soil") || base.contains("sol") || base.contains("dirt")))
            return core;
        if (core.startsWith("loess") && (base.contains("soil") || base.contains("sol") || base.contains("lite")))
            return "loess";
        return base;
    }

    private static String normalizeWood(String base, String core) {
        String cleanedCore = core.replaceAll("wood$", "");
        String raw = base.replace(core, cleanedCore);
        raw = raw.replaceAll("rcw", "rw").replaceAll("cwood$", "wood");
        raw = raw.replaceAll("^ironw(timber|plank|bark|heartwood|wood)$", "ironwood");
        return raw;
    }

    // ========================================================================
    // Final orthography fixes (applied after per-kind normalization)
    // ========================================================================

    private static String applyMineralOrthography(String s) {
        s = s.replaceAll("([bcdfghjklmnpqrstvwxyz])\\1{2,}", "$1$1");
        s = s.replaceAll("([aeiou])\\1{2,}", "$1$1");
        s = s.replaceAll("ite(ite)+$", "ite");

        s = s.replaceFirst("^recc", "brecc");
        if (s.startsWith("brecc") && s.endsWith("rudite")) {
            s = s.replaceAll("rudite$", "ia");
        }

        s = s.replaceAll("(aren)\\1(ite)", "$1$2")
                .replaceAll("(rud)\\1(ite)", "$1$2")
                .replaceAll("(conglom)\\1(erate)", "$1$2")
                .replaceAll("(sylv)\\1(ite)", "$1$2")
                .replaceAll("(halit)\\1(e)", "$1$2")
                .replaceAll("(gyps)\\1(um)", "$1$2")
                .replaceAll("(tephr)\\1(a)", "$1$2")
                .replaceAll("(pumic)\\1(e)", "$1$2")
                .replaceAll("(brecc)\\1(ia)", "$1$2");

        s = s.replaceAll("solsoil$", "sol")
                .replaceAll("soilsol$", "sol")
                .replaceAll("(sol)(?:\\1)+$", "$1")
                .replaceAll("(loess)(?:sol|soil|lite)$", "$1");

        s = s.replaceAll("^chloro?chlorit", "chlorit");

        // Block vanilla names
        s = s.replaceAll("obsidian$", "vitrophyre")
                .replaceAll("obsidian", "vitrophyre")
                .replaceAll("pumice$", "tephra")
                .replaceAll("pumice", "tephra");

        return tidy(s);
    }

    // ========================================================================
    // Local testing / debugging main method
    // ========================================================================
    public static void main(String[] args) {
        long baseSeed = 123456789L; // Fixed seed → reproducible results
        System.out.println("=== GeoNameGen Test Output (seed: " + baseSeed + ") ===\n");

        Random colorRng = new Random(baseSeed);

        for (MaterialKind kind : MaterialKind.values()) {
            System.out.printf("--- %s ---%n", kind.name());

            for (int i = 0; i < 8; i++) {
                // Choose a plausible color for the kind
                int color = switch (kind) {
                    case GEM, CRYSTAL -> randomGemColor(colorRng);
                    case METAL, ALLOY -> randomMetalColor(colorRng);
                    case WOOD -> randomWoodColor(colorRng);
                    case SOIL, CLAY, MUD -> randomEarthColor(colorRng);
                    case SAND, GRAVEL -> randomSandGravelColor(colorRng);
                    case SALT -> 0xFFFFFF; // whiteish salts
                    case VOLCANIC -> randomVolcanicColor(colorRng);
                    default -> colorRng.nextInt(0x1000000);
                };
// Realistic Y range per kind (to trigger depth bias correctly)
                int minY, maxY;
                switch (kind) {
                    case METAL, GEM, CRYSTAL, SALT -> {
                        minY = -59 + colorRng.nextInt(80);  // deep underground
                        maxY = minY + 16 + colorRng.nextInt(48);
                    }
                    case VOLCANIC -> {
                        minY = -30 + colorRng.nextInt(60);
                        maxY = minY + 20 + colorRng.nextInt(40);
                    }
                    case WOOD -> {
                        minY = 50;
                        maxY = 120;
                    }
                    default -> {
                        minY = colorRng.nextInt(100) - 40;
                        maxY = minY + 20 + colorRng.nextInt(80);
                    }
                }
                int centerY = (minY + maxY) / 2;
                int spread = 20 + colorRng.nextInt(40);

                SpawnInfo.YDistribution yDist = new SpawnInfo.YDistribution(minY, maxY, centerY, spread);

                // Realistic but simple spawn settings
                SpawnInfo spawnInfo = new SpawnInfo(
                        6 + colorRng.nextInt(10),           // attemptsPerChunk: 6–15
                        0.6f + colorRng.nextFloat() * 0.4f, // successChance: 0.6–1.0
                        3 + colorRng.nextInt(8),            // veinMin
                        8 + colorRng.nextInt(16),           // veinMax
                        SpawnInfo.VeinShape.ORE_BLOB,            // most common shape
                        yDist,
                        new SpawnInfo.NoiseGate(0.004f + colorRng.nextFloat() * 0.01f, 0.0f),  // regionGate
                        new SpawnInfo.NoiseGate(0.03f + colorRng.nextFloat() * 0.04f, 0.2f),   // pocketGate
                        false,                         // mustTouchAir
                        kind == MaterialKind.SALT || kind == MaterialKind.CLAY,
                        kind == MaterialKind.VOLCANIC
                );
                // Use the simpler generate overload
                MaterialDef.NameInformation name = generate(baseSeed, i, kind, color, spawnInfo);

                System.out.printf("  [%02d] Color: #%06X  Y: %3d..%3d  →  %-28s  (id: %s)%n",
                        i, color & 0xFFFFFF, minY, maxY, name.displayName(), name.id().getPath());
            }
            System.out.println();
        }

        System.out.println("=== End of Test ===");
    }

    // Simple realistic color helpers
    private static int randomGemColor(Random rng) {
        return switch (rng.nextInt(8)) {
            case 0 -> 0xFF0000 + rng.nextInt(0x660000); // reds/rubies
            case 1 -> 0x0000FF + rng.nextInt(0x6600FF); // blues/sapphires
            case 2 -> 0x00FF00 + rng.nextInt(0x66FF00); // greens/emeralds
            case 3 -> 0xFFFF00 + rng.nextInt(0xFFFF66); // yellows
            case 4 -> 0xFF00FF + rng.nextInt(0xFF66FF); // purples/amythests
            case 5 -> 0x00FFFF + rng.nextInt(0x66FFFF); // cyans/aquamarine
            case 6 -> 0xFFA500 + rng.nextInt(0xFFAA66); // oranges/topaz
            default -> 0xFFFFFF; // clear diamond
        };
    }

    private static int randomMetalColor(Random rng) {
        return switch (rng.nextInt(7)) {
            case 0 -> 0xFFD700 + rng.nextInt(0x333300); // gold
            case 1 -> 0xC0C0C0 + rng.nextInt(0x303030); // silver
            case 2 -> 0xB87333 + rng.nextInt(0x333333); // copper/bronze
            case 3 -> 0x808080 + rng.nextInt(0x404040); // iron/steel
            case 4 -> 0xB7410E + rng.nextInt(0x333333); // rust
            case 5 -> 0xE5E5E5 + rng.nextInt(0x1A1A1A); // platinum
            default -> 0x5A5A5A + rng.nextInt(0x505050); // dark metals
        };
    }

    private static int randomWoodColor(Random rng) {
        int base = switch (rng.nextInt(6)) {
            case 0 -> 0x8B4513; // dark oak / mahogany
            case 1 -> 0xD2B48C; // oak / light tan
            case 2 -> 0xF4A460; // birch / pale
            case 3 -> 0x556B2F; // spruce / dark green-tinged
            case 4 -> 0xCD853F; // acacia / orange-brown
            default -> 0xA0522D; // cherry / reddish
        };
        return base + rng.nextInt(0x202020);
    }

    private static int randomEarthColor(Random rng) {
        return 0x8B4513 + rng.nextInt(0x604020); // browns, clays, soils
    }

    private static int randomSandGravelColor(Random rng) {
        return switch (rng.nextInt(4)) {
            case 0 -> 0xF4A460 + rng.nextInt(0x303020); // yellow sand
            case 1 -> 0xD2B48C + rng.nextInt(0x303020); // pale sandstone
            case 2 -> 0xC2B280 + rng.nextInt(0x303020); // beige
            default -> 0x808080 + rng.nextInt(0x404040); // gray gravel
        };
    }

    private static int randomVolcanicColor(Random rng) {
        return switch (rng.nextInt(4)) {
            case 0 -> 0x1A1A1A + rng.nextInt(0x202020); // basalt black
            case 1 -> 0x8B0000 + rng.nextInt(0x300000); // dark red scoria
            case 2 -> 0x696969 + rng.nextInt(0x303030); // gray tuff
            default -> 0x000000; // obsidian
        };
    }
}