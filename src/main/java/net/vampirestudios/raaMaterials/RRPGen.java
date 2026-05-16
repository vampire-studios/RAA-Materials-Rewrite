// RRPGen.java
package net.vampirestudios.raaMaterials;

import net.vampirestudios.arrp.api.RRPCallback;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.recipe.ParametricCookingRecipe;
import net.vampirestudios.raaMaterials.recipe.RRPRecipes;

import java.nio.charset.StandardCharsets;
import java.util.List;

public final class RRPGen {
    public static final RuntimeResourcePack PACK = RuntimeResourcePack.create("raa_materials:runtime");
    private static boolean initialized;

	/**
	 * Registers the ARRP callback and generates all static (non-material-dependent) content.
	 * <p>
	 * Generates one stub JSON per parametric cooking / stonecutting recipe kind so that
	 * Minecraft's recipe manager instantiates those in-memory recipe objects.
	 * Per-material crafting recipes are <em>not</em> stubbed here; they are injected by
	 * {@link #populateRecipes} and picked up after the server-side reload triggered from
	 * {@code SERVER_STARTED}.
	 * <p>
	 * Safe to call on both logical sides. Must run before the first resource reload.
	 */
	public static void init() {
		if (initialized) return;
		initialized = true;
		ARRPGenerationHelper.generateParametricBlockLootTables(PACK);
		generateStaticRecipeStubs();
		RRPCallback.AFTER_VANILLA.register(a -> a.add(PACK));
	}

	/**
	 * Populates the pack with material-dependent recipe-book display data.
	 * Call this after the material list is known:
	 * <ul>
	 *   <li>Server: in the {@code SERVER_STARTED} handler</li>
	 *   <li>Client: in {@code MaterialsAssets.buildAll()}</li>
	 * </ul>
	 * Safe to call multiple times — same-path entries simply overwrite.
	 */
	public static void populateRecipes(List<MaterialDef> materials) {
		RRPRecipes.recipes(PACK, materials);
	}

	// -------------------------------------------------------------------------

	/**
	 * Adds a minimal {@code {"type":"..."}} JSON stub for every parametric cooking and
	 * stonecutting recipe kind.  These stubs cause Minecraft's recipe manager to
	 * instantiate the in-memory recipe objects registered in
	 * {@link net.vampirestudios.raaMaterials.recipe.ParametricRecipes}.
	 * <p>
	 * Crafting recipes are NOT stubbed here — they are generated per-material by
	 * {@link net.vampirestudios.raaMaterials.recipe.RRPRecipes} and loaded via the
	 * resource-reload that {@link RAAMaterials} triggers after materials are known.
	 */
	private static void generateStaticRecipeStubs() {
		for (var kind : ParametricCookingRecipe.Kind.values()) {
			addRecipeStub(kind.id(), "raa_materials:" + kind.id());
		}
	}

	private static void addRecipeStub(String path, String type) {
		PACK.addData(
				RAAMaterials.id("recipe/" + path + ".json"),
				("{\"type\":\"" + type + "\"}").getBytes(StandardCharsets.UTF_8)
		);
	}
}
