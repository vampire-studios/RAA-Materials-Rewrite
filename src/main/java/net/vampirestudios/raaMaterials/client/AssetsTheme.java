package net.vampirestudios.raaMaterials.client;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.vampirestudios.raaMaterials.material.*;
import net.vampirestudios.raaMaterials.material.textureSets.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.random.RandomGenerator;

public final class AssetsTheme {
	private final Map<Slot, List<Identifier>> global = new EnumMap<>(Slot.class);
	private final Map<MaterialKind, Map<Slot, List<Identifier>>> perKind = new EnumMap<>(MaterialKind.class);
	private final Map<Slot, Identifier> fallbacks = new EnumMap<>(Slot.class);
	private final Map<Form, List<Identifier>> globalForm = new EnumMap<>(Form.class);
	private final Map<MaterialKind, Map<Form, List<Identifier>>> perKindForm = new EnumMap<>(MaterialKind.class);

	private AssetsTheme() {
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

	private Optional<Identifier> pickByIndex(MaterialKind k, Slot s, int index) {
		var km = perKind.get(k);
		if (km != null) {
			var rls = km.get(s);
			if (rls != null && !rls.isEmpty())
				return Optional.of(rls.get(rls.size() == 1 ? 0 : Math.floorMod(index, rls.size())));
		}

		var g = global.get(s);
		if (g != null && !g.isEmpty())
			return Optional.of(g.get(g.size() == 1 ? 0 : Math.floorMod(index, g.size())));

		return Optional.ofNullable(fallbacks.get(s));
	}

	private int pickVariantIndex(MaterialKind k, Slot s, RandomGenerator rnd) {
		var km = perKind.get(k);
		if (km != null) {
			var rls = km.get(s);
			if (rls != null && !rls.isEmpty())
				return rls.size() == 1 ? 0 : rnd.nextInt(rls.size());
		}

		var g = global.get(s);
		if (g != null && !g.isEmpty())
			return g.size() == 1 ? 0 : rnd.nextInt(g.size());

		return 0;
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

	private static Identifier pairedDoorTopTexture(Identifier bottomTexture) {
		String path = bottomTexture.getPath();
		if (path.contains("_bottom")) {
			return Identifier.fromNamespaceAndPath(bottomTexture.getNamespace(), path.replace("_bottom", "_top"));
		}
		if (path.contains("/bottom")) {
			return Identifier.fromNamespaceAndPath(bottomTexture.getNamespace(), path.replace("/bottom", "/top"));
		}
		return bottomTexture;
	}

	// ---------- Texture definition builders ----------
	private BlockTextureSet buildBlockTextureSet(MaterialDef m, Random rnd) {
		var k = m.kind();

		// Ore vein: metals vs gems
		Optional<Identifier> oreVein = switch (k) {
			case METAL, ALLOY, OTHER -> pick(k, Slot.ORE_VEIN_METAL, rnd);
			case GEM -> pick(k, Slot.ORE_VEIN_GEM, rnd);
			default -> pick(k, Slot.STONE_DEFAULT, rnd);
		};

		// Storage block textures by kind
		Optional<Identifier> storageBlock = switch (k) {
			case METAL, ALLOY, OTHER -> pick(k, Slot.STORAGE_METAL, rnd);
			case GEM -> pick(k, Slot.STORAGE_GEM, rnd);
			case CRYSTAL -> pick(k, Slot.STORAGE_CRYSTAL, rnd);
			default -> pick(k, Slot.STORAGE_BLOCK, rnd).or(() -> pick(k, Slot.STONE_DEFAULT, rnd));
		};

		// Raw block (metals-like)
		Optional<Identifier> rawBlock = switch (k) {
			case METAL, GEM, OTHER -> pick(k, Slot.RAW_BLOCK, rnd);
			default -> Optional.empty();
		};

		// Metal decor
		Optional<Identifier> plateBlock = pick(k, Slot.PLATE_SHEET, rnd);
		Optional<Identifier> shingles = pick(k, Slot.SHINGLES_SHEET, rnd);
		Optional<Identifier> cobblestone = pick(k, Slot.COBBLESTONE, rnd);
		Optional<Identifier> chiseled = pick(k, Slot.CHISELED, rnd);
		Optional<Identifier> bricks = pick(k, Slot.STONE_BRICKS, rnd);
		Optional<Identifier> polished = pick(k, Slot.POLISHED_RANDOM, rnd);

		return new BlockTextureSet(
				oreVein, storageBlock, rawBlock, plateBlock, shingles,
				cobblestone, chiseled, bricks, polished
		);
	}

	private SandstoneTextureSet buildSandstoneTextureSet(MaterialDef m, Random rnd) {
		var k = m.kind();

		// Sandstone family (vanilla parity)
		Optional<Identifier> sandstoneTop = pick(k, Slot.SANDSTONE_TOP, rnd);
		Optional<Identifier> sandstoneSide = pick(k, Slot.SANDSTONE_SIDE, rnd);
		Optional<Identifier> sandstoneBottom = pick(k, Slot.SANDSTONE_BOTTOM, rnd);
		Optional<Identifier> cutSide = pick(k, Slot.SANDSTONE_CUT, rnd);
		Optional<Identifier> chiseledSandstone = switch (k) {
			case STONE -> pick(k, Slot.CHISELED, rnd);
			case SAND -> pick(k, Slot.SANDSTONE_CHISELED, rnd);
			default -> Optional.empty();
		};

		return new SandstoneTextureSet(
				sandstoneTop, sandstoneSide, sandstoneBottom, cutSide, chiseledSandstone
		);
	}

	private ItemTextureSet buildItemTextureSet(MaterialDef m, Random rnd) {
		var k = m.kind();
		Optional<Identifier> raw = pick(k, Slot.RAW_ITEM, rnd);
		Optional<Identifier> ingot = pick(k, Slot.INGOT_ITEM, rnd);
		Optional<Identifier> dust = pick(k, Slot.DUST_ITEM, rnd);
		Optional<Identifier> nugget = pick(k, Slot.NUGGET_ITEM, rnd);
		Optional<Identifier> plate = pick(k, Slot.PLATE_ITEM, rnd);
		Optional<Identifier> gear = pick(k, Slot.GEAR_ITEM, rnd);
		Optional<Identifier> gem = pick(k, Slot.GEM_ITEM, rnd);
		Optional<Identifier> shard = pick(k, Slot.SHARD_ITEM, rnd);
		Optional<Identifier> clayBall = pick(k, Slot.CLAY_ITEM, rnd);
		Optional<Identifier> rod = pick(k, Slot.ROD_ITEM, rnd);
		Optional<Identifier> wire = pick(k, Slot.WIRE_ITEM, rnd);
		Optional<Identifier> coil = pick(k, Slot.COIL_ITEM, rnd);
		Optional<Identifier> rivet = pick(k, Slot.RIVET_ITEM, rnd);
		Optional<Identifier> bolt = pick(k, Slot.BOLT_ITEM, rnd);
		Optional<Identifier> nail = pick(k, Slot.NAIL_ITEM, rnd);
		Optional<Identifier> ring = pick(k, Slot.RING_ITEM, rnd);

		return new ItemTextureSet(
				raw, ingot, dust, nugget, plate, gear, gem, shard, clayBall,
				rod, wire, coil, rivet, bolt, nail, ring
		);
	}

	private ToolTextureSet buildToolTextureSet(MaterialDef m, Random rnd) {
		var k = m.kind();
		Optional<Identifier> pickHead = pick(k, Slot.PICK_HEAD, rnd);
		Optional<Identifier> pickHandle = pick(k, Slot.PICK_STICK, rnd);
		Optional<Identifier> axeHead = pick(k, Slot.AXE_HEAD, rnd);
		Optional<Identifier> axeHandle = pick(k, Slot.AXE_STICK, rnd);
		Optional<Identifier> swordBlade = pick(k, Slot.SWORD_BLADE, rnd);
		Optional<Identifier> swordHandle = pick(k, Slot.SWORD_HANDLE, rnd);
		Optional<Identifier> shovelHead = pick(k, Slot.SHOVEL_HEAD, rnd);
		Optional<Identifier> shovelHandle = pick(k, Slot.SHOVEL_STICK, rnd);
		Optional<Identifier> hoeHead = pick(k, Slot.HOE_HEAD, rnd);
		Optional<Identifier> hoeHandle = pick(k, Slot.HOE_STICK, rnd);
		Optional<Identifier> shearsBase = pick(k, Slot.SHEARS_BASE, rnd);
		Optional<Identifier> shearsMetal = pick(k, Slot.SHEARS_METAL, rnd);
		int spearVariant = pickVariantIndex(k, Slot.SPEAR_HEAD, rnd);

		Optional<Identifier> spearHead = pickByIndex(k, Slot.SPEAR_HEAD, spearVariant);
		Optional<Identifier> spearHandle = pickByIndex(k, Slot.SPEAR_HANDLE, spearVariant);
		Optional<Identifier> spearHeadInHand = pickByIndex(k, Slot.SPEAR_HEAD_IN_HAND, spearVariant);
		Optional<Identifier> spearHandleInHand = pickByIndex(k, Slot.SPEAR_HANDLE_IN_HAND, spearVariant);

		return new ToolTextureSet(
				pickHead, pickHandle, axeHead, axeHandle, swordBlade, swordHandle,
				shovelHead, shovelHandle, hoeHead, hoeHandle, shearsBase, shearsMetal,
				spearHead, spearHandle, spearHeadInHand, spearHandleInHand
		);
	}

	private LegendaryTextureSet buildLegendaryTextureSet(MaterialDef m, Random rnd) {
		var k = m.kind();
		Optional<Identifier> hammerHead    = pick(k, Slot.HAMMER_HEAD,        rnd);
		Optional<Identifier> hammerHandle  = pick(k, Slot.HAMMER_HANDLE,      rnd);
		Optional<Identifier> daggerBlade   = pick(k, Slot.DAGGER_BLADE,       rnd);
		Optional<Identifier> daggerHandle  = pick(k, Slot.DAGGER_HANDLE,      rnd);
		Optional<Identifier> horseArmor    = pick(k, Slot.HORSE_ARMOR_ITEM,   rnd);
		Optional<Identifier> wolfArmor     = pick(k, Slot.WOLF_ARMOR_ITEM,    rnd);
		Optional<Identifier> nautilusArmor = pick(k, Slot.NAUTILUS_ARMOR_ITEM, rnd);
		return new LegendaryTextureSet(
				hammerHead, hammerHandle, daggerBlade, daggerHandle,
				horseArmor, wolfArmor, nautilusArmor
		);
	}

	private CrystalTextureSet buildCrystalTextureSet(MaterialDef m, Random rnd) {
		var k = m.kind();
		Optional<Identifier> budding = (k == MaterialKind.CRYSTAL) ?
				pick(k, Slot.CRYSTAL_BUDDING, rnd) : Optional.empty();
		Optional<Identifier> budSmall = pick(k, Slot.CRYSTAL_BUD_S, rnd);
		Optional<Identifier> budMedium = pick(k, Slot.CRYSTAL_BUD_M, rnd);
		Optional<Identifier> budLarge = pick(k, Slot.CRYSTAL_BUD_L, rnd);
		Optional<Identifier> cluster = (k == MaterialKind.CRYSTAL) ?
				pick(k, Slot.CRYSTAL_CLUSTER, rnd) : Optional.empty();
		Optional<Identifier> crystalItem = pick(k, Slot.CRYSTAL_ITEM, rnd);
		Optional<Identifier> tintedGlass = pick(k, Slot.TINTED_CRYSTAL_GLASS, rnd);
		Optional<Identifier> crystalGlass = pick(k, Slot.CRYSTAL_GLASS, rnd);
		Optional<Identifier> crystalBricks = pick(k, Slot.CRYSTAL_BRICKS, rnd);
		Optional<Identifier> lampOverlay1 = pick(k, Slot.LAMP_OVERLAY_1, rnd);
		Optional<Identifier> lampOverlay2 = pick(k, Slot.LAMP_OVERLAY_2, rnd);
		Optional<Identifier> chime = pick(k, Slot.CHIME, rnd);

		return new CrystalTextureSet(
				budding, budSmall, budMedium, budLarge, cluster, crystalItem, tintedGlass,
				crystalGlass, crystalBricks, lampOverlay1, lampOverlay2, chime
		);
	}

	private DecorTextureSet buildDecorTextureSet(MaterialDef m, Random rnd) {
		var k = m.kind();
		Optional<Identifier> chain = pickForm(k, Form.CHAIN, rnd);
		Optional<Identifier> lantern = pickForm(k, Form.LANTERN, rnd);
		Optional<Identifier> lanternLight = pick(k, Slot.LANTERN_LIGHT, rnd);
		Optional<Identifier> doorItem = switch (k) {
			case METAL, ALLOY -> pick(k, Slot.DOOR_ITEM_METAL, rnd);
			case WOOD -> pick(k, Slot.DOOR_ITEM_WOOD, rnd);
			default -> Optional.empty();
		};
		Optional<Identifier> doorBottom = pickForm(k, Form.DOOR, rnd);
		Optional<Identifier> doorTop = doorBottom.map(AssetsTheme::pairedDoorTopTexture);
		Optional<Identifier> trapdoor = pickForm(k, Form.TRAPDOOR, rnd);
		Optional<Identifier> fence = pickForm(k, Form.FENCE, rnd);
		Optional<Identifier> fenceGate = pickForm(k, Form.FENCE_GATE, rnd);

		return new DecorTextureSet(chain, lantern, lanternLight, doorItem, doorBottom, doorTop, trapdoor, fence, fenceGate);
	}

	public MaterialAssetsDef resolve(MaterialDef m) {
		Random rnd = new Random(m.assetSeed());
		return new MaterialAssetsDef(
				1,
				m.nameInformation().id(),
				m.assetSeed(),
				buildBlockTextureSet(m, rnd),
				buildSandstoneTextureSet(m, rnd),
				buildItemTextureSet(m, rnd),
				buildToolTextureSet(m, rnd),
				buildDecorTextureSet(m, rnd),
				buildCrystalTextureSet(m, rnd),
				buildLegendaryTextureSet(m, rnd)
		);
	}

	public static AssetsTheme defaultTheme() {
		return AssetsThemeConfig.theme();
	}

	// ----- Slots -----
	public enum Slot implements StringRepresentable {
		ORE_VEIN_METAL, ORE_VEIN_GEM,
		STORAGE_BLOCK, STORAGE_METAL, STORAGE_GEM, STORAGE_CRYSTAL, STONE_DEFAULT,
		RAW_BLOCK,
		PLATE_SHEET, SHINGLES_SHEET,
		SANDSTONE_TOP, SANDSTONE_SIDE, SANDSTONE_BOTTOM, SANDSTONE_CUT, SANDSTONE_CHISELED,
		CRYSTAL_BUDDING, CRYSTAL_BUD_S, CRYSTAL_BUD_M, CRYSTAL_BUD_L, CRYSTAL_BLOCK, CRYSTAL_CLUSTER,
		CRYSTAL_ITEM, TINTED_CRYSTAL_GLASS, CRYSTAL_GLASS, CRYSTAL_BRICKS, LAMP_OVERLAY_1, LAMP_OVERLAY_2, CHIME,
		RAW_ITEM, INGOT_ITEM, DUST_ITEM, NUGGET_ITEM, PLATE_ITEM, GEAR_ITEM, GEM_ITEM, SHARD_ITEM, CLAY_ITEM,
		ROD_ITEM, WIRE_ITEM, COIL_ITEM, RIVET_ITEM, BOLT_ITEM, NAIL_ITEM, RING_ITEM, DOOR_ITEM_METAL, DOOR_ITEM_WOOD,
		PICK_HEAD, PICK_STICK, AXE_HEAD, AXE_STICK, SWORD_BLADE, SWORD_HANDLE,
		SHOVEL_HEAD, SHOVEL_STICK, HOE_HEAD, HOE_STICK,
		SHEARS_BASE, SHEARS_METAL, SPEAR_HEAD, SPEAR_HANDLE, SPEAR_HEAD_IN_HAND, SPEAR_HANDLE_IN_HAND,
		HAMMER_HEAD, HAMMER_HANDLE, DAGGER_BLADE, DAGGER_HANDLE,
		HORSE_ARMOR_ITEM, WOLF_ARMOR_ITEM, NAUTILUS_ARMOR_ITEM,
		BRICKS, POLISHED, TILES, MOSAIC, PILLAR_SIDE, PILLAR_TOP,
		COBBLESTONE, CHISELED, STONE_BRICKS, POLISHED_RANDOM,
		LANTERN_LIGHT;

		public static final Codec<Slot> CODEC = StringRepresentable.fromEnum(Slot::values);

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ROOT);
		}
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
