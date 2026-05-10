package net.vampirestudios.raaMaterials.material;

import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public final class FormsRuntime {
    private FormsRuntime() {}

    /** Merge a material’s declared forms with any world-assigned unique forms. */
    public static List<Form> activeForms(Level level, MaterialDef def) {
        var list = new ArrayList<>(def.forms());
        var extras = level.getAttachedOrCreate(MaterialAttachments.LEGENDARIES).extrasFor(def.nameInformation().id());
        for (var f : extras) if (!list.contains(f)) list.add(f);
        return list;
    }

    /** Check a single form quickly. */
    public static boolean has(Level level, MaterialDef def, Form f) {
        if (def.forms().contains(f)) return true;
        var assignedTo = level.getAttachedOrCreate(MaterialAttachments.LEGENDARIES).byForm().get(f);
        return assignedTo != null && assignedTo.equals(def.nameInformation().id());
    }
}
