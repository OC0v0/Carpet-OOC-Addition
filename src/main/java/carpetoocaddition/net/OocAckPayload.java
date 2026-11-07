package carpetoocaddition.net;

import carpetoocaddition.CarpetOOCAdditionServer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/** Client -> server: "I have the mod and I applied the state." Vanilla clients never send this. */
public record OocAckPayload() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OocAckPayload> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(CarpetOOCAdditionServer.MOD_ID, "rules_ack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OocAckPayload> CODEC = StreamCodec.unit(new OocAckPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
