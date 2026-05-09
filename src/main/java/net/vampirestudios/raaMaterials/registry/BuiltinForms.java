package net.vampirestudios.raaMaterials.registry;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.api.FormRegistry;
import net.vampirestudios.raaMaterials.api.MaterialForm;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialDef;
import net.vampirestudios.raaMaterials.material.MaterialKind;

import java.util.Optional;

public final class BuiltinForms {
    public static void init() {
        // ----- Items -----
        registerSimple("ingot", YItems.PARAM_INGOT, def -> def.forms().contains(Form.INGOT));
        registerSimple("dust", YItems.PARAM_DUST, def -> def.forms().contains(Form.DUST));
        registerSimple("nugget", YItems.PARAM_NUGGET, def -> def.forms().contains(Form.NUGGET));
        registerSimple("raw", YItems.PARAM_RAW, def -> def.forms().contains(Form.RAW));
        registerSimple("gem", YItems.PARAM_GEM, def -> def.forms().contains(Form.GEM));
        registerSimple("crystal", YItems.PARAM_CRYSTAL, def -> def.forms().contains(Form.CRYSTAL));
        registerSimple("shard", YItems.PARAM_SHARD, def -> def.forms().contains(Form.SHARD));
        registerSimple("plate", YItems.PARAM_SHEET, def -> def.forms().contains(Form.SHEET));
        registerSimple("gear", YItems.PARAM_GEAR, def -> def.forms().contains(Form.GEAR));
        registerSimple("cluster", YItems.PARAM_CLUSTER_ITEM, def -> def.forms().contains(Form.CLUSTER));

        // ----- Blocks -----
        registerBlock("ore", YBlocks.PARAM_ORE, YItems.PARAM_ORE_ITEM, def -> def.forms().contains(Form.ORE));
        registerBlock("block", YBlocks.PARAM_BLOCK, YItems.PARAM_BLOCK_ITEM, def -> def.forms().contains(Form.BLOCK));
        registerBlock("raw_block", YBlocks.PARAM_RAW_BLOCK, YItems.PARAM_RAW_BLOCK_ITEM, def -> def.forms().contains(Form.RAW_BLOCK));

        // ----- New Decorative Blocks -----
        registerBlock("plate_block", YBlocks.PARAM_PLATE_BLOCK, YItems.PARAM_PLATE_BLOCK_ITEM,
                def -> def.kind() == MaterialKind.METAL || def.kind() == MaterialKind.ALLOY);
        registerBlock("shingles", YBlocks.PARAM_SHINGLES_BLOCK, YItems.PARAM_SHINGLES_BLOCK_ITEM,
                def -> def.kind() == MaterialKind.METAL || def.kind() == MaterialKind.ALLOY);
    }

    // ------------------------------------------------------------------------

    private static void registerSimple(String name, Item item, java.util.function.Predicate<MaterialDef> supports) {
        Identifier id = RAAMaterials.id(name);
        FormRegistry.register(new MaterialForm() {
            @Override public Identifier id() { return id; }
            @Override public boolean supports(MaterialDef def) { return supports.test(def); }
            @Override public Optional<Item> createItem() { return Optional.of(item); }
            @Override public Optional<Block> createBlock() { return Optional.empty(); }

            @Override public void emitModels(MaterialDef def) {
                // e.g., point to "item/<name>" model tinted with def.primaryColor()
            }
            @Override public void emitRecipes(MaterialDef def) {
                // e.g., nugget <-> ingot; block <-> ingot
            }
            @Override public void emitLoot(MaterialDef def) { }
            @Override public void emitTags(MaterialDef def) { }
        });
    }

    private static void registerBlock(String name, Block block, Item blockItem,
                                      java.util.function.Predicate<MaterialDef> supports) {
        Identifier id = RAAMaterials.id(name);
        FormRegistry.register(new MaterialForm() {
            @Override public Identifier id() { return id; }
            @Override public boolean supports(MaterialDef def) { return supports.test(def); }
            @Override public Optional<Item> createItem() { return Optional.of(blockItem); }
            @Override public Optional<Block> createBlock() { return Optional.of(block); }

            @Override public void emitModels(MaterialDef def) {
                // blockstate + block model; item model pointing to block
            }
            @Override public void emitRecipes(MaterialDef def) {
                // 9x ingot -> 1 block; smelting ore -> ingot; etc.
            }
            @Override public void emitLoot(MaterialDef def) {
                // drop self or item form with material component
            }
            @Override public void emitTags(MaterialDef def) {
                // add to #c:ores/<kind>, #c:storage_blocks/<kind>, etc.
            }
        });
    }

    private BuiltinForms(){}
}
