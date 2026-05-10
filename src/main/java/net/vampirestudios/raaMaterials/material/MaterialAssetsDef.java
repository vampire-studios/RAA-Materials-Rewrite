// MaterialAssetsDef.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

/**
 * Per-material assets shipped out-of-band from the slim MaterialDef.
 * Keep this stable and versioned.
 */
public record MaterialAssetsDef(
		int version,                         // schema version for forwards-compat
		Identifier materialId,         // matches MaterialDef.id
		Long paletteSeed,          // for deterministic client regen
		BlockTextureSet textures1,     // your split structs
		ItemTextureSet textures2,
		ToolStoneTextureSet textures3,
		DecorTextureSet textures4
) {
	/* ------------ JSON codec (data-pack / disk) ------------ */
	public static final Codec<MaterialAssetsDef> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("version").forGetter(MaterialAssetsDef::version),
			Identifier.CODEC.fieldOf("material_id").forGetter(MaterialAssetsDef::materialId),
			Codec.LONG.fieldOf("palette_seed").forGetter(MaterialAssetsDef::paletteSeed),
			BlockTextureSet.CODEC.fieldOf("textures1").forGetter(MaterialAssetsDef::textures1),
			ItemTextureSet.CODEC.fieldOf("textures2").forGetter(MaterialAssetsDef::textures2),
			ToolStoneTextureSet.CODEC.fieldOf("textures3").forGetter(MaterialAssetsDef::textures3),
			DecorTextureSet.CODEC.optionalFieldOf("textures4", DecorTextureSet.EMPTY).forGetter(MaterialAssetsDef::textures4)
	).apply(i, MaterialAssetsDef::new));

	/* ------------ Network codec (compact) ------------ */
	public static final StreamCodec<ByteBuf, MaterialAssetsDef> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.VAR_INT, MaterialAssetsDef::version,
					Identifier.STREAM_CODEC, MaterialAssetsDef::materialId,
					ByteBufCodecs.VAR_LONG, MaterialAssetsDef::paletteSeed,
					BlockTextureSet.STREAM_CODEC, MaterialAssetsDef::textures1,
					ItemTextureSet.STREAM_CODEC, MaterialAssetsDef::textures2,
					ToolStoneTextureSet.STREAM_CODEC, MaterialAssetsDef::textures3,
					DecorTextureSet.STREAM_CODEC, MaterialAssetsDef::textures4,
					MaterialAssetsDef::new
			);
}
