// api/MaterialForm.java
package net.vampirestudios.raaMaterials.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.vampirestudios.raaMaterials.material.MaterialDef;

import java.util.Optional;

public interface MaterialForm {
    Identifier id();                 // e.g. yourmod:plate_block
    boolean supports(MaterialDef def);     // should this form exist for this material?

    // Factories (called once during content bootstrap)
    Optional<Block>   createBlock();       // empty if “item-only” form
    Optional<Item>    createItem();        // empty if “block-only” form

    // Per-material emit hooks (called during RRP build)
    void emitModels(MaterialDef def);
    void emitRecipes(MaterialDef def);
    void emitLoot(MaterialDef def);
    void emitTags(MaterialDef def);
}
