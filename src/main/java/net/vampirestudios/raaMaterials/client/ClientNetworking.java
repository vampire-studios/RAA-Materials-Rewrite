// client/ClientNetworking.java
package net.vampirestudios.raaMaterials.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.vampirestudios.raaMaterials.material.ClientMaterialCache;
import net.vampirestudios.raaMaterials.net.S2C_SyncMaterialsPayload;

public final class ClientNetworking {
	private ClientNetworking() {
	}

	public static void initClient() {
		// Slim materials (ids, forms, pointers)
		ClientPlayNetworking.registerGlobalReceiver(S2C_SyncMaterialsPayload.TYPE, (payload, ctx) -> {
			var set = payload.set();
			ctx.client().execute(() -> {
				ClientMaterialCache.load(set);

				MaterialsAssets.markDirty();
			});
		});
	}
}
