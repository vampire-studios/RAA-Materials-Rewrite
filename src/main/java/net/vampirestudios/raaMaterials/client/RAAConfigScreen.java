package net.vampirestudios.raaMaterials.client;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.vampirestudios.raaMaterials.FormGroupConfig;
import net.vampirestudios.raaMaterials.FormGroup;
import net.vampirestudios.raaMaterials.KindPolicy;
import net.vampirestudios.raaMaterials.RAAConfig;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialGenerator;
import net.vampirestudios.raaMaterials.material.MaterialKind;
import net.vampirestudios.raaMaterials.material.SpawnInfo;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class RAAConfigScreen {
	private RAAConfigScreen() {
	}

	public static Screen create(Screen parent) {
		var draft = new Draft(RAAConfig.active());
		var formGroupDraft = formGroupDraft();
		return YetAnotherConfigLib.createBuilder()
				.title(text("RAA Materials"))
				.category(general(draft))
				.category(materials(draft))
				.category(spawning(draft))
				.category(worldgen(draft))
				.category(blockStats(draft))
				.category(formChances(draft))
				.category(colorRanges(draft))
				.category(forms(draft))
				.category(formGroups(formGroupDraft))
				.category(openEnded(draft))
				.save(() -> {
					RAAConfig.replaceAndSave(draft.toConfig());
					FormGroupConfig.replaceAndSave(formGroupDraft);
				})
				.build()
				.generateScreen(parent);
	}

	private static ConfigCategory general(Draft draft) {
		return ConfigCategory.createBuilder()
				.name(text("General"))
				.group(OptionGroup.createBuilder()
						.name(text("Generation"))
						.option(intField("Materials minimum", 0, 512, () -> draft.materialsMin, v -> draft.materialsMin = v))
						.option(intField("Materials maximum", 0, 512, () -> draft.materialsMax, v -> draft.materialsMax = v))
						.option(intSlider("Tool chance percent", 0, 100, 1, () -> draft.toolChancePercent, v -> draft.toolChancePercent = v))
						.option(enumOption("Default profile", MaterialGenerator.Profile.class, () -> draft.defaultProfile, v -> draft.defaultProfile = v))
						.build())
				.group(OptionGroup.createBuilder()
						.name(text("Tier weights"))
						.option(intField("Stone", 0, 10_000, () -> draft.tiersStone, v -> draft.tiersStone = v))
						.option(intField("Iron", 0, 10_000, () -> draft.tiersIron, v -> draft.tiersIron = v))
						.option(intField("Diamond", 0, 10_000, () -> draft.tiersDiamond, v -> draft.tiersDiamond = v))
						.option(intField("Netherite", 0, 10_000, () -> draft.tiersNetherite, v -> draft.tiersNetherite = v))
						.build())
				.group(OptionGroup.createBuilder()
						.name(text("Name generation"))
						.option(bool("Use color prefixes", () -> draft.useColorPrefixes, v -> draft.useColorPrefixes = v))
						.option(bool("Use biome bias", () -> draft.useBiomeBias, v -> draft.useBiomeBias = v))
						.option(bool("Use replaceable bias", () -> draft.useReplaceableBias, v -> draft.useReplaceableBias = v))
						.option(intField("Hash length", 0, 32, () -> draft.hashLen, v -> draft.hashLen = v))
						.build())
				.group(stringList("Banned names", () -> draft.banned, v -> draft.banned = v))
				.build();
	}

	private static ConfigCategory materials(Draft draft) {
		var weights = OptionGroup.createBuilder().name(text("Kind weights"));
		for (var kind : MaterialKind.values()) {
			weights.option(intField(title(kind), 0, 10_000, () -> draft.kindWeights.getOrDefault(kind, 0), v -> draft.kindWeights.put(kind, v)));
		}
		return ConfigCategory.createBuilder()
				.name(text("Materials"))
				.group(weights.build())
				.build();
	}

	private static ConfigCategory spawning(Draft draft) {
		var category = ConfigCategory.createBuilder().name(text("Spawning"));
		for (var kind : MaterialKind.values()) {
			category.group(OptionGroup.createBuilder()
					.name(text(title(kind)))
					.collapsed(true)
					.option(intField("Ore mode weight", 0, 10_000, () -> draft.mode(kind).ore, v -> draft.mode(kind).ore = v))
					.option(intField("Geode mode weight", 0, 10_000, () -> draft.mode(kind).geode, v -> draft.mode(kind).geode = v))
					.option(intField("Cluster mode weight", 0, 10_000, () -> draft.mode(kind).cluster, v -> draft.mode(kind).cluster = v))
					.option(intField("Shallow depth weight", 0, 10_000, () -> draft.depth(kind).shallow, v -> draft.depth(kind).shallow = v))
					.option(intField("Mid depth weight", 0, 10_000, () -> draft.depth(kind).mid, v -> draft.depth(kind).mid = v))
					.option(intField("Deep depth weight", 0, 10_000, () -> draft.depth(kind).deep, v -> draft.depth(kind).deep = v))
					.build());
		}
		return category.build();
	}

	private static ConfigCategory blockStats(Draft draft) {
		var category = ConfigCategory.createBuilder().name(text("Block stats"));
		for (var kind : MaterialKind.values()) {
			category.group(OptionGroup.createBuilder()
					.name(text(title(kind)))
					.collapsed(true)
					.option(floatField("Hardness multiplier", 0.0F, 100.0F, () -> draft.hardnessMul.getOrDefault(kind, 1.0F), v -> draft.hardnessMul.put(kind, v)))
					.option(floatField("Blast multiplier", 0.0F, 100.0F, () -> draft.blastMul.getOrDefault(kind, 1.0F), v -> draft.blastMul.put(kind, v)))
					.option(floatField("Efficiency multiplier", 0.0F, 100.0F, () -> draft.effMul.getOrDefault(kind, 1.0F), v -> draft.effMul.put(kind, v)))
					.build());
		}
		return category.build();
	}

	private static ConfigCategory worldgen(Draft draft) {
		var category = ConfigCategory.createBuilder().name(text("Worldgen profiles"));
		for (var kind : MaterialKind.values()) {
			category.group(OptionGroup.createBuilder()
					.name(text(title(kind)))
					.collapsed(true)
					.option(intField("Attempts minimum", 0, 512, () -> draft.spawnProfile(kind).attemptsMin, v -> draft.spawnProfile(kind).attemptsMin = v))
					.option(intField("Attempts maximum", 0, 512, () -> draft.spawnProfile(kind).attemptsMax, v -> draft.spawnProfile(kind).attemptsMax = v))
					.option(floatField("Success chance", 0.0F, 1.0F, () -> draft.spawnProfile(kind).successChance, v -> draft.spawnProfile(kind).successChance = v))
					.option(intField("Vein minimum", 0, 256, () -> draft.spawnProfile(kind).veinMin, v -> draft.spawnProfile(kind).veinMin = v))
					.option(intField("Vein maximum", 0, 256, () -> draft.spawnProfile(kind).veinMax, v -> draft.spawnProfile(kind).veinMax = v))
					.option(enumOption("Shape", SpawnInfo.VeinShape.class, () -> draft.spawnProfile(kind).shape, v -> draft.spawnProfile(kind).shape = v))
					.option(intField("Y minimum", -512, 512, () -> draft.spawnProfile(kind).minY, v -> draft.spawnProfile(kind).minY = v))
					.option(intField("Y maximum", -512, 512, () -> draft.spawnProfile(kind).maxY, v -> draft.spawnProfile(kind).maxY = v))
					.option(intField("Center minimum", -512, 512, () -> draft.spawnProfile(kind).centerMin, v -> draft.spawnProfile(kind).centerMin = v))
					.option(intField("Center maximum", -512, 512, () -> draft.spawnProfile(kind).centerMax, v -> draft.spawnProfile(kind).centerMax = v))
					.option(intField("Spread minimum", 0, 512, () -> draft.spawnProfile(kind).spreadMin, v -> draft.spawnProfile(kind).spreadMin = v))
					.option(intField("Spread maximum", 0, 512, () -> draft.spawnProfile(kind).spreadMax, v -> draft.spawnProfile(kind).spreadMax = v))
					.option(floatField("Region frequency", 0.0F, 1.0F, () -> draft.spawnProfile(kind).regionFrequency, v -> draft.spawnProfile(kind).regionFrequency = v))
					.option(floatField("Region threshold", -1.0F, 1.0F, () -> draft.spawnProfile(kind).regionThreshold, v -> draft.spawnProfile(kind).regionThreshold = v))
					.option(floatField("Pocket frequency", 0.0F, 1.0F, () -> draft.spawnProfile(kind).pocketFrequency, v -> draft.spawnProfile(kind).pocketFrequency = v))
					.option(floatField("Pocket threshold", -1.0F, 1.0F, () -> draft.spawnProfile(kind).pocketThreshold, v -> draft.spawnProfile(kind).pocketThreshold = v))
					.option(floatField("Must touch air chance", 0.0F, 1.0F, () -> draft.spawnProfile(kind).mustTouchAirChance, v -> draft.spawnProfile(kind).mustTouchAirChance = v))
					.option(floatField("Near water chance", 0.0F, 1.0F, () -> draft.spawnProfile(kind).nearWaterChance, v -> draft.spawnProfile(kind).nearWaterChance = v))
					.option(floatField("Near lava chance", 0.0F, 1.0F, () -> draft.spawnProfile(kind).nearLavaChance, v -> draft.spawnProfile(kind).nearLavaChance = v))
					.build());
		}
		return category.build();
	}

	private static ConfigCategory formChances(Draft draft) {
		var fc = draft.formChances;
		return ConfigCategory.createBuilder()
				.name(text("Form chances"))
				.group(OptionGroup.createBuilder()
						.name(text("Spike forms"))
						.option(floatField("Stone spike chance", 0.0F, 1.0F, () -> fc.stoneSpikeChance, v -> fc.stoneSpikeChance = v))
						.option(floatField("Volcanic spike chance", 0.0F, 1.0F, () -> fc.volcanicSpikeChance, v -> fc.volcanicSpikeChance = v))
						.option(floatField("Stone spike growth chance", 0.0F, 1.0F, () -> fc.stoneSpikeGrowthChance, v -> fc.stoneSpikeGrowthChance = v))
						.option(floatField("Volcanic spike growth chance", 0.0F, 1.0F, () -> fc.volcanicSpikeGrowthChance, v -> fc.volcanicSpikeGrowthChance = v))
						.build())
				.group(OptionGroup.createBuilder()
						.name(text("Sand forms"))
						.option(floatField("Sand glass chance", 0.0F, 1.0F, () -> fc.sandGlassChance, v -> fc.sandGlassChance = v))
						.option(floatField("Sand tinted glass chance", 0.0F, 1.0F, () -> fc.sandTintedGlassChance, v -> fc.sandTintedGlassChance = v))
						.build())
				.group(OptionGroup.createBuilder()
						.name(text("Spike worldgen"))
						.option(floatField("Spike worldgen chance", 0.0F, 1.0F, () -> fc.spikeWorldgenChance, v -> fc.spikeWorldgenChance = v))
						.option(intField("Spike max height", 1, 16, () -> fc.spikeMaxHeight, v -> fc.spikeMaxHeight = v))
						.build())
				.group(OptionGroup.createBuilder()
						.name(text("Crystal worldgen"))
						.option(floatField("Crystal cluster fill chance", 0.0F, 1.0F, () -> fc.crystalClusterFillChance, v -> fc.crystalClusterFillChance = v))
						.option(floatField("Geode crystal wall chance", 0.0F, 1.0F, () -> fc.geodeCrystalWallChance, v -> fc.geodeCrystalWallChance = v))
						.option(floatField("Geode crystal cluster chance", 0.0F, 1.0F, () -> fc.geodeCrystalClusterChance, v -> fc.geodeCrystalClusterChance = v))
						.build())
				.group(OptionGroup.createBuilder()
						.name(text("Hammer"))
						.option(intSlider("Hammer AoE radius", 0, 4, 1, () -> fc.hammerAoeRadius, v -> fc.hammerAoeRadius = v))
						.build())
				.build();
	}

	private static ConfigCategory colorRanges(Draft draft) {
		var category = ConfigCategory.createBuilder().name(text("Color ranges"));
		for (var kind : MaterialKind.values()) {
			category.group(OptionGroup.createBuilder()
					.name(text(title(kind)))
					.collapsed(true)
					.option(floatField("Lightness minimum", 0.0F, 1.0F, () -> draft.colorRange(kind).lMin, v -> draft.colorRange(kind).lMin = v))
					.option(floatField("Lightness maximum", 0.0F, 1.0F, () -> draft.colorRange(kind).lMax, v -> draft.colorRange(kind).lMax = v))
					.option(floatField("Chroma minimum", 0.0F, 1.0F, () -> draft.colorRange(kind).cMin, v -> draft.colorRange(kind).cMin = v))
					.option(floatField("Chroma maximum", 0.0F, 1.0F, () -> draft.colorRange(kind).cMax, v -> draft.colorRange(kind).cMax = v))
					.build());
		}
		return category.build();
	}

	private static ConfigCategory forms(Draft draft) {
		var category = ConfigCategory.createBuilder().name(text("Forms"));
		for (var kind : MaterialKind.values()) {
			category.group(OptionGroup.createBuilder()
					.name(text(title(kind)))
					.collapsed(true)
					.option(intField("Max shape forms", -1, 10_000, () -> draft.policy(kind).maxShapeForms, v -> draft.policy(kind).maxShapeForms = v))
					.build());
			category.group(formGroupList(title(kind) + " groups plus", () -> draft.policy(kind).groupsPlus, v -> draft.policy(kind).groupsPlus = v));
			category.group(formGroupList(title(kind) + " groups minus", () -> draft.policy(kind).groupsMinus, v -> draft.policy(kind).groupsMinus = v));
			category.group(formList(title(kind) + " add forms", () -> draft.policy(kind).add, v -> draft.policy(kind).add = v));
			category.group(formList(title(kind) + " remove forms", () -> draft.policy(kind).remove, v -> draft.policy(kind).remove = v));
		}
		return category.build();
	}

	private static ConfigCategory formGroups(EnumMap<FormGroup, List<Form>> formGroupDraft) {
		var category = ConfigCategory.createBuilder().name(text("Form groups"));
		for (var group : FormGroup.values()) {
			category.group(formList(title(group), () -> formGroupDraft.get(group), v -> formGroupDraft.put(group, v)));
		}
		return category.build();
	}

	private static ConfigCategory openEnded(Draft draft) {
		return ConfigCategory.createBuilder()
				.name(text("Maps"))
				.group(stringList("Shallow ranges", () -> draft.shallowRangeRows, v -> draft.shallowRangeRows = v))
				.group(stringList("Mid ranges", () -> draft.midRangeRows, v -> draft.midRangeRows = v))
				.group(stringList("Deep ranges", () -> draft.deepRangeRows, v -> draft.deepRangeRows = v))
				.group(stringList("Replaceables", () -> draft.replaceableRows, v -> draft.replaceableRows = v))
				.build();
	}

	private static Option<Integer> intField(String name, int min, int max, Supplier<Integer> getter, Consumer<Integer> setter) {
		return Option.<Integer>createBuilder()
				.name(text(name))
				.binding(getter.get(), getter, setter)
				.controller(opt -> IntegerFieldControllerBuilder.create(opt).range(min, max))
				.build();
	}

	private static Option<Integer> intSlider(String name, int min, int max, int step, Supplier<Integer> getter, Consumer<Integer> setter) {
		return Option.<Integer>createBuilder()
				.name(text(name))
				.binding(getter.get(), getter, setter)
				.controller(opt -> IntegerSliderControllerBuilder.create(opt).range(min, max).step(step))
				.build();
	}

	private static Option<Float> floatField(String name, float min, float max, Supplier<Float> getter, Consumer<Float> setter) {
		return Option.<Float>createBuilder()
				.name(text(name))
				.binding(getter.get(), getter, setter)
				.controller(opt -> FloatFieldControllerBuilder.create(opt).range(min, max))
				.build();
	}

	private static Option<Boolean> bool(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		return Option.<Boolean>createBuilder()
				.name(text(name))
				.binding(getter.get(), getter, setter)
				.controller(TickBoxControllerBuilder::create)
				.build();
	}

	private static <T extends Enum<T>> Option<T> enumOption(String name, Class<T> type, Supplier<T> getter, Consumer<T> setter) {
		return Option.<T>createBuilder(type)
				.name(text(name))
				.binding(getter.get(), getter, setter)
				.controller(opt -> EnumControllerBuilder.create(opt).enumClass(type).valueFormatter(value -> text(title(value))))
				.build();
	}

	private static ListOption<String> stringList(String name, Supplier<List<String>> getter, Consumer<List<String>> setter) {
		return ListOption.<String>createBuilder(String.class)
				.name(text(name))
				.binding(List.copyOf(getter.get()), getter, value -> setter.accept(new ArrayList<>(value)))
				.initial("")
				.controller(StringControllerBuilder::create)
				.collapsed(true)
				.build();
	}

	private static ListOption<FormGroup> formGroupList(String name, Supplier<List<FormGroup>> getter, Consumer<List<FormGroup>> setter) {
		return ListOption.<FormGroup>createBuilder(FormGroup.class)
				.name(text(name))
				.binding(List.copyOf(getter.get()), getter, value -> setter.accept(new ArrayList<>(value)))
				.initial(FormGroup.TOOLS)
				.controller(opt -> EnumControllerBuilder.create(opt).enumClass(FormGroup.class).valueFormatter(value -> text(title(value))))
				.collapsed(true)
				.build();
	}

	private static ListOption<Form> formList(String name, Supplier<List<Form>> getter, Consumer<List<Form>> setter) {
		return ListOption.<Form>createBuilder(Form.class)
				.name(text(name))
				.binding(List.copyOf(getter.get()), getter, value -> setter.accept(new ArrayList<>(value)))
				.initial(Form.INGOT)
				.controller(opt -> EnumControllerBuilder.create(opt).enumClass(Form.class).valueFormatter(value -> text(title(value))))
				.collapsed(true)
				.build();
	}

	private static Component text(String value) {
		return Component.literal(value);
	}

	private static String title(Enum<?> value) {
		return title(value.name());
	}

	private static String title(String value) {
		var words = value.toLowerCase(Locale.ROOT).split("_");
		var out = new StringBuilder();
		for (var word : words) {
			if (word.isEmpty()) continue;
			if (!out.isEmpty()) out.append(' ');
			out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
		}
		return out.toString();
	}

	private static List<String> rangeRows(Map<String, RAAConfig.YRange> map) {
		var rows = new ArrayList<String>();
		map.forEach((key, range) -> rows.add(key + "=" + range.minY() + "," + range.maxY() + "," + range.peakMin() + "," + range.peakMax()));
		return rows;
	}

	private static Map<String, RAAConfig.YRange> parseRanges(List<String> rows) {
		var map = new LinkedHashMap<String, RAAConfig.YRange>();
		for (var row : rows) {
			var split = row.split("=", 2);
			if (split.length != 2 || split[0].isBlank()) continue;
			var parts = split[1].split(",");
			if (parts.length != 4) continue;
			try {
				map.put(split[0].trim(), new RAAConfig.YRange(
						Integer.parseInt(parts[0].trim()),
						Integer.parseInt(parts[1].trim()),
						Integer.parseInt(parts[2].trim()),
						Integer.parseInt(parts[3].trim())
				));
			} catch (NumberFormatException ignored) {
			}
		}
		return map;
	}

	private static List<String> replaceableRows(Map<String, List<String>> map) {
		var rows = new ArrayList<String>();
		map.forEach((key, values) -> rows.add(key + "=" + String.join(",", values)));
		return rows;
	}

	private static Map<String, List<String>> parseReplaceables(List<String> rows) {
		var map = new LinkedHashMap<String, List<String>>();
		for (var row : rows) {
			var split = row.split("=", 2);
			if (split.length != 2 || split[0].isBlank()) continue;
			var values = new ArrayList<String>();
			for (var value : split[1].split(",")) {
				if (!value.isBlank()) values.add(value.trim());
			}
			map.put(split[0].trim(), values);
		}
		return map;
	}

	private static EnumMap<FormGroup, List<Form>> formGroupDraft() {
		var map = new EnumMap<FormGroup, List<Form>>(FormGroup.class);
		var defaults = FormGroupConfig.defaults();
		var active = FormGroupConfig.active();
		for (var group : FormGroup.values()) {
			map.put(group, new ArrayList<>(active.getOrDefault(group, defaults.getOrDefault(group, List.of()))));
		}
		return map;
	}

	private static final class Draft {
		int materialsMin;
		int materialsMax;
		EnumMap<MaterialKind, Integer> kindWeights;
		int tiersStone;
		int tiersIron;
		int tiersDiamond;
		int tiersNetherite;
		EnumMap<MaterialKind, MutableModeWeights> spawnMode;
		EnumMap<MaterialKind, MutableDepthBand> depthWeights;
		EnumMap<MaterialKind, Float> hardnessMul;
		EnumMap<MaterialKind, Float> blastMul;
		EnumMap<MaterialKind, Float> effMul;
		EnumMap<MaterialKind, MutableSpawnProfile> spawnProfiles;
		List<String> shallowRangeRows;
		List<String> midRangeRows;
		List<String> deepRangeRows;
		List<String> replaceableRows;
		boolean useColorPrefixes;
		boolean useBiomeBias;
		boolean useReplaceableBias;
		int hashLen;
		List<String> banned;
		EnumMap<MaterialKind, MutablePolicy> formControls;
		int toolChancePercent;
		MaterialGenerator.Profile defaultProfile;
		EnumMap<MaterialKind, MutableColorRanges> colorRanges;
		MutableFormChances formChances;

		Draft(RAAConfig config) {
			var defaults = RAAConfig.defaults();
			materialsMin = config.materialsMin();
			materialsMax = config.materialsMax();
			kindWeights = enumMap(config.kindWeights(), defaults.kindWeights(), 0);
			tiersStone = config.tiers().stone();
			tiersIron = config.tiers().iron();
			tiersDiamond = config.tiers().diamond();
			tiersNetherite = config.tiers().netherite();
			spawnMode = new EnumMap<>(MaterialKind.class);
			depthWeights = new EnumMap<>(MaterialKind.class);
			for (var kind : MaterialKind.values()) {
				var mode = config.spawnMode().getOrDefault(kind, defaults.spawnMode().getOrDefault(kind, new RAAConfig.ModeWeights()));
				spawnMode.put(kind, new MutableModeWeights(mode));
				var depth = config.depthWeights().getOrDefault(kind, defaults.depthWeights().getOrDefault(kind, new RAAConfig.DepthBand()));
				depthWeights.put(kind, new MutableDepthBand(depth));
			}
			hardnessMul = enumMap(config.blockStats().hardnessMul(), defaults.blockStats().hardnessMul(), 1.0F);
			blastMul = enumMap(config.blockStats().blastMul(), defaults.blockStats().blastMul(), 1.0F);
			effMul = enumMap(config.blockStats().effMul(), defaults.blockStats().effMul(), 1.0F);
			spawnProfiles = new EnumMap<>(MaterialKind.class);
			for (var kind : MaterialKind.values()) {
				var profile = config.spawnProfiles().getOrDefault(kind, defaults.spawnProfiles().get(kind));
				spawnProfiles.put(kind, new MutableSpawnProfile(profile));
			}
			shallowRangeRows = rangeRows(config.shallowRange());
			midRangeRows = rangeRows(config.midRange());
			deepRangeRows = rangeRows(config.deepRange());
			replaceableRows = replaceableRows(config.replaceables());
			useColorPrefixes = config.nameGen().useColorPrefixes();
			useBiomeBias = config.nameGen().useBiomeBias();
			useReplaceableBias = config.nameGen().useReplaceableBias();
			hashLen = config.nameGen().hashLen();
			banned = new ArrayList<>(config.nameGen().banned());
			formControls = new EnumMap<>(MaterialKind.class);
			for (var kind : MaterialKind.values()) {
				var policy = config.formControls().getOrDefault(kind, defaults.formControls().getOrDefault(kind, new KindPolicy(List.of(), List.of(), List.of(), List.of(), -1)));
				formControls.put(kind, new MutablePolicy(policy));
			}
			toolChancePercent = config.toolChancePercent();
			defaultProfile = config.defaultProfile();
			colorRanges = new EnumMap<>(MaterialKind.class);
			var defaultCr = RAAConfig.defaultColorRanges();
			for (var kind : MaterialKind.values()) {
				var cr = config.colorRanges().getOrDefault(kind, defaultCr.getOrDefault(kind, new RAAConfig.ColorRanges(0.4f, 0.8f, 0.05f, 0.2f)));
				colorRanges.put(kind, new MutableColorRanges(cr));
			}
			formChances = new MutableFormChances(config.formChances());
		}

		MutableModeWeights mode(MaterialKind kind) {
			return spawnMode.get(kind);
		}

		MutableDepthBand depth(MaterialKind kind) {
			return depthWeights.get(kind);
		}

		MutablePolicy policy(MaterialKind kind) {
			return formControls.get(kind);
		}

		MutableSpawnProfile spawnProfile(MaterialKind kind) {
			return spawnProfiles.get(kind);
		}

		MutableColorRanges colorRange(MaterialKind kind) {
			return colorRanges.get(kind);
		}

		RAAConfig toConfig() {
			return new RAAConfig(
					materialsMin,
					materialsMax,
					kindWeights,
					new RAAConfig.TierWeights(tiersStone, tiersIron, tiersDiamond, tiersNetherite),
					modeMap(),
					new RAAConfig.BlockStats(hardnessMul, blastMul, effMul),
					depthMap(),
					new RAAConfig.DepthYRanges(
							parseRanges(shallowRangeRows),
							parseRanges(midRangeRows),
							parseRanges(deepRangeRows)
					),
					parseReplaceables(replaceableRows),
					new RAAConfig.NameGen(useColorPrefixes, useBiomeBias, useReplaceableBias, hashLen, banned),
					policyMap(),
					toolChancePercent,
					spawnProfileMap(),
					defaultProfile,
					colorRangeMap(),
					formChances.toRecord()
			);
		}

		private Map<MaterialKind, RAAConfig.ModeWeights> modeMap() {
			var map = new EnumMap<MaterialKind, RAAConfig.ModeWeights>(MaterialKind.class);
			spawnMode.forEach((kind, weights) -> map.put(kind, weights.toRecord()));
			return map;
		}

		private Map<MaterialKind, RAAConfig.DepthBand> depthMap() {
			var map = new EnumMap<MaterialKind, RAAConfig.DepthBand>(MaterialKind.class);
			depthWeights.forEach((kind, weights) -> map.put(kind, weights.toRecord()));
			return map;
		}

		private Map<MaterialKind, KindPolicy> policyMap() {
			var map = new EnumMap<MaterialKind, KindPolicy>(MaterialKind.class);
			formControls.forEach((kind, policy) -> map.put(kind, policy.toRecord()));
			return map;
		}

		private Map<MaterialKind, RAAConfig.SpawnProfile> spawnProfileMap() {
			var map = new EnumMap<MaterialKind, RAAConfig.SpawnProfile>(MaterialKind.class);
			spawnProfiles.forEach((kind, profile) -> map.put(kind, profile.toRecord()));
			return map;
		}

		private Map<MaterialKind, RAAConfig.ColorRanges> colorRangeMap() {
			var map = new EnumMap<MaterialKind, RAAConfig.ColorRanges>(MaterialKind.class);
			colorRanges.forEach((kind, cr) -> map.put(kind, cr.toRecord()));
			return map;
		}
	}

	private static <V> EnumMap<MaterialKind, V> enumMap(Map<MaterialKind, V> values, Map<MaterialKind, V> defaults, V fallback) {
		var map = new EnumMap<MaterialKind, V>(MaterialKind.class);
		for (var kind : MaterialKind.values()) {
			map.put(kind, values.getOrDefault(kind, defaults.getOrDefault(kind, fallback)));
		}
		return map;
	}

	private static final class MutableModeWeights {
		int ore;
		int geode;
		int cluster;

		MutableModeWeights(RAAConfig.ModeWeights weights) {
			ore = weights.ore();
			geode = weights.geode();
			cluster = weights.cluster();
		}

		RAAConfig.ModeWeights toRecord() {
			return new RAAConfig.ModeWeights(ore, geode, cluster);
		}
	}

	private static final class MutableDepthBand {
		int shallow;
		int mid;
		int deep;

		MutableDepthBand(RAAConfig.DepthBand weights) {
			shallow = weights.shallow();
			mid = weights.mid();
			deep = weights.deep();
		}

		RAAConfig.DepthBand toRecord() {
			return new RAAConfig.DepthBand(shallow, mid, deep);
		}
	}

	private static final class MutablePolicy {
		List<FormGroup> groupsPlus;
		List<FormGroup> groupsMinus;
		List<Form> add;
		List<Form> remove;
		int maxShapeForms;

		MutablePolicy(KindPolicy policy) {
			groupsPlus = new ArrayList<>(policy.groupsPlus());
			groupsMinus = new ArrayList<>(policy.groupsMinus());
			add = new ArrayList<>(policy.add());
			remove = new ArrayList<>(policy.remove());
			maxShapeForms = policy.maxShapeForms();
		}

		KindPolicy toRecord() {
			return new KindPolicy(groupsPlus, groupsMinus, add, remove, maxShapeForms);
		}
	}

	private static final class MutableFormChances {
		float stoneSpikeChance;
		float volcanicSpikeChance;
		float stoneSpikeGrowthChance;
		float volcanicSpikeGrowthChance;
		float sandGlassChance;
		float sandTintedGlassChance;
		float spikeWorldgenChance;
		int   spikeMaxHeight;
		float crystalClusterFillChance;
		float geodeCrystalWallChance;
		float geodeCrystalClusterChance;
		int   hammerAoeRadius;

		MutableFormChances(RAAConfig.FormChances fc) {
			stoneSpikeChance          = fc.stoneSpikeChance();
			volcanicSpikeChance       = fc.volcanicSpikeChance();
			stoneSpikeGrowthChance    = fc.stoneSpikeGrowthChance();
			volcanicSpikeGrowthChance = fc.volcanicSpikeGrowthChance();
			sandGlassChance           = fc.sandGlassChance();
			sandTintedGlassChance     = fc.sandTintedGlassChance();
			spikeWorldgenChance       = fc.spikeWorldgenChance();
			spikeMaxHeight            = fc.spikeMaxHeight();
			crystalClusterFillChance  = fc.crystalClusterFillChance();
			geodeCrystalWallChance    = fc.geodeCrystalWallChance();
			geodeCrystalClusterChance = fc.geodeCrystalClusterChance();
			hammerAoeRadius           = fc.hammerAoeRadius();
		}

		RAAConfig.FormChances toRecord() {
			return new RAAConfig.FormChances(
					stoneSpikeChance, volcanicSpikeChance,
					stoneSpikeGrowthChance, volcanicSpikeGrowthChance,
					sandGlassChance, sandTintedGlassChance,
					spikeWorldgenChance, spikeMaxHeight,
					crystalClusterFillChance,
					geodeCrystalWallChance, geodeCrystalClusterChance,
					hammerAoeRadius
			);
		}
	}

	private static final class MutableColorRanges {
		float lMin;
		float lMax;
		float cMin;
		float cMax;

		MutableColorRanges(RAAConfig.ColorRanges cr) {
			lMin = cr.lMin();
			lMax = cr.lMax();
			cMin = cr.cMin();
			cMax = cr.cMax();
		}

		RAAConfig.ColorRanges toRecord() {
			return new RAAConfig.ColorRanges(lMin, lMax, cMin, cMax);
		}
	}

	private static final class MutableSpawnProfile {
		int attemptsMin;
		int attemptsMax;
		float successChance;
		int veinMin;
		int veinMax;
		SpawnInfo.VeinShape shape;
		int minY;
		int maxY;
		int centerMin;
		int centerMax;
		int spreadMin;
		int spreadMax;
		float regionFrequency;
		float regionThreshold;
		float pocketFrequency;
		float pocketThreshold;
		float mustTouchAirChance;
		float nearWaterChance;
		float nearLavaChance;

		MutableSpawnProfile(RAAConfig.SpawnProfile profile) {
			attemptsMin = profile.attempts().min();
			attemptsMax = profile.attempts().max();
			successChance = profile.successChance();
			veinMin = profile.veinMin();
			veinMax = profile.veinMax();
			shape = profile.shape();
			minY = profile.minY();
			maxY = profile.maxY();
			centerMin = profile.center().min();
			centerMax = profile.center().max();
			spreadMin = profile.spread().min();
			spreadMax = profile.spread().max();
			regionFrequency = profile.regionGate().frequency();
			regionThreshold = profile.regionGate().threshold();
			pocketFrequency = profile.pocketGate().frequency();
			pocketThreshold = profile.pocketGate().threshold();
			mustTouchAirChance = profile.mustTouchAirChance();
			nearWaterChance = profile.nearWaterChance();
			nearLavaChance = profile.nearLavaChance();
		}

		RAAConfig.SpawnProfile toRecord() {
			return new RAAConfig.SpawnProfile(
					new RAAConfig.IntRange(attemptsMin, attemptsMax),
					successChance,
					veinMin,
					veinMax,
					shape,
					minY,
					maxY,
					new RAAConfig.IntRange(centerMin, centerMax),
					new RAAConfig.IntRange(spreadMin, spreadMax),
					new SpawnInfo.NoiseGate(regionFrequency, regionThreshold),
					new SpawnInfo.NoiseGate(pocketFrequency, pocketThreshold),
					mustTouchAirChance,
					nearWaterChance,
					nearLavaChance
			);
		}
	}
}
