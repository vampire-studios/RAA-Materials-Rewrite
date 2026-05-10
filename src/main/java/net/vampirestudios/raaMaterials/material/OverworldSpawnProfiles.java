package net.vampirestudios.raaMaterials.material;

import net.vampirestudios.raaMaterials.RAAConfig;

import java.util.Random;

public final class OverworldSpawnProfiles {
	private OverworldSpawnProfiles() {
	}

	public static SpawnInfo pick(MaterialKind kind, Random rng) {
		var profile = RAAConfig.active().spawnProfiles().get(kind);
		if (profile == null) {
			profile = RAAConfig.defaultSpawnProfiles().getOrDefault(kind, RAAConfig.defaultSpawnProfiles().get(MaterialKind.OTHER));
		}
		return profile.pick(rng);
	}
}
