// SurfaceNodeSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import java.util.List;

public record SurfaceNodeSpec(
        List<ResourceLocation> biomeTags,
        List<Target> replaceables,
        YBand y,
        int nodesPerChunk,
        int nodeSize
) implements SpawnSpec {
    public static final MapCodec<SurfaceNodeSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(SurfaceNodeSpec::biomeTags),
        Target.CODEC.listOf().fieldOf("targets").forGetter(SurfaceNodeSpec::replaceables),
        YBand.CODEC.fieldOf("y").forGetter(SurfaceNodeSpec::y),
        Codec.INT.fieldOf("nodes_per_chunk").forGetter(SurfaceNodeSpec::nodesPerChunk),
        Codec.INT.fieldOf("node_size").forGetter(SurfaceNodeSpec::nodeSize)
    ).apply(inst, SurfaceNodeSpec::new));
    public static final Codec<SurfaceNodeSpec> CODEC = MAP_CODEC.codec();
    @Override public Mode mode() { return Mode.SURFACE_NODE; }
}
