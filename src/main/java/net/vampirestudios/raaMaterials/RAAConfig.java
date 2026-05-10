package net.vampirestudios.raaMaterials;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.GsonHelper;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialGenerator;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.material.SpawnInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Map.entry;
import static net.vampirestudios.raaMaterials.material.MaterialKind.*;

public record RAAConfig(int materialsMin, int materialsMax, Map<MaterialKind, Integer> kindWeights,
                        TierWeights tiers,
                        Map<MaterialKind, ModeWeights> spawnMode, BlockStats blockStats,
                        Map<MaterialKind, DepthBand> depthWeights,
                        DepthYRanges depthRanges,
                        Map<String, List<String>> replaceables,
                        NameGen nameGen,
                        Map<MaterialKind, KindPolicy> formControls, int toolChancePercent,
                        Map<MaterialKind, SpawnProfile> spawnProfiles,
                        MaterialGenerator.Profile defaultProfile,
                        Map<MaterialKind, ColorRanges> colorRanges) {
    // ----- Records / Beans -----
    public record TierWeights(int stone, int iron, int diamond, int netherite) {
        public static final Codec<TierWeights> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("stone").forGetter(TierWeights::stone),
                Codec.INT.fieldOf("iron").forGetter(TierWeights::iron),
                Codec.INT.fieldOf("diamond").forGetter(TierWeights::diamond),
                Codec.INT.fieldOf("netherite").forGetter(TierWeights::netherite)
        ).apply(i, TierWeights::new));

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
    }

    public record DepthYRanges(Map<String, YRange> shallow, Map<String, YRange> mid, Map<String, YRange> deep) {
        private static final Codec<Map<String, YRange>> MAP = Codec.unboundedMap(Codec.STRING, YRange.CODEC);
        public static final Codec<DepthYRanges> CODEC = RecordCodecBuilder.create(i -> i.group(
                MAP.fieldOf("shallowRange").forGetter(DepthYRanges::shallow),
                MAP.fieldOf("midRange").forGetter(DepthYRanges::mid),
                MAP.fieldOf("deepRange").forGetter(DepthYRanges::deep)
        ).apply(i, DepthYRanges::new));
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

        public NameGen() {
            this(true, true, true, 4, List.of("obsidian", "pumice"));
        }
    }

    public record IntRange(int min, int max) {
        public static final Codec<IntRange> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("min").forGetter(IntRange::min),
                Codec.INT.fieldOf("max").forGetter(IntRange::max)
        ).apply(i, IntRange::new));

        public int pick(Random rng) {
            int low = Math.min(min, max);
            int high = Math.max(min, max);
            return rng.nextInt(low, high + 1);
        }
    }

    public record SpawnProfile(
            IntRange attempts,
            float successChance,
            int veinMin, int veinMax,
            SpawnInfo.VeinShape shape,
            int minY, int maxY,
            IntRange center,
            IntRange spread,
            SpawnInfo.NoiseGate regionGate,
            SpawnInfo.NoiseGate pocketGate,
            float mustTouchAirChance,
            float nearWaterChance,
            float nearLavaChance
    ) {
        private static final Codec<Float> CHANCE_CODEC = Codec.floatRange(0.0f, 1.0f);

        public static final Codec<SpawnProfile> CODEC = RecordCodecBuilder.create(i -> i.group(
                IntRange.CODEC.fieldOf("attempts").forGetter(SpawnProfile::attempts),
                CHANCE_CODEC.fieldOf("successChance").forGetter(SpawnProfile::successChance),
                Codec.intRange(0, 256).fieldOf("veinMin").forGetter(SpawnProfile::veinMin),
                Codec.intRange(0, 256).fieldOf("veinMax").forGetter(SpawnProfile::veinMax),
                SpawnInfo.VeinShape.CODEC.fieldOf("shape").forGetter(SpawnProfile::shape),
                Codec.INT.fieldOf("minY").forGetter(SpawnProfile::minY),
                Codec.INT.fieldOf("maxY").forGetter(SpawnProfile::maxY),
                IntRange.CODEC.fieldOf("center").forGetter(SpawnProfile::center),
                IntRange.CODEC.fieldOf("spread").forGetter(SpawnProfile::spread),
                SpawnInfo.NoiseGate.CODEC.fieldOf("regionGate").forGetter(SpawnProfile::regionGate),
                SpawnInfo.NoiseGate.CODEC.fieldOf("pocketGate").forGetter(SpawnProfile::pocketGate),
                CHANCE_CODEC.fieldOf("mustTouchAirChance").forGetter(SpawnProfile::mustTouchAirChance),
                CHANCE_CODEC.fieldOf("nearWaterChance").forGetter(SpawnProfile::nearWaterChance),
                CHANCE_CODEC.fieldOf("nearLavaChance").forGetter(SpawnProfile::nearLavaChance)
        ).apply(i, SpawnProfile::new));

        public SpawnInfo pick(Random rng) {
            return new SpawnInfo(
                    attempts.pick(rng),
                    successChance,
                    Math.min(veinMin, veinMax),
                    Math.max(veinMin, veinMax),
                    shape,
                    new SpawnInfo.YDistribution(
                            Math.min(minY, maxY),
                            Math.max(minY, maxY),
                            center.pick(rng),
                            spread.pick(rng)
                    ),
                    regionGate,
                    pocketGate,
                    rng.nextFloat() < mustTouchAirChance,
                    rng.nextFloat() < nearWaterChance,
                    rng.nextFloat() < nearLavaChance
            );
        }

    }

    // Convenience accessors so call sites don't need to know about DepthYRanges.
    public Map<String, YRange> shallowRange() { return depthRanges.shallow(); }
    public Map<String, YRange> midRange()     { return depthRanges.mid(); }
    public Map<String, YRange> deepRange()    { return depthRanges.deep(); }

    // ----- CODECs for maps -----
    private static final Codec<Map<MaterialKind, ColorRanges>> COLOR_RANGES_CODEC =
            Codec.unboundedMap(MaterialKind.CODEC, ColorRanges.CODEC);
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
    private static final Codec<Map<MaterialKind, SpawnProfile>> SPAWN_PROFILE_MAP_CODEC =
            Codec.unboundedMap(MaterialKind.CODEC, SpawnProfile.CODEC);

    /**
     * OKLCh lightness and chroma bounds for a material kind's color generation.
     * {@code lMin/lMax} control brightness (0=black, 1=white).
     * {@code cMin/cMax} control saturation (0=grey, ~0.3=vivid).
     * Hue selection remains kind-specific in code; these ranges scale on top.
     */
    public record ColorRanges(float lMin, float lMax, float cMin, float cMax) {
        private static final Codec<Float> FRAC = Codec.floatRange(0f, 1f);
        public static final Codec<ColorRanges> CODEC = RecordCodecBuilder.create(i -> i.group(
                FRAC.fieldOf("lMin").forGetter(ColorRanges::lMin),
                FRAC.fieldOf("lMax").forGetter(ColorRanges::lMax),
                FRAC.fieldOf("cMin").forGetter(ColorRanges::cMin),
                FRAC.fieldOf("cMax").forGetter(ColorRanges::cMax)
        ).apply(i, ColorRanges::new));
    }

    public record BlockStats(Map<MaterialKind, Float> hardnessMul, Map<MaterialKind, Float> blastMul, Map<MaterialKind, Float> effMul) {
        public static final Codec<BlockStats> CODEC = RecordCodecBuilder.create(i -> i.group(
                FLOAT_MUL_CODEC.fieldOf("hardnessMul").forGetter(BlockStats::hardnessMul),
                FLOAT_MUL_CODEC.fieldOf("blastMul").forGetter(BlockStats::blastMul),
                FLOAT_MUL_CODEC.fieldOf("effMul").forGetter(BlockStats::effMul)
        ).apply(i, BlockStats::new));
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
            DepthYRanges.CODEC.fieldOf("depthRanges").forGetter(RAAConfig::depthRanges),

            Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()).fieldOf("replaceables").forGetter(c -> c.replaceables),
            NameGen.CODEC.fieldOf("nameGen").forGetter(c -> c.nameGen),

            POLICY_MAP_CODEC.fieldOf("formControls").forGetter(c -> c.formControls),

            Codec.INT.fieldOf("toolChancePercent").forGetter(c -> c.toolChancePercent),
            SPAWN_PROFILE_MAP_CODEC.optionalFieldOf("spawnProfiles", defaultSpawnProfiles()).forGetter(c -> c.spawnProfiles),
            MaterialGenerator.Profile.CODEC.fieldOf("defaultProfile").forGetter(RAAConfig::defaultProfile),
            COLOR_RANGES_CODEC.optionalFieldOf("colorRanges", defaultColorRanges()).forGetter(RAAConfig::colorRanges)
    ).apply(i, RAAConfig::new));

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
                new DepthYRanges(
                        Map.of("DEFAULT", new YRange(16, 128, 48, 88)),
                        Map.of("DEFAULT", new YRange(-16, 64, 8, 32)),
                        Map.of("DEFAULT", new YRange(-59, 16, -32, -12))
                ),
                Map.of(
                        "DEEPSLATE", List.of("#minecraft:deepslate_ore_replaceables"),
                        "STONE", List.of("#minecraft:stone_ore_replaceables")
                ),
                new NameGen(),
                defaultPolicies(),
                100,
                defaultSpawnProfiles(),
                MaterialGenerator.Profile.FANTASY_HEAVY,
                defaultColorRanges()
        );
    }

    public static Map<MaterialKind, ColorRanges> defaultColorRanges() {
        var map = new EnumMap<MaterialKind, ColorRanges>(MaterialKind.class);
        //                               lMin  lMax  cMin  cMax
        map.put(METAL,    new ColorRanges(0.40f, 0.72f, 0.03f, 0.14f));
        map.put(ALLOY,    new ColorRanges(0.40f, 0.72f, 0.03f, 0.14f));
        map.put(GEM,      new ColorRanges(0.45f, 0.75f, 0.16f, 0.32f));
        map.put(MaterialKind.CRYSTAL,  new ColorRanges(0.60f, 0.85f, 0.08f, 0.22f));
        map.put(MaterialKind.STONE,    new ColorRanges(0.30f, 0.55f, 0.01f, 0.07f));
        map.put(MaterialKind.SAND,     new ColorRanges(0.62f, 0.82f, 0.08f, 0.18f));
        map.put(MaterialKind.GRAVEL,   new ColorRanges(0.35f, 0.55f, 0.00f, 0.04f));
        map.put(MaterialKind.CLAY,     new ColorRanges(0.25f, 0.50f, 0.04f, 0.12f));
        map.put(MaterialKind.MUD,      new ColorRanges(0.25f, 0.50f, 0.04f, 0.12f));
        map.put(MaterialKind.SOIL,     new ColorRanges(0.25f, 0.50f, 0.04f, 0.12f));
        map.put(MaterialKind.SALT,     new ColorRanges(0.78f, 0.95f, 0.00f, 0.07f));
        map.put(MaterialKind.VOLCANIC, new ColorRanges(0.15f, 0.45f, 0.06f, 0.22f));
        map.put(MaterialKind.WOOD,     new ColorRanges(0.35f, 0.60f, 0.06f, 0.14f));
        map.put(MaterialKind.OTHER,    new ColorRanges(0.45f, 0.80f, 0.12f, 0.30f));
        return Collections.unmodifiableMap(map);
    }

    public static Map<MaterialKind, SpawnProfile> defaultSpawnProfiles() {
        var map = new EnumMap<MaterialKind, SpawnProfile>(MaterialKind.class);
        var oreBlobRegion = new SpawnInfo.NoiseGate(0.002f, 0.0f);
        var oreBlobPocket = new SpawnInfo.NoiseGate(0.02f, 0.2f);
        var diskRegion = new SpawnInfo.NoiseGate(0.003f, -0.2f);
        var diskPocket = new SpawnInfo.NoiseGate(0.025f, 0.0f);

        map.put(METAL, profile(8, 17, 0.75f, 8, 24, SpawnInfo.VeinShape.ORE_BLOB, -64, 96, -20, 29, 18, 31, oreBlobRegion, oreBlobPocket, 0.0f, 0.0f, 0.0f));
        map.put(ALLOY, profile(8, 17, 0.75f, 8, 24, SpawnInfo.VeinShape.ORE_BLOB, -64, 96, -20, 29, 18, 31, oreBlobRegion, oreBlobPocket, 0.0f, 0.0f, 0.0f));
        map.put(GEM, profile(1, 3, 0.5f, 2, 6, SpawnInfo.VeinShape.ORE_STRING, -64, 32, -55, -21, 6, 13, new SpawnInfo.NoiseGate(0.0015f, 0.4f), new SpawnInfo.NoiseGate(0.03f, 0.5f), 0.4f, 0.0f, 0.25f));
        map.put(CRYSTAL, profile(4, 9, 0.8f, 3, 14, SpawnInfo.VeinShape.CRYSTAL_CLUSTER, -32, 160, 10, 49, 20, 44, new SpawnInfo.NoiseGate(0.002f, 0.15f), new SpawnInfo.NoiseGate(0.04f, 0.35f), 1.0f, 0.5f, 0.0f));
        map.put(GRAVEL, profile(1, 3, 0.75f, 7, 15, SpawnInfo.VeinShape.ORE_DISK, 45, 160, 58, 89, 16, 31, diskRegion, diskPocket, 0.0f, 0.5f, 0.0f));
        map.put(MUD, profile(1, 2, 0.75f, 6, 13, SpawnInfo.VeinShape.ORE_DISK, 45, 120, 58, 77, 12, 23, new SpawnInfo.NoiseGate(0.003f, -0.1f), new SpawnInfo.NoiseGate(0.025f, 0.05f), 0.0f, 1.0f, 0.0f));
        map.put(CLAY, profile(1, 2, 0.75f, 6, 12, SpawnInfo.VeinShape.ORE_DISK, 45, 120, 56, 75, 12, 23, new SpawnInfo.NoiseGate(0.003f, -0.1f), new SpawnInfo.NoiseGate(0.025f, 0.05f), 0.0f, 0.6f, 0.0f));
        map.put(SAND, profile(1, 3, 0.75f, 7, 16, SpawnInfo.VeinShape.ORE_DISK, 45, 160, 58, 89, 16, 31, diskRegion, diskPocket, 0.0f, 0.0f, 0.0f));
        map.put(SOIL, profile(1, 2, 0.75f, 6, 13, SpawnInfo.VeinShape.ORE_DISK, 45, 160, 60, 89, 16, 31, diskRegion, diskPocket, 0.0f, 0.0f, 0.0f));
        map.put(SALT, profile(2, 4, 0.7f, 6, 14, SpawnInfo.VeinShape.ORE_DISK, -32, 160, 40, 71, 24, 47, new SpawnInfo.NoiseGate(0.002f, 0.1f), new SpawnInfo.NoiseGate(0.025f, 0.15f), 0.0f, 0.0f, 0.0f));
        map.put(VOLCANIC, profile(2, 3, 0.75f, 7, 15, SpawnInfo.VeinShape.ORE_DISK, -64, 96, -20, 39, 24, 47, new SpawnInfo.NoiseGate(0.002f, 0.0f), new SpawnInfo.NoiseGate(0.025f, 0.1f), 0.0f, 0.0f, 0.0f));
        map.put(STONE, profile(2, 4, 0.85f, 8, 17, SpawnInfo.VeinShape.ORE_DISK, -64, 128, -20, 54, 28, 55, new SpawnInfo.NoiseGate(0.002f, -0.1f), new SpawnInfo.NoiseGate(0.02f, 0.1f), 0.0f, 0.0f, 0.0f));
        map.put(OTHER, profile(6, 13, 0.85f, 8, 20, SpawnInfo.VeinShape.ORE_BLOB, -64, 96, -10, 39, 18, 39, new SpawnInfo.NoiseGate(0.002f, -0.1f), new SpawnInfo.NoiseGate(0.02f, 0.1f), 0.0f, 0.0f, 0.0f));
        map.put(WOOD, map.get(OTHER));
        return Collections.unmodifiableMap(map);
    }

    private static SpawnProfile profile(int attemptsMin, int attemptsMax, float successChance, int veinMin, int veinMax,
                                        SpawnInfo.VeinShape shape, int minY, int maxY, int centerMin, int centerMax,
                                        int spreadMin, int spreadMax, SpawnInfo.NoiseGate regionGate,
                                        SpawnInfo.NoiseGate pocketGate, float mustTouchAirChance,
                                        float nearWaterChance, float nearLavaChance) {
        return new SpawnProfile(
                new IntRange(attemptsMin, attemptsMax),
                successChance,
                veinMin,
                veinMax,
                shape,
                minY,
                maxY,
                new IntRange(centerMin, centerMax),
                new IntRange(spreadMin, spreadMax),
                regionGate,
                pocketGate,
                mustTouchAirChance,
                nearWaterChance,
                nearLavaChance
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
                     DepthYRanges depthRanges,
                     Map<String, List<String>> replaceables,
                     NameGen nameGen,
                     Map<MaterialKind, KindPolicy> formControls,
                     int toolChancePercent,
                     Map<MaterialKind, SpawnProfile> spawnProfiles,
                     MaterialGenerator.Profile defaultProfile,
                     Map<MaterialKind, ColorRanges> colorRanges) {

        this.materialsMin = materialsMin;
        this.materialsMax = materialsMax;
        this.kindWeights = wrapEnumMap(kindWeights);
        this.tiers = tiers;
        this.spawnMode = wrapEnumMap(spawnMode);
        this.blockStats = blockStats;
        this.depthWeights = wrapEnumMap(depthWeights);
        this.depthRanges = depthRanges;
        this.replaceables = replaceables;
        this.nameGen = nameGen;
        this.formControls = wrapEnumMap(formControls);
        this.toolChancePercent = toolChancePercent;
        this.spawnProfiles = wrapEnumMap(spawnProfiles);
        this.defaultProfile = defaultProfile;
        this.colorRanges = wrapEnumMap(colorRanges);
    }

    @SuppressWarnings("unchecked")
    private static <K extends Enum<K>, V> Map<K, V> wrapEnumMap(Map<K, V> input) {
        if (input == null || input.isEmpty()) return Collections.emptyMap();
        Class<K> enumClass = (Class<K>) input.keySet().iterator().next().getClass();
        EnumMap<K, V> em = new EnumMap<>(enumClass);
        em.putAll(input);
        return Collections.unmodifiableMap(em);
    }

    public static void init(Path gameDir) {
        CONFIG_PATH = gameDir.resolve("config/raa_materials/config.json");
        FormGroupConfig.init(gameDir);
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

    public static void replaceAndSave(RAAConfig cfg) {
        ACTIVE.set(cfg);
        try {
            save(cfg);
        } catch (IOException e) {
            log("[Config] Failed to save: " + e.getMessage());
        }
    }

    private static void log(String s) {
        RAAMaterials.LOGGER.info(s);
    }
}
