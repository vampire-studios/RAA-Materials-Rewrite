package net.vampirestudios.raaMaterials;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.vampirestudios.raaMaterials.material.*;
import net.vampirestudios.raaMaterials.net.NetworkInit;
import net.vampirestudios.raaMaterials.net.ServerSend;
import net.vampirestudios.raaMaterials.registry.RAARegistries;
import net.vampirestudios.raaMaterials.registry.YBlocks;
import net.vampirestudios.raaMaterials.registry.YItems;
import net.vampirestudios.raaMaterials.registry.YTabs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RAAMaterials implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("RAAMaterials");

//	public static RAAConfig CONFIG;

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath("raa_materials", path);
	}

	@Override
	public void onInitialize() {
		RAAConfig.init(FabricLoader.getInstance().getGameDir());
		RAARegistries.init();
		YComponents.init();
		YBlocks.init();
		YItems.init();
		NetworkInit.initCommon();
		YTabs.init();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			var overworld = server.overworld();

			var a = overworld.getAttachedOrCreate(MaterialAttachments.LEGENDARIES);
			if (a.byForm().isEmpty()) {
				// Collect candidate materials (server-side IDs)
				var mats = net.vampirestudios.raaMaterials.material.MaterialRegistry
						.all(overworld).stream().map(m -> m.nameInformation().id()).toList();

				// Choose which forms participate in the lottery (put your unique forms here)
				// NOTE: keep this list in data if you want it fully data-driven.
				var uniqueForms = java.util.List.<net.vampirestudios.raaMaterials.material.Form>of(
						// placeholder until you add real “legendary” forms:
						Form.BATTLE_AXE, Form.WAR_HAMMER, Form.SPEAR, Form.SICKLE, Form.CROWN, Form.CLOAK,
						Form.AMULET, Form.ORB, Form.MUSIC_DISC
				);

				var rng = new java.util.Random(server.overworld().getSeed());
				var assigned = LegendaryAssignments.assign(rng, mats, uniqueForms);

				overworld.setAttached(MaterialAttachments.LEGENDARIES, assigned);
			}
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			ServerLevel level = handler.player.level();
			var set = level.getAttached(MaterialAttachments.MATERIALS);
			if (set == null) {
				set = MaterialGenerator.generate(level.getSeed());
				level.setAttached(MaterialAttachments.MATERIALS, set); // persists
			}
			MaterialRegistry.put(level.dimension(), set);
			ServerSend.materials(handler.player, set);
		});
	}
}
