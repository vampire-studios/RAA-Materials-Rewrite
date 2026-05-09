/*
// src/main/java/net/vampirestudios/raaMaterials/client/ClientPools.java
package net.vampirestudios.raaMaterials.material;

import java.util.ArrayList;

import static net.vampirestudios.raaMaterials.RAAMaterials.id;

public final class ClientPools extends MaterialAssetsLocal.Pools {
	public static final ClientPools INSTANCE = new ClientPools();
	private ClientPools() {
		// initialize inherited lists so they're never null
		this.metalOreVeinTextures        = new ArrayList<>();
		this.gemOreVeinTextures          = new ArrayList<>();

		this.metalStorageBlockTextures   = new ArrayList<>();
		this.gemStorageBlockTextures     = new ArrayList<>();
		this.crystalBlocks               = new ArrayList<>();

		this.rawMaterialBlockTextures    = new ArrayList<>();
		this.buddingCrystalBlocks        = new ArrayList<>();
		this.crystals                    = new ArrayList<>();

		this.rawMaterialTextures         = new ArrayList<>();
		this.ingotTextures               = new ArrayList<>();
		this.nuggetTextures              = new ArrayList<>();
		this.dustTextures                = new ArrayList<>();
		this.plateTextures               = new ArrayList<>();
		this.gemTextures                 = new ArrayList<>();
		this.shards                      = new ArrayList<>();

		this.pickaxeHeadTextures         = new ArrayList<>();
		this.pickaxeStickTextures        = new ArrayList<>();

		this.axeHeadTextures             = new ArrayList<>();
		this.axeStickTextures            = new ArrayList<>();
		this.swordBladeTextures          = new ArrayList<>();
		this.swordHandleTextures         = new ArrayList<>();
		this.shovelHeadTextures          = new ArrayList<>();
		this.shovelStickTextures         = new ArrayList<>();
		this.hoeHeadTextures             = new ArrayList<>();
		this.hoeStickTextures            = new ArrayList<>();
	}

	public void loadDefaults() {
		// clear old data in case of reload
		metalOreVeinTextures.clear();
		metalStorageBlockTextures.clear();
		rawMaterialBlockTextures.clear();
		gemOreVeinTextures.clear();
		gemStorageBlockTextures.clear();
		rawMaterialTextures.clear();
		ingotTextures.clear();
		nuggetTextures.clear();
		plateTextures.clear();
		dustTextures.clear();
//		crushedOreTextures.clear();
		gemTextures.clear();
		shards.clear();
		crystalBlocks.clear();
		buddingCrystalBlocks.clear();
		crystals.clear();
		swordBladeTextures.clear();
		swordHandleTextures.clear();
		pickaxeHeadTextures.clear();
		pickaxeStickTextures.clear();
		axeHeadTextures.clear();
		axeStickTextures.clear();
		hoeHeadTextures.clear();
		hoeStickTextures.clear();
		shovelHeadTextures.clear();
		shovelStickTextures.clear();

		// now fill
		for (int i = 0; i < 40; i++) metalOreVeinTextures.add(id("ores/metals/ore_" + (i+1)));
		for (int i = 0; i < 23; i++) metalStorageBlockTextures.add(id("storage_blocks/metals/metal_" + (i+1)));
		for (int i = 0; i < 15; i++) rawMaterialBlockTextures.add(id("storage_blocks/metals/raw_" + (i+1)));

		for (int i = 0; i < 18; i++) rawMaterialTextures.add(id("raw/raw_" + (i+1)));
		for (int i = 0; i < 29; i++) ingotTextures.add(id("ingots/ingot_" + (i+1)));
		for (int i = 0; i < 10; i++) nuggetTextures.add(id("nuggets/nugget_" + (i+1)));
		for (int i = 0; i < 3;  i++) plateTextures.add(id("plates/plate_" + (i+1)));
		for (int i = 0; i < 5;  i++) dustTextures.add(id("dusts/dust_" + (i+1)));
//		for (int i = 0; i < 4;  i++) crushedOreTextures.add(id("crushed_ore/crushed_ore_" + (i+1) + ".png"));

		for (int i = 0; i < 5;  i++) crystalBlocks.add(id("crystal/crystal_block_" + (i+1)));
		buddingCrystalBlocks.add(id("crystal/budding_crystal_block_1"));
		for (int i = 0; i < 9;  i++) crystals.add(id("crystal/crystal_" + (i+1)));
		for (int i = 0; i < 7;  i++) shards.add(id("crystal_items/shard_" + (i+1)));

		for (int i = 0; i < 33; i++) gemOreVeinTextures.add(id("ores/gems/ore_" + (i+1)));
		for (int i = 0; i < 16; i++) gemStorageBlockTextures.add(id("storage_blocks/gems/gem_" + (i+1)));
		for (int i = 0; i < 33; i++) gemTextures.add(id("gems/gem_" + (i+1)));

		for (int i = 0; i < 13; i++) swordBladeTextures.add(id("tools/sword/blade_" + i));
		for (int i = 0; i < 11; i++) swordHandleTextures.add(id("tools/sword/handle_" + i));
		for (int i = 0; i < 11; i++) pickaxeHeadTextures.add(id("tools/pickaxe/pickaxe_" + i));
		for (int i = 0; i < 10; i++) pickaxeStickTextures.add(id("tools/pickaxe/stick_" + (i+1)));
		for (int i = 0; i < 11; i++) axeHeadTextures.add(id("tools/axe/axe_head_" + (i+1)));
		for (int i = 0; i < 8;  i++) axeStickTextures.add(id("tools/axe/axe_stick_" + (i+1)));
		for (int i = 0; i < 9;  i++) hoeHeadTextures.add(id("tools/hoe/hoe_head_" + (i+1)));
		for (int i = 0; i < 9;  i++) hoeStickTextures.add(id("tools/hoe/hoe_stick_" + (i+1)));
		for (int i = 0; i < 11; i++) shovelHeadTextures.add(id("tools/shovel/shovel_head_" + (i+1)));
		for (int i = 0; i < 11; i++) shovelStickTextures.add(id("tools/shovel/shovel_stick_" + (i+1)));
	}
}
*/
