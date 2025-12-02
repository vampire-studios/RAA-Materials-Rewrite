// ColumnSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record ColumnSpec(
		List<ResourceLocation> biomeTags,
		List<Target> replaceables,
		YBand y,
		int radius,              // column radius
		int minHeight,           // min column height
		int maxHeight,           // max column height
		int columnsPerChunk
) implements SpawnSpec {
	public static final MapCodec<ColumnSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(ColumnSpec::biomeTags),
			Target.CODEC.listOf().fieldOf("targets").forGetter(ColumnSpec::replaceables),
			YBand.CODEC.fieldOf("y").forGetter(ColumnSpec::y),
			Codec.INT.fieldOf("radius").forGetter(ColumnSpec::radius),
			Codec.INT.fieldOf("min_height").forGetter(ColumnSpec::minHeight),
			Codec.INT.fieldOf("max_height").forGetter(ColumnSpec::maxHeight),
			Codec.INT.fieldOf("columns_per_chunk").forGetter(ColumnSpec::columnsPerChunk)
	).apply(inst, ColumnSpec::new));
	public static final Codec<ColumnSpec> CODEC = MAP_CODEC.codec();

	@Override
	public Mode mode() {
		return Mode.COLUMN;
	}
}
