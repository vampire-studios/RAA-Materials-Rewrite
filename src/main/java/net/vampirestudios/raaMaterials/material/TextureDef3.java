package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record TextureDef3(
		Optional<ResourceLocation> toolAxeHead,
		Optional<ResourceLocation> toolAxeStick,
		Optional<ResourceLocation> toolSwordBlade,
		Optional<ResourceLocation> toolSwordHandle,
		Optional<ResourceLocation> toolShovelHead,
		Optional<ResourceLocation> toolShovelStick,
		Optional<ResourceLocation> toolHoeHead,
		Optional<ResourceLocation> toolHoeStick,

		Optional<ResourceLocation> cobblestone,
		Optional<ResourceLocation> chiseled,
		Optional<ResourceLocation> bricks,
		Optional<ResourceLocation> polished,
		Optional<ResourceLocation> nail,
		Optional<ResourceLocation> ring
) {
	public static final Codec<TextureDef3> CODEC = RecordCodecBuilder.create(i -> i.group(
			ResourceLocation.CODEC.optionalFieldOf("tool_axe_head").forGetter(TextureDef3::toolAxeHead),
			ResourceLocation.CODEC.optionalFieldOf("tool_axe_stick").forGetter(TextureDef3::toolAxeStick),
			ResourceLocation.CODEC.optionalFieldOf("tool_sword_blade").forGetter(TextureDef3::toolSwordBlade),
			ResourceLocation.CODEC.optionalFieldOf("tool_sword_handle").forGetter(TextureDef3::toolSwordHandle),
			ResourceLocation.CODEC.optionalFieldOf("tool_shovel_head").forGetter(TextureDef3::toolShovelHead),
			ResourceLocation.CODEC.optionalFieldOf("tool_shovel_stick").forGetter(TextureDef3::toolShovelStick),
			ResourceLocation.CODEC.optionalFieldOf("tool_hoe_head").forGetter(TextureDef3::toolHoeHead),
			ResourceLocation.CODEC.optionalFieldOf("tool_hoe_stick").forGetter(TextureDef3::toolHoeStick),
			ResourceLocation.CODEC.optionalFieldOf("cobblestone").forGetter(TextureDef3::cobblestone),
			ResourceLocation.CODEC.optionalFieldOf("chiseled").forGetter(TextureDef3::chiseled),
			ResourceLocation.CODEC.optionalFieldOf("bricks").forGetter(TextureDef3::bricks),
			ResourceLocation.CODEC.optionalFieldOf("polished").forGetter(TextureDef3::polished),
			ResourceLocation.CODEC.optionalFieldOf("nail").forGetter(TextureDef3::nail),
			ResourceLocation.CODEC.optionalFieldOf("ring").forGetter(TextureDef3::ring)
	).apply(i, TextureDef3::new));
	public static final StreamCodec<ByteBuf, TextureDef3> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::toolAxeHead,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::toolAxeStick,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::toolSwordBlade,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::toolSwordHandle,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::toolShovelHead,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::toolShovelStick,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::toolHoeHead,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::toolHoeStick,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::cobblestone,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::chiseled,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::bricks,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::polished,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::nail,
			ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC), TextureDef3::ring,
			TextureDef3::new
	);
}