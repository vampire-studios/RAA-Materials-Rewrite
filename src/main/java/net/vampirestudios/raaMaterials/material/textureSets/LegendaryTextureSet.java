package net.vampirestudios.raaMaterials.material.textureSets;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Texture set for legendary weapons (hammer, dagger) and mount armors
 * (horse, wolf, nautilus). Kept separate from ToolTextureSet because
 * both ToolTextureSet and ItemTextureSet are already at the 16-field DFU cap.
 */
public record LegendaryTextureSet(
        Optional<Identifier> hammerHead,
        Optional<Identifier> hammerHandle,
        Optional<Identifier> daggerBlade,
        Optional<Identifier> daggerHandle,
        Optional<Identifier> horseArmor,
        Optional<Identifier> wolfArmor,
        Optional<Identifier> nautilusArmor
) {
    public static final LegendaryTextureSet EMPTY = new LegendaryTextureSet(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
    );

    public static final Codec<LegendaryTextureSet> CODEC = RecordCodecBuilder.create(i -> i.group(
            Identifier.CODEC.optionalFieldOf("hammer_head").forGetter(LegendaryTextureSet::hammerHead),
            Identifier.CODEC.optionalFieldOf("hammer_handle").forGetter(LegendaryTextureSet::hammerHandle),
            Identifier.CODEC.optionalFieldOf("dagger_blade").forGetter(LegendaryTextureSet::daggerBlade),
            Identifier.CODEC.optionalFieldOf("dagger_handle").forGetter(LegendaryTextureSet::daggerHandle),
            Identifier.CODEC.optionalFieldOf("horse_armor").forGetter(LegendaryTextureSet::horseArmor),
            Identifier.CODEC.optionalFieldOf("wolf_armor").forGetter(LegendaryTextureSet::wolfArmor),
            Identifier.CODEC.optionalFieldOf("nautilus_armor").forGetter(LegendaryTextureSet::nautilusArmor)
    ).apply(i, LegendaryTextureSet::new));

    public static final StreamCodec<ByteBuf, LegendaryTextureSet> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), LegendaryTextureSet::hammerHead,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), LegendaryTextureSet::hammerHandle,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), LegendaryTextureSet::daggerBlade,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), LegendaryTextureSet::daggerHandle,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), LegendaryTextureSet::horseArmor,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), LegendaryTextureSet::wolfArmor,
            ByteBufCodecs.optional(Identifier.STREAM_CODEC), LegendaryTextureSet::nautilusArmor,
            LegendaryTextureSet::new
    );
}
