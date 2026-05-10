// KindPolicy.java
package net.vampirestudios.raaMaterials;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.codecextras.structured.Structure;
import net.vampirestudios.raaMaterials.material.Form;

import java.util.ArrayList;
import java.util.List;

public record KindPolicy(List<FormGroup> groupsPlus, List<FormGroup> groupsMinus, List<Form> add, List<Form> remove,
                         int maxShapeForms) {
    public static final Codec<KindPolicy> CODEC = RecordCodecBuilder.create(i -> i.group(
            FormGroup.CODEC.listOf().optionalFieldOf("groups+", List.of()).forGetter(p -> p.groupsPlus),
            FormGroup.CODEC.listOf().optionalFieldOf("groups-", List.of()).forGetter(p -> p.groupsMinus),
            Form.CODEC.listOf().optionalFieldOf("add", List.of()).forGetter(p -> p.add),
            Form.CODEC.listOf().optionalFieldOf("remove", List.of()).forGetter(p -> p.remove),
            Codec.INT.optionalFieldOf("maxShapeForms", -1).forGetter(p -> p.maxShapeForms)
    ).apply(i, KindPolicy::new));
    public static final Structure<KindPolicy> STRUCTURE = Structure.record(builder -> {
        var a = builder.add("groups+", FormGroup.STRUCTURE.listOf(), KindPolicy::groupsPlus);
        var b = builder.add("groups-", FormGroup.STRUCTURE.listOf(), KindPolicy::groupsMinus);
        var c = builder.add("add", Form.STRUCTURE.listOf(), KindPolicy::add);
        var d = builder.add("remove", Form.STRUCTURE.listOf(), KindPolicy::remove);
        var e = builder.addOptional("maxShapeForms", Structure.INT, KindPolicy::maxShapeForms, () -> -1);
        return container -> new KindPolicy(
                a.apply(container), b.apply(container), c.apply(container), d.apply(container),
                e.apply(container)
        );
    });

    public KindPolicy(List<FormGroup> groupsPlus, List<FormGroup> groupsMinus, List<Form> add, List<Form> remove, int maxShapeForms) {
        this.groupsPlus = new ArrayList<>(groupsPlus);
        this.groupsMinus = new ArrayList<>(groupsMinus);
        this.add = new ArrayList<>(add);
        this.remove = new ArrayList<>(remove);
        this.maxShapeForms = maxShapeForms;
    }
}
