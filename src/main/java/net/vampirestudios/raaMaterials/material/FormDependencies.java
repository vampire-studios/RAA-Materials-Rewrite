package net.vampirestudios.raaMaterials.material;

import net.vampirestudios.raaMaterials.RAAMaterials;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validates that a material's form list satisfies implied dependencies.
 * E.g., SLAB requires BLOCK; NUGGET implies INGOT should also be present.
 * Violations are logged as warnings (not exceptions) so world generation is never blocked.
 */
public final class FormDependencies {
	private FormDependencies() {}

	/** Each entry: if the key form is present, all listed forms should also be present. */
	private static final Map<Form, List<Form>> REQUIRES = Map.ofEntries(
			Map.entry(Form.SLAB,           List.of(Form.BLOCK)),
			Map.entry(Form.STAIRS,         List.of(Form.BLOCK)),
			Map.entry(Form.WALL,           List.of(Form.BLOCK)),
			Map.entry(Form.NUGGET,         List.of(Form.INGOT)),
			Map.entry(Form.RAW_BLOCK,      List.of(Form.RAW)),
			Map.entry(Form.RAW,            List.of(Form.ORE)),
			Map.entry(Form.SMOOTH,         List.of(Form.SANDSTONE)),
			Map.entry(Form.CUT,            List.of(Form.SANDSTONE)),
			Map.entry(Form.CRYSTAL_BRICKS, List.of(Form.CRYSTAL)),
			Map.entry(Form.BUD_SMALL,      List.of(Form.BUDDING)),
			Map.entry(Form.BUD_MEDIUM,     List.of(Form.BUDDING)),
			Map.entry(Form.BUD_LARGE,      List.of(Form.BUDDING)),
			Map.entry(Form.CLUSTER,        List.of(Form.BUDDING))
	);

	/**
	 * Returns a copy of {@code forms} with any missing dependencies automatically added.
	 * Logs at DEBUG level for each auto-added form so it stays quiet in normal play.
	 */
	public static List<Form> resolve(MaterialDef.NameInformation name, List<Form> forms) {
		var result = new ArrayList<>(forms);
		boolean changed = true;
		while (changed) {
			changed = false;
			for (var entry : REQUIRES.entrySet()) {
				if (!result.contains(entry.getKey())) continue;
				for (var dep : entry.getValue()) {
					if (!result.contains(dep)) {
						RAAMaterials.LOGGER.debug("[RAA] Material '{}' auto-added {} (required by {})",
								name.id(), dep, entry.getKey());
						result.add(dep);
						changed = true;
					}
				}
			}
		}
		return result;
	}
}
