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
					rng.nextInt(1, 4),
					0.75f,
					7, 15,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(45, 160, rng.nextInt(58, 90), rng.nextInt(16, 32)),
					new SpawnInfo.NoiseGate(0.003f, -0.2f),
					new SpawnInfo.NoiseGate(0.025f, 0.0f),
					false, rng.nextFloat() < 0.5f, false
			);
			case MUD -> new SpawnInfo(
					rng.nextInt(1, 3),
					0.75f,
					6, 13,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(45, 120, rng.nextInt(58, 78), rng.nextInt(12, 24)),
					new SpawnInfo.NoiseGate(0.003f, -0.1f),
					new SpawnInfo.NoiseGate(0.025f, 0.05f),
					false, true, false
			);
			case CLAY -> new SpawnInfo(
					rng.nextInt(1, 3),
					0.75f,
					6, 12,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(45, 120, rng.nextInt(56, 76), rng.nextInt(12, 24)),
					new SpawnInfo.NoiseGate(0.003f, -0.1f),
					new SpawnInfo.NoiseGate(0.025f, 0.05f),
					false, rng.nextFloat() < 0.6f, false
			);
			case SAND -> new SpawnInfo(
					rng.nextInt(1, 4),
					0.75f,
					7, 16,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(45, 160, rng.nextInt(58, 90), rng.nextInt(16, 32)),
					new SpawnInfo.NoiseGate(0.003f, -0.2f),
					new SpawnInfo.NoiseGate(0.025f, 0.0f),
					false, false, false
			);
			case SOIL -> new SpawnInfo(
					rng.nextInt(1, 3),
					0.75f,
					6, 13,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(45, 160, rng.nextInt(60, 90), rng.nextInt(16, 32)),
					new SpawnInfo.NoiseGate(0.003f, -0.2f),
					new SpawnInfo.NoiseGate(0.025f, 0.0f),
					false, false, false
			);
			// Salt: surface flats + shallow underground deposits
			case SALT -> new SpawnInfo(
					rng.nextInt(2, 5),
					0.7f,
					6, 14,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(-32, 160, rng.nextInt(40, 72), rng.nextInt(24, 48)),
					new SpawnInfo.NoiseGate(0.002f, 0.1f),
					new SpawnInfo.NoiseGate(0.025f, 0.15f),
					false, false, false
			);
			// Volcanic: shallow underground disks
			case VOLCANIC -> new SpawnInfo(
					rng.nextInt(2, 4),
					0.75f,
					7, 15,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(-64, 96, rng.nextInt(-20, 40), rng.nextInt(24, 48)),
					new SpawnInfo.NoiseGate(0.002f, 0.0f),
					new SpawnInfo.NoiseGate(0.025f, 0.1f),
					false, false, false
			);
			// Stone variants: mid-depth underground disks
			case STONE -> new SpawnInfo(
					rng.nextInt(2, 5),
					0.85f,
					8, 17,
					SpawnInfo.VeinShape.ORE_DISK,
					new SpawnInfo.YDistribution(-64, 128, rng.nextInt(-20, 55), rng.nextInt(28, 56)),
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
