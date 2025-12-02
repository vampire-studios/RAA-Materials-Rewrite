// src/main/java/net/vampirestudios/raaMaterials/client/MaterialTooltipUtil.java
package net.vampirestudios.raaMaterials.content;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.vampirestudios.raaMaterials.material.ClientMaterialCache;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.worldgen.SpawnMode;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class MaterialTooltipUtil {
	private MaterialTooltipUtil() {}

	public static void add(ItemStack stack, Consumer<Component> lines) {
		ResourceLocation matId = stack.get(net.vampirestudios.raaMaterials.YComponents.MATERIAL);
		if (matId == null) return;

		var opt = ClientMaterialCache.byRL(matId);
		if (opt.isEmpty()) return;
		var def = opt.get();

		// line 1: Type: %s
		lines.accept(Component.translatable("text.raa_materials.kind",
						Component.translatable(kindKey(def.kind())))
				.withStyle(ChatFormatting.GRAY));

		// line 2–4: Spawn
		var s = def.spawn();
		if (s != null) {
			lines.accept(Component.translatable("text.raa_materials.spawn",
							Component.translatable(spawnModeKey(s.mode())),
							s.minY(), s.maxY())
					.withStyle(ChatFormatting.DARK_GRAY));

			if (Screen.hasShiftDown()) {
				var host = tagsLineKeyed("text.raa_materials.tags.host", s.replaceableTag());
				if (host != null) lines.accept(host.copy().withStyle(ChatFormatting.DARK_GRAY));
				var biomes = tagsLineKeyed("text.raa_materials.tags.biomes", s.biomeTag());
				if (biomes != null) lines.accept(biomes.copy().withStyle(ChatFormatting.DARK_GRAY));
			} else {
				lines.accept(Component.translatable("text.raa_materials.hint.shift")
						.withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
			}
		}
	}

	private static String kindKey(MaterialKind k) {
		return switch (k) {
			case METAL -> "text.raa_materials.kind.metal";
			case ALLOY -> "text.raa_materials.kind.alloy";
			case GEM -> "text.raa_materials.kind.gem";
			case CRYSTAL -> "text.raa_materials.kind.crystal";
			case STONE -> "text.raa_materials.kind.stone";
			case SAND -> "text.raa_materials.kind.sand";
			case GRAVEL -> "text.raa_materials.kind.gravel";
			case CLAY -> "text.raa_materials.kind.clay";
			case MUD -> "text.raa_materials.kind.mud";
			case SOIL -> "text.raa_materials.kind.soil";
			case SALT -> "text.raa_materials.kind.salt";
			case VOLCANIC -> "text.raa_materials.kind.volcanic";
			case WOOD -> "text.raa_materials.kind.wood";
			default -> "text.raa_materials.kind.material";
		};
	}

	private static String spawnModeKey(SpawnMode m) {
		return m == null
				? "text.raa_materials.spawn_mode.unknown"
				: (STR."text.raa_materials.spawn_mode.\{m.name().toLowerCase(Locale.ROOT)}");
	}

	private static Component tagsLineKeyed(String labelKey, List<String> tags) {
		if (tags == null || tags.isEmpty()) return null;
		String v = tags.stream()
				.map(t -> t.contains(":") ? t.substring(t.indexOf(':') + 1) : t)
				.map(t -> t.replace('_', ' '))
				.collect(Collectors.joining(", "));
		return Component.translatable(labelKey, v);
	}

	private static String joinComponents(List<MutableComponent> parts, String sep) {
		var sb = new StringBuilder();
		for (int i = 0; i < parts.size(); i++) {
			if (i > 0) sb.append(sep);
			sb.append(parts.get(i).getString());
		}
		return sb.toString();
	}
}
