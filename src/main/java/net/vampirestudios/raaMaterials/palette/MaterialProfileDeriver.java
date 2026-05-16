package net.vampirestudios.raaMaterials.palette;

import java.util.Random;

public final class MaterialProfileDeriver {
	private MaterialProfileDeriver() {}

	public static MaterialProfile derive(String materialKey, PaletteRules rules) {
		PaletteArchetype archetype = PaletteArchetype.fromScheme(rules.scheme());
		Random rng = new Random(mix(materialKey.hashCode(), rules.seed()));

		MaterialProfile base = MaterialProfile.defaults(archetype);

		return new MaterialProfile(
				archetype,
				jitter(base.roughness(), rng, 0.18f),
				jitter(base.metallic(), rng, 0.08f),
				jitter(base.oxidation(), rng, 0.20f),
				jitter(base.translucency(), rng, 0.12f),
				jitter(base.emissive(), rng, 0.10f),
				jitter(base.grain(), rng, 0.22f),
				jitter(base.contrast(), rng, 0.20f),
				base.hueShift() + range(rng, -6f, 6f),
				base.temperature() + range(rng, -0.20f, 0.20f),
				jitter(base.brightness(), rng, 0.12f),
				jitter(base.speckle(), rng, 0.18f)
		);
	}

	public static PaletteRules applyToRules(PaletteRules rules, MaterialProfile profile) {
		return new PaletteRules.Builder()
				.seed(rules.seed())
				.scheme(rules.scheme())
				.hueBase(rules.hueBase())
				.hueJitter(rules.hueJitter() + profile.hueShift())
				.satBase(clamp01(rules.satBase() * saturationMultiplier(profile)))
				.satVar(clamp01(rules.satVar() + profile.speckle() * 0.10f))
				.valBase(clamp01(rules.valBase() * profile.brightness()))
				.valVar(clamp01(rules.valVar() * profile.contrast()))
				.roughness(profile.roughness())
				.grain(profile.grain())
				.speckle(profile.speckle())
				.temperature(profile.temperature())
				.brightness(profile.brightness())
				.enforceContrast(true)
				.build();
	}

	private static float saturationMultiplier(MaterialProfile profile) {
		return switch (profile.archetype()) {
			case METAL -> 0.70f + profile.oxidation() * 0.45f;
			case GEM -> 1.25f + profile.translucency() * 0.35f;
			case STONE -> 0.55f + profile.speckle() * 0.25f;
			case WOOD -> 0.90f + profile.temperature() * 0.20f;
		};
	}

	private static long mix(long a, long b) {
		long x = a ^ b;
		x ^= x >>> 33;
		x *= 0xff51afd7ed558ccdL;
		x ^= x >>> 33;
		x *= 0xc4ceb9fe1a85ec53L;
		x ^= x >>> 33;
		return x;
	}

	private static float jitter(float base, Random rng, float amount) {
		return clamp01(base + range(rng, -amount, amount));
	}

	private static float range(Random rng, float min, float max) {
		return min + rng.nextFloat() * (max - min);
	}

	private static float clamp01(float v) {
		return Math.max(0f, Math.min(1f, v));
	}
}