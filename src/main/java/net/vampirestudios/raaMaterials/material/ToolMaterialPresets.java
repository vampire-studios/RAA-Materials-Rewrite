// material/ToolMaterialPresets.java
package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.Identifier;

public final class ToolMaterialPresets {
    private ToolMaterialPresets(){}

    // SpearSpec(attackDuration, thrownDamage, delay, dismountTime, dismountThreshold, knockbackTime, damageTime)
    // attackDuration INCREASES with tier (heavier spear = slower swing, harder hit).
    // Timing windows DECREASE with tier (better material = more responsive kinetic weapon).
    // Values mirror vanilla stone/iron/diamond/netherite spear progression.

    /** Vanilla-ish bands for metals/alloys. */
    public static ToolMaterialSpec forMetal(HarvestTier t) {
        return switch (t) {
            case STONE     -> new ToolMaterialSpec(131, 4.0f, 1.0f, 5,
                    rl("minecraft:incorrect_for_stone_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(0.75f, 0.82f, 0.70f, 4.5f, 13.0f, 9.0f,  13.75f));
            case IRON      -> new ToolMaterialSpec(250, 6.0f, 2.0f, 14,
                    rl("minecraft:incorrect_for_iron_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(0.95f, 0.95f, 0.60f, 2.5f, 11.0f, 6.75f, 11.25f));
            case DIAMOND   -> new ToolMaterialSpec(1561, 8.0f, 3.0f, 10,
                    rl("minecraft:incorrect_for_diamond_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(1.05f, 1.075f, 0.50f, 3.0f, 10.0f, 6.5f,  10.0f));
            case NETHERITE -> new ToolMaterialSpec(2031, 9.0f, 4.0f, 15,
                    rl("minecraft:incorrect_for_netherite_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(1.15f, 1.2f,  0.40f, 2.5f,  9.0f, 5.5f,   8.75f));
        };
    }

    /** Slightly glassy but sharp & enchanting: good for gems. */
    public static ToolMaterialSpec forGem(HarvestTier t) {
        // Gems are precise rather than heavy: same timing windows as metals, slightly lower thrown damage.
        return switch (t) {
            case STONE     -> new ToolMaterialSpec(180, 4.5f, 1.0f, 18,
                    rl("minecraft:incorrect_for_stone_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(0.75f, 0.75f, 0.70f, 4.5f, 13.0f, 9.0f,  13.75f));
            case IRON      -> new ToolMaterialSpec(400, 6.5f, 2.0f, 20,
                    rl("minecraft:incorrect_for_iron_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(0.95f, 0.85f, 0.60f, 2.5f, 11.0f, 6.75f, 11.25f));
            case DIAMOND   -> new ToolMaterialSpec(1400, 8.5f, 3.0f, 22,
                    rl("minecraft:incorrect_for_diamond_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(1.05f, 1.0f,  0.50f, 3.0f, 10.0f, 6.5f,  10.0f));
            case NETHERITE -> new ToolMaterialSpec(1800, 9.0f, 3.5f, 22,
                    rl("minecraft:incorrect_for_netherite_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(1.15f, 1.1f,  0.40f, 2.5f,  9.0f, 5.5f,   8.75f));
        };
    }

    /** Crystals: fast, fragile, high enchanting — lighter so shorter attackDuration, weaker throw. */
    public static ToolMaterialSpec forCrystal(HarvestTier t) {
        // Crystals are lighter: attackDuration closer to wooden-spear values, wider timing windows.
        return switch (t) {
            case STONE     -> new ToolMaterialSpec(100, 5.0f, 0.0f, 20,
                    rl("minecraft:incorrect_for_stone_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(0.65f, 0.70f, 0.75f, 5.0f, 14.0f, 10.0f, 15.0f));
            case IRON      -> new ToolMaterialSpec(220, 6.8f, 1.5f, 22,
                    rl("minecraft:incorrect_for_iron_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(0.80f, 0.82f, 0.65f, 4.0f, 12.0f,  8.0f, 12.0f));
            case DIAMOND   -> new ToolMaterialSpec(900, 8.8f, 2.5f, 24,
                    rl("minecraft:incorrect_for_diamond_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(0.90f, 0.95f, 0.55f, 3.5f, 10.5f,  7.0f, 10.5f));
            case NETHERITE -> new ToolMaterialSpec(1200, 9.2f, 3.0f, 24,
                    rl("minecraft:incorrect_for_netherite_tool"), rl("minecraft:air"),
                    new ToolMaterialSpec.SpearSpec(1.00f, 1.05f, 0.45f, 3.0f,  9.5f,  6.0f,  9.5f));
        };
    }

    private static Identifier rl(String s){ return Identifier.tryParse(s); }
}
