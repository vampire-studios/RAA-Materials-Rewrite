// HarvestTier.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public enum HarvestTier implements StringRepresentable {
    STONE,
    IRON,
    DIAMOND,
    NETHERITE;

    public static final Codec<HarvestTier> CODEC = StringRepresentable.fromEnum(HarvestTier::values);
    private static final IntFunction<HarvestTier> BY_ID = ByIdMap.continuous(HarvestTier::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, HarvestTier> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, HarvestTier::ordinal);

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
