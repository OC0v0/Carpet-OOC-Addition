package carpetoocaddition.client;

import carpetoocaddition.CarpetOOCAdditionServer;
import net.minecraft.client.Minecraft;

import java.util.List;

/**
 * Applies the desired pack state to the pack selection, reloading only when the selection actually
 * changes and never starting a second reload while one is still running. That keeps repeated packets
 * or rapid rule flips from piling up reloads.
 */
public final class ClientPackController {

    private static final String PACK_ID = CarpetOOCAdditionServer.MOD_ID + "/ooc_beacon";

    private static boolean reloading;
    private static Boolean desired;

    private ClientPackController() {
    }

    public static void apply(boolean enabled) {
        desired = enabled;
        if (!reloading) {
            push(enabled);
        }
    }

    private static void push(boolean enabled) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return;

        List<String> packs = minecraft.options.resourcePacks;
        boolean selected = packs.contains(PACK_ID);
        if (enabled == selected) return;

        if (enabled) {
            packs.add(PACK_ID);
        } else {
            packs.remove(PACK_ID);
        }

        reloading = true;
        try {
            minecraft.reloadResourcePacks().whenComplete((ignored, throwable) -> {
                reloading = false;
                if (desired != null && desired != enabled) {
                    push(desired);
                }
            });
        } catch (Throwable throwable) {
            reloading = false;
        }
    }
}
