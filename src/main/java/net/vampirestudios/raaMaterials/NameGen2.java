// NameGen2.java
package net.vampirestudios.raaMaterials;

import com.ibm.icu.text.MessageFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.server.MinecraftServer;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NameGen2 {
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    private NameGen2() {}

    /** Resolve a lang pattern for the current language (falls back to the key). */
    public static String resolve(String key) {
        return Language.getInstance().getOrDefault(key);
    }

    /** Format ICU MessageFormat with the client's selected language. */
    public static String format(String key, Object... args) {
        String pattern = resolve(key);
        return new MessageFormat(pattern, clientLocale()).format(args);
    }

    private static Locale clientLocale() {
        String code = Minecraft.getInstance().getLanguageManager().getSelected();
        if (code == null || code.isEmpty()) return Locale.ROOT;

        // Code is like "fr_fr", "en_us"
        String[] parts = code.split("_", 2);
        return parts.length == 2
                ? new Locale(parts[0], parts[1].toUpperCase(Locale.ROOT))
                : new Locale(parts[0]);
    }
}
