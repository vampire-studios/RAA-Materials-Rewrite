package net.vampirestudios.raaMaterials;

import net.vampirestudios.raaMaterials.material.MaterialKind;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public final class KindPicker {
	public static MaterialKind pickKind(Random r) {
		var w = RAAConfig.active().kindWeights();
		var ordered = new ArrayList<>(w.entrySet());
		ordered.sort(Comparator.comparingInt(e -> e.getKey().ordinal()));
		int total = 0;
		for (var e : ordered) total += e.getValue();
		int roll = r.nextInt(total), acc = 0;
		for (var e : ordered) {
			acc += e.getValue();
			if (roll < acc) return e.getKey();
		}
		return MaterialKind.OTHER;
	}
}
