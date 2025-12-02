// StrataSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record StrataSpec(
		List<ResourceLocation> biomeTags,
		List<Target> replaceables,
		YBand y,
		int sheetThickness,      // blocks (e.g., 1–3)
		int sheetsPerChunk       // attempts/frequency
) implements SpawnSpec {
	public static final MapCodec<StrataSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(StrataSpec::biomeTags),
			Target.CODEC.listOf().fieldOf("targets").forGetter(StrataSpec::replaceables),
			YBand.CODEC.fieldOf("y").forGetter(StrataSpec::y),
			Codec.INT.fieldOf("sheet_thickness").forGetter(StrataSpec::sheetThickness),
			Codec.INT.fieldOf("sheets_per_chunk").forGetter(StrataSpec::sheetsPerChunk)
	).apply(inst, StrataSpec::new));
	public static final Codec<StrataSpec> CODEC = MAP_CODEC.codec();

	@Override
	public Mode mode() {
		return Mode.STRATA;
	}
}
