package net.vampirestudios.raaMaterials.net;

import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.material.MaterialCodecs;
import net.vampirestudios.raaMaterials.material.MaterialSet;

public record S2C_SyncMaterialsPayload(MaterialSet set) implements CustomPacketPayload {
	public static final ResourceLocation ID = RAAMaterials.id("sync_materials");
	public static final Type<S2C_SyncMaterialsPayload> TYPE = new Type<>(ID);

	public static final StreamCodec<FriendlyByteBuf, S2C_SyncMaterialsPayload> STREAM_CODEC =
			new StreamCodec<>() {
				@Override
				public S2C_SyncMaterialsPayload decode(FriendlyByteBuf buf) {
					CompoundTag tag = ByteBufCodecs.COMPOUND_TAG.decode(buf);
					MaterialSet set = MaterialCodecs.MATERIAL_SET.parse(NbtOps.INSTANCE, tag).getOrThrow();
					return new S2C_SyncMaterialsPayload(set);
				}

				@Override
				public void encode(FriendlyByteBuf buf, S2C_SyncMaterialsPayload payload) {
					var data = MaterialCodecs.MATERIAL_SET.encodeStart(NbtOps.INSTANCE, payload.set())
							.flatMap(v -> (v instanceof CompoundTag ct) ? DataResult.success(ct)
									: DataResult.error(() -> "Expected CompoundTag")).getOrThrow();
					ByteBufCodecs.COMPOUND_TAG.encode(buf, data);
				}
			};

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
