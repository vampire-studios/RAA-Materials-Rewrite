// MaterialRegistry.java
package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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

	public static void clear() {
		MAP.clear();
	}

	public static MaterialSet get(ResourceKey<Level> dim) {
		return MAP.get(dim);
	}

	public static Optional<MaterialDef> byIndex(ServerLevel level, int idx) {
		var set = get(level.dimension());
		return set != null ? set.byIndex(idx) : Optional.empty();
	}

	public static Optional<Integer> indexOf(ServerLevel level, Identifier id) {
		var set = get(level.dimension());
		return set != null ? set.indexOf(id) : Optional.empty();
	}

	public static Optional<MaterialDef> byId(Identifier id) {
		// Server path first (avoids touching client class on dedicated servers)
		for (MaterialSet set : MAP.values()) {
			var def = set.byId(id);
			if (def.isPresent()) return def;
		}
		// Client fallback — no-op on dedicated server (empty cache)
		return ClientMaterialCache.byRL(id);
	}

	/**
	 * Dual-path index lookup that works on both logical sides without a {@link Level}.
	 * On the server searches all registered material sets; on the client falls back to
	 * {@link ClientMaterialCache}. Use the level-aware overload when a level is available.
	 */
	public static Optional<MaterialDef> byIndexAny(int idx) {
		for (MaterialSet set : MAP.values()) {
			var def = set.byIndex(idx);
			if (def.isPresent()) return def;
		}
		return ClientMaterialCache.byIndex(idx);
	}

	public static List<MaterialDef> all(ServerLevel level) {
		var set = get(level.dimension());
		if (set == null) return List.of();
		return set.all();
	}

	public static Optional<MaterialDef> byIndex(Level level, int idx) {
		if (level instanceof ServerLevel sl) return byIndex(sl, idx);
		return ClientMaterialCache.byIndex(idx);
	}

	public static Optional<Integer> indexOf(Level level, Identifier id) {
		if (level instanceof ServerLevel sl) return indexOf(sl, id);
		return ClientMaterialCache.indexOf(id);
	}
}
