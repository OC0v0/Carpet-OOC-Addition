package carpetoocaddition.client;

import net.fabricmc.loader.api.FabricLoader;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * One cache shared by every singleplayer world and every server, kept across client restarts.
 * Written atomically (temp file + move) so several client processes on the same game directory
 * cannot observe a half-written file; read failures fall back to "disabled".
 */
public final class PackCache {

    private static final String KEY = "beacon_pack";
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("carpet-ooc-addition").resolve("client-pack.properties");

    private PackCache() {
    }

    public static boolean read() {
        if (!Files.isRegularFile(FILE)) return false;
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(FILE)) {
            properties.load(in);
            return Boolean.parseBoolean(properties.getProperty(KEY, "false"));
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void write(boolean enabled) {
        if (read() == enabled) return;

        Properties properties = new Properties();
        properties.setProperty(KEY, Boolean.toString(enabled));
        try {
            Files.createDirectories(FILE.getParent());
            Path temp = Files.createTempFile(FILE.getParent(), "client-pack", ".tmp");
            try (OutputStream out = Files.newOutputStream(temp)) {
                properties.store(out, "carpet-ooc-addition client cache; shared by all worlds and servers");
            }
            try {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception atomicUnsupported) {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ignored) {
            // Cache is best effort - never let it break the client.
        }
    }
}
