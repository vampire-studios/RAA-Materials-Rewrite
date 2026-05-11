// YItems.java
package net.vampirestudios.raaMaterials.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.content.ParametricBlockItem;
import net.vampirestudios.raaMaterials.content.ParametricDaggerItem;
import net.vampirestudios.raaMaterials.content.ParametricHammerItem;
import net.vampirestudios.raaMaterials.content.ParametricHorseArmorItem;
import net.vampirestudios.raaMaterials.content.ParametricItem;
import net.vampirestudios.raaMaterials.content.ParametricNautilusArmorItem;
import net.vampirestudios.raaMaterials.content.ParametricToolItem;
import net.vampirestudios.raaMaterials.content.ParametricWolfArmorItem;
import net.vampirestudios.raaMaterials.material.Form;

public final class YItems {
	public static Item PARAM_INGOT;
	public static Item PARAM_DUST;
	public static Item PARAM_NUGGET;
	public static Item PARAM_RAW;
	public static Item PARAM_GEM;
	public static Item PARAM_CRYSTAL;
	public static Item PARAM_SHARD;
	public static Item PARAM_SHEET;
	public static Item PARAM_GEAR;
	public static Item PARAM_BALL;

	public static Item PARAM_ROD, PARAM_WIRE, PARAM_COIL, PARAM_RIVET, PARAM_BOLT, PARAM_NAIL, PARAM_RING;

	public static Item PARAM_PICKAXE;
	public static Item PARAM_AXE;
	public static Item PARAM_SHOVEL;
	public static Item PARAM_SWORD;
	public static Item PARAM_HOE;
	public static Item PARAM_SPEAR;
	public static Item PARAM_HAMMER;
	public static Item PARAM_DAGGER;

	public static Item PARAM_ORE_ITEM;
	public static Item PARAM_BLOCK_ITEM;
	public static Item PARAM_RAW_BLOCK_ITEM;
	public static Item PARAM_PLATE_BLOCK_ITEM;
	public static Item PARAM_CERAMIC_BLOCK_ITEM;
	public static Item PARAM_SHINGLES_BLOCK_ITEM;

	// New cube families
	public static Item PARAM_SANDSTONE_BLOCK_ITEM;
	public static Item PARAM_CUT_BLOCK_ITEM;
	public static Item PARAM_SMOOTH_BLOCK_ITEM;
	public static Item PARAM_CHISELED_BLOCK_ITEM;
	public static Item PARAM_BRICKS_BLOCK_ITEM;
	public static Item PARAM_POLISHED_BLOCK_ITEM;
	public static Item PARAM_DRIED_BLOCK_ITEM;

	public static Item PARAM_PILLAR_ITEM, PARAM_TILES_ITEM, PARAM_MOSAIC_ITEM;
	public static Item PARAM_MOSSY_ITEM, PARAM_CRACKED_ITEM, PARAM_COBBLED_ITEM;
	public static Item PARAM_BARS_ITEM, PARAM_GRATE_ITEM;
	public static Item PARAM_BUTTON_METAL_ITEM, PARAM_BUTTON_STONE_ITEM, PARAM_BUTTON_WOOD_ITEM,
			PARAM_PRESSURE_PLATE_METAL_ITEM, PARAM_PRESSURE_PLATE_STONE_ITEM, PARAM_PRESSURE_PLATE_WOOD_ITEM,
			PARAM_DOOR_METAL_ITEM, PARAM_DOOR_WOOD_ITEM, PARAM_TRAPDOOR_METAL_ITEM, PARAM_TRAPDOOR_WOOD_ITEM,
			PARAM_FENCE_ITEM, PARAM_FENCE_GATE_ITEM,
			PARAM_CHAIN_ITEM, PARAM_LANTERN_ITEM, PARAM_LAMP_ITEM;

	public static Item PARAM_CRYSTAL_PANE_ITEM, PARAM_ROD_BLOCK_ITEM; // pane + rod-like light

	// Base shapes
	public static Item PARAM_SLAB_ITEM, PARAM_STAIRS_ITEM, PARAM_WALL_ITEM;

	// Variant shapes
	public static Item PARAM_SANDSTONE_SLAB_ITEM, PARAM_SANDSTONE_STAIRS_ITEM, PARAM_SANDSTONE_WALL_ITEM;
	public static Item PARAM_BRICK_SLAB_ITEM, PARAM_BRICK_STAIRS_ITEM, PARAM_BRICK_WALL_ITEM;
	public static Item PARAM_POLISHED_SLAB_ITEM, PARAM_POLISHED_STAIRS_ITEM, PARAM_POLISHED_WALL_ITEM;

	// Crystal items
	public static Item PARAM_CRYSTAL_BLOCK_ITEM;
	public static Item PARAM_CRYSTAL_BRICKS_ITEM;
	public static Item PARAM_CLUSTER_ITEM;
	public static Item PARAM_BASALT_LAMP_ITEM;
	public static Item PARAM_CALCITE_LAMP_ITEM;

	public static Item PARAM_GLASS_ITEM;
	public static Item PARAM_TINTED_GLASS_ITEM;

	public static Item PARAM_SPIKE_ITEM;

	public static Item PARAM_PACKED_SOIL_ITEM;
	public static Item PARAM_HORSE_ARMOR_ITEM;
	public static Item PARAM_WOLF_ARMOR_ITEM;
	public static Item PARAM_NAUTILUS_ARMOR_ITEM;

	public static void init() {
		PARAM_INGOT = regItem("material_ingot");
		PARAM_DUST = regItem("material_dust");
		PARAM_NUGGET = regItem("material_nugget");
		PARAM_RAW = regItem("material_raw");
		PARAM_GEM = regItem("material_gem");
		PARAM_CRYSTAL = regItem("material_crystal");
		PARAM_SHARD = regItem("material_shard");
		PARAM_SHEET = regItem("material_sheet");
		PARAM_GEAR = regItem("material_gear");
		PARAM_BALL = regItem("material_ball");
		PARAM_ROD = regItem("material_rod");
		PARAM_WIRE = regItem("material_wire");
		PARAM_COIL = regItem("material_coil");
		PARAM_RIVET = regItem("material_rivet");
		PARAM_BOLT = regItem("material_bolt");
		PARAM_NAIL = regItem("material_nail");
		PARAM_RING = regItem("material_ring");

		PARAM_PICKAXE = regToolItem("material_pickaxe", Form.PICKAXE);
		PARAM_AXE = regToolItem("material_axe", Form.AXE);
		PARAM_SHOVEL = regToolItem("material_shovel", Form.SHOVEL);
		PARAM_SWORD = regToolItem("material_sword", Form.SWORD);
		PARAM_HOE = regToolItem("material_hoe", Form.HOE);
		PARAM_SPEAR = regToolItem("material_spear", Form.SPEAR);
		PARAM_HAMMER = regHammerItem("material_hammer");
		PARAM_DAGGER = regDaggerItem("material_dagger");

		PARAM_ORE_ITEM = regBlockItem("material_ore", YBlocks.PARAM_ORE);
		PARAM_BLOCK_ITEM = regBlockItem("material_block", YBlocks.PARAM_BLOCK);
		PARAM_RAW_BLOCK_ITEM = regBlockItem("material_raw_block", YBlocks.PARAM_RAW_BLOCK);
		PARAM_PLATE_BLOCK_ITEM = regBlockItem("material_plate_block", YBlocks.PARAM_PLATE_BLOCK);
		PARAM_CERAMIC_BLOCK_ITEM = regBlockItem("material_ceramic", YBlocks.PARAM_CERAMIC);
		PARAM_SHINGLES_BLOCK_ITEM = regBlockItem("material_shingles", YBlocks.PARAM_SHINGLES_BLOCK);

		// Cube families
		PARAM_SANDSTONE_BLOCK_ITEM = regBlockItem("material_sandstone", YBlocks.PARAM_SANDSTONE);

		PARAM_CUT_BLOCK_ITEM = regBlockItem("material_cut", YBlocks.PARAM_CUT);
		PARAM_SMOOTH_BLOCK_ITEM = regBlockItem("material_smooth", YBlocks.PARAM_SMOOTH);
		PARAM_CHISELED_BLOCK_ITEM = regBlockItem("material_chiseled", YBlocks.PARAM_CHISELED);
		PARAM_BRICKS_BLOCK_ITEM = regBlockItem("material_bricks", YBlocks.PARAM_BRICKS);
		PARAM_POLISHED_BLOCK_ITEM = regBlockItem("material_polished", YBlocks.PARAM_POLISHED);
		PARAM_DRIED_BLOCK_ITEM = regBlockItem("material_dried", YBlocks.PARAM_DRIED);

		// Base shapes
		PARAM_SLAB_ITEM = regBlockItem("material_slab", YBlocks.PARAM_SLAB);
		PARAM_STAIRS_ITEM = regBlockItem("material_stairs", YBlocks.PARAM_STAIRS);
		PARAM_WALL_ITEM = regBlockItem("material_wall", YBlocks.PARAM_WALL);

		// Variant shapes
		PARAM_SANDSTONE_SLAB_ITEM = regBlockItem("material_sandstone_slab", YBlocks.PARAM_SANDSTONE_SLAB);
		PARAM_SANDSTONE_STAIRS_ITEM = regBlockItem("material_sandstone_stairs", YBlocks.PARAM_SANDSTONE_STAIRS);
		PARAM_SANDSTONE_WALL_ITEM = regBlockItem("material_sandstone_wall", YBlocks.PARAM_SANDSTONE_WALL);

		PARAM_BRICK_SLAB_ITEM = regBlockItem("material_brick_slab", YBlocks.PARAM_BRICK_SLAB);
		PARAM_BRICK_STAIRS_ITEM = regBlockItem("material_brick_stairs", YBlocks.PARAM_BRICK_STAIRS);
		PARAM_BRICK_WALL_ITEM = regBlockItem("material_brick_wall", YBlocks.PARAM_BRICK_WALL);

		PARAM_POLISHED_SLAB_ITEM = regBlockItem("material_polished_slab", YBlocks.PARAM_POLISHED_SLAB);
		PARAM_POLISHED_STAIRS_ITEM = regBlockItem("material_polished_stairs", YBlocks.PARAM_POLISHED_STAIRS);
		PARAM_POLISHED_WALL_ITEM = regBlockItem("material_polished_wall", YBlocks.PARAM_POLISHED_WALL);

		PARAM_CRYSTAL_BLOCK_ITEM = regBlockItem("material_crystal_block", YBlocks.PARAM_CRYSTAL_BLOCK);
		PARAM_CRYSTAL_BRICKS_ITEM = regBlockItem("material_crystal_bricks", YBlocks.PARAM_CRYSTAL_BRICKS);
		PARAM_CLUSTER_ITEM = regBlockItem("material_crystal_cluster", YBlocks.PARAM_CLUSTER);
		PARAM_BASALT_LAMP_ITEM = regBlockItem("material_basalt_lamp", YBlocks.PARAM_BASALT_LAMP);
		PARAM_CALCITE_LAMP_ITEM = regBlockItem("material_calcite_lamp", YBlocks.PARAM_CALCITE_LAMP);

		PARAM_PILLAR_ITEM = regBlockItem("material_pillar", YBlocks.PARAM_PILLAR);
		PARAM_TILES_ITEM = regBlockItem("material_tiles", YBlocks.PARAM_TILES);
		PARAM_MOSAIC_ITEM = regBlockItem("material_mosaic", YBlocks.PARAM_MOSAIC);

		PARAM_MOSSY_ITEM = regBlockItem("material_mossy", YBlocks.PARAM_MOSSY);
		PARAM_CRACKED_ITEM = regBlockItem("material_cracked", YBlocks.PARAM_CRACKED);
		PARAM_COBBLED_ITEM = regBlockItem("material_cobbled", YBlocks.PARAM_COBBLED);

		PARAM_BARS_ITEM = regBlockItem("material_bars", YBlocks.PARAM_BARS);
		PARAM_GRATE_ITEM = regBlockItem("material_grate", YBlocks.PARAM_GRATE);

		PARAM_BUTTON_METAL_ITEM = regBlockItem("material_button_metal", YBlocks.PARAM_BUTTON_METAL);
		PARAM_BUTTON_STONE_ITEM = regBlockItem("material_button_stone", YBlocks.PARAM_BUTTON_STONE);
		PARAM_BUTTON_WOOD_ITEM = regBlockItem("material_button_wood", YBlocks.PARAM_BUTTON_WOOD);
		PARAM_PRESSURE_PLATE_METAL_ITEM = regBlockItem("material_pressure_plate_metal", YBlocks.PARAM_PRESSURE_PLATE_METAL);
		PARAM_PRESSURE_PLATE_STONE_ITEM = regBlockItem("material_pressure_plate_stone", YBlocks.PARAM_PRESSURE_PLATE_STONE);
		PARAM_PRESSURE_PLATE_WOOD_ITEM = regBlockItem("material_pressure_plate_wood", YBlocks.PARAM_PRESSURE_PLATE_WOOD);
		PARAM_DOOR_METAL_ITEM = regBlockItem("material_door_metal", YBlocks.PARAM_DOOR_METAL);
		PARAM_DOOR_WOOD_ITEM = regBlockItem("material_door_wood", YBlocks.PARAM_DOOR_WOOD);
		PARAM_TRAPDOOR_METAL_ITEM = regBlockItem("material_trapdoor_metal", YBlocks.PARAM_TRAPDOOR_METAL);
		PARAM_TRAPDOOR_WOOD_ITEM = regBlockItem("material_trapdoor_wood", YBlocks.PARAM_TRAPDOOR_WOOD);
		PARAM_FENCE_ITEM = regBlockItem("material_fence", YBlocks.PARAM_FENCE);
		PARAM_FENCE_GATE_ITEM = regBlockItem("material_fence_gate", YBlocks.PARAM_FENCE_GATE);
		PARAM_CHAIN_ITEM = regBlockItem("material_chain", YBlocks.PARAM_CHAIN);
		PARAM_LANTERN_ITEM = regBlockItem("material_lantern", YBlocks.PARAM_LANTERN);
		PARAM_LAMP_ITEM = regBlockItem("material_lamp", YBlocks.PARAM_LAMP);

		// ---- NEW crystal-ish block items ----
		PARAM_CRYSTAL_PANE_ITEM = regBlockItem("material_crystal_pane", YBlocks.PARAM_CRYSTAL_PANE);
		PARAM_ROD_BLOCK_ITEM = regBlockItem("material_rod_block", YBlocks.PARAM_ROD_BLOCK);

		PARAM_GLASS_ITEM = regBlockItem("material_glass", YBlocks.PARAM_GLASS);
		PARAM_TINTED_GLASS_ITEM = regBlockItem("material_tinted_glass", YBlocks.PARAM_TINTED_GLASS);

		PARAM_SPIKE_ITEM = regBlockItem("material_spike", YBlocks.PARAM_SPIKE);
		PARAM_PACKED_SOIL_ITEM = regBlockItem("material_packed_soil", YBlocks.PARAM_PACKED_SOIL);
		PARAM_HORSE_ARMOR_ITEM = regHorseArmorItem("material_horse_armor");
		PARAM_WOLF_ARMOR_ITEM = regWolfArmorItem("material_wolf_armor");
		PARAM_NAUTILUS_ARMOR_ITEM = regNautilusArmorItem("material_nautilus_armor");
	}

	private static Item regItem(String registryId) {
		return regItem(registryId, new Item.Properties());
	}

	private static Item regItem(String registryId, Item.Properties properties) {
		var id = RAAMaterials.id(registryId);
		return Registry.register(BuiltInRegistries.ITEM, id,
				new ParametricItem(properties.setId(ResourceKey.create(Registries.ITEM, id))));
	}

	private static Item regToolItem(String registryId, Form form) {
		var id = RAAMaterials.id(registryId);
		return Registry.register(BuiltInRegistries.ITEM, id,
				new ParametricToolItem(toolProperties(form).setId(ResourceKey.create(Registries.ITEM, id)), form));
	}

	private static Item regHammerItem(String registryId) {
		var id = RAAMaterials.id(registryId);
		// Seed stats match applyHammer() so unassigned stacks feel right
		var props = new Item.Properties().sword(ToolMaterial.IRON, 8.0f, -3.6f)
				.setId(ResourceKey.create(Registries.ITEM, id));
		return Registry.register(BuiltInRegistries.ITEM, id, new ParametricHammerItem(props));
	}

	private static Item regDaggerItem(String registryId) {
		var id = RAAMaterials.id(registryId);
		// Seed stats match applyDagger() so unassigned stacks feel right
		var props = new Item.Properties().sword(ToolMaterial.IRON, 2.5f, -1.0f)
				.setId(ResourceKey.create(Registries.ITEM, id));
		return Registry.register(BuiltInRegistries.ITEM, id, new ParametricDaggerItem(props));
	}

	private static Item regHorseArmorItem(String registryId) {
		var id = RAAMaterials.id(registryId);
		var props = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
		return Registry.register(BuiltInRegistries.ITEM, id, new ParametricHorseArmorItem(props));
	}

	private static Item regWolfArmorItem(String registryId) {
		var id = RAAMaterials.id(registryId);
		var props = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
		return Registry.register(BuiltInRegistries.ITEM, id, new ParametricWolfArmorItem(props));
	}

	private static Item regNautilusArmorItem(String registryId) {
		var id = RAAMaterials.id(registryId);
		var props = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
		return Registry.register(BuiltInRegistries.ITEM, id, new ParametricNautilusArmorItem(props));
	}

	private static Item.Properties toolProperties(Form form) {
		var properties = new Item.Properties();
		return switch (form) {
			case PICKAXE -> properties.pickaxe(ToolMaterial.IRON, 1.0f, -2.8f);
			case AXE -> properties.axe(ToolMaterial.IRON, 6.0f, -3.2f);
			case SHOVEL -> properties.shovel(ToolMaterial.IRON, 1.5f, -3.0f);
			case HOE -> properties.hoe(ToolMaterial.IRON, 0.0f, -3.0f);
			case SWORD -> properties.sword(ToolMaterial.IRON, 3.0f, -2.4f);
			case SPEAR -> properties.spear(ToolMaterial.IRON, 0.95f, 0.95f, 0.6f, 2.5f, 11.0f, 6.75f, 5.1f, 11.25f, 4.6f);
			default -> properties;
		};
	}

	private static Item regBlockItem(String registryId, Block block) {
		var id = RAAMaterials.id(registryId);
		return Registry.register(BuiltInRegistries.ITEM, id, new ParametricBlockItem(
				new Item.Properties()
						.setId(ResourceKey.create(Registries.ITEM, RAAMaterials.id(registryId))),
				block,
				registryId
		));
	}
}
