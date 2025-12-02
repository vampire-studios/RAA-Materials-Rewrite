// SpawnRules.java
package net.vampirestudios.raaMaterials.material;

import net.vampirestudios.raaMaterials.worldgen.SpawnMode;

import java.util.List;

public record SpawnRules(
    SpawnMode mode,
    int veinSize,
    int veinsPerChunk,
    int minY,
    int maxY,
	int yPeak,
    List<String> biomeTag,
	List<String> replaceableTag
) {}
