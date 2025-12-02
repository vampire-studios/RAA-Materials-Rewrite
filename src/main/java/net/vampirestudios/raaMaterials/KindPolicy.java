// KindPolicy.java
package net.vampirestudios.raaMaterials;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.codecextras.structured.Structure;
import net.vampirestudios.raaMaterials.material.Form;

import java.util.ArrayList;
import java.util.List;

public final class KindPolicy {
    public static final Codec<KindPolicy> CODEC = RecordCodecBuilder.create(i -> i.group(
        FormGroup.CODEC.listOf().optionalFieldOf("groups+", List.of()).forGetter(p -> p.groupsPlus),
        FormGroup.CODEC.listOf().optionalFieldOf("groups-", List.of()).forGetter(p -> p.groupsMinus),
        Form.CODEC.listOf().optionalFieldOf("add",        List.of()).forGetter(p -> p.add),
        Form.CODEC.listOf().optionalFieldOf("remove",     List.of()).forGetter(p -> p.remove),
        Codec.INT.optionalFieldOf("maxShapeForms", -1).forGetter(p -> p.maxShapeForms)
    ).apply(i, KindPolicy::new));
    public static final Structure<KindPolicy> STRUCTURE = Structure.record(builder -> {
        var a = builder.add("groups+", FormGroup.STRUCTURE.listOf(), KindPolicy::getGroupsPlus);
        var b = builder.add("groups-", FormGroup.STRUCTURE.listOf(), KindPolicy::getGroupsMinus);
        var c = builder.add("add", Form.STRUCTURE.listOf(), KindPolicy::getAdd);
        var d = builder.add("remove", Form.STRUCTURE.listOf(), KindPolicy::getRemove);
        var e = builder.addOptional("maxShapeForms", Structure.INT, KindPolicy::getMaxShapeForms, () -> -1);
        return container -> new KindPolicy(
                a.apply(container), b.apply(container), c.apply(container), d.apply(container),
                e.apply(container)
        );
    });

    public final List<FormGroup> groupsPlus, groupsMinus;
    public final List<Form> add, remove;
    public final int maxShapeForms;

    public KindPolicy(List<FormGroup> gp, List<FormGroup> gm, List<Form> add, List<Form> rem, int max) {
        this.groupsPlus = new ArrayList<>(gp);
        this.groupsMinus = new ArrayList<>(gm);
        this.add = new ArrayList<>(add);
        this.remove = new ArrayList<>(rem);
        this.maxShapeForms = max;
    }

    public KindPolicy() { this(List.of(), List.of(), List.of(), List.of(), -1); }

    public List<FormGroup> getGroupsPlus() {
        return groupsPlus;
    }

    public List<FormGroup> getGroupsMinus() {
        return groupsMinus;
    }

    public List<Form> getAdd() {
        return add;
    }

    public List<Form> getRemove() {
        return remove;
    }

    public int getMaxShapeForms() {
        return maxShapeForms;
    }
}
