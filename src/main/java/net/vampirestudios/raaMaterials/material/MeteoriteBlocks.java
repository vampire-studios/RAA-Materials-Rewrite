// MeteoriteBlocks.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record MeteoriteBlocks(
        ResourceLocation coreBlock,
        Optional<ResourceLocation> coreHaloBlock, // empty = no halo
        ResourceLocation slagBlock,
        ResourceLocation glassBlock,
        ResourceLocation brecciaBlock // used by “ancient” seam; can be slag or tuff
) {
    public static final MapCodec<MeteoriteBlocks> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        ResourceLocation.CODEC.fieldOf("core").forGetter(MeteoriteBlocks::coreBlock),
        ResourceLocation.CODEC.optionalFieldOf("core_halo").forGetter(MeteoriteBlocks::coreHaloBlock),
        ResourceLocation.CODEC.fieldOf("slag").forGetter(MeteoriteBlocks::slagBlock),
        ResourceLocation.CODEC.fieldOf("glass").forGetter(MeteoriteBlocks::glassBlock),
        ResourceLocation.CODEC.fieldOf("breccia").forGetter(MeteoriteBlocks::brecciaBlock)
    ).apply(i, MeteoriteBlocks::new));
    public static final Codec<MeteoriteBlocks> CODEC = MAP_CODEC.codec();
}
