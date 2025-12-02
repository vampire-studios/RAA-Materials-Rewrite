// SpawnSpec.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public sealed interface SpawnSpec permits CaveLiningSpec, ColumnSpec, DripNoduleSpec, GiantNodeSpec, MagmaticSpec, PocketSpec, SheetVeinSpec, SurfaceNodeSpec, ClusterSpec, GeodeSpec, MeteoriteSpec, StrataSpec, VeinSpec {
	Mode mode();
	List<ResourceLocation> biomeTags();      // e.g. #minecraft:is_overworld
	List<Target> replaceables();             // tag or block targets
	YBand y();                               // min/max/peak/shape

	enum Mode {
		VEIN,            // your existing long snaking blobs
		CLUSTER,         // your existing normal blobs
		GEODE,           // your existing amethyst-like balls with shell
		STRATA,          // thin sheets/layers
		POCKET,          // small irregular nests
		COLUMN,          // stalagmite/stalactite style verticals
		DRIP_NODULE,     // small nodules on ceilings/walls/floors
		SHEET_VEIN,      // thin planar seam + spidery branches
		GIANT_NODE,      // one big ore boulder w/ optional halo
		MAGMATIC,        // near lava/magma pools/chambers
		CAVE_LINING,     // only exposed on cave surfaces
		SURFACE_NODE,     // surface scatter in specific biomes
		METEORITE;
		public static final Codec<Mode> CODEC =
				Codec.STRING.xmap(s -> Mode.valueOf(s.toUpperCase()), m -> m.name().toLowerCase());
	}

	/** Target host: either a TAG (#ns:path) or a BLOCK (ns:path). */
	record Target(Type type, ResourceLocation id) {
		public static Target tag(ResourceLocation id) {
			return new Target(Type.TAG, id);
		}

		public static Target block(ResourceLocation id) {
			return new Target(Type.BLOCK, id);
		}

		public enum Type {TAG, BLOCK}

		private static final MapCodec<Target> OBJECT_CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
				Codec.STRING.xmap(s -> Type.valueOf(s.toUpperCase()), t -> t.name().toLowerCase())
						.fieldOf("type").forGetter(Target::type),
				ResourceLocation.CODEC.fieldOf("id").forGetter(Target::id)
		).apply(inst, Target::new));

		private static final Codec<Target> STRING_CODEC = Codec.STRING.flatXmap(
				s -> {
					boolean isTag = s.startsWith("#");
					String raw = isTag ? s.substring(1) : s;
					ResourceLocation rl = ResourceLocation.tryParse(raw);
					if (rl == null) return DataResult.error(() -> STR."Invalid ResourceLocation: \{s}");
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

		public enum Shape {UNIFORM, TRIANGLE, NORMAL}

		public static final Codec<Shape> SHAPE_CODEC =
				Codec.STRING.xmap(s -> Shape.valueOf(s.toUpperCase()), sh -> sh.name().toLowerCase());

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

	// ---------- Unified (discriminated) codec ----------

	// IMPORTANT: MapCodec here, then .codec() to get Codec
	Codec<SpawnSpec> CODEC = Mode.CODEC.dispatch(
			"mode",
			SpawnSpec::mode,
			m -> switch (m) {
				case VEIN         -> VeinSpec.MAP_CODEC;
				case CLUSTER      -> ClusterSpec.MAP_CODEC;
				case GEODE        -> GeodeSpec.MAP_CODEC;
				case STRATA       -> StrataSpec.MAP_CODEC;
				case POCKET       -> PocketSpec.MAP_CODEC;
				case COLUMN       -> ColumnSpec.MAP_CODEC;
				case DRIP_NODULE  -> DripNoduleSpec.MAP_CODEC;
				case SHEET_VEIN   -> SheetVeinSpec.MAP_CODEC;
				case GIANT_NODE   -> GiantNodeSpec.MAP_CODEC;
				case MAGMATIC     -> MagmaticSpec.MAP_CODEC;
				case CAVE_LINING  -> CaveLiningSpec.MAP_CODEC;
				case SURFACE_NODE -> SurfaceNodeSpec.MAP_CODEC;
				case METEORITE -> MeteoriteSpec.MAP_CODEC;
			}
	);
}
