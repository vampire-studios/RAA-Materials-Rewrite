package net.vampirestudios.raaMaterials.client;

import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.arrp.json.blockstate.BlockstateTemplates;
import net.vampirestudios.arrp.json.blockstate.JBlockModel;
import net.vampirestudios.arrp.json.blockstate.JState;
import net.vampirestudios.arrp.json.iteminfo.JItemInfo;
import net.vampirestudios.arrp.json.iteminfo.model.JItemModel;
import net.vampirestudios.arrp.json.iteminfo.model.JModelBasic;
import net.vampirestudios.arrp.json.iteminfo.model.JSelectCase;
import net.vampirestudios.arrp.json.iteminfo.property.JPropertyComponent;
import net.vampirestudios.arrp.json.iteminfo.property.JPropertyDisplayContext;
import net.vampirestudios.arrp.json.iteminfo.tint.JTint;
import net.vampirestudios.arrp.json.iteminfo.tint.JTintConstant;
import net.vampirestudios.arrp.json.models.JModel;
import net.vampirestudios.raaMaterials.ARRPGenerationHelper;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialAssets;
import net.vampirestudios.raaMaterials.material.MaterialAssetsDef;
import net.vampirestudios.raaMaterials.material.MaterialDef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static net.vampirestudios.raaMaterials.client.MaterialTexturePickers.*;

public final class MaterialAssetBuilders {
    private static final JPropertyComponent MAT_COMP = JPropertyComponent.component("raa_materials:material");

    public static final Identifier ORE_SHARED_ID = RAAMaterials.id("material_ore");
    public static final Identifier BLOCK_SHARED_ID = RAAMaterials.id("material_block");
    public static final Identifier RAW_BLOCK_SHARED_ID = RAAMaterials.id("material_raw_block");
    public static final Identifier SHINGLES_SHARED_ID = RAAMaterials.id("material_shingles");
    public static final Identifier PLATE_BLOCK_SHARED_ID = RAAMaterials.id("material_plate_block");

    public static final Identifier SLAB_SHARED_ID = RAAMaterials.id("material_slab");
    public static final Identifier STAIRS_SHARED_ID = RAAMaterials.id("material_stairs");
    public static final Identifier WALL_SHARED_ID = RAAMaterials.id("material_wall");

    public static final Identifier INGOT_SHARED_ID = RAAMaterials.id("material_ingot");
    public static final Identifier RAW_SHARED_ID = RAAMaterials.id("material_raw");
    public static final Identifier NUGGET_SHARED_ID = RAAMaterials.id("material_nugget");
    public static final Identifier DUST_SHARED_ID = RAAMaterials.id("material_dust");
    public static final Identifier PLATE_SHARED_ID = RAAMaterials.id("material_sheet");
    public static final Identifier SHARD_SHARED_ID = RAAMaterials.id("material_shard");
    public static final Identifier GEAR_SHARED_ID = RAAMaterials.id("material_gear");
    public static final Identifier GEM_SHARED_ID = RAAMaterials.id("material_gem");
    public static final Identifier CRYSTAL_SHARED_ID = RAAMaterials.id("material_crystal");
    public static final Identifier BALL_SHARED_ID = RAAMaterials.id("material_ball");
    public static final Identifier ROD_SHARED_ID = RAAMaterials.id("material_rod");
    public static final Identifier WIRE_SHARED_ID = RAAMaterials.id("material_wire");
    public static final Identifier COIL_SHARED_ID = RAAMaterials.id("material_coil");
    public static final Identifier RIVET_SHARED_ID = RAAMaterials.id("material_rivet");
    public static final Identifier BOLT_SHARED_ID = RAAMaterials.id("material_bolt");
    public static final Identifier NAIL_SHARED_ID = RAAMaterials.id("material_nail");
    public static final Identifier RING_SHARED_ID = RAAMaterials.id("material_ring");

    public static final Identifier SHOVEL_SHARED_ID = RAAMaterials.id("material_shovel");
    public static final Identifier HOE_SHARED_ID = RAAMaterials.id("material_hoe");
    public static final Identifier SWORD_SHARED_ID = RAAMaterials.id("material_sword");
    public static final Identifier PICKAXE_SHARED_ID = RAAMaterials.id("material_pickaxe");
    public static final Identifier AXE_SHARED_ID = RAAMaterials.id("material_axe");
    public static final Identifier SPEAR_SHARED_ID = RAAMaterials.id("material_spear");
    public static final Identifier HAMMER_SHARED_ID = RAAMaterials.id("material_hammer");
    public static final Identifier DAGGER_SHARED_ID = RAAMaterials.id("material_dagger");

    public static final Identifier PACKED_SOIL_SHARED_ID = RAAMaterials.id("material_packed_soil");
    public static final Identifier HORSE_ARMOR_SHARED_ID = RAAMaterials.id("material_horse_armor");
    public static final Identifier WOLF_ARMOR_SHARED_ID = RAAMaterials.id("material_wolf_armor");
    public static final Identifier NAUTILUS_ARMOR_SHARED_ID = RAAMaterials.id("material_nautilus_armor");

    public static final List<Identifier> COLORED_BLOCKS = List.of(
            ORE_SHARED_ID,
            BLOCK_SHARED_ID,
            RAW_BLOCK_SHARED_ID,
            SHINGLES_SHARED_ID,
            PLATE_BLOCK_SHARED_ID,
            RAAMaterials.id("material_crystal_cluster"),
            RAAMaterials.id("material_crystal_bud_small"),
            RAAMaterials.id("material_crystal_bud_medium"),
            RAAMaterials.id("material_crystal_bud_large"),
            RAAMaterials.id("material_crystal_bricks"),
            RAAMaterials.id("material_glass"),
            RAAMaterials.id("material_tinted_glass"),
            RAAMaterials.id("material_basalt_lamp"),
            RAAMaterials.id("material_calcite_lamp"),
            RAAMaterials.id("material_lamp"),
            RAAMaterials.id("material_chain"),
            RAAMaterials.id("material_lantern"),
            RAAMaterials.id("material_door_metal"),
            RAAMaterials.id("material_door_wood"),
            RAAMaterials.id("material_trapdoor_metal"),
            RAAMaterials.id("material_trapdoor_wood"),
            RAAMaterials.id("material_fence"),
            RAAMaterials.id("material_fence_gate"),
            RAAMaterials.id("material_slab"),
            RAAMaterials.id("material_stairs"),
            RAAMaterials.id("material_wall"),
            RAAMaterials.id("material_sandstone_slab"),
            RAAMaterials.id("material_sandstone_stairs"),
            RAAMaterials.id("material_sandstone_wall"),
            RAAMaterials.id("material_brick_slab"),
            RAAMaterials.id("material_brick_stairs"),
            RAAMaterials.id("material_brick_wall"),
            RAAMaterials.id("material_polished_slab"),
            RAAMaterials.id("material_polished_stairs"),
            RAAMaterials.id("material_polished_wall"),
            RAAMaterials.id("material_bars"),
            RAAMaterials.id("material_grate"),
            RAAMaterials.id("material_button_metal"),
            RAAMaterials.id("material_button_stone"),
            RAAMaterials.id("material_button_wood"),
            RAAMaterials.id("material_pressure_plate_metal"),
            RAAMaterials.id("material_pressure_plate_stone"),
            RAAMaterials.id("material_pressure_plate_wood"),
            RAAMaterials.id("material_crystal_pane"),
            RAAMaterials.id("material_rod_block"),
            RAAMaterials.id("material_cobbled"),
            RAAMaterials.id("material_bricks"),
            RAAMaterials.id("material_polished"),
            RAAMaterials.id("material_sandstone"),
            RAAMaterials.id("material_cut"),
            RAAMaterials.id("material_smooth"),
            RAAMaterials.id("material_chiseled"),
            RAAMaterials.id("material_dried"),
            RAAMaterials.id("material_ceramic"),
            RAAMaterials.id("material_tiles"),
            RAAMaterials.id("material_mosaic"),
            RAAMaterials.id("material_mossy"),
            RAAMaterials.id("material_cracked"),
            RAAMaterials.id("material_pillar"),
            RAAMaterials.id("material_spike"),
            RAAMaterials.id("material_packed_soil"),
            RAAMaterials.id("material_crystal_block")
    );

    private MaterialAssetBuilders() {
    }

    private static final List<TextureRequirement> REQUIRED_TEXTURES = List.of(
            new TextureRequirement(Form.ORE, "oreVein", assets -> assets.blockTextures().oreVein()),
            new TextureRequirement(Form.RAW_BLOCK, "rawBlock", assets -> assets.blockTextures().rawBlock()),
            new TextureRequirement(Form.TINTED_GLASS, "tintedGlass", assets -> assets.crystalTextures().tintedGlass()),
            new TextureRequirement(Form.SHINGLES, "shingles", assets -> assets.blockTextures().shingles()),
            new TextureRequirement(Form.PLATE_BLOCK, "plateBlock", assets -> assets.blockTextures().plateBlock()),
            new TextureRequirement(Form.CRYSTAL_BRICKS, "crystalBricks", assets -> assets.crystalTextures().crystalBricks()),
            new TextureRequirement(Form.GLASS, "crystalGlass", assets -> assets.crystalTextures().crystalGlass()),
            new TextureRequirement(Form.COBBLED, "cobblestone", assets -> assets.blockTextures().cobblestone()),
            new TextureRequirement(Form.BRICKS, "bricks", assets -> assets.blockTextures().bricks()),
            new TextureRequirement(Form.POLISHED, "polished", assets -> assets.blockTextures().polished()),
            new TextureRequirement(Form.SANDSTONE, "sandstoneSide", assets -> assets.sandstoneTextures().sandstoneSide()),
            new TextureRequirement(Form.SANDSTONE, "sandstoneTop", assets -> assets.sandstoneTextures().sandstoneTop()),
            new TextureRequirement(Form.SANDSTONE, "sandstoneBottom", assets -> assets.sandstoneTextures().sandstoneBottom()),
            new TextureRequirement(Form.CUT, "cutSandstoneSide", assets -> assets.sandstoneTextures().cutSandstoneSide()),
            new TextureRequirement(Form.CUT, "sandstoneTop", assets -> assets.sandstoneTextures().sandstoneTop()),
            new TextureRequirement(Form.SMOOTH, "sandstoneTop", assets -> assets.sandstoneTextures().sandstoneTop()),
            new TextureRequirement(Form.INGOT, "ingot", assets -> assets.itemTextures().ingot()),
            new TextureRequirement(Form.RAW, "raw", assets -> assets.itemTextures().raw()),
            new TextureRequirement(Form.NUGGET, "nugget", assets -> assets.itemTextures().nugget()),
            new TextureRequirement(Form.DUST, "dust", assets -> assets.itemTextures().dust()),
            new TextureRequirement(Form.SHEET, "plate", assets -> assets.itemTextures().plate()),
            new TextureRequirement(Form.SHARD, "shard", assets -> assets.itemTextures().shard()),
            new TextureRequirement(Form.GEAR, "gear", assets -> assets.itemTextures().gear()),
            new TextureRequirement(Form.GEM, "gem", assets -> assets.itemTextures().gem()),
            new TextureRequirement(Form.BALL, "ball", assets -> assets.itemTextures().ball()),
            new TextureRequirement(Form.ROD, "rod", assets -> assets.itemTextures().rod()),
            new TextureRequirement(Form.WIRE, "wire", assets -> assets.itemTextures().wire()),
            new TextureRequirement(Form.COIL, "coil", assets -> assets.itemTextures().coil()),
            new TextureRequirement(Form.RIVET, "rivet", assets -> assets.itemTextures().rivet()),
            new TextureRequirement(Form.BOLT, "bolt", assets -> assets.itemTextures().bolt()),
            new TextureRequirement(Form.NAIL, "nail", assets -> assets.itemTextures().nail()),
            new TextureRequirement(Form.RING, "ring", assets -> assets.itemTextures().ring()),
            new TextureRequirement(Form.BUDDING, "budding", assets -> assets.crystalTextures().budding()),
            new TextureRequirement(Form.BUD_SMALL, "budSmall", assets -> assets.crystalTextures().budSmall()),
            new TextureRequirement(Form.BUD_MEDIUM, "budMedium", assets -> assets.crystalTextures().budMedium()),
            new TextureRequirement(Form.BUD_LARGE, "budLarge", assets -> assets.crystalTextures().budLarge()),
            new TextureRequirement(Form.CLUSTER, "cluster", assets -> assets.crystalTextures().cluster()),
            new TextureRequirement(Form.CRYSTAL, "crystalItem", assets -> assets.crystalTextures().crystalItem()),
            new TextureRequirement(Form.BASALT_LAMP, "lampBasalt", assets -> assets.crystalTextures().lampBasalt()),
            new TextureRequirement(Form.CALCITE_LAMP, "lampCalcite", assets -> assets.crystalTextures().lampCalcite()),
            new TextureRequirement(Form.PICKAXE, "pickaxeHead", assets -> assets.toolTextures().pickaxeHead()),
            new TextureRequirement(Form.PICKAXE, "pickaxeStick", assets -> assets.toolTextures().pickaxeStick()),
            new TextureRequirement(Form.AXE, "axeHead", assets -> assets.toolTextures().axeHead()),
            new TextureRequirement(Form.AXE, "axeStick", assets -> assets.toolTextures().axeStick()),
            new TextureRequirement(Form.SHOVEL, "shovelHead", assets -> assets.toolTextures().shovelHead()),
            new TextureRequirement(Form.SHOVEL, "shovelStick", assets -> assets.toolTextures().shovelStick()),
            new TextureRequirement(Form.HOE, "hoeHead", assets -> assets.toolTextures().hoeHead()),
            new TextureRequirement(Form.HOE, "hoeStick", assets -> assets.toolTextures().hoeStick()),
            new TextureRequirement(Form.SWORD, "swordBlade", assets -> assets.toolTextures().swordBlade()),
            new TextureRequirement(Form.SWORD, "swordHandle", assets -> assets.toolTextures().swordHandle()),
            new TextureRequirement(Form.SPEAR, "spearHead", assets -> assets.toolTextures().spearHead()),
            new TextureRequirement(Form.SPEAR, "spearHandle", assets -> assets.toolTextures().spearHandle()),
            new TextureRequirement(Form.SPEAR, "spearHeadInHand", assets -> assets.toolTextures().spearHeadInHand()),
            new TextureRequirement(Form.SPEAR, "spearHandleInHand", assets -> assets.toolTextures().spearHandleInHand()),
            // Legendary weapons — dedicated slots in LegendaryTextureSet
            new TextureRequirement(Form.HAMMER, "hammerHead",   assets -> assets.legendaryTextures().hammerHead()),
            new TextureRequirement(Form.HAMMER, "hammerHandle", assets -> assets.legendaryTextures().hammerHandle()),
            new TextureRequirement(Form.DAGGER, "daggerBlade",  assets -> assets.legendaryTextures().daggerBlade()),
            new TextureRequirement(Form.DAGGER, "daggerHandle", assets -> assets.legendaryTextures().daggerHandle()),
            // Mount armors — dedicated slots
            new TextureRequirement(Form.HORSE_ARMOR,    "horseArmor",    assets -> assets.legendaryTextures().horseArmor()),
            new TextureRequirement(Form.WOLF_ARMOR,     "wolfArmor",     assets -> assets.legendaryTextures().wolfArmor()),
            new TextureRequirement(Form.NAUTILUS_ARMOR, "nautilusArmor", assets -> assets.legendaryTextures().nautilusArmor()),
            new TextureRequirement(Form.CHAIN, "chain", assets -> assets.decorTextures().chain()),
            new TextureRequirement(Form.LANTERN, "lantern", assets -> assets.decorTextures().lantern()),
            new TextureRequirement(Form.DOOR, "doorBottom", assets -> assets.decorTextures().doorBottom()),
            new TextureRequirement(Form.TRAPDOOR, "trapdoor", assets -> assets.decorTextures().trapdoor()),
            new TextureRequirement(Form.FENCE, "fence", assets -> assets.decorTextures().fence()),
            new TextureRequirement(Form.FENCE_GATE, "fenceGate", assets -> assets.decorTextures().fenceGate())
    );

    /**
     * Pre-validates that every material has the textures required by its forms.
     * Collects all failures and logs them together before throwing, so the full
     * picture is visible in a single log scan rather than one crash per missing texture.
     */
    public static void validateTextures(MaterialAssetContext ctx) {
        var failures = new ArrayList<String>();
        var materials = ctx.materials();

        for (int idx = 0; idx < materials.size(); idx++) {
            var def = materials.get(idx);
            var id = def.nameInformation().id().toString();
            var assets = textures(def);
            var forms = def.forms();
            int materialIndex = idx;

            REQUIRED_TEXTURES.forEach(requirement -> requirement.validate(failures, materialIndex, id, forms, assets));
        }

        if (!failures.isEmpty()) {
            RAAMaterials.LOGGER.error("[RAA] {} missing texture(s) detected before asset build:", failures.size());
            failures.forEach(msg -> RAAMaterials.LOGGER.error("[RAA]{}", msg));
//            throw new IllegalStateException(
//                "[RAA] Asset build aborted: " + failures.size() + " missing texture(s) - see log for details");
        }
    }

    private record TextureRequirement(
            Form form,
            String textureName,
            Function<MaterialAssetsDef, Optional<Identifier>> texture
    ) {
        void validate(List<String> failures, int idx, String matId, List<Form> forms, MaterialAssetsDef assets) {
            requireTex(failures, idx, matId, forms, form, textureName, texture.apply(assets));
        }
    }

    private static void requireTex(List<String> failures, int idx, String matId,
                                   List<Form> forms, Form form, String texName,
                                   Optional<Identifier> tex) {
        if (forms.contains(form) && tex.isEmpty()) {
            failures.add("  " + matId + " [" + idx + "] " + form + " missing " + texName);
        }
    }

    public static void addFallbackModels(MaterialAssetContext ctx) {
        var rp = ctx.pack();

        rp.addModel(JModel.model("minecraft:item/stone"), RAAMaterials.id("item/material_ore"));
        rp.addModel(JModel.model("minecraft:item/iron_ingot"), RAAMaterials.id("item/material_ingot"));
        rp.addModel(JModel.model("minecraft:item/raw_iron"), RAAMaterials.id("item/material_raw"));
        rp.addModel(JModel.model("minecraft:item/gold_nugget"), RAAMaterials.id("item/material_nugget"));
        rp.addModel(JModel.model("minecraft:item/sugar"), RAAMaterials.id("item/material_dust"));
        rp.addModel(JModel.model("minecraft:item/iron_ingot"), RAAMaterials.id("item/material_plate"));
        rp.addModel(JModel.model("minecraft:item/stick"), RAAMaterials.id("item/material_rod"));
    }

    public static void buildOreFamilies(MaterialAssetContext ctx) {
        buildOreFamily(ctx, ORE_SHARED_ID, "block/material_ore/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().oreVein()
                        .orElseThrow(() -> new IllegalStateException("Missing oreVein texture for material at index " + idx)));
    }

    public static void buildBlockFamilies(MaterialAssetContext ctx) {
        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.BLOCK),
                BLOCK_SHARED_ID,
                "block/material_block/",
                idx -> pickBlockTexture(ctx.materials().get(idx), idx)
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.RAW_BLOCK),
                RAW_BLOCK_SHARED_ID,
                "block/material_raw_block/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().rawBlock()
                        .orElseThrow(() -> new IllegalStateException("Missing rawBlock texture for material at index " + idx))
        ));

        // Crystal block — solid block placed inside geode cavities (separate from material_block).
        // Gated on CLUSTER because all crystal materials have it and no other kind does.
        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.CLUSTER),
                RAAMaterials.id("material_crystal_block"),
                "block/material_crystal_block/",
                idx -> pickBlockTexture(ctx.materials().get(idx), idx)
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.CRYSTAL_BRICKS),
                RAAMaterials.id("material_crystal_bricks"),
                "block/material_crystal_bricks/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().crystalBricks()
                        .orElseThrow(() -> new IllegalStateException("Missing crystalBricks texture for material at index " + idx))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.GLASS),
                RAAMaterials.id("material_glass"),
                "block/material_glass/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().crystalGlass()
                        .orElseThrow(() -> new IllegalStateException("Missing crystalGlass texture for material at index " + idx))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.TINTED_GLASS),
                RAAMaterials.id("material_tinted_glass"),
                "block/material_tinted_glass/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().tintedGlass()
                        .orElseThrow(() -> new IllegalStateException("Missing tintedGlass texture for material at index " + idx))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.SHINGLES),
                SHINGLES_SHARED_ID,
                "block/material_shingles/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().shingles()
                        .orElseThrow(() -> new IllegalStateException("Missing shingles texture for material at index " + idx))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.PLATE_BLOCK),
                PLATE_BLOCK_SHARED_ID,
                "block/material_plate_block/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().plateBlock()
                        .orElseThrow(() -> new IllegalStateException("Missing plateBlock texture for material at index " + idx))
        ));

        buildBuildingFamilies(ctx);
    }

    private static void buildBuildingFamilies(MaterialAssetContext ctx) {
        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.COBBLED),
                RAAMaterials.id("material_cobbled"),
                "block/material_cobbled/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().cobblestone()
                        .orElseThrow(() -> new IllegalStateException("Missing cobblestone texture for material at index " + idx))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.BRICKS),
                RAAMaterials.id("material_bricks"),
                "block/material_bricks/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().bricks()
                        .orElseThrow(() -> new IllegalStateException("Missing bricks texture for material at index " + idx))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.POLISHED),
                RAAMaterials.id("material_polished"),
                "block/material_polished/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().polished()
                        .orElseThrow(() -> new IllegalStateException("Missing polished texture for material at index " + idx))
        ));

        buildSandstoneBlockFamily(ctx, Form.SANDSTONE, RAAMaterials.id("material_sandstone"), "block/material_sandstone/", false);
        buildSandstoneBlockFamily(ctx, Form.CUT, RAAMaterials.id("material_cut"), "block/material_cut/", true);

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.SMOOTH),
                RAAMaterials.id("material_smooth"),
                "block/material_smooth/",
                idx -> textures(ctx.materials().get(idx)).sandstoneTextures().sandstoneTop()
                        .orElse(RAAMaterials.id("storage_blocks/sand_" + oneIndexed(idx, AssetsThemeConfig.SAND_BLOCK_COUNT)))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.CHISELED),
                RAAMaterials.id("material_chiseled"),
                "block/material_chiseled/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().chiseled()
                        .orElse(RAAMaterials.id("stone/stone_chiseled_" + oneIndexed(idx, AssetsThemeConfig.STONE_CHISELED_COUNT)))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.DRIED),
                RAAMaterials.id("material_dried"),
                "block/material_dried/",
                idx -> RAAMaterials.id("stone/stone_bricks_" + oneIndexed(idx, AssetsThemeConfig.STONE_BRICKS_COUNT))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.CERAMIC),
                RAAMaterials.id("material_ceramic"),
                "block/material_ceramic/",
                idx -> RAAMaterials.id("stone/stone_tiles_" + oneIndexed(idx, AssetsThemeConfig.STONE_TILES_COUNT))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.PACKED_SOIL),
                RAAMaterials.id("material_packed_soil"),
                "block/material_packed_soil/",
                idx -> RAAMaterials.id("stone/stone_cobbled_" + oneIndexed(idx, AssetsThemeConfig.STONE_COBBLED_COUNT))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.TILES),
                RAAMaterials.id("material_tiles"),
                "block/material_tiles/",
                idx -> RAAMaterials.id("stone/stone_tiles_" + oneIndexed(idx, AssetsThemeConfig.STONE_TILES_COUNT))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.MOSAIC),
                RAAMaterials.id("material_mosaic"),
                "block/material_mosaic/",
                idx -> RAAMaterials.id("stone/stone_frame_" + oneIndexed(idx, AssetsThemeConfig.STONE_FRAME_COUNT))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.MOSSY),
                RAAMaterials.id("material_mossy"),
                "block/material_mossy/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().bricks()
                        .orElse(RAAMaterials.id("stone/stone_bricks_" + oneIndexed(idx, AssetsThemeConfig.STONE_BRICKS_COUNT)))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.CRACKED),
                RAAMaterials.id("material_cracked"),
                "block/material_cracked/",
                idx -> textures(ctx.materials().get(idx)).blockTextures().cobblestone()
                        .orElse(RAAMaterials.id("stone/stone_cobbled_" + oneIndexed(idx, AssetsThemeConfig.STONE_COBBLED_COUNT)))
        ));

        buildPillarFamily(
                ctx,
                RAAMaterials.id("material_pillar"),
                "block/material_pillar/",
                idx -> RAAMaterials.id("stone/stone_frame_" + oneIndexed(idx, AssetsThemeConfig.STONE_FRAME_COUNT)),
                idx -> RAAMaterials.id("stone/stone_tiles_" + oneIndexed(idx, AssetsThemeConfig.STONE_TILES_COUNT))
        );
    }

    public static void buildSpecialBlockFamilies(MaterialAssetContext ctx) {
        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.BASALT_LAMP),
                RAAMaterials.id("material_basalt_lamp"),
                "block/material_basalt_lamp/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().lampBasalt()
                        .orElse(RAAMaterials.id("crystal/basalt_lamp"))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.CALCITE_LAMP),
                RAAMaterials.id("material_calcite_lamp"),
                "block/material_calcite_lamp/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().lampCalcite()
                        .orElse(RAAMaterials.id("crystal/calcite_lamp"))
        ));

        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.LAMP),
                RAAMaterials.id("material_lamp"),
                "block/material_lamp/",
                idx -> pickFormTexture(Form.LAMP, ctx.materials().get(idx), idx)
        ));

        buildDoorFamily(ctx, RAAMaterials.id("material_door_metal"), "block/material_door_metal/");
        buildDoorFamily(ctx, RAAMaterials.id("material_door_wood"), "block/material_door_wood/");
        buildTrapdoorFamily(ctx, RAAMaterials.id("material_trapdoor_metal"), "block/material_trapdoor_metal/");
        buildTrapdoorFamily(ctx, RAAMaterials.id("material_trapdoor_wood"), "block/material_trapdoor_wood/");
        buildFenceFamily(ctx, RAAMaterials.id("material_fence"), "block/material_fence/");
        buildFenceGateFamily(ctx, RAAMaterials.id("material_fence_gate"), "block/material_fence_gate/");
        buildChainFamily(ctx, RAAMaterials.id("material_chain"), "block/material_chain/");
        buildLanternFamily(ctx, RAAMaterials.id("material_lantern"), "block/material_lantern/");
        buildBarsFamily(ctx, RAAMaterials.id("material_bars"), "block/material_bars/");
        buildGrateFamily(ctx, RAAMaterials.id("material_grate"), "block/material_grate/");
        buildButtonFamily(ctx, RAAMaterials.id("material_button_metal"), "block/material_button_metal/");
        buildButtonFamily(ctx, RAAMaterials.id("material_button_stone"), "block/material_button_stone/");
        buildButtonFamily(ctx, RAAMaterials.id("material_button_wood"), "block/material_button_wood/");
        buildPressurePlateFamily(ctx, RAAMaterials.id("material_pressure_plate_metal"), "block/material_pressure_plate_metal/", true);
        buildPressurePlateFamily(ctx, RAAMaterials.id("material_pressure_plate_stone"), "block/material_pressure_plate_stone/", false);
        buildPressurePlateFamily(ctx, RAAMaterials.id("material_pressure_plate_wood"), "block/material_pressure_plate_wood/", false);

        buildCrystalClusterFamily(
                ctx,
                RAAMaterials.id("material_crystal_cluster"),
                Form.CLUSTER,
                "block/material_crystal_cluster/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().cluster()
                        .orElse(RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, AssetsThemeConfig.CRYSTAL_CLUSTER_COUNT)))
        );

        buildCrystalBudFamily(
                ctx,
                RAAMaterials.id("material_crystal_bud_small"),
                Form.BUD_SMALL,
                "block/material_crystal_bud_small/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().budSmall()
                        .orElse(RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, AssetsThemeConfig.CRYSTAL_CLUSTER_COUNT)))
        );

        buildCrystalBudFamily(
                ctx,
                RAAMaterials.id("material_crystal_bud_medium"),
                Form.BUD_MEDIUM,
                "block/material_crystal_bud_medium/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().budMedium()
                        .orElse(RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, AssetsThemeConfig.CRYSTAL_CLUSTER_COUNT)))
        );

        buildCrystalBudFamily(
                ctx,
                RAAMaterials.id("material_crystal_bud_large"),
                Form.BUD_LARGE,
                "block/material_crystal_bud_large/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().budLarge()
                        .orElse(RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, AssetsThemeConfig.CRYSTAL_CLUSTER_COUNT)))
        );

        buildPaneFamily(
                ctx,
                RAAMaterials.id("material_crystal_pane"),
                "block/material_crystal_pane/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().crystalGlass()
                        .orElse(RAAMaterials.id("crystal/crystal_glass"))
        );

        buildRodBlockFamily(
                ctx,
                RAAMaterials.id("material_rod_block"),
                "block/material_rod_block/",
                idx -> RAAMaterials.id("crystal/crystal_" + oneIndexed(idx, AssetsThemeConfig.CRYSTAL_CLUSTER_COUNT))
        );

        buildSpikeFamily(ctx, RAAMaterials.id("material_spike"), "block/material_spike/");
    }

    public static void buildShapeFamilies(MaterialAssetContext ctx) {
        buildSlabFamilyForBlock(ctx, SLAB_SHARED_ID);
        buildStairsFamilyForBlock(ctx, STAIRS_SHARED_ID);
        buildWallFamilyForBlock(ctx, WALL_SHARED_ID);
        buildSlabFamilyForSandstone(ctx, RAAMaterials.id("material_sandstone_slab"));
        buildStairsFamilyForSandstone(ctx, RAAMaterials.id("material_sandstone_stairs"));
        buildWallFamilyForSandstone(ctx, RAAMaterials.id("material_sandstone_wall"));
        buildSlabFamilyForForm(ctx, RAAMaterials.id("material_brick_slab"), Form.BRICKS, "block/material_bricks/");
        buildStairsFamilyForForm(ctx, RAAMaterials.id("material_brick_stairs"), Form.BRICKS, "block/material_bricks/");
        buildWallFamilyForForm(ctx, RAAMaterials.id("material_brick_wall"), Form.BRICKS, "block/material_bricks/");
        buildSlabFamilyForForm(ctx, RAAMaterials.id("material_polished_slab"), Form.POLISHED, "block/material_polished/");
        buildStairsFamilyForForm(ctx, RAAMaterials.id("material_polished_stairs"), Form.POLISHED, "block/material_polished/");
        buildWallFamilyForForm(ctx, RAAMaterials.id("material_polished_wall"), Form.POLISHED, "block/material_polished/");
    }

    public static void buildItemFamilies(MaterialAssetContext ctx) {
        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.INGOT),
                INGOT_SHARED_ID,
                "item/material_ingot/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().ingot()
                        .orElseThrow(() -> new IllegalStateException("Missing ingot texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.RAW),
                RAW_SHARED_ID,
                "item/material_raw/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().raw()
                        .orElseThrow(() -> new IllegalStateException("Missing raw texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.NUGGET),
                NUGGET_SHARED_ID,
                "item/material_nugget/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().nugget()
                        .orElseThrow(() -> new IllegalStateException("Missing nugget texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.DUST),
                DUST_SHARED_ID,
                "item/material_dust/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().dust()
                        .orElseThrow(() -> new IllegalStateException("Missing dust texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.SHEET),
                PLATE_SHARED_ID,
                "item/material_plate/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().plate()
                        .orElseThrow(() -> new IllegalStateException("Missing plate texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.SHARD),
                SHARD_SHARED_ID,
                "item/material_shard/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().shard()
                        .orElseThrow(() -> new IllegalStateException("Missing shard texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.GEAR),
                GEAR_SHARED_ID,
                "item/material_gear/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().gear()
                        .orElseThrow(() -> new IllegalStateException("Missing gear texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.GEM),
                GEM_SHARED_ID,
                "item/material_gem/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().gem()
                        .orElseThrow(() -> new IllegalStateException("Missing gem texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.CRYSTAL),
                CRYSTAL_SHARED_ID,
                "item/material_crystal/",
                idx -> textures(ctx.materials().get(idx)).crystalTextures().crystalItem()
                        .orElseThrow(() -> new IllegalStateException("Missing crystalItem texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.BALL),
                BALL_SHARED_ID,
                "item/material_ball/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().ball()
                        .orElseThrow(() -> new IllegalStateException("Missing ball texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.ROD),
                ROD_SHARED_ID,
                "item/material_rod/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().rod()
                        .orElseThrow(() -> new IllegalStateException("Missing rod texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.WIRE),
                WIRE_SHARED_ID,
                "item/material_wire/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().wire()
                        .orElseThrow(() -> new IllegalStateException("Missing wire texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.COIL),
                COIL_SHARED_ID,
                "item/material_coil/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().coil()
                        .orElseThrow(() -> new IllegalStateException("Missing coil texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.RIVET),
                RIVET_SHARED_ID,
                "item/material_rivet/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().rivet()
                        .orElseThrow(() -> new IllegalStateException("Missing rivet texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.BOLT),
                BOLT_SHARED_ID,
                "item/material_bolt/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().bolt()
                        .orElseThrow(() -> new IllegalStateException("Missing bolt texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.NAIL),
                NAIL_SHARED_ID,
                "item/material_nail/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().nail()
                        .orElseThrow(() -> new IllegalStateException("Missing nail texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.RING),
                RING_SHARED_ID,
                "item/material_ring/",
                idx -> textures(ctx.materials().get(idx)).itemTextures().ring()
                        .orElseThrow(() -> new IllegalStateException("Missing ring texture for material at index " + idx))
        ));

        buildLayeredItemFamily(ctx, Form.SHOVEL, SHOVEL_SHARED_ID, "item/material_shovel/",
                idx -> textures(ctx.materials().get(idx)).toolTextures().shovelHead()
                        .orElseThrow(() -> new IllegalStateException("Missing shovelHead texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).toolTextures().shovelStick()
                        .orElseThrow(() -> new IllegalStateException("Missing shovelStick texture for material at index " + idx)));

        buildLayeredItemFamily(ctx, Form.HOE, HOE_SHARED_ID, "item/material_hoe/",
                idx -> textures(ctx.materials().get(idx)).toolTextures().hoeHead()
                        .orElseThrow(() -> new IllegalStateException("Missing hoeHead texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).toolTextures().hoeStick()
                        .orElseThrow(() -> new IllegalStateException("Missing hoeStick texture for material at index " + idx)));

        buildLayeredItemFamily(ctx, Form.SWORD, SWORD_SHARED_ID, "item/material_sword/",
                idx -> textures(ctx.materials().get(idx)).toolTextures().swordBlade()
                        .orElseThrow(() -> new IllegalStateException("Missing swordBlade texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).toolTextures().swordHandle()
                        .orElseThrow(() -> new IllegalStateException("Missing swordHandle texture for material at index " + idx)));

        buildLayeredItemFamily(ctx, Form.PICKAXE, PICKAXE_SHARED_ID, "item/material_pickaxe/",
                idx -> textures(ctx.materials().get(idx)).toolTextures().pickaxeHead()
                        .orElseThrow(() -> new IllegalStateException("Missing pickaxeHead texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).toolTextures().pickaxeStick()
                        .orElseThrow(() -> new IllegalStateException("Missing pickaxeStick texture for material at index " + idx)));

        buildLayeredItemFamily(ctx, Form.AXE, AXE_SHARED_ID, "item/material_axe/",
                idx -> textures(ctx.materials().get(idx)).toolTextures().axeHead()
                        .orElseThrow(() -> new IllegalStateException("Missing axeHead texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).toolTextures().axeStick()
                        .orElseThrow(() -> new IllegalStateException("Missing axeStick texture for material at index " + idx)));

        buildSpearItemFamily(ctx, SPEAR_SHARED_ID, "item/material_spear/",
                idx -> textures(ctx.materials().get(idx)).toolTextures().spearHead()
                        .orElseThrow(() -> new IllegalStateException("Missing spearHead texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).toolTextures().spearHandle()
                        .orElseThrow(() -> new IllegalStateException("Missing spearHandle texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).toolTextures().spearHeadInHand()
                        .orElseThrow(() -> new IllegalStateException("Missing spearHeadInHand texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).toolTextures().spearHandleInHand()
                        .orElseThrow(() -> new IllegalStateException("Missing spearHandleInHand texture for material at index " + idx)));

        // Legendary weapons — dedicated legendary texture slots
        buildLayeredItemFamily(ctx, Form.HAMMER, HAMMER_SHARED_ID, "item/material_hammer/",
                idx -> textures(ctx.materials().get(idx)).legendaryTextures().hammerHead()
                        .orElseThrow(() -> new IllegalStateException("Missing hammerHead texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).legendaryTextures().hammerHandle()
                        .orElseThrow(() -> new IllegalStateException("Missing hammerHandle texture for material at index " + idx)));

        buildLayeredItemFamily(ctx, Form.DAGGER, DAGGER_SHARED_ID, "item/material_dagger/",
                idx -> textures(ctx.materials().get(idx)).legendaryTextures().daggerBlade()
                        .orElseThrow(() -> new IllegalStateException("Missing daggerBlade texture for material at index " + idx)),
                idx -> textures(ctx.materials().get(idx)).legendaryTextures().daggerHandle()
                        .orElseThrow(() -> new IllegalStateException("Missing daggerHandle texture for material at index " + idx)));

        // Mount armors — flat item, dedicated legendary texture slots
        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.HORSE_ARMOR),
                HORSE_ARMOR_SHARED_ID,
                "item/material_horse_armor/",
                idx -> textures(ctx.materials().get(idx)).legendaryTextures().horseArmor()
                        .orElseThrow(() -> new IllegalStateException("Missing horseArmor texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.WOLF_ARMOR),
                WOLF_ARMOR_SHARED_ID,
                "item/material_wolf_armor/",
                idx -> textures(ctx.materials().get(idx)).legendaryTextures().wolfArmor()
                        .orElseThrow(() -> new IllegalStateException("Missing wolfArmor texture for material at index " + idx))
        ));

        buildItemFamily(ctx, new ItemFamilySpec(
                List.of(Form.NAUTILUS_ARMOR),
                NAUTILUS_ARMOR_SHARED_ID,
                "item/material_nautilus_armor/",
                idx -> textures(ctx.materials().get(idx)).legendaryTextures().nautilusArmor()
                        .orElseThrow(() -> new IllegalStateException("Missing nautilusArmor texture for material at index " + idx))
        ));
    }

    private static void buildBlockFamily(MaterialAssetContext ctx, BlockFamilySpec spec) {
        var variants = new ArrayList<JBlockModelEntry>();
        var select = JItemModel.select().property(MAT_COMP);

        ctx.forEachMaterialWithAny(spec.forms(), (idx, def) -> {
            var modelId = RAAMaterials.id(spec.modelPrefix() + def.nameInformation().id().getPath());

            ARRPGenerationHelper.generateAllTintedBlockModel(ctx.pack(), modelId, spec.texture().pick(idx));

            variants.add(new JBlockModelEntry(idx, JState.model(modelId)));
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(modelId, def)));
        });

        if (variants.isEmpty()) {
            addFallbackItemModel(ctx.pack(), spec.sharedId());
            return;
        }

        var variant = JState.variant();
        for (var entry : variants) {
            variant.put("mat", entry.idx(), entry.model());
        }

        ctx.pack().addBlockState(JState.state(variant), spec.sharedId());

        select.fallback(JModelBasic.of("minecraft:block/stone"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), spec.sharedId());
    }

    private static void buildSandstoneBlockFamily(MaterialAssetContext ctx, Form form, Identifier sharedBlockId, String modelPrefix, boolean cut) {
        var variants = new ArrayList<JBlockModelEntry>();
        var select = JItemModel.select().property(MAT_COMP);

        ctx.forEachMaterialWith(form, (idx, def) -> {
            var modelId = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath());
            var sandstone = textures(def).sandstoneTextures();
            var fallback = RAAMaterials.id("storage_blocks/sand_" + oneIndexed(idx, AssetsThemeConfig.SAND_BLOCK_COUNT));
            var top = sandstone.sandstoneTop().orElse(fallback);
            var side = cut ? sandstone.cutSandstoneSide().orElse(fallback) : sandstone.sandstoneSide().orElse(fallback);
            var bottom = cut ? top : sandstone.sandstoneBottom().orElse(fallback);

            ARRPGenerationHelper.generateTopBottomBlockModel(ctx.pack(), modelId, top, bottom, side);
            variants.add(new JBlockModelEntry(idx, JState.model(modelId)));
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(modelId, def)));
        });

        if (variants.isEmpty()) {
            addFallbackItemModel(ctx.pack(), sharedBlockId);
            return;
        }

        var variant = JState.variant();
        for (var entry : variants) {
            variant.put("mat", entry.idx(), entry.model());
        }

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/sandstone"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildOreFamily(
            MaterialAssetContext ctx,
            Identifier sharedBlockId,
            String perModelPrefix,
            MaterialTexturePickers.TexPicker oreTexture
    ) {
        var variants = new ArrayList<JBlockModelEntry>();
        var select = JItemModel.select().property(MAT_COMP);

        ctx.forEachMaterialWith(Form.ORE, (idx, def) -> {
            var modelId = RAAMaterials.id(perModelPrefix + def.nameInformation().id().getPath());

            addOreBlockModel(ctx.pack(), modelId, def.host().baseTexture(), oreTexture.pick(idx));

            variants.add(new JBlockModelEntry(idx, JState.model(modelId)));
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(modelId, def)));
        });

        if (variants.isEmpty()) {
            return;
        }

        var variant = JState.variant();
        for (var entry : variants) {
            variant.put("mat", entry.idx(), entry.model());
        }

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);

        select.fallback(JModelBasic.of("minecraft:block/stone"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildItemFamily(MaterialAssetContext ctx, ItemFamilySpec spec) {
        var select = JItemModel.select().property(MAT_COMP);
        int cases = 0;

        for (int idx = 0; idx < ctx.materials().size(); idx++) {
            MaterialDef def = ctx.materials().get(idx);

            if (!ctx.hasAny(def, spec.forms())) {
                continue;
            }

            var modelId = RAAMaterials.id(spec.modelPrefix() + def.nameInformation().id().getPath());
            addGeneratedItemModel(ctx.pack(), modelId, spec.texture().pick(idx));

            select.addCase(JSelectCase.of(
                    def.nameInformation().id().toString(),
                    JModelBasic.model(modelId.toString())
                            .tint(new JTintConstant(MaterialsAssets.opaqueColor(def.primaryColor())))
            ));

            cases++;
        }

        if (cases == 0) {
            addFallbackItemModel(ctx.pack(), spec.itemId());
            return;
        }

        select.fallback(JModelBasic.of(spec.itemId().withPrefix("item/").toString()));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), spec.itemId());
    }

    private static void buildLayeredItemFamily(
            MaterialAssetContext ctx,
            Form form,
            Identifier itemId,
            String modelPrefix,
            MaterialTexturePickers.TexPicker layer0,
            MaterialTexturePickers.TexPicker layer1
    ) {
        var select = JItemModel.select().property(MAT_COMP);
        int cases = 0;

        ctx.forEachMaterialWith(form, (idx, def) -> {
            var modelId = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath());

            var model = JModel.model("minecraft:item/handheld")
                    .textures(JModel.textures()
                            .var("layer0", itemTexture(layer0.pick(idx)))
                            .var("layer1", itemTexture(layer1.pick(idx)))
                    );

            ctx.pack().addModel(model, modelId);

            select.addCase(JSelectCase.of(
                    def.nameInformation().id().toString(),
                    JModelBasic.model(modelId.toString())
                            .tint(new JTintConstant(MaterialsAssets.opaqueColor(def.primaryColor())))
            ));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, form)) {
                cases++;
            }
        }

        if (cases == 0) {
            ctx.pack().addItemModelInfo(new JItemInfo().model(JModelBasic.of(fallbackToolModel(form))), itemId);
            return;
        }

        select.fallback(JModelBasic.of(fallbackToolModel(form)));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), itemId);
    }

    private static void buildSpearItemFamily(
            MaterialAssetContext ctx,
            Identifier itemId,
            String modelPrefix,
            MaterialTexturePickers.TexPicker inventoryHead,
            MaterialTexturePickers.TexPicker inventoryHandle,
            MaterialTexturePickers.TexPicker inHandHead,
            MaterialTexturePickers.TexPicker inHandHandle
    ) {
        var guiSelect = JItemModel.select().property(MAT_COMP);
        var inHandSelect = JItemModel.select().property(MAT_COMP);
        int cases = 0;

        ctx.forEachMaterialWith(Form.SPEAR, (idx, def) -> {
            var modelId = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath());
            var inHandModelId = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath() + "_in_hand");

            var guiModel = JModel.model("minecraft:item/generated")
                    .textures(JModel.textures()
                            .var("layer0", itemTexture(inventoryHead.pick(idx)))
                            .var("layer1", itemTexture(inventoryHandle.pick(idx)))
                    );
            var inHandModel = JModel.model("minecraft:item/spear_in_hand")
                    .textures(JModel.textures()
                            .var("layer0", itemTexture(inHandHead.pick(idx)))
                            .var("layer1", itemTexture(inHandHandle.pick(idx)))
                    );

            ctx.pack().addModel(guiModel, modelId);
            ctx.pack().addModel(inHandModel, inHandModelId);

            var tint = new JTintConstant(MaterialsAssets.opaqueColor(def.primaryColor()));
            guiSelect.addCase(JSelectCase.of(def.nameInformation().id().toString(), JModelBasic.model(modelId.toString()).tint(tint)));
            inHandSelect.addCase(JSelectCase.of(def.nameInformation().id().toString(), JModelBasic.model(inHandModelId.toString()).tint(tint)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.SPEAR)) {
                cases++;
            }
        }

        if (cases == 0) {
            ctx.pack().addItemModelInfo(new JItemInfo().model(JModelBasic.of("minecraft:item/stone_spear")), itemId);
            return;
        }

        guiSelect.fallback(JModelBasic.of("minecraft:item/stone_spear"));
        inHandSelect.fallback(JModelBasic.of("minecraft:item/stone_spear_in_hand"));

        var displaySelect = JItemModel.select()
                .property(JPropertyDisplayContext.displayContext())
                .addCase(JSelectCase.of(List.of("gui", "ground", "fixed", "on_shelf"), guiSelect))
                .fallback(inHandSelect);

        ctx.pack().addItemModelInfo(new JItemInfo().model(displaySelect), itemId);
    }

    private static String fallbackToolModel(Form form) {
        return switch (form) {
            case PICKAXE -> "minecraft:item/stone_pickaxe";
            case AXE -> "minecraft:item/stone_axe";
            case SHOVEL -> "minecraft:item/stone_shovel";
            case HOE -> "minecraft:item/stone_hoe";
            case SWORD -> "minecraft:item/stone_sword";
            case SPEAR -> "minecraft:item/stone_spear";
            default -> "minecraft:item/stone_pickaxe";
        };
    }

    private static void buildSlabFamilyForBlock(MaterialAssetContext ctx, Identifier sharedSlabId) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        for (int idx = 0; idx < ctx.materials().size(); idx++) {
            var def = ctx.materials().get(idx);

            if (!ctx.has(def, Form.SLAB) || !ctx.has(def, Form.BLOCK)) {
                continue;
            }

            var tex = pickBlockTexture(def, idx);
            var path = def.nameInformation().id().getPath();

            var bottom = RAAMaterials.id("block/material_block/" + path + "_slab");
            var top = RAAMaterials.id("block/material_block/" + path + "_slab_top");
            var full = RAAMaterials.id("block/material_block/" + path);

            addSlabModels(ctx.pack(), bottom, top, tex);
            BlockstateTemplates.addSlab(variant, Map.of("mat", idx), JState.model(bottom), JState.model(top), JState.model(full));

            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(bottom, def)));
            cases++;
        }

        if (cases == 0) {
            return;
        }

        ctx.pack().addBlockState(JState.state(variant), sharedSlabId);
        select.fallback(JModelBasic.of("minecraft:block/stone"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedSlabId);
    }

    private static void buildStairsFamilyForBlock(MaterialAssetContext ctx, Identifier sharedStairsId) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        for (int idx = 0; idx < ctx.materials().size(); idx++) {
            var def = ctx.materials().get(idx);

            if (!ctx.has(def, Form.STAIRS) || !ctx.has(def, Form.BLOCK)) {
                continue;
            }

            var tex = pickBlockTexture(def, idx);
            var path = def.nameInformation().id().getPath();

            var normal = RAAMaterials.id("block/material_block/" + path + "_stairs");
            var inner = RAAMaterials.id("block/material_block/" + path + "_stairs_inner");
            var outer = RAAMaterials.id("block/material_block/" + path + "_stairs_outer");

            addStairModels(ctx.pack(), normal, inner, outer, tex);
            addStairVariants(variant, idx, normal, inner, outer);

            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(normal, def)));
            cases++;
        }

        if (cases == 0) {
            return;
        }

        ctx.pack().addBlockState(JState.state(variant), sharedStairsId);
        select.fallback(JModelBasic.of("minecraft:block/stone"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedStairsId);
    }

    private static void buildWallFamilyForBlock(MaterialAssetContext ctx, Identifier sharedWallId) {
        var select = JItemModel.select().property(MAT_COMP);
        var state = JState.state();
        int cases = 0;

        for (int idx = 0; idx < ctx.materials().size(); idx++) {
            var def = ctx.materials().get(idx);

            if (!ctx.has(def, Form.WALL) || !ctx.has(def, Form.BLOCK)) {
                continue;
            }

            var tex = pickBlockTexture(def, idx);
            var path = def.nameInformation().id().getPath();

            var post = RAAMaterials.id("block/material_block/" + path + "_wall_post");
            var side = RAAMaterials.id("block/material_block/" + path + "_wall_side");
            var sideTall = RAAMaterials.id("block/material_block/" + path + "_wall_side_tall");
            var inventory = RAAMaterials.id("block/material_block/" + path + "_wall_inventory");

            addWallModels(ctx.pack(), post, side, sideTall, inventory, tex);
            addWallParts(state, Map.of("mat", idx), post, side, sideTall);

            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(inventory, def)));
            cases++;
        }

        if (cases == 0) {
            return;
        }

        ctx.pack().addBlockState(state, sharedWallId);
        select.fallback(JModelBasic.of("minecraft:block/stone"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedWallId);
    }

    private static void buildSlabFamilyForForm(MaterialAssetContext ctx, Identifier sharedSlabId, Form sourceForm, String sourceModelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        for (int idx = 0; idx < ctx.materials().size(); idx++) {
            var def = ctx.materials().get(idx);
            if (!ctx.has(def, Form.SLAB) || !ctx.has(def, sourceForm)) continue;

            var tex = formBlockTexture(def, sourceForm, idx);
            var path = def.nameInformation().id().getPath();
            var bottom = RAAMaterials.id(sourceModelPrefix + path + "_slab");
            var top = RAAMaterials.id(sourceModelPrefix + path + "_slab_top");
            var full = RAAMaterials.id(sourceModelPrefix + path);

            addSlabModels(ctx.pack(), bottom, top, tex);
            BlockstateTemplates.addSlab(variant, Map.of("mat", idx), JState.model(bottom), JState.model(top), JState.model(full));
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(bottom, def)));
            cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedSlabId);
        select.fallback(JModelBasic.of("minecraft:block/stone_slab"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedSlabId);
    }

    private static void buildStairsFamilyForForm(MaterialAssetContext ctx, Identifier sharedStairsId, Form sourceForm, String sourceModelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        for (int idx = 0; idx < ctx.materials().size(); idx++) {
            var def = ctx.materials().get(idx);
            if (!ctx.has(def, Form.STAIRS) || !ctx.has(def, sourceForm)) continue;

            var tex = formBlockTexture(def, sourceForm, idx);
            var path = def.nameInformation().id().getPath();
            var normal = RAAMaterials.id(sourceModelPrefix + path + "_stairs");
            var inner = RAAMaterials.id(sourceModelPrefix + path + "_stairs_inner");
            var outer = RAAMaterials.id(sourceModelPrefix + path + "_stairs_outer");

            addStairModels(ctx.pack(), normal, inner, outer, tex);
            addStairVariants(variant, idx, normal, inner, outer);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(normal, def)));
            cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedStairsId);
        select.fallback(JModelBasic.of("minecraft:block/stone_stairs"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedStairsId);
    }

    private static void buildWallFamilyForForm(MaterialAssetContext ctx, Identifier sharedWallId, Form sourceForm, String sourceModelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var state = JState.state();
        int cases = 0;

        for (int idx = 0; idx < ctx.materials().size(); idx++) {
            var def = ctx.materials().get(idx);
            if (!ctx.has(def, Form.WALL) || !ctx.has(def, sourceForm)) continue;

            var tex = formBlockTexture(def, sourceForm, idx);
            var path = def.nameInformation().id().getPath();
            var post = RAAMaterials.id(sourceModelPrefix + path + "_wall_post");
            var side = RAAMaterials.id(sourceModelPrefix + path + "_wall_side");
            var sideTall = RAAMaterials.id(sourceModelPrefix + path + "_wall_side_tall");
            var inventory = RAAMaterials.id(sourceModelPrefix + path + "_wall_inventory");

            addWallModels(ctx.pack(), post, side, sideTall, inventory, tex);
            addWallParts(state, Map.of("mat", idx), post, side, sideTall);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(inventory, def)));
            cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(state, sharedWallId);
        select.fallback(JModelBasic.of("minecraft:block/cobblestone_wall_inventory"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedWallId);
    }

    private static void buildSlabFamilyForSandstone(MaterialAssetContext ctx, Identifier sharedSlabId) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        for (int idx = 0; idx < ctx.materials().size(); idx++) {
            var def = ctx.materials().get(idx);
            if (!ctx.has(def, Form.SLAB) || !ctx.has(def, Form.SANDSTONE)) continue;

            var textures = sandstoneTextures(def, idx, false);
            var path = def.nameInformation().id().getPath();
            var bottom = RAAMaterials.id("block/material_sandstone/" + path + "_slab");
            var top = RAAMaterials.id("block/material_sandstone/" + path + "_slab_top");
            var full = RAAMaterials.id("block/material_sandstone/" + path);

            addSlabModels(ctx.pack(), bottom, top, textures.bottom(), textures.top(), textures.side());
            BlockstateTemplates.addSlab(variant, Map.of("mat", idx), JState.model(bottom), JState.model(top), JState.model(full));
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(bottom, def)));
            cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedSlabId);
        select.fallback(JModelBasic.of("minecraft:block/sandstone_slab"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedSlabId);
    }

    private static void buildStairsFamilyForSandstone(MaterialAssetContext ctx, Identifier sharedStairsId) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        for (int idx = 0; idx < ctx.materials().size(); idx++) {
            var def = ctx.materials().get(idx);
            if (!ctx.has(def, Form.STAIRS) || !ctx.has(def, Form.SANDSTONE)) continue;

            var textures = sandstoneTextures(def, idx, false);
            var path = def.nameInformation().id().getPath();
            var normal = RAAMaterials.id("block/material_sandstone/" + path + "_stairs");
            var inner = RAAMaterials.id("block/material_sandstone/" + path + "_stairs_inner");
            var outer = RAAMaterials.id("block/material_sandstone/" + path + "_stairs_outer");

            addStairModels(ctx.pack(), normal, inner, outer, textures.top(), textures.bottom(), textures.side());
            addStairVariants(variant, idx, normal, inner, outer);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(normal, def)));
            cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedStairsId);
        select.fallback(JModelBasic.of("minecraft:block/sandstone_stairs"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedStairsId);
    }

    private static void buildWallFamilyForSandstone(MaterialAssetContext ctx, Identifier sharedWallId) {
        buildWallFamilyForForm(ctx, sharedWallId, Form.SANDSTONE, "block/material_sandstone/");
    }

    private static void buildPillarFamily(
            MaterialAssetContext ctx,
            Identifier sharedBlockId,
            String modelPrefix,
            MaterialTexturePickers.TexPicker endTexture,
            MaterialTexturePickers.TexPicker sideTexture
    ) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(Form.PILLAR, (idx, def) -> {
            var modelId = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath());

            ARRPGenerationHelper.generateColumnBlockModel(
                    ctx.pack(),
                    modelId,
                    endTexture.pick(idx),
                    sideTexture.pick(idx)
            );

            variant.put(Map.of("mat", idx, "axis", "y"), JState.model(modelId));
            variant.put(Map.of("mat", idx, "axis", "x"), JState.model(modelId).x(90).y(90));
            variant.put(Map.of("mat", idx, "axis", "z"), JState.model(modelId).x(90));

            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(modelId, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.PILLAR)) {
                cases++;
            }
        }

        if (cases == 0) {
            return;
        }

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/stone"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildCrystalClusterFamily(
            MaterialAssetContext ctx,
            Identifier sharedBlockId,
            Form form,
            String modelPrefix,
            MaterialTexturePickers.TexPicker texture
    ) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(form, (idx, def) -> {
            var modelId = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath());

            ctx.pack().addModel(JModel.model("raa_materials:block/raa_cross")
                    .textures(JModel.textures().var("cross", blockTexture(texture.pick(idx)))), modelId);

            addFacingVariants(variant, idx, modelId);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(modelId, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, form)) {
                cases++;
            }
        }

        if (cases == 0) {
            return;
        }

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/amethyst_cluster"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildCrystalBudFamily(
            MaterialAssetContext ctx,
            Identifier sharedBlockId,
            Form form,
            String modelPrefix,
            MaterialTexturePickers.TexPicker texture
    ) {
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(form, (idx, def) -> {
            var modelId = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath());

            ctx.pack().addModel(JModel.model("raa_materials:block/raa_cross")
                    .textures(JModel.textures().var("cross", blockTexture(texture.pick(idx)))), modelId);

            addFacingVariants(variant, idx, modelId);
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, form)) {
                cases++;
            }
        }

        if (cases == 0) {
            return;
        }

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
    }

    private static void buildRodBlockFamily(
            MaterialAssetContext ctx,
            Identifier sharedBlockId,
            String modelPrefix,
            MaterialTexturePickers.TexPicker texture
    ) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(Form.ROD_BLOCK, (idx, def) -> {
            var modelId = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath());

            ctx.pack().addModel(JModel.model("raa_materials:block/raa_end_rod")
                    .textures(JModel.textures().var("end_rod", blockTexture(texture.pick(idx)))), modelId);

            addFacingVariants(variant, idx, modelId);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(modelId, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.ROD_BLOCK)) {
                cases++;
            }
        }

        if (cases == 0) {
            return;
        }

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/end_rod"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    // dir/thickness pairs matching DripstoneThickness serialized names
    private static final List<String[]> SPIKE_VARIANTS = List.of(
            new String[]{"up",   "tip_merge"},
            new String[]{"up",   "tip"},
            new String[]{"up",   "frustum"},
            new String[]{"up",   "middle"},
            new String[]{"up",   "base"},
            new String[]{"down", "tip_merge"},
            new String[]{"down", "tip"},
            new String[]{"down", "frustum"},
            new String[]{"down", "middle"},
            new String[]{"down", "base"}
    );

    private static void buildSpikeFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();

        ctx.forEachMaterialWith(Form.SPIKE, (idx, def) -> {
            Identifier texPrefix = MaterialAssets.texture(Form.SPIKE, def)
                    .orElse(RAAMaterials.id("block/stone/spikes/1/spike"));
            String matPath = def.nameInformation().id().getPath();

            for (String[] sv : SPIKE_VARIANTS) {
                String dir = sv[0];
                String thickness = sv[1];
                Identifier texId = Identifier.fromNamespaceAndPath(
                        texPrefix.getNamespace(),
                        texPrefix.getPath() + "_" + dir + "_" + thickness
                );
                Identifier templateId = RAAMaterials.id("block/raa_spike_" + dir + "_" + thickness);
                Identifier modelId = RAAMaterials.id(modelPrefix + matPath + "_" + dir + "_" + thickness);

                ctx.pack().addModel(
                        JModel.model(templateId.toString())
                              .textures(JModel.textures().var("spike", blockTexture(texId))),
                        modelId
                );
                variant.put(Map.of("mat", idx, "vertical_direction", dir, "thickness", thickness), JState.model(modelId));
            }

            Identifier itemModel = RAAMaterials.id(modelPrefix + matPath + "_up_tip");
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(itemModel, def)));
        });

        int cases = 0;
        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.SPIKE)) cases++;
        }
        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/pointed_dripstone_up_tip"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildDoorFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(Form.DOOR, (idx, def) -> {
            var bottomTexture = pickDecorTexture(Form.DOOR, def, idx);
            var topTexture = textures(def).decorTextures().doorTop().orElseGet(() -> pairedDoorTopTexture(bottomTexture));
            var itemTexture = textures(def).decorTextures().doorItem().orElse(bottomTexture);
            var path = def.nameInformation().id().getPath();
            var itemModel = RAAMaterials.id(modelPrefix + path + "_item");
            var bottomLeft = RAAMaterials.id(modelPrefix + path + "_bottom_left");
            var bottomLeftOpen = RAAMaterials.id(modelPrefix + path + "_bottom_left_open");
            var bottomRight = RAAMaterials.id(modelPrefix + path + "_bottom_right");
            var bottomRightOpen = RAAMaterials.id(modelPrefix + path + "_bottom_right_open");
            var topLeft = RAAMaterials.id(modelPrefix + path + "_top_left");
            var topLeftOpen = RAAMaterials.id(modelPrefix + path + "_top_left_open");
            var topRight = RAAMaterials.id(modelPrefix + path + "_top_right");
            var topRightOpen = RAAMaterials.id(modelPrefix + path + "_top_right_open");

            addGeneratedItemModel(ctx.pack(), itemModel, itemTexture);
            addDoorModels(ctx.pack(), bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen, bottomTexture, topTexture);
            addDoorVariants(variant, idx, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(itemModel, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.DOOR)) cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/oak_door_bottom_left"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildTrapdoorFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(Form.TRAPDOOR, (idx, def) -> {
            var tex = pickDecorTexture(Form.TRAPDOOR, def, idx);
            var path = def.nameInformation().id().getPath();
            var bottom = RAAMaterials.id(modelPrefix + path + "_bottom");
            var top = RAAMaterials.id(modelPrefix + path + "_top");
            var open = RAAMaterials.id(modelPrefix + path + "_open");

            addTrapdoorModels(ctx.pack(), bottom, top, open, tex);
            addTrapdoorVariants(variant, idx, bottom, top, open);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(bottom, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.TRAPDOOR)) cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/oak_trapdoor_bottom"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildFenceFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var state = JState.state();
        int cases = 0;

        ctx.forEachMaterialWith(Form.FENCE, (idx, def) -> {
            var tex = pickDecorTexture(Form.FENCE, def, idx);
            var path = def.nameInformation().id().getPath();
            var post = RAAMaterials.id(modelPrefix + path + "_post");
            var side = RAAMaterials.id(modelPrefix + path + "_side");
            var inventory = RAAMaterials.id(modelPrefix + path + "_inventory");

            addFenceModels(ctx.pack(), post, side, inventory, tex);
            addFenceParts(state, Map.of("mat", idx), post, side);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(inventory, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.FENCE)) cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(state, sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/oak_fence_inventory"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildFenceGateFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(Form.FENCE_GATE, (idx, def) -> {
            var tex = pickDecorTexture(Form.FENCE_GATE, def, idx);
            var path = def.nameInformation().id().getPath();
            var gate = RAAMaterials.id(modelPrefix + path);
            var gateOpen = RAAMaterials.id(modelPrefix + path + "_open");
            var gateWall = RAAMaterials.id(modelPrefix + path + "_wall");
            var gateWallOpen = RAAMaterials.id(modelPrefix + path + "_wall_open");

            addFenceGateModels(ctx.pack(), gate, gateOpen, gateWall, gateWallOpen, tex);
            addFenceGateVariants(variant, idx, gate, gateOpen, gateWall, gateWallOpen);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(gate, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.FENCE_GATE)) cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/oak_fence_gate"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildChainFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(Form.CHAIN, (idx, def) -> {
            var modelId = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath());
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_chain")
                    .textures(JModel.textures().var("texture", blockTexture(pickDecorTexture(Form.CHAIN, def, idx)))), modelId);

            variant.put(Map.of("mat", idx, "axis", "y"), JState.model(modelId));
            variant.put(Map.of("mat", idx, "axis", "x"), JState.model(modelId).x(90).y(90));
            variant.put(Map.of("mat", idx, "axis", "z"), JState.model(modelId).x(90));
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(modelId, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.CHAIN)) cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/iron_chain"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildLanternFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(Form.LANTERN, (idx, def) -> {
            var tex = blockTexture(pickDecorTexture(Form.LANTERN, def, idx));
            var base = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath());
            var hanging = RAAMaterials.id(modelPrefix + def.nameInformation().id().getPath() + "_hanging");

            ctx.pack().addModel(JModel.model("raa_materials:block/raa_lantern")
                    .textures(JModel.textures().var("lantern", tex)), base);
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_hanging_lantern")
                    .textures(JModel.textures().var("lantern", tex)), hanging);

            variant.put(Map.of("mat", idx, "hanging", "false"), JState.model(base));
            variant.put(Map.of("mat", idx, "hanging", "true"), JState.model(hanging));
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(base, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.LANTERN)) cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/lantern"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildPaneFamily(
            MaterialAssetContext ctx,
            Identifier sharedBlockId,
            String modelPrefix,
            MaterialTexturePickers.TexPicker texture
    ) {
        var select = JItemModel.select().property(MAT_COMP);
        var state = JState.state();
        int cases = 0;

        ctx.forEachMaterialWith(Form.PANE, (idx, def) -> {
            var tex = blockTexture(texture.pick(idx));
            var path = def.nameInformation().id().getPath();

            var post = RAAMaterials.id(modelPrefix + path + "_post");
            var side = RAAMaterials.id(modelPrefix + path + "_side");
            var sideAlt = RAAMaterials.id(modelPrefix + path + "_side_alt");
            var noSide = RAAMaterials.id(modelPrefix + path + "_noside");
            var noSideAlt = RAAMaterials.id(modelPrefix + path + "_noside_alt");

            ctx.pack().addModel(JModel.model("raa_materials:block/raa_glass_pane_post")
                    .textures(JModel.textures().var("pane", tex)), post);
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_glass_pane_side")
                    .textures(JModel.textures().var("pane", tex)), side);
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_glass_pane_side_alt")
                    .textures(JModel.textures().var("pane", tex)), sideAlt);
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_glass_pane_noside")
                    .textures(JModel.textures().var("pane", tex)), noSide);
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_glass_pane_noside_alt")
                    .textures(JModel.textures().var("pane", tex)), noSideAlt);

            state.add(JState.multipart(JState.model(post)).when(Map.of("mat", idx)));
            state.add(JState.multipart(JState.model(side)).when(Map.of("mat", idx, "north", "true")));
            state.add(JState.multipart(JState.model(side).y(90)).when(Map.of("mat", idx, "east", "true")));
            state.add(JState.multipart(JState.model(sideAlt)).when(Map.of("mat", idx, "south", "true")));
            state.add(JState.multipart(JState.model(sideAlt).y(90)).when(Map.of("mat", idx, "west", "true")));
            state.add(JState.multipart(JState.model(noSide)).when(Map.of("mat", idx, "north", "false")));
            state.add(JState.multipart(JState.model(noSideAlt)).when(Map.of("mat", idx, "east", "false")));
            state.add(JState.multipart(JState.model(noSideAlt).y(90)).when(Map.of("mat", idx, "south", "false")));
            state.add(JState.multipart(JState.model(noSide).y(270)).when(Map.of("mat", idx, "west", "false")));

            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(post, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.PANE)) {
                cases++;
            }
        }

        if (cases == 0) {
            return;
        }

        ctx.pack().addBlockState(state, sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/glass_pane_post"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildBarsFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var state = JState.state();
        int cases = 0;

        ctx.forEachMaterialWith(Form.BARS, (idx, def) -> {
            var tex = blockTexture(pickFormTexture(Form.BARS, def, idx));
            var path = def.nameInformation().id().getPath();
            var post = RAAMaterials.id(modelPrefix + path + "_post");
            var side = RAAMaterials.id(modelPrefix + path + "_side");
            var sideAlt = RAAMaterials.id(modelPrefix + path + "_side_alt");
            var cap = RAAMaterials.id(modelPrefix + path + "_cap");
            var capAlt = RAAMaterials.id(modelPrefix + path + "_cap_alt");

            ctx.pack().addModel(JModel.model("raa_materials:block/raa_iron_bars_post").textures(JModel.textures().var("bars", tex)), post);
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_iron_bars_side").textures(JModel.textures().var("bars", tex)), side);
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_iron_bars_side_alt").textures(JModel.textures().var("bars", tex)), sideAlt);
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_iron_bars_cap").textures(JModel.textures().var("bars", tex)), cap);
            ctx.pack().addModel(JModel.model("raa_materials:block/raa_iron_bars_cap_alt").textures(JModel.textures().var("bars", tex)), capAlt);

            state.add(JState.multipart(JState.model(post)).when(Map.of("mat", idx)));
            state.add(JState.multipart(JState.model(side)).when(Map.of("mat", idx, "north", "true")));
            state.add(JState.multipart(JState.model(side).y(90)).when(Map.of("mat", idx, "east", "true")));
            state.add(JState.multipart(JState.model(sideAlt)).when(Map.of("mat", idx, "south", "true")));
            state.add(JState.multipart(JState.model(sideAlt).y(90)).when(Map.of("mat", idx, "west", "true")));
            state.add(JState.multipart(JState.model(cap)).when(Map.of("mat", idx, "north", "false")));
            state.add(JState.multipart(JState.model(capAlt)).when(Map.of("mat", idx, "east", "false")));
            state.add(JState.multipart(JState.model(capAlt).y(90)).when(Map.of("mat", idx, "south", "false")));
            state.add(JState.multipart(JState.model(cap).y(270)).when(Map.of("mat", idx, "west", "false")));

            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(post, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.BARS)) cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(state, sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/iron_bars_post"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildGrateFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        buildBlockFamily(ctx, new BlockFamilySpec(
                List.of(Form.GRATE),
                sharedBlockId,
                modelPrefix,
                idx -> pickFormTexture(Form.GRATE, ctx.materials().get(idx), idx)
        ));
    }

    private static void buildButtonFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(Form.BUTTON, (idx, def) -> {
            var path = def.nameInformation().id().getPath();
            var button = RAAMaterials.id(modelPrefix + path);
            var pressed = RAAMaterials.id(modelPrefix + path + "_pressed");
            var inventory = RAAMaterials.id(modelPrefix + path + "_inventory");
            addButtonModels(ctx.pack(), button, pressed, inventory, pickBlockTexture(def, idx));
            addButtonVariants(variant, idx, button, pressed);
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(inventory, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.BUTTON)) cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of("minecraft:block/stone_button_inventory"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void buildPressurePlateFamily(MaterialAssetContext ctx, Identifier sharedBlockId, String modelPrefix, boolean weighted) {
        var select = JItemModel.select().property(MAT_COMP);
        var variant = JState.variant();
        int cases = 0;

        ctx.forEachMaterialWith(Form.PRESSURE_PLATE, (idx, def) -> {
            var path = def.nameInformation().id().getPath();
            var up = RAAMaterials.id(modelPrefix + path);
            var down = RAAMaterials.id(modelPrefix + path + "_down");
            addPressurePlateModels(ctx.pack(), up, down, pickBlockTexture(def, idx));
            if (weighted) {
                variant.put(Map.of("mat", idx, "power", 0), JState.model(up));
                for (int power = 1; power <= 15; power++) {
                    variant.put(Map.of("mat", idx, "power", power), JState.model(down));
                }
            } else {
                variant.put(Map.of("mat", idx, "powered", "false"), JState.model(up));
                variant.put(Map.of("mat", idx, "powered", "true"), JState.model(down));
            }
            select.addCase(JSelectCase.of(def.nameInformation().id().toString(), tintedModel(up, def)));
        });

        for (MaterialDef def : ctx.materials()) {
            if (ctx.has(def, Form.PRESSURE_PLATE)) cases++;
        }

        if (cases == 0) return;

        ctx.pack().addBlockState(JState.state(variant), sharedBlockId);
        select.fallback(JModelBasic.of(weighted ? "minecraft:block/heavy_weighted_pressure_plate" : "minecraft:block/stone_pressure_plate"));
        ctx.pack().addItemModelInfo(new JItemInfo().model(select), sharedBlockId);
    }

    private static void addOreBlockModel(RuntimeResourcePack rp, Identifier modelId, Identifier baseTexture, Identifier overlayTexture) {
        var base = blockTexture(baseTexture);
        var overlay = blockTexture(overlayTexture);

        var model = JModel.model("minecraft:block/block")
                .textures(JModel.textures()
                        .var("particle", base)
                        .var("base", base)
                        .var("overlay", overlay))
                .element(JModel.element()
                        .bounds(0, 0, 0, 16, 16, 16)
                        .faces(JModel.faces()
                                .north(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.NORTH))
                                .east(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.EAST))
                                .south(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.SOUTH))
                                .west(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.WEST))
                                .up(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.UP))
                                .down(JModel.face("base").uv(0, 0, 16, 16).cullface(Direction.DOWN))))
                .element(JModel.element()
                        .bounds(-0.01f, -0.01f, -0.01f, 16.01f, 16.01f, 16.01f)
                        .faces(JModel.faces()
                                .north(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.NORTH).tintIndex(0))
                                .east(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.EAST).tintIndex(0))
                                .south(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.SOUTH).tintIndex(0))
                                .west(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.WEST).tintIndex(0))
                                .up(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.UP).tintIndex(0))
                                .down(JModel.face("overlay").uv(0, 0, 16, 16).cullface(Direction.DOWN).tintIndex(0))));

        rp.addModel(model, modelId);
    }

    private static void addSlabModels(RuntimeResourcePack rp, Identifier bottom, Identifier top, Identifier texture) {
        addSlabModels(rp, bottom, top, texture, texture, texture);
    }

    private static void addSlabModels(RuntimeResourcePack rp, Identifier bottom, Identifier topModel, Identifier bottomTexture, Identifier topTexture, Identifier sideTexture) {
        rp.addModel(JModel.model("raa_materials:block/raa_slab")
                .textures(JModel.textures()
                        .var("bottom", blockTexture(bottomTexture))
                        .var("top", blockTexture(topTexture))
                        .var("side", blockTexture(sideTexture))), bottom);

        rp.addModel(JModel.model("raa_materials:block/raa_slab_top")
                .textures(JModel.textures()
                        .var("bottom", blockTexture(bottomTexture))
                        .var("top", blockTexture(topTexture))
                        .var("side", blockTexture(sideTexture))), topModel);
    }

    private static void addStairModels(RuntimeResourcePack rp, Identifier normal, Identifier inner, Identifier outer, Identifier texture) {
        addStairModels(rp, normal, inner, outer, texture, texture, texture);
    }

    private static void addStairModels(RuntimeResourcePack rp, Identifier normal, Identifier inner, Identifier outer,
                                       Identifier topTexture, Identifier bottomTexture, Identifier sideTexture) {
        var textures = JModel.textures()
                .var("bottom", blockTexture(bottomTexture))
                .var("top", blockTexture(topTexture))
                .var("side", blockTexture(sideTexture));

        rp.addModel(JModel.model("raa_materials:block/raa_stairs").textures(textures), normal);
        rp.addModel(JModel.model("raa_materials:block/raa_inner_stairs").textures(textures), inner);
        rp.addModel(JModel.model("raa_materials:block/raa_outer_stairs").textures(textures), outer);
    }

    private static void addWallModels(RuntimeResourcePack rp, Identifier post, Identifier side, Identifier sideTall, Identifier inventory, Identifier texture) {
        var tex = blockTexture(texture);

        rp.addModel(JModel.model("raa_materials:block/raa_wall_post")
                .textures(JModel.textures().var("wall", tex)), post);

        rp.addModel(JModel.model("raa_materials:block/raa_wall_side")
                .textures(JModel.textures().var("wall", tex)), side);

        rp.addModel(JModel.model("raa_materials:block/raa_wall_side_tall")
                .textures(JModel.textures().var("wall", tex)), sideTall);

        rp.addModel(JModel.model("raa_materials:block/raa_wall_inventory")
                .textures(JModel.textures().var("wall", tex)), inventory);
    }

    private static void addDoorModels(RuntimeResourcePack rp, Identifier bottomLeft, Identifier bottomLeftOpen, Identifier bottomRight, Identifier bottomRightOpen,
                                      Identifier topLeft, Identifier topLeftOpen, Identifier topRight, Identifier topRightOpen,
                                      Identifier bottomTexture, Identifier topTexture) {
        var textures = JModel.textures()
                .var("bottom", blockTexture(bottomTexture))
                .var("top", blockTexture(topTexture));

        rp.addModel(JModel.model("raa_materials:block/raa_door_bottom_left").textures(textures), bottomLeft);
        rp.addModel(JModel.model("raa_materials:block/raa_door_bottom_left_open").textures(textures), bottomLeftOpen);
        rp.addModel(JModel.model("raa_materials:block/raa_door_bottom_right").textures(textures), bottomRight);
        rp.addModel(JModel.model("raa_materials:block/raa_door_bottom_right_open").textures(textures), bottomRightOpen);
        rp.addModel(JModel.model("raa_materials:block/raa_door_top_left").textures(textures), topLeft);
        rp.addModel(JModel.model("raa_materials:block/raa_door_top_left_open").textures(textures), topLeftOpen);
        rp.addModel(JModel.model("raa_materials:block/raa_door_top_right").textures(textures), topRight);
        rp.addModel(JModel.model("raa_materials:block/raa_door_top_right_open").textures(textures), topRightOpen);
    }

    private static void addTrapdoorModels(RuntimeResourcePack rp, Identifier bottom, Identifier top, Identifier open, Identifier texture) {
        var textures = JModel.textures().var("texture", blockTexture(texture));
        rp.addModel(JModel.model("raa_materials:block/raa_trapdoor_bottom").textures(textures), bottom);
        rp.addModel(JModel.model("raa_materials:block/raa_trapdoor_top").textures(textures), top);
        rp.addModel(JModel.model("raa_materials:block/raa_trapdoor_open").textures(textures), open);
    }

    private static void addFenceModels(RuntimeResourcePack rp, Identifier post, Identifier side, Identifier inventory, Identifier texture) {
        var textures = JModel.textures().var("texture", blockTexture(texture));
        rp.addModel(JModel.model("raa_materials:block/raa_fence_post").textures(textures), post);
        rp.addModel(JModel.model("raa_materials:block/raa_fence_side").textures(textures), side);
        rp.addModel(JModel.model("raa_materials:block/raa_fence_inventory").textures(textures), inventory);
    }

    private static void addFenceGateModels(RuntimeResourcePack rp, Identifier gate, Identifier gateOpen, Identifier gateWall, Identifier gateWallOpen, Identifier texture) {
        var textures = JModel.textures().var("texture", blockTexture(texture));
        rp.addModel(JModel.model("raa_materials:block/raa_fence_gate").textures(textures), gate);
        rp.addModel(JModel.model("raa_materials:block/raa_fence_gate_open").textures(textures), gateOpen);
        rp.addModel(JModel.model("raa_materials:block/raa_fence_gate_wall").textures(textures), gateWall);
        rp.addModel(JModel.model("raa_materials:block/raa_fence_gate_wall_open").textures(textures), gateWallOpen);
    }

    private static void addGeneratedItemModel(RuntimeResourcePack rp, Identifier modelId, Identifier layer0) {
        rp.addModel(JModel.model("minecraft:item/generated")
                .textures(JModel.textures().var("layer0", itemTexture(layer0))), modelId);
    }

    private static void addButtonModels(RuntimeResourcePack rp, Identifier button, Identifier pressed, Identifier inventory, Identifier texture) {
        var textures = JModel.textures().var("texture", blockTexture(texture));
        rp.addModel(JModel.model("raa_materials:block/raa_button").textures(textures), button);
        rp.addModel(JModel.model("raa_materials:block/raa_button_pressed").textures(textures), pressed);
        rp.addModel(JModel.model("raa_materials:block/raa_button_inventory").textures(textures), inventory);
    }

    private static void addPressurePlateModels(RuntimeResourcePack rp, Identifier up, Identifier down, Identifier texture) {
        var textures = JModel.textures().var("texture", blockTexture(texture));
        rp.addModel(JModel.model("raa_materials:block/raa_pressure_plate_up").textures(textures), up);
        rp.addModel(JModel.model("raa_materials:block/raa_pressure_plate_down").textures(textures), down);
    }

    private static void addButtonVariants(net.vampirestudios.arrp.json.blockstate.JVariant variant, int mat, Identifier button, Identifier pressed) {
        for (String powered : List.of("false", "true")) {
            Identifier model = Boolean.parseBoolean(powered) ? pressed : button;

            variant.put(Map.of("mat", mat, "face", "floor", "facing", "north", "powered", powered), JState.model(model).uvlock().y(0));
            variant.put(Map.of("mat", mat, "face", "floor", "facing", "east", "powered", powered), JState.model(model).uvlock().y(90));
            variant.put(Map.of("mat", mat, "face", "floor", "facing", "south", "powered", powered), JState.model(model).uvlock().y(180));
            variant.put(Map.of("mat", mat, "face", "floor", "facing", "west", "powered", powered), JState.model(model).uvlock().y(270));

            variant.put(Map.of("mat", mat, "face", "wall", "facing", "north", "powered", powered), JState.model(model).uvlock().x(90).y(0));
            variant.put(Map.of("mat", mat, "face", "wall", "facing", "east", "powered", powered), JState.model(model).uvlock().x(90).y(90));
            variant.put(Map.of("mat", mat, "face", "wall", "facing", "south", "powered", powered), JState.model(model).uvlock().x(90).y(180));
            variant.put(Map.of("mat", mat, "face", "wall", "facing", "west", "powered", powered), JState.model(model).uvlock().x(90).y(270));

            variant.put(Map.of("mat", mat, "face", "ceiling", "facing", "north", "powered", powered), JState.model(model).uvlock().x(180).y(180));
            variant.put(Map.of("mat", mat, "face", "ceiling", "facing", "east", "powered", powered), JState.model(model).uvlock().x(180).y(270));
            variant.put(Map.of("mat", mat, "face", "ceiling", "facing", "south", "powered", powered), JState.model(model).uvlock().x(180).y(0));
            variant.put(Map.of("mat", mat, "face", "ceiling", "facing", "west", "powered", powered), JState.model(model).uvlock().x(180).y(90));
        }
    }

    private static void addWallParts(JState state, Map<String, ?> base, Identifier post, Identifier side, Identifier sideTall) {
        state.add(JState.multipart(JState.model(post)).when(base));
        state.add(JState.multipart(JState.model(side).uvlock().y(0)).when(BlockstateTemplates.plus(base, "north", "low")));
        state.add(JState.multipart(JState.model(side).uvlock().y(90)).when(BlockstateTemplates.plus(base, "east", "low")));
        state.add(JState.multipart(JState.model(side).uvlock().y(180)).when(BlockstateTemplates.plus(base, "south", "low")));
        state.add(JState.multipart(JState.model(side).uvlock().y(270)).when(BlockstateTemplates.plus(base, "west", "low")));
        state.add(JState.multipart(JState.model(sideTall).uvlock().y(0)).when(BlockstateTemplates.plus(base, "north", "tall")));
        state.add(JState.multipart(JState.model(sideTall).uvlock().y(90)).when(BlockstateTemplates.plus(base, "east", "tall")));
        state.add(JState.multipart(JState.model(sideTall).uvlock().y(180)).when(BlockstateTemplates.plus(base, "south", "tall")));
        state.add(JState.multipart(JState.model(sideTall).uvlock().y(270)).when(BlockstateTemplates.plus(base, "west", "tall")));
    }

    // Stair y-rotation per facing: east=0, south=90, west=180, north=270 (matches vanilla oak_stairs.json)
    private static void addStairVariants(net.vampirestudios.arrp.json.blockstate.JVariant variant, int mat,
                                          Identifier normal, Identifier inner, Identifier outer) {
        record S(String facing, String half, String shape, Identifier model, int y, int x) {}

        String[] facings = {"east", "south", "west", "north"};
        int[] yForFacing = {0, 90, 180, 270};

        for (int fi = 0; fi < facings.length; fi++) {
            String f = facings[fi];
            int y = yForFacing[fi];
            int yLeft  = (y + 270) % 360;
            int yRight = (y + 90) % 360;

            for (var e : List.of(
                    new S(f, "bottom", "straight",    normal, y,      0),
                    new S(f, "bottom", "inner_right", inner,  y,      0),
                    new S(f, "bottom", "outer_right", outer,  y,      0),
                    new S(f, "bottom", "inner_left",  inner,  yLeft,  0),
                    new S(f, "bottom", "outer_left",  outer,  yLeft,  0),
                    new S(f, "top",    "straight",    normal, y,      180),
                    new S(f, "top",    "inner_left",  inner,  y,      180),
                    new S(f, "top",    "outer_left",  outer,  y,      180),
                    new S(f, "top",    "inner_right", inner,  yRight, 180),
                    new S(f, "top",    "outer_right", outer,  yRight, 180)
            )) {
                var model = JState.model(e.model());
                if (e.x() != 0) model.x(e.x());
                if (e.y() != 0) model.y(e.y());
                if (e.x() != 0 || e.y() != 0) model.uvlock();
                variant.put(Map.of("mat", mat, "facing", e.facing(), "half", e.half(), "shape", e.shape()), model);
            }
        }
    }

    private static void addFenceParts(JState state, Map<String, ?> base, Identifier post, Identifier side) {
        state.add(JState.multipart(JState.model(post)).when(base));
        state.add(JState.multipart(JState.model(side).uvlock()).when(BlockstateTemplates.plus(base, "north", "true")));
        state.add(JState.multipart(JState.model(side).uvlock().y(90)).when(BlockstateTemplates.plus(base, "east", "true")));
        state.add(JState.multipart(JState.model(side).uvlock().y(180)).when(BlockstateTemplates.plus(base, "south", "true")));
        state.add(JState.multipart(JState.model(side).uvlock().y(270)).when(BlockstateTemplates.plus(base, "west", "true")));
    }

    /** Standard Minecraft facing to Y rotation: south=0, west=90, north=180, east=270. */
    private static int facingToY(String facing) {
        return switch (facing) {
            case "west" -> 90;
            case "north" -> 180;
            case "east" -> 270;
            default -> 0; // south
        };
    }

    private static void addFenceGateVariants(net.vampirestudios.arrp.json.blockstate.JVariant variant, int mat,
                                             Identifier gate, Identifier gateOpen, Identifier gateWall, Identifier gateWallOpen) {
        for (String facing : List.of("south", "west", "north", "east")) {
            int y = facingToY(facing);
            putFenceGate(variant, mat, facing, false, false, JState.model(gate).uvlock().y(y));
            putFenceGate(variant, mat, facing, false, true, JState.model(gateOpen).uvlock().y(y));
            putFenceGate(variant, mat, facing, true, false, JState.model(gateWall).uvlock().y(y));
            putFenceGate(variant, mat, facing, true, true, JState.model(gateWallOpen).uvlock().y(y));
        }
    }

    private static void putFenceGate(net.vampirestudios.arrp.json.blockstate.JVariant variant, int mat, String facing, boolean inWall, boolean open, JBlockModel model) {
        variant.put(Map.of("mat", mat, "facing", facing, "in_wall", Boolean.toString(inWall), "open", Boolean.toString(open)), model);
    }

    private static void addTrapdoorVariants(net.vampirestudios.arrp.json.blockstate.JVariant variant, int mat, Identifier bottom, Identifier top, Identifier openModel) {
        for (String facing : List.of("north", "east", "south", "west")) {
            int y = facingToY(facing);
            variant.put(Map.of("mat", mat, "facing", facing, "half", "bottom", "open", "false"), JState.model(bottom));
            variant.put(Map.of("mat", mat, "facing", facing, "half", "top", "open", "false"), JState.model(top));
            variant.put(Map.of("mat", mat, "facing", facing, "half", "bottom", "open", "true"), JState.model(openModel).y(y));
            variant.put(Map.of("mat", mat, "facing", facing, "half", "top", "open", "true"), JState.model(openModel).y(y));
        }
    }

    private static void addDoorVariants(net.vampirestudios.arrp.json.blockstate.JVariant variant, int mat,
                                        Identifier bottomLeft, Identifier bottomLeftOpen, Identifier bottomRight, Identifier bottomRightOpen,
                                        Identifier topLeft, Identifier topLeftOpen, Identifier topRight, Identifier topRightOpen) {
        putDoorFacing(variant, mat, "east", 0, 90, 0, 270, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
        putDoorFacing(variant, mat, "south", 90, 180, 90, 0, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
        putDoorFacing(variant, mat, "west", 180, 270, 180, 90, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
        putDoorFacing(variant, mat, "north", 270, 0, 270, 180, bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen);
    }

    private static void putDoorFacing(net.vampirestudios.arrp.json.blockstate.JVariant variant, int mat, String facing,
                                      int closedY, int leftOpenY, int rightClosedY, int rightOpenY,
                                      Identifier bottomLeft, Identifier bottomLeftOpen, Identifier bottomRight, Identifier bottomRightOpen,
                                      Identifier topLeft, Identifier topLeftOpen, Identifier topRight, Identifier topRightOpen) {
        putDoor(variant, mat, facing, "lower", "left", "false", JState.model(bottomLeft).y(closedY));
        putDoor(variant, mat, facing, "lower", "left", "true", JState.model(bottomLeftOpen).y(leftOpenY));
        putDoor(variant, mat, facing, "lower", "right", "false", JState.model(bottomRight).y(rightClosedY));
        putDoor(variant, mat, facing, "lower", "right", "true", JState.model(bottomRightOpen).y(rightOpenY));
        putDoor(variant, mat, facing, "upper", "left", "false", JState.model(topLeft).y(closedY));
        putDoor(variant, mat, facing, "upper", "left", "true", JState.model(topLeftOpen).y(leftOpenY));
        putDoor(variant, mat, facing, "upper", "right", "false", JState.model(topRight).y(rightClosedY));
        putDoor(variant, mat, facing, "upper", "right", "true", JState.model(topRightOpen).y(rightOpenY));
    }

    private static void putDoor(net.vampirestudios.arrp.json.blockstate.JVariant variant, int mat, String facing, String half, String hinge, String open, JBlockModel model) {
        variant.put(Map.of("mat", mat, "facing", facing, "half", half, "hinge", hinge, "open", open), model);
    }

    private static void addFacingVariants(net.vampirestudios.arrp.json.blockstate.JVariant variant, int mat, Identifier model) {
        variant.put(Map.of("mat", mat, "facing", "up"), JState.model(model));
        variant.put(Map.of("mat", mat, "facing", "down"), JState.model(model).x(180));
        variant.put(Map.of("mat", mat, "facing", "north"), JState.model(model).x(90));
        variant.put(Map.of("mat", mat, "facing", "south"), JState.model(model).x(90).y(180));
        variant.put(Map.of("mat", mat, "facing", "west"), JState.model(model).x(90).y(270));
        variant.put(Map.of("mat", mat, "facing", "east"), JState.model(model).x(90).y(90));
    }

    private static JModelBasic tintedModel(Identifier modelId, MaterialDef def) {
        return (JModelBasic) JModelBasic.model(modelId.toString())
                .tint(JTint.constant(MaterialsAssets.opaqueColor(def.primaryColor())));
    }

    private static Identifier pickFormTexture(Form form, MaterialDef def, int idx) {
        return MaterialAssets.texture(form, def).orElseGet(() -> pickBlockTexture(def, idx));
    }

    private static Identifier formBlockTexture(MaterialDef def, Form form, int idx) {
        var assets = textures(def);
        return switch (form) {
            case BRICKS -> assets.blockTextures().bricks().orElseGet(() -> pickBlockTexture(def, idx));
            case POLISHED -> assets.blockTextures().polished().orElseGet(() -> pickBlockTexture(def, idx));
            case SANDSTONE -> sandstoneTextures(def, idx, false).side();
            case CUT -> sandstoneTextures(def, idx, true).side();
            default -> pickBlockTexture(def, idx);
        };
    }

    private static SandstoneFaces sandstoneTextures(MaterialDef def, int idx, boolean cut) {
        var sandstone = textures(def).sandstoneTextures();
        var fallback = RAAMaterials.id("storage_blocks/sand_" + oneIndexed(idx, AssetsThemeConfig.SAND_BLOCK_COUNT));
        var top = sandstone.sandstoneTop().orElse(fallback);
        var side = cut ? sandstone.cutSandstoneSide().orElse(fallback) : sandstone.sandstoneSide().orElse(fallback);
        var bottom = cut ? top : sandstone.sandstoneBottom().orElse(fallback);
        return new SandstoneFaces(top, side, bottom);
    }

    private record SandstoneFaces(Identifier top, Identifier side, Identifier bottom) {
    }

    private static Identifier pickDecorTexture(Form form, MaterialDef def, int idx) {
        var decor = textures(def).decorTextures();
        Optional<Identifier> texture = switch (form) {
            case CHAIN -> decor.chain();
            case LANTERN -> decor.lantern();
            case DOOR -> decor.doorBottom();
            case TRAPDOOR -> decor.trapdoor();
            case FENCE -> decor.fence();
            case FENCE_GATE -> decor.fenceGate();
            default -> Optional.empty();
        };
        return texture.orElseGet(() -> pickFormTexture(form, def, idx));
    }

    private static Identifier pairedDoorTopTexture(Identifier bottomTexture) {
        String path = bottomTexture.getPath();
        if (path.endsWith("_bottom")) {
            return Identifier.fromNamespaceAndPath(bottomTexture.getNamespace(), path.substring(0, path.length() - "_bottom".length()) + "_top");
        }
        if (path.endsWith("bottom")) {
            return Identifier.fromNamespaceAndPath(bottomTexture.getNamespace(), path.substring(0, path.length() - "bottom".length()) + "top");
        }
        return bottomTexture;
    }

    private static void addFallbackItemModel(RuntimeResourcePack rp, Identifier itemId) {
        rp.addItemModelInfo(
                new JItemInfo().model(JModelBasic.of(itemId.withPrefix("item/").toString())),
                itemId
        );
    }

    private static String blockTexture(Identifier id) {
        String path = stripPng(id.getPath());

        if (!path.startsWith("block/")) {
            path = "block/" + path;
        }

        return Identifier.fromNamespaceAndPath(id.getNamespace(), path).toString();
    }

    private static String itemTexture(Identifier id) {
        String path = stripPng(id.getPath());

        if (!path.startsWith("item/") && !path.startsWith("block/")) {
            path = "item/" + path;
        }

        return Identifier.fromNamespaceAndPath(id.getNamespace(), path).toString();
    }

    private static String stripPng(String value) {
        return value.endsWith(".png")
                ? value.substring(0, value.length() - 4)
                : value;
    }

    private record BlockFamilySpec(
            List<Form> forms,
            Identifier sharedId,
            String modelPrefix,
            MaterialTexturePickers.TexPicker texture
    ) {
    }

    private record ItemFamilySpec(
            List<Form> forms,
            Identifier itemId,
            String modelPrefix,
            MaterialTexturePickers.TexPicker texture
    ) {
    }

    private record JBlockModelEntry(int idx, JBlockModel model) {
    }
}
