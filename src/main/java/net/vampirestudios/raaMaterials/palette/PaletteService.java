// src/main/java/your/mod/palette/PaletteService.java
package net.vampirestudios.raaMaterials.palette;

import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.resources.ResourceLocation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.devtech.arrp.api.RuntimeResourcePack.create;

public final class PaletteService {
	private static final RuntimeResourcePack RRP = create("raa_materials:palettes");
	private static final Map<String, Palette> CACHE = new HashMap<>();
	private static final Map<String, int[]> RAMPS = new HashMap<>();

	public static void init() {
		RRPCallback.BEFORE_VANILLA.register(packs -> packs.add(RRP));
	}
	
	public static void registerMaterial(String materialKey,
										PaletteRules rules,
										ResourceLocation baseTexture,           // e.g. "raa_materials:materials/base_ingot"
										ResourceLocation outTexturePath,        // e.g. "raa_materials:textures/item/ingot_myrite.png"
										ResourceLocation outItemModelPath) {    // e.g. "raa_materials:item/myrite_ingot"
		Palette p = CACHE.computeIfAbsent(materialKey, k -> PaletteDeriver.derive(k, rules));
		int[] ramp = RAMPS.computeIfAbsent(materialKey, k -> Recolor.buildRamp(p.stops()));

		float rankCurve = 1.0f;
		float rampLo    = 0.0f;
		float rampHi    = 1.0f;

		// Load base image from classpath/resources (your mod resources)
		try {
			BufferedImage src = ImageIO.read(Objects.requireNonNull(
					PaletteService.class.getResourceAsStream("/assets/" + baseTexture.toString().replace(':', '/') + ".png"),
					"Missing base texture: " + baseTexture
			));
			BufferedImage out = Recolor.applyLockedRank(
					src, ramp, false, false, 1.0f,
					rankCurve, rampLo, rampHi
			);
//			BufferedImage out = Recolor.apply(src, ramp, p.dither());
			byte[] png = toPNG(out);

			RRP.addAsset(outTexturePath, png);

//			JModel itemModel = JModel.model("minecraft:item/generated")
//					.textures(JModel.textures().layer0(outTexturePath.toString().replace("raa_materials:textures/", "raa_materials:")));
//			RRP.addModel(itemModel, outItemModelPath);
//
//			// Optional: item info (1.21+ item model definitions, if you want per-layer tricks)
//			JItemModel im = JItemModel.model(outItemModelPath.toString());
//			JItemInfo info = JItemInfo.item().model(im);
//			RRP.addItemModelInfo(info, outItemModelPath);
		} catch (Exception e) {
			throw new RuntimeException("Palette generation failed for " + materialKey + ": " + e.getMessage(), e);
		}
	}
	
	private static byte[] toPNG(BufferedImage img) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "png", baos);
		return baos.toByteArray();
	}

	public static int color(String materialKey, int idx) {
		Palette p = CACHE.get(materialKey);
		if (p == null) return 0xFFFFFFFF;
		int[] ramp = RAMPS.get(materialKey);
		if (ramp == null) ramp = Recolor.buildRamp(p.stops());
		return ramp[Math.max(0, Math.min(255, idx))];
	}
}
