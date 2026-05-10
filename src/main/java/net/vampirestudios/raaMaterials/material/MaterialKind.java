package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public enum MaterialKind implements StringRepresentable {
	METAL,
	GEM,
	CRYSTAL,
	ALLOY,
	STONE,
	SOIL,
	SAND,
	GRAVEL,
	CLAY,
	MUD,
	SALT,
	VOLCANIC,
	OTHER,
	WOOD;

	public static final Codec<MaterialKind> CODEC = StringRepresentable.fromEnum(MaterialKind::values);
	private static final IntFunction<MaterialKind> BY_ID = ByIdMap.continuous(MaterialKind::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, MaterialKind> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, MaterialKind::ordinal);

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
