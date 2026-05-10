package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwingAnimationType;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.vampirestudios.raaMaterials.material.ToolMaterialSpec;

import java.util.List;
import java.util.Optional;

public final class ToolComponentUtil {
    private ToolComponentUtil(){}

    public static void applyMiningTool(ItemStack out, ToolMaterialSpec spec, float baseAttack, float baseSpeed, TagKey<Block> mineTag) {
        var reg = BuiltInRegistries.BLOCK;
        var incorrectTag = TagKey.create(reg.key(), spec.incorrectBlocksForDropsTag());
        var incorrectBlocks = reg.get(incorrectTag)
                .orElseThrow(() -> new IllegalStateException("Missing block tag: " + incorrectTag.location()));
        var mineBlocks = reg.get(mineTag)
                .orElseThrow(() -> new IllegalStateException("Missing block tag: " + mineTag.location()));

        out.set(DataComponents.TOOL, new Tool(
                List.of(
                        Tool.Rule.deniesDrops(incorrectBlocks),
                        Tool.Rule.minesAndDrops(mineBlocks, spec.speed())
                ),
                1.0f, 1, true
        ));

        out.set(DataComponents.WEAPON, new Weapon(2));

        out.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, baseAttack + spec.attackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, baseSpeed,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build());
    }

    public static void applySpear(ItemStack out, ToolMaterialSpec spec) {
        var spear = spec.spearSpec();
        float attackDuration = spear.attackDuration();

        // knockbackThreshold (5.1) and damageThreshold (4.6) are constant across all vanilla tiers.
        out.set(DataComponents.KINETIC_WEAPON, new KineticWeapon(10,
                ticks(spear.delay()),
                KineticWeapon.Condition.ofAttackerSpeed(ticks(spear.dismountTime()),  spear.dismountThreshold()),
                KineticWeapon.Condition.ofAttackerSpeed(ticks(spear.knockbackTime()), 5.1f),
                KineticWeapon.Condition.ofRelativeSpeed(ticks(spear.damageTime()),    4.6f),
                0.38f, spear.thrownDamage(),
                Optional.of(SoundEvents.SPEAR_USE), Optional.of(SoundEvents.SPEAR_HIT)));

        out.set(DataComponents.PIERCING_WEAPON, new PiercingWeapon(true, false,
                Optional.of(SoundEvents.SPEAR_ATTACK), Optional.of(SoundEvents.SPEAR_HIT)));

        out.set(DataComponents.ATTACK_RANGE, new AttackRange(2.0f, 4.5f, 2.0f, 6.5f, 0.125f, 0.5f));

        out.set(DataComponents.MINIMUM_ATTACK_CHARGE, 1.0f);

        out.set(DataComponents.SWING_ANIMATION,
                new SwingAnimation(SwingAnimationType.STAB, ticks(attackDuration)));

        out.set(DataComponents.USE_EFFECTS, new UseEffects(true, false, 1.0f));

        out.set(DataComponents.WEAPON, new Weapon(1));

        out.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, spec.attackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                                1.0f / attackDuration - 4.0f,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build());
    }

    private static int ticks(float seconds) {
        return (int) (seconds * 20.0f);
    }

    public static void applySword(ItemStack out, ToolMaterialSpec spec, float baseAttack, float baseSpeed) {
        var reg = BuiltInRegistries.BLOCK;
        var instantlyMines = reg.get(BlockTags.SWORD_INSTANTLY_MINES)
                .orElseThrow(() -> new IllegalStateException("Missing block tag: " + BlockTags.SWORD_INSTANTLY_MINES.location()));
        var efficient = reg.get(BlockTags.SWORD_EFFICIENT)
                .orElseThrow(() -> new IllegalStateException("Missing block tag: " + BlockTags.SWORD_EFFICIENT.location()));
        out.set(DataComponents.TOOL, new Tool(
                List.of(
                        Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0f),
                        Tool.Rule.overrideSpeed(instantlyMines, Float.MAX_VALUE),
                        Tool.Rule.overrideSpeed(efficient, 1.5f)
                ),
                1.0f, 2, false
        ));
        out.set(DataComponents.WEAPON, new Weapon(1));
        out.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, baseAttack + spec.attackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, baseSpeed,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build());
    }

    public static void applyCommon(ItemStack out, ToolMaterialSpec spec) {
        out.set(DataComponents.MAX_DAMAGE, spec.durability());
        out.set(DataComponents.DAMAGE, 0);
        out.set(DataComponents.ENCHANTABLE, new Enchantable(spec.enchantmentValue()));

        // Allow these items to be used as repair inputs (ingredient-only).
        // We'll enforce "same material" in the anvil event below.
//        Ingredient repairables = Ingredient.of(
//                YItems.PARAM_INGOT,
//                YItems.PARAM_GEM,
//                YItems.PARAM_CRYSTAL,
//                YItems.PARAM_RAW,
//                YItems.PARAM_SHARD
//        );
//        out.set(DataComponents.REPAIRABLE, new Repairable(repairables));
    }
}
