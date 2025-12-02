// SheetVeinSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SheetVeinSpec(
		List<ResourceLocation> biomeTags,
		List<Target> replaceables,
		YBand y,
		int sheetThickness,
		int branchiness,         // 0..N small offshoot count
		int veinsPerChunk
) implements SpawnSpec {
	public static final MapCodec<SheetVeinSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(SheetVeinSpec::biomeTags),
			Target.CODEC.listOf().fieldOf("targets").forGetter(SheetVeinSpec::replaceables),
			YBand.CODEC.fieldOf("y").forGetter(SheetVeinSpec::y),
			Codec.INT.fieldOf("sheet_thickness").forGetter(SheetVeinSpec::sheetThickness),
			Codec.INT.fieldOf("branchiness").forGetter(SheetVeinSpec::branchiness),
			Codec.INT.fieldOf("veins_per_chunk").forGetter(SheetVeinSpec::veinsPerChunk)
	).apply(inst, SheetVeinSpec::new));
	public static final Codec<SheetVeinSpec> CODEC = MAP_CODEC.codec();

	@Override
	public Mode mode() {
		return Mode.SHEET_VEIN;
	}
}
