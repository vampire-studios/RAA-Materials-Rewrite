// api/FormRegistry.java
package net.vampirestudios.raaMaterials.api;

import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FormRegistry {
	private static final Map<ResourceLocation, MaterialForm> FORMS = new LinkedHashMap<>();

	private FormRegistry() {
	}

	public static MaterialForm register(MaterialForm form) {
		return FORMS.put(form.id(), form);
	}

	public static MaterialForm get(ResourceLocation id) {
		return FORMS.get(id);
	}

	public static Collection<MaterialForm> all() {
		return FORMS.values();
	}
}
