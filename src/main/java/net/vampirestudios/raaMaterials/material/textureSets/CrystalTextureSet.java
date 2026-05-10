package net.vampirestudios.raaMaterials.material.textureSets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record CrystalTextureSet(
		Optional<Identifier> budding,
		Optional<Identifier> budSmall,
		Optional<Identifier> budMedium,
		Optional<Identifier> budLarge,
		Optional<Identifier> cluster,
		Optional<Identifier> crystalItem,
		Optional<Identifier> tintedGlass,
		Optional<Identifier> crystalGlass,
		Optional<Identifier> crystalBricks,
		Optional<Identifier> lampBasalt,
		Optional<Identifier> lampCalcite,
		Optional<Identifier> chime
) {
	public static final CrystalTextureSet EMPTY = new CrystalTextureSet(
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty(),
			Optional.empty()
	);

	public static final Codec<CrystalTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("budding").forGetter(CrystalTextureSet::budding),
			Identifier.CODEC.optionalFieldOf("bud_small").forGetter(CrystalTextureSet::budSmall),
			Identifier.CODEC.optionalFieldOf("bud_medium").forGetter(CrystalTextureSet::budMedium),
			Identifier.CODEC.optionalFieldOf("bud_large").forGetter(CrystalTextureSet::budLarge),
			Identifier.CODEC.optionalFieldOf("cluster").forGetter(CrystalTextureSet::cluster),
			Identifier.CODEC.optionalFieldOf("crystal_item").forGetter(CrystalTextureSet::crystalItem),
			Identifier.CODEC.optionalFieldOf("tinted_glass").forGetter(CrystalTextureSet::tintedGlass),
			Identifier.CODEC.optionalFieldOf("crystal_glass").forGetter(CrystalTextureSet::crystalGlass),
			Identifier.CODEC.optionalFieldOf("crystal_bricks").forGetter(CrystalTextureSet::crystalBricks),
			Identifier.CODEC.optionalFieldOf("lamp_basalt").forGetter(CrystalTextureSet::lampBasalt),
			Identifier.CODEC.optionalFieldOf("lamp_calcite").forGetter(CrystalTextureSet::lampCalcite),
			Identifier.CODEC.optionalFieldOf("chime").forGetter(CrystalTextureSet::chime)
	).apply(i, CrystalTextureSet::new));

	public static final StreamCodec<ByteBuf, CrystalTextureSet> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::budding,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::budSmall,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::budMedium,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::budLarge,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::cluster,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::crystalItem,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::tintedGlass,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::crystalGlass,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::crystalBricks,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::lampBasalt,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::lampCalcite,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), CrystalTextureSet::chime,
			CrystalTextureSet::new
	);
}
