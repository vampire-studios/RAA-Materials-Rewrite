package net.vampirestudios.raaMaterials;

import net.vampirestudios.raaMaterials.material.Form;
import java.util.EnumSet;
import java.util.List;
import static net.vampirestudios.raaMaterials.material.Form.*;

public final class FormResolver {
    public static List<Form> formsFor(FormGroup g) {
        return FormGroupConfig.active().getOrDefault(g, FormGroupConfig.defaults().getOrDefault(g, List.of()));
    }

    /** Compute final allowed forms for a kind given defaults + config KindPolicy. */
    public static EnumSet<Form> computeAllowed(java.util.List<FormGroup> baseGroups, KindPolicy policy) {
        var out = EnumSet.noneOf(Form.class);
        // Base groups
        for (var g : baseGroups) out.addAll(formsFor(g));
        // Apply +/- groups from policy
        for (var g : policy.groupsPlus()) out.addAll(formsFor(g));
        for (var g : policy.groupsMinus()) formsFor(g).forEach(out::remove);
        // Explicit adds/removes
        out.addAll(policy.add());
        policy.remove().forEach(out::remove);
        // Shape clamp
        if (policy.maxShapeForms() >= 0) {
            var shapes = out.stream().filter(f -> f == SLAB || f == STAIRS || f == WALL).toList();
            if (shapes.size() > policy.maxShapeForms()) {
                shapes.stream().skip(policy.maxShapeForms()).forEach(out::remove);
            }
        }
        return out;
    }
}
