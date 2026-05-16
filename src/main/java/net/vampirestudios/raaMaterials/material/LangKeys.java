package net.vampirestudios.raaMaterials.material;

import java.util.List;
import java.util.Set;

import static net.vampirestudios.raaMaterials.material.Form.*;

public final class LangKeys {
	private LangKeys() {
	}

	private static final Set<Form> BASE_AWARE = Set.of(
			SLAB, STAIRS, WALL
	);

	/** Translation key for a form; use with one arg: the material display name. */
	public static String keyFor(Form form, MaterialKind kind, List<Form> forms) {
		if (BASE_AWARE.contains(form)) {
			Form base = IdSuffixes.shapeBaseFor(forms);
			return String.format("block.raa_materials.form.%s", IdSuffixes.shapeSuffix(form, base));
		}

		if (form == CUT) {
			return (kind == MaterialKind.SAND) ? "block.raa_materials.form.cut_sandstone" : "block.raa_materials.form.cut";
		}
		if (form == SMOOTH) {
			return (kind == MaterialKind.SAND) ? "block.raa_materials.form.smooth_sandstone" : "block.raa_materials.form.smooth";
		}
		if (form == CHISELED) {
			return (kind == MaterialKind.SAND) ? "block.raa_materials.form.chiseled_sandstone" : "block.raa_materials.form.chiseled";
		}
		if (form == RAW) {
			return (kind == MaterialKind.CLAY) ? "item.raa_materials.form.ball" : "item.raa_materials.form.raw";
		}
		if (form == BLOCK) {
			var organic = kind == MaterialKind.METAL || kind == MaterialKind.GEM || kind == MaterialKind.CRYSTAL;
			return !organic ? "block.raa_materials.form.block" : "block.raa_materials.form.storage_block";
		}

		return switch (form) {
			case AXE -> "item.raa_materials.form.axe";
			case BARS -> "block.raa_materials.form.bars";
			case BASALT_LAMP -> "block.raa_materials.form.basalt_lamp";
			case BRICKS -> "block.raa_materials.form.bricks";
			case SPIKE -> "block.raa_materials.form.spike";
			case BUDDING -> "block.raa_materials.form.budding";
			case BUD_LARGE -> "block.raa_materials.form.bud_large";
			case BUD_MEDIUM -> "block.raa_materials.form.bud_medium";
			case BUD_SMALL -> "block.raa_materials.form.bud_small";
			case BUTTON -> "block.raa_materials.form.button";
			case CALCITE_LAMP -> "block.raa_materials.form.calcite_lamp";
			case CERAMIC -> "block.raa_materials.form.ceramic";
			case CHIME -> "block.raa_materials.form.chime";
			case CLUSTER -> "block.raa_materials.form.cluster";
			case COBBLED -> "block.raa_materials.form.cobbled";
			case CRACKED -> "block.raa_materials.form.cracked";
			case CRYSTAL -> "item.raa_materials.form.crystal";
			case CRYSTAL_BRICKS -> "block.raa_materials.form.crystal_bricks";
			case DOOR -> "block.raa_materials.form.door";
			case DRIED -> "block.raa_materials.form.dried";
			case DUST -> "item.raa_materials.form.dust";
			case FENCE -> "block.raa_materials.form.fence";
			case FENCE_GATE -> "block.raa_materials.form.fence_gate";
			case GEAR -> "item.raa_materials.form.gear";
			case GEM -> "item.raa_materials.form.gem";
			case GLASS -> "block.raa_materials.form.glass";
			case GRATE -> "block.raa_materials.form.grate";
			case HANGING_SIGN -> "item.raa_materials.form.hanging_sign";
			case HOE -> "item.raa_materials.form.hoe";
			case INGOT -> "item.raa_materials.form.ingot";
			case LAMP -> "block.raa_materials.form.lamp";
			case MOSAIC -> "block.raa_materials.form.mosaic";
			case MOSSY -> "block.raa_materials.form.mossy";
			case NUGGET -> "item.raa_materials.form.nugget";
			case ORE -> "block.raa_materials.form.ore";
			case PACKED_SOIL -> "block.raa_materials.form.packed_soil";
			case PANE -> "block.raa_materials.form.pane";
			case PICKAXE -> "item.raa_materials.form.pickaxe";
			case CHAIN -> "block.raa_materials.form.chain";
			case LANTERN -> "block.raa_materials.form.lantern";
			case TORCH -> "block.raa_materials.form.torch";
			case PILLAR -> "block.raa_materials.form.pillar";
			case PLATE_BLOCK -> "block.raa_materials.form.plate";
			case POLISHED -> "block.raa_materials.form.polished";
			case PRESSURE_PLATE -> "block.raa_materials.form.pressure_plate";
			case RAW_BLOCK -> "block.raa_materials.form.raw_block";
			case ROD_BLOCK -> "block.raa_materials.form.rod";
			case SANDSTONE -> "block.raa_materials.form.sandstone";
			case SHEET -> "item.raa_materials.form.sheet";
			case SHINGLES -> "block.raa_materials.form.shingles";
			case SHARD -> "item.raa_materials.form.shard";
			case SHOVEL -> "item.raa_materials.form.shovel";
			case SIGN -> "item.raa_materials.form.sign";
			case SWORD -> "item.raa_materials.form.sword";
			case TILES -> "block.raa_materials.form.tiles";
			case TINTED_GLASS -> "block.raa_materials.form.tinted_glass";
			case TRAPDOOR -> "block.raa_materials.form.trapdoor";
			case SHIELD -> "item.raa_materials.form.shield";
			case BOW -> "item.raa_materials.form.bow";
			case CROSSBOW -> "item.raa_materials.form.crossbow";
			case DAGGER -> "item.raa_materials.form.dagger";
			case HAMMER -> "item.raa_materials.form.hammer";
			case SCYTHE -> "item.raa_materials.form.scythe";
			case STAFF -> "item.raa_materials.form.staff";
			case WAND -> "item.raa_materials.form.wand";
			case HELMET -> "item.raa_materials.form.helmet";
			case CHESTPLATE -> "item.raa_materials.form.chestplate";
			case LEGGINGS -> "item.raa_materials.form.leggings";
			case BOOTS -> "item.raa_materials.form.boots";
			case HORSE_ARMOR -> "item.raa_materials.form.horse_armor";
			case WOLF_ARMOR -> "item.raa_materials.form.wolf_armor";
			case NAUTILUS_ARMOR -> "item.raa_materials.form.nautilus_armor";
			case SHEARS -> "item.raa_materials.form.shears";
			case BUCKET -> "item.raa_materials.form.bucket";
			case LADDER -> "block.raa_materials.form.ladder";
			case BATTLE_AXE    -> "item.raa_materials.form.battle_axe";
			case WAR_HAMMER    -> "item.raa_materials.form.war_hammer";
			case SPEAR         -> "item.raa_materials.form.spear";
			case SICKLE        -> "item.raa_materials.form.sickle";
			case FOOD          -> "item.raa_materials.form.food";
			case SLAB -> "block.raa_materials.form.slab";
			case STAIRS -> "block.raa_materials.form.stairs";
			case WALL -> "block.raa_materials.form.wall";
			case BEAM -> "block.raa_materials.form.beam";
			case LOG -> "block.raa_materials.form.log";
			case WOOD -> "block.raa_materials.form.wood";
			case STRIPPED_LOG -> "block.raa_materials.form.stripped_log";
			case STRIPPED_WOOD -> "block.raa_materials.form.stripped_wood";
			case LEAVES -> "block.raa_materials.form.leaves";
			case SAPLING -> "block.raa_materials.form.sapling";
			default -> "block.raa_materials.form.generic";
		};
	}
}
