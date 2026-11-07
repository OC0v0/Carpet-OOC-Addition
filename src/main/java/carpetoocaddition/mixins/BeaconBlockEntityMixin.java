package carpetoocaddition.mixins;

import carpetoocaddition.CarpetOOCAdditionSettings;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Beacons offer Regeneration as their highest-tier effect. With the rule on, the effect that actually
 * gets applied is Saturation instead. The client-side list stays vanilla, so the bundled resource pack
 * handles how that entry looks (icon + name).
 */
@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {

    @ModifyVariable(method = "applyEffects", at = @At("HEAD"), argsOnly = true, index = 3)
    private static Holder<MobEffect> carpetoocaddition$saturationPrimary(Holder<MobEffect> effect) {
        return carpetoocaddition$replace(effect);
    }

    @ModifyVariable(method = "applyEffects", at = @At("HEAD"), argsOnly = true, index = 4)
    private static Holder<MobEffect> carpetoocaddition$saturationSecondary(Holder<MobEffect> effect) {
        return carpetoocaddition$replace(effect);
    }

    private static Holder<MobEffect> carpetoocaddition$replace(Holder<MobEffect> effect) {
        if (CarpetOOCAdditionSettings.beaconRegenerationToSaturation
                && effect != null && effect.value() == MobEffects.REGENERATION.value()) {
            return MobEffects.SATURATION;
        }
        return effect;
    }
}
