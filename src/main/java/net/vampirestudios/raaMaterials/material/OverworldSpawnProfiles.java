package net.vampirestudios.raaMaterials.material;

import java.util.Random;

public final class OverworldSpawnProfiles {
	public static SpawnInfo pick(MaterialKind kind, Random rng) {
		return switch (kind) {
			case METAL, ALLOY -> new SpawnInfo(
					rng.nextInt(8, 18),
					0.75f,
					8, 24,
					SpawnInfo.VeinShape.ORE_BLOB,
					new SpawnInfo.YDistribution(-64, 96, rng.nextInt(-20, 30), rng.nextInt(18, 32)),
					new SpawnInfo.NoiseGate(0.002f, 0.0f),
					new SpawnInfo.NoiseGate(0.02f, 0.2f),
					false, false, false
			);
			case GEM -> new SpawnInfo(
					rng.nextInt(1, 4),
					0.5f,
					2, 6,
					SpawnInfo.VeinShape.ORE_STRING,
					new SpawnInfo.YDistribution(-64, 32, rng.nextInt(-55, -20), rng.nextInt(6, 14)),
					new SpawnInfo.NoiseGate(0.0015f, 0.4f),
					new SpawnInfo.NoiseGate(0.03f, 0.5f),
					rng.nextFloat() < 0.4f, false, rng.nextFloat() < 0.25f
			);
			case CRYSTAL -> new SpawnInfo(
					rng.nextInt(4, 10),
					0.8f,
					3, 14,
					SpawnInfo.VeinShape.CRYSTAL_CLUSTER,
					new SpawnInfo.YDistribution(-32, 160, rng.nextInt(10, 50), rng.nextInt(20, 45)),
					new SpawnInfo.NoiseGate(0.002f, 0.15f),
					new SpawnInfo.NoiseGate(0.04f, 0.35f),
					true, rng.nextFloat() < 0.5f, false
			);
			default -> new SpawnInfo(
					rng.nextInt(6, 14),
					0.85f,
					8, 20,
					SpawnInfo.VeinShape.ORE_BLOB,
					new SpawnInfo.YDistribution(-64, 96, rng.nextInt(-10, 40), rng.nextInt(18, 40)),
					new SpawnInfo.NoiseGate(0.002f, -0.1f),
					new SpawnInfo.NoiseGate(0.02f, 0.1f),
					false, false, false
			);
		};
	}
}
