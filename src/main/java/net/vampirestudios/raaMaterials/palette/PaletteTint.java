package net.vampirestudios.raaMaterials.palette;

import com.mojang.blaze3d.platform.NativeImage;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Generates tinted textures by mapping a base mask's lightness to a MaterialPalette gradient.
 * Writes the result into the given RuntimeResourcePack.
 */
public final class PaletteTint {

    /** Simple palette with color stops. Offsets are in [0..1], ascending. Colors are 0xRRGGBB. */
    public record MaterialPalette(List<Stop> stops) {
        public record Stop(float at, int rgb) {}
        public int sample(float t) {
            if (stops.isEmpty()) return 0xFFFFFF;
            if (t <= stops.getFirst().at()) return stops.getFirst().rgb();
            if (t >= stops.getLast().at()) return stops.getLast().rgb();
            for (int i = 0; i < stops.size()-1; i++) {
                var a = stops.get(i);
                var b = stops.get(i+1);
                if (t >= a.at() && t <= b.at()) {
                    float u = (t - a.at()) / Math.max(1e-6f, (b.at() - a.at()));
                    return lerpRGB(a.rgb(), b.rgb(), u);
                }
            }
            return stops.getLast().rgb();
        }
    }

    /** Generate a tinted texture and add it to the pack at targetPath ("textures/…​.png"). */
    public static void generateTintedTexture(RuntimeResourcePack rp,
                                             Identifier baseMask,     // e.g. raa_materials:base/ingot_mask
                                             Identifier targetPath,   // e.g. raa_materials:textures/item/material_ingot/<mat>.png
                                             MaterialPalette palette) throws IOException {
        NativeImage src = readNativeImage(baseMask);
        NativeImage out = new NativeImage(src.getWidth(), src.getHeight(), false);

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {
                int argb = src.getPixel(x, y);
                int a = (argb >>> 24) & 0xFF;
                if (a == 0) { // fully transparent: passthrough
                    out.setPixel(x, y, 0x00000000);
                    continue;
                }

                // Lightness from source (perceived luminance works well for grayscale masks too)
                int r = (argb)       & 0xFF;
                int g = (argb >>> 8) & 0xFF;
                int b = (argb >>>16) & 0xFF;
                float l = clamp01((0.2126f * r + 0.7152f * g + 0.0722f * b) / 255f);

                int rgb = palette.sample(l); // 0xRRGGBB
                int rr = (rgb >> 16) & 0xFF;
                int gg = (rgb >> 8)  & 0xFF;
                int bb = (rgb)       & 0xFF;

                out.setPixel(x, y, (a << 24) | (rr << 16) | (gg << 8) | bb);
            }
        }

        // Write PNG to the pack
        try (var baos = new ByteArrayOutputStream()) {
//            out.writeToFile(new FileImageOutputStream(baos.toByteArray()));
            rp.addAsset(targetPath, baos.toByteArray());
        } finally {
            out.close();
            src.close();
        }
    }

    // ===== Utils =====

    private static NativeImage readNativeImage(Identifier rl) throws IOException {
        var rm = Minecraft.getInstance().getResourceManager();
        try (InputStream in = rm.getResource(rl).orElseThrow().open()) {
            return NativeImage.read(in);
        }
    }

    private static float clamp01(float v) { return v < 0 ? 0 : (v > 1 ? 1 : v); }

    private static int lerpRGB(int c0, int c1, float t) {
        int r = (int) ( ((c0 >> 16) & 0xFF) * (1 - t) + ((c1 >> 16) & 0xFF) * t );
        int g = (int) ( ((c0 >>  8) & 0xFF) * (1 - t) + ((c1 >>  8) & 0xFF) * t );
        int b = (int) ( ((c0      ) & 0xFF) * (1 - t) + ((c1      ) & 0xFF) * t );
        return (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    // Quick palette helpers (optional)
    public static MaterialPalette triBand(int dark, int mid, int light) {
        return new MaterialPalette(List.of(
                new MaterialPalette.Stop(0.00f, dark),
                new MaterialPalette.Stop(0.50f, mid),
                new MaterialPalette.Stop(1.00f, light)
        ));
    }
    public static int darken(int rgb, float f){ return scale(rgb, 1f - f); }
    public static int lighten(int rgb, float f){ return blend(rgb, 0xFFFFFF, f); }
    public static int scale(int rgb, float s) {
        int r = Math.min(255, Math.max(0, (int)(((rgb>>16)&0xFF)*s)));
        int g = Math.min(255, Math.max(0, (int)(((rgb>> 8)&0xFF)*s)));
        int b = Math.min(255, Math.max(0, (int)(((rgb    )&0xFF)*s)));
        return (r<<16)|(g<<8)|b;
    }
    public static int blend(int rgbA, int rgbB, float t) {
        int ar=(rgbA>>16)&0xFF, ag=(rgbA>>8)&0xFF, ab=rgbA&0xFF;
        int br=(rgbB>>16)&0xFF, bg=(rgbB>>8)&0xFF, bb=rgbB&0xFF;
        int r=(int)(ar*(1-t)+br*t), g=(int)(ag*(1-t)+bg*t), b=(int)(ab*(1-t)+bb*t);
        return (r<<16)|(g<<8)|b;
    }
}
