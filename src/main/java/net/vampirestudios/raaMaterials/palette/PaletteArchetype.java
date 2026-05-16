package net.vampirestudios.raaMaterials.palette;

public enum PaletteArchetype {
	METAL,
	GEM,
	STONE,
	WOOD;

	public static PaletteArchetype fromScheme(String scheme) {
		if (scheme == null) return METAL;

		return switch (scheme.toLowerCase()) {
			case "gem", "crystal" -> GEM;
			case "stone", "rock" -> STONE;
			case "wood", "bark" -> WOOD;
			default -> METAL;
		};
	}
}