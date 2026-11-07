package carpetoocaddition.client;

/** Rule state mirrored from the server. False until the server tells us otherwise. */
public final class ClientRuleState {

    private static boolean beaconSaturation = false;

    private ClientRuleState() {
    }

    public static boolean beaconSaturation() {
        return beaconSaturation;
    }

    public static void setBeaconSaturation(boolean value) {
        beaconSaturation = value;
    }
}
