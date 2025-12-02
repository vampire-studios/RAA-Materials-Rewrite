// MeteoriteSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/** Surface-biased crater + core + ejecta + fragments. */
public record MeteoriteSpec(
        List<ResourceLocation> biomeTags,
        List<Target> replaceables,
        YBand y,
        MeteoriteParams params,
        MeteoriteBlocks blocks
) implements SpawnSpec {

    public static final MapCodec<MeteoriteSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(MeteoriteSpec::biomeTags),
            Target.CODEC.listOf().fieldOf("targets").forGetter(MeteoriteSpec::replaceables),
            YBand.CODEC.fieldOf("y").forGetter(MeteoriteSpec::y),
            MeteoriteParams.MAP_CODEC.fieldOf("params").forGetter(MeteoriteSpec::params),
            MeteoriteBlocks.MAP_CODEC.fieldOf("blocks").forGetter(MeteoriteSpec::blocks)
    ).apply(i, MeteoriteSpec::new));

    public static final Codec<MeteoriteSpec> CODEC = MAP_CODEC.codec();

    public MeteoriteSpec(List<ResourceLocation> biomeTags, List<Target> replaceables, YBand y,
                         MeteoriteParams params, MeteoriteBlocks blocks) {
        this.biomeTags = List.copyOf(biomeTags);
        this.replaceables = List.copyOf(replaceables);
        this.y = y;
        this.params = params;
        this.blocks = blocks;
    }

    @Override public Mode mode() { return Mode.METEORITE; }
}
