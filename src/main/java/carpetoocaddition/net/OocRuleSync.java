package carpetoocaddition.net;

import carpet.api.settings.SettingsManager;
import carpetoocaddition.CarpetOOCAdditionSettings;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Sends the client-relevant rule state on join and whenever the rule actually changes (via Carpet's
 * rule observer - no polling). Clients that have the mod answer with an ack; until then the packet is
 * repeated once per second for up to {@link #MAX_ATTEMPTS} seconds, which also covers vanilla clients
 * (they never ack and simply stop receiving after the window).
 */
public final class OocRuleSync {

    private static final String RULE_NAME = "beaconRegenerationToSaturation";
    private static final String FAKE_PLAYER_CLASS = "carpet.patches.EntityPlayerMPFake";
    private static final int MAX_ATTEMPTS = 10;

    /** Player -> remaining attempts. Empty most of the time. */
    private static final Map<UUID, Integer> PENDING = new HashMap<>();

    private OocRuleSync() {
    }

    public static void register() {
        PayloadTypeRegistry.playS2C().register(OocRulesPayload.TYPE, OocRulesPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(OocAckPayload.TYPE, OocAckPayload.CODEC);

        SettingsManager.registerGlobalRuleObserver((source, rule, value) -> {
            if (RULE_NAME.equals(rule.name())) {
                broadcast();
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> send(handler.player, MAX_ATTEMPTS));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> PENDING.remove(handler.player.getUUID()));

        ServerPlayNetworking.registerGlobalReceiver(OocAckPayload.TYPE, (payload, context) ->
                context.server().execute(() -> PENDING.remove(context.player().getUUID())));

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ServerHolder.set(server);
            if (PENDING.isEmpty() || server.getTickCount() % 20 != 0) return;

            Iterator<Map.Entry<UUID, Integer>> iterator = PENDING.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, Integer> entry = iterator.next();
                int left = entry.getValue();
                ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                if (player == null || left <= 0) {
                    iterator.remove();
                    continue;
                }
                sendPacket(player);
                entry.setValue(left - 1);
            }
        });
    }

    private static void broadcast() {
        MinecraftServer server = ServerHolder.server();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            send(player, MAX_ATTEMPTS);
        }
    }

    private static void send(ServerPlayer player, int attempts) {
        if (isFakePlayer(player)) return;
        sendPacket(player);
        PENDING.put(player.getUUID(), attempts);
    }

    private static void sendPacket(ServerPlayer player) {
        ServerPlayNetworking.send(player,
                new OocRulesPayload(CarpetOOCAdditionSettings.beaconRegenerationToSaturation));
    }

    /** Carpet fake players have no real connection - never send to them. */
    private static boolean isFakePlayer(ServerPlayer player) {
        return player.getClass().getName().equals(FAKE_PLAYER_CLASS);
    }

    /** The tick callback gives us the server instance; keep it for the rule observer path. */
    private static final class ServerHolder {
        private static MinecraftServer instance;

        static MinecraftServer server() {
            return instance;
        }

        static void set(MinecraftServer server) {
            instance = server;
        }
    }
}
