package net.vampirestudios.raaMaterials.material;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

/**
 * Network (StreamCodec) equivalents for SpawnSpec + shared component types.
 *
 * Assumes each concrete spec class exposes:
 *   public static final StreamCodec<RegistryFriendlyByteBuf, <SpecType>> STREAM_CODEC;
 *
 * Example:
 *   public static final StreamCodec<RegistryFriendlyByteBuf, VeinSpec> STREAM_CODEC = StreamCodec.of(...);
 */
public final class SpawnSpecStreamCodecs {
	private SpawnSpecStreamCodecs() {}

	// -------------------------------------------------------------------------
	// Small helpers
	// -------------------------------------------------------------------------

	private static ResourceLocation readRL(RegistryFriendlyByteBuf buf) {
		// Prefer the stable buf methods for broad version compatibility
		return buf.readResourceLocation();
	}

	private static void writeRL(RegistryFriendlyByteBuf buf, ResourceLocation id) {
		buf.writeResourceLocation(id);
	}

	// -------------------------------------------------------------------------
	// Mode
	// -------------------------------------------------------------------------

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnSpec.Mode> MODE =
			StreamCodec.of(
					(buf, mode) -> buf.writeEnum(mode),
					buf -> buf.readEnum(SpawnSpec.Mode.class)
			);

	// -------------------------------------------------------------------------
	// Target
	// -------------------------------------------------------------------------

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnSpec.Target> TARGET =
			StreamCodec.of(
					(buf, t) -> {
						// Encode as: boolean isTag, then ResourceLocation
						buf.writeBoolean(t.type() == SpawnSpec.Target.Type.TAG);
						writeRL(buf, t.id());
					},
					buf -> {
						boolean isTag = buf.readBoolean();
						ResourceLocation id = readRL(buf);
						return isTag ? SpawnSpec.Target.tag(id) : SpawnSpec.Target.block(id);
					}
			);

	// -------------------------------------------------------------------------
	// YBand
	// -------------------------------------------------------------------------

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnSpec.YBand> Y_BAND =
			StreamCodec.of(
					(buf, y) -> {
						buf.writeVarInt(y.minY());
						buf.writeVarInt(y.maxY());
						buf.writeVarInt(y.peakY());
						// Shape as enum
						buf.writeEnum(y.shape());
					},
					buf -> {
						int min = buf.readVarInt();
						int max = buf.readVarInt();
						int peak = buf.readVarInt();
						SpawnSpec.YBand.Shape shape = buf.readEnum(SpawnSpec.YBand.Shape.class);
						return new SpawnSpec.YBand(min, max, peak, shape);
					}
			);

	// -------------------------------------------------------------------------
	// SpawnSpec (discriminated / dispatch)
	// -------------------------------------------------------------------------

	public static final StreamCodec<RegistryFriendlyByteBuf, SpawnSpec> SPAWN_SPEC =
			StreamCodec.of(
					(buf, spec) -> {
						// write discriminator first
						MODE.encode(buf, spec.mode());

						// then payload
						switch (spec.mode()) {
							case VEIN         -> VeinSpec.STREAM_CODEC.encode(buf, (VeinSpec) spec);
							case CLUSTER      -> ClusterSpec.STREAM_CODEC.encode(buf, (ClusterSpec) spec);
							case GEODE        -> GeodeSpec.STREAM_CODEC.encode(buf, (GeodeSpec) spec);
							case STRATA       -> StrataSpec.STREAM_CODEC.encode(buf, (StrataSpec) spec);
							case POCKET       -> PocketSpec.STREAM_CODEC.encode(buf, (PocketSpec) spec);
							case COLUMN       -> ColumnSpec.STREAM_CODEC.encode(buf, (ColumnSpec) spec);
							case DRIP_NODULE  -> DripNoduleSpec.STREAM_CODEC.encode(buf, (DripNoduleSpec) spec);
							case SHEET_VEIN   -> SheetVeinSpec.STREAM_CODEC.encode(buf, (SheetVeinSpec) spec);
							case GIANT_NODE   -> GiantNodeSpec.STREAM_CODEC.encode(buf, (GiantNodeSpec) spec);
							case MAGMATIC     -> MagmaticSpec.STREAM_CODEC.encode(buf, (MagmaticSpec) spec);
							case CAVE_LINING  -> CaveLiningSpec.STREAM_CODEC.encode(buf, (CaveLiningSpec) spec);
							case SURFACE_NODE -> SurfaceNodeSpec.STREAM_CODEC.encode(buf, (SurfaceNodeSpec) spec);
							case METEORITE    -> MeteoriteSpec.STREAM_CODEC.encode(buf, (MeteoriteSpec) spec);
						}
					},
					buf -> {
						SpawnSpec.Mode mode = MODE.decode(buf);

						return switch (mode) {
							case VEIN         -> VeinSpec.STREAM_CODEC.decode(buf);
							case CLUSTER      -> ClusterSpec.STREAM_CODEC.decode(buf);
							case GEODE        -> GeodeSpec.STREAM_CODEC.decode(buf);
							case STRATA       -> StrataSpec.STREAM_CODEC.decode(buf);
							case POCKET       -> PocketSpec.STREAM_CODEC.decode(buf);
							case COLUMN       -> ColumnSpec.STREAM_CODEC.decode(buf);
							case DRIP_NODULE  -> DripNoduleSpec.STREAM_CODEC.decode(buf);
							case SHEET_VEIN   -> SheetVeinSpec.STREAM_CODEC.decode(buf);
							case GIANT_NODE   -> GiantNodeSpec.STREAM_CODEC.decode(buf);
							case MAGMATIC     -> MagmaticSpec.STREAM_CODEC.decode(buf);
							case CAVE_LINING  -> CaveLiningSpec.STREAM_CODEC.decode(buf);
							case SURFACE_NODE -> SurfaceNodeSpec.STREAM_CODEC.decode(buf);
							case METEORITE    -> MeteoriteSpec.STREAM_CODEC.decode(buf);
						};
					}
			);
}
