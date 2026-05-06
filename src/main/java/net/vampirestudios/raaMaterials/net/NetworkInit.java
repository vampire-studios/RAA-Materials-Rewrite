// net/NetworkInit.java
package net.vampirestudios.raaMaterials.net;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.vampirestudios.raaMaterials.RAAMaterials;

public final class NetworkInit {
    private NetworkInit() {}
    public static void initCommon() {
        PayloadTypeRegistry.clientboundPlay().registerLarge(S2C_SyncMaterialsPayload.TYPE, S2C_SyncMaterialsPayload.STREAM_CODEC, 100000);

        BiomeModifications.create(RAAMaterials.id("")).add(ModificationPhase.ADDITIONS, BiomeSelectors.all(),
                (selection, modification) -> {

                }
        );
    }
}
