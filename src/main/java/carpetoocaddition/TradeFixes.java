package carpetoocaddition;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class TradeFixes {

    private TradeFixes() {
    }

    /**
     * Applies every enabled villager trade rule to the given offers. Called both when the offers are
     * (re)generated and when an already generated list is opened.
     */
    public static void apply(Villager villager, MerchantOffers offers) {
        if (offers == null || offers.isEmpty()) return;

        if (CarpetOOCAdditionSettings.fletcherWeaknessArrows
                && villager.getVillagerData().profession().is(VillagerProfession.FLETCHER)) {
            lockTippedArrows(offers);
        } else if (CarpetOOCAdditionSettings.librarianMaxLevelBooks
                && villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN)) {
            lockMaxLevelBooks(offers);
        }
    }

    /**
     * Vanilla builds tipped-arrow offers in {@code VillagerTrades.TippedArrowForItemsAndEmeralds}
     * with a random potion; rewrite that component to Weakness.
     */
    private static void lockTippedArrows(MerchantOffers offers) {
        Holder<Potion> weakness = Potions.WEAKNESS;

        for (MerchantOffer offer : offers) {
            ItemStack result = offer.getResult();
            if (!result.is(Items.TIPPED_ARROW)) continue;

            PotionContents contents = result.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.potion().filter(holder -> holder.value() == weakness.value()).isPresent()) {
                continue;
            }
            result.set(DataComponents.POTION_CONTENTS, new PotionContents(weakness));
        }
    }

    /**
     * Vanilla rolls a random level in {@code [getMinLevel(), getMaxLevel()]}; raise every stored
     * enchantment of a book offer to its own registered maximum. The max level is read from the
     * enchantment registry, so modded/datapack enchantments work without any hardcoded table.
     */
    private static void lockMaxLevelBooks(MerchantOffers offers) {
        for (MerchantOffer offer : offers) {
            ItemStack result = offer.getResult();
            if (!result.is(Items.ENCHANTED_BOOK)) continue;

            ItemEnchantments stored = result.get(DataComponents.STORED_ENCHANTMENTS);
            if (stored == null || stored.isEmpty()) continue;

            ItemEnchantments.Mutable maxed = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
            boolean differs = false;

            for (var entry : stored.entrySet()) {
                Holder<Enchantment> enchantment = entry.getKey();
                int maxLevel = enchantment.value().getMaxLevel();
                maxed.set(enchantment, maxLevel);
                if (entry.getIntValue() != maxLevel) differs = true;
            }

            if (!differs) continue;
            result.set(DataComponents.STORED_ENCHANTMENTS, maxed.toImmutable());
        }
    }
}
