// FormGroup.java
package net.vampirestudios.raaMaterials;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum FormGroup implements StringRepresentable {
	TOOLS, ORE_CHAIN,
	METAL_DECOR,
	STONE_DECOR,
	SAND_SET, GRAVEL_SET, CLAY_SET, MUD_SET, SOIL_SET, SALT_SET, VOLCANIC_SET,
	CRYSTAL_SET, CRYSTAL_LAMPS;

	public static final Codec<FormGroup> CODEC = StringRepresentable.fromEnum(FormGroup::values);

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
