package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ToolMaterialSpec(
        int durability,
        float speed,
        float attackDamageBonus,
        int enchantmentValue,
        Identifier incorrectBlocksForDropsTag, // e.g. minecraft:incorrect_for_iron_tool
        Identifier repairItemsTag              // e.g. yourmod:ingots/<id> (optional)
) {
    public static final Codec<ToolMaterialSpec> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("durability").forGetter(ToolMaterialSpec::durability),
            Codec.FLOAT.fieldOf("speed").forGetter(ToolMaterialSpec::speed),
            Codec.FLOAT.fieldOf("attack_damage_bonus").forGetter(ToolMaterialSpec::attackDamageBonus),
            Codec.INT.fieldOf("enchantment_value").forGetter(ToolMaterialSpec::enchantmentValue),
            Identifier.CODEC.fieldOf("incorrect_tag").forGetter(ToolMaterialSpec::incorrectBlocksForDropsTag),
            Identifier.CODEC.optionalFieldOf("repair_tag", Identifier.withDefaultNamespace("air")).forGetter(ToolMaterialSpec::repairItemsTag)
    ).apply(i, ToolMaterialSpec::new));

    public static final StreamCodec<io.netty.buffer.ByteBuf, ToolMaterialSpec> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, ToolMaterialSpec::durability,
                    ByteBufCodecs.FLOAT,   ToolMaterialSpec::speed,
                    ByteBufCodecs.FLOAT,   ToolMaterialSpec::attackDamageBonus,
                    ByteBufCodecs.VAR_INT, ToolMaterialSpec::enchantmentValue,
                    Identifier.STREAM_CODEC, ToolMaterialSpec::incorrectBlocksForDropsTag,
                    Identifier.STREAM_CODEC, ToolMaterialSpec::repairItemsTag,
                    ToolMaterialSpec::new
            );
}
