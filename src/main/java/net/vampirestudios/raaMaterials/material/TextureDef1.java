package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record TextureDef1(
		// ---- Ores ----
		Optional<ResourceLocation> oreVein,

		// ---- Core blocks (uniform or per-face) ----
		Optional<ResourceLocation> block,
		Optional<ResourceLocation> rawBlock,
		Optional<ResourceLocation> plateBlock,
		Optional<ResourceLocation> shingles,

		// ---- Sandstone family (per-face) ----
		Optional<ResourceLocation> sandstoneTop,
		Optional<ResourceLocation> sandstoneSide,
		Optional<ResourceLocation> sandstoneBottom,
		Optional<ResourceLocation> cutSandstoneSide,
		Optional<ResourceLocation> chiseledSandstoneSide,

		// ---- Budding & buds ----
		Optional<ResourceLocation> budding,
		Optional<ResourceLocation> budSmall,
		Optional<ResourceLocation> budMedium,
		Optional<ResourceLocation> budLarge,
		Optional<ResourceLocation> cluster,
		Optional<ResourceLocation> tintedGlass
) {
	public static final Codec<TextureDef1> CODEC = RecordCodecBuilder.create(i -> i.group(
			ResourceLocation.CODEC.optionalFieldOf("ore_vein").forGetter(TextureDef1::oreVein),

			ResourceLocation.CODEC.optionalFieldOf("block").forGetter(TextureDef1::block),
			ResourceLocation.CODEC.optionalFieldOf("raw_block").forGetter(TextureDef1::rawBlock),
			ResourceLocation.CODEC.optionalFieldOf("plate_block").forGetter(TextureDef1::plateBlock),
			ResourceLocation.CODEC.optionalFieldOf("shingles").forGetter(TextureDef1::shingles),

			ResourceLocation.CODEC.optionalFieldOf("sandstone_top").forGetter(TextureDef1::sandstoneTop),
			ResourceLocation.CODEC.optionalFieldOf("sandstone_side").forGetter(TextureDef1::sandstoneSide),
			ResourceLocation.CODEC.optionalFieldOf("sandstone_bottom").forGetter(TextureDef1::sandstoneBottom),
			ResourceLocation.CODEC.optionalFieldOf("cut_sandstone_side").forGetter(TextureDef1::cutSandstoneSide),
			ResourceLocation.CODEC.optionalFieldOf("chiseled_sandstone_side").forGetter(TextureDef1::chiseledSandstoneSide),

			ResourceLocation.CODEC.optionalFieldOf("budding").forGetter(TextureDef1::budding),
			ResourceLocation.CODEC.optionalFieldOf("bud_small").forGetter(TextureDef1::budSmall),
			ResourceLocation.CODEC.optionalFieldOf("bud_medium").forGetter(TextureDef1::budMedium),
			ResourceLocation.CODEC.optionalFieldOf("bud_large").forGetter(TextureDef1::budLarge),
			ResourceLocation.CODEC.optionalFieldOf("cluster").forGetter(TextureDef1::cluster),
			ResourceLocation.CODEC.optionalFieldOf("tinted_glass").forGetter(TextureDef1::tintedGlass)
	).apply(i, TextureDef1::new));
	public static final StreamCodec<ByteBuf, TextureDef1> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::oreVein,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::block,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::rawBlock,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::plateBlock,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::shingles,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::sandstoneTop,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::sandstoneSide,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::sandstoneBottom,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::cutSandstoneSide,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::chiseledSandstoneSide,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::budding,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::budSmall,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::budMedium,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::budLarge,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::cluster,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef1::tintedGlass,
			TextureDef1::new
	);
}