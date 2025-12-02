// Form.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import dev.lukebemish.codecextras.structured.Structure;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.function.IntFunction;

public enum Form implements StringRepresentable {
	ORE, BLOCK, RAW_BLOCK, PLATE_BLOCK, SHINGLES, RAW, INGOT, DUST, NUGGET, SHEET, GEAR, GEM, SHARD, CRYSTAL, CLUSTER,
	BUDDING, BUD_SMALL, BUD_MEDIUM, BUD_LARGE, GLASS, TINTED_GLASS, BASALT_LAMP, CALCITE_LAMP, CHIME, PICKAXE, AXE, SWORD,
	SHOVEL, HOE, BALL, SLAB, STAIRS, WALL, SANDSTONE, CUT, CHISELED, SMOOTH, POLISHED, CERAMIC, DRIED, BRICKS, CRYSTAL_BRICKS,
	PACKED_SOIL, CHAIN, LANTERN, TORCH, PILLAR, TILES, MOSAIC, MOSSY, CRACKED, COBBLED, PANE, BARS, GRATE, ROD_BLOCK, BUTTON,
	PRESSURE_PLATE, DOOR, TRAPDOOR, FENCE, FENCE_GATE, LAMP, ROD, COIL, WIRE, RIVET, BOLT, NAIL, RING, SIGN, HANGING_SIGN,
	HELMET, CHESTPLATE, LEGGINGS, BOOTS, HORSE_ARMOR, WOLF_ARMOR, SHEARS, BUCKET, LADDER, BATTLE_AXE, WAR_HAMMER, SPEAR, SICKLE,
	CROWN, CLOAK, AMULET, ORB, MUSIC_DISC, FOOD;

	public static final Codec<Form> CODEC = StringRepresentable.fromEnum(Form::values);
	public static final Structure<Form> STRUCTURE = Structure.stringRepresentable(Form::values, Form::getSerializedName);
	private static final IntFunction<Form> BY_ID = ByIdMap.continuous(Form::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, Form> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Form::ordinal);

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
