package net.vampirestudios.raaMaterials.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.material.MaterialSet;
import org.jspecify.annotations.NonNull;

public record S2C_SyncMaterialsPayload(MaterialSet set) implements CustomPacketPayload {
	public static final Identifier ID = RAAMaterials.id("sync_materials");
	public static final Type<S2C_SyncMaterialsPayload> TYPE = new Type<>(ID);

	public static final StreamCodec<FriendlyByteBuf, S2C_SyncMaterialsPayload> STREAM_CODEC = StreamCodec.composite(
			MaterialSet.PACKET_CODEC,
			S2C_SyncMaterialsPayload::set,
			S2C_SyncMaterialsPayload::new
	);

	@Override
	public @NonNull Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
