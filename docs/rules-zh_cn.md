# 规则

中文 | [English](rules.md)

本文档列出 Carpet OOC Addition 当前提供的全部 Carpet 规则及其具体行为。

所有规则默认关闭，通过 `/carpet <规则名> [值]` 查看和修改，修改后在服务端即时生效。模组只需要安装在服务端。

---

## 制箭师只卖虚弱药箭 (fletcherWeaknessArrows)

制箭师出售的药箭固定为虚弱药水，而不是随机药水。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 图书管理员只卖满级附魔书 (librarianMaxLevelBooks)

图书管理员出售的附魔书固定为该附魔的最高等级。等级在生成交易时动态读取附魔注册表，因此数据包新增的附魔与自定义最高等级同样生效。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 无头活塞掉落方块 (headlessPistonDrops)

无头活塞破坏方块时的掉落行为。

- `false`：原版行为，不掉落。
- `droppable`：只掉有战利品表且能产出物品的方块。
- `all`：连没有战利品表的方块也掉（基岩、末地传送门框、刷怪笼等）。刷怪笼掉落物会保留方块实体数据（NBT），放置后刷怪类型与延迟等设置仍然保留。

- 类型：`enum`
- 默认值：`false`
- 参考选项：`false`, `droppable`, `all`
- 分类：`OOC`, `survival`, `creative`

---

## 禁用村庄生成猫 (disableVillageCats)

村庄不再自然生成猫。女巫小屋的猫不受影响。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 攻击村民永久降价 (attackVillagerDiscount)

攻击村民会记一次“治愈僵尸村民”的流言（`MAJOR_POSITIVE` 20 + `MINOR_POSITIVE` 25），从而获得交易折扣。

规则开启时解除 `MAJOR_POSITIVE` 上限，折扣永久且可无限叠加；攻击或击杀都不会产生负面声望。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 空手攻击村民给生命恢复 (bareHandVillagerRegeneration)

空手攻击村民时给村民 3 秒生命恢复 V（代码等级 4），避免在刷折扣时把村民打死。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 大师级工具商只卖固定附魔钻石镐 (masterToolsmithSilkPickaxe)

大师级工具商只出售带有耐久 III、效率 III、精准采集的钻石镐。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 盔甲匠只卖最佳附魔盔甲 (armorerBestArmor)

盔甲匠只出售最佳附魔的钻石盔甲：专家级提供护腿与靴子，大师级提供头盔与胸甲。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 巨型蘑菇覆盖末地传送门框 (hugeMushroomReplaceEndPortalFrame)

巨型蘑菇生长时可以覆盖末地传送门框。末地传送门框被加入蘑菇可替换标签，并受规则开关控制。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 信标再生改饱和 (beaconRegenerationToSaturation)

信标最高级原本施加的再生效果改为施加饱和。

客户端会按服务端同步的规则状态，在信标界面用图集精灵绘制饱和图标并替换按钮文字；内置资源包 `ooc_beacon` 可额外替换再生效果贴图。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 下界合金升级免模板 (netheriteUpgradeWithoutTemplate)

钻石装备只需要下界合金锭即可升级为下界合金装备，不需要也不消耗下界合金升级模板。

规则覆盖全部下界合金升级配方（剑、镐、斧、锹、锄、头盔、胸甲、护腿、靴子、矛、马铠、鹦鹉螺铠）。纯服务端实现，原版客户端即可使用。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 斧头右键制作无头活塞 (axeMakesHeadlessPiston)

用斧头右键未激活的活塞（含黏性活塞），使其直接变成无头活塞，且不发出任何方块更新。

只对未伸出、无红石信号的活塞生效；已经是伸出状态的活塞不受影响。不会消耗斧头耐久。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`

---

## 关闭仅管理员可写方块数据 (disableOpOnlyCustomData)

关闭原版的 `OP_ONLY_CUSTOM_DATA` 限制：携带方块实体数据（NBT）的物品（刷怪笼、命令方块、讲台、告示牌、试炼刷怪笼等）在放置时对所有玩家生效，不再只有管理员或 OP 才能写入。

- 类型：`boolean`
- 默认值：`false`
- 参考选项：`true`, `false`
- 分类：`OOC`, `survival`, `creative`
