// MaterialSet.java
package net.vampirestudios.raaMaterials.material;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class MaterialSet {
	public static final StreamCodec<ByteBuf, MaterialSet> PACKET_CODEC = StreamCodec.composite(
			MaterialCodecs.MATERIAL_DEF_STREAM_CODEC.apply(ByteBufCodecs.list()),
			MaterialSet::all,
			MaterialSet::new
	);

	public static final MaterialSet DEFAULT = new MaterialSet(List.of());

	private final List<MaterialDef> all;
	private final Map<Identifier, Integer> idIndex;

	public MaterialSet(List<MaterialDef> all) {
		this.all = List.copyOf(all);
		var map = new HashMap<Identifier, Integer>(all.size() * 2);
		for (int i = 0; i < all.size(); i++) {
			map.put(all.get(i).nameInformation().id(), i);
		}
		this.idIndex = Map.copyOf(map);
	}

	public List<MaterialDef> all() {
		return all;
	}

	public Optional<MaterialDef> byId(Identifier id) {
		var idx = idIndex.get(id);
		return idx != null ? Optional.of(all.get(idx)) : Optional.empty();
	}

	public Optional<Integer> indexOf(Identifier id) {
		return Optional.ofNullable(idIndex.get(id));
	}

	public Optional<MaterialDef> byIndex(int idx) {
		if (idx < 0 || idx >= all.size()) return Optional.empty();
		return Optional.of(all.get(idx));
	}
}
