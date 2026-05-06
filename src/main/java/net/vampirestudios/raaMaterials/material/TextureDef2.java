package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record TextureDef2(
		Optional<Identifier> crystalGlass,
		Optional<Identifier> crystalBricks,

		// ---- Blocky utilities ----
		Optional<Identifier> lampBasalt,
		Optional<Identifier> lampCalcite,
//            Optional<Identifier> chime,

		// ---- Items (forms) ----
		Optional<Identifier> raw,
		Optional<Identifier> ingot,
		Optional<Identifier> dust,
		Optional<Identifier> nugget,
		Optional<Identifier> plate,
		Optional<Identifier> gear,
		Optional<Identifier> gem,
		Optional<Identifier> shard,
		Optional<Identifier> ball,

		// ---- Tools ----
		Optional<Identifier> toolPickaxeHead,
		Optional<Identifier> toolPickaxeStick
) {
	public static final Codec<TextureDef2> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("crystal_glass").forGetter(TextureDef2::crystalGlass),
			Identifier.CODEC.optionalFieldOf("crystal_bricks").forGetter(TextureDef2::crystalBricks),

			Identifier.CODEC.optionalFieldOf("lamp_basalt").forGetter(TextureDef2::lampBasalt),
			Identifier.CODEC.optionalFieldOf("lamp_calcite").forGetter(TextureDef2::lampCalcite),

			Identifier.CODEC.optionalFieldOf("raw").forGetter(TextureDef2::raw),
			Identifier.CODEC.optionalFieldOf("ingot").forGetter(TextureDef2::ingot),
			Identifier.CODEC.optionalFieldOf("dust").forGetter(TextureDef2::dust),
			Identifier.CODEC.optionalFieldOf("nugget").forGetter(TextureDef2::nugget),
			Identifier.CODEC.optionalFieldOf("plate").forGetter(TextureDef2::plate),
			Identifier.CODEC.optionalFieldOf("gear").forGetter(TextureDef2::gear),
			Identifier.CODEC.optionalFieldOf("gem").forGetter(TextureDef2::gem),
			Identifier.CODEC.optionalFieldOf("shard").forGetter(TextureDef2::shard),
			Identifier.CODEC.optionalFieldOf("ball").forGetter(TextureDef2::ball),

			Identifier.CODEC.optionalFieldOf("tool_pickaxe_head").forGetter(TextureDef2::toolPickaxeHead),
			Identifier.CODEC.optionalFieldOf("tool_pickaxe_stick").forGetter(TextureDef2::toolPickaxeStick)
	).apply(i, TextureDef2::new));
	public static final StreamCodec<ByteBuf, TextureDef2> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::crystalGlass,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::crystalBricks,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::lampBasalt,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::lampCalcite,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::raw,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::ingot,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::dust,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::nugget,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::plate,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::gear,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::gem,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::shard,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::ball,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::toolPickaxeHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef2::toolPickaxeStick,
			TextureDef2::new
	);
}