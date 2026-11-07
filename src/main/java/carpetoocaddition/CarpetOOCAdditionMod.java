package carpetoocaddition;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class CarpetOOCAdditionMod implements ModInitializer {

    @Override
    public void onInitialize() {
        String version = FabricLoader.getInstance()
                .getModContainer(CarpetOOCAdditionServer.MOD_ID)
                .map(container -> container.getMetadata().getVersion().getFriendlyString())
                .orElse("unknown");
        CarpetOOCAdditionServer.init(version);
        carpetoocaddition.net.OocRuleSync.register();
    }
}
