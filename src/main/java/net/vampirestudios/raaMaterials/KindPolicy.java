// KindPolicy.java
package net.vampirestudios.raaMaterials;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public KindPolicy(List<FormGroup> groupsPlus, List<FormGroup> groupsMinus, List<Form> add, List<Form> remove, int maxShapeForms) {
        this.groupsPlus = new ArrayList<>(groupsPlus);
        this.groupsMinus = new ArrayList<>(groupsMinus);
        this.add = new ArrayList<>(add);
        this.remove = new ArrayList<>(remove);
        this.maxShapeForms = maxShapeForms;
    }
}
