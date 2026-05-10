package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.vampirestudios.raaMaterials.material.ToolMaterialSpec;

import java.util.List;

public final class ToolComponentUtil {
    private ToolComponentUtil(){}

    public static void applyMiningTool(ItemStack out, ToolMaterialSpec spec, float baseAttack, float baseSpeed, TagKey<Block> mineTag) {
        var blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        var incorrectTag = TagKey.create(BuiltInRegistries.BLOCK.key(), spec.incorrectBlocksForDropsTag());

        out.set(DataComponents.TOOL, new Tool(
                List.of(
                        Tool.Rule.deniesDrops(blocks.getOrThrow(incorrectTag)),
                        Tool.Rule.minesAndDrops(blocks.getOrThrow(mineTag), spec.speed())
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
        out.set(DataComponents.WEAPON, new Weapon(1));
        out.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 4.0f + spec.attackDamageBonus(),
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, -2.9f,
                                AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build());
    }

    public static void applySword(ItemStack out, ToolMaterialSpec spec, float baseAttack, float baseSpeed) {
        var blocks = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);
        out.set(DataComponents.TOOL, new Tool(
                List.of(
                        Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0f),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SWORD_INSTANTLY_MINES), Float.MAX_VALUE),
                        Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5f)
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
