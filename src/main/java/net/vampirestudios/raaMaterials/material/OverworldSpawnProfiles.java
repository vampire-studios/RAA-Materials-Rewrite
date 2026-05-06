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
			// Surface sediments — flat disk patches; veinMin/veinMax = radius in blocks
			case GRAVEL -> new SpawnInfo(
					rng.nextInt(2, 5),
					0.8f,
					5, 12,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(50, 120, rng.nextInt(58, 80), rng.nextInt(10, 20)),
					new SpawnInfo.NoiseGate(0.003f, -0.2f),
					new SpawnInfo.NoiseGate(0.025f, 0.0f),
					false, rng.nextFloat() < 0.5f, false
			);
			case MUD -> new SpawnInfo(
					rng.nextInt(2, 4),
					0.75f,
					4, 9,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(55, 80, rng.nextInt(60, 70), rng.nextInt(6, 12)),
					new SpawnInfo.NoiseGate(0.003f, -0.1f),
					new SpawnInfo.NoiseGate(0.025f, 0.05f),
					false, true, false
			);
			case CLAY -> new SpawnInfo(
					rng.nextInt(2, 4),
					0.75f,
					4, 8,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(50, 80, rng.nextInt(58, 68), rng.nextInt(6, 14)),
					new SpawnInfo.NoiseGate(0.003f, -0.1f),
					new SpawnInfo.NoiseGate(0.025f, 0.05f),
					false, rng.nextFloat() < 0.6f, false
			);
			case SAND -> new SpawnInfo(
					rng.nextInt(2, 5),
					0.8f,
					5, 12,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(55, 100, rng.nextInt(60, 80), rng.nextInt(8, 16)),
					new SpawnInfo.NoiseGate(0.003f, -0.2f),
					new SpawnInfo.NoiseGate(0.025f, 0.0f),
					false, false, false
			);
			case SOIL -> new SpawnInfo(
					rng.nextInt(2, 4),
					0.8f,
					4, 9,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(55, 85, rng.nextInt(60, 72), rng.nextInt(6, 12)),
					new SpawnInfo.NoiseGate(0.003f, -0.2f),
					new SpawnInfo.NoiseGate(0.025f, 0.0f),
					false, false, false
			);
			// Salt: surface flats + shallow underground deposits
			case SALT -> new SpawnInfo(
					rng.nextInt(2, 5),
					0.65f,
					4, 9,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(20, 90, rng.nextInt(45, 68), rng.nextInt(12, 22)),
					new SpawnInfo.NoiseGate(0.002f, 0.1f),
					new SpawnInfo.NoiseGate(0.025f, 0.15f),
					false, false, false
			);
			// Volcanic: shallow underground disks
			case VOLCANIC -> new SpawnInfo(
					rng.nextInt(2, 5),
					0.7f,
					4, 9,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(-20, 60, rng.nextInt(0, 30), rng.nextInt(12, 22)),
					new SpawnInfo.NoiseGate(0.002f, 0.0f),
					new SpawnInfo.NoiseGate(0.025f, 0.1f),
					false, false, false
			);
			// Stone variants: mid-depth underground disks
			case STONE -> new SpawnInfo(
					rng.nextInt(3, 6),
					0.85f,
					5, 11,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(-32, 80, rng.nextInt(-10, 40), rng.nextInt(16, 32)),
					new SpawnInfo.NoiseGate(0.002f, -0.1f),
					new SpawnInfo.NoiseGate(0.02f, 0.1f),
					false, false, false
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
