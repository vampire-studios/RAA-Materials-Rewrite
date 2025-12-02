package net.vampirestudios.raaMaterials.client;

import net.fabricmc.api.ClientModInitializer;
import net.vampirestudios.raaMaterials.RRPGen;

public class RAAMaterialsClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientNetworking.initClient();
		MaterialsAssets.init(); // tick + reload listeners
		RRPGen.init();
	}
}
