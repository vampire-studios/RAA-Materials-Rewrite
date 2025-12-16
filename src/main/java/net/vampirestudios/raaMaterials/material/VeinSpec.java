// OreVeinSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

/**
 * @param veinSize  blocks per vein
 * @param veinsPerChunk  frequency */
public record VeinSpec(
		List<ResourceLocation> biomeTags,
		List<Target> replaceables,
		YBand y,
		int veinSize,
		int veinsPerChunk,
		// NEW (optional)
		float wobbleScale,        // noise wobble scale (blocks)
		float verticality,        // 0..1 (0 = flat, 1 = vertical bias)
		int branchCount,          // small offshoots per vein
		float densityFalloff      // 0..1: thinner ends (0 none, 1 strong)
) implements SpawnSpec {
	public static final StreamCodec<RegistryFriendlyByteBuf, VeinSpec> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()),
			VeinSpec::biomeTags,
			SpawnSpecStreamCodecs.TARGET.apply(ByteBufCodecs.list()),
			VeinSpec::replaceables,
			SpawnSpecStreamCodecs.Y_BAND,
			VeinSpec::y,
			ByteBufCodecs.INT,
			VeinSpec::veinSize,
			ByteBufCodecs.INT,
			VeinSpec::veinsPerChunk,
			ByteBufCodecs.FLOAT,
			VeinSpec::wobbleScale,
			ByteBufCodecs.FLOAT,
			VeinSpec::verticality,
			ByteBufCodecs.INT,
			VeinSpec::branchCount,
			ByteBufCodecs.FLOAT,
			VeinSpec::densityFalloff,
			VeinSpec::new
	);
	public static final MapCodec<VeinSpec> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
			ResourceLocation.CODEC.listOf().fieldOf("biomes").forGetter(VeinSpec::biomeTags),
			Target.CODEC.listOf().fieldOf("targets").forGetter(VeinSpec::replaceables),
			YBand.CODEC.fieldOf("y").forGetter(VeinSpec::y),
			Codec.INT.fieldOf("vein_size").forGetter(VeinSpec::veinSize),
			Codec.INT.fieldOf("veins_per_chunk").forGetter(VeinSpec::veinsPerChunk),
			Codec.FLOAT.optionalFieldOf("wobble_scale", 8.0f).forGetter(VeinSpec::wobbleScale),
			Codec.FLOAT.optionalFieldOf("verticality", 0.35f).forGetter(VeinSpec::verticality),
			Codec.INT.optionalFieldOf("branch_count", 2).forGetter(VeinSpec::branchCount),
			Codec.FLOAT.optionalFieldOf("density_falloff", 0.5f).forGetter(VeinSpec::densityFalloff)
	).apply(inst, VeinSpec::new));
	public static final Codec<VeinSpec> CODEC = MAP_CODEC.codec();

	public VeinSpec(
			List<ResourceLocation> biomeTags,
			List<Target> replaceables,
			YBand y,
			int veinSize,
			int veinsPerChunk,
			// NEW (optional)
			float wobbleScale,        // noise wobble scale (blocks)
			float verticality,        // 0..1 (0 = flat, 1 = vertical bias)
			int branchCount,          // small offshoots per vein
			float densityFalloff      // 0..1: thinner ends (0 none, 1 strong)
	) {
		this.biomeTags = List.copyOf(biomeTags);
		this.replaceables = List.copyOf(replaceables);
		this.y = y;
		this.veinSize = veinSize;
		this.veinsPerChunk = veinsPerChunk;
		this.wobbleScale = wobbleScale;
		this.verticality = verticality;
		this.branchCount = branchCount;
		this.densityFalloff = densityFalloff;
	}

	@Override
	public Mode mode() {
		return Mode.VEIN;
	}
}
