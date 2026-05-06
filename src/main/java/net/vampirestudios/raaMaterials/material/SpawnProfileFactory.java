package net.vampirestudios.raaMaterials.material;

import java.util.Random;

public final class SpawnProfileFactory {

    private static int lerpInt(int a, int b, float t) {
        return Math.round(a + (b - a) * t);
    }
    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
    private static float clamp01(float v) { return Math.max(0f, Math.min(1f, v)); }

    public static SpawnInfo metal(long seed, SpawnParams p) {
        Random r = new Random(seed);

        float rare = clamp01(p.rarity());

        int attempts = lerpInt(18, 3, rare);
        float success = lerp(0.9f, 0.5f, rare);

        int veinMin = lerpInt(12, 3, rare);
        int veinMax = lerpInt(28, 8, rare);

        int centerY = lerpInt(
                30,
                -55,
                clamp01(p.depthBias())
        );

        int spread = lerpInt(30, 12, p.clusterBias());

        return new SpawnInfo(
                attempts,
                success,

                veinMin,
                veinMax,
                r.nextFloat() < 0.6f
                        ? SpawnInfo.VeinShape.ORE_BLOB
                        : SpawnInfo.VeinShape.ORE_SHEET,

                new SpawnInfo.YDistribution(
                        -64,
                        96,
                        centerY,
                        spread
                ),

                new SpawnInfo.NoiseGate(
                        0.002f,
                        lerp(-0.35f, 0.15f, rare)
                ),
                new SpawnInfo.NoiseGate(
                        0.020f,
                        lerp(-0.25f, 0.35f, rare)
                ),

                p.exposureBias() > 0.5f,
                false,
                false
        );
    }

    public static SpawnInfo gem(long seed, SpawnParams p) {
        Random r = new Random(seed);

        return new SpawnInfo(
                rand(r, 1, 4),
                randf(r, 0.3f, 0.6f),

                rand(r, 2, 4),
                rand(r, 4, 7),
                SpawnInfo.VeinShape.ORE_STRING,

                new SpawnInfo.YDistribution(
                        -64,
                        32,
                        rand(r, -50, -20),
                        rand(r, 6, 14)
                ),

                new SpawnInfo.NoiseGate(0.0015f, randf(r, 0.25f, 0.6f)),
                new SpawnInfo.NoiseGate(0.030f, randf(r, 0.3f, 0.7f)),

                r.nextBoolean(),  // exposed gems
                false,
                r.nextFloat() < 0.3f
        );
    }

    public static SpawnInfo crystal(long seed, SpawnParams p) {
        Random r = new Random(seed);

        return new SpawnInfo(
                rand(r, 4, 10),
                randf(r, 0.6f, 0.9f),

                rand(r, 3, 6),
                rand(r, 8, 18),
                r.nextFloat() < 0.8f
                        ? SpawnInfo.VeinShape.CRYSTAL_CLUSTER
                        : SpawnInfo.VeinShape.GEODE,

                new SpawnInfo.YDistribution(
                        -32,
                        160,
                        rand(r, 10, 50),
                        rand(r, 20, 45)
                ),

                new SpawnInfo.NoiseGate(0.002f, randf(r, 0.0f, 0.35f)),
                new SpawnInfo.NoiseGate(0.040f, randf(r, 0.2f, 0.6f)),

                true,
                r.nextFloat() < 0.5f,
                false
        );
    }

    // helpers
    private static int rand(Random r, int min, int max) {
        return min + r.nextInt(max - min + 1);
    }
    private static float randf(Random r, float min, float max) {
        return min + r.nextFloat() * (max - min);
    }
}
