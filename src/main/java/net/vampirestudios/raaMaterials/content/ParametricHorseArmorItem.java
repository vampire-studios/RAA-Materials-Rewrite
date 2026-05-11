package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterials;

/**
 * Horse armor: a {@link ParametricItem} equippable on horses, donkeys, and mules.
 *
 * <p>Uses {@link Item.Properties#horseArmor(net.minecraft.world.item.equipment.ArmorMaterial)}
 * to set the {@code EQUIPPABLE} component (BODY slot, allowed entities from the
 * {@code minecraft:can_wear_horse_armor} entity-type tag) and static iron-tier protection
 * attributes as a baseline.
 *
 * <p>Right-clicking a horse / donkey / mule with this item equips it automatically.
 */
public class ParametricHorseArmorItem extends ParametricItem {

    public ParametricHorseArmorItem(Item.Properties props) {
        super(props.horseArmor(ArmorMaterials.IRON));
    }
}
