// YComponents.java
package net.vampirestudios.raaMaterials;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class YComponents {
	public static DataComponentType<ResourceLocation> MATERIAL;

	public static void init() {
		MATERIAL = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
				RAAMaterials.id("material"),
				DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).build());
	}
}
