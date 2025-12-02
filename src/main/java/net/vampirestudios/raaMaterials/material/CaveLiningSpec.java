// CaveLiningSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record CaveLiningSpec(
        List<ResourceLocation> biomeTags,
        List<Target> replaceables,
        YBand y,
        int densityPerChunk      // tries per chunk across cave surfaces
) implements SpawnSpec {
    public static final MapCodec<CaveLiningSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(CaveLiningSpec::biomeTags),
        Target.CODEC.listOf().fieldOf("targets").forGetter(CaveLiningSpec::replaceables),
        YBand.CODEC.fieldOf("y").forGetter(CaveLiningSpec::y),
        Codec.INT.fieldOf("density_per_chunk").forGetter(CaveLiningSpec::densityPerChunk)
    ).apply(inst, CaveLiningSpec::new));
    public static final Codec<CaveLiningSpec> CODEC = MAP_CODEC.codec();
    @Override public Mode mode() { return Mode.CAVE_LINING; }
}
