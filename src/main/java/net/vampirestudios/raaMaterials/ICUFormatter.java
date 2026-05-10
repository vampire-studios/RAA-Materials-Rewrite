package net.vampirestudios.raaMaterials;

import com.ibm.icu.text.MessageFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;

import java.util.Locale;

/**
 * Resolves lang keys and formats them using ICU MessageFormat with the client's active locale.
 * This enables language-aware select patterns in translations, e.g. French vowel elision:
 * {@code "block.raa_materials.form.ore": "Minerai {2, select, a {d'{1}} ... other {de {1}}}"}
 */
public final class ICUFormatter {
    private ICUFormatter() {}

    /** Resolve a lang key to its translation pattern for the current language. */
    public static String resolve(String key) {
        return Language.getInstance().getOrDefault(key);
    }

    /** Format a lang key's translation pattern with ICU MessageFormat and the client's locale. */
    public static String format(String key, Object... args) {
        String pattern = resolve(key);
        return new MessageFormat(pattern, clientLocale()).format(args);
    }

    public static String clientLanguageCode() {
        String code = Minecraft.getInstance().getLanguageManager().getSelected();
        return code == null ? "" : code;
    }

    private static Locale clientLocale() {
        String code = clientLanguageCode();
        if (code.isEmpty()) return Locale.ROOT;
        String[] parts = code.split("_", 2);
        return parts.length == 2
                ? new Locale(parts[0], parts[1].toUpperCase(Locale.ROOT))
                : new Locale(parts[0]);
    }
}
