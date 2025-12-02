// ClientMaterialCache.java
package net.vampirestudios.raaMaterials.material;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public final class ClientMaterialCache {
	private static MaterialSet set = new MaterialSet(List.of());

	public static void load(MaterialSet s) {
		set = s;
	}

	public static List<MaterialDef> all() {
		return set.all();
	}

	public static Optional<MaterialDef> byRL(ResourceLocation rl) {
		return set.all().stream().filter(m -> m.id().equals(rl)).findFirst();
	}

	public static Optional<MaterialDef> byIndex(int idx) {
		return set.byIndex(idx);
	}

	public static Optional<Integer> indexOf(ResourceLocation rl) {
		var all = set.all();
		for (int i = 0; i < all.size(); i++) {
			if (all.get(i).id().equals(rl)) {
				return Optional.of(i);
			}
		}
		return Optional.empty();
	}
}
