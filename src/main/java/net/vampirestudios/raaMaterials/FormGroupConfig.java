package net.vampirestudios.raaMaterials;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.GsonHelper;
import net.vampirestudios.raaMaterials.material.Form;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static net.vampirestudios.raaMaterials.material.Form.*;

public final class FormGroupConfig {
	private static final Codec<Map<FormGroup, List<Form>>> CODEC =
			Codec.unboundedMap(FormGroup.CODEC, Form.CODEC.listOf());

	private static final AtomicReference<Map<FormGroup, List<Form>>> ACTIVE =
			new AtomicReference<>(defaults());
	private static Path configPath;

	private FormGroupConfig() {
	}

	public static Map<FormGroup, List<Form>> active() {
		return ACTIVE.get();
	}

	public static Map<FormGroup, List<Form>> defaults() {
		var map = new EnumMap<FormGroup, List<Form>>(FormGroup.class);
		map.put(FormGroup.TOOLS, List.of(PICKAXE, AXE, SWORD, SHOVEL, HOE, SPEAR));
		map.put(FormGroup.ORE_CHAIN, List.of(ORE, RAW, RAW_BLOCK));
		map.put(FormGroup.METAL_DECOR, List.of(PLATE_BLOCK, SHINGLES, PILLAR, TILES, MOSAIC));
		map.put(FormGroup.STONE_DECOR, List.of(BRICKS, PILLAR, TILES, MOSAIC, MOSSY, CRACKED, COBBLED, CHISELED, POLISHED, SLAB, STAIRS, WALL, CUT, SMOOTH));
		map.put(FormGroup.SAND_SET, List.of(SANDSTONE, SLAB, STAIRS, WALL, CHISELED));
		map.put(FormGroup.GRAVEL_SET, List.of(POLISHED, SLAB, STAIRS, WALL));
		map.put(FormGroup.CLAY_SET, List.of(BLOCK, BALL, CERAMIC));
		map.put(FormGroup.MUD_SET, List.of(DRIED, BRICKS, SLAB, STAIRS, WALL));
		map.put(FormGroup.SOIL_SET, List.of(BLOCK, PACKED_SOIL));
		map.put(FormGroup.SALT_SET, List.of(BLOCK, DUST));
		map.put(FormGroup.VOLCANIC_SET, List.of(BLOCK, BRICKS, PILLAR, COBBLED, POLISHED, MOSSY, BUTTON, PRESSURE_PLATE));
		map.put(FormGroup.CRYSTAL_SET, List.of(CLUSTER, CRYSTAL, SHARD, DUST, CRYSTAL_BRICKS, GLASS, TINTED_GLASS, PANE, ROD_BLOCK));
		map.put(FormGroup.CRYSTAL_LAMPS, List.of(CALCITE_LAMP, BASALT_LAMP, CHIME));
		return Collections.unmodifiableMap(map);
	}

	public static void init(Path gameDir) {
		configPath = gameDir.resolve("config/raa_materials/form_groups.json");
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
			result.resultOrPartial(err -> RAAMaterials.LOGGER.info("[Form Groups] " + err))
					.ifPresentOrElse(value -> ACTIVE.set(wrap(value)), () -> ACTIVE.set(defaults()));
		} catch (Exception e) {
			RAAMaterials.LOGGER.info("[Form Groups] Failed to read: " + e.getMessage());
			ACTIVE.set(defaults());
		}
	}

	public static void save(Map<FormGroup, List<Form>> config) throws IOException {
		var json = CODEC.encodeStart(JsonOps.INSTANCE, config)
				.resultOrPartial(err -> RAAMaterials.LOGGER.info("[Form Groups] Encode error: " + err))
				.orElseThrow(() -> new IOException("encode failed"));
		Files.createDirectories(configPath.getParent());
		var gson = new com.google.gson.GsonBuilder().setPrettyPrinting().create();
		Files.writeString(configPath, gson.toJson(json));
	}

	public static void replaceAndSave(Map<FormGroup, List<Form>> config) {
		var wrapped = wrap(config);
		ACTIVE.set(wrapped);
		try {
			save(wrapped);
		} catch (IOException e) {
			RAAMaterials.LOGGER.info("[Form Groups] Failed to save: " + e.getMessage());
		}
	}

	private static Map<FormGroup, List<Form>> wrap(Map<FormGroup, List<Form>> input) {
		var map = new EnumMap<FormGroup, List<Form>>(FormGroup.class);
		defaults().forEach((group, forms) -> map.put(group, List.copyOf(input.getOrDefault(group, forms))));
		return Collections.unmodifiableMap(map);
	}
}
