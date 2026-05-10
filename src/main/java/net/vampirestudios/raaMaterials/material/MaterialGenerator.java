package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.material.MaterialDef.OreHost;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.IntFunction;

import static java.util.Map.entry;
import static net.vampirestudios.raaMaterials.material.Form.*;

public final class MaterialGenerator {
	private MaterialGenerator() {}

	public enum Profile implements StringRepresentable {
		BALANCED,
		TECH_HEAVY,
		GEO_HEAVY,
		FANTASY_HEAVY;

		public static final Codec<Profile> CODEC = StringRepresentable.fromEnum(Profile::values);
		private static final IntFunction<Profile> BY_ID = ByIdMap.continuous(Profile::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		public static final StreamCodec<ByteBuf, Profile> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, Profile::ordinal);

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
	}

	public static MaterialSet generate(long worldSeed) {
		Random rng = new Random(hash(worldSeed));
		RAAConfig cfg = RAAConfig.active();

		int min = Math.max(0, cfg.materialsMin());
		int max = Math.max(min, cfg.materialsMax());
		int count = rng.nextInt(min, max + 1);

		List<MaterialDef> list = new ArrayList<>(count);
		Set<String> usedDisplayNames = new HashSet<>();
		Set<String> usedIds = new HashSet<>();

		for (int i = 0; i < count; i++) {
			MaterialKind kind = pickKind(rng);
			int color = randomColorFor(kind, rng);

			HarvestTier tier = pickTierFor(kind, rng);
			List<Form> forms = getForms(kind, rng);

			SpawnInfo spawn = OverworldSpawnProfiles.pick(kind, rng);
			OreHost host = pickOreHost(kind, rng, spawn.y().minY(), spawn.y().maxY());

			MaterialDef.NameInformation name = generateUniqueName(
					worldSeed,
					i,
					kind,
					color,
					spawn,
					usedDisplayNames,
					usedIds
			);

			Optional<ToolMaterialSpec> toolSpec = toolSpecFor(kind, tier, rng);

			FormDependencies.validate(name, forms);

			list.add(new MaterialDef(
					name,
					kind,
					color,
					hardnessFor(kind, tier),
					blastFor(kind, tier),
					effFor(kind, tier),
					tier,
					forms,
					spawn,
					toolSpec,
					mix(worldSeed, name.id()),
					host
			));
		}

		return new MaterialSet(list);
	}

	private static MaterialDef.NameInformation generateUniqueName(
			long worldSeed,
			int index,
			MaterialKind kind,
			int color,
			SpawnInfo spawn,
			Set<String> usedDisplayNames,
			Set<String> usedIds
	) {
		for (int tries = 0; tries < 32; tries++) {
			int adjustedIndex = index + tries * 10_000;

			MaterialDef.NameInformation name = net.vampirestudios.raaMaterials.GeoNameGen.generateUnique(
					worldSeed,
					adjustedIndex,
					kind,
					color,
					spawn,
					usedDisplayNames
			);

			String idKey = name.id().toString();
			if (usedIds.add(idKey)) {
				return name;
			}
		}

		MaterialDef.NameInformation fallback = GeoNameGen.generate(worldSeed, index, kind, color, spawn);
		usedDisplayNames.add(fallback.displayName().toLowerCase(Locale.ROOT));
		usedIds.add(fallback.id().toString());
		return fallback;
	}

	private static Optional<ToolMaterialSpec> toolSpecFor(MaterialKind kind, HarvestTier tier, Random rng) {
		return switch (kind) {
			case METAL, ALLOY -> Optional.of(ToolMaterialPresets.forMetal(tier));
			case GEM -> Optional.of(ToolMaterialPresets.forGem(tier));
			default -> Optional.empty();
		};
	}

	private static OreHost pickOreHost(MaterialKind kind, Random rng, int minY, int maxY) {
		if (kind == MaterialKind.STONE) {
			return OreHost.OVERWORLD_STONE;
		}

		if (kind == MaterialKind.VOLCANIC) {
			int r = rng.nextInt(100);
			if (r < 35) return OreHost.BASALT;
			if (r < 65) return OreHost.TUFF;
			if (r < 85) return OreHost.SMOOTH_BASALT;
			return OreHost.OVERWORLD_STONE;
		}

		if (kind == MaterialKind.SALT) {
			int r = rng.nextInt(100);
			if (r < 45) return OreHost.STONE;
			if (r < 70) return OreHost.CALCITE;
			if (r < 85) return OreHost.TUFF;
			return OreHost.OVERWORLD_STONE;
		}

		int mid = (minY + maxY) / 2;

		if (mid <= 0) {
			int r = rng.nextInt(100);
			if (r < 60) return OreHost.DEEPSLATE;
			if (r < 85) return OreHost.TUFF;
			return OreHost.SMOOTH_BASALT;
		}

		if (mid >= 80) {
			int r = rng.nextInt(100);
			if (r < 45) return OreHost.STONE;
			if (r < 62) return OreHost.GRANITE;
			if (r < 79) return OreHost.DIORITE;
			if (r < 94) return OreHost.ANDESITE;
			return OreHost.CALCITE;
		}

		int r = rng.nextInt(100);
		if (r < 48) return OreHost.STONE;
		if (r < 64) return OreHost.GRANITE;
		if (r < 80) return OreHost.DIORITE;
		if (r < 93) return OreHost.ANDESITE;
		return OreHost.CALCITE;
	}

	private static final List<Form> STANDARD_EQUIPMENT_SET = List.of(
			PICKAXE, AXE, SWORD, SHOVEL, HOE
	);

	private static final List<Form> ARMOR_SET = List.of(
			HELMET, CHESTPLATE, LEGGINGS, BOOTS
	);

	private static final List<Form> METAL_PARTS = List.of(
			INGOT, SHEET, GEAR, ROD, WIRE, COIL,
			RIVET, BOLT, NAIL, RING
	);

	private static final List<Form> METAL_DECOR_BLOCKS = List.of(
			BRICKS, BLOCK, PLATE_BLOCK, SHINGLES,
			PILLAR, TILES, CHISELED, BARS, GRATE
	);

	private static final List<Form> METAL_INTERACTIVE_BLOCKS = List.of(
			BUTTON, PRESSURE_PLATE,
			DOOR, TRAPDOOR,
			CHAIN, LANTERN, LAMP
	);

	private static final List<Form> BLOCK_SHAPES = List.of(
			SLAB, STAIRS, WALL
	);

	private static final List<Form> BASIC_INTERACTIVE_BLOCKS = List.of(
			BUTTON, PRESSURE_PLATE
	);

	private static final List<Form> WOOD_NATURAL = List.of(
			LOG, WOOD, LEAVES, SAPLING
	);

	private static final List<Form> WOOD_PROCESSED = List.of(
			STRIPPED_LOG, STRIPPED_WOOD, PLANKS, BEAM, BOARDS
	);

	private static final List<Form> WOOD_BUILDING = List.of(
			SLAB, STAIRS, FENCE, FENCE_GATE,
			DOOR, TRAPDOOR, BUTTON, PRESSURE_PLATE,
			SIGN, HANGING_SIGN, LADDER
	);

	private static @NotNull List<Form> getForms(MaterialKind kind, Random rng) {
		return switch (kind) {
			case METAL -> join(
					List.of(ORE, RAW, DUST, NUGGET, RAW_BLOCK),
					METAL_PARTS,
					METAL_DECOR_BLOCKS,
					METAL_INTERACTIVE_BLOCKS,
					STANDARD_EQUIPMENT_SET,
					ARMOR_SET
			);

			case ALLOY -> join(
					METAL_PARTS,
					METAL_DECOR_BLOCKS,
					METAL_INTERACTIVE_BLOCKS,
					STANDARD_EQUIPMENT_SET,
					ARMOR_SET
			);

			case GEM -> join(
					List.of(ORE, BLOCK, GEM, SHARD),
					STANDARD_EQUIPMENT_SET
			);

			case CRYSTAL -> List.of(
					CLUSTER, BLOCK, CRYSTAL, SHARD, DUST,
					BUDDING, BUD_SMALL, BUD_MEDIUM, BUD_LARGE,
					GLASS, TINTED_GLASS, PANE,
					CRYSTAL_BRICKS, MOSAIC,
					CALCITE_LAMP, BASALT_LAMP, LAMP,
					CHIME, ROD_BLOCK
			);

			case STONE -> join(
					List.of(
							BLOCK, CHISELED, POLISHED, BRICKS, PILLAR, TILES,
							MOSAIC, MOSSY, CRACKED, COBBLED
					),
					BLOCK_SHAPES,
					BASIC_INTERACTIVE_BLOCKS,
					List.of(LAMP)
			);

			case SAND -> sandForms(rng);

			case GRAVEL -> join(
					List.of(BLOCK, POLISHED),
					BLOCK_SHAPES
			);

			case CLAY -> List.of(
					BLOCK, BALL, CERAMIC
			);

			case MUD -> join(
					List.of(BLOCK, DRIED, BRICKS),
					BLOCK_SHAPES
			);

			case SOIL -> List.of(
					BLOCK, PACKED_SOIL
			);

			case SALT -> List.of(
					BLOCK, DUST, LAMP
			);

			case VOLCANIC -> join(
					List.of(BLOCK, COBBLED, POLISHED, BRICKS, PILLAR, MOSSY),
					BASIC_INTERACTIVE_BLOCKS,
					List.of(LAMP)
			);

			case WOOD -> join(
					WOOD_NATURAL,
					WOOD_PROCESSED,
					WOOD_BUILDING
			);

			case OTHER -> List.of(DUST);
		};
	}

	private static @NotNull List<Form> sandForms(Random rng) {
		var forms = new ArrayList<>(List.of(BLOCK, SANDSTONE, CUT, CHISELED, SMOOTH, SLAB, STAIRS, WALL));
		if (rng.nextFloat() < 0.45f) {
			forms.add(GLASS);
			if (rng.nextFloat() < 0.25f) {
				forms.add(TINTED_GLASS);
			}
		}
		return List.copyOf(forms);
	}

	@SafeVarargs
	private static @NotNull List<Form> join(List<Form>... groups) {
		LinkedHashSet<Form> out = new LinkedHashSet<>();

		for (List<Form> group : groups) {
			out.addAll(group);
		}

		return List.copyOf(out);
	}

	private static int randomColorFor(MaterialKind kind, Random rng) {
		return switch (kind) {
			case METAL, ALLOY -> randomMetalColor(rng);
			case GEM -> randomGemColor(rng);
			case CRYSTAL -> randomCrystalColor(rng);
			case SAND -> randomWarmEarthColor(rng);
			case GRAVEL, STONE -> randomStoneColor(rng);
			case CLAY, MUD, SOIL -> randomEarthColor(rng);
			case SALT -> randomSaltColor(rng);
			case VOLCANIC -> randomVolcanicColor(rng);
			case WOOD -> randomWoodColor(rng);
			default -> randomColor(rng);
		};
	}

	private static int randomMetalColor(Random rng) {
		float hue = pickFloat(rng, 0.04f, 0.09f, 0.11f, 0.58f, 0.67f);
		float saturation = 0.25f + rng.nextFloat() * 0.55f;
		float brightness = 0.45f + rng.nextFloat() * 0.40f;
		return hsb(hue, saturation, brightness);
	}

	private static int randomGemColor(Random rng) {
		float hue = rng.nextFloat();
		float saturation = 0.65f + rng.nextFloat() * 0.35f;
		float brightness = 0.55f + rng.nextFloat() * 0.40f;
		return hsb(hue, saturation, brightness);
	}

	private static int randomCrystalColor(Random rng) {
		float hue = rng.nextFloat();
		float saturation = 0.25f + rng.nextFloat() * 0.55f;
		float brightness = 0.70f + rng.nextFloat() * 0.25f;
		return hsb(hue, saturation, brightness);
	}

	private static int randomStoneColor(Random rng) {
		float hue = 0.05f + rng.nextFloat() * 0.15f;
		float saturation = 0.08f + rng.nextFloat() * 0.25f;
		float brightness = 0.35f + rng.nextFloat() * 0.45f;
		return hsb(hue, saturation, brightness);
	}

	private static int randomEarthColor(Random rng) {
		float hue = 0.04f + rng.nextFloat() * 0.13f;
		float saturation = 0.30f + rng.nextFloat() * 0.45f;
		float brightness = 0.30f + rng.nextFloat() * 0.40f;
		return hsb(hue, saturation, brightness);
	}

	private static int randomWarmEarthColor(Random rng) {
		float hue = 0.08f + rng.nextFloat() * 0.08f;
		float saturation = 0.35f + rng.nextFloat() * 0.40f;
		float brightness = 0.55f + rng.nextFloat() * 0.35f;
		return hsb(hue, saturation, brightness);
	}

	private static int randomSaltColor(Random rng) {
		float hue = pickFloat(rng, 0.0f, 0.08f, 0.55f, 0.75f);
		float saturation = 0.08f + rng.nextFloat() * 0.35f;
		float brightness = 0.70f + rng.nextFloat() * 0.25f;
		return hsb(hue, saturation, brightness);
	}

	private static int randomVolcanicColor(Random rng) {
		float hue = pickFloat(rng, 0.0f, 0.04f, 0.08f, 0.72f);
		float saturation = 0.25f + rng.nextFloat() * 0.65f;
		float brightness = 0.20f + rng.nextFloat() * 0.45f;
		return hsb(hue, saturation, brightness);
	}

	private static int randomWoodColor(Random rng) {
		float hue = 0.06f + rng.nextFloat() * 0.08f;
		float saturation = 0.35f + rng.nextFloat() * 0.45f;
		float brightness = 0.35f + rng.nextFloat() * 0.35f;
		return hsb(hue, saturation, brightness);
	}

	private static int randomColor(Random rng) {
		float hue = rng.nextFloat();
		float saturation = 0.45f + rng.nextFloat() * 0.55f;
		float brightness = 0.50f + rng.nextFloat() * 0.40f;
		return hsb(hue, saturation, brightness);
	}

	private static int hsb(float hue, float saturation, float brightness) {
		return java.awt.Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;
	}

	private static float pickFloat(Random rng, float... values) {
		return values[rng.nextInt(values.length)];
	}

	private static HarvestTier pickTierFor(MaterialKind kind, Random rng) {
		return switch (kind) {
			case METAL, ALLOY -> pickTier(rng);
			case GEM -> pickGemTier(rng);
			default -> HarvestTier.IRON;
		};
	}

	private static HarvestTier pickTier(Random rng) {
		int roll = rng.nextInt(100);
		if (roll < 40) return HarvestTier.STONE;
		if (roll < 80) return HarvestTier.IRON;
		if (roll < 95) return HarvestTier.DIAMOND;
		return HarvestTier.NETHERITE;
	}

	private static HarvestTier pickGemTier(Random rng) {
		int roll = rng.nextInt(100);
		if (roll < 15) return HarvestTier.STONE;
		if (roll < 65) return HarvestTier.IRON;
		if (roll < 95) return HarvestTier.DIAMOND;
		return HarvestTier.NETHERITE;
	}

	private static MaterialKind pickKind(Random rng) {
		Map<MaterialKind, Integer> cfg = loadWeightsFromConfig();

		if (cfg != null && !cfg.isEmpty()) {
			return pickKind(rng, cfg);
		}

		return pickKind(rng, defaultWeights(RAAConfig.active().defaultProfile()));
	}

	private static MaterialKind pickKind(Random rng, Map<MaterialKind, Integer> weights) {
		ArrayList<Map.Entry<MaterialKind, Integer>> ordered = new ArrayList<>();

		for (Map.Entry<MaterialKind, Integer> entry : weights.entrySet()) {
			if (entry.getKey() == null) continue;

			int weight = entry.getValue() == null ? 0 : entry.getValue();
			if (weight > 0) {
				ordered.add(entry(entry.getKey(), weight));
			}
		}

		ordered.sort(Comparator.comparingInt(e -> e.getKey().ordinal()));

		int total = 0;
		for (Map.Entry<MaterialKind, Integer> entry : ordered) {
			total += entry.getValue();
		}

		if (total <= 0) {
			return MaterialKind.OTHER;
		}

		int roll = rng.nextInt(total);
		int acc = 0;

		for (Map.Entry<MaterialKind, Integer> entry : ordered) {
			acc += entry.getValue();

			if (roll < acc) {
				return entry.getKey();
			}
		}

		return MaterialKind.OTHER;
	}

	private static Map<MaterialKind, Integer> loadWeightsFromConfig() {
		Map<MaterialKind, Integer> weights = RAAConfig.active().kindWeights();
		return weights == null || weights.isEmpty() ? null : weights;
	}

	private static Map<MaterialKind, Integer> defaultWeights(Profile profile) {
		return switch (profile) {
			case BALANCED -> Map.ofEntries(
					entry(MaterialKind.METAL, 25),
					entry(MaterialKind.ALLOY, 10),
					entry(MaterialKind.GEM, 12),
					entry(MaterialKind.CRYSTAL, 10),
					entry(MaterialKind.STONE, 12),
					entry(MaterialKind.VOLCANIC, 3),
					entry(MaterialKind.SAND, 8),
					entry(MaterialKind.GRAVEL, 7),
					entry(MaterialKind.CLAY, 5),
					entry(MaterialKind.MUD, 4),
					entry(MaterialKind.SALT, 2),
					entry(MaterialKind.SOIL, 2)
			);

			case TECH_HEAVY -> Map.ofEntries(
					entry(MaterialKind.METAL, 35),
					entry(MaterialKind.ALLOY, 20),
					entry(MaterialKind.GEM, 10),
					entry(MaterialKind.CRYSTAL, 8),
					entry(MaterialKind.STONE, 8),
					entry(MaterialKind.VOLCANIC, 2),
					entry(MaterialKind.SAND, 5),
					entry(MaterialKind.GRAVEL, 4),
					entry(MaterialKind.CLAY, 4),
					entry(MaterialKind.MUD, 2),
					entry(MaterialKind.SALT, 1),
					entry(MaterialKind.SOIL, 1)
			);

			case GEO_HEAVY -> Map.ofEntries(
					entry(MaterialKind.METAL, 15),
					entry(MaterialKind.ALLOY, 5),
					entry(MaterialKind.GEM, 8),
					entry(MaterialKind.CRYSTAL, 7),
					entry(MaterialKind.STONE, 22),
					entry(MaterialKind.VOLCANIC, 5),
					entry(MaterialKind.SAND, 12),
					entry(MaterialKind.GRAVEL, 8),
					entry(MaterialKind.CLAY, 8),
					entry(MaterialKind.MUD, 6),
					entry(MaterialKind.SALT, 2),
					entry(MaterialKind.SOIL, 2)
			);

			case FANTASY_HEAVY -> Map.ofEntries(
					entry(MaterialKind.METAL, 24),
					entry(MaterialKind.ALLOY, 12),
					entry(MaterialKind.GEM, 16),
					entry(MaterialKind.CRYSTAL, 14),
					entry(MaterialKind.STONE, 9),
					entry(MaterialKind.VOLCANIC, 5),
					entry(MaterialKind.SAND, 5),
					entry(MaterialKind.GRAVEL, 4),
					entry(MaterialKind.CLAY, 4),
					entry(MaterialKind.MUD, 3),
					entry(MaterialKind.SALT, 2),
					entry(MaterialKind.SOIL, 2)
			);
		};
	}

	private static float hardnessFor(MaterialKind kind, HarvestTier tier) {
		return switch (kind) {
			case METAL, ALLOY -> switch (tier) {
				case STONE -> 3.0f;
				case IRON -> 4.0f;
				case DIAMOND -> 5.0f;
				case NETHERITE -> 6.0f;
			};

			case GEM -> 5.5f;
			case CRYSTAL -> 4.0f;
			case STONE -> 3.0f;
			case SAND, MUD -> 0.5f;
			case GRAVEL -> 0.8f;
			case CLAY, SOIL -> 0.6f;
			case SALT -> 1.5f;
			case VOLCANIC -> 2.5f;
			case WOOD -> 2.0f;
			case OTHER -> 3.0f;
		};
	}

	private static float blastFor(MaterialKind kind, HarvestTier tier) {
		return switch (kind) {
			case METAL, ALLOY -> 6.0f + tier.ordinal() * 2.0f;
			case GEM -> 8.0f;
			case CRYSTAL -> 5.0f;
			case STONE -> 6.0f;
			case SAND, MUD -> 0.2f;
			case GRAVEL -> 0.4f;
			case CLAY -> 0.5f;
			case SOIL -> 0.3f;
			case SALT -> 1.0f;
			case VOLCANIC -> 4.0f;
			case WOOD -> 3.0f;
			case OTHER -> 3.0f;
		};
	}

	private static float effFor(MaterialKind kind, HarvestTier tier) {
		return switch (kind) {
			case METAL, ALLOY -> 1.0f + tier.ordinal() * 0.25f;
			case GEM -> 1.15f;
			default -> 1.0f;
		};
	}

	private static long mix(long worldSeed, Identifier id) {
		long h = worldSeed ^ 0x9E3779B97F4A7C15L;
		h ^= id.getNamespace().hashCode() * 0x9E3779B97F4A7C15L;
		h ^= id.getPath().hashCode() * 0xBF58476D1CE4E5B9L;

		h ^= h >>> 30;
		h *= 0xBF58476D1CE4E5B9L;
		h ^= h >>> 27;
		h *= 0x94D049BB133111EBL;
		h ^= h >>> 31;

		return h;
	}

	private static long hash(long seed) {
		long h = seed ^ 0x9E3779B97F4A7C15L ^ "raa_materials:materials".hashCode();

		h ^= h >>> 30;
		h *= 0xBF58476D1CE4E5B9L;
		h ^= h >>> 27;
		h *= 0x94D049BB133111EBL;
		h ^= h >>> 31;

		return h;
	}
}
