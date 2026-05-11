package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public enum SpikeGrowthLiquid implements StringRepresentable {
    NONE,
    WATER,
    LAVA;

    public static final Codec<SpikeGrowthLiquid> CODEC = StringRepresentable.fromEnum(SpikeGrowthLiquid::values);
    private static final IntFunction<SpikeGrowthLiquid> BY_ID = ByIdMap.continuous(SpikeGrowthLiquid::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, SpikeGrowthLiquid> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, SpikeGrowthLiquid::ordinal);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
