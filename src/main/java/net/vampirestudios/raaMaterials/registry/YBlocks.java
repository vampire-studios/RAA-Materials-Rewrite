// YItems.java
package net.vampirestudios.raaMaterials.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.content.*;

import java.util.function.Function;

public final class YBlocks {
	public static Block PARAM_ORE;
	public static Block PARAM_BLOCK;
	public static Block PARAM_RAW_BLOCK;
	public static Block PARAM_PLATE_BLOCK;
	public static Block PARAM_CERAMIC;
	public static Block PARAM_SHINGLES_BLOCK;

	// New cube-all building families
	public static Block PARAM_SANDSTONE;
	public static Block PARAM_CUT;
	public static Block PARAM_SMOOTH;
	public static Block PARAM_CHISELED;
	public static Block PARAM_BRICKS;
	public static Block PARAM_POLISHED;
	public static Block PARAM_DRIED;

	// Base shapes (vanilla-like, based on BLOCK)
	public static Block PARAM_SLAB;
	public static Block PARAM_STAIRS;
	public static Block PARAM_WALL;

	// Variant shapes (attach to per-variant textures)
	public static Block PARAM_SANDSTONE_SLAB, PARAM_SANDSTONE_STAIRS, PARAM_SANDSTONE_WALL;
	public static Block PARAM_BRICK_SLAB,    PARAM_BRICK_STAIRS,    PARAM_BRICK_WALL;
	public static Block PARAM_POLISHED_SLAB, PARAM_POLISHED_STAIRS, PARAM_POLISHED_WALL;

	// Crystal-only forms
	public static Block PARAM_CLUSTER;
	public static Block PARAM_CRYSTAL_BLOCK;
	public static Block PARAM_CRYSTAL_GLASS;
	public static Block PARAM_CRYSTAL_TINTED_GLASS;
	public static Block PARAM_CRYSTAL_BRICKS;
	public static Block PARAM_BASALT_LAMP;
	public static Block PARAM_CALCITE_LAMP;

	// Buds (no items)
	public static Block PARAM_CRYSTAL_BUD_SMALL;
	public static Block PARAM_CRYSTAL_BUD_MEDIUM;
	public static Block PARAM_CRYSTAL_BUD_LARGE;

	// + NEW general blocks
	public static Block PARAM_PILLAR;
	public static Block PARAM_TILES, PARAM_MOSAIC;
	public static Block PARAM_MOSSY, PARAM_CRACKED, PARAM_COBBLED;
	public static Block PARAM_BARS, PARAM_GRATE;
	public static Block PARAM_BUTTON_STONE, PARAM_BUTTON_METAL, PARAM_BUTTON_WOOD,
			PARAM_PRESSURE_PLATE_STONE, PARAM_PRESSURE_PLATE_METAL, PARAM_PRESSURE_PLATE_WOOD
			/*PARAM_DOOR_METAL, PARAM_DOOR_WOOD, PARAM_TRAPDOOR_METAL, PARAM_TRAPDOOR_WOOD*/;

	// + NEW crystal-ish
	public static Block PARAM_CRYSTAL_PANE;
	public static Block PARAM_ROD_BLOCK;

	private static Block regBlock(String id, Block item) {
		return Registry.register(BuiltInRegistries.BLOCK, RAAMaterials.id(id), item);
	}

	private static Block regBlock(String id, Function<BlockBehaviour.Properties, Block> blockFunction, BlockBehaviour.Properties properties) {
		return Registry.register(BuiltInRegistries.BLOCK, RAAMaterials.id(id), blockFunction.apply(properties));
	}

	public static void init() {
		PARAM_ORE = regBlock("material_ore", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_ore")))
				.strength(3.0f)
				.sound(SoundType.STONE)
		));

		PARAM_BLOCK = regBlock("material_block", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_block")))
				.strength(5.0f)
				.sound(SoundType.METAL)
		));

		PARAM_RAW_BLOCK = regBlock("material_raw_block", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_raw_block")))
				.strength(5.0f)
				.sound(SoundType.METAL)
		));

		PARAM_PLATE_BLOCK = regBlock("material_plate_block", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_plate_block")))
				.strength(4.0f)
				.sound(SoundType.METAL)
		));

		PARAM_CERAMIC = regBlock("material_ceramic", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_ceramic")))
				.strength(4.0f)
				.sound(SoundType.STONE)
		));

		PARAM_SHINGLES_BLOCK = regBlock("material_shingles", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_shingles")))
				.strength(3.5f)
				.sound(SoundType.METAL)
		));

		// --- Cube-all building families ---
		PARAM_SANDSTONE = regBlock("material_sandstone", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_sandstone")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_CUT = regBlock("material_cut", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_cut")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_SMOOTH = regBlock("material_smooth", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_smooth")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_CHISELED = regBlock("material_chiseled", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_chiseled")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_BRICKS = regBlock("material_bricks", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_bricks")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_POLISHED = regBlock("material_polished", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_polished")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_DRIED = regBlock("material_dried", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_dried")))
				.strength(1.8f)
				.sound(SoundType.STONE)
		));

		// --- Base BLOCK shapes ---
		PARAM_SLAB = regBlock("material_slab", new ParametricSlabBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_slab")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_STAIRS = regBlock("material_stairs", new ParametricStairsBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_stairs")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_WALL = regBlock("material_wall", new ParametricWallBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_wall")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));

		// --- Variant shapes (sandstone / bricks / polished) ---
		PARAM_SANDSTONE_SLAB = regBlock("material_sandstone_slab", new ParametricSlabBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_sandstone_slab")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_SANDSTONE_STAIRS = regBlock("material_sandstone_stairs", new ParametricStairsBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_sandstone_stairs")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_SANDSTONE_WALL = regBlock("material_sandstone_wall", new ParametricWallBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_sandstone_wall")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));

		PARAM_BRICK_SLAB = regBlock("material_brick_slab", new ParametricSlabBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_brick_slab")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_BRICK_STAIRS = regBlock("material_brick_stairs", new ParametricStairsBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_brick_stairs")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_BRICK_WALL = regBlock("material_brick_wall", new ParametricWallBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_brick_wall")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));

		PARAM_POLISHED_SLAB = regBlock("material_polished_slab", new ParametricSlabBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_polished_slab")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_POLISHED_STAIRS = regBlock("material_polished_stairs", new ParametricStairsBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_polished_stairs")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));
		PARAM_POLISHED_WALL = regBlock("material_polished_wall", new ParametricWallBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_polished_wall")))
				.strength(2.0f)
				.sound(SoundType.STONE)
		));

		// --- Crystal-only forms ---
		PARAM_CRYSTAL_BLOCK = regBlock("material_crystal_block", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_crystal_block")))
				.strength(5.0f)
				.sound(SoundType.AMETHYST)
		));
		// Crystal Bricks (opaque decor)
		PARAM_CRYSTAL_BRICKS = regBlock("material_crystal_bricks", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_crystal_bricks")))
				.strength(5.0f)
				.sound(SoundType.AMETHYST)
		));

		// Crystal Cluster (spiky, emits light; silk-touch drops itself, otherwise shards via loot table)
		PARAM_CLUSTER = regBlock("material_crystal_cluster", new ParametricCrystalClusterBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_crystal_cluster")))
				.strength(1.5f)
				.lightLevel(s -> 12)  // nice glow
				.noOcclusion()
				.sound(SoundType.AMETHYST_CLUSTER)
		));

		// Crystal Glass (translucent + tintable)
		PARAM_CRYSTAL_GLASS = regBlock("material_crystal_glass", new ParametricCrystalGlassBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_crystal_glass")))
				.strength(0.3f)
				.noOcclusion()
				.sound(SoundType.GLASS)
		));

		// Crystal Tinted Glass (translucent model; full light block handled by render/behavior later if needed)
		PARAM_CRYSTAL_TINTED_GLASS = regBlock("material_crystal_tinted_glass", new ParametricCrystalTintedGlassBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_crystal_tinted_glass")))
				.strength(0.3f)
				.noOcclusion()
				.sound(SoundType.GLASS)
		));

		// Basalt Lamp (opaque housing + tinted slits; full lamp)
		PARAM_BASALT_LAMP = regBlock("material_basalt_lamp", new ParametricCrystalLampBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_basalt_lamp")))
				.strength(2.5f)
				.lightLevel(s -> 15)
				.sound(SoundType.BASALT)
		));

		// Calcite Lamp (opaque housing + tinted slits; full lamp)
		PARAM_CALCITE_LAMP = regBlock("material_calcite_lamp", new ParametricCrystalLampBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_calcite_lamp")))
				.strength(2.0f)
				.lightLevel(s -> 15)
				.sound(SoundType.CALCITE)
		));

		// Buds (no BlockItem registered anywhere)
		PARAM_CRYSTAL_BUD_SMALL = regBlock("material_crystal_bud_small",
				new ParametricCrystalBudBlock(Block.Properties.of().strength(0.5f).noOcclusion().sound(SoundType.AMETHYST_CLUSTER)
						.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_crystal_bud_small"))),
						ParametricCrystalBudBlock.Size.SMALL));
		PARAM_CRYSTAL_BUD_MEDIUM = regBlock("material_crystal_bud_medium",
				new ParametricCrystalBudBlock(Block.Properties.of().strength(0.75f).noOcclusion().sound(SoundType.AMETHYST_CLUSTER)
						.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_crystal_bud_medium"))),
						ParametricCrystalBudBlock.Size.MEDIUM));
		PARAM_CRYSTAL_BUD_LARGE = regBlock("material_crystal_bud_large",
				new ParametricCrystalBudBlock(Block.Properties.of().strength(1.0f).noOcclusion().sound(SoundType.AMETHYST_CLUSTER)
						.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_crystal_bud_large"))),
						ParametricCrystalBudBlock.Size.LARGE));


		// -------- General building variants --------
		PARAM_PILLAR = regBlock("material_pillar", new ParametricPillarBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_pillar")))
				.strength(2.0f).sound(SoundType.STONE)));

		PARAM_TILES = regBlock("material_tiles", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_tiles")))
				.strength(2.0f).sound(SoundType.STONE)));

		PARAM_MOSAIC = regBlock("material_mosaic", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_mosaic")))
				.strength(2.0f).sound(SoundType.STONE)));

		PARAM_MOSSY = regBlock("material_mossy", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_mossy")))
				.strength(2.0f).sound(SoundType.STONE)));

		PARAM_CRACKED = regBlock("material_cracked", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_cracked")))
				.strength(2.0f).sound(SoundType.STONE)));

		PARAM_COBBLED = regBlock("material_cobbled", new ParametricBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_cobbled")))
				.strength(2.0f).sound(SoundType.STONE)));

		// -------- Metal-ish / redstone-y --------
		PARAM_BARS = regBlock("material_bars", new ParametricBarsBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_bars")))
				.strength(5.0f).sound(SoundType.METAL).noOcclusion()));

		PARAM_GRATE = regBlock("material_grate", new ParametricGrateBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_grate")))
				.strength(4.0f).sound(SoundType.METAL).noOcclusion()));

		PARAM_BUTTON_STONE = regBlock("material_button_stone", new ParametricButtonBlock(BlockSetType.STONE, 20, buttonProperties("material_button_stone")));
		PARAM_BUTTON_METAL = regBlock("material_button_metal", new ParametricButtonBlock(BlockSetType.IRON, 20, buttonProperties("material_button_metal")));
		PARAM_BUTTON_WOOD = regBlock("material_button_wood", new ParametricButtonBlock(BlockSetType.OAK, 30, buttonProperties("material_button_wood")));
		PARAM_PRESSURE_PLATE_STONE = regBlock(
				"material_pressure_plate_stone",
				properties -> new ParametricPressurePlateBlock(BlockSetType.STONE, properties),
				BlockBehaviour.Properties.of()
						.mapColor(MapColor.STONE)
						.forceSolidOn()
						.instrument(NoteBlockInstrument.BASEDRUM)
						.noCollission()
						.strength(0.5F)
						.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_pressure_plate_stone")))
		);
		PARAM_PRESSURE_PLATE_WOOD = regBlock(
				"material_pressure_plate_wood",
				properties -> new ParametricPressurePlateBlock(BlockSetType.OAK, properties),
				BlockBehaviour.Properties.of()
						.mapColor(PARAM_BLOCK.defaultMapColor())
						.forceSolidOn()
						.instrument(NoteBlockInstrument.BASS)
						.noCollission()
						.strength(0.5F)
						.ignitedByLava()
						.pushReaction(PushReaction.DESTROY)
						.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_pressure_plate_wood")))
		);
		PARAM_PRESSURE_PLATE_METAL = regBlock(
				"material_pressure_plate_metal",
				ParametricMetalPressurePlateBlock::new,
				BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().noCollission()
						.strength(0.5F).pushReaction(PushReaction.DESTROY)
						.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_pressure_plate_metal")))
		);
//		PARAM_DOOR = regBlock("material_door", new ParametricDoorBlock(Block.Properties.of()
//				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_door")))
//				.strength(5.0f).sound(SoundType.METAL).noOcclusion()));
//
//		PARAM_TRAPDOOR = regBlock("material_trapdoor", new ParametricTrapdoorBlock(Block.Properties.of()
//				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_trapdoor")))
//				.strength(5.0f).sound(SoundType.METAL).noOcclusion()));

		// -------- Crystal-specific --------
		PARAM_CRYSTAL_PANE = regBlock("material_crystal_pane", new ParametricPaneBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_crystal_pane")))
				.strength(0.3f).noOcclusion().sound(SoundType.GLASS)));

		PARAM_ROD_BLOCK = regBlock("material_rod_block", new ParametricRodBlock(Block.Properties.of()
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id("material_rod_block")))
				.strength(1.0f).lightLevel(s -> 14).noOcclusion().sound(SoundType.AMETHYST)));
	}

	public static BlockBehaviour.Properties buttonProperties(String name) {
		return BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY)
				.setId(ResourceKey.create(Registries.BLOCK, RAAMaterials.id(name)));
	}
}
