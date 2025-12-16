// MaterialRegistry.java
package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MaterialRegistry {
	private static final Map<ResourceKey<Level>, MaterialSet> MAP = new HashMap<>();

	public static void put(ResourceKey<Level> dim, MaterialSet set) {
		MAP.put(dim, set);
	}

	public static MaterialSet get(ResourceKey<Level> dim) {
		return MAP.get(dim);
	}

	// --- Helpers for MAT property lookups ---

	/** Get the material definition by its index for this level's dimension. */
	public static Optional<MaterialDef> byIndex(ServerLevel level, int idx) {
		var set = get(level.dimension());
		if (set == null) return Optional.empty();
		var all = set.all();
		return (idx >= 0 && idx < all.size()) ? Optional.of(all.get(idx)) : Optional.empty();
	}

	/** Resolve the index of a given material ResourceLocation within this level's dimension set. */
	public static Optional<Integer> indexOf(ServerLevel level, ResourceLocation id) {
		var set = get(level.dimension());
		if (set == null) return Optional.empty();
		var all = set.all();
		for (int i = 0; i < all.size(); i++) {
			if (all.get(i).nameInformation().id().equals(id)) {
				return Optional.of(i);
			}
		}
		return Optional.empty();
	}

	public static List<MaterialDef> all(ServerLevel level) {
		var set = get(level.dimension());
		if (set == null) return List.of();
		var all = set.all();
		if (all.isEmpty()) return List.of();
		return all;
	}

	// --- NEW: level-agnostic overloads (server → registry, client → cache) ---
	public static Optional<MaterialDef> byIndex(Level level, int idx) {
		if (level instanceof ServerLevel sl) return byIndex(sl, idx);
		return ClientMaterialCache.byIndex(idx); // client cache
	}

	public static Optional<Integer> indexOf(Level level, ResourceLocation id) {
		if (level instanceof ServerLevel sl) return indexOf(sl, id);
		return ClientMaterialCache.indexOf(id); // client cache
	}
}
