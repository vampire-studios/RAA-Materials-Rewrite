package net.vampirestudios.raaMaterials;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.material.*;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.net.NetworkInit;
import net.vampirestudios.raaMaterials.net.ServerSend;
import net.vampirestudios.raaMaterials.recipe.ParametricRecipes;
import net.vampirestudios.raaMaterials.registry.RAARegistries;
import net.vampirestudios.raaMaterials.registry.YBlocks;
import net.vampirestudios.raaMaterials.registry.YItems;
import net.vampirestudios.raaMaterials.registry.YTabs;
import net.vampirestudios.raaMaterials.worldgen.WorldgenInit;
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
		ParametricRecipes.init();
		NetworkInit.initCommon();
		YTabs.init();
		RAACommands.init();
		RRPGen.init();
		WorldgenInit.init();

		ServerLifecycleEvents.SERVER_STOPPED.register(server -> MaterialRegistry.clear());

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			var overworld = server.overworld();

			if (!MaterialRegistry.all(overworld).isEmpty()) {
				LOGGER.warn("[RAA] SERVER_STARTED fired but registry already populated — skipping duplicate init");
				return;
			}

			var set = overworld.getAttached(MaterialAttachments.MATERIALS);
			if (set == null || set.all().isEmpty()) {
				set = MaterialGenerator.generate(overworld.getSeed());
				overworld.setAttached(MaterialAttachments.MATERIALS, set);
			}
			MaterialRegistry.put(overworld.dimension(), set);

			var a = overworld.getAttachedOrCreate(MaterialAttachments.LEGENDARIES);
			if (a.byForm().isEmpty()) {
				// Only materials with tool textures are viable legendary weapon carriers
				var mats = MaterialRegistry
						.all(overworld).stream()
						.filter(m -> m.kind() == MaterialKind.METAL
								|| m.kind() == MaterialKind.ALLOY
								|| m.kind() == MaterialKind.GEM)
						.map(m -> m.nameInformation().id())
						.toList();

				var uniqueForms = java.util.List.of(
						Form.BATTLE_AXE, Form.WAR_HAMMER, Form.SICKLE, Form.DAGGER, Form.HAMMER,
						Form.SCYTHE, Form.SHIELD, Form.BOW, Form.CROSSBOW, Form.STAFF, Form.WAND, Form.CROWN,
						Form.CLOAK, Form.AMULET, Form.ORB, Form.MUSIC_DISC
				);

				var rng = new java.util.Random(overworld.getSeed());
				var assigned = LegendaryAssignments.assign(rng, mats, uniqueForms);

				overworld.setAttached(MaterialAttachments.LEGENDARIES, assigned);
			}
		});
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			var overworld = server.overworld();
			var set = overworld.getAttached(MaterialAttachments.MATERIALS);
			if (set != null) {
				ServerSend.materials(handler.player, set);
			}
		});
	}
}
