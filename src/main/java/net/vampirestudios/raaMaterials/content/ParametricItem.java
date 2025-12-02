// ParametricIngotItem.java
package net.vampirestudios.raaMaterials.content;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.vampirestudios.raaMaterials.NameGen2;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.*;

import java.util.Locale;
import java.util.function.Consumer;

public class ParametricItem extends Item {

	public ParametricItem(Properties props) {
		super(props);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
		super.appendHoverText(itemStack, tooltipContext, tooltipDisplay, consumer, tooltipFlag);
		MaterialTooltipUtil.add(itemStack, consumer);
	}

	@Override
	public Component getName(ItemStack stack) {
		var mat = stack.get(YComponents.MATERIAL);
		var def = ClientMaterialCache.byRL(mat);

		var kind  = def.map(MaterialDef::kind).orElse(MaterialKind.OTHER);
		var forms = def.map(MaterialDef::forms).orElse(java.util.List.of());
		var form  = Forms.fromDescriptionId(this.getDescriptionId());

		String key = LangKeys.keyFor(form, kind, forms);

		String base  = def.map(MaterialDef::displayName).orElse("");
		String lower = base.toLowerCase(Locale.ROOT);

		Object[] icuArgs = {
				base,                                   // {0} full material name
				lower,                                  // {1} lowercase
				lower.isEmpty() ? "" : STR."\{lower.charAt(0)}",   // {2} first char
				lower.isEmpty() ? "" : STR."\{lower.charAt(lower.length() - 1)}" // {3} last char
		};

		String cooked = NameGen2.format(key, icuArgs);
		return Component.literal(cooked);
	}
}
