package net.vampirestudios.raaMaterials.palette;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import static java.lang.Math.min;
import static net.vampirestudios.raaMaterials.palette.OKLCh.*;

public final class PaletteDeriver {
	public static final float METAL_SHADOW_SATURATION = 0.55f;
	public static final float METAL_HIGHLIGHT_VALUE = 0.98f;
	private static final float METAL_ROUGHNESS = 0.5f; // 0.0 (smooth) to 1.0 (rough)
	private static final float METAL_ANISOTROPY = 0.0f; // 0.0 (isotropic) to 1.0 (anisotropic)
	private static final float MIN_CONTRAST_RATIO = 4.5f;

	// Constants for material-specific values
	private static final float GEM_DEEP_VALUE = 0.30f;
	private static final float GEM_DEEP_VAR = 0.08f;
	private static final float GEM_BODY_VALUE = 0.55f;
	private static final float GEM_BODY_VAR = 0.08f;
	private static final float GEM_BRIGHT_VALUE = 0.78f;
	private static final float GEM_BRIGHT_VAR = 0.06f;
	private static final float GEM_GLINT_VALUE = 0.96f;
	private static final float GEM_GLINT_SAT = 0.70f;

	private static final float STONE_DARK_VALUE = 0.24f;
	private static final float STONE_BODY_VALUE = 0.46f;
	private static final float STONE_LIGHT_VALUE = 0.66f;
	private static final float STONE_HIGHLIGHT_VALUE = 0.82f;

	private static final float WOOD_BARK_VALUE = 0.28f;
	private static final float WOOD_BODY_VALUE = 0.58f;
	private static final float WOOD_BLEACHED_VALUE = 0.82f;
	// ---- tunables up top (one place) ----
	private static final float WOOD_SPREAD_DEG = 16f;   // total hue swing across the ramp
	private static final float WOOD_COOL_SHIFT_DEG = -18f;  // cool shadow tilt (negative = toward cyan/olive)
	private static final float WOOD_WARM_SHIFT_DEG = 14f;  // warm highlight tilt (toward yellow/orange)
	private static final float WOOD_UNDERTONE_DELTA = -55f;  // "olive" axis relative to base (negative leans green)
	private static final float U_SHADOW_MIX = 0.32f; // mix factor to undertone on shadows (0..1)
	private static final float U_MID_MIX = 0.16f; // mix factor on mids (0..1)


	private PaletteDeriver() {
	}

	public static Palette deriveWithPreset(String materialKey, PaletteRules rules) {
		// Check if a preset is specified and apply it
		String preset = rules.preset().toLowerCase();

		if (preset != null && !preset.isEmpty()) {
			PaletteRules.Builder builder = new PaletteRules.Builder()
					.seed(rules.seed())
					.scheme(rules.scheme())
					.hueBase(rules.hueBase())
					.hueJitter(rules.hueJitterDeg())
					.satBase(rules.satBase())
					.satVar(rules.satVar())
					.valBase(rules.valBase())
					.valVar(rules.valVar())
					.enforceContrast(rules.enforceContrast())
					.roughness(rules.roughness())
					.anisotropy(rules.anisotropy())
					.speckle(rules.speckle())
					.grain(rules.grain())
					.temperature(rules.temperature())
					.brightness(rules.brightness())
					.harmony(rules.harmony());

			// Apply preset values, but allow individual overrides
			switch (preset) {
				case "copper" -> {
					if (rules.hueBase() == 0f) builder.hueBase(40f);
					if (rules.satBase() == 0.5f) builder.satBase(0.85f);
					if (rules.valBase() == 0.5f) builder.valBase(0.80f);
					if (rules.roughness() == 0.5f) builder.roughness(0.3f);
					if (rules.temperature() == 0.0f) builder.temperature(0.3f);
					if (rules.brightness() == 0.0f) builder.brightness(0.15f);
				}
				case "bronze" -> {
					if (rules.hueBase() == 0f) builder.hueBase(35f);
					if (rules.satBase() == 0.5f) builder.satBase(0.82f);
					if (rules.valBase() == 0.5f) builder.valBase(0.78f);
					if (rules.roughness() == 0.5f) builder.roughness(0.35f);
					if (rules.temperature() == 0.0f) builder.temperature(0.25f);
					if (rules.brightness() == 0.0f) builder.brightness(0.1f);
				}
				case "gold" -> {
					if (rules.hueBase() == 0f) builder.hueBase(50f);
					if (rules.satBase() == 0.5f) builder.satBase(0.9f);
					if (rules.valBase() == 0.5f) builder.valBase(0.9f);
					if (rules.roughness() == 0.5f) builder.roughness(0.2f);
					if (rules.temperature() == 0.0f) builder.temperature(0.4f);
					if (rules.brightness() == 0.0f) builder.brightness(0.2f);
				}
				case "steel" -> {
					if (rules.hueBase() == 0f) builder.hueBase(220f);
					if (rules.satBase() == 0.5f) builder.satBase(0.2f);
					if (rules.valBase() == 0.5f) builder.valBase(0.8f);
					if (rules.roughness() == 0.5f) builder.roughness(0.6f);
					if (rules.temperature() == 0.0f) builder.temperature(-0.1f);
				}
				case "emerald" -> {
					if (rules.hueBase() == 0f) builder.hueBase(140f);
					if (rules.satBase() == 0.5f) builder.satBase(0.95f);
					if (rules.valBase() == 0.5f) builder.valBase(0.7f);
					if (rules.brightness() == 0.0f) builder.brightness(0.1f);
				}
			}

			// Apply any explicit overrides from the rules
			return derive(materialKey, builder.build());
		}

		// No preset, use rules as-is
		return derive(materialKey, rules);
	}

	// Update the main derive method to use presets
	public static Palette derive(String materialKey, PaletteRules rules) {
		// First apply preset if specified
		PaletteRules effectiveRules = rules;
		if (rules.preset() != null && !rules.preset().isEmpty()) {
			effectiveRules = applyPreset(rules);
		}

		return switch (effectiveRules.scheme().toUpperCase()) {
			case "METAL" -> deriveMetal(materialKey, effectiveRules);
			case "COPPER" -> deriveCopper(materialKey, effectiveRules);
			case "METAL_SHIFT" -> deriveMetalHueShift(materialKey, effectiveRules);
			case "GEM" -> deriveGem(materialKey, effectiveRules);
			case "STONE" -> deriveStone(materialKey, effectiveRules);
			case "WOOD" -> deriveWood(materialKey, effectiveRules);
			case "BARK" -> deriveBark(materialKey, effectiveRules);
			default -> deriveGeneric(materialKey, effectiveRules, ColorHarmony.ANALOGOUS);
		};
	}

	// Helper method to apply presets
	private static PaletteRules applyPreset(PaletteRules rules) {
		String preset = rules.preset().toLowerCase();
		PaletteRules.Builder builder = new PaletteRules.Builder()
				.seed(rules.seed())
				.scheme(rules.scheme())
				.hueBase(rules.hueBase())
				.hueJitter(rules.hueJitterDeg())
				.satBase(rules.satBase())
				.satVar(rules.satVar())
				.valBase(rules.valBase())
				.valVar(rules.valVar())
				.enforceContrast(rules.enforceContrast())
				.roughness(rules.roughness())
				.anisotropy(rules.anisotropy())
				.speckle(rules.speckle())
				.grain(rules.grain())
				.temperature(rules.temperature())
				.brightness(rules.brightness())
				.harmony(rules.harmony())
				.preset(rules.preset());

		switch (preset) {
			case "copper" -> {
				builder.hueBase(40f)
						.satBase(0.85f)
						.valBase(0.80f)
						.roughness(0.3f)
						.temperature(0.3f)
						.brightness(0.15f);
			}
			case "bronze" -> {
				builder.hueBase(35f)
						.satBase(0.82f)
						.valBase(0.78f)
						.roughness(0.35f)
						.temperature(0.25f)
						.brightness(0.1f);
			}
			case "gold" -> {
				builder.hueBase(50f)
						.satBase(0.9f)
						.valBase(0.9f)
						.roughness(0.2f)
						.temperature(0.4f)
						.brightness(0.2f);
			}
			case "steel" -> {
				builder.hueBase(220f)
						.satBase(0.2f)
						.valBase(0.8f)
						.roughness(0.6f)
						.temperature(-0.1f);
			}
			case "cold" -> {
				builder.hueBase(200f)        // Blue/cyan base
						.satBase(0.4f)        // Moderate saturation
						.valBase(0.75f)       // Medium brightness
						.roughness(0.4f)      // Medium roughness
						.temperature(-0.4f)   // Very cool
						.brightness(-0.2f);   // Darker overall
			}
		}

		return builder.build();
	}

	public static Palette deriveGeneric(String materialKey, PaletteRules rules, ColorHarmony harmony) {
		long seed = mix(seed(materialKey), rules.seed());
		var r = new SplittableRandom(seed);

		float baseHue = rules.hueBase();
		float adjustedHue = wrap(baseHue + jitter(r, rules.hueJitterDeg()), 0f, 360f);
		adjustedHue = wrap(adjustedHue + (rules.temperature() * 15f), 0f, 360f);

		// Generate harmonious hues based on harmony type
		float[] H = generateHarmony(adjustedHue, harmony, r, rules.hueJitterDeg());

		List<Palette.Stop> stops = new ArrayList<>(6);
		float[] pos = new float[]{0f, 0.25f, 0.45f, 0.65f, 0.85f, 1f};

		for (int i = 0; i < pos.length; i++) {
			float sat = clamp(rules.satBase() + jitter(r, rules.satVar()), 0f, 1f);
			float val = clamp(rules.valBase() + jitter(r, rules.valVar()), 0f, 1f);
			val = clamp(val + rules.brightness() * 0.2f, 0f, 1f);

			int argb = hsvToArgbSafe(H[min(i, H.length - 1)], sat, val);
			stops.add(new Palette.Stop(pos[i], argb));
		}

		if (rules.enforceContrast()) {
			ensureContrast(stops);
		}

		return new Palette(materialKey, stops, Palette.Mode.BY_LUMINANCE, Palette.Dither.NONE, Palette.Quantize.UNIQUE);
	}

	private static float[] generateHarmony(float baseHue, ColorHarmony harmony, SplittableRandom r, float jitter) {
		return switch (harmony) {
			case MONOCHROMATIC -> new float[]{
					baseHue,
					wrap(baseHue + jitter(r, jitter * 0.3f), 0, 360),
					wrap(baseHue + jitter(r, jitter * 0.6f), 0, 360)
			};
			case ANALOGOUS -> new float[]{
					wrap(baseHue - 30, 0, 360),
					baseHue,
					wrap(baseHue + 30, 0, 360)
			};
			case COMPLEMENTARY -> new float[]{
					baseHue,
					wrap(baseHue + 180, 0, 360)
			};
			case TRIADIC -> new float[]{
					baseHue,
					wrap(baseHue + 120, 0, 360),
					wrap(baseHue + 240, 0, 360)
			};
			case TETRADIC -> new float[]{
					baseHue,
					wrap(baseHue + 90, 0, 360),
					wrap(baseHue + 180, 0, 360),
					wrap(baseHue + 270, 0, 360)
			};
		};
	}

	public static Palette deriveMetal(String materialKey, PaletteRules rules) {
		long seed = mix(seed(materialKey), rules.seed());
		var r = new SplittableRandom(seed);

		float base = wrap(rules.hueBase() + jitter(r, rules.hueJitterDeg() * 0.5f), 0f, 360f);

		// Apply temperature adjustment
		base = wrap(base + (rules.temperature() * 10f), 0f, 360f);

		// Temperature progression with roughness influence
		float roughnessFactor = rules.roughness();
		float hShadow = wrap(base - 10f - (roughnessFactor * 5f), 0, 360);
		float hMidDark = wrap(base - 4f, 0, 360);
		float hMid = base;
		float hMidBright = wrap(base + 6f + (roughnessFactor * 3f), 0, 360);
		float hHighlight = wrap(base + 14f + (roughnessFactor * 8f), 0, 360);

		float sb = rules.satBase(), sv = rules.satVar(), vb = rules.valBase(), vv = rules.valVar();

		// Saturation with anisotropy influence
		float anisotropyBoost = rules.anisotropy() * 0.2f;
		float sShadow = clamp(sb * 0.70f + jitter(r, sv * 0.15f), 0f, 1f);
		float sMidDark = clamp(sb * 0.90f + jitter(r, sv * 0.12f) + anisotropyBoost, 0f, 1f);
		float sMid = clamp(sb * 1.00f + jitter(r, sv * 0.08f) + anisotropyBoost, 0f, 1f);
		float sMidBright = clamp(sb * 0.85f + jitter(r, sv * 0.10f) + anisotropyBoost, 0f, 1f);
		float sHighlight = clamp(sb * 0.65f + jitter(r, sv * 0.12f), 0f, 1f);

		// Value progression with roughness influence
		float vShadowBase = 0.25f + (vb - 0.5f) * 0.2f;
		float vMidBase = 0.65f + (vb - 0.5f) * 0.3f;
		float vHighlightBase = 0.82f + (vb - 0.5f) * 0.15f;

		// Roughness affects shadow darkness and highlight brightness
		vShadowBase *= (1.0f - roughnessFactor * 0.2f);
		vHighlightBase -= (roughnessFactor * 0.1f);

		// Apply brightness adjustment
		float brightnessAdjust = rules.brightness() * 0.15f;
		vShadowBase = clamp(vShadowBase + brightnessAdjust, 0f, 1f);
		vMidBase = clamp(vMidBase + brightnessAdjust, 0f, 1f);
		vHighlightBase = clamp(vHighlightBase + brightnessAdjust, 0f, 1f);

		float vShadow = clamp(vShadowBase + jitter(r, vv * 0.10f), 0f, 1f);
		float vMidDark = clamp(0.45f + (vb - 0.5f) * 0.2f + jitter(r, vv * 0.08f) + brightnessAdjust, 0f, 1f);
		float vMid = clamp(vMidBase + jitter(r, vv * 0.06f), 0f, 1f);
		float vMidBright = clamp(0.82f + (vb - 0.5f) * 0.15f + jitter(r, vv * 0.04f) + brightnessAdjust, 0f, 1f);
		float vHighlight = METAL_HIGHLIGHT_VALUE - (roughnessFactor * 0.1f);

		int cShadow = hsvToArgbSafe(hShadow, sShadow, vShadow);
		int cMidDark = hsvToArgbSafe(hMidDark, sMidDark, vMidDark);
		int cMid = hsvToArgbSafe(hMid, sMid, vMid);
		int cMidBright = hsvToArgbSafe(hMidBright, sMidBright, vMidBright);
		int cHighlight = hsvToArgbSafe(hHighlight, sHighlight, vHighlight);

		var stops = new ArrayList<>(List.of(
				new Palette.Stop(0.00f, cShadow),
				new Palette.Stop(0.25f, cMidDark),
				new Palette.Stop(0.50f, cMid),
				new Palette.Stop(0.75f, cMidBright),
				new Palette.Stop(1.00f, cHighlight)
		));

		if (rules.enforceContrast()) {
			ensureContrast(stops);
		}

		return new Palette(materialKey, stops, Palette.Mode.BY_LUMINANCE, Palette.Dither.NONE, Palette.Quantize.NONE);
	}

	public static Palette deriveMetalHueShift(String materialKey, PaletteRules rules) {
		long seed = mix(seed(materialKey), rules.seed());
		var r = new SplittableRandom(seed);

		float base = wrap(rules.hueBase() + jitter(r, rules.hueJitterDeg() * 0.4f), 0f, 360f);
		float phase = jitter(r, 3f);

		// Apply temperature adjustment
		base = wrap(base + (rules.temperature() * 12f), 0f, 360f);

		// Physically-inspired metallic hue progression with all parameters
		float roughnessFactor = rules.roughness();
		float hShadow = wrap(base - 12f - Math.abs(phase * 0.5f) - (roughnessFactor * 4f), 0, 360);
		float hMid = wrap(base + phase * 0.3f, 0, 360);
		float hHighlight = wrap(base + 18f + Math.abs(phase * 0.7f) + (roughnessFactor * 6f), 0, 360);
		float hSpecular = wrap(base + 35f + phase + (roughnessFactor * 10f), 0, 360);

		float sb = rules.satBase();
		float sv = rules.satVar();
		float vb = rules.valBase();
		float vv = rules.valVar();

		// Saturation with anisotropy influence
		float anisotropyBoost = rules.anisotropy() * 0.15f;
		float sShadow = clamp(sb * 0.80f + jitter(r, sv * 0.12f), 0f, 1f);
		float sMid = clamp(sb * 1.05f + jitter(r, sv * 0.08f) + anisotropyBoost, 0f, 1f);
		float sHighlight = clamp(sb * 0.70f + jitter(r, sv * 0.10f) + anisotropyBoost, 0f, 1f);
		float sSpecular = clamp(sb * 0.55f + jitter(r, sv * 0.06f), 0f, 1f);

		// Value progression with roughness influence
		float vShadowBase = 0.20f + (vb * 0.15f);
		float vMidBase = 0.60f + (vb * 0.25f);
		float vHighlightBase = 0.85f + (vb * 0.10f);

		// Roughness effects
		vShadowBase *= (1.0f - roughnessFactor * 0.2f);
		vHighlightBase += (roughnessFactor * 0.1f);

		// Apply brightness adjustment
		float brightnessAdjust = rules.brightness() * 0.2f;
		vShadowBase = clamp(vShadowBase + brightnessAdjust, 0f, 1f);
		vMidBase = clamp(vMidBase + brightnessAdjust, 0f, 1f);
		vHighlightBase = clamp(vHighlightBase + brightnessAdjust, 0f, 1f);

		float vShadow = clamp(vShadowBase + jitter(r, vv * 0.07f), 0f, 1f);
		float vMid = clamp(vMidBase + jitter(r, vv * 0.05f), 0f, 1f);
		float vHighlight = clamp(vHighlightBase + jitter(r, vv * 0.03f), 0f, 1f);
		float vSpecular = 0.98f;

		// Hue enhancement based on base color and temperature
		if (base > 20 && base < 60) { // Gold/Copper range
			hHighlight = wrap(hHighlight + 5f + (rules.temperature() * 8f), 0, 360);
			hSpecular = wrap(hSpecular + 10f + (rules.temperature() * 12f), 0, 360);
			sHighlight *= 1.1f;
		} else if (base > 220 && base < 280) { // Blue range
			hShadow = wrap(hShadow - 5f + (rules.temperature() * -8f), 0, 360);
			sShadow *= 1.05f;
		}

		int cShadow = hsvToArgbSafe(hShadow, sShadow, vShadow);
		int cMid = hsvToArgbSafe(hMid, sMid, vMid);
		int cHighlight = hsvToArgbSafe(hHighlight, sHighlight, vHighlight);
		int cSpecular = hsvToArgbSafe(hSpecular, sSpecular, vSpecular);

		var stops = new ArrayList<>(List.of(
				new Palette.Stop(0.00f, cShadow),
				new Palette.Stop(0.30f, cMid),
				new Palette.Stop(0.70f, cHighlight),
				new Palette.Stop(1.00f, cSpecular)
		));

		if (rules.enforceContrast()) {
			ensureContrast2(stops);
		}

		return new Palette(materialKey, stops, Palette.Mode.BY_LUMINANCE, Palette.Dither.NONE, Palette.Quantize.NONE);
	}

	public static Palette deriveGem(String materialKey, PaletteRules rules) {
		long seed = mix(seed(materialKey), rules.seed());
		var r = new SplittableRandom(seed);

		float base = wrap(rules.hueBase() + jitter(r, rules.hueJitterDeg()), 0f, 360f);
		// Apply temperature adjustment
		base = wrap(base + (rules.temperature() * 10f), 0f, 360f);

		// Gems: subtle hue swing across ramp, high saturation, bright top
		float h0 = wrap(base - 8f, 0, 360);
		float h2 = wrap(base + 6f, 0, 360);
		float h3 = wrap(base + 10f, 0, 360);

		float sb = rules.satBase(), sv = rules.satVar(), vb = rules.valBase(), vv = rules.valVar();

		// Apply brightness adjustment
		float brightnessAdjust = rules.brightness() * 0.1f;

		int c0 = hsvToArgbSafe(h0, clamp(sb * 0.90f + jitter(r, sv * 0.15f), 0f, 1f),
				clamp(GEM_DEEP_VALUE + jitter(r, GEM_DEEP_VAR) + brightnessAdjust, 0f, 1f));
		int c1 = hsvToArgbSafe(base, clamp(sb * 1.00f + jitter(r, sv * 0.12f), 0f, 1f),
				clamp(GEM_BODY_VALUE + jitter(r, GEM_BODY_VAR) + brightnessAdjust, 0f, 1f));
		int c2 = hsvToArgbSafe(h2, clamp(sb * 0.95f + jitter(r, sv * 0.10f), 0f, 1f),
				clamp(GEM_BRIGHT_VALUE + jitter(r, GEM_BRIGHT_VAR) + brightnessAdjust, 0f, 1f));
		int c3 = hsvToArgbSafe(h3, clamp(GEM_GLINT_SAT + jitter(r, sv * 0.08f), 0f, 1f),
				clamp(GEM_GLINT_VALUE + brightnessAdjust, 0f, 1f));

		var stops = new ArrayList<>(List.of(
				new Palette.Stop(0.00f, c0),
				new Palette.Stop(0.42f, c1),
				new Palette.Stop(0.78f, c2),
				new Palette.Stop(1.00f, c3)
		));

		if (rules.enforceContrast()) {
			ensureContrast2(stops);
		}

		return new Palette(materialKey, stops, Palette.Mode.BY_LUMINANCE, Palette.Dither.NONE, Palette.Quantize.NONE);
	}

	public static Palette deriveStone(String materialKey, PaletteRules rules) {
		long seed = mix(seed(materialKey), rules.seed());
		var r = new SplittableRandom(seed);

		float base = wrap(rules.hueBase() + jitter(r, rules.hueJitterDeg() * 0.3f), 0f, 360f);
		// Apply temperature adjustment
		base = wrap(base + (rules.temperature() * 5f), 0f, 360f);

		// Gentle multi-hue drift with speckle influence
		float speckleInfluence = rules.speckle() * 15f;
		float hDarkest = wrap(base - 6f - speckleInfluence, 0, 360);
		float hDark = wrap(base - 2f, 0, 360);
		float hMid = base;
		float hLight = wrap(base + 3f + speckleInfluence * 0.5f, 0, 360);
		float hLightest = wrap(base + 8f + speckleInfluence, 0, 360);

		// Stone-specific saturation with grain influence
		float sb = Math.max(0.15f, rules.satBase() * 0.4f);
		float sv = rules.satVar() * (0.12f + rules.speckle() * 0.25f);
		float grainBoost = rules.grain() * 0.1f;

		float sDarkest = clamp(sb * 0.85f + jitter(r, sv * 0.8f) + grainBoost, 0f, 1f);
		float sDark = clamp(sb * 1.00f + jitter(r, sv * 0.7f) + grainBoost, 0f, 1f);
		float sMid = clamp(sb * 0.95f + jitter(r, sv * 0.6f), 0f, 1f);
		float sLight = clamp(sb * 0.80f + jitter(r, sv * 0.7f) - grainBoost, 0f, 1f);
		float sLightest = clamp(sb * 0.65f + jitter(r, sv * 0.8f) - grainBoost, 0f, 1f);

		// Value progression with speckle influence
		float speckleEffect = rules.speckle() * 0.1f;
		float brightnessAdjust = rules.brightness() * 0.15f;

		float vDarkest = clamp(0.18f + jitter(r, 0.05f) - speckleEffect + brightnessAdjust, 0f, 1f);
		float vDark = clamp(0.35f + jitter(r, 0.08f) + brightnessAdjust, 0f, 1f);
		float vMid = clamp(0.55f + jitter(r, 0.10f) + brightnessAdjust, 0f, 1f);
		float vLight = clamp(0.72f + jitter(r, 0.08f) + speckleEffect + brightnessAdjust, 0f, 1f);
		float vLightest = clamp(0.88f + jitter(r, 0.05f) + speckleEffect * 2f + brightnessAdjust, 0f, 1f);

		int cDarkest = hsvToArgbSafe(hDarkest, sDarkest, vDarkest);
		int cDark = hsvToArgbSafe(hDark, sDark, vDark);
		int cMid = hsvToArgbSafe(hMid, sMid, vMid);
		int cLight = hsvToArgbSafe(hLight, sLight, vLight);
		int cLightest = hsvToArgbSafe(hLightest, sLightest, vLightest);

		var stops = new ArrayList<>(List.of(
				new Palette.Stop(0.00f, cDarkest),
				new Palette.Stop(0.25f, cDark),
				new Palette.Stop(0.50f, cMid),
				new Palette.Stop(0.75f, cLight),
				new Palette.Stop(1.00f, cLightest)
		));

		if (rules.enforceContrast()) {
			ensureContrast(stops);
		}

		return new Palette(materialKey, stops, Palette.Mode.BY_LUMINANCE, Palette.Dither.NONE, Palette.Quantize.NONE);
	}

	public static Palette deriveWood(String materialKey, PaletteRules rules) {
		long seed = mix(seed(materialKey), rules.seed());
		var r = new java.util.SplittableRandom(seed);

		// Base hue with jitter + temperature
		float base = wrap(
				rules.hueBase()
						+ (float) (r.nextGaussian() * rules.hueJitterDeg() * 0.45f)
						+ rules.temperature() * 8f,
				0f, 360f
		);

		// Harmony hues
		ColorHarmony harmony = parseHarmony(rules.harmony());
		float[] harmSet = generateHarmony1(base, harmony, r, rules.hueJitterDeg());

		// Pick three anchors (shadow, mid, light) from harmony set
		float undertoneAxis = wrap(base - 55f, 0f, 360f); // olive-ish undertone
		float hShadow = mixHue(harmSet[0], undertoneAxis, 0.25f);
		float hMid = mixHue(harmSet[harmSet.length > 1 ? 1 : 0], undertoneAxis, 0.12f);
		float hLight = harmSet[harmSet.length > 2 ? 2 : harmSet.length - 1];

		// Saturation & value = your earlier calmer version
		float sb = clamp(rules.satBase(), 0f, 1f);
		float sv = rules.satVar();
		float brightAdj = rules.brightness() * 0.10f;

		float sShadow = clamp(sb * 0.75f + jitter(r, sv * 0.10f), 0f, 1f);
		float sMid = clamp(sb * 0.85f + jitter(r, sv * 0.08f), 0f, 1f);
		float sLight = clamp(sb * 0.80f + jitter(r, sv * 0.08f), 0f, 1f);

		// Values: back to calmer range
		float vShadow = clamp(0.42f + jitter(r, 0.03f) + brightAdj, 0f, 1f);
		float vMid = clamp(0.55f + jitter(r, 0.03f) + brightAdj, 0f, 1f);
		float vLight = clamp(0.68f + jitter(r, 0.02f) + brightAdj, 0f, 1f);

		int c0 = hsvToArgbSafe(hShadow, sShadow, vShadow);
		int c1 = hsvToArgbSafe(hMid, sMid, vMid);
		int c2 = hsvToArgbSafe(hLight, sLight, vLight);

		var stops = new ArrayList<Palette.Stop>(3);
		stops.add(new Palette.Stop(0.00f, c0));
		stops.add(new Palette.Stop(0.60f, c1));
		stops.add(new Palette.Stop(1.00f, c2));

		if (rules.enforceContrast()) ensureContrast(stops);
		return new Palette(materialKey, stops, Palette.Mode.BY_LUMINANCE, Palette.Dither.NONE, Palette.Quantize.NONE);
	}

	// Helper: mix angles on a hue circle
	private static float mixHue(float a, float b, float t) {
		// shortest-arc mix
		float da = wrap(b - a, -180f, 180f);
		return wrap(a + da * clamp(t, 0f, 1f), 0f, 360f);
	}

	// Use existing enum
	private static ColorHarmony parseHarmony(String h) {
		try {
			return ColorHarmony.valueOf(h.toUpperCase());
		} catch (Exception e) {
			return ColorHarmony.ANALOGOUS;
		}
	}

	private static float[] generateHarmony1(float base, ColorHarmony harm, SplittableRandom r, float jitter) {
		return switch (harm) {
			case MONOCHROMATIC -> new float[]{base,
					wrap(base + (float) (r.nextGaussian() * jitter * 0.3f), 0, 360),
					wrap(base + (float) (r.nextGaussian() * jitter * 0.6f), 0, 360)};
			case ANALOGOUS -> new float[]{wrap(base - 30, 0, 360), base, wrap(base + 30, 0, 360)};
			case COMPLEMENTARY -> new float[]{base, wrap(base + 180, 0, 360)};
			case TRIADIC -> new float[]{base, wrap(base + 120, 0, 360), wrap(base + 240, 0, 360)};
			case TETRADIC ->
					new float[]{base, wrap(base + 90, 0, 360), wrap(base + 180, 0, 360), wrap(base + 270, 0, 360)};
		};
	}

	public static Palette deriveWood2(String materialKey, PaletteRules rules) {
		long seed = mix(seed(materialKey), rules.seed());
		var r = new SplittableRandom(seed);

		float base = wrap(rules.hueBase() + jitter(r, rules.hueJitterDeg() * 0.5f), 0f, 360f);
		// Apply temperature adjustment
		base = wrap(base + (rules.temperature() * 8f), 0f, 360f);

		// Woods: warm mids, cooler shadows, desaturated highlights
		float grainInfluence = rules.grain() * 10f;
		float h0 = wrap(base - 14f - grainInfluence, 0, 360);
		float h1 = wrap(base - 4f, 0, 360);
		float h2 = wrap(base + 6f + grainInfluence, 0, 360);

		float sb = rules.satBase(), sv = rules.satVar();
		float brightnessAdjust = rules.brightness() * 0.1f;

		int c0 = hsvToArgbSafe(h0, clamp(sb * 0.55f + jitter(r, sv * 0.15f), 0f, 1f),
				clamp(WOOD_BARK_VALUE + brightnessAdjust, 0f, 1f));
		int c1 = hsvToArgbSafe(h1, clamp(sb * 0.85f + jitter(r, sv * 0.15f), 0f, 1f),
				clamp(WOOD_BODY_VALUE + brightnessAdjust, 0f, 1f));
		int c2 = hsvToArgbSafe(h2, clamp(sb * 0.50f + jitter(r, sv * 0.10f), 0f, 1f),
				clamp(WOOD_BLEACHED_VALUE + brightnessAdjust, 0f, 1f));

		var stops = new ArrayList<>(List.of(
				new Palette.Stop(0.00f, c0),
				new Palette.Stop(0.60f, c1),
				new Palette.Stop(1.00f, c2)
		));

		if (rules.enforceContrast()) {
			ensureContrast(stops);
		}

		return new Palette(materialKey, stops, Palette.Mode.BY_LUMINANCE, Palette.Dither.NONE, Palette.Quantize.NONE);
	}

	public static Palette deriveBark(String materialKey, PaletteRules rules) {
		long seed = mix(seed(materialKey), rules.seed());
		var r = new java.util.SplittableRandom(seed);

		// Very small hue movement; tiny temperature swing
		float base = wrap(rules.hueBase() + jitter(r, rules.hueJitterDeg() * 0.20f)
				+ rules.temperature() * 4f, 0f, 360f);

		float grain = clamp(rules.grain(), 0f, 1f);
		float speck = clamp(rules.speckle(), 0f, 1f);

		// Tight hue cluster; no wild greens unless you want them
		float hCrease = wrap(base - 6f - grain * 4f, 0, 360);
		float hBody = wrap(base - 1f, 0, 360);
		float hPlateau = wrap(base + 3f + grain * 2f, 0, 360);

		// Low chroma & jitter
		float sb = clamp(rules.satBase() * 0.55f, 0f, 1f);
		float sv = rules.satVar() * 0.4f;   // reduced
		float bright = rules.brightness() * 0.08f;

		// **Narrow value range** (this is the big de-noiser)
		float vCrease = clamp(0.38f + jitter(r, 0.02f) + bright - grain * 0.01f, 0f, 1f);
		float vBody = clamp(0.50f + jitter(r, 0.02f) + bright, 0f, 1f);
		float vPlateau = clamp(0.60f + jitter(r, 0.02f) + bright + grain * 0.01f, 0f, 1f);

		float sCrease = clamp(sb * (0.70f) + jitter(r, sv * 0.10f), 0f, 1f);
		float sBody = clamp(sb * (0.85f) + jitter(r, sv * 0.10f), 0f, 1f);
		float sPlateau = clamp(sb * (0.65f) + jitter(r, sv * 0.08f), 0f, 1f);

		int crease = hsvToArgbSafe(hCrease, sCrease, vCrease);
		int body = hsvToArgbSafe(hBody, sBody, vBody);
		int plateau = hsvToArgbSafe(hPlateau, sPlateau, vPlateau);

		// Optional very subtle lichen tint – only on the light plateaus, and clamped
		if (speck > 0.01f) {
			float lichenHue = wrap(90f + jitter(r, 3f), 0, 360); // yellow-green
			int moss = hsvToArgbSafe(lichenHue,
					clamp(0.20f + speck * 0.18f, 0f, 1f),
					clamp(0.55f + speck * 0.12f, 0f, 1f));
			float mossAmt = Math.min(0.06f, speck * 0.07f);       // << super low
			plateau = blendColors(plateau, moss, mossAmt);
		}

		var stops = new ArrayList<Palette.Stop>(3);
		stops.add(new Palette.Stop(0.00f, crease));
		stops.add(new Palette.Stop(0.55f, body));
		stops.add(new Palette.Stop(1.00f, plateau));

		// For bark, default to false; turn on only if your base is very flat.
		if (rules.enforceContrast()) ensureContrast(stops);

		return new Palette(materialKey, stops, Palette.Mode.BY_LUMINANCE, Palette.Dither.NONE, Palette.Quantize.UNIQUE);
	}

	public static Palette deriveCopper(String materialKey, PaletteRules rules) {
		long seed = mix(seed(materialKey), rules.seed());
		var r = new SplittableRandom(seed);

		float base = wrap(rules.hueBase() + jitter(r, rules.hueJitterDeg() * 0.4f), 0f, 360f);

		// Apply temperature adjustment for warm metals
		float tempAdjust = rules.temperature();
		float hShadow = wrap(base - 12f - (tempAdjust * 8f), 0, 360);
		float hMid = wrap(base + (tempAdjust * 4f), 0, 360);
		float hHighlight = wrap(base + 20f + (tempAdjust * 12f), 0, 360);
		float hSpecular = wrap(base + 35f + (tempAdjust * 15f), 0, 360);

		float sb = rules.satBase(), sv = rules.satVar(), vb = rules.valBase(), vv = rules.valVar();

		// Copper-specific saturation profile
		float sShadow = clamp(sb * 0.80f + jitter(r, sv * 0.12f), 0f, 1f);
		float sMid = clamp(sb * 1.05f + jitter(r, sv * 0.08f), 0f, 1f);
		float sHighlight = clamp(sb * 0.75f + jitter(r, sv * 0.10f), 0f, 1f);
		float sSpecular = clamp(sb * 0.60f + jitter(r, sv * 0.06f), 0f, 1f);

		// Value progression with copper characteristics
		float vShadowBase = 0.25f + (vb * 0.15f);
		float vMidBase = 0.65f + (vb * 0.25f);
		float vHighlightBase = 0.85f + (vb * 0.10f);

		// Roughness affects shadow darkness and highlight brightness
		float roughnessFactor = rules.roughness();
		vShadowBase *= (1.0f - roughnessFactor * 0.2f);
		vHighlightBase += (roughnessFactor * 0.1f);

		float vShadow = clamp(vShadowBase + jitter(r, vv * 0.10f), 0f, 1f);
		float vMid = clamp(vMidBase + jitter(r, vv * 0.06f), 0f, 1f);
		float vHighlight = clamp(vHighlightBase + jitter(r, vv * 0.04f), 0f, 1f);
		float vSpecular = 0.98f;

		int cShadow = hsvToArgbSafe(hShadow, sShadow, vShadow);
		int cMid = hsvToArgbSafe(hMid, sMid, vMid);
		int cHighlight = hsvToArgbSafe(hHighlight, sHighlight, vHighlight);
		int cSpecular = hsvToArgbSafe(hSpecular, sSpecular, vSpecular);

		var stops = new ArrayList<>(List.of(
				new Palette.Stop(0.00f, cShadow),
				new Palette.Stop(0.35f, cMid),
				new Palette.Stop(0.70f, cHighlight),
				new Palette.Stop(1.00f, cSpecular)
		));

		if (rules.enforceContrast()) {
			ensureContrast(stops);
		}

		return new Palette(materialKey, stops, Palette.Mode.BY_LUMINANCE, Palette.Dither.NONE, Palette.Quantize.NONE);
	}

	public static Palette blend(Palette palette1, Palette palette2, float ratio, String newKey) {
		// Ensure ratio is between 0 and 1
		ratio = clamp(ratio, 0f, 1f);

		List<Palette.Stop> blendedStops = new ArrayList<>();

		// Get stops from both palettes (assuming same number of stops)
		var stops1 = palette1.stops();
		var stops2 = palette2.stops();

		int numStops = Math.min(stops1.size(), stops2.size());

		for (int i = 0; i < numStops; i++) {
			float pos = stops1.get(i).pos(); // Use position from first palette
			int color1 = stops1.get(i).argb();
			int color2 = stops2.get(i).argb();

			// Blend colors
			int blendedColor = blendColors(color1, color2, ratio);
			blendedStops.add(new Palette.Stop(pos, blendedColor));
		}

		return new Palette(newKey, blendedStops, Palette.Mode.BY_LUMINANCE,
				Palette.Dither.NONE, Palette.Quantize.NONE);
	}

	private static int blendColors(int color1, int color2, float ratio) {
		int a1 = (color1 >> 24) & 0xFF;
		int r1 = (color1 >> 16) & 0xFF;
		int g1 = (color1 >> 8) & 0xFF;
		int b1 = color1 & 0xFF;

		int a2 = (color2 >> 24) & 0xFF;
		int r2 = (color2 >> 16) & 0xFF;
		int g2 = (color2 >> 8) & 0xFF;
		int b2 = color2 & 0xFF;

		int a = (int) (a1 * (1 - ratio) + a2 * ratio);
		int r = (int) (r1 * (1 - ratio) + r2 * ratio);
		int g = (int) (g1 * (1 - ratio) + g2 * ratio);
		int b = (int) (b1 * (1 - ratio) + b2 * ratio);

		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	// Helper methods
	private static float jitter(SplittableRandom random, float variance) {
		return (float) (random.nextGaussian() * variance);
	}

	private static float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	private static double calculateLuminance(int argb) {
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		return 0.2126 * (r / 255.0) + 0.7152 * (g / 255.0) + 0.0722 * (b / 255.0);
	}

	private static void ensureContrast2(List<Palette.Stop> stops) {
		for (int i = 1; i < stops.size(); i++) {
			var prev = stops.get(i - 1);
			var curr = stops.get(i);

			double l1 = linearLuminance(prev.argb());
			double l2 = linearLuminance(curr.argb());
			double contrast = (Math.max(l1, l2) + 0.05) / (Math.min(l1, l2) + 0.05);

			if (contrast >= MIN_CONTRAST_RATIO) continue;

			// Nudge both sides in opposite directions, preserve hue/sat
			int prevAdj = prev.argb();
			int currAdj = curr.argb();
			int maxSteps = 8;

			for (int step = 0; step < maxSteps && contrast < MIN_CONTRAST_RATIO; step++) {
				float vPrev = extractValue(prevAdj);
				float vCurr = extractValue(currAdj);

				vPrev = clamp(vPrev * 0.94f, 0f, 1f);  // darker
				vCurr = clamp(vCurr * 1.06f, 0f, 1f);  // brighter

				prevAdj = adjustValue(prevAdj, vPrev);
				currAdj = adjustValue(currAdj, vCurr);

				l1 = linearLuminance(prevAdj);
				l2 = linearLuminance(currAdj);
				contrast = (Math.max(l1, l2) + 0.05) / (Math.min(l1, l2) + 0.05);
			}

			stops.set(i - 1, new Palette.Stop(prev.pos(), prevAdj));
			stops.set(i, new Palette.Stop(curr.pos(), currAdj));
		}
	}

	private static void ensureContrast(List<Palette.Stop> stops) {
		for (int i = 1; i < stops.size(); i++) {
			double dE = deltaEOK(stops.get(i-1).argb(), stops.get(i).argb());
			if (dE < 0.05) { // ~5 ΔE threshold; tweak as needed
				// Push the darker stop slightly darker
				int argb = stops.get(i-1).argb();
				float L = toOKLab1(argb)[0];
				float newL = Math.max(0f, L * 0.9f);
				stops.set(i-1, new Palette.Stop(stops.get(i-1).pos(),
						setOKLabL(argb, newL)));
			}
		}
	}

	private static int setOKLabL(int argb, float newL) {
		// quick + dirty: scale RGB to match new L
		// For accuracy, you’d need full OKLab→sRGB conversion.
		float oldL = oklabL(argb);
		float scale = oldL > 0 ? newL / oldL : 1f;
		int r = (int) clamp(((argb >> 16) & 0xFF) * scale, 0, 255);
		int g = (int) clamp(((argb >>  8) & 0xFF) * scale, 0, 255);
		int b = (int) clamp(( argb        & 0xFF) * scale, 0, 255);
		int a = (argb >>> 24) & 0xFF;
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	// Replace calculateLuminance(...) with gamma-aware
	private static double linearLuminance(int argb) {
		// sRGB -> linear, then Y
		double r = srgbToLinear((argb >> 16 & 0xFF) / 255.0);
		double g = srgbToLinear((argb >> 8 & 0xFF) / 255.0);
		double b = srgbToLinear((argb & 0xFF) / 255.0);
		return 0.2126 * r + 0.7152 * g + 0.0722 * b;
	}

	private static double srgbToLinear(double c) {
		return c <= 0.04045 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
	}

	private static float extractValue(int argb) {
		// Simplified: extract value from ARGB (implement HSV conversion if needed)
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		return Math.max(r, Math.max(g, b)) / 255.0f;
	}

	private static int adjustValue(int argb, float newValue) {
		// Simplified: adjust value while preserving hue/saturation (implement full HSV conversion if needed)
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		float currentValue = Math.max(r, Math.max(g, b)) / 255.0f;
		float scale = currentValue > 0 ? newValue / currentValue : 1.0f;
		r = (int) clamp(r * scale, 0, 255);
		g = (int) clamp(g * scale, 0, 255);
		b = (int) clamp(b * scale, 0, 255);
		return (0xFF << 24) | (r << 16) | (g << 8) | b;
	}

	private static long seed(String s) {
		long h = 1469598103934665603L;
		for (int i = 0; i < s.length(); i++) h = (h ^ s.charAt(i)) * 1099511628211L;
		return h;
	}

	private static long mix(long a, long b) {
		long x = a ^ b;
		x ^= (x << 13);
		x ^= (x >>> 7);
		x ^= (x << 17);
		return x;
	}

	private static float wrap(float v, float min, float max) {
		float d = max - min;
		v = (v - min) % d;
		if (v < 0) v += d;
		return v + min;
	}

	private static int hsvToArgbSafe(float h, float s, float v) {
		float[] lab = hsvToOKLab(h, s, v);
		return oklabToSRGB(lab[0], lab[1], lab[2]);
	}

	private static float[] hsvToOKLab(float h, float s, float v) {
		// --- HSV -> sRGB (gamma) ---
		float c = v * s;
		float hh = (h % 360f) / 60f; if (hh < 0) hh += 6f;
		int   i = (int)Math.floor(hh);
		float f = hh - i;
		float p = v - c;
		float q = v - c * f;
		float t = v - c * (1f - f);

		float Rg, Gg, Bg; // gamma-encoded 0..1
		switch (i) {
			case 1 -> { Rg = q; Gg = v; Bg = p; }
			case 2 -> { Rg = p; Gg = v; Bg = t; }
			case 3 -> { Rg = p; Gg = q; Bg = v; }
			case 4 -> { Rg = t; Gg = p; Bg = v; }
			case 5 -> { Rg = v; Gg = p; Bg = q; }
			default -> { Rg = v; Gg = t; Bg = p; } // 0
		}

		// --- sRGB (gamma) -> linear ---
		float R = (float) srgbToLinear(Rg);
		float G = (float) srgbToLinear(Gg);
		float B = (float) srgbToLinear(Bg);

		// --- linear sRGB -> OKLab ---
		// to LMS (OKLab)
		float l = 0.4122214708f*R + 0.5363325363f*G + 0.0514459929f*B;
		float m = 0.2119034982f*R + 0.6806995451f*G + 0.1073969566f*B;
		float s1 = 0.0883024619f*R + 0.2817188376f*G + 0.6299787005f*B;

		float l3 = (float)Math.cbrt(Math.max(0f,l));
		float m3 = (float)Math.cbrt(Math.max(0f,m));
		float s3 = (float)Math.cbrt(Math.max(0f,s1));

		float L = 0.2104542553f*l3 + 0.7936177850f*m3 - 0.0040720468f*s3;
		float A = 1.9779984951f*l3 - 2.4285922050f*m3 + 0.4505937099f*s3;
		float Bc= 0.0259040371f*l3 + 0.7827717662f*m3 - 0.8086757660f*s3;

		return new float[]{ L, A, Bc };
	}

	private static int oklabToSRGB(float L, float A, float B) {
		// Mild L-dependent chroma fade to avoid clipping at very dark/bright
		float C = (float)Math.hypot(A, B);
		float h = (float)Math.atan2(B, A);
		float fade = (L < 0.2f) ? (L/0.2f) : (L > 0.85f ? (1f - L)/0.15f : 1f);
		C *= Math.max(0f, Math.min(1f, fade));
		A = (float)(Math.cos(h) * C);
		B = (float)(Math.sin(h) * C);

		// OKLab -> linear sRGB
		float l_ = L + 0.3963377774f*A + 0.2158037573f*B;
		float m_ = L - 0.1055613458f*A - 0.0638541728f*B;
		float s_ = L - 0.0894841775f*A - 1.2914855480f*B;

		float l = l_*l_*l_;
		float m = m_*m_*m_;
		float s = s_*s_*s_;

		float R = +4.0767416621f*l - 3.3077115913f*m + 0.2309699292f*s;
		float G = -1.2684380046f*l + 2.6097574011f*m - 0.3413193965f*s;
		float Bc= -0.0041960863f*l - 0.7034186147f*m + 1.7076147010f*s;

		// Clamp + gamma encode
		R = (float) linearToSrgb(Math.max(0f, Math.min(1f, R)));
		G = (float) linearToSrgb(Math.max(0f, Math.min(1f, G)));
		Bc= (float) linearToSrgb(Math.max(0f, Math.min(1f, Bc)));

		int Ri = Math.round(R*255f), Gi = Math.round(G*255f), Bi = Math.round(Bc*255f);
		return (0xFF<<24) | (Ri<<16) | (Gi<<8) | Bi;
	}

	private static int hsvToArgb(float h, float s, float v) {
		float c = v * s, x = c * (1 - Math.abs(((h / 60f) % 2) - 1)), m = v - c;
		float r = 0, g = 0, b = 0;
		int hi = (int) Math.floor(h / 60f) % 6;
		switch (hi) {
			case 0 -> {
				r = c;
				g = x;
				b = 0;
			}
			case 1 -> {
				r = x;
				g = c;
				b = 0;
			}
			case 2 -> {
				r = 0;
				g = c;
				b = x;
			}
			case 3 -> {
				r = 0;
				g = x;
				b = c;
			}
			case 4 -> {
				r = x;
				g = 0;
				b = c;
			}
			case 5 -> {
				r = c;
				g = 0;
				b = x;
			}
		}
		int R = (int) Math.round((r + m) * 255);
		int G = (int) Math.round((g + m) * 255);
		int B = (int) Math.round((b + m) * 255);
		return (0xFF << 24) | (R << 16) | (G << 8) | B;
	}

	private static float extractSaturation(int argb) {
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		float max = Math.max(Math.max(r, g), b) / 255.0f;
		float min = Math.min(Math.min(r, g), b) / 255.0f;
		float delta = max - min;
		return max == 0 ? 0 : delta / max;
	}

	// sRGB (0-1) to OKLab (L, a, b)
	private static float[] rgbToOKLab(float r, float g, float b) {
		// Linearize sRGB
		r = (float) srgbToLinear(r);
		g = (float) srgbToLinear(g);
		b = (float) srgbToLinear(b);

		// sRGB to XYZ (simplified D65 matrix)
		float X = 0.4124f * r + 0.3576f * g + 0.1805f * b;
		float Y = 0.2126f * r + 0.7152f * g + 0.0722f * b;
		float Z = 0.0193f * r + 0.1192f * g + 0.9505f * b;

		// XYZ to LMS
		float L = (float) Math.cbrt(0.8190f * X + 0.3619f * Y - 0.1289f * Z);
		float M = (float) Math.cbrt(0.0324f * X + 0.9297f * Y + 0.0361f * Z);
		float S = (float) Math.cbrt(0.0482f * X + 0.2647f * Y + 0.6339f * Z);

		// LMS to OKLab
		float l = 0.2105f * L + 0.7937f * M - 0.0042f * S;
		float a = 1.9779f * L - 2.4286f * M + 0.4507f * S;
		float b_ = 0.0259f * L + 0.7828f * M - 0.8087f * S;

		return new float[]{l, a, b_};
	}

	// OKLab to sRGB (0-1)
	private static float[] okLabToRgb(float l, float a, float b) {
		// OKLab to LMS
		float L = l + 0.3963f * a + 0.2158f * b;
		float M = l - 0.1056f * a - 0.0639f * b;
		float S = l - 0.0895f * a - 1.2915f * b;

		// LMS to XYZ
		L = L * L * L;
		M = M * M * M;
		S = S * S * S;
		float X = 4.0767f * L - 3.3077f * M + 0.2310f * S;
		float Y = -1.2684f * L + 2.6099f * M - 0.3415f * S;
		float Z = -0.0042f * L - 0.7034f * M + 1.7076f * S;

		// XYZ to sRGB
		float r = 3.2405f * X - 1.5371f * Y - 0.4985f * Z;
		float g = -0.9693f * X + 1.8760f * Y + 0.0416f * Z;
		float b_ = 0.0556f * X - 0.2040f * Y + 1.0572f * Z;

		// Apply sRGB gamma
		r = (float) linearToSrgb(r);
		g = (float) linearToSrgb(g);
		b_ = (float) linearToSrgb(b_);

		return new float[]{clamp(r, 0f, 1f), clamp(g, 0f, 1f), clamp(b_, 0f, 1f)};
	}

	private static double linearToSrgb(double c) {
		return c <= 0.0031308 ? 12.92 * c : 1.055 * Math.pow(c, 1 / 2.4) - 0.055;
	}

	public enum Scheme {
		METAL, GEM, STONE, WOOD
	}

	public enum ColorHarmony {
		MONOCHROMATIC, ANALOGOUS, COMPLEMENTARY, TRIADIC, TETRADIC
	}

	public static class PaletteAnalysis {
		public static float getAverageSaturation(Palette palette) {
			float totalSat = 0;
			for (var stop : palette.stops()) {
				totalSat += extractSaturation(stop.argb());
			}
			return totalSat / palette.stops().size();
		}

		public static float getBrightnessRange(Palette palette) {
			float minBright = 1f, maxBright = 0f;
			for (var stop : palette.stops()) {
				float brightness = extractValue(stop.argb());
				minBright = Math.min(minBright, brightness);
				maxBright = Math.max(maxBright, brightness);
			}
			return maxBright - minBright;
		}

		public static boolean isHighContrast(Palette palette) {
			for (int i = 1; i < palette.stops().size(); i++) {
				double l1 = calculateLuminance(palette.stops().get(i - 1).argb());
				double l2 = calculateLuminance(palette.stops().get(i).argb());
				double contrast = (Math.max(l1, l2) + 0.05) / (Math.min(l1, l2) + 0.05);
				if (contrast < MIN_CONTRAST_RATIO) {
					return false;
				}
			}
			return true;
		}
	}
}