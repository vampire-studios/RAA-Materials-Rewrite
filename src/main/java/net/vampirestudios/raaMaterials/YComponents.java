// YComponents.java
package net.vampirestudios.raaMaterials;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class YComponents {
	public static DataComponentType<Identifier> MATERIAL;

	public static void init() {
		MATERIAL = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
				RAAMaterials.id("material"),
				DataComponentType.<Identifier>builder().persistent(Identifier.CODEC).build());
	}
}
