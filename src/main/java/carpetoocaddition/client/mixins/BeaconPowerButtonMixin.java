package carpetoocaddition.client.mixins;

import carpetoocaddition.client.ClientRuleState;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Draws the beacon's Regeneration entry as Saturation (icon + name) on the client, using the rule state
 * synced from the server. Only this screen is affected - no global translation or texture changes, and
 * clients without the mod keep the vanilla look.
 *
 * The icon goes through {@link Gui#getMobEffectSprite}, i.e. the same baked GUI sprite vanilla uses for
 * effect icons. Drawing a loose texture with {@code blit} does not work here: non-atlas textures are not
 * registered with the texture manager and render as the missing-texture checkerboard.
 */
@Mixin(targets = "net.minecraft.client.gui.screens.inventory.BeaconScreen$BeaconPowerButton")
public class BeaconPowerButtonMixin {

    @Shadow
    private Holder<MobEffect> effect;

    @Inject(method = "renderIcon", at = @At("HEAD"), cancellable = true)
    private void carpetoocaddition$drawSaturationIcon(GuiGraphics graphics, CallbackInfo ci) {
        if (!ClientRuleState.beaconSaturation() || this.effect.value() != MobEffects.REGENERATION.value()) return;

        Identifier sprite = Gui.getMobEffectSprite(MobEffects.SATURATION);
        // One pixel larger than vanilla's 18x18 so the drumstick reads at the same visual weight as the other icons.
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite,
                ((net.minecraft.client.gui.components.AbstractWidget) (Object) this).getX() + 1, ((net.minecraft.client.gui.components.AbstractWidget) (Object) this).getY() + 1, 20, 20);
        ci.cancel();
    }

    @ModifyReturnValue(method = "createEffectDescription", at = @At("RETURN"))
    private MutableComponent carpetoocaddition$beaconSaturationName(MutableComponent original, Holder<MobEffect> effect) {
        if (ClientRuleState.beaconSaturation() && effect.value() == MobEffects.REGENERATION.value()) {
            return Component.translatable("effect.minecraft.saturation");
        }
        return original;
    }
}
