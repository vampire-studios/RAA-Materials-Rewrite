package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SpawnParams(
        float rarity,        // 0 = common, 1 = extremely rare
        float depthBias,     // deeper = higher
        float clusterBias,   // larger / more clustered
        float exposureBias,  // more exposed to caves
        float magicBias      // more exotic behavior
) {
    private static final Codec<Float> CLAMP01 = Codec.floatRange(0.0f, 1.0f);

    public static final Codec<SpawnParams> CODEC = RecordCodecBuilder.create(i -> i.group(
            CLAMP01.fieldOf("rarity").forGetter(SpawnParams::rarity),
            CLAMP01.fieldOf("depth_bias").forGetter(SpawnParams::depthBias),
            CLAMP01.fieldOf("cluster_bias").forGetter(SpawnParams::clusterBias),
            CLAMP01.fieldOf("exposure_bias").forGetter(SpawnParams::exposureBias),
            CLAMP01.fieldOf("magic_bias").forGetter(SpawnParams::magicBias)
    ).apply(i, SpawnParams::new));

    public static final StreamCodec<ByteBuf, SpawnParams> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, SpawnParams::rarity,
            ByteBufCodecs.FLOAT, SpawnParams::depthBias,
            ByteBufCodecs.FLOAT, SpawnParams::clusterBias,
            ByteBufCodecs.FLOAT, SpawnParams::exposureBias,
            ByteBufCodecs.FLOAT, SpawnParams::magicBias,
            SpawnParams::new
    );
    public static SpawnParams common() {
        return new SpawnParams(0.2f, 0.3f, 0.5f, 0.2f, 0.0f);
    }
}