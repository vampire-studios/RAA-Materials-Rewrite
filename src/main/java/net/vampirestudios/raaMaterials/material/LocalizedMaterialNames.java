package net.vampirestudios.raaMaterials.material;

import net.vampirestudios.raaMaterials.NameGen2;

import java.util.Locale;

public final class LocalizedMaterialNames {
	private LocalizedMaterialNames() {
	}

	public static String displayName(MaterialDef def) {
		return displayName(def, NameGen2.clientLanguageCode());
	}

	static String displayName(MaterialDef def, String languageCode) {
		String base = def.nameInformation().displayName();
		String lang = languageCode == null ? "" : languageCode.toLowerCase(Locale.ROOT);

		if (lang.startsWith("fr")) {
			return french(base, def.kind());
		}
		if (lang.startsWith("nb") || lang.startsWith("nn") || lang.startsWith("no") || lang.endsWith("_no")) {
			return norwegian(base, def.kind());
		}
		return base;
	}

	private static String french(String base, MaterialKind kind) {
		return switch (kind) {
			case SAND -> replaceTerminal(base,
					"sandstone", "gr\u00e8s",
					"arenite", "ar\u00e9nite",
					"psammite", "psammite",
					"sand", "sable");
			case GRAVEL -> replaceTerminal(base,
					"conglomerate", "conglom\u00e9rat",
					"breccia", "br\u00e8che",
					"gravel", "gravier");
			case CLAY -> replaceTerminal(base,
					"argillite", "argilite",
					"kaolinite", "kaolinite",
					"mudstone", "p\u00e9lite",
					"shale", "schiste",
					"clay", "argile");
			case MUD -> replaceTerminal(base,
					"mudstone", "p\u00e9lite",
					"lutite", "lutite",
					"pelite", "p\u00e9lite",
					"mud", "boue");
			case SOIL -> replaceTerminal(base,
					"soil", "sol",
					"dirt", "terre",
					"humus", "humus");
			case SALT -> replaceTerminal(base,
					"evaporite", "\u00e9vaporite",
					"salt", "sel");
			case VOLCANIC -> replaceTerminal(base,
					"tephra", "t\u00e9phra",
					"scoria", "scorie",
					"pumice", "ponce",
					"glass", "verre",
					"tuff", "tuf");
			case WOOD -> replaceTerminal(base,
					"heartwood", "duramen",
					"timber", "bois",
					"plank", "planche",
					"bark", "\u00e9corce",
					"wood", "bois");
			default -> base;
		};
	}

	private static String norwegian(String base, MaterialKind kind) {
		return switch (kind) {
			case SAND -> replaceTerminal(base,
					"sandstone", "sandstein",
					"arenite", "arenitt",
					"psammite", "psammitt",
					"sand", "sand");
			case GRAVEL -> replaceTerminal(base,
					"conglomerate", "konglomerat",
					"breccia", "breksje",
					"gravel", "grus");
			case CLAY -> replaceTerminal(base,
					"argillite", "argillitt",
					"kaolinite", "kaolinitt",
					"mudstone", "slamstein",
					"shale", "skifer",
					"clay", "leire");
			case MUD -> replaceTerminal(base,
					"mudstone", "slamstein",
					"lutite", "lutitt",
					"pelite", "pelitt",
					"mud", "slam");
			case SOIL -> replaceTerminal(base,
					"soil", "jord",
					"dirt", "jord",
					"humus", "humus");
			case SALT -> replaceTerminal(base,
					"evaporite", "evaporitt",
					"salt", "salt");
			case VOLCANIC -> replaceTerminal(base,
					"tephra", "tefra",
					"scoria", "slagg",
					"pumice", "pimpstein",
					"glass", "glass",
					"tuff", "tuff");
			case WOOD -> replaceTerminal(base,
					"heartwood", "kjerneved",
					"timber", "trevirke",
					"plank", "planke",
					"bark", "bark",
					"wood", "tre");
			default -> base;
		};
	}

	private static String replaceTerminal(String base, String... replacements) {
		String lower = base.toLowerCase(Locale.ROOT);
		for (int i = 0; i < replacements.length; i += 2) {
			String from = replacements[i];
			String to = replacements[i + 1];
			if (lower.endsWith(from)) {
				String replaced = base.substring(0, base.length() - from.length()) + to;
				return capitalizeFirst(replaced);
			}
		}
		return base;
	}

	private static String capitalizeFirst(String value) {
		if (value.isEmpty()) {
			return value;
		}
		return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
	}
}
