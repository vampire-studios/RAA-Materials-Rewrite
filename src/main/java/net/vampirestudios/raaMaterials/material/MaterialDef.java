package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;

public record MaterialDef(
		NameInformation nameInformation,
		MaterialKind kind,
		int primaryColor,
		float hardness,
		float resistance,
		float toolEfficiency,
		HarvestTier tier,
		List<Form> forms,
		SpawnInfo spawn,
		Optional<ToolMaterialSpec> toolSpec,
		long assetSeed,
		OreHost host,
		SpikeGrowthLiquid spikeGrowth
) {
	public record NameInformation(Identifier id, String displayName) {
		public static final Codec<NameInformation> CODEC = RecordCodecBuilder.create(i -> i.group(
				Identifier.CODEC.fieldOf("id").forGetter(NameInformation::id),
				Codec.STRING.fieldOf("display_name").forGetter(NameInformation::displayName)
		).apply(i, NameInformation::new));
		public static final StreamCodec<ByteBuf, NameInformation> STREAM_CODEC = StreamCodec.composite(
				Identifier.STREAM_CODEC,
				NameInformation::id,
				ByteBufCodecs.STRING_UTF8,
				NameInformation::displayName,
				NameInformation::new
		);
	}
	public enum OreHost implements StringRepresentable {
		STONE("minecraft:block/stone", Target.tag("minecraft:stone_ore_replaceables")),
		DEEPSLATE("minecraft:block/deepslate", Target.tag("minecraft:deepslate_ore_replaceables")),
		NETHERRACK("minecraft:block/netherrack", Target.tag("minecraft:netherrack_ore_replaceables")),
		END_STONE("minecraft:block/end_stone", Target.block("minecraft:end_stone")),
		ANDESITE("minecraft:block/andesite", Target.block("minecraft:andesite")),
		DIORITE("minecraft:block/diorite", Target.block("minecraft:diorite")),
		GRANITE("minecraft:block/granite", Target.block("minecraft:granite")),
		TUFF("minecraft:block/tuff", Target.block("minecraft:tuff")),
		BASALT("minecraft:block/basalt_side", Target.block("minecraft:basalt")),
		BLACKSTONE("minecraft:block/blackstone", Target.block("minecraft:blackstone")),
		SANDSTONE("minecraft:block/sandstone", Target.block("minecraft:sandstone")),
		RED_SANDSTONE("minecraft:block/red_sandstone", Target.block("minecraft:red_sandstone")),
		CALCITE("minecraft:block/calcite", Target.block("minecraft:calcite")),
		DRIPSTONE("minecraft:block/dripstone_block", Target.block("minecraft:dripstone_block")),
		SMOOTH_BASALT("minecraft:block/smooth_basalt", Target.block("minecraft:smooth_basalt")),
		OVERWORLD_STONE("minecraft:block/stone", Target.tag("minecraft:base_stone_overworld"));

		private final Identifier baseTexture;
		private final Target target;

		OreHost(String tex, Target target) {
			this.baseTexture = Identifier.tryParse(tex);
			this.target = target;
		}

		public Identifier baseTexture() {
			return baseTexture;
		}

		public Target target() {
			return target;
		}

		/** Turn the target into a RuleTest for placed feature configs. */
		public RuleTest toRuleTest() {
			return target.isTag()
					? new TagMatchTest(TagKey.create(Registries.BLOCK, target.id()))
					: new BlockMatchTest(BuiltInRegistries.BLOCK.getValue(target.id()));
		}

		public static final Codec<OreHost> CODEC = StringRepresentable.fromEnum(OreHost::values);
		private static final IntFunction<OreHost> BY_ID = ByIdMap.continuous(OreHost::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public static final StreamCodec<ByteBuf, OreHost> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, OreHost::ordinal);

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}

		/** Small sealed type to carry either a tag or a block id. */
		public sealed interface Target permits Target.TagT, Target.BlockT {
			static Target tag(String tagId) {
				return new TagT(Identifier.tryParse(tagId));
			}

			static Target block(String blkId) {
				return new BlockT(Identifier.tryParse(blkId));
			}

			Identifier id();

			default boolean isTag() {
				return this instanceof TagT;
			}

			record TagT(Identifier id) implements Target {
				public TagT {
					Objects.requireNonNull(id);
				}
			}

			record BlockT(Identifier id) implements Target {
				public BlockT {
					Objects.requireNonNull(id);
				}
			}
		}
	}
}
