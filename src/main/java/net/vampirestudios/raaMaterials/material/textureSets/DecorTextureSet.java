package net.vampirestudios.raaMaterials.material.textureSets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record DecorTextureSet(
		Optional<Identifier> chain,
		Optional<Identifier> lantern,
		Optional<Identifier> doorItem,
		Optional<Identifier> doorBottom,
		Optional<Identifier> doorTop,
		Optional<Identifier> trapdoor,
		Optional<Identifier> fence,
		Optional<Identifier> fenceGate
) {
	public static final DecorTextureSet EMPTY = new DecorTextureSet(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty()
	);

	public static final Codec<DecorTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("chain").forGetter(DecorTextureSet::chain),
			Identifier.CODEC.optionalFieldOf("lantern").forGetter(DecorTextureSet::lantern),
			Identifier.CODEC.optionalFieldOf("door_item").forGetter(DecorTextureSet::doorItem),
			Identifier.CODEC.optionalFieldOf("door_bottom").forGetter(DecorTextureSet::doorBottom),
			Identifier.CODEC.optionalFieldOf("door_top").forGetter(DecorTextureSet::doorTop),
			Identifier.CODEC.optionalFieldOf("trapdoor").forGetter(DecorTextureSet::trapdoor),
			Identifier.CODEC.optionalFieldOf("fence").forGetter(DecorTextureSet::fence),
			Identifier.CODEC.optionalFieldOf("fence_gate").forGetter(DecorTextureSet::fenceGate)
	).apply(i, DecorTextureSet::new));

	public static final StreamCodec<ByteBuf, DecorTextureSet> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), DecorTextureSet::chain,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), DecorTextureSet::lantern,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), DecorTextureSet::doorItem,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), DecorTextureSet::doorBottom,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), DecorTextureSet::doorTop,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), DecorTextureSet::trapdoor,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), DecorTextureSet::fence,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), DecorTextureSet::fenceGate,
			DecorTextureSet::new
	);
}
