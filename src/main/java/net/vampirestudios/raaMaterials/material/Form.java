package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.IntFunction;

public enum Form implements StringRepresentable {
	ORE(FormCategory.NATURAL),
	BLOCK(FormCategory.STORAGE, FormCategory.BUILDING),
	RAW_BLOCK(FormCategory.STORAGE),
	PLATE_BLOCK(FormCategory.BUILDING, FormCategory.DECORATION),
	SHINGLES(FormCategory.BUILDING, FormCategory.DECORATION),

	RAW(FormCategory.CRAFTING_PART),
	INGOT(FormCategory.CRAFTING_PART),
	DUST(FormCategory.CRAFTING_PART),
	NUGGET(FormCategory.CRAFTING_PART),
	SHEET(FormCategory.CRAFTING_PART),
	GEAR(FormCategory.CRAFTING_PART),
	GEM(FormCategory.CRAFTING_PART),
	SHARD(FormCategory.CRAFTING_PART),
	CRYSTAL(FormCategory.CRAFTING_PART, FormCategory.CRYSTAL),
	CLUSTER(FormCategory.NATURAL, FormCategory.CRYSTAL),

	BUDDING(FormCategory.NATURAL, FormCategory.CRYSTAL),
	BUD_SMALL(FormCategory.NATURAL, FormCategory.CRYSTAL),
	BUD_MEDIUM(FormCategory.NATURAL, FormCategory.CRYSTAL),
	BUD_LARGE(FormCategory.NATURAL, FormCategory.CRYSTAL),

	GLASS(FormCategory.BUILDING, FormCategory.CRYSTAL),
	TINTED_GLASS(FormCategory.BUILDING, FormCategory.CRYSTAL),
	PANE(FormCategory.BUILDING, FormCategory.CRYSTAL),

	BASALT_LAMP(FormCategory.FUNCTIONAL, FormCategory.DECORATION),
	CALCITE_LAMP(FormCategory.FUNCTIONAL, FormCategory.DECORATION),
	LAMP(FormCategory.FUNCTIONAL, FormCategory.DECORATION),
	LANTERN(FormCategory.FUNCTIONAL, FormCategory.DECORATION),
	TORCH(FormCategory.FUNCTIONAL, FormCategory.DECORATION),
	CHIME(FormCategory.FUNCTIONAL, FormCategory.DECORATION),

	PICKAXE(FormCategory.TOOL),
	AXE(FormCategory.TOOL),
	SHOVEL(FormCategory.TOOL),
	HOE(FormCategory.TOOL),
	SHEARS(FormCategory.TOOL),
	BUCKET(FormCategory.TOOL),

	SWORD(FormCategory.WEAPON),
	BATTLE_AXE(FormCategory.WEAPON, FormCategory.LEGENDARY),
	WAR_HAMMER(FormCategory.WEAPON, FormCategory.LEGENDARY),
	SPEAR(FormCategory.WEAPON, FormCategory.LEGENDARY),
	SICKLE(FormCategory.WEAPON, FormCategory.TOOL, FormCategory.LEGENDARY),
	SHIELD(FormCategory.WEAPON, FormCategory.ARMOR, FormCategory.LEGENDARY),
	BOW(FormCategory.WEAPON, FormCategory.LEGENDARY),
	CROSSBOW(FormCategory.WEAPON, FormCategory.LEGENDARY),
	DAGGER(FormCategory.WEAPON, FormCategory.LEGENDARY),
	HAMMER(FormCategory.TOOL, FormCategory.WEAPON, FormCategory.LEGENDARY),
	SCYTHE(FormCategory.TOOL, FormCategory.WEAPON, FormCategory.LEGENDARY),
	STAFF(FormCategory.WEAPON, FormCategory.LEGENDARY),
	WAND(FormCategory.WEAPON, FormCategory.LEGENDARY),

	HELMET(FormCategory.ARMOR),
	CHESTPLATE(FormCategory.ARMOR),
	LEGGINGS(FormCategory.ARMOR),
	BOOTS(FormCategory.ARMOR),
	HORSE_ARMOR(FormCategory.ARMOR),
	WOLF_ARMOR(FormCategory.ARMOR),

	CROWN(FormCategory.ARMOR, FormCategory.LEGENDARY),
	CLOAK(FormCategory.ARMOR, FormCategory.LEGENDARY),
	AMULET(FormCategory.LEGENDARY),
	ORB(FormCategory.LEGENDARY),
	MUSIC_DISC(FormCategory.LEGENDARY),
	FOOD(FormCategory.FUNCTIONAL),

	BALL(FormCategory.CRAFTING_PART),

	SLAB(FormCategory.BUILDING),
	STAIRS(FormCategory.BUILDING),
	WALL(FormCategory.BUILDING),
	SANDSTONE(FormCategory.BUILDING),
	CUT(FormCategory.BUILDING, FormCategory.DECORATION),
	CHISELED(FormCategory.BUILDING, FormCategory.DECORATION),
	SMOOTH(FormCategory.BUILDING),
	POLISHED(FormCategory.BUILDING),
	CERAMIC(FormCategory.BUILDING, FormCategory.DECORATION),
	DRIED(FormCategory.BUILDING),
	BRICKS(FormCategory.BUILDING),
	CRYSTAL_BRICKS(FormCategory.BUILDING, FormCategory.CRYSTAL),
	PACKED_SOIL(FormCategory.BUILDING),

	CHAIN(FormCategory.BUILDING, FormCategory.DECORATION),
	PILLAR(FormCategory.BUILDING, FormCategory.DECORATION),
	TILES(FormCategory.BUILDING, FormCategory.DECORATION),
	MOSAIC(FormCategory.BUILDING, FormCategory.DECORATION),
	MOSSY(FormCategory.BUILDING, FormCategory.DECORATION),
	CRACKED(FormCategory.BUILDING, FormCategory.DECORATION),
	COBBLED(FormCategory.BUILDING),

	BARS(FormCategory.BUILDING, FormCategory.DECORATION),
	GRATE(FormCategory.BUILDING, FormCategory.DECORATION),
	ROD_BLOCK(FormCategory.BUILDING, FormCategory.DECORATION),

	BUTTON(FormCategory.FUNCTIONAL),
	PRESSURE_PLATE(FormCategory.FUNCTIONAL),
	DOOR(FormCategory.FUNCTIONAL, FormCategory.BUILDING),
	TRAPDOOR(FormCategory.FUNCTIONAL, FormCategory.BUILDING),
	FENCE(FormCategory.BUILDING),
	FENCE_GATE(FormCategory.FUNCTIONAL, FormCategory.BUILDING),
	SIGN(FormCategory.FUNCTIONAL),
	HANGING_SIGN(FormCategory.FUNCTIONAL),
	LADDER(FormCategory.FUNCTIONAL, FormCategory.BUILDING),

	ROD(FormCategory.CRAFTING_PART),
	COIL(FormCategory.CRAFTING_PART),
	WIRE(FormCategory.CRAFTING_PART),
	RIVET(FormCategory.CRAFTING_PART),
	BOLT(FormCategory.CRAFTING_PART),
	NAIL(FormCategory.CRAFTING_PART),
	RING(FormCategory.CRAFTING_PART),

	PLANKS(FormCategory.WOOD, FormCategory.BUILDING),
	BEAM(FormCategory.WOOD, FormCategory.BUILDING),
	BOARDS(FormCategory.WOOD, FormCategory.BUILDING),
	LOG(FormCategory.WOOD, FormCategory.NATURAL),
	WOOD(FormCategory.WOOD, FormCategory.NATURAL),
	STRIPPED_LOG(FormCategory.WOOD, FormCategory.BUILDING),
	STRIPPED_WOOD(FormCategory.WOOD, FormCategory.BUILDING),
	LEAVES(FormCategory.WOOD, FormCategory.NATURAL),
	SAPLING(FormCategory.WOOD, FormCategory.NATURAL);

	public static final Codec<Form> CODEC = StringRepresentable.fromEnum(Form::values);
	private static final IntFunction<Form> BY_ID = ByIdMap.continuous(Form::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
	public static final StreamCodec<ByteBuf, Form> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Form::ordinal);

	private final Set<FormCategory> categories;

	Form(FormCategory... categories) {
		this.categories = categories.length == 0
				? Set.of()
				: EnumSet.copyOf(List.of(categories));
	}

	public boolean is(FormCategory category) {
		return categories.contains(category);
	}

	public Set<FormCategory> categories() {
		return categories;
	}

	@Override
	public String getSerializedName() {
		return name().toLowerCase(Locale.ROOT);
	}
}
