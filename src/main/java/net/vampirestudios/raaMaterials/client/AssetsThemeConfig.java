package net.vampirestudios.raaMaterials.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.util.GsonHelper;
import net.vampirestudios.raaMaterials.material.Form;
import net.vampirestudios.raaMaterials.material.MaterialKind;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraft.resources.Identifier.withDefaultNamespace;
import static net.vampirestudios.raaMaterials.RAAMaterials.id;

public final class AssetsThemeConfig {
	private static final Codec<Map<AssetsTheme.Slot, List<Identifier>>> SLOT_MAP_CODEC =
			Codec.unboundedMap(AssetsTheme.Slot.CODEC, Identifier.CODEC.listOf());
	private static final Codec<Map<MaterialKind, Map<AssetsTheme.Slot, List<Identifier>>>> KIND_SLOT_MAP_CODEC =
			Codec.unboundedMap(MaterialKind.CODEC, SLOT_MAP_CODEC);
	private static final Codec<Map<Form, List<Identifier>>> FORM_MAP_CODEC =
			Codec.unboundedMap(Form.CODEC, Identifier.CODEC.listOf());
	private static final Codec<Map<MaterialKind, Map<Form, List<Identifier>>>> KIND_FORM_MAP_CODEC =
			Codec.unboundedMap(MaterialKind.CODEC, FORM_MAP_CODEC);

	private static final Codec<Data> CODEC = com.mojang.serialization.codecs.RecordCodecBuilder.create(i -> i.group(
			SLOT_MAP_CODEC.optionalFieldOf("global", Map.of()).forGetter(Data::global),
			SLOT_MAP_CODEC.optionalFieldOf("fallbacks", Map.of()).forGetter(Data::fallbacks),
			KIND_SLOT_MAP_CODEC.optionalFieldOf("perKind", Map.of()).forGetter(Data::perKind),
			FORM_MAP_CODEC.optionalFieldOf("globalForm", Map.of()).forGetter(Data::globalForm),
			KIND_FORM_MAP_CODEC.optionalFieldOf("perKindForm", Map.of()).forGetter(Data::perKindForm)
	).apply(i, Data::new));

	private static final AtomicReference<Data> ACTIVE = new AtomicReference<>();
	private static Path configPath;

	private AssetsThemeConfig() {
	}

	public static AssetsTheme theme() {
		return active().toTheme();
	}

	public static Data active() {
		var data = ACTIVE.get();
		if (data == null) {
			init(FabricLoader.getInstance().getGameDir());
			data = ACTIVE.get();
		}
		return data == null ? defaults() : data;
	}

	public static void init(Path gameDir) {
		configPath = gameDir.resolve("config/raa_materials/assets.json");
		try {
			if (Files.notExists(configPath)) save(defaults());
		} catch (IOException ignored) {
		}
		load();
	}

	public static void load() {
		try {
			var json = GsonHelper.parse(Files.newBufferedReader(configPath));
			var result = CODEC.parse(JsonOps.INSTANCE, json);
			result.resultOrPartial(err -> net.vampirestudios.raaMaterials.RAAMaterials.LOGGER.info("[Assets Theme] " + err))
					.ifPresentOrElse(data -> {
						var migrated = migrateDecorDefaults(data);
						ACTIVE.set(migrated);
						if (!migrated.equals(data)) {
							try {
								save(migrated);
							} catch (IOException ignored) {
							}
						}
					}, () -> ACTIVE.set(defaults()));
		} catch (Exception e) {
			net.vampirestudios.raaMaterials.RAAMaterials.LOGGER.info("[Assets Theme] Failed to read: " + e.getMessage());
			ACTIVE.set(defaults());
		}
	}

	public static void save(Data data) throws IOException {
		var json = CODEC.encodeStart(JsonOps.INSTANCE, data)
				.resultOrPartial(err -> net.vampirestudios.raaMaterials.RAAMaterials.LOGGER.info("[Assets Theme] Encode error: " + err))
				.orElseThrow(() -> new IOException("encode failed"));
		Files.createDirectories(configPath.getParent());
		var gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
		Files.writeString(configPath, gson.toJson(json));
	}

	public static Data defaults() {
		var global = new EnumMap<AssetsTheme.Slot, List<Identifier>>(AssetsTheme.Slot.class);
		var fallbacks = new EnumMap<AssetsTheme.Slot, List<Identifier>>(AssetsTheme.Slot.class);
		var perKind = new EnumMap<MaterialKind, Map<AssetsTheme.Slot, List<Identifier>>>(MaterialKind.class);
		var globalForm = new EnumMap<Form, List<Identifier>>(Form.class);
		var perKindForm = new EnumMap<MaterialKind, Map<Form, List<Identifier>>>(MaterialKind.class);

		global.put(AssetsTheme.Slot.ORE_VEIN_METAL, numbered("ores/metals/ore_", 40));
		global.put(AssetsTheme.Slot.ORE_VEIN_GEM, numbered("ores/gems/ore_", 33));
		global.put(AssetsTheme.Slot.STORAGE_METAL, numbered("storage_blocks/metals/metal_", 23));
		global.put(AssetsTheme.Slot.STORAGE_GEM, numbered("storage_blocks/gems/gem_", 16));
		global.put(AssetsTheme.Slot.STORAGE_CRYSTAL, numbered("crystal/crystal_block_", 5));
		global.put(AssetsTheme.Slot.RAW_BLOCK, numbered("storage_blocks/metals/raw_", 15));
		global.put(AssetsTheme.Slot.CRYSTAL_CLUSTER, numbered("crystal/crystal_", 9));
		global.put(AssetsTheme.Slot.CRYSTAL_ITEM, numbered("crystal_items/shard_", 7));
		global.put(AssetsTheme.Slot.RAW_ITEM, numbered("raw/raw_", 18));
		global.put(AssetsTheme.Slot.INGOT_ITEM, numbered("ingots/ingot_", 31));
		global.put(AssetsTheme.Slot.DUST_ITEM, numbered("dusts/dust_", 5));
		global.put(AssetsTheme.Slot.NUGGET_ITEM, numbered("nuggets/nugget_", 10));
		global.put(AssetsTheme.Slot.PLATE_ITEM, numbered("plates/plate_", 3));
		global.put(AssetsTheme.Slot.GEM_ITEM, numbered("gems/gem_", 33));
		global.put(AssetsTheme.Slot.SHARD_ITEM, numbered("crystal_items/shard_", 7));
		global.put(AssetsTheme.Slot.PICK_HEAD, numbered("tools/pickaxe/pickaxe_", 12));
		global.put(AssetsTheme.Slot.PICK_STICK, numbered("tools/pickaxe/stick_", 12));
		global.put(AssetsTheme.Slot.AXE_HEAD, numbered("tools/axe/axe_head_", 12));
		global.put(AssetsTheme.Slot.AXE_STICK, numbered("tools/axe/axe_stick_", 9));
		global.put(AssetsTheme.Slot.SWORD_BLADE, numbered("tools/sword/blade_", 13));
		global.put(AssetsTheme.Slot.SWORD_HANDLE, numbered("tools/sword/handle_", 11));
		global.put(AssetsTheme.Slot.SHOVEL_HEAD, numbered("tools/shovel/shovel_head_", 11));
		global.put(AssetsTheme.Slot.SHOVEL_STICK, numbered("tools/shovel/shovel_stick_", 11));
		global.put(AssetsTheme.Slot.HOE_HEAD, numbered("tools/hoe/hoe_head_", 9));
		global.put(AssetsTheme.Slot.HOE_STICK, numbered("tools/hoe/hoe_stick_", 9));
		global.put(AssetsTheme.Slot.SHEARS_BASE, List.of(id("tools/shears_base")));
		global.put(AssetsTheme.Slot.SHEARS_METAL, List.of(id("tools/shears_metal")));
		putMissingSpearDefaults(global);
		global.put(AssetsTheme.Slot.COBBLESTONE, numbered("stone/stone_cobbled_", 13));
		global.put(AssetsTheme.Slot.CHISELED, numbered("stone/stone_chiseled_", 4));
		global.put(AssetsTheme.Slot.STONE_BRICKS, numbered("stone/stone_bricks_", 24));
		global.put(AssetsTheme.Slot.POLISHED_RANDOM, numbered("stone/stone_frame_", 9));

		fallbacks.put(AssetsTheme.Slot.PLATE_SHEET, List.of(id("metal/metal_plate")));
		fallbacks.put(AssetsTheme.Slot.SHINGLES_SHEET, List.of(id("metal/metal_shingles")));
		fallbacks.put(AssetsTheme.Slot.SANDSTONE_TOP, List.of(withDefaultNamespace("block/sandstone_top")));
		fallbacks.put(AssetsTheme.Slot.SANDSTONE_SIDE, List.of(withDefaultNamespace("block/sandstone")));
		fallbacks.put(AssetsTheme.Slot.SANDSTONE_BOTTOM, List.of(withDefaultNamespace("block/sandstone_bottom")));
		fallbacks.put(AssetsTheme.Slot.SANDSTONE_CUT, List.of(withDefaultNamespace("block/cut_sandstone")));
		fallbacks.put(AssetsTheme.Slot.SANDSTONE_CHISELED, List.of(withDefaultNamespace("block/chiseled_sandstone")));
		fallbacks.put(AssetsTheme.Slot.CRYSTAL_BUDDING, List.of(id("crystal/budding_crystal_block_1")));
		fallbacks.put(AssetsTheme.Slot.CRYSTAL_BUD_S, List.of(withDefaultNamespace("block/small_amethyst_bud")));
		fallbacks.put(AssetsTheme.Slot.CRYSTAL_BUD_M, List.of(withDefaultNamespace("block/medium_amethyst_bud")));
		fallbacks.put(AssetsTheme.Slot.CRYSTAL_BUD_L, List.of(withDefaultNamespace("block/large_amethyst_bud")));
		fallbacks.put(AssetsTheme.Slot.TINTED_CRYSTAL_GLASS, List.of(id("crystal/tinted_glass_1")));
		fallbacks.put(AssetsTheme.Slot.CRYSTAL_GLASS, List.of(id("crystal/crystal_glass")));
		fallbacks.put(AssetsTheme.Slot.CRYSTAL_BRICKS, List.of(id("crystal/crystal_bricks")));
		fallbacks.put(AssetsTheme.Slot.LAMP_OVERLAY_1, List.of(id("crystal/lamp_overlay1")));
		fallbacks.put(AssetsTheme.Slot.LAMP_OVERLAY_2, List.of(id("crystal/lamp_overlay2")));
		fallbacks.put(AssetsTheme.Slot.CHIME, List.of(id("crystal/lamp_overlay3")));
		fallbacks.put(AssetsTheme.Slot.CLAY_ITEM, List.of(withDefaultNamespace("item/clay_ball")));
		fallbacks.put(AssetsTheme.Slot.GEAR_ITEM, List.of(id("gears/gear_1")));
		fallbacks.put(AssetsTheme.Slot.ROD_ITEM, List.of(id("parts/rod")));
		fallbacks.put(AssetsTheme.Slot.WIRE_ITEM, List.of(id("parts/wire")));
		fallbacks.put(AssetsTheme.Slot.COIL_ITEM, List.of(id("parts/coil")));
		fallbacks.put(AssetsTheme.Slot.RIVET_ITEM, List.of(id("parts/rivet")));
		fallbacks.put(AssetsTheme.Slot.BOLT_ITEM, List.of(id("parts/bolt")));
		fallbacks.put(AssetsTheme.Slot.NAIL_ITEM, List.of(id("nail")));
		fallbacks.put(AssetsTheme.Slot.RING_ITEM, List.of(id("ring")));
		fallbacks.put(AssetsTheme.Slot.DOOR_ITEM_METAL, List.of(id("decor/door")));
		fallbacks.put(AssetsTheme.Slot.DOOR_ITEM_WOOD, List.of(id("decor/door")));
		fallbacks.put(AssetsTheme.Slot.STONE_DEFAULT, List.of(withDefaultNamespace("block/stone")));

		perKind.put(MaterialKind.METAL, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/iron_block"))));
		perKind.put(MaterialKind.ALLOY, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/copper_block"))));
		perKind.put(MaterialKind.GEM, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/diamond_block"))));
		perKind.put(MaterialKind.CRYSTAL, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/amethyst_block"))));
		perKind.put(MaterialKind.STONE, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/stone"))));
		perKind.put(MaterialKind.SAND, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/sandstone"))));
		perKind.put(MaterialKind.GRAVEL, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/gravel"))));
		perKind.put(MaterialKind.CLAY, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/clay"))));
		perKind.put(MaterialKind.MUD, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/packed_mud"))));
		perKind.put(MaterialKind.SOIL, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/dirt"))));
		perKind.put(MaterialKind.SALT, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/calcite"))));
		perKind.put(MaterialKind.VOLCANIC, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/basalt_top"))));
		perKind.put(MaterialKind.WOOD, Map.of(AssetsTheme.Slot.STORAGE_BLOCK, List.of(withDefaultNamespace("block/oak_planks"))));

		globalForm.put(Form.CHAIN, List.of(id("decor/chain_1"), id("decor/chain_2")));
		globalForm.put(Form.LANTERN, List.of(id("decor/lantern")));
		globalForm.put(Form.DOOR, List.of(id("decor/door_bottom")));
		globalForm.put(Form.TRAPDOOR, List.of(id("decor/trapdoor")));
		globalForm.put(Form.FENCE, List.of(id("decor/planks")));
		globalForm.put(Form.FENCE_GATE, List.of(id("decor/planks")));
		perKindForm.put(MaterialKind.METAL, Map.of(
				Form.DOOR, List.of(id("decor/door_bottom")),
				Form.TRAPDOOR, List.of(id("decor/trapdoor"))
		));
		perKindForm.put(MaterialKind.ALLOY, Map.of(
				Form.DOOR, List.of(id("decor/door_bottom")),
				Form.TRAPDOOR, List.of(id("decor/trapdoor"))
		));

		return new Data(global, fallbacks, perKind, globalForm, perKindForm);
	}

	private static Data migrateDecorDefaults(Data data) {
		var global = new EnumMap<AssetsTheme.Slot, List<Identifier>>(AssetsTheme.Slot.class);
		var fallbacks = new EnumMap<AssetsTheme.Slot, List<Identifier>>(AssetsTheme.Slot.class);
		var perKind = new EnumMap<MaterialKind, Map<AssetsTheme.Slot, List<Identifier>>>(MaterialKind.class);
		var globalForm = new EnumMap<Form, List<Identifier>>(Form.class);
		var perKindForm = new EnumMap<MaterialKind, Map<Form, List<Identifier>>>(MaterialKind.class);

		global.putAll(data.global());
		fallbacks.putAll(data.fallbacks());
		globalForm.putAll(data.globalForm());
		data.perKind().forEach((kind, slots) -> {
			var copy = new EnumMap<AssetsTheme.Slot, List<Identifier>>(AssetsTheme.Slot.class);
			copy.putAll(slots);
			perKind.put(kind, copy);
		});
		data.perKindForm().forEach((kind, forms) -> {
			var copy = new EnumMap<Form, List<Identifier>>(Form.class);
			copy.putAll(forms);
			perKindForm.put(kind, copy);
		});

		putMissingSpearDefaults(global);

		replaceIfEquals(fallbacks, AssetsTheme.Slot.DOOR_ITEM_METAL,
				List.of(withDefaultNamespace("item/iron_door")), List.of(id("decor/door")));
		replaceIfEquals(fallbacks, AssetsTheme.Slot.DOOR_ITEM_WOOD,
				List.of(withDefaultNamespace("item/oak_door")), List.of(id("decor/door")));

		replaceIfEquals(globalForm, Form.CHAIN,
				List.of(id("metal/chain_1"), id("metal/chain_2")), List.of(id("decor/chain_1"), id("decor/chain_2")));
		replaceIfEquals(globalForm, Form.LANTERN,
				List.of(withDefaultNamespace("block/lantern")), List.of(id("decor/lantern")));
		replaceIfEquals(globalForm, Form.DOOR,
				List.of(withDefaultNamespace("block/oak_door_bottom")), List.of(id("decor/door_bottom")));
		replaceIfEquals(globalForm, Form.TRAPDOOR,
				List.of(withDefaultNamespace("block/oak_trapdoor")), List.of(id("decor/trapdoor")));
		replaceIfEquals(globalForm, Form.FENCE,
				List.of(withDefaultNamespace("block/oak_planks")), List.of(id("decor/planks")));
		replaceIfEquals(globalForm, Form.FENCE_GATE,
				List.of(withDefaultNamespace("block/oak_planks")), List.of(id("decor/planks")));

		migrateMetalDecor(perKindForm, MaterialKind.METAL);
		migrateMetalDecor(perKindForm, MaterialKind.ALLOY);

		return new Data(global, fallbacks, perKind, globalForm, perKindForm);
	}

	private static <K> void replaceIfEquals(Map<K, List<Identifier>> map, K key, List<Identifier> oldValue, List<Identifier> newValue) {
		if (oldValue.equals(map.get(key))) {
			map.put(key, newValue);
		}
	}

	private static void putMissingSpearDefaults(Map<AssetsTheme.Slot, List<Identifier>> global) {
		global.putIfAbsent(AssetsTheme.Slot.SPEAR_HEAD, List.of(id("tools/spear/head")));
		global.putIfAbsent(AssetsTheme.Slot.SPEAR_HANDLE, List.of(id("tools/spear/handle")));
		global.putIfAbsent(AssetsTheme.Slot.SPEAR_HEAD_IN_HAND, List.of(id("tools/spear/head_in_hand")));
		global.putIfAbsent(AssetsTheme.Slot.SPEAR_HANDLE_IN_HAND, List.of(id("tools/spear/handle_in_hand")));
	}

	private static void migrateMetalDecor(Map<MaterialKind, Map<Form, List<Identifier>>> perKindForm, MaterialKind kind) {
		var forms = perKindForm.computeIfAbsent(kind, ignored -> new EnumMap<>(Form.class));
		replaceIfEquals(forms, Form.DOOR,
				List.of(withDefaultNamespace("block/iron_door_bottom")), List.of(id("decor/door_bottom")));
		replaceIfEquals(forms, Form.TRAPDOOR,
				List.of(withDefaultNamespace("block/iron_trapdoor")), List.of(id("decor/trapdoor")));
	}

	private static List<Identifier> numbered(String base, int max) {
		return java.util.stream.IntStream.rangeClosed(1, max)
				.mapToObj(i -> id(base + i))
				.toList();
	}

	public record Data(
			Map<AssetsTheme.Slot, List<Identifier>> global,
			Map<AssetsTheme.Slot, List<Identifier>> fallbacks,
			Map<MaterialKind, Map<AssetsTheme.Slot, List<Identifier>>> perKind,
			Map<Form, List<Identifier>> globalForm,
			Map<MaterialKind, Map<Form, List<Identifier>>> perKindForm
	) {
		AssetsTheme toTheme() {
			var builder = AssetsTheme.theme();
			global.forEach(builder::choose);
			fallbacks.forEach((slot, values) -> {
				if (!values.isEmpty()) builder.fallback(slot, values.get(0));
			});
			perKind.forEach((kind, slots) -> builder.kind(kind, kindBuilder -> slots.forEach(kindBuilder::choose)));
			globalForm.forEach(builder::choose);
			perKindForm.forEach((kind, forms) -> builder.kindForm(kind, formBuilder -> forms.forEach(formBuilder::choose)));
			return builder.build();
		}
	}
}
