package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record TextureDef4(
		Optional<Identifier> chain,
		Optional<Identifier> lantern,
		Optional<Identifier> doorItem,
		Optional<Identifier> doorBottom,
		Optional<Identifier> doorTop,
		Optional<Identifier> trapdoor,
		Optional<Identifier> fence,
		Optional<Identifier> fenceGate
) {
	public static final TextureDef4 EMPTY = new TextureDef4(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty()
	);

	public static final Codec<TextureDef4> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("chain").forGetter(TextureDef4::chain),
			Identifier.CODEC.optionalFieldOf("lantern").forGetter(TextureDef4::lantern),
			Identifier.CODEC.optionalFieldOf("door_item").forGetter(TextureDef4::doorItem),
			Identifier.CODEC.optionalFieldOf("door_bottom").forGetter(TextureDef4::doorBottom),
			Identifier.CODEC.optionalFieldOf("door_top").forGetter(TextureDef4::doorTop),
			Identifier.CODEC.optionalFieldOf("trapdoor").forGetter(TextureDef4::trapdoor),
			Identifier.CODEC.optionalFieldOf("fence").forGetter(TextureDef4::fence),
			Identifier.CODEC.optionalFieldOf("fence_gate").forGetter(TextureDef4::fenceGate)
	).apply(i, TextureDef4::new));

	public static final StreamCodec<ByteBuf, TextureDef4> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef4::chain,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef4::lantern,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef4::doorItem,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef4::doorBottom,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef4::doorTop,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef4::trapdoor,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef4::fence,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef4::fenceGate,
			TextureDef4::new
	);
}
