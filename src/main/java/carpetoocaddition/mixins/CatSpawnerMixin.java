package carpetoocaddition.mixins;

import carpetoocaddition.CarpetOOCAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.CatSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla spawns village cats from {@code CatSpawner#tick} via {@code spawnInVillage}; witch hut cats
 * use {@code spawnInHut} and are intentionally left alone.
 */
@Mixin(CatSpawner.class)
public class CatSpawnerMixin {

    @Inject(method = "spawnInVillage", at = @At("HEAD"), cancellable = true)
    private void carpetoocaddition$disableVillageCats(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        if (CarpetOOCAdditionSettings.disableVillageCats) {
            ci.cancel();
        }
    }
}
