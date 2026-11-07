package carpetoocaddition.client;

import carpetoocaddition.CarpetOOCAdditionServer;
import carpetoocaddition.net.OocAckPayload;
import carpetoocaddition.net.OocRulesPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class OocClientInit implements ClientModInitializer {

    /** Slightly longer than the server's 10 second retry window. */
    private static final long SYNC_TIMEOUT_SECONDS = 11;

    private static final ScheduledExecutorService TIMER = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "carpet-ooc-addition-sync");
        thread.setDaemon(true);
        return thread;
    });

    private static ScheduledFuture<?> timeout;
    private static boolean syncedThisSession;
    private static boolean startupApplied;

    @Override
    public void onInitializeClient() {
        // Always registered, but only enabled according to the cache / the server's state.
        ResourceManagerHelper.registerBuiltinResourcePack(
                Identifier.fromNamespaceAndPath(CarpetOOCAdditionServer.MOD_ID, "ooc_beacon"),
                FabricLoader.getInstance().getModContainer(CarpetOOCAdditionServer.MOD_ID).orElseThrow(),
                ResourcePackActivationType.NORMAL);

        ClientPlayNetworking.registerGlobalReceiver(OocRulesPayload.TYPE, (payload, context) ->
                context.client().execute(() -> {
                    syncedThisSession = true;
                    cancelTimeout();
                    ClientRuleState.setBeaconSaturation(payload.beaconSaturation());
                    PackCache.write(payload.beaconSaturation());
                    ClientPackController.apply(payload.beaconSaturation());
                    ClientPlayNetworking.send(new OocAckPayload());
                }));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            syncedThisSession = false;
            scheduleTimeout();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            cancelTimeout();
            syncedThisSession = false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (startupApplied) return;
            startupApplied = true;
            // Start the client with whatever the shared cache says; no world/server needed.
            ClientPackController.apply(PackCache.read());
        });
    }

    /** A server that never sends the sync packet (vanilla, or no mod) must not keep the pack loaded. */
    private static void scheduleTimeout() {
        cancelTimeout();
        timeout = TIMER.schedule(() -> {
            if (syncedThisSession) return;
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft == null) return;
            minecraft.execute(() -> {
                if (syncedThisSession) return;
                ClientRuleState.setBeaconSaturation(false);
                PackCache.write(false);
                ClientPackController.apply(false);
            });
        }, SYNC_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    private static void cancelTimeout() {
        if (timeout != null) {
            timeout.cancel(false);
            timeout = null;
        }
    }
}
