package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterials;

/**
 * Nautilus armor: a {@link ParametricItem} equippable on nautiluses.
 *
 * <p>Uses {@link Item.Properties#nautilusArmor(net.minecraft.world.item.equipment.ArmorMaterial)}
 * to set the {@code EQUIPPABLE} component (BODY slot, allowed entities from the
 * {@code minecraft:can_wear_nautilus_armor} entity-type tag), equip/unequip sounds, and
 * iron-tier protection attributes as a baseline.
 *
 * <p>Equipped via {@code equip_on_interact} — right-clicking a nautilus equips it directly.
 * Can be removed by shearing.
 */
public class ParametricNautilusArmorItem extends ParametricItem {

    public ParametricNautilusArmorItem(Item.Properties props) {
        super(props.nautilusArmor(ArmorMaterials.IRON));
    }
}
