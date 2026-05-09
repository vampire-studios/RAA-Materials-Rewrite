// RRPGen.java
package net.vampirestudios.raaMaterials;

import net.vampirestudios.arrp.api.RRPCallback;
import net.vampirestudios.arrp.api.RuntimeResourcePack;

public final class RRPGen {
    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("raa_materials:runtime");
    private static boolean initialized;

	public static void init() {
		if (initialized) return;
		initialized = true;
		ARRPGenerationHelper.generateParametricBlockLootTables(PACK);
		ARRPGenerationHelper.generateParametricRecipes(PACK);
		RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
	}
}
