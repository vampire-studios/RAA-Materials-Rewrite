// SpawnMode.java
package net.vampirestudios.raaMaterials.worldgen;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public enum SpawnMode implements StringRepresentable {
    ORE_VEIN,
    GEODE,
    CLUSTER;

    public static final Codec<SpawnMode> CODEC = StringRepresentable.fromEnum(SpawnMode::values);
    private static final IntFunction<SpawnMode> BY_ID = ByIdMap.continuous(SpawnMode::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, SpawnMode> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, SpawnMode::ordinal);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
