// ClusterSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * @param clusterSize  small number
 * @param clustersPerChunk  frequency */
public record ClusterSpec(List<ResourceLocation> biomeTags, List<Target> replaceables, YBand y, int clusterSize,
                          int clustersPerChunk,
                          // NEW
                          int spread,               // how far clusters scatter (blocks)
                          boolean attachToCave,     // if true, bias to air-adjacent blocks
                          float sphericity          // 0..1: 1 = round; 0 = lumpy
) implements SpawnSpec {
    public static final MapCodec<ClusterSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(ClusterSpec::biomeTags),
            Target.CODEC.listOf().fieldOf("targets").forGetter(ClusterSpec::replaceables),
            YBand.CODEC.fieldOf("y").forGetter(ClusterSpec::y),
            Codec.INT.fieldOf("cluster_size").forGetter(ClusterSpec::clusterSize),
            Codec.INT.fieldOf("clusters_per_chunk").forGetter(ClusterSpec::clustersPerChunk)
    ).apply(inst, ClusterSpec::new));
    public static final Codec<ClusterSpec> CODEC = MAP_CODEC.codec();

    public ClusterSpec(List<ResourceLocation> biomeTags, List<Target> replaceables, YBand y, int clusterSize, int clustersPerChunk) {
        this.biomeTags = List.copyOf(biomeTags);
        this.replaceables = List.copyOf(replaceables);
        this.y = y;
        this.clusterSize = clusterSize;
        this.clustersPerChunk = clustersPerChunk;
    }

    @Override
    public Mode mode() {
        return Mode.CLUSTER;
    }
}
