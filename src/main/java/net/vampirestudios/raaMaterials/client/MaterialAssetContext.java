package net.vampirestudios.raaMaterials.client;

import net.minecraft.client.Minecraft;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.FormsRuntime;
import net.vampirestudios.raaMaterials.material.MaterialDef;

import java.util.List;

public record MaterialAssetContext(
        RuntimeResourcePack pack,
        Minecraft minecraft,
        List<MaterialDef> materials
) {
    public List<Form> forms(MaterialDef def) {
        return FormsRuntime.activeForms(minecraft.level, def);
    }

    public boolean has(MaterialDef def, Form form) {
        return forms(def).contains(form);
    }

    public boolean hasAny(MaterialDef def, List<Form> wanted) {
        var actual = forms(def);

        for (Form form : wanted) {
            if (actual.contains(form)) {
                return true;
            }
        }

        return false;
    }

    public void forEachMaterialWith(Form form, MaterialConsumer consumer) {
        forEachMaterialWithAny(List.of(form), consumer);
    }

    public void forEachMaterialWithAny(List<Form> forms, MaterialConsumer consumer) {
        for (int idx = 0; idx < materials.size(); idx++) {
            var def = materials.get(idx);

            if (hasAny(def, forms)) {
                consumer.accept(idx, def);
            }
        }
    }

    @FunctionalInterface
    public interface MaterialConsumer {
        void accept(int idx, MaterialDef def);
    }
}