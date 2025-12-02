package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

// ClientMaterialAssetsCache.java
public final class ClientMaterialAssetsCache {
	private static final java.util.Map<ResourceLocation, MaterialAssetsDef> MAP = new java.util.HashMap<>();

	public static void put(MaterialAssetsDef def) {
		MAP.put(def.materialId(), def);
	}

	public static Optional<MaterialAssetsDef> get(ResourceLocation id) {
		return Optional.ofNullable(MAP.get(id));
	}

	public static void clear() {
		MAP.clear();
	}
}
