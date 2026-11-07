# Rules

[中文](rules-zh_cn.md) | English

This document lists every Carpet rule provided by Carpet OOC Addition and what it does.

All rules default to off, are read with `/carpet <rule> [value]` and take effect on the server immediately. The mod is only required on the server.

---

## fletcherWeaknessArrows

Fletchers sell tipped arrows of Weakness instead of a randomly chosen potion.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## librarianMaxLevelBooks

Librarians sell enchanted books at the enchantment's own maximum level, read dynamically from the enchantment registry (datapack and custom enchantments included).

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## headlessPistonDrops

Drops for blocks destroyed by a headless piston.

- `false`: vanilla, no drops.
- `droppable`: only blocks whose loot table yields something.
- `all`: also blocks without a loot table (bedrock, end portal frame, spawner, ...). A dropped spawner keeps its block entity data (NBT), so the mob type and timings survive.

- Type: `enum`
- Default: `false`
- Options: `false`, `droppable`, `all`
- Categories: `OOC`, `survival`, `creative`

---

## disableVillageCats

Villages no longer spawn cats. Witch hut cats are unaffected.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## attackVillagerDiscount

Hitting a villager grants the player the gossip of curing a zombie villager (`MAJOR_POSITIVE` 20 + `MINOR_POSITIVE` 25), which lowers prices.

While the rule is on, the `MAJOR_POSITIVE` cap is lifted, so the discount is permanent and stacks. Hitting or killing never creates negative reputation.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## bareHandVillagerRegeneration

Hitting a villager with an empty hand gives it Regeneration V for 3 seconds (amplifier 4), so it survives while the discount is farmed.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## masterToolsmithSilkPickaxe

Master toolsmiths sell only a diamond pickaxe with Unbreaking III, Efficiency III and Silk Touch.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## armorerBestArmor

Armorers sell only their best enchanted diamond armor: expert offers leggings and boots, master offers helmet and chestplate.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## hugeMushroomReplaceEndPortalFrame

Huge mushrooms can replace end portal frames while growing. End portal frames are added to the mushroom-replaceable tag, gated by this rule.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## beaconRegenerationToSaturation

The highest beacon tier applies Saturation instead of Regeneration.

The client draws a Saturation icon with an atlas sprite and relabels the button based on the synced rule state; the built-in resource pack `ooc_beacon` can additionally replace the Regeneration effect texture.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## netheriteUpgradeWithoutTemplate

Diamond gear upgrades to netherite with just a netherite ingot: the netherite upgrade smithing template is not needed and is not consumed.

Every netherite upgrade recipe is covered (sword, pickaxe, axe, shovel, hoe, helmet, chestplate, leggings, boots, spear, horse armor, nautilus armor). Server-side only; vanilla clients work as is.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## axeMakesHeadlessPiston

Right-clicking an unpowered piston (sticky piston included) with an axe turns it into a headless piston without emitting any block update.

Only piston bases that are not extended and have no redstone signal are affected; no axe durability is used.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`

---

## disableOpOnlyCustomData

Disables vanilla's `OP_ONLY_CUSTOM_DATA` restriction: items carrying block entity data (spawner, command block, lectern, signs, trial spawner, ...) restore that data on placement for every player, instead of only for game-master/op players.

- Type: `boolean`
- Default: `false`
- Options: `true`, `false`
- Categories: `OOC`, `survival`, `creative`
