package carpetoocaddition;

import carpet.api.settings.Rule;

/**
 * All rules default to vanilla behaviour; nothing here changes anything until a rule is flipped.
 */
public class CarpetOOCAdditionSettings {

    public static final String OOC = "OOC";

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean fletcherWeaknessArrows = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean librarianMaxLevelBooks = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static HeadlessPistonDrops headlessPistonDrops = HeadlessPistonDrops.FALSE;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean disableVillageCats = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean attackVillagerDiscount = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean bareHandVillagerRegeneration = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean masterToolsmithSilkPickaxe = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean armorerBestArmor = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean hugeMushroomReplaceEndPortalFrame = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean beaconRegenerationToSaturation = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean netheriteUpgradeWithoutTemplate = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean axeMakesHeadlessPiston = false;

    @Rule(categories = {OOC, "survival", "creative"})
    public static boolean disableOpOnlyCustomData = false;

    public enum HeadlessPistonDrops {
        /** Vanilla: the eaten block disappears without drops. */
        FALSE,
        /** Only blocks whose loot table yields something drop. */
        DROPPABLE,
        /** Every block drops, falling back to the block's own item when its loot table is empty. */
        ALL
    }
}
