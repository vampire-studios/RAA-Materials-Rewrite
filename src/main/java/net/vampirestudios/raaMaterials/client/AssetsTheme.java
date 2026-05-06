// src/main/java/net/vampirestudios/raaMaterials/client/AssetsTheme.java
package net.vampirestudios.raaMaterials.client;

import net.minecraft.resources.Identifier;
import net.vampirestudios.raaMaterials.material.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

import static net.minecraft.resources.Identifier.withDefaultNamespace;
import static net.vampirestudios.raaMaterials.RAAMaterials.id;

public final class AssetsTheme {
	private final Map<Slot, List<Identifier>> global = new EnumMap<>(Slot.class);
	private final Map<MaterialKind, Map<Slot, List<Identifier>>> perKind = new EnumMap<>(MaterialKind.class);
	private final Map<Slot, Identifier> fallbacks = new EnumMap<>(Slot.class);
	private final Map<Form, List<Identifier>> globalForm = new EnumMap<>(Form.class);
	private final Map<MaterialKind, Map<Form, List<Identifier>>> perKindForm = new EnumMap<>(MaterialKind.class);

	private AssetsTheme() {
		// Initialize fallbacks
		initializeFallbacks();
	}

	private void initializeFallbacks() {
		fallbacks.put(Slot.SANDSTONE_TOP, withDefaultNamespace("sandstone_top.png"));
		fallbacks.put(Slot.SANDSTONE_SIDE, withDefaultNamespace("sandstone.png"));
		fallbacks.put(Slot.SANDSTONE_BOTTOM, withDefaultNamespace("sandstone_bottom.png"));
		fallbacks.put(Slot.SANDSTONE_CUT, withDefaultNamespace("cut_sandstone.png"));
		fallbacks.put(Slot.SANDSTONE_CHISELED, withDefaultNamespace("chiseled_sandstone.png"));
		fallbacks.put(Slot.CRYSTAL_BUD_S, withDefaultNamespace("small_amethyst_bud.png"));
		fallbacks.put(Slot.CRYSTAL_BUD_M, withDefaultNamespace("medium_amethyst_bud.png"));
		fallbacks.put(Slot.CRYSTAL_BUD_L, withDefaultNamespace("large_amethyst_bud.png"));
		fallbacks.put(Slot.TINTED_CRYSTAL_GLASS, id("crystal/tinted_glass_1.png"));
		fallbacks.put(Slot.CLAY_ITEM, withDefaultNamespace("clay.png"));
		fallbacks.put(Slot.GEAR_ITEM, id("gears/gear_1.png"));
	}

	// ---------- Builder ----------
	public static Builder theme() {
		return new Builder(new AssetsTheme());
	}

	// ---------- Internal resolvers ----------
	private Optional<Identifier> pick(MaterialKind k, Slot s, RandomGenerator rnd) {
		var km = perKind.get(k);
		if (km != null) {
			var rls = km.get(s);
			if (rls != null && !rls.isEmpty())
				return Optional.of(rls.get(rls.size() == 1 ? 0 : rnd.nextInt(rls.size())));
		}
		var g = global.get(s);
		if (g != null && !g.isEmpty())
			return Optional.of(g.get(g.size() == 1 ? 0 : rnd.nextInt(g.size())));
		return Optional.ofNullable(fallbacks.get(s));
	}

	private Optional<Identifier> pickForm(MaterialKind k, Form f, RandomGenerator rnd) {
		var km = perKindForm.get(k);
		if (km != null) {
			var rls = km.get(f);
			if (rls != null && !rls.isEmpty())
				return Optional.of(rls.get(rls.size() == 1 ? 0 : rnd.nextInt(rls.size())));
		}
		var g = globalForm.get(f);
		if (g != null && !g.isEmpty())
			return Optional.of(g.get(g.size() == 1 ? 0 : rnd.nextInt(g.size())));
		return Optional.empty();
	}

	public Optional<Identifier> resolveSlot(MaterialKind kind, Slot slot, Random rnd) {
		return pick(kind, slot, rnd);
	}

	// Public bridge for generators
	public Optional<Identifier> formTexture(MaterialKind k, Form f, long seed) {
		return pickForm(k, f, new Random(seed));
	}

	// ---------- Helper methods for texture generation ----------
	private static int wrapIndex(long seed, int max) {
		return (int) (Math.abs(seed) % max);
	}

	private static Identifier numbered(String base, Random rnd, int max) {
		return id(base + (rnd.nextInt(max) + 1) + ".png");
	}

	// ---------- Texture definition builders ----------
	private TextureDef1 buildTextureDef1(MaterialDef m, Random rnd) {
		var k = m.kind();

		// Ore vein: metals vs gems
		Optional<Identifier> oreVein = switch (k) {
			case METAL, ALLOY, OTHER -> Optional.of(numbered("ores/metals/ore_", rnd, 40));
			case GEM -> Optional.of(numbered("ores/gems/ore_", rnd, 33));
			default -> Optional.of(withDefaultNamespace("block/stone"));
		};

		// Storage block textures by kind
		Optional<Identifier> storageBlock = switch (k) {
			case METAL, ALLOY, OTHER -> Optional.of(numbered("storage_blocks/metals/metal_", rnd, 23));
			case GEM -> Optional.of(numbered("storage_blocks/gems/gem_", rnd, 16));
			case CRYSTAL -> Optional.of(numbered("crystal/crystal_block_", rnd, 5));
			default -> Optional.of(withDefaultNamespace("block/stone"));
		};

		// Raw block (metals-like)
		Optional<Identifier> rawBlock = switch (k) {
			case METAL, GEM, OTHER -> Optional.of(numbered("storage_blocks/metals/raw_", rnd, 15));
			default -> Optional.empty();
		};

		// Metal decor
		Optional<Identifier> plateBlock = Optional.of(id("metal/metal_plate.png"));
		Optional<Identifier> shingles = Optional.of(id("metal/metal_shingles.png"));

		// Sandstone family (vanilla parity)
		Optional<Identifier> sandstoneTop = Optional.of(withDefaultNamespace("block/sandstone_top"));
		Optional<Identifier> sandstoneSide = Optional.of(withDefaultNamespace("block/sandstone"));
		Optional<Identifier> sandstoneBottom = Optional.of(withDefaultNamespace("block/sandstone_bottom"));
		Optional<Identifier> cut = Optional.of(withDefaultNamespace("block/cut_sandstone"));
		Optional<Identifier> chiseled = switch (k) {
			case STONE -> Optional.of(numbered("stone/stone_chiseled_", rnd, 4));
			case SAND -> Optional.of(withDefaultNamespace("block/chiseled_sandstone"));
			default -> Optional.empty();
		};

		// Budding & buds (crystal-like)
		Optional<Identifier> budding = (k == MaterialKind.CRYSTAL) ?
				Optional.of(id("crystal/budding_crystal_block_1.png")) : Optional.empty();
		Optional<Identifier> budSmall = Optional.of(withDefaultNamespace("block/small_amethyst_bud"));
		Optional<Identifier> budMedium = Optional.of(withDefaultNamespace("block/medium_amethyst_bud"));
		Optional<Identifier> budLarge = Optional.of(withDefaultNamespace("block/large_amethyst_bud"));

		// Cluster + tinted glass overlay
		Optional<Identifier> cluster = (k == MaterialKind.CRYSTAL) ?
				Optional.of(numbered("crystal/crystal_", rnd, 9)) : Optional.empty();
		Optional<Identifier> tintedGlass = Optional.of(id("crystal/tinted_glass_1.png"));

		return new TextureDef1(
				oreVein, storageBlock, rawBlock, plateBlock, shingles,
				sandstoneTop, sandstoneSide, sandstoneBottom, cut, chiseled,
				budding, budSmall, budMedium, budLarge, cluster, tintedGlass
		);
	}

	private TextureDef2 buildTextureDef2(MaterialDef m, Random rnd) {
		Optional<Identifier> crystalGlass = Optional.of(id("crystal/crystal_glass.png"));
		Optional<Identifier> crystalBricks = Optional.of(id("crystal/crystal_bricks.png"));
		Optional<Identifier> lampOverlay1 = Optional.of(id("crystal/lamp_overlay1.png"));
		Optional<Identifier> lampOverlay2 = Optional.of(id("crystal/lamp_overlay2.png"));

		Optional<Identifier> raw = Optional.of(numbered("raw/raw_", rnd, 18));
		Optional<Identifier> ingot = Optional.of(numbered("ingots/ingot_", rnd, 29));
		Optional<Identifier> dust = Optional.of(numbered("dusts/dust_", rnd, 5));
		Optional<Identifier> nugget = Optional.of(numbered("nuggets/nugget_", rnd, 10));
		Optional<Identifier> plate = Optional.of(numbered("plates/plate_", rnd, 3));
		Optional<Identifier> gear = Optional.of(id("gears/gear_1.png"));
		Optional<Identifier> gem = Optional.of(numbered("gems/gem_", rnd, 33));
		Optional<Identifier> shard = Optional.of(numbered("crystal_items/shard_", rnd, 7));
		Optional<Identifier> clayBall = Optional.of(withDefaultNamespace("item/clay_ball"));

		// Tool parts
		Optional<Identifier> pickHead = Optional.of(numbered("tools/pickaxe/pickaxe_", rnd, 11));
		Optional<Identifier> pickHandle = Optional.of(numbered("tools/pickaxe/stick_", rnd, 10));

		return new TextureDef2(
				crystalGlass, crystalBricks, lampOverlay1, lampOverlay2,
				raw, ingot, dust, nugget, plate, gear, gem, shard, clayBall,
				pickHead, pickHandle
		);
	}

	private TextureDef3 buildTextureDef3(MaterialDef m, Random rnd) {
		Optional<Identifier> axeHead = Optional.of(numbered("tools/axe/axe_head_", rnd, 11));
		Optional<Identifier> axeHandle = Optional.of(numbered("tools/axe/axe_stick_", rnd, 8));
		Optional<Identifier> swordBlade = Optional.of(numbered("tools/sword/blade_", rnd, 13));
		Optional<Identifier> swordHandle = Optional.of(numbered("tools/sword/handle_", rnd, 11));
		Optional<Identifier> shovelHead = Optional.of(numbered("tools/shovel/shovel_head_", rnd, 11));
		Optional<Identifier> shovelHandle = Optional.of(numbered("tools/shovel/shovel_stick_", rnd, 11));
		Optional<Identifier> hoeHead = Optional.of(numbered("tools/hoe/hoe_head_", rnd, 9));
		Optional<Identifier> hoeHandle = Optional.of(numbered("tools/hoe/hoe_stick_", rnd, 9));
		Optional<Identifier> cobblestone = Optional.of(numbered("stone/stone_cobbled_", rnd, 13));
		Optional<Identifier> chiseled = Optional.of(numbered("stone/stone_chiseled_", rnd, 4));
		Optional<Identifier> bricks = Optional.of(numbered("stone/stone_bricks_", rnd, 24));
		Optional<Identifier> polished = Optional.of(numbered("stone/stone_frame_", rnd, 9));
		Optional<Identifier> nail = Optional.of(id("nail"));
		Optional<Identifier> ring = Optional.of(id("ring"));

		return new TextureDef3(
				axeHead, axeHandle, swordBlade, swordHandle,
				shovelHead, shovelHandle, hoeHead, hoeHandle,
				cobblestone, chiseled, bricks, polished,
				nail, ring
		);
	}

	public MaterialAssetsDef resolve(MaterialDef m) {
		Random rnd = new Random(m.assetSeed());
		return new MaterialAssetsDef(
				1,
				m.nameInformation().id(),
				m.assetSeed(),
				buildTextureDef1(m, rnd),
				buildTextureDef2(m, rnd),
				buildTextureDef3(m, rnd)
		);
	}

	public static AssetsTheme defaultTheme() {
		return AssetsTheme.theme()
				// Role sheets (global)
				.fallback(Slot.PLATE_SHEET, id("metal/metal_plate.png"))
				.fallback(Slot.SHINGLES_SHEET, id("metal/metal_shingles.png"))
				.fallback(Slot.TINTED_CRYSTAL_GLASS, id("crystal/tinted_glass_1.png"))

				// Material kind storage blocks
				.kind(MaterialKind.METAL, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("iron_block.png")))
				.kind(MaterialKind.ALLOY, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("copper_block.png")))
				.kind(MaterialKind.GEM, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("diamond_block.png")))
				.kind(MaterialKind.CRYSTAL, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("amethyst_block.png")))
				.kind(MaterialKind.STONE, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("stone.png")))
				.kind(MaterialKind.SAND, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("sandstone.png")))
				.kind(MaterialKind.GRAVEL, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("gravel.png")))
				.kind(MaterialKind.CLAY, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("clay.png")))
				.kind(MaterialKind.MUD, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("packed_mud.png")))
				.kind(MaterialKind.SOIL, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("dirt.png")))
				.kind(MaterialKind.SALT, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("calcite.png")))
				.kind(MaterialKind.VOLCANIC, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("basalt_top.png")))
				.kind(MaterialKind.WOOD, k -> k.set(Slot.STORAGE_BLOCK, withDefaultNamespace("oak_planks.png")))

				// Sandstone set
				.fallback(Slot.SANDSTONE_TOP, withDefaultNamespace("sandstone_top.png"))
				.fallback(Slot.SANDSTONE_SIDE, withDefaultNamespace("sandstone.png"))
				.fallback(Slot.SANDSTONE_BOTTOM, withDefaultNamespace("sandstone_bottom.png"))
				.fallback(Slot.SANDSTONE_CUT, withDefaultNamespace("cut_sandstone.png"))
				.fallback(Slot.SANDSTONE_CHISELED, withDefaultNamespace("chiseled_sandstone.png"))

				// Crystal buds
				.fallback(Slot.CRYSTAL_BUD_S, withDefaultNamespace("small_amethyst_bud.png"))
				.fallback(Slot.CRYSTAL_BUD_M, withDefaultNamespace("medium_amethyst_bud.png"))
				.fallback(Slot.CRYSTAL_BUD_L, withDefaultNamespace("large_amethyst_bud.png"))

				// Per-form defaults
				.set(Slot.BRICKS, withDefaultNamespace("stone_bricks.png"))
				.set(Slot.POLISHED, withDefaultNamespace("smooth_stone.png"))
				.set(Slot.TILES, withDefaultNamespace("quartz_block_top.png"))
				.set(Slot.MOSAIC, withDefaultNamespace("bamboo_mosaic.png"))
				.set(Slot.PILLAR_SIDE, withDefaultNamespace("quartz_block_side.png"))
				.set(Slot.PILLAR_TOP, withDefaultNamespace("quartz_block_top.png"))

				// Item icons
				.fallback(Slot.CLAY_ITEM, withDefaultNamespace("clay.png"))
				.set(Slot.GEAR_ITEM, id("gears/gear_1.png"))

				.build();
	}

	// ----- Slots -----
	public enum Slot {
		// Block textures
		ORE_VEIN_METAL, ORE_VEIN_GEM, STORAGE_BLOCK, RAW_BLOCK,
		PLATE_SHEET, SHINGLES_SHEET,
		SANDSTONE_TOP, SANDSTONE_SIDE, SANDSTONE_BOTTOM, SANDSTONE_CUT, SANDSTONE_CHISELED,
		CRYSTAL_BUDDING, CRYSTAL_BUD_S, CRYSTAL_BUD_M, CRYSTAL_BUD_L, CRYSTAL_BLOCK, TINTED_CRYSTAL_GLASS,

		// Item textures
		RAW_ITEM, INGOT_ITEM, DUST_ITEM, NUGGET_ITEM, PLATE_ITEM, GEAR_ITEM, GEM_ITEM, SHARD_ITEM, CLAY_ITEM,

		// Tool part textures
		PICK_HEAD, PICK_STICK, AXE_HEAD, AXE_STICK, SWORD_BLADE, SWORD_HANDLE,
		SHOVEL_HEAD, SHOVEL_STICK, HOE_HEAD, HOE_STICK,

		// Form textures
		BRICKS, POLISHED, TILES, MOSAIC, PILLAR_SIDE, PILLAR_TOP
	}

	// ----- Builder classes -----
	public static final class Builder {
		private final AssetsTheme theme;

		private Builder(AssetsTheme theme) {
			this.theme = theme;
		}

		public Builder set(Slot slot, Identifier rl) {
			theme.global.put(slot, List.of(rl));
			return this;
		}

		public Builder choose(Slot slot, List<Identifier> rls) {
			theme.global.put(slot, List.copyOf(rls));
			return this;
		}

		public Builder fallback(Slot slot, Identifier rl) {
			theme.fallbacks.put(slot, rl);
			return this;
		}

		public Builder kind(MaterialKind kind, Consumer<KindBuilder> consumer) {
			var km = theme.perKind.computeIfAbsent(kind, k -> new EnumMap<>(Slot.class));
			consumer.accept(new KindBuilder(km));
			return this;
		}

		public Builder set(Form form, Identifier rl) {
			theme.globalForm.put(form, List.of(rl));
			return this;
		}

		public Builder choose(Form form, List<Identifier> rls) {
			theme.globalForm.put(form, List.copyOf(rls));
			return this;
		}

		public Builder kindForm(MaterialKind kind, Consumer<FormKindBuilder> consumer) {
			var km = theme.perKindForm.computeIfAbsent(kind, k -> new EnumMap<>(Form.class));
			consumer.accept(new FormKindBuilder(km));
			return this;
		}

		public AssetsTheme build() {
			return theme;
		}
	}

	public static final class KindBuilder {
		private final Map<Slot, List<Identifier>> kindMap;

		private KindBuilder(Map<Slot, List<Identifier>> kindMap) {
			this.kindMap = kindMap;
		}

		public KindBuilder set(Slot slot, Identifier rl) {
			kindMap.put(slot, List.of(rl));
			return this;
		}

		public KindBuilder choose(Slot slot, List<Identifier> rls) {
			kindMap.put(slot, List.copyOf(rls));
			return this;
		}

		public KindBuilder clear(Slot slot) {
			kindMap.remove(slot);
			return this;
		}
	}

	public static final class FormKindBuilder {
		private final Map<Form, List<Identifier>> formMap;

		private FormKindBuilder(Map<Form, List<Identifier>> formMap) {
			this.formMap = formMap;
		}

		public FormKindBuilder set(Form form, Identifier rl) {
			formMap.put(form, List.of(rl));
			return this;
		}

		public FormKindBuilder choose(Form form, List<Identifier> rls) {
			formMap.put(form, List.copyOf(rls));
			return this;
		}

		public FormKindBuilder clear(Form form) {
			formMap.remove(form);
			return this;
		}
	}
}