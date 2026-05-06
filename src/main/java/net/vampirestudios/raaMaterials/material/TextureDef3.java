package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record TextureDef3(
		Optional<Identifier> toolAxeHead,
		Optional<Identifier> toolAxeStick,
		Optional<Identifier> toolSwordBlade,
		Optional<Identifier> toolSwordHandle,
		Optional<Identifier> toolShovelHead,
		Optional<Identifier> toolShovelStick,
		Optional<Identifier> toolHoeHead,
		Optional<Identifier> toolHoeStick,

		Optional<Identifier> cobblestone,
		Optional<Identifier> chiseled,
		Optional<Identifier> bricks,
		Optional<Identifier> polished,
		Optional<Identifier> nail,
		Optional<Identifier> ring
) {
	public static final Codec<TextureDef3> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("tool_axe_head").forGetter(TextureDef3::toolAxeHead),
			Identifier.CODEC.optionalFieldOf("tool_axe_stick").forGetter(TextureDef3::toolAxeStick),
			Identifier.CODEC.optionalFieldOf("tool_sword_blade").forGetter(TextureDef3::toolSwordBlade),
			Identifier.CODEC.optionalFieldOf("tool_sword_handle").forGetter(TextureDef3::toolSwordHandle),
			Identifier.CODEC.optionalFieldOf("tool_shovel_head").forGetter(TextureDef3::toolShovelHead),
			Identifier.CODEC.optionalFieldOf("tool_shovel_stick").forGetter(TextureDef3::toolShovelStick),
			Identifier.CODEC.optionalFieldOf("tool_hoe_head").forGetter(TextureDef3::toolHoeHead),
			Identifier.CODEC.optionalFieldOf("tool_hoe_stick").forGetter(TextureDef3::toolHoeStick),
			Identifier.CODEC.optionalFieldOf("cobblestone").forGetter(TextureDef3::cobblestone),
			Identifier.CODEC.optionalFieldOf("chiseled").forGetter(TextureDef3::chiseled),
			Identifier.CODEC.optionalFieldOf("bricks").forGetter(TextureDef3::bricks),
			Identifier.CODEC.optionalFieldOf("polished").forGetter(TextureDef3::polished),
			Identifier.CODEC.optionalFieldOf("nail").forGetter(TextureDef3::nail),
			Identifier.CODEC.optionalFieldOf("ring").forGetter(TextureDef3::ring)
	).apply(i, TextureDef3::new));
	public static final StreamCodec<ByteBuf, TextureDef3> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::toolAxeHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::toolAxeStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::toolSwordBlade,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::toolSwordHandle,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::toolShovelHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::toolShovelStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::toolHoeHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::toolHoeStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::cobblestone,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::chiseled,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::bricks,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::polished,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::nail,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), TextureDef3::ring,
			TextureDef3::new
	);
}