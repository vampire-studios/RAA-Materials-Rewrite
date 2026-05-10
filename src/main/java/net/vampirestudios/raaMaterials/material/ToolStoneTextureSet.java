package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;

import java.util.Optional;

public record ToolStoneTextureSet(
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
	public static final Codec<ToolStoneTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
			Identifier.CODEC.optionalFieldOf("tool_axe_head").forGetter(ToolStoneTextureSet::toolAxeHead),
			Identifier.CODEC.optionalFieldOf("tool_axe_stick").forGetter(ToolStoneTextureSet::toolAxeStick),
			Identifier.CODEC.optionalFieldOf("tool_sword_blade").forGetter(ToolStoneTextureSet::toolSwordBlade),
			Identifier.CODEC.optionalFieldOf("tool_sword_handle").forGetter(ToolStoneTextureSet::toolSwordHandle),
			Identifier.CODEC.optionalFieldOf("tool_shovel_head").forGetter(ToolStoneTextureSet::toolShovelHead),
			Identifier.CODEC.optionalFieldOf("tool_shovel_stick").forGetter(ToolStoneTextureSet::toolShovelStick),
			Identifier.CODEC.optionalFieldOf("tool_hoe_head").forGetter(ToolStoneTextureSet::toolHoeHead),
			Identifier.CODEC.optionalFieldOf("tool_hoe_stick").forGetter(ToolStoneTextureSet::toolHoeStick),
			Identifier.CODEC.optionalFieldOf("cobblestone").forGetter(ToolStoneTextureSet::cobblestone),
			Identifier.CODEC.optionalFieldOf("chiseled").forGetter(ToolStoneTextureSet::chiseled),
			Identifier.CODEC.optionalFieldOf("bricks").forGetter(ToolStoneTextureSet::bricks),
			Identifier.CODEC.optionalFieldOf("polished").forGetter(ToolStoneTextureSet::polished),
			Identifier.CODEC.optionalFieldOf("nail").forGetter(ToolStoneTextureSet::nail),
			Identifier.CODEC.optionalFieldOf("ring").forGetter(ToolStoneTextureSet::ring)
	).apply(i, ToolStoneTextureSet::new));
	public static final StreamCodec<ByteBuf, ToolStoneTextureSet> STREAM_CODEC = StreamCodecExpanded.composite(
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::toolAxeHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::toolAxeStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::toolSwordBlade,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::toolSwordHandle,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::toolShovelHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::toolShovelStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::toolHoeHead,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::toolHoeStick,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::cobblestone,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::chiseled,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::bricks,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::polished,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::nail,
			ByteBufCodecs.optional(Identifier.STREAM_CODEC), ToolStoneTextureSet::ring,
			ToolStoneTextureSet::new
	);
}