// net/NetworkInit.java
package net.vampirestudios.raaMaterials.net;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class NetworkInit {
    private NetworkInit() {}
    public static void initCommon() {
        PayloadTypeRegistry.clientboundPlay().registerLarge(S2C_SyncMaterialsPayload.TYPE, S2C_SyncMaterialsPayload.STREAM_CODEC, 100000);
    }
}
