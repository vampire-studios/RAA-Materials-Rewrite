// MaterialSet.java
package net.vampirestudios.raaMaterials.material;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Optional;

public record MaterialSet(List<MaterialDef> all) {
	public static StreamCodec<ByteBuf, MaterialSet> PACKET_CODEC = StreamCodec.composite(
			MaterialCodecs.MATERIAL_DEF_STREAM_CODEC.apply(ByteBufCodecs.list()),
			MaterialSet::all,
			MaterialSet::new
	);

	public static MaterialSet DEFAULT = new MaterialSet(List.of());

	public Optional<MaterialDef> byId(Identifier id) {
		return all.stream().filter(m -> m.nameInformation().id().equals(id)).findFirst();
	}

	public Optional<MaterialDef> byIndex(int idx) {
		if (idx < 0 || idx >= all.size()) return Optional.empty();
		return Optional.of(all.get(idx));
	}
}
