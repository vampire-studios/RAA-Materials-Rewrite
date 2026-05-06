// ParametricIngotItem.java
package net.vampirestudios.raaMaterials.content;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.vampirestudios.raaMaterials.NameGen2;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.YComponents;
import net.vampirestudios.raaMaterials.material.*;

import java.util.Locale;
import java.util.function.Consumer;

public class ParametricBlockItem extends BlockItem {
	public ParametricBlockItem(Properties properties, Block block, String id) {
		super(block, properties.setId(ResourceKey.create(Registries.ITEM, RAAMaterials.id(id))));
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

		String base  = def.map(def1 -> def1.nameInformation().displayName()).orElse("");
		String lower = base.toLowerCase(Locale.ROOT);

		Object[] icuArgs = {
				base,                                   // {0} full material name
				lower,                                  // {1} lowercase
				lower.isEmpty() ? "" : String.format("%s", lower.charAt(0)),   // {2} first char
				lower.isEmpty() ? "" : String.format("%s", lower.charAt(lower.length() - 1))
		};

		String cooked = NameGen2.format(key, icuArgs);
		return Component.literal(cooked);
	}

	@Override
	protected BlockState getPlacementState(BlockPlaceContext ctx) {
		BlockState s = super.getPlacementState(ctx);
		if (s == null) return null;

		var stack = ctx.getItemInHand();

		// 1) Prefer explicit BLOCK_STATE component (e.g., creative tab showed specific state)
		var props = stack.get(DataComponents.BLOCK_STATE);
		if (props != null && s.hasProperty(ParametricBlock.MAT) && props.get(ParametricBlock.MAT) != null) {
			return s.setValue(ParametricBlock.MAT, props.get(ParametricBlock.MAT));
		}

		// 2) Else derive MAT index from YComponents.MATERIAL (ResourceLocation)
		var rl = stack.get(YComponents.MATERIAL);
		if (rl != null && s.hasProperty(ParametricBlock.MAT) && ctx.getLevel() instanceof ServerLevel serverLevel) {
			int idx = MaterialRegistry.indexOf(serverLevel, rl).orElse(0);
			return s.setValue(ParametricBlock.MAT, Math.max(0, Math.min(4095, idx)));
		}

		return s;
	}
}
