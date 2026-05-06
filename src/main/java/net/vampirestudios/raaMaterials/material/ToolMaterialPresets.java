// material/ToolMaterialPresets.java
package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.Identifier;

public final class ToolMaterialPresets {
    private ToolMaterialPresets(){}

    /** Vanilla-ish bands for metals/alloys. */
    public static ToolMaterialSpec forMetal(HarvestTier t) {
        return switch (t) {
            case STONE     -> new ToolMaterialSpec(131, 4.0f, 1.0f, 5,
                    rl("minecraft:incorrect_for_stone_tool"), rl("minecraft:air"));
            case IRON      -> new ToolMaterialSpec(250, 6.0f, 2.0f, 14,
                    rl("minecraft:incorrect_for_iron_tool"), rl("minecraft:air"));
            case DIAMOND   -> new ToolMaterialSpec(1561, 8.0f, 3.0f, 10,
                    rl("minecraft:incorrect_for_diamond_tool"), rl("minecraft:air"));
            case NETHERITE -> new ToolMaterialSpec(2031, 9.0f, 4.0f, 15,
                    rl("minecraft:incorrect_for_netherite_tool"), rl("minecraft:air"));
        };
    }

    /** Slightly glassy but sharp & enchanting: good for gems. */
    public static ToolMaterialSpec forGem(HarvestTier t) {
        // generally higher enchantability, similar or slightly lower durability than metals of same tier
        return switch (t) {
            case STONE     -> new ToolMaterialSpec(180, 4.5f, 1.0f, 18,
                    rl("minecraft:incorrect_for_stone_tool"), rl("minecraft:air"));
            case IRON      -> new ToolMaterialSpec(400, 6.5f, 2.0f, 20,
                    rl("minecraft:incorrect_for_iron_tool"), rl("minecraft:air"));
            case DIAMOND   -> new ToolMaterialSpec(1400, 8.5f, 3.0f, 22,
                    rl("minecraft:incorrect_for_diamond_tool"), rl("minecraft:air"));
            case NETHERITE -> new ToolMaterialSpec(1800, 9.0f, 3.5f, 22,
                    rl("minecraft:incorrect_for_netherite_tool"), rl("minecraft:air"));
        };
    }

    /** Crystals (optional): faster, fragile, high enchanting. */
    public static ToolMaterialSpec forCrystal(HarvestTier t) {
        return switch (t) {
            case STONE     -> new ToolMaterialSpec(100, 5.0f, 0.0f, 20, rl("minecraft:incorrect_for_stone_tool"), rl("minecraft:air"));
            case IRON      -> new ToolMaterialSpec(220, 6.8f, 1.5f, 22, rl("minecraft:incorrect_for_iron_tool"), rl("minecraft:air"));
            case DIAMOND   -> new ToolMaterialSpec(900, 8.8f, 2.5f, 24, rl("minecraft:incorrect_for_diamond_tool"), rl("minecraft:air"));
            case NETHERITE -> new ToolMaterialSpec(1200, 9.2f, 3.0f, 24, rl("minecraft:incorrect_for_netherite_tool"), rl("minecraft:air"));
        };
    }

    private static Identifier rl(String s){ return Identifier.tryParse(s); }
}
