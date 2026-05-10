package net.vampirestudios.raaMaterials;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.codecextras.config.ConfigType;
import dev.lukebemish.codecextras.structured.Annotation;
import dev.lukebemish.codecextras.structured.IdentityInterpreter;
import dev.lukebemish.codecextras.structured.Structure;
import net.minecraft.util.GsonHelper;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialGenerator;
import net.vampirestudios.raaMaterials.material.MaterialKind;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Map.entry;
import static net.vampirestudios.raaMaterials.material.MaterialKind.*;

public record RAAConfig(int materialsMin, int materialsMax, Map<MaterialKind, Integer> kindWeights,
                        net.vampirestudios.raaMaterials.RAAConfig.TierWeights tiers,
                        Map<MaterialKind, ModeWeights> spawnMode, BlockStats blockStats,
                        Map<MaterialKind, DepthBand> depthWeights, Map<String, YRange> shallowRange,
                        Map<String, YRange> midRange, Map<String, YRange> deepRange,
                        Map<String, List<String>> replaceables,
                        net.vampirestudios.raaMaterials.RAAConfig.NameGen nameGen,
                        Map<MaterialKind, KindPolicy> formControls, int toolChancePercent,
                        MaterialGenerator.Profile defaultProfile) {
    // ----- Records / Beans -----
    public record TierWeights(int stone, int iron, int diamond, int netherite) {
        public static final Codec<TierWeights> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("stone").forGetter(TierWeights::stone),
                Codec.INT.fieldOf("iron").forGetter(TierWeights::iron),
                Codec.INT.fieldOf("diamond").forGetter(TierWeights::diamond),
                Codec.INT.fieldOf("netherite").forGetter(TierWeights::netherite)
        ).apply(i, TierWeights::new));
        public static final Structure<TierWeights> STRUCTURE = Structure.record(builder -> {
            var a = builder.add("stone", Structure.INT, TierWeights::stone);
            var b = builder.add("iron", Structure.INT, TierWeights::iron);
            var c = builder.add("diamond", Structure.INT, TierWeights::diamond);
            var d = builder.add("netherite", Structure.INT, TierWeights::netherite);
            return container -> new TierWeights(
                    a.apply(container), b.apply(container), c.apply(container), d.apply(container)
            );
        });

        public TierWeights() {
            this(40, 40, 15, 5);
        }
    }

    public record ModeWeights(int ore, int geode, int cluster) {
        public static final Codec<ModeWeights> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("ore").forGetter(ModeWeights::ore),
                Codec.INT.fieldOf("geode").forGetter(ModeWeights::geode),
                Codec.INT.fieldOf("cluster").forGetter(ModeWeights::cluster)
        ).apply(i, ModeWeights::new));
        public static final Structure<ModeWeights> STRUCTURE = Structure.record(builder -> {
            var a = builder.add("ore", Structure.INT, ModeWeights::ore);
            var b = builder.add("geode", Structure.INT, ModeWeights::geode);
            var c = builder.add("cluster", Structure.INT, ModeWeights::cluster);
            return container -> new ModeWeights(
                    a.apply(container), b.apply(container), c.apply(container)
            );
        });

        public ModeWeights() {
            this(70, 15, 15);
        }
    }

    public record DepthBand(int shallow, int mid, int deep) {
        public static final Codec<DepthBand> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("shallow").forGetter(DepthBand::shallow),
                Codec.INT.fieldOf("mid").forGetter(DepthBand::mid),
                Codec.INT.fieldOf("deep").forGetter(DepthBand::deep)
        ).apply(i, DepthBand::new));
        public static final Structure<DepthBand> STRUCTURE = Structure.record(builder -> {
            var a = builder.add("shallow", Structure.INT, DepthBand::shallow);
            var b = builder.add("mid", Structure.INT, DepthBand::mid);
            var c = builder.add("deep", Structure.INT, DepthBand::deep);
            return container -> new DepthBand(
                    a.apply(container), b.apply(container), c.apply(container)
            );
        });

        public DepthBand() {
            this(30, 40, 30);
        }
    }

    public record YRange(int minY, int maxY, int peakMin, int peakMax) {
        public static final Codec<YRange> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("minY").forGetter(YRange::minY),
                Codec.INT.fieldOf("maxY").forGetter(YRange::maxY),
                Codec.INT.fieldOf("peakMin").forGetter(YRange::peakMin),
                Codec.INT.fieldOf("peakMax").forGetter(YRange::peakMax)
        ).apply(i, YRange::new));
        public static final Structure<YRange> STRUCTURE = Structure.record(builder -> {
            var a = builder.add("minY", Structure.INT, YRange::minY);
            var b = builder.add("maxY", Structure.INT, YRange::maxY);
            var c = builder.add("peakMin", Structure.INT, YRange::peakMin);
            var d = builder.add("peakMax", Structure.INT, YRange::peakMax);
            return container -> new YRange(
                    a.apply(container), b.apply(container), c.apply(container), d.apply(container)
            );
        });
    }

    public record NameGen(boolean useColorPrefixes, boolean useBiomeBias, boolean useReplaceableBias,
                          int hashLen, List<String> banned) {
        public static final Codec<NameGen> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.BOOL.fieldOf("useColorPrefixes").forGetter(NameGen::useColorPrefixes),
                Codec.BOOL.fieldOf("useBiomeBias").forGetter(NameGen::useBiomeBias),
                Codec.BOOL.fieldOf("useReplaceableBias").forGetter(NameGen::useReplaceableBias),
                Codec.INT.fieldOf("hashLen").forGetter(NameGen::hashLen),
                Codec.STRING.listOf().fieldOf("banned").forGetter(NameGen::banned)
        ).apply(i, NameGen::new));
        public static final Structure<NameGen> STRUCTURE = Structure.record(builder -> {
            var a = builder.add("useColorPrefixes", Structure.BOOL, NameGen::useColorPrefixes);
            var b = builder.add("useBiomeBias", Structure.BOOL, NameGen::useBiomeBias);
            var c = builder.add("useReplaceableBias", Structure.BOOL, NameGen::useReplaceableBias);
            var d = builder.add("hashLen", Structure.INT, NameGen::hashLen);
            var e = builder.add("banned", Structure.STRING.listOf(), NameGen::banned);
            return container -> new NameGen(
                    a.apply(container), b.apply(container), c.apply(container), d.apply(container),
                    e.apply(container)
            );
        });

        public NameGen() {
            this(true, true, true, 4, List.of("obsidian", "pumice"));
        }
    }

    // ----- CODECs for maps -----
    private static final Codec<Map<MaterialKind, Integer>> KIND_WEIGHTS_CODEC =
            Codec.unboundedMap(MaterialKind.CODEC, Codec.INT).flatXmap(RAAConfig::validateWeights, RAAConfig::validateWeights);
    private static final Codec<Map<MaterialKind, ModeWeights>> MODE_MAP_CODEC =
            Codec.unboundedMap(MaterialKind.CODEC, ModeWeights.CODEC);
    private static final Codec<Map<MaterialKind, Float>> FLOAT_MUL_CODEC =
            Codec.unboundedMap(MaterialKind.CODEC, Codec.FLOAT);
    private static final Codec<Map<MaterialKind, DepthBand>> DEPTH_CODEC =
            Codec.unboundedMap(MaterialKind.CODEC, DepthBand.CODEC);
    private static final Codec<Map<MaterialKind, KindPolicy>> POLICY_MAP_CODEC =
            Codec.unboundedMap(MaterialKind.CODEC, KindPolicy.CODEC);

    private static final Structure<Map<MaterialKind, Integer>> KIND_WEIGHTS_STRUCTURE =
            Structure.unboundedMap(MaterialKind.STRUCTURE, Structure.INT).flatXmap(RAAConfig::validateWeights, RAAConfig::validateWeights);
    private static final Structure<Map<MaterialKind, ModeWeights>> MODE_MAP_STRUCTURE =
            Structure.unboundedMap(MaterialKind.STRUCTURE, ModeWeights.STRUCTURE);
    private static final Structure<Map<MaterialKind, Float>> FLOAT_MUL_STRUCTURE =
            Structure.unboundedMap(MaterialKind.STRUCTURE, Structure.FLOAT);
    private static final Structure<Map<MaterialKind, DepthBand>> DEPTH_STRUCTURE =
            Structure.unboundedMap(MaterialKind.STRUCTURE, DepthBand.STRUCTURE);
    private static final Structure<Map<MaterialKind, KindPolicy>> POLICY_MAP_STRUCTURE =
            Structure.unboundedMap(MaterialKind.STRUCTURE, KindPolicy.STRUCTURE);

    public record BlockStats(Map<MaterialKind, Float> hardnessMul, Map<MaterialKind, Float> blastMul, Map<MaterialKind, Float> effMul) {
        public static final Codec<BlockStats> CODEC = RecordCodecBuilder.create(i -> i.group(
                FLOAT_MUL_CODEC.fieldOf("hardnessMul").forGetter(BlockStats::hardnessMul),
                FLOAT_MUL_CODEC.fieldOf("blastMul").forGetter(BlockStats::blastMul),
                FLOAT_MUL_CODEC.fieldOf("effMul").forGetter(BlockStats::effMul)
        ).apply(i, BlockStats::new));
        public static final Structure<BlockStats> STRUCTURE = Structure.record(builder -> {
            var a = builder.add("hardnessMul", FLOAT_MUL_STRUCTURE, BlockStats::hardnessMul);
            var b = builder.add("blastMul", FLOAT_MUL_STRUCTURE, BlockStats::blastMul);
            var c = builder.add("effMul", FLOAT_MUL_STRUCTURE, BlockStats::effMul);
            return container -> new BlockStats(
                    a.apply(container), b.apply(container), c.apply(container)
            );
        });
    }

    // ----- Main config schema -----
    public static final Codec<RAAConfig> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("materialsMin").forGetter(c -> c.materialsMin),
            Codec.INT.fieldOf("materialsMax").forGetter(c -> c.materialsMax),
            KIND_WEIGHTS_CODEC.fieldOf("kind_weights").forGetter(c -> c.kindWeights),

            TierWeights.CODEC.fieldOf("tiers").forGetter(c -> c.tiers),
            MODE_MAP_CODEC.fieldOf("spawnMode").forGetter(c -> c.spawnMode),

            BlockStats.CODEC.fieldOf("blockStats").forGetter(RAAConfig::blockStats),

            DEPTH_CODEC.fieldOf("depthWeights").forGetter(c -> c.depthWeights),
            Codec.unboundedMap(Codec.STRING, YRange.CODEC).fieldOf("shallowRange").forGetter(c -> c.shallowRange),
            Codec.unboundedMap(Codec.STRING, YRange.CODEC).fieldOf("midRange").forGetter(c -> c.midRange),
            Codec.unboundedMap(Codec.STRING, YRange.CODEC).fieldOf("deepRange").forGetter(c -> c.deepRange),

            Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()).fieldOf("replaceables").forGetter(c -> c.replaceables),
            NameGen.CODEC.fieldOf("nameGen").forGetter(c -> c.nameGen),

            POLICY_MAP_CODEC.fieldOf("formControls").forGetter(c -> c.formControls),

            Codec.INT.fieldOf("toolChancePercent").forGetter(c -> c.toolChancePercent),
            MaterialGenerator.Profile.CODEC.fieldOf("defaultProfile").forGetter(RAAConfig::defaultProfile)
    ).apply(i, RAAConfig::new));

    public static final Structure<RAAConfig> STRUCTURE = Structure.record(builder -> {
        var materialsMin = builder.add("materialsMin", Structure.INT.annotate(Annotation.DESCRIPTION, "Describes the field!").annotate(Annotation.TITLE, "Field A"), RAAConfig::materialsMin);
        var materialsMax = builder.add("materialsMax", Structure.INT.annotate(Annotation.DESCRIPTION, "Describes the field!").annotate(Annotation.TITLE, "Field A"), RAAConfig::materialsMax);
        var kindWeights = builder.add("kindWeights", KIND_WEIGHTS_STRUCTURE.annotate(Annotation.DESCRIPTION, "This is a test"), RAAConfig::kindWeights);
        var tiers = builder.add("tiers", TierWeights.STRUCTURE, RAAConfig::tiers);
        var spawnMode = builder.add("spawnMode", MODE_MAP_STRUCTURE, RAAConfig::spawnMode);
        var blockStats = builder.add("blockStats", BlockStats.STRUCTURE, RAAConfig::blockStats);
        var depthWeights = builder.add("depthWeights", DEPTH_STRUCTURE, RAAConfig::depthWeights);
        var shallowRange = builder.add("shallowRange", Structure.unboundedMap(Structure.STRING, YRange.STRUCTURE), RAAConfig::shallowRange);
        var midRange = builder.add("midRange", Structure.unboundedMap(Structure.STRING, YRange.STRUCTURE), RAAConfig::midRange);
        var deepRange = builder.add("deepRange", Structure.unboundedMap(Structure.STRING, YRange.STRUCTURE), RAAConfig::deepRange);
        var replacables = builder.add("replacables", Structure.unboundedMap(Structure.STRING, Structure.STRING.listOf()), RAAConfig::replaceables);
        var nameGen = builder.add("nameGen", NameGen.STRUCTURE, RAAConfig::nameGen);
        var formControl = builder.add("formControls", POLICY_MAP_STRUCTURE, RAAConfig::formControls);
        var toolChancePercent = builder.add("toolChancePercent", Structure.INT, RAAConfig::toolChancePercent);
        var defaultProfile = builder.add("defaultProfile", Structure.INT, RAAConfig::toolChancePercent);
        return container -> new RAAConfig(
                materialsMin.apply(container), materialsMax.apply(container), kindWeights.apply(container),
                tiers.apply(container), spawnMode.apply(container), blockStats.apply(container),
                depthWeights.apply(container), shallowRange.apply(container), midRange.apply(container),
                deepRange.apply(container), replacables.apply(container), nameGen.apply(container),
                formControl.apply(container), toolChancePercent.apply(container)
        );
    });

    public static final ConfigType<RAAConfig> CONFIG = new ConfigType<>() {
        @Override
        public Codec<RAAConfig> codec() {
            return RAAConfig.CODEC;
        }

        @Override
        public RAAConfig defaultConfig() {
            return IdentityInterpreter.INSTANCE.interpret(RAAConfig.STRUCTURE).getOrThrow();
        }
    };

    // ----- Defaults (ported from your old config; volcanic rarer) -----
    public static RAAConfig defaults() {
        return new RAAConfig(
                8, 13,
                Map.ofEntries(
                        entry(METAL, 25), entry(GEM, 18), entry(MaterialKind.CRYSTAL, 12),
                        entry(MaterialKind.ALLOY, 8), entry(MaterialKind.STONE, 6), entry(MaterialKind.SAND, 4),
                        entry(MaterialKind.GRAVEL, 3), entry(MaterialKind.CLAY, 3), entry(MaterialKind.MUD, 3),
                        entry(MaterialKind.SALT, 2), entry(MaterialKind.VOLCANIC, 1), entry(MaterialKind.SOIL, 2)
                ),
                new TierWeights(),
                Map.of(
                        GEM, new ModeWeights(50, 50, 0),
                        MaterialKind.CRYSTAL, new ModeWeights(0, 50, 50),
                        MaterialKind.VOLCANIC, new ModeWeights(30, 0, 70)
                ),
                new BlockStats(Map.of(), Map.of(), Map.of()),
                Map.of(
                        METAL, new DepthBand(20, 40, 40),
                        GEM, new DepthBand(35, 45, 20)
                ),
                Map.of("DEFAULT", new YRange(16, 128, 48, 88)),
                Map.of("DEFAULT", new YRange(-16, 64, 8, 32)),
                Map.of("DEFAULT", new YRange(-59, 16, -32, -12)),
                Map.of(
                        "DEEPSLATE", List.of("#minecraft:deepslate_ore_replaceables"),
                        "STONE", List.of("#minecraft:stone_ore_replaceables")
                ),
                new NameGen(),
                defaultPolicies(),
                100,
                MaterialGenerator.Profile.FANTASY_HEAVY
        );
    }

    private static Map<MaterialKind, KindPolicy> defaultPolicies() {
        var M = MaterialKind.class;
        var map = new EnumMap<MaterialKind, KindPolicy>(M);
        map.put(METAL, policy().plus(FormGroup.TOOLS, FormGroup.ORE_CHAIN, FormGroup.METAL_DECOR, FormGroup.STONE_DECOR).build());
        map.put(ALLOY, policy().plus(FormGroup.TOOLS, FormGroup.METAL_DECOR, FormGroup.STONE_DECOR).build());
        map.put(GEM, policy().plus(FormGroup.TOOLS, FormGroup.ORE_CHAIN).build());
        map.put(CRYSTAL, policy().plus(FormGroup.CRYSTAL_SET, FormGroup.CRYSTAL_LAMPS).build());
        map.put(STONE, policy().plus(FormGroup.STONE_DECOR).build());
        map.put(SAND, policy().plus(FormGroup.SAND_SET, FormGroup.STONE_DECOR).build());
        map.put(GRAVEL, policy().plus(FormGroup.GRAVEL_SET, FormGroup.STONE_DECOR).build());
        map.put(CLAY, policy().plus(FormGroup.CLAY_SET).build());
        map.put(MUD, policy().plus(FormGroup.MUD_SET).build());
        map.put(SOIL, policy().plus(FormGroup.SOIL_SET).build());
        map.put(SALT, policy().plus(FormGroup.SALT_SET).build());
        map.put(VOLCANIC, policy().plus(FormGroup.VOLCANIC_SET, FormGroup.STONE_DECOR).build());
        map.put(OTHER, policy().build());
        return Collections.unmodifiableMap(map);
    }

    private static PolicyBuilder policy() {
        return new PolicyBuilder();
    }

    private static final class PolicyBuilder {
        final List<FormGroup> plus = new ArrayList<>();
        final List<FormGroup> minus = new ArrayList<>();
        final List<Form> add = new ArrayList<>();
        final List<Form> rem = new ArrayList<>();
        int max = -1;

        PolicyBuilder plus(FormGroup... g) {
            plus.addAll(List.of(g));
            return this;
        }

        PolicyBuilder minus(FormGroup... g) {
            minus.addAll(List.of(g));
            return this;
        }

        PolicyBuilder add(Form... f) {
            add.addAll(List.of(f));
            return this;
        }

        PolicyBuilder remove(Form... f) {
            rem.addAll(List.of(f));
            return this;
        }

        PolicyBuilder maxShapes(int m) {
            max = m;
            return this;
        }

        KindPolicy build() {
            return new KindPolicy(plus, minus, add, rem, max);
        }
    }

    // ----- Validation -----
    private static DataResult<Map<MaterialKind, Integer>> validateWeights(Map<MaterialKind, Integer> m) {
        int total = 0;
        for (var e : m.entrySet()) {
            if (e.getKey() == null) return DataResult.error(() -> "Null MaterialKind key");
            int w = e.getValue() == null ? 0 : e.getValue();
            if (w < 0) return DataResult.error(() -> "Negative weight for " + e.getKey());
            total += w;
        }
        if (total <= 0) return DataResult.error(() -> "All kind_weights are zero");
        return DataResult.success(m);
    }

    // ----- State / IO -----
    private static final AtomicReference<RAAConfig> ACTIVE = new AtomicReference<>(defaults());
    private static Path CONFIG_PATH;
    private static long lastMtime = -1L;

    public static RAAConfig active() {
        return ACTIVE.get();
    }

    public RAAConfig(int materialsMin, int materialsMax,
                     Map<MaterialKind, Integer> kindWeights,
                     TierWeights tiers,
                     Map<MaterialKind, ModeWeights> spawnMode,
                     BlockStats blockStats,
                     Map<MaterialKind, DepthBand> depthWeights,
                     Map<String, YRange> shallowRange,
                     Map<String, YRange> midRange,
                     Map<String, YRange> deepRange,
                     Map<String, List<String>> replaceables,
                     NameGen nameGen,
                     Map<MaterialKind, KindPolicy> formControls,
                     int toolChancePercent,
                     MaterialGenerator.Profile defaultProfile) {

        this.materialsMin = materialsMin;
        this.materialsMax = materialsMax;
        this.tiers = tiers;
        this.kindWeights = wrapEnumMap(kindWeights);
        this.spawnMode = wrapEnumMap(spawnMode);
        this.blockStats = blockStats;
        this.depthWeights = wrapEnumMap(depthWeights);
        this.formControls = wrapEnumMap(formControls);
        this.shallowRange = shallowRange;
        this.midRange = midRange;
        this.deepRange = deepRange;
        this.replaceables = replaceables;
        this.nameGen = nameGen;
        this.toolChancePercent = toolChancePercent;
        this.defaultProfile = defaultProfile;
    }

    private static <K extends Enum<K>, V> Map<K, V> wrapEnumMap(Map<K, V> input) {
        EnumMap<K, V> em = new EnumMap<>((Class<K>) MaterialKind.class);
        if (input != null) em.putAll(input);
        return Collections.unmodifiableMap(em);
    }

    public static void init(Path gameDir) {
        CONFIG_PATH = gameDir.resolve("config/raa_materials.json");
        try {
            if (Files.notExists(CONFIG_PATH)) save(defaults());
        } catch (IOException ignored) {
        }
        load();
    }

    public static void pollReload() {
        try {
            long mt = Files.getLastModifiedTime(CONFIG_PATH).toMillis();
            if (mt != lastMtime) {
                lastMtime = mt;
                load();
                log("Reloaded " + CONFIG_PATH.getFileName());
            }
        } catch (Exception ignored) {
        }
    }

    public static void load() {
        try {
            var json = GsonHelper.parse(Files.newBufferedReader(CONFIG_PATH)); // lenient (allows comments)
            var res = CODEC.parse(JsonOps.INSTANCE, json);
            res.resultOrPartial(err -> log("[Config] " + err))
                    .ifPresentOrElse(ACTIVE::set, () -> log("[Config] Using defaults"));
        } catch (Exception e) {
            log("[Config] Failed to read: " + e.getMessage());
            ACTIVE.set(defaults());
        }
    }

    public static void save(RAAConfig cfg) throws IOException {
        var json = CODEC.encodeStart(JsonOps.INSTANCE, cfg)
                .resultOrPartial(err -> log("[Config] Encode error: " + err))
                .orElseThrow(() -> new IOException("encode failed"));
        Files.createDirectories(CONFIG_PATH.getParent());
        var gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
        Files.writeString(CONFIG_PATH, gson.toJson(json));
    }

    private static void log(String s) {
        RAAMaterials.LOGGER.info(s);
    }
}