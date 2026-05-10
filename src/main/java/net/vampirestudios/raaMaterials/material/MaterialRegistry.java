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
		var clientDef = ClientMaterialCache.byRL(id);
		if (clientDef.isPresent()) return clientDef;

		for (MaterialSet set : MAP.values()) {
			var def = set.byId(id);
			if (def.isPresent()) return def;
		}
		return Optional.empty();
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
