package net.vampirestudios.raaMaterials.material;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.vampirestudios.raaMaterials.GeoNameGen;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.material.MaterialDef.OreHost;
import net.vampirestudios.raaMaterials.palette.OKLCh;
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

			forms = FormDependencies.resolve(name, forms);
			RAAConfig.BlockStats blockStats = cfg.blockStats();
			float resistance = blastFor(kind, tier) * multiplier(blockStats.blastMul(), kind);
			float efficiency = effFor(kind, tier) * multiplier(blockStats.effMul(), kind);
			SpikeGrowthLiquid spikeGrowth = spikeGrowthFor(kind, rng);

			list.add(new MaterialDef(
					name,
					kind,
					color,
					hardnessFor(kind, tier),
					resistance,
					efficiency,
					tier,
					forms,
					spawn,
					toolSpec.map(spec -> applyEfficiencyMultiplier(spec, multiplier(blockStats.effMul(), kind))),
					mix(worldSeed, name.id()),
					host,
					spikeGrowth
			));
		}

		return new MaterialSet(list);
	}

	private static float multiplier(Map<MaterialKind, Float> multipliers, MaterialKind kind) {
		return Math.max(0.0f, multipliers.getOrDefault(kind, 1.0f));
	}

	private static ToolMaterialSpec applyEfficiencyMultiplier(ToolMaterialSpec spec, float multiplier) {
		if (multiplier == 1.0f) {
			return spec;
		}
		return new ToolMaterialSpec(
				spec.durability(),
				spec.speed() * multiplier,
				spec.attackDamageBonus(),
				spec.enchantmentValue(),
				spec.incorrectBlocksForDropsTag(),
				spec.repairItemsTag(),
				spec.spearSpec()
		);
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

	private static SpikeGrowthLiquid spikeGrowthFor(MaterialKind kind, Random rng) {
		var fc = RAAConfig.active().formChances();
		return switch (kind) {
			case STONE    -> rng.nextFloat() < fc.stoneSpikeGrowthChance()    ? SpikeGrowthLiquid.WATER : SpikeGrowthLiquid.NONE;
			case VOLCANIC -> rng.nextFloat() < fc.volcanicSpikeGrowthChance() ? SpikeGrowthLiquid.LAVA  : SpikeGrowthLiquid.NONE;
			default -> SpikeGrowthLiquid.NONE;
		};
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
			PICKAXE, AXE, SWORD, SHOVEL, HOE, SPEAR
	);

	private static final List<Form> MOUNT_ARMOR = List.of(
			HORSE_ARMOR, WOLF_ARMOR, NAUTILUS_ARMOR
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
			DOOR, TRAPDOOR, BUTTON, PRESSURE_PLATE
	);

	private static @NotNull List<Form> getForms(MaterialKind kind, Random rng) {
		return switch (kind) {
			case METAL -> join(
					List.of(ORE, RAW, DUST, NUGGET, RAW_BLOCK),
					METAL_PARTS,
					METAL_DECOR_BLOCKS,
					METAL_INTERACTIVE_BLOCKS,
					STANDARD_EQUIPMENT_SET,
					MOUNT_ARMOR
			);

			case ALLOY -> join(
					METAL_PARTS,
					METAL_DECOR_BLOCKS,
					METAL_INTERACTIVE_BLOCKS,
					STANDARD_EQUIPMENT_SET,
					MOUNT_ARMOR
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
					ROD_BLOCK
			);

			case STONE -> {
				var stoneForms = new ArrayList<>(join(
						List.of(
								BLOCK, CHISELED, POLISHED, BRICKS, PILLAR, TILES,
								MOSAIC, MOSSY, CRACKED, COBBLED
						),
						BLOCK_SHAPES,
						BASIC_INTERACTIVE_BLOCKS,
						List.of(LAMP)
				));
				if (rng.nextFloat() < RAAConfig.active().formChances().stoneSpikeChance()) stoneForms.add(SPIKE);
				yield List.copyOf(stoneForms);
			}

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

			case VOLCANIC -> {
				var volcanicForms = new ArrayList<>(join(
						List.of(BLOCK, COBBLED, POLISHED, BRICKS, PILLAR, MOSSY),
						BASIC_INTERACTIVE_BLOCKS,
						List.of(LAMP)
				));
				if (rng.nextFloat() < RAAConfig.active().formChances().volcanicSpikeChance()) volcanicForms.add(SPIKE);
				yield List.copyOf(volcanicForms);
			}

			case WOOD -> join(
					WOOD_NATURAL,
					WOOD_PROCESSED,
					WOOD_BUILDING
			);

			case OTHER -> List.of(DUST);
		};
	}

	private static @NotNull List<Form> sandForms(Random rng) {
		var fc = RAAConfig.active().formChances();
		var forms = new ArrayList<>(List.of(BLOCK, SANDSTONE, CUT, CHISELED, SMOOTH, SLAB, STAIRS, WALL));
		if (rng.nextFloat() < fc.sandGlassChance()) {
			forms.add(GLASS);
			if (rng.nextFloat() < fc.sandTintedGlassChance()) {
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
		RAAConfig.ColorRanges cr = RAAConfig.active().colorRanges()
				.getOrDefault(kind, new RAAConfig.ColorRanges(0.45f, 0.80f, 0.12f, 0.30f));
		return switch (kind) {
			case METAL, ALLOY -> randomMetalColor(rng, cr);
			case GEM           -> randomGemColor(rng, cr);
			case CRYSTAL       -> randomCrystalColor(rng, cr);
			case SAND          -> randomSandColor(rng, cr);
			case GRAVEL        -> randomGravelColor(rng, cr);
			case STONE         -> randomStoneColor(rng, cr);
			case CLAY, MUD, SOIL -> randomEarthColor(rng, cr);
			case SALT          -> randomSaltColor(rng, cr);
			case VOLCANIC      -> randomVolcanicColor(rng, cr);
			case WOOD          -> randomWoodColor(rng, cr);
			default            -> randomColor(rng, cr);
		};
	}

	// OKLCh hue landmarks (radians): red≈0.09, orange≈0.75, yellow≈1.4,
	// green≈2.3, teal≈2.7, cyan≈3.1, blue≈4.1, violet≈4.9, magenta≈5.5

	private static int randomMetalColor(Random rng, RAAConfig.ColorRanges cr) {
		// Biased toward real-world metal hues; exotic tones occasionally
		float h = pickFloat(rng, 0.10f, 0.75f, 0.95f, 1.35f, 4.10f, 4.80f, 0.10f, 1.35f);
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()), h);
	}

	private static int randomGemColor(Random rng, RAAConfig.ColorRanges cr) {
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()),
		             rng.nextFloat() * 6.28f);
	}

	private static int randomCrystalColor(Random rng, RAAConfig.ColorRanges cr) {
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()),
		             rng.nextFloat() * 6.28f);
	}

	private static int randomStoneColor(Random rng, RAAConfig.ColorRanges cr) {
		float h = 0.60f + rng.nextFloat() * 0.80f; // warm neutrals
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()), h);
	}

	private static int randomEarthColor(Random rng, RAAConfig.ColorRanges cr) {
		float h = 0.50f + rng.nextFloat() * 0.70f; // orange-brown
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()), h);
	}

	private static int randomSandColor(Random rng, RAAConfig.ColorRanges cr) {
		float h = 0.65f + rng.nextFloat() * 0.65f; // orange to yellow
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()), h);
	}

	private static int randomGravelColor(Random rng, RAAConfig.ColorRanges cr) {
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()),
		             rng.nextFloat() * 6.28f);
	}

	private static int randomSaltColor(Random rng, RAAConfig.ColorRanges cr) {
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()),
		             rng.nextFloat() * 6.28f);
	}

	private static int randomVolcanicColor(Random rng, RAAConfig.ColorRanges cr) {
		float h = pickFloat(rng, 0.09f, 0.20f, 0.75f, 0.09f, 0.09f); // biased toward reds
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()), h);
	}

	private static int randomWoodColor(Random rng, RAAConfig.ColorRanges cr) {
		float h = 0.70f + rng.nextFloat() * 0.40f; // orange-brown
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()), h);
	}

	private static int randomColor(Random rng, RAAConfig.ColorRanges cr) {
		return oklch(lerp(cr.lMin(), cr.lMax(), rng.nextFloat()),
		             lerp(cr.cMin(), cr.cMax(), rng.nextFloat()),
		             rng.nextFloat() * 6.28f);
	}

	private static float lerp(float a, float b, float t) { return a + (b - a) * t; }

	/** Convert OKLCh to sRGB int. h is in radians. */
	private static int oklch(float L, float C, float h) {
		float A = (float) (Math.cos(h) * C);
		float B = (float) (Math.sin(h) * C);
		return OKLCh.oklabToSRGB(L, A, B) & 0xFFFFFF;
	}

	private static float pickFloat(Random rng, float... values) {
		return values[rng.nextInt(values.length)];
	}

	private static HarvestTier pickTierFor(MaterialKind kind, Random rng) {
		return PROFILES.get(kind).pickTier(rng);
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
		return PROFILES.get(kind).hardness(tier);
	}

	private static float blastFor(MaterialKind kind, HarvestTier tier) {
		return PROFILES.get(kind).blast(tier);
	}

	private static float effFor(MaterialKind kind, HarvestTier tier) {
		return PROFILES.get(kind).eff(tier);
	}

	// ---- Per-kind profile table ----

	private record MaterialKindProfile(
			float hardnessBase, float hardnessTierStep,
			float blastBase,    float blastTierStep,
			float effBase,      float effTierStep,
			int[] tierWeights   // {stone, iron, diamond, netherite}
	) {
		float hardness(HarvestTier tier) { return hardnessBase + hardnessTierStep * tier.ordinal(); }
		float blast(HarvestTier tier)    { return blastBase    + blastTierStep    * tier.ordinal(); }
		float eff(HarvestTier tier)      { return effBase      + effTierStep      * tier.ordinal(); }

		HarvestTier pickTier(Random rng) {
			int total = 0;
			for (int w : tierWeights) total += w;
			if (total == 0) return HarvestTier.IRON;
			int roll = rng.nextInt(total);
			int acc = 0;
			HarvestTier[] tiers = HarvestTier.values();
			for (int i = 0; i < tierWeights.length && i < tiers.length; i++) {
				acc += tierWeights[i];
				if (roll < acc) return tiers[i];
			}
			return HarvestTier.IRON;
		}
	}

	// Tier weight shorthand: {stone, iron, diamond, netherite}
	private static final int[] TIER_METAL  = {40, 40, 15,  5};
	private static final int[] TIER_GEM    = {15, 50, 30,  5};
	private static final int[] TIER_IRON   = { 0,100,  0,  0};

	private static final Map<MaterialKind, MaterialKindProfile> PROFILES;
	static {
		var p = new EnumMap<MaterialKind, MaterialKindProfile>(MaterialKind.class);
		//                       hardBase  hardStep  blastBase  blastStep  effBase  effStep  tierWeights
		p.put(MaterialKind.METAL,    new MaterialKindProfile(3.0f, 1.0f,  6.0f, 2.0f,  1.00f, 0.25f, TIER_METAL));
		p.put(MaterialKind.ALLOY,    new MaterialKindProfile(3.0f, 1.0f,  6.0f, 2.0f,  1.00f, 0.25f, TIER_METAL));
		p.put(MaterialKind.GEM,      new MaterialKindProfile(5.5f, 0.0f,  8.0f, 0.0f,  1.15f, 0.00f, TIER_GEM));
		p.put(MaterialKind.CRYSTAL,  new MaterialKindProfile(4.0f, 0.0f,  5.0f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.STONE,    new MaterialKindProfile(3.0f, 0.0f,  6.0f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.SAND,     new MaterialKindProfile(0.5f, 0.0f,  0.2f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.GRAVEL,   new MaterialKindProfile(0.8f, 0.0f,  0.4f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.CLAY,     new MaterialKindProfile(0.6f, 0.0f,  0.5f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.MUD,      new MaterialKindProfile(0.5f, 0.0f,  0.2f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.SOIL,     new MaterialKindProfile(0.6f, 0.0f,  0.3f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.SALT,     new MaterialKindProfile(1.5f, 0.0f,  1.0f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.VOLCANIC, new MaterialKindProfile(2.5f, 0.0f,  4.0f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.WOOD,     new MaterialKindProfile(2.0f, 0.0f,  3.0f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		p.put(MaterialKind.OTHER,    new MaterialKindProfile(3.0f, 0.0f,  3.0f, 0.0f,  1.00f, 0.00f, TIER_IRON));
		PROFILES = Collections.unmodifiableMap(p);
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
