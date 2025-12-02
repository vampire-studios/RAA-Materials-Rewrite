package net.vampirestudios.raaMaterials.palette;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal CLI you can Run ▶ in IntelliJ.
 * Assumption: grayscale base → BY_LUMINANCE ramp mapping.
 */
public final class PaletteRunner {

	public static void main(String[] args) throws Exception {
		Args a = Args.parse(args);

		if (a.batchPath != null) {
			List<BatchRow> rows = readBatch(a.batchPath);
			int threads = Math.max(1, a.threads);
			java.util.concurrent.ExecutorService es = java.util.concurrent.Executors.newFixedThreadPool(threads);
			java.util.List<java.util.concurrent.Future<?>> fs = new java.util.ArrayList<>();

			for (BatchRow row : rows) {
				int n = Math.max(1, row.variants == null ? 1 : row.variants);
				for (int i = 0; i < n; i++) {
					final int idx = i;
					fs.add(es.submit(() -> {
						try {
							runOneBatchRow(a, row, idx);
						} catch (Exception e) {
							System.err.println("[ERROR] " + row.out + " :: " + e.getMessage());
							e.printStackTrace();
						}
					}));
				}
			}
			for (var f : fs) f.get();
			es.shutdown();
			System.out.println("Batch complete.");
			return;
		}

		// single-file mode (unchanged, but factor through the same path)
		BatchRow single = new BatchRow();
		single.in = a.inPng.toString();
		single.out = a.outPng.toString();
		single.key = a.materialKey;
		single.scheme = a.scheme;
		single.seed = a.seed;
		single.hueBase = a.hueBase;
		single.hueJitter = a.hueJitter;
		single.satBase = a.satBase;
		single.satVar = a.satVar;
		single.valBase = a.valBase;
		single.valVar = a.valVar;
		single.reverseOrder = a.reverseOrder;
		single.invert = a.invert;
		single.gamma = a.gamma;

		// New parameters
		single.roughness = a.roughness;
		single.anisotropy = a.anisotropy;
		single.speckle = a.speckle;
		single.grain = a.grain;
		single.temperature = a.temperature;
		single.brightness = a.brightness;
		single.preset = a.preset;
		single.harmony = a.harmony;
		single.enforceContrast = a.enforceContrast;

		runOneBatchRow(a, single, 0);
		System.out.println("Wrote → " + a.outPng.toAbsolutePath());
	}

    /*public static void main(String[] args) throws Exception {
        Args a = Args.parse(args);

        // 1) Load rules (inline via flags; no JSON needed)
        PaletteRules rules = new PaletteRules(
                a.seed, a.scheme,
                a.hueBase, a.hueJitterDeg,
                a.satBase, a.satVar,
                a.valBase, a.valVar
        );

        Palette pal = switch (a.scheme.toUpperCase()) {
            case "GEM"   -> PaletteDeriver.deriveGem(a.materialKey, rules);
            case "STONE" -> PaletteDeriver.deriveStone(a.materialKey, rules);
            case "WOOD"  -> PaletteDeriver.deriveWood(a.materialKey, rules);
			default      -> PaletteDeriver.deriveMetal(a.materialKey, rules);
        };

        // 3) Build ramp + recolor
        int[] ramp = Recolor.buildRampOkLab(pal.stops());
        BufferedImage src = readPng(a.inPng);
        BufferedImage dst = Recolor.applyLockedRank(src, ramp, false, false, 1.0f);

        // 4) Write PNG
        writePng(dst, a.outPng);
        System.out.println("Wrote → " + a.outPng.toAbsolutePath());
    }*/

	private static void runOneBatchRow(Args a, BatchRow row, int variantIdx) throws Exception {
		// 1) Load source
		java.nio.file.Path in = java.nio.file.Paths.get(row.in);
		java.awt.image.BufferedImage src = readPng(in);

		// 2) Compute effective rules (row overrides → fall back to CLI defaults)
		long baseSeed = (row.seed != null ? row.seed : a.seed);
		long effSeed = baseSeed ^ (a.seedMix + 0x9E3779B97F4A7C15L * variantIdx);

		// Create PaletteRules using Builder pattern for flexibility
		PaletteRules.Builder builder = new PaletteRules.Builder()
				.seed(effSeed)
				.scheme(row.scheme != null ? row.scheme.toLowerCase() : a.scheme.toLowerCase())
				.hueBase(or(row.hueBase, a.hueBase))
				.hueJitter(or(row.hueJitter, a.hueJitter))
				.satBase(or(row.satBase, a.satBase))
				.satVar(or(row.satVar, a.satVar))
				.valBase(or(row.valBase, a.valBase))
				.valVar(or(row.valVar, a.valVar))
				.enforceContrast(row.enforceContrast != null ? row.enforceContrast : a.enforceContrast);

		// Set new parameters if provided
		if (row.roughness != null) builder.roughness(row.roughness);
		else if (a.roughness != null) builder.roughness(a.roughness);

		if (row.anisotropy != null) builder.anisotropy(row.anisotropy);
		else if (a.anisotropy != null) builder.anisotropy(a.anisotropy);

		if (row.speckle != null) builder.speckle(row.speckle);
		else if (a.speckle != null) builder.speckle(a.speckle);

		if (row.grain != null) builder.grain(row.grain);
		else if (a.grain != null) builder.grain(a.grain);

		if (row.temperature != null) builder.temperature(row.temperature);
		else if (a.temperature != null) builder.temperature(a.temperature);

		if (row.brightness != null) builder.brightness(row.brightness);
		else if (a.brightness != null) builder.brightness(a.brightness);

		if (row.preset != null) builder.preset(row.preset);
		else if (a.preset != null) builder.preset(a.preset);

		if (row.harmony != null) builder.harmony(row.harmony);
		else if (a.harmony != null) builder.harmony(a.harmony);

		PaletteRules rules = builder.build();

		Palette pal = PaletteDeriver.derive(a.materialKey, rules);

		// 4) Build ramp (OKLab recommended)
		int[] ramp = Recolor.buildRampOkLab(pal.stops());

		// 5) Recolor with rank-locked mapper
		boolean reverseOrder = row.reverseOrder != null ? row.reverseOrder : a.reverseOrder;
		boolean invert = row.invert != null ? row.invert : a.invert;
		float gamma = row.gamma != null ? row.gamma : a.gamma;

		float rankCurve = row.rankCurve != null ? row.rankCurve : (a.rankCurve != null ? a.rankCurve : 1.0f);
		float rampLo    = row.rampLo    != null ? row.rampLo    : (a.rampLo    != null ? a.rampLo    : 0.0f);
		float rampHi    = row.rampHi    != null ? row.rampHi    : (a.rampHi    != null ? a.rampHi    : 1.0f);

		BufferedImage dst = Recolor.applyLockedRank(
				src, ramp, reverseOrder, invert, gamma,
				rankCurve, rampLo, rampHi
		);

		// 6) Resolve output path (supports {i} for variant index)
		String outPath = row.out.replace("{i}", String.valueOf(variantIdx));
		java.nio.file.Path out = java.nio.file.Paths.get(outPath);
		writePng(dst, out);
		System.out.println("✓ " + out.toAbsolutePath());
	}

	// --- batch readers (JSON | TSV | directory glob) ---
	private static List<BatchRow> readBatch(String batchPath) throws java.io.IOException {
		java.nio.file.Path p = java.nio.file.Paths.get(batchPath);
		String s = p.toString().toLowerCase();

		if (java.nio.file.Files.isDirectory(p)) {
			// All PNGs in dir → same scheme/settings, output alongside with suffix “_gen.png”
			List<BatchRow> rows = new java.util.ArrayList<>();
			try (var stream = java.nio.file.Files.walk(p)) {
				stream.filter(f -> f.toString().toLowerCase().endsWith(".png")).forEach(f -> {
					BatchRow r = new BatchRow();
					r.in = f.toString();
					String base = f.toString().substring(0, f.toString().length() - 4);
					r.out = base + "_gen.png";
					rows.add(r);
				});
			}
			return rows;
		}

		String txt = java.nio.file.Files.readString(p);
		if (s.endsWith(".json")) {
			// If the JSON has "bundle": "WOOD_TEXTURE_SET", expand it first.
			var arr = com.google.gson.JsonParser.parseString(txt).getAsJsonArray();
			boolean hasBundle = false;
			java.util.List<com.google.gson.JsonObject> raw = new java.util.ArrayList<>();
			for (var el : arr) {
				var obj = el.getAsJsonObject();
				if (obj.has("bundle")) hasBundle = true;
				raw.add(obj);
			}

			if (hasBundle) {
				var expanded = expandWoodTextureBundles(raw);
				var rows = new java.util.ArrayList<BatchRow>();
				var gson = new com.google.gson.Gson();

				// Handle flat jobs: perform COPY_FILE here, keep recolor jobs as BatchRow
				for (var job : expanded) {
					if (job.has("internal") && "COPY_FILE".equals(job.get("internal").getAsString())) {
						java.nio.file.Path from = java.nio.file.Path.of(job.get("from").getAsString());
						java.nio.file.Path to = java.nio.file.Path.of(job.get("to").getAsString());
						java.nio.file.Files.createDirectories(to.getParent());
						java.nio.file.Files.copy(from, to, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
						System.out.println("✓ " + to.toAbsolutePath());
						continue;
					}
					rows.add(gson.fromJson(job, BatchRow.class));
				}
				return rows;
			} else {
				// Normal (non-bundle) JSON: [{ in, out, key, scheme, seed, ... }]
				com.google.gson.reflect.TypeToken<java.util.List<BatchRow>> tt =
						new com.google.gson.reflect.TypeToken<>() {
						};
				return new com.google.gson.Gson().fromJson(txt, tt.getType());
			}
		} else if (s.endsWith(".tsv") || s.endsWith(".csv")) {
			// Header-based: in,out,key,scheme,seed,variants,hueBase,...
			boolean csv = s.endsWith(".csv");
			String[] lines = txt.split("\\R");
			String[] hdr = split(lines[0], csv);
			java.util.Map<String, Integer> idx = new java.util.HashMap<>();
			for (int i = 0; i < hdr.length; i++) idx.put(hdr[i].trim().toLowerCase(), i);

			java.util.List<BatchRow> rows = new java.util.ArrayList<>();
			for (int li = 1; li < lines.length; li++) {
				if (lines[li].isBlank()) continue;
				String[] cols = split(lines[li], csv);
				BatchRow r = new BatchRow();
				r.in = get(cols, idx, "in");
				r.out = get(cols, idx, "out");
				r.key = or(get(cols, idx, "key"), "yourmod:material");
				r.scheme = or(get(cols, idx, "scheme"), "METAL");
				r.seed = parseLongN(get(cols, idx, "seed"));
				r.variants = parseIntN(get(cols, idx, "variants"));
				r.hueBase = parseFloatN(get(cols, idx, "hueBase"));
				r.hueJitter = parseFloatN(get(cols, idx, "hueJitterDeg"));
				r.satBase = parseFloatN(get(cols, idx, "satBase"));
				r.satVar = parseFloatN(get(cols, idx, "satVar"));
				r.valBase = parseFloatN(get(cols, idx, "valBase"));
				r.valVar = parseFloatN(get(cols, idx, "valVar"));
				r.reverseOrder = parseBoolN(get(cols, idx, "reverseOrder"));
				r.invert = parseBoolN(get(cols, idx, "invert"));
				r.gamma = parseFloatN(get(cols, idx, "gamma"));

				// New parameters
				r.roughness = parseFloatN(get(cols, idx, "roughness"));
				r.anisotropy = parseFloatN(get(cols, idx, "anisotropy"));
				r.speckle = parseFloatN(get(cols, idx, "speckle"));
				r.grain = parseFloatN(get(cols, idx, "grain"));
				r.temperature = parseFloatN(get(cols, idx, "temperature"));
				r.brightness = parseFloatN(get(cols, idx, "brightness"));
				r.preset = get(cols, idx, "preset");
				r.harmony = get(cols, idx, "harmony");
				r.enforceContrast = parseBoolN(get(cols, idx, "enforceContrast"));

				rows.add(r);
			}
			return rows;
		}
		throw new IllegalArgumentException("Unsupported batch file: " + p);
	}

	static List<com.google.gson.JsonObject> expandWoodTextureBundles(List<com.google.gson.JsonObject> rows) {
		var out = new java.util.ArrayList<com.google.gson.JsonObject>();

		// merge: base <- override
		java.util.function.BiFunction<com.google.gson.JsonObject, com.google.gson.JsonObject, com.google.gson.JsonObject> mergePalette =
				(base, override_) -> {
					var res = new com.google.gson.JsonObject();
					if (base != null) for (var e : base.entrySet()) res.add(e.getKey(), e.getValue());
					if (override_ != null) for (var e : override_.entrySet()) res.add(e.getKey(), e.getValue());
					return res;
				};

		for (var row : rows) {
			var bundle = row.has("bundle") ? row.get("bundle").getAsString() : "";
			if (!"WOOD_TEXTURE_SET".equalsIgnoreCase(bundle)) { out.add(row); continue; }

			String name    = row.get("name").getAsString();
			String keyBase = row.get("keyBase").getAsString();
			String outDir  = row.has("outDir") ? row.get("outDir").getAsString() : "dev_out";
			var basePalette = row.has("palette") ? row.getAsJsonObject("palette") : null;

			var palettes = row.has("palettes") ? row.getAsJsonObject("palettes") : null;
			var palDefault         = palettes != null && palettes.has("default")         ? palettes.getAsJsonObject("default")         : null;
			var palLogSide         = palettes != null && palettes.has("logSide")         ? palettes.getAsJsonObject("logSide")         : null;
			var palLogTop          = palettes != null && palettes.has("logTop")          ? palettes.getAsJsonObject("logTop")          : null;
			var palStrippedLogSide = palettes != null && palettes.has("strippedLogSide") ? palettes.getAsJsonObject("strippedLogSide") : null;
			var palStrippedLogTop  = palettes != null && palettes.has("strippedLogTop")  ? palettes.getAsJsonObject("strippedLogTop")  : null;
			var palDoor            = palettes != null && palettes.has("door")            ? palettes.getAsJsonObject("door")            : null;
			var palTrapdoor        = palettes != null && palettes.has("trapdoor")        ? palettes.getAsJsonObject("trapdoor")        : null;
			var parts = row.getAsJsonObject("parts");

			java.util.function.BiConsumer<com.google.gson.JsonObject, com.google.gson.JsonObject> copyPalette = (job, chosen) -> {
				if (chosen == null) return;
				String[] keys = {
						"scheme","seed","hueBase","hueJitterDeg","satBase","satVar",
						"valBase","valVar","grain","speckle","brightness","temperature",
						"enforceContrast","harmony","preset"
				};
				for (var k : keys) if (chosen.has(k)) job.add(k, chosen.get(k));
			};

			java.util.function.Function<String, com.google.gson.JsonObject> paletteForPart = part -> {
				var partObj = parts.has(part) ? parts.getAsJsonObject(part) : null;
				var partPal = (partObj != null && partObj.has("palette")) ? partObj.getAsJsonObject("palette") : null;
				com.google.gson.JsonObject rolePal = switch (part) {
					case "log_side"          -> palLogSide;
					case "log_top"           -> palLogTop;
					case "stripped_log_side" -> palStrippedLogSide;
					case "stripped_log_top"  -> palStrippedLogTop;
					case "door_top", "door_bottom" -> palDoor;
					case "trapdoor"          -> palTrapdoor;
					default                  -> palDefault;
				};
				var merged = mergePalette.apply(palDefault, basePalette);
				merged = mergePalette.apply(merged, rolePal);
				merged = mergePalette.apply(merged, partPal);
				return merged;
			};

			String base = outDir + "/blocks/wood/";
			String baseWithName = base + name;
			String planksOut = baseWithName + "_planks.png";

			java.util.function.BiConsumer<String,String> addRecolor = (part, outTex) -> {
				if (!parts.has(part)) return; // <- avoid NPE
				var p = parts.getAsJsonObject(part);
				if (p.has("alias")) return;
				var job = new com.google.gson.JsonObject();
				job.addProperty("in", p.get("in").getAsString());
				job.addProperty("out", outTex);
				job.addProperty("key", keyBase + ":" + part);
				var chosen = paletteForPart.apply(part);
				copyPalette.accept(job, chosen);
				out.add(job);
			};

			// real recolors
			addRecolor.accept("planks",             planksOut);
			addRecolor.accept("log_side",           baseWithName + "_log.png");
			addRecolor.accept("log_top",            baseWithName + "_log_top.png");
			addRecolor.accept("stripped_log_side",  base + "stripped_" + name + "_log.png");      // unified path
			addRecolor.accept("stripped_log_top",   base + "stripped_" + name + "_log_top.png");  // unified path
			addRecolor.accept("door_top",           baseWithName + "_door_top.png");
			addRecolor.accept("door_bottom",        baseWithName + "_door_bottom.png");
			addRecolor.accept("trapdoor",           baseWithName + "_trapdoor.png");
			addRecolor.accept("ladder",             baseWithName + "_ladder.png");

			// If you want aliases back later, re-enable the COPY_FILE section.
		}
		return out;
	}

	private static String[] split(String line, boolean csv) {
		if (!csv) return line.split("\\t");
		// simple CSV (no quotes)
		return line.split(",");
	}

	private static String get(String[] cols, java.util.Map<String, Integer> idx, String k) {
		Integer i = idx.get(k.toLowerCase());
		return (i == null || i >= cols.length) ? null : cols[i].trim();
	}

	private static String or(String v, String d) {
		return v != null && !v.isBlank() ? v : d;
	}

	private static float or(Float v, float d) {
		return v != null ? v : d;
	}

	private static Long parseLongN(String s) {
		try {
			return s == null ? null : Long.parseUnsignedLong(s);
		} catch (Exception e) {
			return null;
		}
	}

	private static Integer parseIntN(String s) {
		try {
			return s == null ? null : Integer.parseInt(s);
		} catch (Exception e) {
			return null;
		}
	}

	private static Float parseFloatN(String s) {
		try {
			return s == null ? null : Float.parseFloat(s);
		} catch (Exception e) {
			return null;
		}
	}

	private static Boolean parseBoolN(String s) {
		if (s == null) return null;
		return Boolean.parseBoolean(s);
	}

	// ---------- Helpers ----------
	private static BufferedImage readPng(Path p) throws IOException {
		if (!Files.isRegularFile(p)) throw new IllegalArgumentException("Missing base PNG: " + p);
		return ImageIO.read(Files.newInputStream(p));
	}

	private static void writePng(BufferedImage img, Path out) throws IOException {
		Files.createDirectories(out.getParent());
		ImageIO.write(img, "png", Files.newOutputStream(out));
	}

	static final class BatchRow {
		String in, out, key = "yourmod:material";
		String scheme = "METAL";         // METAL | GEM | STONE | WOOD | METAL_SHIFT
		Long seed;                     // optional per-row seed (mixes with global)
		Float hueBase, hueJitter, satBase, satVar, valBase, valVar; // optional overrides
		Boolean reverseOrder, invert;
		Float gamma;
		Integer variants;                // generate N variants by bumping seed
		Float rankCurve, rampLo, rampHi;

		// New parameters for enhanced PaletteRules
		Float roughness, anisotropy, speckle, grain, temperature, brightness;
		String preset, harmony;
		Boolean enforceContrast;
	}

	// ---------- Tiny arg parser with sane defaults ----------
	private static final class Args {
		Path inPng, outPng;
		String materialKey = "yourmod:example";
		String scheme = "metal";
		long seed = 11_400_714_819_323_198_48L; // default golden ratio-ish
		float hueBase = 28f, hueJitter = 14f;
		float satBase = 0.70f, satVar = 0.10f;
		float valBase = 0.82f, valVar = 0.12f;
		String batchPath;
		int threads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
		long seedMix = 0L;
		boolean reverseOrder = false;
		boolean invert = false;
		float gamma = 1.0f;

		// New CLI parameters
		Float roughness, anisotropy, speckle, grain, temperature, brightness;
		Float rankCurve, rampLo, rampHi;
		String preset, harmony;
		Boolean enforceContrast = true;

		static Args parse(String[] argv) {
			Map<String, String> m = new HashMap<>();
			for (int i = 0; i < argv.length; i++) {
				String k = argv[i];
				String v = (i + 1 < argv.length && !argv[i + 1].startsWith("--")) ? argv[++i] : "true";
				m.put(k, v);
			}
			Args a = new Args();

			// Required
			a.inPng = Paths.get(req(m, "--in", "dev_assets/items"));
			a.outPng = Paths.get(req(m, "--out", "dev_out/items"));

			// Optional
			a.materialKey = m.getOrDefault("--key", a.materialKey);
			a.scheme = m.getOrDefault("--scheme", a.scheme);
			a.seed = parseLong(m, "--seed", a.seed);
			a.hueBase = parseFloat(m, "--hueBase", a.hueBase);
			a.hueJitter = parseFloat(m, "--hueJitterDeg", a.hueJitter);
			a.satBase = parseFloat(m, "--satBase", a.satBase);
			a.satVar = parseFloat(m, "--satVar", a.satVar);
			a.valBase = parseFloat(m, "--valBase", a.valBase);
			a.valVar = parseFloat(m, "--valVar", a.valVar);
			a.batchPath = m.get("--batch");                      // file/dir path
			a.threads = (int) parseLong(m, "--threads", a.threads);
			a.seedMix = parseLong(m, "--seedMix", 0L);
			a.reverseOrder = Boolean.parseBoolean(m.getOrDefault("--reverseOrder", "false"));
			a.invert = Boolean.parseBoolean(m.getOrDefault("--invert", "false"));
			a.gamma = parseFloat(m, "--gamma", 1.0f);

			// New parameters
			a.roughness = parseFloatN(m.get("--roughness"));
			a.rankCurve = parseFloatN(m.get("--rankCurve"));
			a.rampHi = parseFloatN(m.get("--rampHi"));
			a.rampLo = parseFloatN(m.get("--rampLo"));
			a.anisotropy = parseFloatN(m.get("--anisotropy"));
			a.speckle = parseFloatN(m.get("--speckle"));
			a.grain = parseFloatN(m.get("--grain"));
			a.temperature = parseFloatN(m.get("--temperature"));
			a.brightness = parseFloatN(m.get("--brightness"));
			a.preset = m.get("--preset");
			a.harmony = m.get("--harmony");
			a.enforceContrast = Boolean.parseBoolean(m.getOrDefault("--enforceContrast", "true"));

			return a;
		}

		private static String req(Map<String, String> m, String key, String d) {
			String v = m.get(key);
			if (v == null) return d;
			return v;
		}

		private static long parseLong(Map<String, String> m, String k, long d) {
			try {
				return m.containsKey(k) ? Long.parseUnsignedLong(m.get(k)) : d;
			} catch (Exception e) {
				return d;
			}
		}

		private static float parseFloat(Map<String, String> m, String k, float d) {
			try {
				return m.containsKey(k) ? Float.parseFloat(m.get(k)) : d;
			} catch (Exception e) {
				return d;
			}
		}

		private static Float parseFloatN(String s) {
			try {
				return s == null ? null : Float.parseFloat(s);
			} catch (Exception e) {
				return null;
			}
		}
	}
}
