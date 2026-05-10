package net.vampirestudios.raaMaterials.material.textureSets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

public record SandstoneTextureSet(
		Optional<Identifier> sandstoneTop,
		Optional<Identifier> sandstoneSide,
		Optional<Identifier> sandstoneBottom,
		Optional<Identifier> cutSandstoneSide,
		Optional<Identifier> chiseledSandstoneSide
) {
	public static final SandstoneTextureSet EMPTY = new SandstoneTextureSet(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty()
	);

	public static final Codec<SandstoneTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("sandstone_top").forGetter(SandstoneTextureSet::sandstoneTop),
			Identifier.CODEC.optionalFieldOf("sandstone_side").forGetter(SandstoneTextureSet::sandstoneSide),
			Identifier.CODEC.optionalFieldOf("sandstone_bottom").forGetter(SandstoneTextureSet::sandstoneBottom),
			Identifier.CODEC.optionalFieldOf("cut_sandstone_side").forGetter(SandstoneTextureSet::cutSandstoneSide),
			Identifier.CODEC.optionalFieldOf("chiseled_sandstone_side").forGetter(SandstoneTextureSet::chiseledSandstoneSide)
	).apply(i, SandstoneTextureSet::new));

	public static final StreamCodec<ByteBuf, SandstoneTextureSet> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), SandstoneTextureSet::sandstoneTop,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), SandstoneTextureSet::sandstoneSide,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), SandstoneTextureSet::sandstoneBottom,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), SandstoneTextureSet::cutSandstoneSide,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), SandstoneTextureSet::chiseledSandstoneSide,
			SandstoneTextureSet::new
	);
}
