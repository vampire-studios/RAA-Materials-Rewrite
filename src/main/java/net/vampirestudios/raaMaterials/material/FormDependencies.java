package net.vampirestudios.raaMaterials.material;

import net.vampirestudios.raaMaterials.RAAMaterials;

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

	public static void validate(MaterialDef.NameInformation name, List<Form> forms) {
		for (var entry : REQUIRES.entrySet()) {
			var form = entry.getKey();
			if (!forms.contains(form)) continue;
			for (var dep : entry.getValue()) {
				if (!forms.contains(dep)) {
					RAAMaterials.LOGGER.warn("[RAA] Material '{}' has {} but is missing required {}",
							name.id(), form, dep);
				}
			}
		}
	}
}
