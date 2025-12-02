// src/main/java/your/mod/palette/PaletteRules.java
package net.vampirestudios.raaMaterials.palette;

public record PaletteRules(long seed,
						   String scheme,         // "METAL", "GEM", "STONE", "WOOD", "METAL_SHIFT"
						   float hueBase,         // 0..360 - base hue
						   float hueJitterDeg,    // degrees - hue variation
						   float satBase,         // 0..1 - base saturation
						   float satVar,          // 0..1 - saturation variation
						   float valBase,         // 0..1 - base value/brightness
						   float valVar,          // 0..1 - value variation
						   boolean enforceContrast, // whether to enforce contrast between stops
						   float roughness,       // 0.0 (smooth) to 1.0 (rough) - affects highlight spread
						   float anisotropy,      // 0.0 (isotropic) to 1.0 (anisotropic) - directional properties
						   float speckle,         // 0.0 to 1.0 - adds noise/grain effect
						   float grain,           // 0.0 to 1.0 - adds directional texture
						   float temperature,     // -1.0 (cool) to 1.0 (warm) - color temperature shift
						   float brightness,      // -1.0 (darker) to 1.0 (brighter) - overall brightness adjustment
						   String preset,         // Optional preset name for predefined materials
						   String harmony         // Optional color harmony type
) {
	// Default constructor
	public PaletteRules {
		// Validate parameters
		hueBase = clamp(hueBase, 0f, 360f);
		hueJitterDeg = Math.abs(hueJitterDeg);
		satBase = clamp(satBase, 0f, 1f);
		satVar = clamp(satVar, 0f, 1f);
		valBase = clamp(valBase, 0f, 1f);
		valVar = clamp(valVar, 0f, 1f);
		roughness = clamp(roughness, 0f, 1f);
		anisotropy = clamp(anisotropy, 0f, 1f);
		speckle = clamp(speckle, 0f, 1f);
		grain = clamp(grain, 0f, 1f);
		temperature = clamp(temperature, -1f, 1f);
		brightness = clamp(brightness, -1f, 1f);
	}

	// Convenience constructors for backward compatibility
	public PaletteRules(long seed, String scheme, float hueBase, float hueJitterDeg,
						float satBase, float satVar, float valBase, float valVar,
						boolean enforceContrast) {
		this(seed, scheme, hueBase, hueJitterDeg, satBase, satVar, valBase, valVar,
				enforceContrast, 0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, null, null);
	}

	public PaletteRules(long seed, String scheme, float hueBase, float hueJitterDeg,
						float satBase, float satVar, float valBase, float valVar,
						boolean enforceContrast, float roughness) {
		this(seed, scheme, hueBase, hueJitterDeg, satBase, satVar, valBase, valVar,
				enforceContrast, roughness, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, null, null);
	}

	// Getters that match the original interface
	public float hueJitter() { return hueJitterDeg; }
	public long seed() { return seed; }
	public boolean enforceContrast() { return enforceContrast; }
	public float getRoughness() { return roughness; }

	// Optional parameters with defaults
	public String preset() { return preset != null ? preset : ""; }
	public String harmony() { return harmony != null ? harmony : "MONOCHROMATIC"; }

	// Builder pattern
	public static class Builder {
		private long seed = 0;
		private String scheme = "METAL";
		private float hueBase = 0f;
		private float hueJitterDeg = 0f;
		private float satBase = 0.5f;
		private float satVar = 0.1f;
		private float valBase = 0.5f;
		private float valVar = 0.1f;
		private boolean enforceContrast = false;
		private float roughness = 0.5f;
		private float anisotropy = 0.0f;
		private float speckle = 0.0f;
		private float grain = 0.0f;
		private float temperature = 0.0f;
		private float brightness = 0.0f;
		private String preset = null;
		private String harmony = null;

		public Builder seed(long seed) { this.seed = seed; return this; }
		public Builder scheme(String scheme) { this.scheme = scheme; return this; }
		public Builder hueBase(float hueBase) { this.hueBase = hueBase; return this; }
		public Builder hueJitter(float jitter) { this.hueJitterDeg = jitter; return this; }
		public Builder satBase(float satBase) { this.satBase = satBase; return this; }
		public Builder satVar(float satVar) { this.satVar = satVar; return this; }
		public Builder valBase(float valBase) { this.valBase = valBase; return this; }
		public Builder valVar(float valVar) { this.valVar = valVar; return this; }
		public Builder enforceContrast(boolean enforce) { this.enforceContrast = enforce; return this; }
		public Builder roughness(float roughness) { this.roughness = roughness; return this; }
		public Builder anisotropy(float anisotropy) { this.anisotropy = anisotropy; return this; }
		public Builder speckle(float speckle) { this.speckle = speckle; return this; }
		public Builder grain(float grain) { this.grain = grain; return this; }
		public Builder temperature(float temperature) { this.temperature = temperature; return this; }
		public Builder brightness(float brightness) { this.brightness = brightness; return this; }
		public Builder preset(String preset) { this.preset = preset; return this; }
		public Builder harmony(String harmony) { this.harmony = harmony; return this; }

		public PaletteRules build() {
			return new PaletteRules(seed, scheme, hueBase, hueJitterDeg, satBase, satVar,
					valBase, valVar, enforceContrast, roughness, anisotropy, speckle,
					grain, temperature, brightness, preset, harmony);
		}
	}

	// Preset factory methods
	public static class Presets {
		public static Builder bronze() {
			return new Builder()
					.scheme("COPPER")
					.hueBase(35f)
					.satBase(0.82f)
					.valBase(0.78f)
					.roughness(0.35f)
					.temperature(0.25f)
					.brightness(0.1f);
		}

		public static Builder copper() {
			return new Builder()
					.scheme("COPPER")
					.hueBase(40f)
					.satBase(0.85f)
					.valBase(0.80f)
					.roughness(0.3f)
					.temperature(0.3f)
					.brightness(0.15f);
		}

		public static Builder steel() {
			return new Builder()
					.scheme("METAL")
					.hueBase(220f)
					.satBase(0.2f)
					.valBase(0.8f)
					.roughness(0.6f);
		}

		public static Builder gold() {
			return new Builder()
					.scheme("METAL_SHIFT")
					.hueBase(45f)
					.satBase(0.9f)
					.valBase(0.9f)
					.roughness(0.2f)
					.temperature(0.3f);
		}

		public static Builder emerald() {
			return new Builder()
					.scheme("GEM")
					.hueBase(140f)
					.satBase(0.95f)
					.valBase(0.7f)
					.brightness(0.1f);
		}

		public static Builder granite() {
			return new Builder()
					.scheme("STONE")
					.hueBase(15f)
					.satBase(0.25f)
					.valBase(0.6f)
					.speckle(0.2f)
					.grain(0.1f);
		}

		public static Builder oak() {
			return new Builder()
					.scheme("WOOD")
					.hueBase(30f)
					.satBase(0.4f)
					.valBase(0.65f)
					.grain(0.15f);
		}
	}

	// Utility method for clamping values
	private static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}
}
