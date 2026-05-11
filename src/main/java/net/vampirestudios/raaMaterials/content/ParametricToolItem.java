package net.vampirestudios.raaMaterials.content;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialDef;

public class ParametricToolItem extends ParametricItem {
	private final Form form;

	public ParametricToolItem(Properties props, Form form) {
		super(props);
		this.form = form;
	}

	public Form form() {
		return form;
	}

	public static void applyComponents(ItemStack stack, MaterialDef def, Form form) {
		def.toolSpec().ifPresent(spec -> {
			ToolComponentUtil.applyCommon(stack, spec);
			switch (form) {
				case PICKAXE -> ToolComponentUtil.applyMiningTool(stack, spec, 1.0f, -2.8f, BlockTags.MINEABLE_WITH_PICKAXE);
				case AXE -> ToolComponentUtil.applyMiningTool(stack, spec, 7.0f, -3.2f, BlockTags.MINEABLE_WITH_AXE);
				case SHOVEL -> ToolComponentUtil.applyMiningTool(stack, spec, 1.5f, -3.0f, BlockTags.MINEABLE_WITH_SHOVEL);
				case HOE -> ToolComponentUtil.applyMiningTool(stack, spec, 0.0f, -3.0f, BlockTags.MINEABLE_WITH_HOE);
				case SWORD -> ToolComponentUtil.applySword(stack, spec, 3.0f, -2.4f);
				case SPEAR -> ToolComponentUtil.applySpear(stack, spec);
				case HAMMER -> ToolComponentUtil.applyHammer(stack, spec);
				case DAGGER -> ToolComponentUtil.applyDagger(stack, spec);
				default -> {
				}
			}
		});
	}

	public void applyComponents(ItemStack stack, MaterialDef def) {
		applyComponents(stack, def, form);
	}
}
