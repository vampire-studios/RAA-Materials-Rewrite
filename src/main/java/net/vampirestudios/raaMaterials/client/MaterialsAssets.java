package net.vampirestudios.raaMaterials.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.raaMaterials.RAAMaterials;
import net.vampirestudios.raaMaterials.RRPGen;
import net.vampirestudios.raaMaterials.content.ParametricBlock;
import net.vampirestudios.raaMaterials.material.ClientMaterialCache;
import net.vampirestudios.raaMaterials.material.MaterialAssets;
import net.vampirestudios.raaMaterials.material.MaterialAssetsDef;
import net.vampirestudios.raaMaterials.material.MaterialDef;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class MaterialsAssets {
	private static final Gson GSON = new GsonBuilder()
			.setPrettyPrinting()
			.disableHtmlEscaping()
			.create();

	private static boolean dirty = false;
	private static boolean reloading = false;

	private MaterialsAssets() {
	}

	public static void init() {
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (!dirty || reloading || client.level == null) {
				return;
			}

			dirty = false;
			buildAll();

			reloading = true;
			client.execute(() -> {
				client.reloadResourcePacks();
				reloading = false;
			});

			registerBlockColorProviders();
		});

		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				RAAMaterials.id("materials_assets_rebuilder"),
				(_, _, preparationBarrier, _) -> {
					if (!ClientMaterialCache.all().isEmpty()) {
						buildAll();
					}

					return preparationBarrier.wait(null);
				}
		);
	}

	public static void markDirty() {
		dirty = true;
	}

	public static void buildAll() {
		var ctx = new MaterialAssetContext(
				RRPGen.PACK,
				Minecraft.getInstance(),
				ClientMaterialCache.all()
		);

		MaterialAssetBuilders.addFallbackModels(ctx);
		exportDevAssets(ctx);

		MaterialAssetBuilders.validateTextures(ctx);

		MaterialAssetBuilders.buildOreFamilies(ctx);
		MaterialAssetBuilders.buildBlockFamilies(ctx);
		MaterialAssetBuilders.buildSpecialBlockFamilies(ctx);
		MaterialAssetBuilders.buildShapeFamilies(ctx);
		MaterialAssetBuilders.buildItemFamilies(ctx);

		ctx.pack().dump();
	}

	public static void saveDevExport(Path gameDir) throws IOException {
		saveDevExport(gameDir, ClientMaterialCache.all());
	}

	public static void saveDevExport(Path gameDir, List<MaterialDef> materials) throws IOException {
		Path root = gameDir.resolve("dev_export");
		Files.createDirectories(root);

		for (MaterialDef material : materials) {
			MaterialAssetsDef assetDef = MaterialAssets.generate(material);

			Path out = root
					.resolve("data")
					.resolve(material.nameInformation().id().getNamespace())
					.resolve("assets")
					.resolve(material.nameInformation().id().getPath() + ".json");

			Files.createDirectories(out.getParent());

			JsonElement json = encodeOrThrow(MaterialAssetsDef.CODEC, assetDef);
			try (Writer writer = Files.newBufferedWriter(out)) {
				GSON.toJson(json, writer);
			}
		}
	}

	private static void exportDevAssets(MaterialAssetContext ctx) {
		if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
			return;
		}

		try {
			saveDevExport(FabricLoader.getInstance().getGameDir(), ctx.materials());
		} catch (IOException e) {
			RAAMaterials.LOGGER.error("[RAA] Dev export failed", e);
		}
	}

	private static <T> JsonElement encodeOrThrow(Codec<T> codec, T value) {
		DataResult<JsonElement> result = codec.encodeStart(JsonOps.INSTANCE, value);
		return result.getOrThrow();
	}

	private static void registerBlockColorProviders() {
		var materials = ClientMaterialCache.all();

		for (Identifier id : MaterialAssetBuilders.COLORED_BLOCKS) {
			registerColorProviderForSharedBlock(id, materials);
		}
	}

	private static void registerColorProviderForSharedBlock(Identifier sharedBlockId, List<MaterialDef> materials) {
		var block = BuiltInRegistries.BLOCK.getValue(sharedBlockId);

		if (block == null) {
			RAAMaterials.LOGGER.warn("[RAA] Block not found for id: {}", sharedBlockId);
			return;
		}

		BlockColorRegistry.register((state, _, _, tintValues) -> {
			int matIndex = state.getValue(ParametricBlock.MAT);

			if (matIndex >= 0 && matIndex < materials.size()) {
				tintValues.add(opaqueColor(materials.get(matIndex).primaryColor()));
			}
		}, block);
	}

	static int opaqueColor(int color) {
		return color | 0xFF000000;
	}
}