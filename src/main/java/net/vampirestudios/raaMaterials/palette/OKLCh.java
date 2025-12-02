package net.vampirestudios.raaMaterials.palette;

public class OKLCh {
	record OKLab(float L, float a, float b) { }

	static OKLab toOKLab(int argb) {
		float r = ((argb >> 16) & 0xFF) / 255f;
		float g = ((argb >>  8) & 0xFF) / 255f;
		float b = ( argb        & 0xFF) / 255f;

		// linearize
		r = (float) Math.pow(r, 2.2);
		g = (float) Math.pow(g, 2.2);
		b = (float) Math.pow(b, 2.2);

		// to LMS
		float l = 0.4122214708f*r + 0.5363325363f*g + 0.0514459929f*b;
		float m = 0.2119034982f*r + 0.6806995451f*g + 0.1073969566f*b;
		float s = 0.0883024619f*r + 0.2817188376f*g + 0.6299787005f*b;

		l = (float) Math.cbrt(l);
		m = (float) Math.cbrt(m);
		s = (float) Math.cbrt(s);

		float L = 0.2104542553f*l + 0.7936177850f*m - 0.0040720468f*s;
		float A = 1.9779984951f*l - 2.4285922050f*m + 0.4505937099f*s;
		float B = 0.0259040371f*l + 0.7827717662f*m - 0.8086757660f*s;
		return new OKLab(L,A,B);
	}

	static float oklabL(int argb) {
		float r = ((argb >> 16) & 0xFF) / 255f;
		float g = ((argb >>  8) & 0xFF) / 255f;
		float b = ( argb        & 0xFF) / 255f;

		// linearize sRGB
		r = (float)Math.pow(r, 2.2);
		g = (float)Math.pow(g, 2.2);
		b = (float)Math.pow(b, 2.2);

		// to LMS
		float l = 0.4122214708f*r + 0.5363325363f*g + 0.0514459929f*b;
		float m = 0.2119034982f*r + 0.6806995451f*g + 0.1073969566f*b;
		float s = 0.0883024619f*r + 0.2817188376f*g + 0.6299787005f*b;

		l = (float)Math.cbrt(l);
		m = (float)Math.cbrt(m);
		s = (float)Math.cbrt(s);

		return 0.2104542553f*l + 0.7936177850f*m - 0.0040720468f*s; // 0..1
	}

	static double deltaEOK(int argb1, int argb2) {
		float[] l1 = toOKLab1(argb1);
		float[] l2 = toOKLab1(argb2);
		float dL = l1[0] - l2[0];
		float da = l1[1] - l2[1];
		float db = l1[2] - l2[2];
		return Math.sqrt(dL*dL + da*da + db*db);
	}

	static float[] toOKLab1(int argb) {
		float r = ((argb >> 16) & 0xFF) / 255f;
		float g = ((argb >>  8) & 0xFF) / 255f;
		float b = ( argb        & 0xFF) / 255f;

		r = (float)Math.pow(r, 2.2);
		g = (float)Math.pow(g, 2.2);
		b = (float)Math.pow(b, 2.2);

		float l = 0.4122214708f*r + 0.5363325363f*g + 0.0514459929f*b;
		float m = 0.2119034982f*r + 0.6806995451f*g + 0.1073969566f*b;
		float s = 0.0883024619f*r + 0.2817188376f*g + 0.6299787005f*b;

		l = (float)Math.cbrt(l);
		m = (float)Math.cbrt(m);
		s = (float)Math.cbrt(s);

		float L = 0.2104542553f*l + 0.7936177850f*m - 0.0040720468f*s;
		float A = 1.9779984951f*l - 2.4285922050f*m + 0.4505937099f*s;
		float B = 0.0259040371f*l + 0.7827717662f*m - 0.8086757660f*s;
		return new float[]{L,A,B};
	}

	static int safeSRGB(float r, float g, float b) {
		// Clip to 0–1 first
		r = Math.max(0f, Math.min(1f, r));
		g = Math.max(0f, Math.min(1f, g));
		b = Math.max(0f, Math.min(1f, b));

		// Apply sRGB gamma
		r = (float)Math.pow(r, 1/2.2);
		g = (float)Math.pow(g, 1/2.2);
		b = (float)Math.pow(b, 1/2.2);

		int R = Math.round(r * 255f);
		int G = Math.round(g * 255f);
		int B = Math.round(b * 255f);
		return (0xFF << 24) | (R << 16) | (G << 8) | B;
	}

	static float[] compressChroma(float L, float C, float h) {
		// reduce chroma near shadows/highlights
		float fade = 1f;
		if (L < 0.2f) fade = L / 0.2f;          // fade to 0 as L→0
		else if (L > 0.8f) fade = (1f-L)/0.2f;  // fade to 0 as L→1
		fade = Math.max(0f, Math.min(1f, fade));
		return new float[]{L, C * fade, h};
	}

	static int oklabToSRGB(float L, float A, float B) {
		// --- Step 1: compress chroma near extremes ---
		// Convert to OKLCh
		float C = (float)Math.sqrt(A*A + B*B);
		float h = (float)Math.atan2(B, A); // radians

		// Fade chroma near shadows (L <0.2) and highlights (L >0.8)
		float fade;
		if (L < 0.2f) fade = L / 0.2f;
		else if (L > 0.8f) fade = (1f - L) / 0.2f;
		else fade = 1f;

		C *= Math.max(0f, Math.min(1f, fade));
		A = (float)(Math.cos(h) * C);
		B = (float)(Math.sin(h) * C);

		// --- Step 2: back to LMS cube ---
		float l_ = L + 0.3963377774f*A + 0.2158037573f*B;
		float m_ = L - 0.1055613458f*A - 0.0638541728f*B;
		float s_ = L - 0.0894841775f*A - 1.2914855480f*B;

		float l = l_*l_*l_;
		float m = m_*m_*m_;
		float s = s_*s_*s_;

		// --- Step 3: linear sRGB ---
		float R = +4.0767416621f*l - 3.3077115913f*m + 0.2309699292f*s;
		float G = -1.2684380046f*l + 2.6097574011f*m - 0.3413193965f*s;
		float Bc= -0.0041960863f*l - 0.7034186147f*m + 1.7076147010f*s;

		// --- Step 4: clamp + gamma encode ---
		return safeSRGB(R, G, Bc);
	}

	static float srgbToLinear(float c) {
		// c: 0..1 (gamma-encoded)
		if (c <= 0.04045f) return c / 12.92f;
		return (float)Math.pow((c + 0.055f) / 1.055f, 2.4f);
	}

	static float linearToSrgb(float c) {
		// c: 0..1 (linear)
		if (c <= 0.0031308f) return c * 12.92f;
		return 1.055f * (float)Math.pow(c, 1.0/2.4) - 0.055f;
	}
}
