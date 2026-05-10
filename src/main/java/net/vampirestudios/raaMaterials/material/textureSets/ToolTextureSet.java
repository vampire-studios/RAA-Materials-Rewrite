package net.vampirestudios.raaMaterials.material.textureSets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record ToolTextureSet(
		Optional<Identifier> pickaxeHead,
		Optional<Identifier> pickaxeStick,
		Optional<Identifier> axeHead,
		Optional<Identifier> axeStick,
		Optional<Identifier> swordBlade,
		Optional<Identifier> swordHandle,
		Optional<Identifier> shovelHead,
		Optional<Identifier> shovelStick,
		Optional<Identifier> hoeHead,
		Optional<Identifier> hoeStick,
		Optional<Identifier> shearsBase,
		Optional<Identifier> shearsMetal,
		Optional<Identifier> spearHead,
		Optional<Identifier> spearHandle,
		Optional<Identifier> spearHeadInHand,
		Optional<Identifier> spearHandleInHand
) {
	public static final Codec<ToolTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("pickaxe_head").forGetter(ToolTextureSet::pickaxeHead),
			Identifier.CODEC.optionalFieldOf("pickaxe_stick").forGetter(ToolTextureSet::pickaxeStick),
			Identifier.CODEC.optionalFieldOf("axe_head").forGetter(ToolTextureSet::axeHead),
			Identifier.CODEC.optionalFieldOf("axe_stick").forGetter(ToolTextureSet::axeStick),
			Identifier.CODEC.optionalFieldOf("sword_blade").forGetter(ToolTextureSet::swordBlade),
			Identifier.CODEC.optionalFieldOf("sword_handle").forGetter(ToolTextureSet::swordHandle),
			Identifier.CODEC.optionalFieldOf("shovel_head").forGetter(ToolTextureSet::shovelHead),
			Identifier.CODEC.optionalFieldOf("shovel_stick").forGetter(ToolTextureSet::shovelStick),
			Identifier.CODEC.optionalFieldOf("hoe_head").forGetter(ToolTextureSet::hoeHead),
			Identifier.CODEC.optionalFieldOf("hoe_stick").forGetter(ToolTextureSet::hoeStick),
			Identifier.CODEC.optionalFieldOf("shears_base").forGetter(ToolTextureSet::shearsBase),
			Identifier.CODEC.optionalFieldOf("shears_metal").forGetter(ToolTextureSet::shearsMetal),
			Identifier.CODEC.optionalFieldOf("spear_head").forGetter(ToolTextureSet::spearHead),
			Identifier.CODEC.optionalFieldOf("spear_handle").forGetter(ToolTextureSet::spearHandle),
			Identifier.CODEC.optionalFieldOf("spear_head_in_hand").forGetter(ToolTextureSet::spearHeadInHand),
			Identifier.CODEC.optionalFieldOf("spear_handle_in_hand").forGetter(ToolTextureSet::spearHandleInHand)
	).apply(i, ToolTextureSet::new));
	public static final StreamCodec<ByteBuf, ToolTextureSet> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::pickaxeHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::pickaxeStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::axeHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::axeStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::swordBlade,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::swordHandle,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::shovelHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::shovelStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::hoeHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::hoeStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::shearsBase,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::shearsMetal,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::spearHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::spearHandle,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::spearHeadInHand,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolTextureSet::spearHandleInHand,
			ToolTextureSet::new
	);
}
