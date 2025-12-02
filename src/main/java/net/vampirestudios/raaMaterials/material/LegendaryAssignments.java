package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

public record LegendaryAssignments(Map<Form, ResourceLocation> byForm) {
	// JSON codec (persisted)
	public static final Codec<LegendaryAssignments> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.unboundedMap(Form.CODEC, ResourceLocation.CODEC).fieldOf("by_form").forGetter(LegendaryAssignments::byForm)
	).apply(i, LegendaryAssignments::new));

	// Packet codec (synced)
	public static final StreamCodec<ByteBuf, LegendaryAssignments> PACKET_CODEC = StreamCodec.composite(
			ByteBufCodecs.map(HashMap::new, Form.STREAM_CODEC, ResourceLocation.STREAM_CODEC), LegendaryAssignments::byForm,
			LegendaryAssignments::new
	);

	public static LegendaryAssignments empty() {
		return new LegendaryAssignments(new HashMap<>());
	}

	/** Returns extra unique forms this material has (0 or more, but no two materials share the same Form). */
	public Set<Form> extrasFor(ResourceLocation materialId) {
		Set<Form> out = new HashSet<>();
		for (var e : byForm.entrySet()) if (materialId.equals(e.getValue())) out.add(e.getKey());
		return out;
	}

	/** Build a new assignment where each form is owned by at most one material. */
	public static LegendaryAssignments assign(Random rng, List<ResourceLocation> materials, List<Form> uniqueForms) {
		Map<Form, ResourceLocation> map = new EnumMap<>(Form.class);
		if (materials.isEmpty() || uniqueForms.isEmpty()) return new LegendaryAssignments(map);

		// Shuffle copies to keep it deterministic per seed if desired
		List<ResourceLocation> mats = new ArrayList<>(materials);
		Collections.shuffle(mats, rng);

		int mi = 0;
		for (Form f : uniqueForms) {
			if (mi >= mats.size()) break;
			map.put(f, mats.get(mi++));
		}
		return new LegendaryAssignments(map);
	}
}
