package net.vampirestudios.raaMaterials.material.textureSets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record BlockTextureSet(
		Optional<Identifier> oreVein,
		Optional<Identifier> block,
		Optional<Identifier> rawBlock,
		Optional<Identifier> plateBlock,
		Optional<Identifier> shingles,
		Optional<Identifier> cobblestone,
		Optional<Identifier> chiseled,
		Optional<Identifier> bricks,
		Optional<Identifier> polished
) {
	public static final Codec<BlockTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("ore_vein").forGetter(BlockTextureSet::oreVein),
			Identifier.CODEC.optionalFieldOf("block").forGetter(BlockTextureSet::block),
			Identifier.CODEC.optionalFieldOf("raw_block").forGetter(BlockTextureSet::rawBlock),
			Identifier.CODEC.optionalFieldOf("plate_block").forGetter(BlockTextureSet::plateBlock),
			Identifier.CODEC.optionalFieldOf("shingles").forGetter(BlockTextureSet::shingles),
			Identifier.CODEC.optionalFieldOf("cobblestone").forGetter(BlockTextureSet::cobblestone),
			Identifier.CODEC.optionalFieldOf("chiseled").forGetter(BlockTextureSet::chiseled),
			Identifier.CODEC.optionalFieldOf("bricks").forGetter(BlockTextureSet::bricks),
			Identifier.CODEC.optionalFieldOf("polished").forGetter(BlockTextureSet::polished)
	).apply(i, BlockTextureSet::new));
	public static final StreamCodec<ByteBuf, BlockTextureSet> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::oreVein,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::block,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::rawBlock,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::plateBlock,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::shingles,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::cobblestone,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::chiseled,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::bricks,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::polished,
			BlockTextureSet::new
	);
}
