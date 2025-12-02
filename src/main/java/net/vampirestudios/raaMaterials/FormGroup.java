// FormGroup.java
package net.vampirestudios.raaMaterials;

import com.mojang.serialization.Codec;
import dev.lukebemish.codecextras.structured.Structure;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum FormGroup implements StringRepresentable {
	TOOLS, ORE_CHAIN,
	METAL_DECOR,      // plate/shingles/metal structure set
	STONE_DECOR,      // pillar/tiles/mosaic/mossy/cracked/cobbled/chiseled/polished
	SAND_SET, GRAVEL_SET, CLAY_SET, MUD_SET, SOIL_SET, SALT_SET, VOLCANIC_SET,
	CRYSTAL_SET, CRYSTAL_LAMPS; // cluster, crystal bricks/glass/pane/rod, lamps

	public static final Codec<FormGroup> CODEC = StringRepresentable.fromEnum(FormGroup::values);
	public static final Structure<FormGroup> STRUCTURE = Structure.stringRepresentable(FormGroup::values, FormGroup::getSerializedName);

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
