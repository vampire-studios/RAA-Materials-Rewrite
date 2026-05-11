package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.material.textureSets.*;

public record MaterialAssetsDef(
		int version,
		Identifier materialId,
		Long paletteSeed,
		BlockTextureSet blockTextures,
		SandstoneTextureSet sandstoneTextures,
		ItemTextureSet itemTextures,
		ToolTextureSet toolTextures,
		DecorTextureSet decorTextures,
		CrystalTextureSet crystalTextures,
		LegendaryTextureSet legendaryTextures
) {
	/* ------------ JSON codec (data-pack / disk) ------------ */
	public static final Codec<MaterialAssetsDef> CODEC = RecordCodecBuilder.create(i -> i.group(
			Codec.INT.fieldOf("version").forGetter(MaterialAssetsDef::version),
			Identifier.CODEC.fieldOf("material_id").forGetter(MaterialAssetsDef::materialId),
			Codec.LONG.fieldOf("palette_seed").forGetter(MaterialAssetsDef::paletteSeed),
			BlockTextureSet.CODEC.fieldOf("block_textures").forGetter(MaterialAssetsDef::blockTextures),
			SandstoneTextureSet.CODEC.optionalFieldOf("sandstone_textures", SandstoneTextureSet.EMPTY).forGetter(MaterialAssetsDef::sandstoneTextures),
			ItemTextureSet.CODEC.fieldOf("item_textures").forGetter(MaterialAssetsDef::itemTextures),
			ToolTextureSet.CODEC.fieldOf("tool_textures").forGetter(MaterialAssetsDef::toolTextures),
			DecorTextureSet.CODEC.optionalFieldOf("decor_textures", DecorTextureSet.EMPTY).forGetter(MaterialAssetsDef::decorTextures),
			CrystalTextureSet.CODEC.optionalFieldOf("crystal_textures", CrystalTextureSet.EMPTY).forGetter(MaterialAssetsDef::crystalTextures),
			LegendaryTextureSet.CODEC.optionalFieldOf("legendary_textures", LegendaryTextureSet.EMPTY).forGetter(MaterialAssetsDef::legendaryTextures)
	).apply(i, MaterialAssetsDef::new));

	/* ------------ Network codec (compact) ------------ */
	public static final StreamCodec<ByteBuf, MaterialAssetsDef> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.VAR_INT, MaterialAssetsDef::version,
					Identifier.STREAM_CODEC, MaterialAssetsDef::materialId,
					ByteBufCodecs.VAR_LONG, MaterialAssetsDef::paletteSeed,
					BlockTextureSet.STREAM_CODEC, MaterialAssetsDef::blockTextures,
					SandstoneTextureSet.STREAM_CODEC, MaterialAssetsDef::sandstoneTextures,
					ItemTextureSet.STREAM_CODEC, MaterialAssetsDef::itemTextures,
					ToolTextureSet.STREAM_CODEC, MaterialAssetsDef::toolTextures,
					DecorTextureSet.STREAM_CODEC, MaterialAssetsDef::decorTextures,
					CrystalTextureSet.STREAM_CODEC, MaterialAssetsDef::crystalTextures,
					LegendaryTextureSet.STREAM_CODEC, MaterialAssetsDef::legendaryTextures,
					MaterialAssetsDef::new
			);
}
