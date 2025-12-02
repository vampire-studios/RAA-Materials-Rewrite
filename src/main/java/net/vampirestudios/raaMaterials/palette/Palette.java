// src/main/java/your/mod/palette/Palette.java
package net.vampirestudios.raaMaterials.palette;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public record Palette(String id,
                      List<Stop> stops,   // sorted by pos
                      Mode mode,          // mapping mode
                      Dither dither,
                      Quantize quantize) {

    public static final Codec<Palette> CODEC = RecordCodecBuilder.create(i -> i.group(
        Codec.STRING.fieldOf("id").forGetter(Palette::id),
        Stop.CODEC.listOf().fieldOf("stops").forGetter(Palette::stops),
        Mode.CODEC.fieldOf("mode").orElse(Mode.BY_LUMINANCE).forGetter(Palette::mode),
        Dither.CODEC.fieldOf("dither").orElse(Dither.NONE).forGetter(Palette::dither),
        Quantize.CODEC.fieldOf("quantize").orElse(Quantize.NONE).forGetter(Palette::quantize)
    ).apply(i, Palette::new));

    public enum Mode { BY_LUMINANCE, BY_HUE_REMAP, BY_INDEX;
        public static final Codec<Mode> CODEC = Codec.STRING.xmap(Mode::valueOf, Enum::name);
    }
    public enum Dither { NONE, ORDERED_4x4, BLUE_NOISE;
        public static final Codec<Dither> CODEC = Codec.STRING.xmap(Dither::valueOf, Enum::name);
    }
    public enum Quantize { NONE, UNIQUE, BINS;
        public static final com.mojang.serialization.Codec<Quantize> CODEC =
                com.mojang.serialization.Codec.STRING.xmap(Quantize::valueOf, Enum::name);
    }

    public record Stop(float pos, int argb) {
        public static final Codec<Stop> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("pos").forGetter(Stop::pos),
            Codec.INT.fieldOf("argb").forGetter(Stop::argb)
        ).apply(i, Stop::new));
    }
}
