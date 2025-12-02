// MeteoriteParams.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MeteoriteParams(
        Sampling sampling,
        Geometry geometry,
        Thermal thermal,
        Burial burial,
        Ancient ancient
) {
    public static final MapCodec<MeteoriteParams> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        Sampling.MAP_CODEC.fieldOf("sampling").forGetter(MeteoriteParams::sampling),
        Geometry.MAP_CODEC.fieldOf("geometry").forGetter(MeteoriteParams::geometry),
        Thermal.MAP_CODEC.fieldOf("thermal").forGetter(MeteoriteParams::thermal),
        Burial.MAP_CODEC.fieldOf("burial").forGetter(MeteoriteParams::burial),
        Ancient.MAP_CODEC.fieldOf("ancient").forGetter(MeteoriteParams::ancient)
    ).apply(i, MeteoriteParams::new));
    public static final Codec<MeteoriteParams> CODEC = MAP_CODEC.codec();

    /* --- sub-records --- */
    public record Sampling(int attemptsPerChunk, double chance) {
        public static final MapCodec<Sampling> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("attempts_per_chunk").orElse(1).forGetter(Sampling::attemptsPerChunk),
            Codec.DOUBLE.fieldOf("chance").orElse(0.12).forGetter(Sampling::chance)
        ).apply(i, Sampling::new));
    }
    public record Geometry(int minRadius, int maxRadius, double depthScale, double coreRatio, double ellipticity) {
        public static final MapCodec<Geometry> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("min_radius").orElse(5).forGetter(Geometry::minRadius),
            Codec.INT.fieldOf("max_radius").orElse(12).forGetter(Geometry::maxRadius),
            Codec.DOUBLE.fieldOf("depth_scale").orElse(0.8).forGetter(Geometry::depthScale),
            Codec.DOUBLE.fieldOf("core_ratio").orElse(0.4).forGetter(Geometry::coreRatio),
            Codec.DOUBLE.fieldOf("ellipticity").orElse(0.2).forGetter(Geometry::ellipticity)
        ).apply(i, Geometry::new));
    }
    public record Thermal(double glassChance, double slagChance, int strewnField) {
        public static final MapCodec<Thermal> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.DOUBLE.fieldOf("glass_chance").orElse(0.6).forGetter(Thermal::glassChance),
            Codec.DOUBLE.fieldOf("slag_chance").orElse(0.4).forGetter(Thermal::slagChance),
            Codec.INT.fieldOf("strewn_field").orElse(28).forGetter(Thermal::strewnField)
        ).apply(i, Thermal::new));
    }
    public record Burial(boolean surfaceOnly, int maxBuryDepth) {
        public static final MapCodec<Burial> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.BOOL.fieldOf("surface_only").orElse(true).forGetter(Burial::surfaceOnly),
            Codec.INT.fieldOf("max_bury_depth").orElse(16).forGetter(Burial::maxBuryDepth)
        ).apply(i, Burial::new));
    }
    public record Ancient(double chance, boolean backfill, int seamDepth, int seamThickness,
                          double hintScarChance, double hintGlassChance) {
        public static final MapCodec<Ancient> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.DOUBLE.fieldOf("chance").orElse(0.0).forGetter(Ancient::chance),
            Codec.BOOL.fieldOf("backfill").orElse(true).forGetter(Ancient::backfill),
            Codec.INT.fieldOf("seam_depth").orElse(4).forGetter(Ancient::seamDepth),
            Codec.INT.fieldOf("seam_thickness").orElse(2).forGetter(Ancient::seamThickness),
            Codec.DOUBLE.fieldOf("hint_scar_chance").orElse(0.12).forGetter(Ancient::hintScarChance),
            Codec.DOUBLE.fieldOf("hint_glass_chance").orElse(0.08).forGetter(Ancient::hintGlassChance)
        ).apply(i, Ancient::new));
    }
}
