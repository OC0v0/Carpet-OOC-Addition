package carpetoocaddition.mixins;

import carpetoocaddition.CarpetOOCAdditionSettings;
import carpet.api.settings.Rule;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Vanilla clamps every gossip value to {@code GossipType#max} (20 for MAJOR_POSITIVE), which is exactly
 * why curing a zombie villager only ever grants one discount. With the rule on, the clamp is skipped for
 * MAJOR_POSITIVE so repeated cures - and the "one hit = one cure" gossip - keep stacking.
 */
@Mixin(GossipContainer.class)
public class GossipContainerMixin {

    @ModifyReturnValue(method = "mergeValuesForAddition", at = @At("RETURN"))
    private int carpetoocaddition$liftMajorPositiveCap(int original, GossipType type, int existing, int added) {
        if (CarpetOOCAdditionSettings.attackVillagerDiscount && type == GossipType.MAJOR_POSITIVE) {
            return existing + added;
        }
        return original;
    }
}
