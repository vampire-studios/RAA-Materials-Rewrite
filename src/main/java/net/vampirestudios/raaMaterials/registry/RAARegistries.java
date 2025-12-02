package net.vampirestudios.raaMaterials.registry;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialKind;

// RAARegistries.java
public final class RAARegistries {
    public static final ResourceKey<Registry<MaterialKind>> MATERIALS = createRegistryKey("raa_materials/kinds");
    public static final ResourceKey<Registry<Form>> FORMS = createRegistryKey("raa_materials/forms");

    public static void init() {
        DynamicRegistries.register(MATERIALS, MaterialKind.CODEC);
        DynamicRegistries.register(FORMS, Form.CODEC);
    }

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String string) {
        return ResourceKey.createRegistryKey(RAAMaterials.id(string));
    }
}
