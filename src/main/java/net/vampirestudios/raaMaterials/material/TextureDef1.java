package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record TextureDef1(
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
	public static final Codec<TextureDef1> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("ore_vein").forGetter(TextureDef1::oreVein),

			Identifier.CODEC.optionalFieldOf("block").forGetter(TextureDef1::block),
			Identifier.CODEC.optionalFieldOf("raw_block").forGetter(TextureDef1::rawBlock),
			Identifier.CODEC.optionalFieldOf("plate_block").forGetter(TextureDef1::plateBlock),
			Identifier.CODEC.optionalFieldOf("shingles").forGetter(TextureDef1::shingles),

			Identifier.CODEC.optionalFieldOf("sandstone_top").forGetter(TextureDef1::sandstoneTop),
			Identifier.CODEC.optionalFieldOf("sandstone_side").forGetter(TextureDef1::sandstoneSide),
			Identifier.CODEC.optionalFieldOf("sandstone_bottom").forGetter(TextureDef1::sandstoneBottom),
			Identifier.CODEC.optionalFieldOf("cut_sandstone_side").forGetter(TextureDef1::cutSandstoneSide),
			Identifier.CODEC.optionalFieldOf("chiseled_sandstone_side").forGetter(TextureDef1::chiseledSandstoneSide),

			Identifier.CODEC.optionalFieldOf("budding").forGetter(TextureDef1::budding),
			Identifier.CODEC.optionalFieldOf("bud_small").forGetter(TextureDef1::budSmall),
			Identifier.CODEC.optionalFieldOf("bud_medium").forGetter(TextureDef1::budMedium),
			Identifier.CODEC.optionalFieldOf("bud_large").forGetter(TextureDef1::budLarge),
			Identifier.CODEC.optionalFieldOf("cluster").forGetter(TextureDef1::cluster),
			Identifier.CODEC.optionalFieldOf("tinted_glass").forGetter(TextureDef1::tintedGlass)
	).apply(i, TextureDef1::new));
	public static final StreamCodec<ByteBuf, TextureDef1> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::oreVein,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::block,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::rawBlock,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::plateBlock,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::shingles,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::sandstoneTop,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::sandstoneSide,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::sandstoneBottom,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::cutSandstoneSide,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::chiseledSandstoneSide,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::budding,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::budSmall,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::budMedium,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::budLarge,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::cluster,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef1::tintedGlass,
			TextureDef1::new
	);
}