package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record BlockTextureSet(
		// ---- Ores ----
		Optional<Identifier> oreVein,

		// ---- Core blocks (uniform or per-face) ----
		Optional<Identifier> block,
		Optional<Identifier> rawBlock,
		Optional<Identifier> plateBlock,
		Optional<Identifier> shingles,

		// ---- Sandstone family (per-face) ----
		Optional<Identifier> sandstoneTop,
		Optional<Identifier> sandstoneSide,
		Optional<Identifier> sandstoneBottom,
		Optional<Identifier> cutSandstoneSide,
		Optional<Identifier> chiseledSandstoneSide,

		// ---- Budding & buds ----
		Optional<Identifier> budding,
		Optional<Identifier> budSmall,
		Optional<Identifier> budMedium,
		Optional<Identifier> budLarge,
		Optional<Identifier> cluster,
		Optional<Identifier> tintedGlass
) {
	public static final Codec<BlockTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("ore_vein").forGetter(BlockTextureSet::oreVein),

			Identifier.CODEC.optionalFieldOf("block").forGetter(BlockTextureSet::block),
			Identifier.CODEC.optionalFieldOf("raw_block").forGetter(BlockTextureSet::rawBlock),
			Identifier.CODEC.optionalFieldOf("plate_block").forGetter(BlockTextureSet::plateBlock),
			Identifier.CODEC.optionalFieldOf("shingles").forGetter(BlockTextureSet::shingles),

			Identifier.CODEC.optionalFieldOf("sandstone_top").forGetter(BlockTextureSet::sandstoneTop),
			Identifier.CODEC.optionalFieldOf("sandstone_side").forGetter(BlockTextureSet::sandstoneSide),
			Identifier.CODEC.optionalFieldOf("sandstone_bottom").forGetter(BlockTextureSet::sandstoneBottom),
			Identifier.CODEC.optionalFieldOf("cut_sandstone_side").forGetter(BlockTextureSet::cutSandstoneSide),
			Identifier.CODEC.optionalFieldOf("chiseled_sandstone_side").forGetter(BlockTextureSet::chiseledSandstoneSide),

			Identifier.CODEC.optionalFieldOf("budding").forGetter(BlockTextureSet::budding),
			Identifier.CODEC.optionalFieldOf("bud_small").forGetter(BlockTextureSet::budSmall),
			Identifier.CODEC.optionalFieldOf("bud_medium").forGetter(BlockTextureSet::budMedium),
			Identifier.CODEC.optionalFieldOf("bud_large").forGetter(BlockTextureSet::budLarge),
			Identifier.CODEC.optionalFieldOf("cluster").forGetter(BlockTextureSet::cluster),
			Identifier.CODEC.optionalFieldOf("tinted_glass").forGetter(BlockTextureSet::tintedGlass)
	).apply(i, BlockTextureSet::new));
	public static final StreamCodec<ByteBuf, BlockTextureSet> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::oreVein,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::block,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::rawBlock,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::plateBlock,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::shingles,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::sandstoneTop,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::sandstoneSide,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::sandstoneBottom,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::cutSandstoneSide,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::chiseledSandstoneSide,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::budding,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::budSmall,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::budMedium,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::budLarge,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::cluster,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), BlockTextureSet::tintedGlass,
			BlockTextureSet::new
	);
}