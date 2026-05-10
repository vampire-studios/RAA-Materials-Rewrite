package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record ItemTextureSet(
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
	public static final Codec<ItemTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("crystal_glass").forGetter(ItemTextureSet::crystalGlass),
			Identifier.CODEC.optionalFieldOf("crystal_bricks").forGetter(ItemTextureSet::crystalBricks),

			Identifier.CODEC.optionalFieldOf("lamp_basalt").forGetter(ItemTextureSet::lampBasalt),
			Identifier.CODEC.optionalFieldOf("lamp_calcite").forGetter(ItemTextureSet::lampCalcite),

			Identifier.CODEC.optionalFieldOf("raw").forGetter(ItemTextureSet::raw),
			Identifier.CODEC.optionalFieldOf("ingot").forGetter(ItemTextureSet::ingot),
			Identifier.CODEC.optionalFieldOf("dust").forGetter(ItemTextureSet::dust),
			Identifier.CODEC.optionalFieldOf("nugget").forGetter(ItemTextureSet::nugget),
			Identifier.CODEC.optionalFieldOf("plate").forGetter(ItemTextureSet::plate),
			Identifier.CODEC.optionalFieldOf("gear").forGetter(ItemTextureSet::gear),
			Identifier.CODEC.optionalFieldOf("gem").forGetter(ItemTextureSet::gem),
			Identifier.CODEC.optionalFieldOf("shard").forGetter(ItemTextureSet::shard),
			Identifier.CODEC.optionalFieldOf("ball").forGetter(ItemTextureSet::ball),

			Identifier.CODEC.optionalFieldOf("tool_pickaxe_head").forGetter(ItemTextureSet::toolPickaxeHead),
			Identifier.CODEC.optionalFieldOf("tool_pickaxe_stick").forGetter(ItemTextureSet::toolPickaxeStick)
	).apply(i, ItemTextureSet::new));
	public static final StreamCodec<ByteBuf, ItemTextureSet> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::crystalGlass,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::crystalBricks,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::lampBasalt,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::lampCalcite,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::raw,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::ingot,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::dust,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::nugget,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::plate,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::gear,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::gem,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::shard,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::ball,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::toolPickaxeHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ItemTextureSet::toolPickaxeStick,
			ItemTextureSet::new
	);
}