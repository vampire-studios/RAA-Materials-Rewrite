package net.vampirestudios.raaMaterials;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.vampirestudios.raaMaterials.material.MaterialRegistry;

public final class RAACommands {
	private RAACommands() {}

	public static void init() {
		CommandRegistrationCallback.EVENT.register(RAACommands::register);
	}

	private static void register(CommandDispatcher<CommandSourceStack> dispatcher,
	                             CommandBuildContext ctx, Commands.CommandSelection env) {
		dispatcher.register(
				Commands.literal("raa")
						.then(Commands.literal("materials")
								.then(Commands.literal("list")
										.executes(context -> {
											var source = context.getSource();
											ServerLevel level = source.getLevel();
											var mats = MaterialRegistry.all(level);

											if (mats.isEmpty()) {
												source.sendSuccess(() -> Component.literal("[RAA] No materials loaded for this dimension."), false);
												return 0;
											}

											source.sendSuccess(() -> Component.literal(
													"[RAA] " + mats.size() + " material(s) in " + level.dimension().identifier() + ":"
											), false);

											for (int i = 0; i < mats.size(); i++) {
												var m = mats.get(i);
												final int index = i;
												source.sendSuccess(() -> Component.literal(
														String.format("  [%d] %s  kind=%s  tier=%s  color=#%06X",
																index,
																m.nameInformation().id(),
																m.kind(),
																m.tier(),
																m.primaryColor() & 0xFFFFFF)
												), false);
											}
											return mats.size();
										}))
						)
		);
	}
}
