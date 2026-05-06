package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public record SpawnInfo(
        int attemptsPerChunk,
        float successChance,      // extra gate after noise/biome
        int veinMin,
        int veinMax,
        VeinShape shape,
		YDistribution y,
        NoiseGate regionGate,     // low-freq 2D noise (x,z)
        NoiseGate pocketGate,     // higher-freq 2D noise (x,z)
        boolean mustTouchAir,
        boolean nearWater,
        boolean nearLava
) {

	public static final Codec<SpawnInfo> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.intRange(0, 512).fieldOf("attempts_per_chunk").forGetter(SpawnInfo::attemptsPerChunk),
			Codec.floatRange(0.0f, 1.0f).fieldOf("success_chance").forGetter(SpawnInfo::successChance),

			Codec.intRange(0, 256).fieldOf("vein_min").forGetter(SpawnInfo::veinMin),
			Codec.intRange(0, 256).fieldOf("vein_max").forGetter(SpawnInfo::veinMax),
			VeinShape.CODEC.fieldOf("shape").forGetter(SpawnInfo::shape),

			YDistribution.CODEC.fieldOf("y").forGetter(SpawnInfo::y),

			NoiseGate.CODEC.fieldOf("region_gate").forGetter(SpawnInfo::regionGate),
			NoiseGate.CODEC.fieldOf("pocket_gate").forGetter(SpawnInfo::pocketGate),

			Codec.BOOL.fieldOf("must_touch_air").forGetter(SpawnInfo::mustTouchAir),
			Codec.BOOL.fieldOf("near_water").forGetter(SpawnInfo::nearWater),
			Codec.BOOL.fieldOf("near_lava").forGetter(SpawnInfo::nearLava)
	).apply(i, SpawnInfo::new));

	public static final StreamCodec<ByteBuf, SpawnInfo> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, SpawnInfo::attemptsPerChunk,
			ByteBufCodecs.FLOAT, SpawnInfo::successChance,

			ByteBufCodecs.VAR_INT, SpawnInfo::veinMin,
			ByteBufCodecs.VAR_INT, SpawnInfo::veinMax,
			VeinShape.STREAM_CODEC, SpawnInfo::shape,

			YDistribution.STREAM_CODEC, SpawnInfo::y,

			NoiseGate.STREAM_CODEC, SpawnInfo::regionGate,
			NoiseGate.STREAM_CODEC, SpawnInfo::pocketGate,

			ByteBufCodecs.BOOL, SpawnInfo::mustTouchAir,
			ByteBufCodecs.BOOL, SpawnInfo::nearWater,
			ByteBufCodecs.BOOL, SpawnInfo::nearLava,

			SpawnInfo::new
	);

	// --- nested types ---
	public enum VeinShape implements StringRepresentable {
		ORE_BLOB,
		ORE_STRING,
		ORE_SHEET,
		/** Flat circular disk — veinMin/veinMax are the radius in blocks, not block count. */
		ORE_DISK,
		CRYSTAL_CLUSTER,
		GEODE;

		public static final Codec<VeinShape> CODEC = StringRepresentable.fromEnum(VeinShape::values);
		private static final IntFunction<VeinShape> BY_ID =
				ByIdMap.continuous(VeinShape::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public static final StreamCodec<ByteBuf, VeinShape> STREAM_CODEC =
				ByteBufCodecs.idMapper(BY_ID, VeinShape::ordinal);

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	public record NoiseGate(float frequency, float threshold) {
		public static final Codec<NoiseGate> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.floatRange(0.0f, 1.0f).fieldOf("frequency").forGetter(NoiseGate::frequency),
				Codec.floatRange(-1.0f, 1.0f).fieldOf("threshold").forGetter(NoiseGate::threshold)
		).apply(i, NoiseGate::new));

		public static final StreamCodec<ByteBuf, NoiseGate> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.FLOAT, NoiseGate::frequency,
				ByteBufCodecs.FLOAT, NoiseGate::threshold,
				NoiseGate::new
		);
	}

	public record YDistribution(int minY, int maxY, int centerY, int spread) {
		public static final Codec<YDistribution> CODEC = RecordCodecBuilder.create(i -> i.group(
				Codec.INT.fieldOf("min_y").forGetter(YDistribution::minY),
				Codec.INT.fieldOf("max_y").forGetter(YDistribution::maxY),
				Codec.INT.fieldOf("center_y").forGetter(YDistribution::centerY),
				Codec.intRange(0, 512).fieldOf("spread").forGetter(YDistribution::spread)
		).apply(i, YDistribution::new));

		public static final StreamCodec<ByteBuf, YDistribution> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, YDistribution::minY,
				ByteBufCodecs.VAR_INT, YDistribution::maxY,
				ByteBufCodecs.VAR_INT, YDistribution::centerY,
				ByteBufCodecs.VAR_INT, YDistribution::spread,
				YDistribution::new
		);
	}
}