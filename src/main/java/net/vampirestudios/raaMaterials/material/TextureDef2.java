package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record TextureDef2(
		Optional<ResourceLocation> crystalGlass,
		Optional<ResourceLocation> crystalBricks,

		// ---- Blocky utilities ----
		Optional<ResourceLocation> lampBasalt,
		Optional<ResourceLocation> lampCalcite,
//            Optional<ResourceLocation> chime,

		// ---- Items (forms) ----
		Optional<ResourceLocation> raw,
		Optional<ResourceLocation> ingot,
		Optional<ResourceLocation> dust,
		Optional<ResourceLocation> nugget,
		Optional<ResourceLocation> plate,
		Optional<ResourceLocation> gear,
		Optional<ResourceLocation> gem,
		Optional<ResourceLocation> shard,
		Optional<ResourceLocation> ball,

		// ---- Tools ----
		Optional<ResourceLocation> toolPickaxeHead,
		Optional<ResourceLocation> toolPickaxeStick
) {
	public static final Codec<TextureDef2> CODEC = RecordCodecBuilder.create(i -> i.group(
			ResourceLocation.CODEC.optionalFieldOf("crystal_glass").forGetter(TextureDef2::crystalGlass),
			ResourceLocation.CODEC.optionalFieldOf("crystal_bricks").forGetter(TextureDef2::crystalBricks),

			ResourceLocation.CODEC.optionalFieldOf("lamp_basalt").forGetter(TextureDef2::lampBasalt),
			ResourceLocation.CODEC.optionalFieldOf("lamp_calcite").forGetter(TextureDef2::lampCalcite),

			ResourceLocation.CODEC.optionalFieldOf("raw").forGetter(TextureDef2::raw),
			ResourceLocation.CODEC.optionalFieldOf("ingot").forGetter(TextureDef2::ingot),
			ResourceLocation.CODEC.optionalFieldOf("dust").forGetter(TextureDef2::dust),
			ResourceLocation.CODEC.optionalFieldOf("nugget").forGetter(TextureDef2::nugget),
			ResourceLocation.CODEC.optionalFieldOf("plate").forGetter(TextureDef2::plate),
			ResourceLocation.CODEC.optionalFieldOf("gear").forGetter(TextureDef2::gear),
			ResourceLocation.CODEC.optionalFieldOf("gem").forGetter(TextureDef2::gem),
			ResourceLocation.CODEC.optionalFieldOf("shard").forGetter(TextureDef2::shard),
			ResourceLocation.CODEC.optionalFieldOf("ball").forGetter(TextureDef2::ball),

			ResourceLocation.CODEC.optionalFieldOf("tool_pickaxe_head").forGetter(TextureDef2::toolPickaxeHead),
			ResourceLocation.CODEC.optionalFieldOf("tool_pickaxe_stick").forGetter(TextureDef2::toolPickaxeStick)
	).apply(i, TextureDef2::new));
	public static final StreamCodec<ByteBuf, TextureDef2> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::crystalGlass,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::crystalBricks,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::lampBasalt,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::lampCalcite,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::raw,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::ingot,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::dust,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::nugget,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::plate,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::gear,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::gem,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::shard,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::ball,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::toolPickaxeHead,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef2::toolPickaxeStick,
			TextureDef2::new
	);
}