package carpetoocaddition;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

/**
 * Fully hardcoded master-level trades. Vanilla rolls random enchantments for these; the rules replace
 * the whole master trade set with these fixed offers instead.
 */
public final class MasterTrades {

    private static final int MAX_USES = 3;
    private static final int XP = 30;
    private static final float PRICE_MULTIPLIER = 0.05f;

    private MasterTrades() {
    }

    /** 331: Unbreaking III, Efficiency III, Silk Touch I. */
    public static List<MerchantOffer> toolsmithPickaxe(ServerLevel level) {
        Map<ResourceKey<Enchantment>, Integer> enchantments = new LinkedHashMap<>();
        enchantments.put(Enchantments.UNBREAKING, 3);
        enchantments.put(Enchantments.EFFICIENCY, 3);
        enchantments.put(Enchantments.SILK_TOUCH, 1);

        return List.of(offer(Items.DIAMOND_PICKAXE, 31, enchantments, level));
    }

    /** Expert (4): leggings and boots, which is what vanilla sells at that level. */
    public static List<MerchantOffer> armorerExpertArmor(ServerLevel level) {
        List<MerchantOffer> offers = new ArrayList<>();

        Map<ResourceKey<Enchantment>, Integer> leggings = new LinkedHashMap<>();
        leggings.put(Enchantments.MENDING, 1);
        leggings.put(Enchantments.UNBREAKING, 3);
        leggings.put(Enchantments.SWIFT_SNEAK, 3);
        leggings.put(Enchantments.PROTECTION, 4);
        offers.add(offer(Items.DIAMOND_LEGGINGS, 32, leggings, level));

        Map<ResourceKey<Enchantment>, Integer> boots = new LinkedHashMap<>();
        boots.put(Enchantments.MENDING, 1);
        boots.put(Enchantments.UNBREAKING, 3);
        boots.put(Enchantments.PROTECTION, 4);
        boots.put(Enchantments.FEATHER_FALLING, 4);
        boots.put(Enchantments.DEPTH_STRIDER, 3);
        offers.add(offer(Items.DIAMOND_BOOTS, 23, boots, level));

        return offers;
    }

    /** Master (5): helmet and chestplate, which is what vanilla sells at that level. */
    public static List<MerchantOffer> armorerMasterArmor(ServerLevel level) {
        List<MerchantOffer> offers = new ArrayList<>();

        Map<ResourceKey<Enchantment>, Integer> helmet = new LinkedHashMap<>();
        helmet.put(Enchantments.MENDING, 1);
        helmet.put(Enchantments.AQUA_AFFINITY, 1);
        helmet.put(Enchantments.UNBREAKING, 3);
        helmet.put(Enchantments.RESPIRATION, 3);
        helmet.put(Enchantments.PROTECTION, 4);
        offers.add(offer(Items.DIAMOND_HELMET, 26, helmet, level));

        Map<ResourceKey<Enchantment>, Integer> chestplate = new LinkedHashMap<>();
        chestplate.put(Enchantments.MENDING, 1);
        chestplate.put(Enchantments.UNBREAKING, 3);
        chestplate.put(Enchantments.PROTECTION, 4);
        offers.add(offer(Items.DIAMOND_CHESTPLATE, 30, chestplate, level));

        return offers;
    }

    private static MerchantOffer offer(Item item, int emeralds,
                                       Map<ResourceKey<Enchantment>, Integer> enchantments, ServerLevel level) {
        return new MerchantOffer(new ItemCost(Items.EMERALD, emeralds), Optional.empty(),
                enchanted(level, item, enchantments), MAX_USES, XP, PRICE_MULTIPLIER);
    }

    private static ItemStack enchanted(ServerLevel level, Item item,
                                       Map<ResourceKey<Enchantment>, Integer> enchantments) {
        Registry<Enchantment> registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        for (Map.Entry<ResourceKey<Enchantment>, Integer> entry : enchantments.entrySet()) {
            Holder<Enchantment> enchantment = registry.get(entry.getKey())
                    .orElseThrow(() -> new IllegalStateException("Unknown enchantment " + entry.getKey()));
            mutable.set(enchantment, entry.getValue());
        }

        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }
}
