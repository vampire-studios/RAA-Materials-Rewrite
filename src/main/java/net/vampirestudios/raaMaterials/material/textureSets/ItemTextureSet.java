package net.vampirestudios.raaMaterials.material.textureSets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record ItemTextureSet(
		Optional<Identifier> raw,
		Optional<Identifier> ingot,
		Optional<Identifier> dust,
		Optional<Identifier> nugget,
		Optional<Identifier> plate,
		Optional<Identifier> gear,
		Optional<Identifier> gem,
		Optional<Identifier> shard,
		Optional<Identifier> ball,
		Optional<Identifier> rod,
		Optional<Identifier> wire,
		Optional<Identifier> coil,
		Optional<Identifier> rivet,
		Optional<Identifier> bolt,
		Optional<Identifier> nail,
		Optional<Identifier> ring
) {
	public static final Codec<ItemTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("raw").forGetter(ItemTextureSet::raw),
			Identifier.CODEC.optionalFieldOf("ingot").forGetter(ItemTextureSet::ingot),
			Identifier.CODEC.optionalFieldOf("dust").forGetter(ItemTextureSet::dust),
			Identifier.CODEC.optionalFieldOf("nugget").forGetter(ItemTextureSet::nugget),
			Identifier.CODEC.optionalFieldOf("plate").forGetter(ItemTextureSet::plate),
			Identifier.CODEC.optionalFieldOf("gear").forGetter(ItemTextureSet::gear),
			Identifier.CODEC.optionalFieldOf("gem").forGetter(ItemTextureSet::gem),
			Identifier.CODEC.optionalFieldOf("shard").forGetter(ItemTextureSet::shard),
			Identifier.CODEC.optionalFieldOf("ball").forGetter(ItemTextureSet::ball),
			Identifier.CODEC.optionalFieldOf("rod").forGetter(ItemTextureSet::rod),
			Identifier.CODEC.optionalFieldOf("wire").forGetter(ItemTextureSet::wire),
			Identifier.CODEC.optionalFieldOf("coil").forGetter(ItemTextureSet::coil),
			Identifier.CODEC.optionalFieldOf("rivet").forGetter(ItemTextureSet::rivet),
			Identifier.CODEC.optionalFieldOf("bolt").forGetter(ItemTextureSet::bolt),
			Identifier.CODEC.optionalFieldOf("nail").forGetter(ItemTextureSet::nail),
			Identifier.CODEC.optionalFieldOf("ring").forGetter(ItemTextureSet::ring)
	).apply(i, ItemTextureSet::new));
	public static final StreamCodec<ByteBuf, ItemTextureSet> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::raw,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::ingot,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::dust,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::nugget,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::plate,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::gear,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::gem,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::shard,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::ball,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::rod,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::wire,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::coil,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::rivet,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::bolt,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::nail,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::ring,
			ItemTextureSet::new
	);
}
