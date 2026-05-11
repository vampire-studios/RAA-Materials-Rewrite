package net.vampirestudios.raaMaterials.content;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.vampirestudios.raaMaterials.material.Form;

/**
 * Dagger item: extremely fast, light blade.
 *
 * <p>Stats set via {@link ToolComponentUtil#applyDagger}:
 * <ul>
 *   <li>2.5 base damage + material bonus — slightly below a sword</li>
 *   <li>−1.0 attack-speed modifier → ~3 attacks/second (vs sword's ~1.6)</li>
 * </ul>
 *
 * <p>Consumes 1 durability per hit (mirrors vanilla sword behaviour).
 */
public class ParametricDaggerItem extends ParametricToolItem {

    public ParametricDaggerItem(Properties props) {
        super(props, Form.DAGGER);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Consume durability on every successful hit
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }
}
