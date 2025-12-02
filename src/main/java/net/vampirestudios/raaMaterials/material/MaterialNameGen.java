package net.vampirestudios.raaMaterials.material;

import java.text.Normalizer;
import java.util.*;

/**
 * Deterministic material displayName generator with kind-aware suffixes,
 * soft theme bias, and simple phonotactics.
 */
public final class MaterialNameGen {

    // ===== Public API =====

    public record Name(String display, String id) {}

    public enum Theme {
        FANTASY, MYSTICAL, ANCIENT, ELEMENTAL, ELVEN, DWARVEN, FROST, ROYAL,
        LUMINOUS, EARTH, CELESTIAL, ETHEREAL, EXOTIC, LEGACY, NEUTRAL
    }

    /**
     * Generate a deterministic displayName for a material.
     * @param worldSeed the level seed
     * @param kind the material family (METAL/GEM/CRYSTAL/ALLOY/OTHER)
     * @param index stable ordinal for this material within the world (0..N-1)
     * @param theme optional theme bias (null = NEUTRAL)
     */
    public static Name generate(long worldSeed, MaterialKind kind, int index, Theme theme) {
        var rng = new Random(mix(worldSeed, 0xC0FEBABEL, kind.ordinal(), index));
        Theme useTheme = theme != null ? theme : Theme.NEUTRAL;

        // Build syllables via phonotactics template
        String base = buildCore(rng, useTheme);

        // Attach kind-aware suffix
        String suff = pickSuffix(rng, kind);
        String display = titleCase(base + suff);

        // Make registry-safe id, ensure determinism, avoid collisions by appending short hash
        String idBase = slug(display);
        String id = idBase + "_" + shortHash(mix(worldSeed, 0x51AFFF77, display.hashCode(), kind.ordinal()));

        return new Name(display, id);
    }

    // ===== Phonotactics & Syllable Banks =====

    private static final String[] VOWELS = {
        "a","e","i","o","u","y","ae","ai","ea","ia","io","oa","ou","ui"
    };
    private static final String[] CONSONANTS = {
        "b","c","d","f","g","h","j","k","l","m","n","p","r","s","t","v","w","z",
        "br","cr","dr","fr","gr","kr","pr","tr","vr","wr","str","skr",
        "bl","cl","fl","gl","kl","pl","sl"
    };
    // To avoid ugly sequences, forbid some combos
    private static final Set<String> FORBIDDEN = Set.of("jj","vv","ww","yy","qq","hhh","kkk","xxx","zz","--","''");

    // Theme nudge pools (lightweight compared to old all-in-one strings)
    private static final Map<Theme, String[]> THEME_ONSETS = Map.ofEntries(
        Map.entry(Theme.FANTASY, new String[]{"myth","aeth","zan","tyr","eld","lor","fey"}),
        Map.entry(Theme.MYSTICAL,new String[]{"lum","ser","nyx","eos","aura","mira"}),
        Map.entry(Theme.ANCIENT, new String[]{"thar","rune","krag","dur","bryn","gorm","grim","bor"}),
        Map.entry(Theme.ELEMENTAL,new String[]{"pyro","aqua","geo","zeph","volt","ether","frost"}),
        Map.entry(Theme.ELVEN,   new String[]{"ela","fael","ith","nim","sil","thal"}),
        Map.entry(Theme.DWARVEN, new String[]{"brag","dwal","gloin","mor","skir","thrain"}),
        Map.entry(Theme.FROST,   new String[]{"cry","frig","glac","bore","ymir","nive"}),
        Map.entry(Theme.ROYAL,   new String[]{"reg","majest","nobil","sovre","crown"}),
        Map.entry(Theme.LUMINOUS,new String[]{"lux","luci","phos","radi","clar","splend"}),
        Map.entry(Theme.EARTH,   new String[]{"terra","rock","slate","clay","dust"}),
        Map.entry(Theme.CELESTIAL,new String[]{"astra","stella","sider","neb","nova","cosmo"}),
        Map.entry(Theme.ETHEREAL,new String[]{"spir","ether","phant","whisp","arc"}),
        Map.entry(Theme.EXOTIC,  new String[]{"oasis","lotus","coral","sahar","nile"})
    );

    // Kind-aware suffixes
    private static final String[] SUF_METAL   = {"ium","ite","or","anite","ium","steel","arite"};
    private static final String[] SUF_ALLOY   = {"alloy","bronze","steel","fusion","blend"};
    private static final String[] SUF_GEM     = {"ite","spar","olite","dite","jewel","beryl"};
    private static final String[] SUF_CRYSTAL = {"cryst","lith","shard","glaze","prism"};
    private static final String[] SUF_OTHER   = {"stone","rock","mass","core","flux"};

    // CV templates (C=consonant cluster, V=vowel group), weighted
    private static final String[] TEMPLATES = {
        "CV","CVC","CVV","CVCV","CCVC","CVCC","CVCVC","CVCCV","CCVCC","CVCVCV"
    };

    private static String buildCore(Random rng, Theme theme) {
        // 1–3 syllables based on weight (shorter more common)
        int syllables = weighted(rng, new int[]{50,35,15}) + 1; // returns 0..2 → +1 → 1..3
        StringBuilder sb = new StringBuilder();

        // 10% chance to start with a theme onset, else phonotactic onset
        boolean themedStart = theme != Theme.NEUTRAL && rng.nextInt(100) < 35;
        if (themedStart) {
            var bank = THEME_ONSETS.getOrDefault(theme, new String[0]);
            if (bank.length > 0) {
                sb.append(pick(rng, bank));
                syllables = Math.max(1, syllables - 1);
            }
        }

        for (int i = 0; i < syllables; i++) {
            String tmpl = pick(rng, TEMPLATES);
            for (int t = 0; t < tmpl.length(); t++) {
                char c = tmpl.charAt(t);
                if (c == 'C') sb.append(pickCon(rng, sb));
                else if (c == 'V') sb.append(pickVow(rng, sb));
            }
        }
        String out = sanitize(sb.toString());
        // First char uppercase for display -> suffix handles casing next
        return out;
    }

    private static String pickSuffix(Random rng, MaterialKind kind) {
        return switch (kind) {
            case METAL   -> pick(rng, SUF_METAL);
            case ALLOY   -> pick(rng, SUF_ALLOY);
            case GEM     -> pick(rng, SUF_GEM);
            case CRYSTAL -> pick(rng, SUF_CRYSTAL);
            default      -> pick(rng, SUF_OTHER);
        };
    }

    private static String pickCon(Random rng, CharSequence soFar) {
        String c;
        int guard = 0;
        do { c = pick(rng, CONSONANTS); } while (badJoin(soFar, c) && guard++ < 8);
        return c;
    }

    private static String pickVow(Random rng, CharSequence soFar) {
        String v;
        int guard = 0;
        do { v = pick(rng, VOWELS); } while (badJoin(soFar, v) && guard++ < 8);
        return v;
    }

    private static boolean badJoin(CharSequence prev, String add) {
        if (prev.length() == 0) return false;
        String s = (prev.toString() + add).toLowerCase(Locale.ROOT);
        if (s.contains("qq") || s.contains("hh")) return true;
        for (String f : FORBIDDEN) if (s.contains(f)) return true;
        // Avoid triple vowels/consonants in a row
        if (s.matches(".*[aeiouy]{3,}.*")) return true;
        if (s.matches(".*[bcdfghjklmnpqrstvwxz]{4,}.*")) return true;
        return false;
    }

    private static String sanitize(String s) {
        // remove accidental apostrophes/diacritics, compress doubles
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        n = n.replaceAll("[^A-Za-z]", "");
        n = n.replaceAll("(.)\\1{2,}", "$1$1"); // collapse 3+ same letters → double
        if (n.isEmpty()) n = "Ar"; // fallback
        n = Character.toUpperCase(n.charAt(0)) + n.substring(1);
        return n;
    }

    private static String slug(String display) {
        String x = display.toLowerCase(Locale.ROOT);
        x = Normalizer.normalize(x, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        x = x.replaceAll("[^a-z0-9]+", "_").replaceAll("_+", "_");
        x = x.replaceAll("^_+|_+$", "");
        if (x.isEmpty()) x = "mat";
        return x;
    }

    private static String titleCase(String s) {
        // Basic: first letter caps, ensure suffix not yelling
        if (s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String shortHash(long v) {
        // 4-hex chars is plenty to avoid id collisions across runs
        long h = mix(v, 0x9E3779B97F4A7C15L, 0xD1B54A32D192ED03L);
        String hex = Long.toHexString(h);
        return hex.substring(Math.max(0, hex.length() - 4));
    }

    private static <T> T pick(Random rng, T[] arr) {
        return arr[rng.nextInt(arr.length)];
    }

    // returns 0..(weights.length-1)
    static int weighted(Random rng, int[] weights) {
        int sum = 0;
        for (int w : weights) sum += w;
        int roll = rng.nextInt(sum);
        int acc = 0;
        for (int i = 0; i < weights.length; i++) {
            acc += weights[i];
            if (roll < acc) return i;
        }
        return weights.length - 1;
    }

    private static long mix(long a, long b, long c) {
        long x = a ^ b; x ^= Long.rotateLeft(x, 27);
        x = (x + c) * 0x9E3779B97F4A7C15L;
        x ^= x >>> 33; x *= 0xC2B2AE3D27D4EB4FL;
        return x ^ (x >>> 29);
    }
    private static long mix(long a, long b, long c, long d) {
        return mix(mix(a,b,c), d, 0xD6E8FEB86659FD93L);
    }

    // Quick demo
    public static void main(String[] args) {
        long seed = 123456789L;
        for (int i = 0; i < 6; i++) {
            System.out.println(generate(seed, MaterialKind.METAL, i, Theme.ANCIENT));
            System.out.println(generate(seed, MaterialKind.GEM,   i, Theme.MYSTICAL));
            System.out.println(generate(seed, MaterialKind.CRYSTAL,i, Theme.CELESTIAL));
        }
    }
}
