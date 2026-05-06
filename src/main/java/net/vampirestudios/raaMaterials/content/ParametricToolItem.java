// ParametricIngotItem.java
package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.ClientMaterialCache;
import net.vampirestudios.raaMaterials.material.MaterialDef;

import java.util.Locale;
import java.util.Optional;

public class ParametricToolItem extends Item {
	private final String name;

	public ParametricToolItem(Properties props, String id, String name) {
		super(props.setId(ResourceKey.create(Registries.ITEM, RAAMaterials.id(id.replace("%s ", "").toLowerCase(Locale.ROOT)))));
		this.name = name;
	}

	public ParametricToolItem(String id, String name) {
		super(new Properties().setId(ResourceKey.create(Registries.ITEM, RAAMaterials.id(id.replace("%s ", "").toLowerCase(Locale.ROOT)))));
		this.name = name;
	}

	@Override
	public Component getName(ItemStack stack) {
		Identifier mat = stack.get(YComponents.MATERIAL);
		Optional<MaterialDef> def = ClientMaterialCache.byRL(mat);
		return Component.literal(def
				.map(d -> name.replace("%s", d.nameInformation().displayName()))
				.orElse(name.replace("%s ", ""))
		);
	}
}
