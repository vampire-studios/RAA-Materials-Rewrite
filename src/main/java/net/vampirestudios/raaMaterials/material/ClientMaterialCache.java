package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ClientMaterialCache {
	private static MaterialSet set = new MaterialSet(List.of());
	private static Map<Identifier, Integer> indexCache = new HashMap<>();

	public static void load(MaterialSet s) {
		set = s;
		indexCache = buildIndex(s.all());
	}

	public static List<MaterialDef> all() {
		return set.all();
	}

	public static Optional<MaterialDef> byRL(Identifier rl) {
		var idx = indexCache.get(rl);
		if (idx == null) return Optional.empty();
		return set.byIndex(idx);
	}

	public static Optional<MaterialDef> byIndex(int idx) {
		return set.byIndex(idx);
	}

	public static Optional<Integer> indexOf(Identifier rl) {
		return Optional.ofNullable(indexCache.get(rl));
	}

	private static Map<Identifier, Integer> buildIndex(List<MaterialDef> all) {
		var map = new HashMap<Identifier, Integer>(all.size() * 2);
		for (int i = 0; i < all.size(); i++) {
			map.put(all.get(i).nameInformation().id(), i);
		}
		return map;
	}
}
