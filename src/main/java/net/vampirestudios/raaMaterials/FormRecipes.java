/*
package net.vampirestudios.raaMaterials;

import net.vampirestudios.arrp.json.recipe.*;
import net.minecraft.resources.Identifier;

import static net.vampirestudios.raaMaterials.RRPGen.PACK;

public final class FormRecipes {
    // 9 plates → 1 plate_block ; and back
    public static void emitPlateBlockRecipes(Identifier matId) {
        // craft/<material>/plate_block_from_plates
        JShapedRecipe shaped = JShapedRecipe.shaped(
                JPattern.pattern("PPP","PPP","PPP"),
                JKeys.keys().key("P", JIngredient.ingredient().item(RAAMaterials.id("material_plate").toString())),
                JResult.stackedResult(RAAMaterials.id("material_plate_block").toString(), 1)
        );
        PACK.addRecipe(matId.withPrefix("craft/").withPath("/plate_block_from_plates"), shaped);

        // craft/<material>/plates_from_plate_block
        JShapelessRecipe back = JRecipe.shapeless(
                JIngredients.ingredients().add(JIngredient.ingredient().item(RAAMaterials.id("material_plate_block").toString())),
                JResult.stackedResult(RAAMaterials.id("material_plate").toString(), 9)
        );
        PACK.addRecipe(matId.withPrefix("craft/").withPath("/plates_from_plate_block"), back);
    }

    // 3 plates → 6 shingles ; and back (2x2 shingles → 3 plates)
    public static void emitShinglesRecipes(Identifier matId) {
        JShapedRecipe make = JShapedRecipe.shaped(
                JPattern.pattern("PPP"),
                JKeys.keys().key("P", JIngredient.ingredient().item(RAAMaterials.id("material_plate").toString())),
                JResult.stackedResult(RAAMaterials.id("material_shingles").toString(), 6)
        );
        PACK.addRecipe(matId.withPrefix("craft/").withPath("/shingles_from_plates"), make);

//        JsonObject back = new JsonObject();
//        back.addProperty("type", "raa:copy_material_shaped");
//        back.add("pattern", JPattern.of("SS","SS").toJson());
//        back.add("key", JKeys.of()
//            .key("S", JRecipe.ingredient().item(RAAMaterials.id("shingles"))).toJson());
//        back.add("result", JRecipe.result(RAAMaterials.id("plate"), 3).toJson());
//        PACK.addRecipe(id("craft/" + matId.getPath() + "/plates_from_shingles"), back);
    }
}
*/
