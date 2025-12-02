// MaterialAssetsDef.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Per-material assets shipped out-of-band from the slim MaterialDef.
 * Keep this stable and versioned.
 */
public record MaterialAssetsDef(
		int version,                         // schema version for forwards-compat
		ResourceLocation materialId,         // matches MaterialDef.id
		Long paletteSeed,          // for deterministic client regen
		TextureDef1 textures1,     // your split structs
		TextureDef2 textures2,
		TextureDef3 textures3
) {
	/* ------------ JSON codec (data-pack / disk) ------------ */
	public static final Codec<MaterialAssetsDef> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("version").forGetter(MaterialAssetsDef::version),
			ResourceLocation.CODEC.fieldOf("material_id").forGetter(MaterialAssetsDef::materialId),
			Codec.LONG.fieldOf("palette_seed").forGetter(MaterialAssetsDef::paletteSeed),
			TextureDef1.CODEC.fieldOf("textures1").forGetter(MaterialAssetsDef::textures1),
			TextureDef2.CODEC.fieldOf("textures2").forGetter(MaterialAssetsDef::textures2),
			TextureDef3.CODEC.fieldOf("textures3").forGetter(MaterialAssetsDef::textures3)
	).apply(i, MaterialAssetsDef::new));

	/* ------------ Network codec (compact) ------------ */
	public static final StreamCodec<ByteBuf, MaterialAssetsDef> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.VAR_INT, MaterialAssetsDef::version,
					ResourceLocation.STREAM_CODEC, MaterialAssetsDef::materialId,
					ByteBufCodecs.VAR_LONG, MaterialAssetsDef::paletteSeed,
					TextureDef1.STREAM_CODEC, MaterialAssetsDef::textures1,
					TextureDef2.STREAM_CODEC, MaterialAssetsDef::textures2,
					TextureDef3.STREAM_CODEC, MaterialAssetsDef::textures3,
					MaterialAssetsDef::new
			);
}
