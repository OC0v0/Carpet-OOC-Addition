package carpetoocaddition.mixins;

import carpetoocaddition.CarpetOOCAdditionSettings;
import carpetoocaddition.MasterTrades;
import carpetoocaddition.TradeFixes;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    /** Same gossip a zombie villager cure grants: MAJOR_POSITIVE 20 + MINOR_POSITIVE 25 = 125 reputation. */
    @Unique
    private static final int CARPETOOCADDITION_CURE_MAJOR_GOSSIP = 20;
    @Unique
    private static final int CARPETOOCADDITION_CURE_MINOR_GOSSIP = 25;

    /** 3 seconds of Regeneration V (amplifier 4, since levels are zero based). */
    @Unique
    private static final int CARPETOOCADDITION_REGEN_DURATION = 60;
    @Unique
    private static final int CARPETOOCADDITION_REGEN_AMPLIFIER = 4;

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void carpetoocaddition$applyTradeRules(ServerLevel serverLevel, CallbackInfo ci) {
        TradeFixes.apply((Villager) (Object) this, this.offers);
    }

    /**
     * Replaces the master-level (5) trade set with the hardcoded offers: a 331 diamond pickaxe for
     * master toolsmiths, the best enchanted diamond armor for armorers - leggings/boots at expert (4)
     * and helmet/chestplate at master (5), mirroring which slots vanilla sells at those levels.
     */
    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    private void carpetoocaddition$overrideMasterTrades(ServerLevel serverLevel, CallbackInfo ci) {
        Villager self = (Villager) (Object) this;
        int level = self.getVillagerData().level();
        if (level < 4) return;

        boolean toolsmith = self.getVillagerData().profession().is(VillagerProfession.TOOLSMITH);
        boolean armorer = self.getVillagerData().profession().is(VillagerProfession.ARMORER);

        if (CarpetOOCAdditionSettings.masterToolsmithSilkPickaxe && toolsmith && level >= 5) {
            if (this.offers == null) this.offers = new MerchantOffers();
            this.offers.addAll(MasterTrades.toolsmithPickaxe(serverLevel));
            ci.cancel();
        } else if (CarpetOOCAdditionSettings.armorerBestArmor && armorer) {
            if (this.offers == null) this.offers = new MerchantOffers();
            if (level == 4) {
                this.offers.addAll(MasterTrades.armorerExpertArmor(serverLevel));
                ci.cancel();
            } else if (level >= 5) {
                this.offers.addAll(MasterTrades.armorerMasterArmor(serverLevel));
                ci.cancel();
            }
        }
    }

    /** Bare-handed hits keep the villager alive while you farm the discount. */
    @Inject(method = "setLastHurtByMob", at = @At("HEAD"))
    private void carpetoocaddition$bareHandRegeneration(LivingEntity attacker, CallbackInfo ci) {
        if (!CarpetOOCAdditionSettings.bareHandVillagerRegeneration) return;
        if (!(attacker instanceof ServerPlayer player)) return;
        if (!player.getMainHandItem().isEmpty()) return;
        if (this.level().isClientSide()) return;

        this.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                CARPETOOCADDITION_REGEN_DURATION, CARPETOOCADDITION_REGEN_AMPLIFIER, false, true, true));
    }

    /**
     * Normally being hit fires {@code VILLAGER_HURT} reputation, which makes everything more expensive.
     * With the rule on the player instead earns exactly the gossip a zombie villager cure grants, so the
     * discount is permanent (MAJOR_POSITIVE does not decay) and - with the cap lifted - stacks.
     */
    @WrapOperation(
            method = "setLastHurtByMob",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;onReputationEvent(Lnet/minecraft/world/entity/ai/village/ReputationEventType;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/ReputationEventHandler;)V")
    )
    private void carpetoocaddition$cureGossipInsteadOfHate(ServerLevel level, ReputationEventType type, Entity entity,
                                                           ReputationEventHandler handler, Operation<Void> original) {
        if (CarpetOOCAdditionSettings.attackVillagerDiscount && entity instanceof ServerPlayer player) {
            ((Villager) (Object) this).getGossips().add(player.getUUID(), GossipType.MAJOR_POSITIVE, CARPETOOCADDITION_CURE_MAJOR_GOSSIP);
            ((Villager) (Object) this).getGossips().add(player.getUUID(), GossipType.MINOR_POSITIVE, CARPETOOCADDITION_CURE_MINOR_GOSSIP);
            return;
        }
        original.call(level, type, entity, handler);
    }

    /**
     * Receiver-side guard: killing a villager notifies nearby villagers with {@code VILLAGER_KILLED}.
     * With the rule on that is ignored as well, so violence never spreads negative gossip to other
     * villagers (which is what would raise their prices for the killer).
     */
    @Inject(method = "onReputationEventFrom", at = @At("HEAD"), cancellable = true)
    private void carpetoocaddition$ignoreViolenceReputation(ReputationEventType type, Entity entity, CallbackInfo ci) {
        if (!CarpetOOCAdditionSettings.attackVillagerDiscount) return;
        if (!(entity instanceof ServerPlayer)) return;
        if (type == ReputationEventType.VILLAGER_HURT || type == ReputationEventType.VILLAGER_KILLED) {
            ci.cancel();
        }
    }
}
