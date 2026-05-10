package net.vampirestudios.raaMaterials.palette;

public class OKLCh {
    record OKLab(float L, float a, float b) { }

    // ---- sRGB → OKLab (shared core) ----

    /** Convert a packed sRGB int to OKLab {L, A, B}. Uses the fast γ=2.2 approximation. */
    private static float[] srgbToOKLab(int argb) {
        float r = (float) Math.pow(((argb >> 16) & 0xFF) / 255f, 2.2);
        float g = (float) Math.pow(((argb >>  8) & 0xFF) / 255f, 2.2);
        float b = (float) Math.pow(( argb        & 0xFF) / 255f, 2.2);

        float l = (float) Math.cbrt(0.4122214708f*r + 0.5363325363f*g + 0.0514459929f*b);
        float m = (float) Math.cbrt(0.2119034982f*r + 0.6806995451f*g + 0.1073969566f*b);
        float s = (float) Math.cbrt(0.0883024619f*r + 0.2817188376f*g + 0.6299787005f*b);

        return new float[] {
            0.2104542553f*l + 0.7936177850f*m - 0.0040720468f*s,
            1.9779984951f*l - 2.4285922050f*m + 0.4505937099f*s,
            0.0259040371f*l + 0.7827717662f*m - 0.8086757660f*s
        };
    }

    // ---- Public / package-visible API ----

    static OKLab toOKLab(int argb) {
        float[] lab = srgbToOKLab(argb);
        return new OKLab(lab[0], lab[1], lab[2]);
    }

    static float oklabL(int argb) {
        return srgbToOKLab(argb)[0];
    }

    static float[] toOKLab1(int argb) {
        return srgbToOKLab(argb);
    }

    static double deltaEOK(int argb1, int argb2) {
        float[] l1 = srgbToOKLab(argb1);
        float[] l2 = srgbToOKLab(argb2);
        float dL = l1[0] - l2[0];
        float da = l1[1] - l2[1];
        float db = l1[2] - l2[2];
        return Math.sqrt(dL*dL + da*da + db*db);
    }

    static int safeSRGB(float r, float g, float b) {
        r = Math.max(0f, Math.min(1f, r));
        g = Math.max(0f, Math.min(1f, g));
        b = Math.max(0f, Math.min(1f, b));
        int R = Math.round((float) Math.pow(r, 1 / 2.2) * 255f);
        int G = Math.round((float) Math.pow(g, 1 / 2.2) * 255f);
        int B = Math.round((float) Math.pow(b, 1 / 2.2) * 255f);
        return (0xFF << 24) | (R << 16) | (G << 8) | B;
    }

    public static int oklabToSRGB(float L, float A, float B) {
        // Compress chroma near shadows (L<0.2) and highlights (L>0.8)
        float C = (float) Math.sqrt(A*A + B*B);
        float h = (float) Math.atan2(B, A);
        float fade = L < 0.2f ? L / 0.2f : L > 0.8f ? (1f - L) / 0.2f : 1f;
        C *= Math.max(0f, Math.min(1f, fade));
        A = (float) (Math.cos(h) * C);
        B = (float) (Math.sin(h) * C);

        // OKLab → LMS
        float l_ = L + 0.3963377774f*A + 0.2158037573f*B;
        float m_ = L - 0.1055613458f*A - 0.0638541728f*B;
        float s_ = L - 0.0894841775f*A - 1.2914855480f*B;

        float l = l_*l_*l_;
        float m = m_*m_*m_;
        float s = s_*s_*s_;

        // LMS → linear sRGB → gamma-encoded
        return safeSRGB(
            +4.0767416621f*l - 3.3077115913f*m + 0.2309699292f*s,
            -1.2684380046f*l + 2.6097574011f*m - 0.3413193965f*s,
            -0.0041960863f*l - 0.7034186147f*m + 1.7076147010f*s
        );
    }
}
