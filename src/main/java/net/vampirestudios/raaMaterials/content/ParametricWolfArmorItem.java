package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterials;

/**
 * Wolf armor: a {@link ParametricItem} equippable on tamed wolves.
 *
 * <p>Uses {@link Item.Properties#wolfArmor(net.minecraft.world.item.equipment.ArmorMaterial)}
 * to set the {@code EQUIPPABLE} component (BODY slot, allowed: wolf entity type),
 * durability (armadillo-scute tier as baseline), and the wolf armor break/unequip sounds.
 *
 * <p>Takes durability damage when the wolf takes damage and can be removed by shearing.
 */
public class ParametricWolfArmorItem extends ParametricItem {

    public ParametricWolfArmorItem(Item.Properties props) {
        super(props.wolfArmor(ArmorMaterials.ARMADILLO_SCUTE));
    }
}
