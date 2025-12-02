// GiantNodeSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record GiantNodeSpec(
		List<ResourceLocation> biomeTags,
		List<Target> replaceables,
		YBand y,
		int attemptsPerChunk,
		int radius,              // big boulder radius
		boolean halo             // sprinkle smaller surrounding bits
) implements SpawnSpec {
	public static final MapCodec<GiantNodeSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(GiantNodeSpec::biomeTags),
			Target.CODEC.listOf().fieldOf("targets").forGetter(GiantNodeSpec::replaceables),
			YBand.CODEC.fieldOf("y").forGetter(GiantNodeSpec::y),
			Codec.INT.fieldOf("attempts_per_chunk").forGetter(GiantNodeSpec::attemptsPerChunk),
			Codec.INT.fieldOf("radius").forGetter(GiantNodeSpec::radius),
			Codec.BOOL.fieldOf("halo").forGetter(GiantNodeSpec::halo)
	).apply(inst, GiantNodeSpec::new));
	public static final Codec<GiantNodeSpec> CODEC = MAP_CODEC.codec();

	@Override
	public Mode mode() {
		return Mode.GIANT_NODE;
	}
}
