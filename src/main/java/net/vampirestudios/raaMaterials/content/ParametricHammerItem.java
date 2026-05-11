package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.material.Form;

import java.util.ArrayList;
import java.util.List;

/**
 * Hammer item: heavy two-handed weapon with area-of-effect combat and 3×3 block mining.
 *
 * <h3>Combat AOE</h3>
 * On every hit, nearby entities within {@value #SPLASH_RADIUS} blocks also receive
 * {@value #SPLASH_RATIO}× the hammer's base attack damage as splash damage (the primary
 * target still takes the full hit from the WEAPON component).
 * Durability costs 1 for the primary hit and 1 per entity in the splash ring.
 *
 * <h3>Mining AOE</h3>
 * On breaking a pickaxe-mineable block, every block in a square face-patch around the
 * target is also broken. Size = {@code 2×radius+1}, radius from
 * {@link RAAConfig.FormChances#hammerAoeRadius()} (default 1 = 3×3).
 * Ores are excluded via the {@code raa_materials:hammer_cannot_aoe} data tag.
 */
public class ParametricHammerItem extends ParametricToolItem {

    /** Radius (in blocks) of the combat splash ring. */
    private static final float SPLASH_RADIUS = 2.5f;

    /**
     * Fraction of the primary attack damage dealt to entities in the splash ring.
     * 0.5 = 50 % of the hammer's base damage modifier.
     */
    private static final float SPLASH_RATIO = 0.5f;

    /** Blocks the mining AOE should never break (ores, ancient debris, …). */
    private static final TagKey<Block> HAMMER_CANNOT_AOE = TagKey.create(
            Registries.BLOCK, RAAMaterials.id("hammer_cannot_aoe"));

    /** Re-entry guard for the block-mining AOE loop. */
    private static final ThreadLocal<Boolean> MINING_AOE_ACTIVE = ThreadLocal.withInitial(() -> false);

    public ParametricHammerItem(Properties props) {
        super(props, Form.HAMMER);
    }

    // ── Combat AOE ─────────────────────────────────────────────────────────────

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // Durability for the primary hit
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);

        // Splash ring — only runs server-side
        if (target.level().isClientSide()) return;

        float splashDamage = getSplashDamage(stack);
        if (splashDamage <= 0f) return;

        var level = target.level();
        var bb = new AABB(target.position(), target.position()).inflate(SPLASH_RADIUS);

        var nearby = level.getEntitiesOfClass(LivingEntity.class, bb,
                e -> e != attacker && e != target && e.isAlive());

        for (var entity : nearby) {
            entity.hurt(level.damageSources().generic(), splashDamage);
            stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
            if (stack.isEmpty()) break;
        }
    }

    /**
     * Returns the splash damage value: {@value #SPLASH_RATIO}× the ATTACK_DAMAGE
     * modifier on the stack, or 4.0 as a safe fallback.
     */
    private static float getSplashDamage(ItemStack stack) {
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null) return 4.0f;
        for (var entry : modifiers.modifiers()) {
            if (entry.modifier().id().equals(Item.BASE_ATTACK_DAMAGE_ID)) {
                return (float) (entry.modifier().amount() * SPLASH_RATIO);
            }
        }
        return 4.0f;
    }

    // ── Mining AOE ─────────────────────────────────────────────────────────────

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
        // Durability per block broken (mirrors vanilla DiggerItem)
        if (!level.isClientSide() && state.getDestroySpeed(level, pos) != 0f) {
            stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND);
        }

        // Mining AOE — server-side only, pickaxe blocks only, no re-entry
        if (!MINING_AOE_ACTIVE.get()
                && level instanceof ServerLevel serverLevel
                && entity instanceof ServerPlayer player
                && state.is(BlockTags.MINEABLE_WITH_PICKAXE)) {

            int radius = RAAConfig.active().formChances().hammerAoeRadius();
            if (radius > 0) {
                MINING_AOE_ACTIVE.set(true);
                try {
                    breakAoeBlocks(stack, serverLevel, pos, player, radius);
                } finally {
                    MINING_AOE_ACTIVE.set(false);
                }
            }
        }

        return true;
    }

    private static void breakAoeBlocks(ItemStack stack, ServerLevel level, BlockPos center,
            ServerPlayer player, int radius) {

        var look = player.getLookAngle();
        double ax = Math.abs(look.x);
        double ay = Math.abs(look.y);
        double az = Math.abs(look.z);

        List<BlockPos> targets = new ArrayList<>();
        if (ay >= ax && ay >= az) {
            for (int dx = -radius; dx <= radius; dx++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dx != 0 || dz != 0) targets.add(center.offset(dx, 0, dz));
        } else if (ax >= az) {
            for (int dy = -radius; dy <= radius; dy++)
                for (int dz = -radius; dz <= radius; dz++)
                    if (dy != 0 || dz != 0) targets.add(center.offset(0, dy, dz));
        } else {
            for (int dx = -radius; dx <= radius; dx++)
                for (int dy = -radius; dy <= radius; dy++)
                    if (dx != 0 || dy != 0) targets.add(center.offset(dx, dy, 0));
        }

        for (BlockPos pos : targets) {
            if (stack.isEmpty()) break;
            var state = level.getBlockState(pos);
            if (state.isAir()) continue;
            if (!state.is(BlockTags.MINEABLE_WITH_PICKAXE)) continue;
            if (state.is(HAMMER_CANNOT_AOE)) continue;
            if (!player.hasCorrectToolForDrops(state)) continue;

            state.getBlock().playerDestroy(level, player, pos, state, level.getBlockEntity(pos), stack);
            level.removeBlock(pos, false);
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
        }
    }
}
