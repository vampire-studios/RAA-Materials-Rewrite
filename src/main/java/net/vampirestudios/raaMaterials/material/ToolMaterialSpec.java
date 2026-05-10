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
        Identifier repairItemsTag,             // e.g. yourmod:ingots/<id> (optional)
        SpearSpec spearSpec
) {
    /**
     * Per-material spear tuning, mirroring vanilla's Item.Properties.spear() parameters.
     * knockbackThreshold (always 5.1) and damageThreshold (always 4.6) are hardcoded in applySpear.
     * AttackRange is also hardcoded (vanilla does not vary it per tier).
     *
     *   attackDuration    – seconds per swing; INCREASES with tier (heavier = slower swing, more power)
     *   thrownDamage      – KineticWeapon multiplier for thrown damage
     *   delay             – seconds before kinetic throw can trigger
     *   dismountTime      – kinetic dismount window (seconds)
     *   dismountThreshold – speed threshold for dismount condition
     *   knockbackTime     – kinetic knockback window (seconds)
     *   damageTime        – kinetic damage window (seconds)
     */
    public record SpearSpec(
            float attackDuration,
            float thrownDamage,
            float delay,
            float dismountTime,
            float dismountThreshold,
            float knockbackTime,
            float damageTime
    ) {
        // Iron-tier defaults
        public static final SpearSpec DEFAULT = new SpearSpec(0.95f, 0.95f, 0.60f, 2.5f, 11.0f, 6.75f, 11.25f);

        public static final Codec<SpearSpec> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.FLOAT.optionalFieldOf("attack_duration",     0.95f).forGetter(SpearSpec::attackDuration),
                Codec.FLOAT.optionalFieldOf("thrown_damage",       0.95f).forGetter(SpearSpec::thrownDamage),
                Codec.FLOAT.optionalFieldOf("delay",               0.60f).forGetter(SpearSpec::delay),
                Codec.FLOAT.optionalFieldOf("dismount_time",       2.5f).forGetter(SpearSpec::dismountTime),
                Codec.FLOAT.optionalFieldOf("dismount_threshold",  11.0f).forGetter(SpearSpec::dismountThreshold),
                Codec.FLOAT.optionalFieldOf("knockback_time",      6.75f).forGetter(SpearSpec::knockbackTime),
                Codec.FLOAT.optionalFieldOf("damage_time",         11.25f).forGetter(SpearSpec::damageTime)
        ).apply(i, SpearSpec::new));

        public static final StreamCodec<io.netty.buffer.ByteBuf, SpearSpec> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.FLOAT, SpearSpec::attackDuration,
                        ByteBufCodecs.FLOAT, SpearSpec::thrownDamage,
                        ByteBufCodecs.FLOAT, SpearSpec::delay,
                        ByteBufCodecs.FLOAT, SpearSpec::dismountTime,
                        ByteBufCodecs.FLOAT, SpearSpec::dismountThreshold,
                        ByteBufCodecs.FLOAT, SpearSpec::knockbackTime,
                        ByteBufCodecs.FLOAT, SpearSpec::damageTime,
                        SpearSpec::new
                );
    }

    public static final Codec<ToolMaterialSpec> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.fieldOf("durability").forGetter(ToolMaterialSpec::durability),
            Codec.FLOAT.fieldOf("speed").forGetter(ToolMaterialSpec::speed),
            Codec.FLOAT.fieldOf("attack_damage_bonus").forGetter(ToolMaterialSpec::attackDamageBonus),
            Codec.INT.fieldOf("enchantment_value").forGetter(ToolMaterialSpec::enchantmentValue),
            Identifier.CODEC.fieldOf("incorrect_tag").forGetter(ToolMaterialSpec::incorrectBlocksForDropsTag),
            Identifier.CODEC.optionalFieldOf("repair_tag", Identifier.withDefaultNamespace("air")).forGetter(ToolMaterialSpec::repairItemsTag),
            SpearSpec.CODEC.optionalFieldOf("spear", SpearSpec.DEFAULT).forGetter(ToolMaterialSpec::spearSpec)
    ).apply(i, ToolMaterialSpec::new));

    public static final StreamCodec<io.netty.buffer.ByteBuf, ToolMaterialSpec> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,     ToolMaterialSpec::durability,
                    ByteBufCodecs.FLOAT,       ToolMaterialSpec::speed,
                    ByteBufCodecs.FLOAT,       ToolMaterialSpec::attackDamageBonus,
                    ByteBufCodecs.VAR_INT,     ToolMaterialSpec::enchantmentValue,
                    Identifier.STREAM_CODEC,   ToolMaterialSpec::incorrectBlocksForDropsTag,
                    Identifier.STREAM_CODEC,   ToolMaterialSpec::repairItemsTag,
                    SpearSpec.STREAM_CODEC,    ToolMaterialSpec::spearSpec,
                    ToolMaterialSpec::new
            );
}
