// net/ServerSend.java
package net.vampirestudios.raaMaterials.net;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.vampirestudios.raaMaterials.material.MaterialSet;

public final class ServerSend {
    private ServerSend() {}
    public static void materials(ServerPlayer player, MaterialSet set) {
        ServerPlayNetworking.send(player, new S2C_SyncMaterialsPayload(set));
    }
}
