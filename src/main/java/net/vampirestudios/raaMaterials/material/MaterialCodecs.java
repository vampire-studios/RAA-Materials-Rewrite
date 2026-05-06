// material/MaterialCodecs.java
package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.vampirestudios.raaMaterials.StreamCodecExpanded;
import net.vampirestudios.raaMaterials.worldgen.SpawnMode;

public final class MaterialCodecs {
	public static final Codec<SpawnRules> SPAWN_RULES = RecordCodecBuilder.create(i -> i.group(
			SpawnMode.CODEC.fieldOf("mode").forGetter(SpawnRules::mode),
			Codec.INT.fieldOf("vein_size").forGetter(SpawnRules::veinSize),
			Codec.INT.fieldOf("veins_per_chunk").forGetter(SpawnRules::veinsPerChunk),
			Codec.INT.fieldOf("min_y").forGetter(SpawnRules::minY),
			Codec.INT.fieldOf("max_y").forGetter(SpawnRules::maxY),
			Codec.INT.fieldOf("y_peak").forGetter(SpawnRules::yPeak),
			Codec.STRING.listOf().fieldOf("biome_tag").forGetter(SpawnRules::biomeTag),
			Codec.STRING.listOf().fieldOf("replaceable_tag").forGetter(SpawnRules::replaceableTag)
	).apply(i, SpawnRules::new));
	public static final StreamCodec<ByteBuf, SpawnRules> SPAWN_RULES_STREAM_CODEC = StreamCodec.composite(
			SpawnMode.STREAM_CODEC,
			SpawnRules::mode,
			ByteBufCodecs.INT,
			SpawnRules::veinSize,
			ByteBufCodecs.INT,
			SpawnRules::veinsPerChunk,
			ByteBufCodecs.INT,
			SpawnRules::minY,
			ByteBufCodecs.INT,
			SpawnRules::maxY,
			ByteBufCodecs.INT,
			SpawnRules::yPeak,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
			SpawnRules::biomeTag,
			ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()),
			SpawnRules::replaceableTag,
			SpawnRules::new
	);

	public static final Codec<MaterialDef> MATERIAL_DEF = RecordCodecBuilder.create(i -> i.group(
			MaterialDef.NameInformation.CODEC.fieldOf("name_information").forGetter(MaterialDef::nameInformation),
			MaterialKind.CODEC.fieldOf("kind").forGetter(MaterialDef::kind),
			Codec.INT.fieldOf("primary_color").forGetter(MaterialDef::primaryColor),
			Codec.FLOAT.fieldOf("hardness").forGetter(MaterialDef::hardness),
			Codec.FLOAT.fieldOf("resistance").forGetter(MaterialDef::resistance),
			Codec.FLOAT.fieldOf("tool_efficiency").forGetter(MaterialDef::toolEfficiency),
			HarvestTier.CODEC.fieldOf("tier").forGetter(MaterialDef::tier),
			Form.CODEC.listOf().fieldOf("forms").forGetter(MaterialDef::forms),
			SpawnInfo.CODEC.fieldOf("spawn_info").forGetter(MaterialDef::spawn),
			ToolMaterialSpec.CODEC.optionalFieldOf("tool_spec").forGetter(MaterialDef::toolSpec),
			Codec.LONG.fieldOf("asset_seed").forGetter(MaterialDef::assetSeed),
			MaterialDef.OreHost.CODEC.fieldOf("forms").forGetter(MaterialDef::host)
	).apply(i, MaterialDef::new));
	public static final StreamCodec<ByteBuf, MaterialDef> MATERIAL_DEF_STREAM_CODEC = StreamCodecExpanded.composite(
			MaterialDef.NameInformation.STREAM_CODEC,
			MaterialDef::nameInformation,
			MaterialKind.STREAM_CODEC,
			MaterialDef::kind,
			ByteBufCodecs.INT,
			MaterialDef::primaryColor,
			ByteBufCodecs.FLOAT,
			MaterialDef::hardness,
			ByteBufCodecs.FLOAT,
			MaterialDef::resistance,
			ByteBufCodecs.FLOAT,
			MaterialDef::toolEfficiency,
			HarvestTier.STREAM_CODEC,
			MaterialDef::tier,
			Form.STREAM_CODEC.apply(ByteBufCodecs.list()),
			MaterialDef::forms,
			SpawnInfo.STREAM_CODEC,
			MaterialDef::spawn,
			ByteBufCodecs.optional(ToolMaterialSpec.STREAM_CODEC),
			MaterialDef::toolSpec,
			ByteBufCodecs.LONG,
			MaterialDef::assetSeed,
			MaterialDef.OreHost.STREAM_CODEC,
			MaterialDef::host,
			MaterialDef::new
	);
	public static final Codec<MaterialSet> MATERIAL_SET = RecordCodecBuilder.create(i -> i.group(
			MATERIAL_DEF.listOf().fieldOf("all").forGetter(MaterialSet::all)
	).apply(i, MaterialSet::new));

	private MaterialCodecs() {
	}
}
