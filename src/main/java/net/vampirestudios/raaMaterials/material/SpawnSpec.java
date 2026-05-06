// SpawnSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public record SpawnSpec(List<Identifier> biomeTags, List<Target> replaceables, YBand y) {
	record Target(Type type, Identifier id) {
		public static Target tag(Identifier id) {
			return new Target(Type.TAG, id);
		}

		public static Target block(Identifier id) {
			return new Target(Type.BLOCK, id);
		}

		public enum Type {TAG, BLOCK}

		private static final MapCodec<Target> OBJECT_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				Codec.STRING.xmap(s -> Type.valueOf(s.toUpperCase()), t -> t.name().toLowerCase())
						.fieldOf("type").forGetter(Target::type),
				Identifier.CODEC.fieldOf("id").forGetter(Target::id)
		).apply(inst, Target::new));

		private static final Codec<Target> STRING_CODEC = Codec.STRING.flatXmap(
				s -> {
					boolean isTag = s.startsWith("#");
					String raw = isTag ? s.substring(1) : s;
					Identifier rl = Identifier.tryParse(raw);
					if (rl == null) return DataResult.error(() -> String.format("Invalid Identifier: %s", s));
					return DataResult.success(isTag ? Target.tag(rl) : Target.block(rl));
				},
				t -> DataResult.success((t.type == Type.TAG ? "#" : "") + t.id.toString())
		);

		public static final Codec<Target> CODEC = Codec.either(OBJECT_CODEC.codec(), STRING_CODEC)
				.xmap(e -> e.map(o -> o, s -> s), Either::right);
	}

	/** Y distribution band; peak used by triangle/normal shapes. */
	record YBand(int minY, int maxY, int peakY, Shape shape) {
		public static YBand uniform(int minY, int maxY) {
			return new YBand(minY, maxY, (minY + maxY) / 2, Shape.UNIFORM);
		}

		public static YBand triangle(int minY, int maxY, int peakY) {
			return new YBand(minY, maxY, peakY, Shape.TRIANGLE);
		}

		public enum Shape {
			UNIFORM,
			TRIANGLE,
			NORMAL
		}

		public static final Codec<Shape> SHAPE_CODEC = Codec.STRING.xmap(
				s -> Shape.valueOf(s.toUpperCase()),
				sh -> sh.name().toLowerCase()
		);

		public static final Codec<YBand> CODEC = RecordCodecBuilder.create(inst -> inst.group(
				Codec.INT.fieldOf("min_y").forGetter(YBand::minY),
				Codec.INT.fieldOf("max_y").forGetter(YBand::maxY),
				Codec.INT.optionalFieldOf("peak_y").forGetter(b -> Optional.of(b.peakY)),
				SHAPE_CODEC.optionalFieldOf("shape", Shape.TRIANGLE).forGetter(YBand::shape)
		).apply(inst, (min, max, optPeak, shape) -> {
			int peak = optPeak.orElse((min + max) / 2);
			return new YBand(min, max, peak, shape);
		}));
	}
}
