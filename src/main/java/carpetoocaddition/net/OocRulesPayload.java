package carpetoocaddition.net;

import carpetoocaddition.CarpetOOCAdditionServer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Server -> client: which of the client-visible rules are active. Vanilla clients simply log
 * "Unknown custom packet payload" and keep playing, so the mod is never required on the client.
 */
public record OocRulesPayload(boolean beaconSaturation) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OocRulesPayload> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(CarpetOOCAdditionServer.MOD_ID, "rules"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OocRulesPayload> CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeBoolean(payload.beaconSaturation()),
            buf -> new OocRulesPayload(buf.readBoolean()));

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
