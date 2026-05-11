// src/main/java/net/vampirestudios/raaMaterials/util/Forms.java
package net.vampirestudios.raaMaterials.material;

import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class Forms {
	// -------- canonical token <-> form maps --------
	private static final Map<String, Form> TOKEN_TO_FORM = new HashMap<>();
	private static final Map<Form, String> FORM_TO_TOKEN = new EnumMap<>(Form.class);
	// Hot-path cache for arbitrary description ids — bounded LRU to prevent unbounded growth
	private static final int MAX_CACHE_SIZE = 4096;
	private static final Map<String, Form> DESCID_CACHE = Collections.synchronizedMap(
		new LinkedHashMap<>(256, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<String, Form> eldest) {
				return size() > MAX_CACHE_SIZE;
			}
		}
	);

	static {
		// ---- Core blocks
		put("material_block", Form.BLOCK);
		put("material_ore", Form.ORE);
		put("material_raw_block", Form.RAW_BLOCK);
		put("material_shingles", Form.SHINGLES);
		put("material_plate_block", Form.PLATE_BLOCK);

		// ---- Core items
		put("material_ingot", Form.INGOT);
		put("material_raw", Form.RAW);
		put("material_gem", Form.GEM);
		put("material_nugget", Form.NUGGET);
		put("material_dust", Form.DUST);
		put("material_sheet", Form.SHEET);
		put("material_gear", Form.GEAR);
		put("material_ball", Form.BALL);

		// ---- Crystalline
		put("material_crystal", Form.CRYSTAL);
		put("material_shard", Form.SHARD);
		put("material_crystal_cluster", Form.CLUSTER);
		put("material_spike", Form.SPIKE);
		put("material_budding", Form.BUDDING);
		put("material_bud_small", Form.BUD_SMALL);
		put("material_bud_medium", Form.BUD_MEDIUM);
		put("material_bud_large", Form.BUD_LARGE);
		put("material_crystal_bricks", Form.CRYSTAL_BRICKS);
		put("material_glass", Form.GLASS);
		put("material_tinted_glass", Form.TINTED_GLASS);

		// ---- Sandstone & stone variants
		put("material_sandstone", Form.SANDSTONE);
		put("material_cut", Form.CUT);
		put("material_cut_sandstone", Form.CUT);
		put("material_smooth", Form.SMOOTH);
		put("material_smooth_sandstone", Form.SMOOTH);
		put("material_chiseled", Form.CHISELED);
		put("material_polished", Form.POLISHED);
		put("material_bricks", Form.BRICKS);
		put("material_cobbled", Form.COBBLED);
		put("material_tiles", Form.TILES);
		put("material_mosaic", Form.MOSAIC);
		put("material_pillar", Form.PILLAR);
		put("material_mossy", Form.MOSSY);
		put("material_cracked", Form.CRACKED);

		// ---- Shapes
		put("material_slab", Form.SLAB);
		put("material_stairs", Form.STAIRS);
		put("material_wall", Form.WALL);
		put("material_pane", Form.PANE);
		put("material_bars", Form.BARS);
		put("material_grate", Form.GRATE);
		put("material_rod_block", Form.ROD_BLOCK);

		// ---- Redstone-ish / doors / fences
		put("material_button", Form.BUTTON);
		put("material_button_stone", Form.BUTTON);
		put("material_button_metal", Form.BUTTON);
		put("material_button_wood", Form.BUTTON);
		put("material_pressure_plate", Form.PRESSURE_PLATE);
		put("material_pressure_plate_stone", Form.PRESSURE_PLATE);
		put("material_pressure_plate_metal", Form.PRESSURE_PLATE);
		put("material_pressure_plate_wood", Form.PRESSURE_PLATE);
		put("material_door", Form.DOOR);
		put("material_door_metal", Form.DOOR);
		put("material_door_wood", Form.DOOR);
		put("material_trapdoor", Form.TRAPDOOR);
		put("material_trapdoor_metal", Form.TRAPDOOR);
		put("material_trapdoor_wood", Form.TRAPDOOR);
		put("material_fence", Form.FENCE);
		put("material_fence_gate", Form.FENCE_GATE);

		// ---- Lights / decor
		put("material_lamp", Form.LAMP);
		put("material_calcite_lamp", Form.CALCITE_LAMP);
		put("material_basalt_lamp", Form.BASALT_LAMP);
		put("material_chime", Form.CHIME);
		put("material_chain", Form.CHAIN);
		put("material_lantern", Form.LANTERN);
		put("material_torch", Form.TORCH);
		put("material_ladder", Form.LADDER);

		// ---- Processing lines (clay/mud/soil)
		put("material_ceramic", Form.CERAMIC);
		put("material_dried", Form.DRIED);
		put("material_packed_soil", Form.PACKED_SOIL);

		// ---- Tools
		put("material_pickaxe", Form.PICKAXE);
		put("material_axe", Form.AXE);
		put("material_sword", Form.SWORD);
		put("material_shovel", Form.SHOVEL);
		put("material_hoe", Form.HOE);
		put("material_shears", Form.SHEARS);
		put("material_bucket", Form.BUCKET);

		// ---- Components / fasteners / wiring
		put("material_rod", Form.ROD);
		put("material_wire", Form.WIRE);
		put("material_coil", Form.COIL);
		put("material_rivet", Form.RIVET);
		put("material_bolt", Form.BOLT);
		put("material_nail", Form.NAIL);
		put("material_ring", Form.RING);

		// ---- Signs
		put("material_sign", Form.SIGN);
		put("material_hanging_sign", Form.HANGING_SIGN);

		// ---- Armor
		put("material_helmet", Form.HELMET);
		put("material_chestplate", Form.CHESTPLATE);
		put("material_leggings", Form.LEGGINGS);
		put("material_boots", Form.BOOTS);
		put("material_horse_armor", Form.HORSE_ARMOR);
		put("material_wolf_armor", Form.WOLF_ARMOR);
		put("material_nautilus_armor", Form.NAUTILUS_ARMOR);

		put("material_hammer", Form.HAMMER);
		put("material_dagger", Form.DAGGER);
		put("material_battle_axe", Form.BATTLE_AXE);
		put("material_war_hammer", Form.WAR_HAMMER);
		put("material_spear", Form.SPEAR);
		put("material_sickle", Form.SICKLE);
		put("material_crown", Form.CROWN);
		put("material_cloak", Form.CLOAK);
		put("material_amulet", Form.AMULET);
		put("material_orb", Form.ORB);
		put("material_music_disc", Form.MUSIC_DISC);
		put("material_food", Form.FOOD);
	}

	private Forms() {
	}

	private static void put(String token, Form f) {
		TOKEN_TO_FORM.put(token, f);
		FORM_TO_TOKEN.putIfAbsent(f, token);
	}

	/** Extracts the logical id token from a description id like "item.raa_materials.material_ingot". */
	public static String tokenFromDescId(String descriptionId) {
		if (descriptionId == null || descriptionId.isEmpty()) return "";
		int dot = descriptionId.lastIndexOf('.');
		String tail = dot >= 0 ? descriptionId.substring(dot + 1) : descriptionId;
		return tail.toLowerCase(Locale.ROOT);
	}

	/** More defensive tokenization: accepts description ids, raw tokens, or resource keys. */
	public static String normalizeToken(String any) {
		if (any == null || any.isEmpty()) return "";
		String t = any;

		// If looks like a resource location, use its path
		int colon = t.indexOf(':');
		if (colon >= 0 && colon < t.length() - 1) {
			t = t.substring(colon + 1);
		}
		// Take last segment after '.' (item./block./custom)
		int dot = t.lastIndexOf('.');
		if (dot >= 0 && dot < t.length() - 1) {
			t = t.substring(dot + 1);
		}
		return t.toLowerCase(Locale.ROOT);
	}

	/** Try exact, then suffix heuristics. */
	public static Form fromToken(String token) {
		String t = normalizeToken(token);

		// Fast path
		Form f = TOKEN_TO_FORM.get(t);
		if (f != null) return f;

		// Specific sandstone chain
		if (t.endsWith("_cut_sandstone")) return Form.CUT;
		if (t.endsWith("_smooth_sandstone")) return Form.SMOOTH;
		if (t.endsWith("_sandstone")) return Form.SANDSTONE;

		// Building variants (order: more specific first)
		if (t.endsWith("_crystal_bricks")) return Form.CRYSTAL_BRICKS;
		if (t.endsWith("_bricks")) return Form.BRICKS;
		if (t.endsWith("_polished")) return Form.POLISHED;
		if (t.endsWith("_chiseled")) return Form.CHISELED;
		if (t.endsWith("_cobbled")) return Form.COBBLED;
		if (t.endsWith("_tiles")) return Form.TILES;
		if (t.endsWith("_mosaic")) return Form.MOSAIC;
		if (t.endsWith("_mossy")) return Form.MOSSY;
		if (t.endsWith("_cracked")) return Form.CRACKED;
		if (t.endsWith("_pillar")) return Form.PILLAR;

		// Shapes
		if (t.endsWith("_stairs")) return Form.STAIRS;
		if (t.endsWith("_slab")) return Form.SLAB;
		if (t.endsWith("_wall")) return Form.WALL;
		if (t.endsWith("_pane")) return Form.PANE;
		if (t.endsWith("_bars")) return Form.BARS;
		if (t.endsWith("_grate")) return Form.GRATE;
		if (t.endsWith("_rod")) return Form.ROD_BLOCK;

		// Redstone-ish / doors / fences
		if (t.endsWith("_button_stone") || t.endsWith("_button_metal") || t.endsWith("_button_wood")) return Form.BUTTON;
		if (t.endsWith("_button")) return Form.BUTTON;
		if (t.endsWith("_pressure_plate_stone") || t.endsWith("_pressure_plate_metal") || t.endsWith("_pressure_plate_wood")) return Form.PRESSURE_PLATE;
		if (t.endsWith("_pressure_plate")) return Form.PRESSURE_PLATE;
		if (t.endsWith("_door")) return Form.DOOR;
		if (t.endsWith("_trapdoor")) return Form.TRAPDOOR;
		if (t.endsWith("_fence_gate")) return Form.FENCE_GATE;
		if (t.endsWith("_fence")) return Form.FENCE;

		// Glass
		if (t.endsWith("_tinted_glass")) return Form.TINTED_GLASS;
		if (t.endsWith("_glass")) return Form.GLASS;

		// Decor/Lights
		if (t.endsWith("_lamp")) return Form.LAMP;
		if (t.endsWith("_lantern")) return Form.LANTERN;
		if (t.endsWith("_torch")) return Form.TORCH;
		if (t.endsWith("_ladder")) return Form.LADDER;
		if (t.endsWith("_chime")) return Form.CHIME;
		if (t.endsWith("_chain")) return Form.CHAIN;

		// Items/components
		if (t.endsWith("_ingot")) return Form.INGOT;
		if (t.endsWith("_nugget")) return Form.NUGGET;
		if (t.endsWith("_dust")) return Form.DUST;
		if (t.endsWith("_sheet") || t.endsWith("_plate")) return Form.SHEET;
		if (t.endsWith("_gear")) return Form.GEAR;
		if (t.endsWith("_gem")) return Form.GEM;
		if (t.endsWith("_raw")) return Form.RAW;
		if (t.endsWith("_ball")) return Form.BALL;
		if (t.endsWith("_wire")) return Form.WIRE;
		if (t.endsWith("_coil")) return Form.COIL;
		if (t.endsWith("_rivet")) return Form.RIVET;
		if (t.endsWith("_bolt")) return Form.BOLT;
		if (t.endsWith("_nail")) return Form.NAIL;
		if (t.endsWith("_ring")) return Form.RING;
		if (t.endsWith("_rod_item") || t.endsWith("_rod")) return Form.ROD;

		// Tools
		if (t.endsWith("_pickaxe")) return Form.PICKAXE;
		if (t.endsWith("_axe")) return Form.AXE;
		if (t.endsWith("_sword")) return Form.SWORD;
		if (t.endsWith("_shovel")) return Form.SHOVEL;
		if (t.endsWith("_hoe")) return Form.HOE;
		if (t.endsWith("_shears")) return Form.SHEARS;
		if (t.endsWith("_bucket")) return Form.BUCKET;

		// Signs
		if (t.endsWith("_hanging_sign")) return Form.HANGING_SIGN;
		if (t.endsWith("_sign")) return Form.SIGN;

		// Armor
		if (t.endsWith("_helmet")) return Form.HELMET;
		if (t.endsWith("_chestplate")) return Form.CHESTPLATE;
		if (t.endsWith("_leggings")) return Form.LEGGINGS;
		if (t.endsWith("_boots")) return Form.BOOTS;
		if (t.endsWith("_nautilus_armor")) return Form.NAUTILUS_ARMOR;
		if (t.endsWith("_horse_armor")) return Form.HORSE_ARMOR;
		if (t.endsWith("_wolf_armor")) return Form.WOLF_ARMOR;

		if (t.endsWith("_hammer")) return Form.HAMMER;
		if (t.endsWith("_dagger")) return Form.DAGGER;
		if (t.endsWith("_battle_axe")) return Form.BATTLE_AXE;
		if (t.endsWith("_war_hammer")) return Form.WAR_HAMMER;
		if (t.endsWith("_spear")) return Form.SPEAR;
		if (t.endsWith("_sickle")) return Form.SICKLE;
		if (t.endsWith("_crown")) return Form.CROWN;
		if (t.endsWith("_cloak")) return Form.CLOAK;
		if (t.endsWith("_amulet")) return Form.AMULET;
		if (t.endsWith("_orb")) return Form.ORB;
		if (t.endsWith("_music_disc")) return Form.MUSIC_DISC;
		if (t.endsWith("_food")) return Form.FOOD;
		if (t.endsWith("_spike")) return Form.SPIKE;

		// Default safest guess
		return Form.BLOCK;
	}

	/** From description id with caching. */
	public static Form fromDescriptionId(String descriptionId) {
		Form cached = DESCID_CACHE.get(descriptionId);
		if (cached != null) return cached;
		Form result = fromToken(tokenFromDescId(descriptionId));
		DESCID_CACHE.put(descriptionId, result);
		return result;
	}

	/** From stack: prefer a form component if present, else description id. */
	public static Form fromStack(ItemStack stack) {
		if (stack == null) return Form.BLOCK;
		return fromDescriptionId(stack.getItem().getDescriptionId());
	}

	// -------- Quality-of-life helpers --------

	/** Canonical token for this form (stable for JSON/model routing). */
	public static String token(Form form) {
		return FORM_TO_TOKEN.getOrDefault(form, form.name().toLowerCase(Locale.ROOT));
	}

	/** Is this a “building block family” form (slab/stairs/wall/bricks/polished/…)? */
	public static boolean isBuilding(Form f) {
		return switch (f) {
			case SLAB, STAIRS, WALL, BRICKS, POLISHED, CHISELED,
				 SANDSTONE, CUT, SMOOTH,
				 CERAMIC, DRIED -> true;
			default -> false;
		};
	}

	/** Is this an “ore chain” form? */
	public static boolean isOreChain(Form f) {
		return switch (f) {
			case ORE, RAW, RAW_BLOCK, INGOT, NUGGET, DUST -> true;
			default -> false;
		};
	}
}
