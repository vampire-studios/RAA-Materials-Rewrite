// MagmaticSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record MagmaticSpec(
		List<ResourceLocation> biomeTags,
		List<Target> replaceables,
		YBand y,
		int clustersPerChunk,
		int lavaRadiusBlocks     // only place within N blocks of lava
) implements SpawnSpec {
	public static final MapCodec<MagmaticSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(MagmaticSpec::biomeTags),
			Target.CODEC.listOf().fieldOf("targets").forGetter(MagmaticSpec::replaceables),
			YBand.CODEC.fieldOf("y").forGetter(MagmaticSpec::y),
			Codec.INT.fieldOf("clusters_per_chunk").forGetter(MagmaticSpec::clustersPerChunk),
			Codec.INT.fieldOf("lava_radius").forGetter(MagmaticSpec::lavaRadiusBlocks)
	).apply(inst, MagmaticSpec::new));
	public static final Codec<MagmaticSpec> CODEC = MAP_CODEC.codec();

	@Override
	public Mode mode() {
		return Mode.MAGMATIC;
	}
}
