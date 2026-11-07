package carpetoocaddition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.utils.Translations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CarpetOOCAdditionServer implements CarpetExtension {

    public static final String MOD_ID = "carpet-ooc-addition";
    public static final String MOD_NAME = "Carpet OOC Addition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static final CarpetOOCAdditionServer INSTANCE = new CarpetOOCAdditionServer();
    private static String version = "unknown";

    public static void init(String modVersion) {
        version = modVersion;
        CarpetServer.manageExtension(INSTANCE);
    }

    @Override
    public String version() {
        return MOD_ID + " " + version;
    }

    @Override
    public void onGameStarted() {
        // Rule descriptions are resolved from Carpet's translation map while the rules are parsed,
        // so fold this extension's entries in first (canHasTranslations is called by updateLanguage).
        Translations.updateLanguage();
        CarpetServer.settingsManager.parseSettingsClass(CarpetOOCAdditionSettings.class);
    }

    @Override
    public Map<String, String> canHasTranslations(String language) {
        return loadTranslations(language);
    }

    /**
     * Carpet merges this map into its own translation storage. Keys are written in the resource files
     * under the {@code carpetoocaddition.carpet_translations.} prefix (so the client never sees them
     * twice) and are stripped to plain {@code carpet.*} keys here, the same trick TIS/AMS use.
     */
    private static Map<String, String> loadTranslations(String language) {
        var container = FabricLoader.getInstance().getModContainer(MOD_ID).orElse(null);
        if (container == null) return Map.of();

        for (String candidate : new String[]{language, "en_us"}) {
            var path = container.findPath("assets/" + MOD_ID + "/lang/" + candidate + ".json").orElse(null);
            if (path == null) continue;

            try (InputStream in = java.nio.file.Files.newInputStream(path)) {
                Map<String, String> raw = new Gson().fromJson(
                        new InputStreamReader(in, StandardCharsets.UTF_8),
                        new TypeToken<Map<String, String>>() {
                        }.getType());
                if (raw == null || raw.isEmpty()) continue;

                Map<String, String> result = new HashMap<>();
                raw.forEach((key, value) -> result.put(
                        key.replaceFirst("^carpetoocaddition\\.carpet_translations\\.", ""), value));
                if (!result.isEmpty()) return result;
            } catch (Exception e) {
                LOGGER.warn("Failed to load translations for {}", candidate, e);
            }
        }
        return Map.of();
    }
}
