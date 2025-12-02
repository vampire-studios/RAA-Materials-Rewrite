// src/main/java/your/mod/palette/Recolor.java
package net.vampirestudios.raaMaterials.palette;

import net.vampirestudios.raaMaterials.palette.Palette.Quantize;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static net.vampirestudios.raaMaterials.palette.OKLCh.oklabL;

public final class Recolor {
    private Recolor(){}

    public enum MapMode { CONTINUOUS, LOCKED_RANK }

    /** Build a 256-color ramp once from stops. */
    public static int[] buildRamp(List<Palette.Stop> stops){
        int[] ramp = new int[256];
        for (int i=0;i<256;i++){
            float t = i/255f;
            ramp[i] = lerpStops(stops, t);
        }
        return ramp;
    }

    public static int[] buildRampOkLab(java.util.List<Palette.Stop> stops){
        int max = 256*2;
        int[] ramp = new int[max];
        for(int i=0;i<max;i++){
            float t=i/255f;
            int c = lerpStopsOKLab(stops, t);
            ramp[i]=c;
        }
        return ramp;
    }

    /** Rank-locked recolor: preserves the number of shades and their ordering. */
    public static BufferedImage applyLockedRank(
            BufferedImage src,
            int[] ramp,
            boolean reverseOrder,      // true = darkest source → lightest target
            boolean invert,            // invert source luminance before ranking
            float gamma,                // gamma on normalized luminance before ranking

            // NEW
            float rankCurve,     // >= 0.1; 1 = linear, 2 = bias to darks, 0.5 = bias to lights
            float rampLo,        // 0..1: lower bound of ramp window to sample from
            float rampHi         // 0..1: upper bound (exclusive/inclusive doesn’t matter here)
    ) {
        final int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // 1) Collect unique luminance AFTER invert/gamma so ranks match what we map.
        TreeSet<Integer> uniques = new TreeSet<>();
        int[][] lumBuf = new int[h][w];
        for (int y=0;y<h;y++){
            for (int x=0;x<w;x++){
                int argb = src.getRGB(x,y);
                int a = (argb>>>24)&0xFF;
                if (a == 0) { lumBuf[y][x] = -1; continue; }
                float L = oklabL(argb); // 0..1

                if (invert) L = 1f - L;
                if (gamma != 1.0f) {
                    L = (float)Math.pow(L, gamma);
                }
                int lum = clamp(Math.round(L * 255f), 0, 255);

                lumBuf[y][x] = lum;
                uniques.add(lum);
            }
        }
        // Edge: solid transparent image
        if (uniques.isEmpty()) return out;

        // 2) Build rank → rampIndex mapping with preserved order (or reversed).

        int U = uniques.size();
        int[] uniqueLevels = uniques.stream().mapToInt(Integer::intValue).toArray();
        int[] rankOfLum = new int[256];
        Arrays.fill(rankOfLum, -1);
        for (int i=0;i<U;i++) rankOfLum[uniqueLevels[i]] = i;

        // clamp new params
        rankCurve = Math.max(0.1f, rankCurve);
        rampLo = Math.max(0f, Math.min(1f, rampLo));
        rampHi = Math.max(rampLo, Math.min(1f, rampHi));
        float rampSpan = Math.max(1e-6f, rampHi - rampLo);

        int[] colorOfRank = new int[U];
        for (int i=0;i<U;i++){
            int rank = reverseOrder ? (U-1 - i) : i;
            float tLinear = (U==1) ? 0f : (i / (float)(U-1));
            // curve the distribution of ranks across the ramp
            float tCurved = (float)Math.pow(tLinear, rankCurve);
            float tWindow = rampLo + tCurved * rampSpan;
            int rampIdx = clamp(Math.round(tWindow * 255f), 0, 255);
            colorOfRank[i] = ramp[rampIdx];
        }

        // 3) Paint using exact ranks; unknown luminance (should not happen) → nearest unique
        for (int y=0;y<h;y++){
            for (int x=0;x<w;x++){
                int lum = lumBuf[y][x];
                if (lum < 0) { out.setRGB(x,y,0); continue; }
                int rank = rankOfLum[lum];
                if (rank < 0) {
                    // nearest unique (safety)
                    int idx = Arrays.binarySearch(uniqueLevels, lum);
                    if (idx < 0) {
                        int ins = -idx-1;
                        int i1 = clamp(ins, 0, U-1);
                        int i0 = clamp(ins-1, 0, U-1);
                        rank = (Math.abs(uniqueLevels[i0]-lum) <= Math.abs(uniqueLevels[i1]-lum)) ? i0 : i1;
                    } else rank = idx;
                }
                int srcARGB = src.getRGB(x,y);
                int a = (srcARGB>>>24)&0xFF;
                int rgb = colorOfRank[rank] & 0x00FFFFFF;
                out.setRGB(x,y, (a<<24)|rgb);
            }
        }
        return out;
    }

    /**
     * Rank-locked recolor with per-pixel hue/sat jitter (mottle), optional speckles,
     * and directional grain. Preserves the number of shades in the source and their order.
     *
     * @param src            source image (RGBA)
     * @param ramp           256-entry ramp (build with OKLab if possible)
     * @param reverseOrder   false = darkest gray → darkest ramp color (recommended)
     * @param invert         invert source luminance before ranking (usually false if bases are "dark outline")
     * @param gamma          gamma on luminance before ranking (1.0f = none)
     * @param noiseSeed      stable seed for noise (e.g., rules.seed())
     * @param hueJitterDeg   per-pixel hue jitter amplitude in degrees (e.g., 4–10)
     * @param satJitter      per-pixel saturation jitter amplitude (0..~0.15)
     * @param speckle        0..1 sparse mixes with neighbor rank (0.10–0.30 is subtle)
     * @param grain          0..1 directional banding strength (0.05–0.20 works for stone/wood)
     */
    public static BufferedImage applyLockedRankMottled(
            BufferedImage src, int[] ramp,
            boolean reverseOrder, boolean invert, float gamma,
            long noiseSeed,
            float hueJitterDeg, float satJitter,
            float speckle, float grain
    ) {
        final int w = src.getWidth(), h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // --- Prepass: luminance → ranks (after invert/gamma), then rank → base color ---
        java.util.TreeSet<Integer> uniques = new java.util.TreeSet<>();
        int[][] lumBuf = new int[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = src.getRGB(x, y);
                int a = (argb >>> 24) & 0xFF;
                if (a == 0) { lumBuf[y][x] = -1; continue; }

                float L = oklabL(argb); // 0..1

                if (invert) L = 1f - L;
                if (gamma != 1.0f) {
                    L = (float)Math.pow(L, gamma);
                }
                int lum = clamp(Math.round(L * 255f), 0, 255);

                lumBuf[y][x] = lum;
                uniques.add(lum);
            }
        }
        if (uniques.isEmpty()) return out;

        int U = uniques.size();
        int[] uniqueLevels = uniques.stream().mapToInt(Integer::intValue).toArray();

        // luminance → rank index (exact matches)
        int[] rankOfLum = new int[256];
        java.util.Arrays.fill(rankOfLum, -1);
        for (int i = 0; i < U; i++) rankOfLum[uniqueLevels[i]] = i;

        // rank → ramp color (spread across 0..255 ramp)
        int[] colorOfRank = new int[U];
        for (int i = 0; i < U; i++) {
            float t = (U == 1) ? 0f : (i / (float) (U - 1));
            int rampIdx = clamp(Math.round(t * 255f), 0, 255);
            int rank = reverseOrder ? (U - 1 - i) : i;
            colorOfRank[rank] = ramp[rampIdx];
        }

        // --- Grain orientation/frequency derived from seed (deterministic) ---
        float theta = hashAngle(noiseSeed);             // grain direction in radians
        float gx = (float) Math.cos(theta), gy = (float) Math.sin(theta);
        float gFreq = 0.65f + 0.45f * hash01(noiseSeed ^ 0x6a09e667f3bcc909L); // 0.65..1.10
        float gPhase = 6.2831853f * hash01(noiseSeed ^ 0xbb67ae8584caa73bL);   // 0..2π

        // clamp inputs
        hueJitterDeg = Math.max(0f, hueJitterDeg);
        satJitter = Math.max(0f, satJitter);
        speckle = clamp01f(speckle);
        grain = clamp01f(grain);

        // --- Paint with mottle ---
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int lum = lumBuf[y][x];
                if (lum < 0) { out.setRGB(x, y, 0); continue; }

                // find rank (or nearest)
                int rank = rankOfLum[lum];
                if (rank < 0) {
                    int idx = java.util.Arrays.binarySearch(uniqueLevels, lum);
                    if (idx < 0) {
                        int ins = -idx - 1;
                        int i1 = clamp(ins, 0, U - 1), i0 = clamp(ins - 1, 0, U - 1);
                        rank = (Math.abs(uniqueLevels[i0] - lum) <= Math.abs(uniqueLevels[i1] - lum)) ? i0 : i1;
                    } else rank = idx;
                }

                int srcARGB = src.getRGB(x, y);
                int a = (srcARGB >>> 24) & 0xFF;

                // base color by rank
                int baseRGB = colorOfRank[rank] & 0x00FFFFFF;

                // blue-noise core (−1..+1), deterministic
                float n = blueNoise(x, y, noiseSeed);

                // directional grain (−1..+1) along (gx, gy)
                float proj = (x * gx + y * gy) * gFreq * 0.35f;  // scale controls band spacing
                float gWave = (float) Math.sin(proj + gPhase);   // smooth stripe
                float gNoise = gWave * (0.6f + 0.4f * blueNoise(x * 2, y * 2, noiseSeed ^ 0x3c6ef372fe94f82aL));
                float gTerm = grain * gNoise;

                // combine into hue/sat deltas
                float dH = hueJitterDeg * (0.7f * n + 0.3f * gTerm);
                float dS = satJitter    * (0.5f * n + 0.5f * Math.abs(gTerm));

                int rgb = hueSatJitter(baseRGB, dH, dS);

                // optional speckle: sometimes blend with neighbor rank color
                if (speckle > 0f) {
                    float s = hash01((x * 73856093L) ^ (y * 19349663L) ^ (noiseSeed * 0x9e3779b97f4a7c15L));
                    if (s < speckle) {
                        int altRank = clamp(rank + (n >= 0 ? +1 : -1), 0, U - 1);
                        int altRGB = colorOfRank[altRank] & 0x00FFFFFF;
                        float t = 0.35f + 0.30f * Math.abs(n); // 0.35..0.65
                        rgb = mixRgb(rgb, altRGB, t);
                    }
                }

                // alpha-aware saturation guard: fade chroma on edges to avoid halos
                if (a < 255) {
                    rgb = scaleSaturation(rgb, a / 255f);
                }

                out.setRGB(x, y, (a << 24) | (rgb & 0x00FFFFFF));
            }
        }
        return out;
    }


    private static float blueNoise(int x, int y, long seed) {
        long v = (x * 0x9E3779B97F4A7C15L) ^ (y * 0xC2B2AE3D27D4EB4FL) ^ seed;
        v ^= (v >>> 30); v *= 0xBF58476D1CE4E5B9L;
        v ^= (v >>> 27); v *= 0x94D049BB133111EBL;
        v ^= (v >>> 31);
        int i = (int) (v >>> 9) & 0xFFFF;
        return (i / 32767.5f) - 1f; // −1..+1
    }
    private static float hash01(long v) {
        v ^= (v >>> 33);
        v *= 0xff51afd7ed558ccdL;
        v ^= (v >>> 33);
        v *= 0xc4ceb9fe1a85ec53L;
        v ^= (v >>> 33);
        return ((v >>> 11) & 0xFFFF) / 65535f; // 0..1
    }

    // Convenience for ints (avoid sign-extension surprises)
    private static float hash01i(int v) {
        return hash01(((long) v) & 0xFFFFFFFFL);
    }

    // XY+seed helper (use this or inline; both call the base)
    private static float hash01xy(int x, int y, long seed) {
        long mix = ((long) x * 73856093L) ^ ((long) y * 19349663L) ^ seed;
        return hash01(mix);
    }
    private static float hash01(int mixed) { return hash01((long) mixed); }
    private static float hash01(int x, int y, long seed) { return hash01((x * 73856093L) ^ (y * 19349663L) ^ seed); }
    private static float hashAngle(long seed) { return 6.2831853f * hash01(seed ^ 0x1f83d9abfb41bd6bL); } // 0..2π

    private static int hueSatJitter(int rgb, float dHueDeg, float dSat) {
        float[] hsv = rgbToHsv(rgb);
        float h = (hsv[0] + dHueDeg) % 360f; if (h < 0) h += 360f;
        float s = clamp01f(hsv[1] + dSat);
        return hsvToRgb(h, s, hsv[2]);
    }

    private static int clamp255(int v) { return (v < 0) ? 0 : Math.min(v, 255); }
    private static int clamp(float v, int lo, int hi) { return clamp(Math.round(v), lo, hi); }
    private static int clamp(float v) { return clamp(Math.round(v), 0, 255); }
    private static int clamp01(int v) { return clamp(v, 0, 255); }
    private static float clamp01f(float f) { return (f < 0f) ? 0f : Math.min(f, 1f); }


    // ----- CONTINUOUS mode (kept for completeness) -----
    public static BufferedImage applyContinuous(
            BufferedImage src, int[] ramp,
            Palette.Dither dither, boolean invert, float gamma
    ){
        final int[][] BAYER4 = {
                { 0,  8,  2, 10}, {12,  4, 14,  6},
                { 3, 11,  1,  9}, {15,  7, 13,  5}
        };
        int w=src.getWidth(), h=src.getHeight();
        BufferedImage out = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        for (int y=0;y<h;y++){
            for (int x=0;x<w;x++){
                int argb = src.getRGB(x,y);
                int a = (argb>>>24)&0xFF;
                if (a==0) { out.setRGB(x,y,0); continue; }
                int r=(argb>>>16)&0xFF, g=(argb>>>8)&0xFF, b=argb&0xFF;
                int lum = (r*299 + g*587 + b*114)/1000;
                if (dither == Palette.Dither.ORDERED_4x4){
                    int threshold = BAYER4[y&3][x&3]*16;
                    lum = clamp(lum + threshold/16 - 8, 0, 255);
                }
                if (invert) lum = 255 - lum;
                if (gamma != 1.0f){
                    float t = (float)Math.pow(lum/255f, gamma);
                    lum = clamp(Math.round(t*255f), 0, 255);
                }
                int mapped = (a<<24) | (ramp[lum] & 0x00FFFFFF);
                out.setRGB(x,y, mapped);
            }
        }
        return out;
    }

    /** NEW: posterize/quantize luminance before lookup */
    public static BufferedImage apply(BufferedImage src, int[] ramp,
                                      Palette.Dither dither,
                                      Quantize quantize, int bins,
                                      boolean invert, float gamma) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Precompute UNIQUE luminance set if requested
        int[] uniqueLevels = null;
        if (quantize == Quantize.UNIQUE) uniqueLevels = collectUniqueLuminance(src);

        final int[][] BAYER4 = {
                { 0,  8,  2, 10},
                {12,  4, 14,  6},
                { 3, 11,  1,  9},
                {15,  7, 13,  5}
        };

        for (int y=0;y<src.getHeight();y++){
            for (int x=0;x<src.getWidth();x++){
                int argb = src.getRGB(x,y);
                int a = (argb>>>24)&0xFF;
                if (a == 0) { out.setRGB(x,y,0); continue; }

                int r=(argb>>>16)&0xFF, g=(argb>>>8)&0xFF, b=argb&0xFF;
                int lum = (r*299 + g*587 + b*114) / 1000; // 0..255

                // 1) Optional ordered dither (before mapping)
                if (dither == Palette.Dither.ORDERED_4x4) {
                    int threshold = BAYER4[y&3][x&3]*16; // 0..240
                    lum = clamp(lum + threshold/16 - 8, 0, 255);
                }

                // 2) Invert + gamma remap (critical for “dark outline stays dark”)
                if (invert) lum = 255 - lum;
                if (gamma != 1.0f) {
                    float t = (float) Math.pow(lum / 255f, gamma);
                    lum = clamp(Math.round(t * 255f), 0, 255);
                }

                // 3) Quantize (keep few colors)
                if (quantize == Quantize.BINS) {
                    int levels = Math.max(2, bins);
                    int bin = (lum * levels) / 256;
                    lum = clamp((int)((bin + 0.5f) * (255f/levels)), 0, 255);
                } else if (quantize == Quantize.UNIQUE && uniqueLevels.length > 0) {
                    lum = nearest(lum, uniqueLevels);
                }

//                int mapped = (a<<24) | (ramp[lum] & 0x00FFFFFF);
                int rgb = ramp[lum] & 0x00FFFFFF;

                // Specular lift on very bright source (use pre-invert/gamma lum)
                float t = lum / 255f;
                float spec = smoothstep(0.82f, 0.98f, t);       // only the top ~20% lift
                rgb = mixRgb(rgb, 0x00FFFFFF, spec);

                // Alpha-aware saturation guard to avoid color fringes on edges
                rgb = scaleSaturation(rgb, (a/255f) * 1.0f /*k*/);

                out.setRGB(x, y, (a<<24) | (rgb & 0x00FFFFFF));
                out.setRGB(x,y, rgb);
            }
        }
        return out;
    }

    // helpers:
    private static int mixRgb(int rgb, int rgb2, float t){
        int r=((rgb>>16)&255), g=((rgb>>8)&255), b=(rgb&255);
        int R=((rgb2>>16)&255),G=((rgb2>>8)&255),B=(rgb2&255);
        int nr=Math.round(r+(R-r)*t), ng=Math.round(g+(G-g)*t), nb=Math.round(b+(B-b)*t);
        return (nr<<16)|(ng<<8)|nb;
    }
    private static float smoothstep(float e0, float e1, float x){
        float t = Math.max(0f, Math.min(1f, (x-e0)/(e1-e0))); return t*t*(3f-2f*t);
    }
    private static int scaleSaturation(int rgb, float sScale){
        float[] hsv = rgbToHsv(rgb);
        hsv[1] = Math.max(0f, Math.min(1f, hsv[1]*sScale));
        return hsvToRgb(hsv[0], hsv[1], hsv[2]);
    }
    // compact HSV helpers:
    private static float[] rgbToHsv(int rgb){
        float r=((rgb>>16)&255)/255f, g=((rgb>>8)&255)/255f, b=(rgb&255)/255f;
        float max=Math.max(r,Math.max(g,b)), min=Math.min(r,Math.min(g,b)), d=max-min;
        float h=0f; if(d>1e-6f){
            if(max==r) h=((g-b)/d)%6f; else if(max==g) h=((b-r)/d)+2f; else h=((r-g)/d)+4f;
            h*=60f; if(h<0)h+=360f;
        }
        float s = max==0f?0f:d/max;
        return new float[]{h,s,max};
    }
    private static int hsvToRgb(float h,float s,float v){
        float c=v*s, x=c*(1-Math.abs(((h/60f)%2)-1)), m=v-c;
        float rr=0,gg=0,bb=0; int hi=((int)Math.floor(h/60f))%6;
        switch(hi){case 0-> {rr=c;gg=x;} case 1->{rr=x;gg=c;} case 2->{gg=c;bb=x;}
            case 3->{gg=x;bb=c;} case 4->{rr=x;bb=c;} case 5->{rr=c;}}
        int R=Math.round((rr+m)*255), G=Math.round((gg+m)*255), B=Math.round((bb+m)*255);
        return (R<<16)|(G<<8)|B;
    }

    // helpers unchanged… plus:
    private static int[] collectUniqueLuminance(BufferedImage src){
        java.util.TreeSet<Integer> set = new java.util.TreeSet<>();
        for (int y=0;y<src.getHeight();y++)
            for (int x=0;x<src.getWidth();x++){
                int argb = src.getRGB(x,y);
                int a = (argb>>>24)&0xFF; if (a==0) continue;
                int r=(argb>>>16)&0xFF, g=(argb>>>8)&0xFF, b=argb&0xFF;
                int lum = (r*299 + g*587 + b*114) / 1000;
                set.add(lum);
            }
        return set.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int nearest(int v, int[] sorted) {
        // binary search closest
        int lo = 0, hi = sorted.length-1;
        while (lo <= hi) {
            int mid = (lo+hi)>>>1;
            int mv = sorted[mid];
            if (mv < v) lo = mid+1;
            else if (mv > v) hi = mid-1;
            else return v;
        }
        int i1 = Math.max(0, Math.min(sorted.length-1, lo));
        int i0 = Math.max(0, i1-1);
        return (Math.abs(sorted[i0]-v) <= Math.abs(sorted[i1]-v)) ? sorted[i0] : sorted[i1];
    }

    private static int clamp(int v,int lo,int hi){ return Math.max(lo, Math.min(hi, v)); }

    /** Recolors a grayscale/masked image via luminance mapping + optional ordered dithering. */
    public static BufferedImage apply(BufferedImage src, int[] ramp, Palette.Dither dither){
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final int[][] BAYER4 = {
            { 0,  8,  2, 10},
            {12,  4, 14,  6},
            { 3, 11,  1,  9},
            {15,  7, 13,  5}
        };
        for (int y=0;y<src.getHeight();y++){
            for (int x=0;x<src.getWidth();x++){
                int argb = src.getRGB(x,y);
                int a = (argb>>>24) & 0xFF;
                if (a == 0) { out.setRGB(x,y,0); continue; }

                int r = (argb>>>16)&0xFF, g=(argb>>>8)&0xFF, b=argb&0xFF;
                int lum = (r*299 + g*587 + b*114) / 1000; // 0..255 perceptual

                if (dither == Palette.Dither.ORDERED_4x4) {
                    int threshold = BAYER4[y&3][x&3]*16; // 0..240
                    lum = Math.max(0, Math.min(255, lum + threshold/16 - 8));
                }

                int mapped = ramp[lum];
                // preserve original alpha
                mapped = (a<<24) | (mapped & 0x00FFFFFF);
                out.setRGB(x,y,mapped);
            }
        }
        return out;
    }

    private static int lerpStops(List<Palette.Stop> stops, float t){
        if (t<=stops.getFirst().pos()) return stops.getFirst().argb();
        if (t>=stops.getLast().pos()) return stops.getLast().argb();
        for (int i=0;i<stops.size()-1;i++){
            var a = stops.get(i); var b = stops.get(i+1);
            if (t >= a.pos() && t <= b.pos()){
                float u = (t - a.pos()) / (b.pos() - a.pos());
                return lerpARGB(a.argb(), b.argb(), u);
            }
        }
        return stops.getLast().argb();
    }

    private static int lerpStopsOKLab(java.util.List<Palette.Stop> stops, float t){
        if (t<=stops.getFirst().pos()) return stops.getFirst().argb();
        if (t>=stops.getLast().pos()) return stops.getLast().argb();
        for (int i=0;i<stops.size()-1;i++){
            var a=stops.get(i); var b=stops.get(i+1);
            if (t>=a.pos() && t<=b.pos()){
                float u=(t-a.pos())/(b.pos()-a.pos());
                return oklabLerp(a.argb(), b.argb(), u);
            }
        }
        return stops.getLast().argb();
    }
    private static int oklabLerp(int c1,int c2,float t){
        float[] L1 = srgbToOKLab(c1), L2 = srgbToOKLab(c2);
        float L = L1[0] + (L2[0]-L1[0])*t;
        float A = L1[1] + (L2[1]-L1[1])*t;
        float B = L1[2] + (L2[2]-L1[2])*t;
        return oklabToSrgbARGB(255, L,A,B);
    }

    // sRGB ↔ OKLab (compact reference implementation)
    private static float srgbToLinear(float u){ return (u<=0.04045f)? (u/12.92f) : (float)Math.pow((u+0.055f)/1.055f, 2.4); }
    private static float linearToSrgb(float u){ return (u<=0.0031308f)? (12.92f*u) : (1.055f*(float)Math.pow(u,1/2.4)-0.055f); }
    private static float[] srgbToOKLab(int rgb){
        float r=srgbToLinear(((rgb>>16)&255)/255f), g=srgbToLinear(((rgb>>8)&255)/255f), b=srgbToLinear((rgb&255)/255f);
        float l = 0.4122214708f*r + 0.5363325363f*g + 0.0514459929f*b;
        float m = 0.2119034982f*r + 0.6806995451f*g + 0.1073969566f*b;
        float s = 0.0883024619f*r + 0.2817188376f*g + 0.6299787005f*b;
        float l3=(float)Math.cbrt(l), m3=(float)Math.cbrt(m), s3=(float)Math.cbrt(s);
        float L = 0.2104542553f*l3 + 0.7936177850f*m3 - 0.0040720468f*s3;
        float A = 1.9779984951f*l3 - 2.4285922050f*m3 + 0.4505937099f*s3;
        float B = 0.0259040371f*l3 + 0.7827717662f*m3 - 0.8086757660f*s3;
        return new float[]{L,A,B};
    }
    private static int oklabToSrgbARGB(int alpha, float L,float A,float B){
        float l_= (float)Math.pow(L + 0.3963377774f*A + 0.2158037573f*B, 3);
        float m_= (float)Math.pow(L - 0.1055613458f*A - 0.0638541728f*B, 3);
        float s_= (float)Math.pow(L - 0.0894841775f*A - 1.2914855480f*B, 3);
        float r = + 4.0767416621f*l_ - 3.3077115913f*m_ + 0.2309699292f*s_;
        float g = - 1.2684380046f*l_ + 2.6097574011f*m_ - 0.3413193965f*s_;
        float b = + 0.0041960863f*l_ - 0.7034186147f*m_ + 1.6990627620f*s_;
        int R=Math.round(Math.max(0,Math.min(1,linearToSrgb(r)))*255);
        int G=Math.round(Math.max(0,Math.min(1,linearToSrgb(g)))*255);
        int Bc=Math.round(Math.max(0,Math.min(1,linearToSrgb(b)))*255);
        return (alpha<<24)|(R<<16)|(G<<8)|Bc;
    }

    private static int lerpARGB(int c1, int c2, float t){
        int a1=(c1>>>24)&0xFF, r1=(c1>>>16)&0xFF, g1=(c1>>>8)&0xFF, b1=c1&0xFF;
        int a2=(c2>>>24)&0xFF, r2=(c2>>>16)&0xFF, g2=(c2>>>8)&0xFF, b2=c2&0xFF;
        int a=(int)(a1+(a2-a1)*t);
        int r=(int)(r1+(r2-r1)*t);
        int g=(int)(g1+(g2-g1)*t);
        int b=(int)(b1+(b2-b1)*t);
        return (a<<24)|(r<<16)|(g<<8)|b;
    }
}
