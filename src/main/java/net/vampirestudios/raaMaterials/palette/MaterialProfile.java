package net.vampirestudios.raaMaterials.palette;

public record MaterialProfile(
		PaletteArchetype archetype,
		float roughness,
		float metallic,
		float oxidation,
		float translucency,
		float emissive,
		float grain,
		float contrast,
		float hueShift,
		float temperature,
		float brightness,
		float speckle
) {
	public static MaterialProfile defaults(PaletteArchetype archetype) {
		return switch (archetype) {
			case METAL -> new MaterialProfile(archetype, 0.35f, 1.00f, 0.20f, 0.00f, 0.00f, 0.25f, 1.20f, 8f, 0.10f, 1.00f, 0.15f);
			case GEM   -> new MaterialProfile(archetype, 0.12f, 0.00f, 0.00f, 0.45f, 0.15f, 0.08f, 1.45f, 18f, -0.05f, 1.08f, 0.20f);
			case STONE -> new MaterialProfile(archetype, 0.80f, 0.00f, 0.08f, 0.00f, 0.00f, 0.65f, 0.85f, 5f, -0.05f, 0.92f, 0.35f);
			case WOOD  -> new MaterialProfile(archetype, 0.65f, 0.00f, 0.12f, 0.00f, 0.00f, 0.85f, 1.00f, 10f, 0.15f, 0.95f, 0.25f);
		};
	}
}