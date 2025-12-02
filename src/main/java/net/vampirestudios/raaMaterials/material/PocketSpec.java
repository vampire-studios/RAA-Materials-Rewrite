// PocketSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record PocketSpec(
		List<ResourceLocation> biomeTags,
		List<Target> replaceables,
		YBand y,
		int pocketSize,          // average size
		int pocketsPerChunk
) implements SpawnSpec {
	public static final MapCodec<PocketSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(PocketSpec::biomeTags),
			Target.CODEC.listOf().fieldOf("targets").forGetter(PocketSpec::replaceables),
			YBand.CODEC.fieldOf("y").forGetter(PocketSpec::y),
			Codec.INT.fieldOf("pocket_size").forGetter(PocketSpec::pocketSize),
			Codec.INT.fieldOf("pockets_per_chunk").forGetter(PocketSpec::pocketsPerChunk)
	).apply(inst, PocketSpec::new));
	public static final Codec<PocketSpec> CODEC = MAP_CODEC.codec();

	@Override
	public Mode mode() {
		return Mode.POCKET;
	}
}
