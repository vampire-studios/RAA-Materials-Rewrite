// DripNoduleSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record DripNoduleSpec(
		List<ResourceLocation> biomeTags,
		List<Target> replaceables,
		YBand y,
		int noduleSize,          // tiny clumps (1–3)
		int nodulesPerChunk
) implements SpawnSpec {
	public static final MapCodec<DripNoduleSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(DripNoduleSpec::biomeTags),
			Target.CODEC.listOf().fieldOf("targets").forGetter(DripNoduleSpec::replaceables),
			YBand.CODEC.fieldOf("y").forGetter(DripNoduleSpec::y),
			Codec.INT.fieldOf("nodule_size").forGetter(DripNoduleSpec::noduleSize),
			Codec.INT.fieldOf("nodules_per_chunk").forGetter(DripNoduleSpec::nodulesPerChunk)
	).apply(inst, DripNoduleSpec::new));
	public static final Codec<DripNoduleSpec> CODEC = MAP_CODEC.codec();

	@Override
	public Mode mode() {
		return Mode.DRIP_NODULE;
	}
}
