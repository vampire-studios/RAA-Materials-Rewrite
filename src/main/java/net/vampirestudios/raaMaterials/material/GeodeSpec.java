// GeodeSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * “Frequency” here is a rarity proxy; your actual feature can ignore sizes.
 * @param replaceables  usually stone/deepslate/… shell
 * @param attemptsPerChunk  0-2 typical; your feature can sample rarity
 **/
public record GeodeSpec(
		List<ResourceLocation> biomeTags,
		List<Target> replaceables,
		YBand y,
		int attemptsPerChunk,
		// NEW
		ResourceLocation outerShellBlock, // block id for outer shell
		ResourceLocation innerShellBlock, // block id for inner shell
		ResourceLocation budBlock,        // budding block
		int minRadius,                    // in blocks
		int maxRadius,
		float crackChance
) implements SpawnSpec {
	public static final MapCodec<GeodeSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(GeodeSpec::biomeTags),
			SpawnSpec.Target.CODEC.listOf().fieldOf("targets").forGetter(GeodeSpec::replaceables),
			SpawnSpec.YBand.CODEC.fieldOf("y").forGetter(GeodeSpec::y),
			Codec.INT.fieldOf("attempts_per_chunk").forGetter(GeodeSpec::attemptsPerChunk),
			ResourceLocation.CODEC.optionalFieldOf("outer_shell", ResourceLocation.withDefaultNamespace("calcite")).forGetter(GeodeSpec::outerShellBlock),
			ResourceLocation.CODEC.optionalFieldOf("inner_shell", ResourceLocation.withDefaultNamespace("smooth_basalt")).forGetter(GeodeSpec::innerShellBlock),
			ResourceLocation.CODEC.optionalFieldOf("bud_block", ResourceLocation.withDefaultNamespace("amethyst_block")).forGetter(GeodeSpec::budBlock),
			Codec.INT.optionalFieldOf("min_radius", 5).forGetter(GeodeSpec::minRadius),
			Codec.INT.optionalFieldOf("max_radius", 9).forGetter(GeodeSpec::maxRadius),
			Codec.FLOAT.optionalFieldOf("crack_chance", 0.35f).forGetter(GeodeSpec::crackChance)
	).apply(inst, GeodeSpec::new));
	public static final Codec<GeodeSpec> CODEC = MAP_CODEC.codec();

	public GeodeSpec(List<ResourceLocation> biomeTags, List<Target> replaceables, YBand y,
					 int attemptsPerChunk, int outerRadius, int innerRadius) {
		this.biomeTags = List.copyOf(biomeTags);
		this.replaceables = List.copyOf(replaceables);
		this.y = y;
		this.attemptsPerChunk = attemptsPerChunk;
		this.outerRadius = outerRadius;
		this.innerRadius = innerRadius;
	}

	@Override
	public Mode mode() {
		return Mode.GEODE;
	}
}
