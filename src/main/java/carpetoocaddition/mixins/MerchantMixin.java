package carpetoocaddition.mixins;

import carpetoocaddition.TradeFixes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Villagers whose offer list came from the save file are only re-sent, never regenerated, so the
 * rules have to be applied at the moment the screen opens as well.
 */
@Mixin(Merchant.class)
public interface MerchantMixin {

    @Inject(method = "openTradingScreen", at = @At("HEAD"))
    private void carpetoocaddition$applyTradeRules(Player player, Component displayName, int merchantLevel, CallbackInfo ci) {
        if (!((Object) this instanceof Villager villager)) return;
        // AbstractVillager#getOffers throws on the client; a merchant screen only ever opens on the server.
        if (!(villager.level() instanceof ServerLevel)) return;

        TradeFixes.apply(villager, villager.getOffers());
    }
}
