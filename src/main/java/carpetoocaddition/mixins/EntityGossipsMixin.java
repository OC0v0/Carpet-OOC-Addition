package carpetoocaddition.mixins;

import carpetoocaddition.CarpetOOCAdditionSettings;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.world.entity.ai.gossip.GossipType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code GossipContainer#add} merges the value and then calls this method, which clamps it back to
 * {@code GossipType#max} (20 for MAJOR_POSITIVE) - so lifting the cap in
 * {@link GossipContainerMixin} alone is not enough. With the rule on this skips the upper clamp for
 * MAJOR_POSITIVE while keeping vanilla's "drop values below 2" cleanup.
 */
@Mixin(targets = "net.minecraft.world.entity.ai.gossip.GossipContainer$EntityGossips")
public class EntityGossipsMixin {

    @Shadow
    @Final
    private Object2IntMap<GossipType> entries;

    @Inject(method = "makeSureValueIsntTooLowOrTooHigh", at = @At("HEAD"), cancellable = true)
    private void carpetoocaddition$skipMajorPositiveCap(GossipType type, CallbackInfo ci) {
        if (!CarpetOOCAdditionSettings.attackVillagerDiscount || type != GossipType.MAJOR_POSITIVE) return;

        if (this.entries.getInt(type) < 2) {
            this.entries.removeInt(type);
        }
        ci.cancel();
    }
}
