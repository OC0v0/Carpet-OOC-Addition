# Carpet OOC Addition

- mod id：`carpet-ooc-addition`，显示名：**Carpet OOC Addition**，规则分类：`OOC`
- 依赖：fabric-carpet ≥ 1.4.193、Java 21、Minecraft 1.21.11
- 所有规则默认 `false`（原版行为），用 `/carpet <rule> [value]` 开关

## 规则

| 规则 | 类型 | 作用 |
| --- | --- | --- |
| `fletcherWeaknessArrows` | 布尔 | 制箭师药箭固定为虚弱 |
| `librarianMaxLevelBooks` | 布尔 | 图书管理员的附魔书固定为最高等级（动态读注册表） |
| `headlessPistonDrops` | `false`/`droppable`/`all` | 无头活塞破坏方块时掉落；`all` 连无战利品表的方块（基岩、末地传送门框）也掉；刷怪笼掉落时保留 NBT |
| `disableVillageCats` | 布尔 | 村庄不生成猫（女巫小屋不受影响） |
| `attackVillagerDiscount` | 布尔 | 攻击村民 = 记一次治愈的流言（MAJOR_POSITIVE 20 + MINOR_POSITIVE 25）；规则开启时解除 MAJOR_POSITIVE 上限，永久且可叠加；攻击/击杀不产生负面声望 |
| `bareHandVillagerRegeneration` | 布尔 | 空手打村民时给 3 秒生命恢复 V（amplifier 4） |
| `masterToolsmithSilkPickaxe` | 布尔 | 大师级工具商只卖耐久三/效率三/精准采集的钻石镐 |
| `armorerBestArmor` | 布尔 | 盔甲匠只卖最佳附魔钻石盔甲（专家级：护腿+靴子；大师级：头盔+胸甲） |
| `hugeMushroomReplaceEndPortalFrame` | 布尔 | 巨型蘑菇可覆盖末地传送门框（tag + 规则门控） |
| `beaconRegenerationToSaturation` | 布尔 | 信标最高级的再生效果改为施加饱和 |
| `netheriteUpgradeWithoutTemplate` | 布尔 | 钻石装备只用下界合金锭即可升级为下界合金装备，不需要也不消耗下界合金升级模板（纯服务端，原版客户端可用） |
| `axeMakesHeadlessPiston` | 布尔 | 斧头右键未激活的活塞（含黏性）→ 变成无头活塞，不发出任何方块更新 |
| `disableOpOnlyCustomData` | 布尔 | 关闭 `OP_ONLY_CUSTOM_DATA`：带方块实体 NBT 的物品（刷怪笼、命令方块、讲台、告示牌等）放置时对所有玩家生效 |

## 资源

- 内置资源包 `ooc_beacon`（资源包界面可勾选，默认关闭）：把 `mob_effect/regeneration` 贴图换成放大后的饱和图标
- 信标界面不依赖资源包：客户端按服务端同步的规则状态，用图集精灵 `Gui.getMobEffectSprite(SATURATION)` 画 20×20 图标，并把按钮文字换成饱和
- 规则同步：进服时及规则变化时发 `carpet-ooc-addition:rules` 包；没装模组的客户端只会记一条 `Unknown custom packet payload`，不受影响
- `data/minecraft/tags/block/replaceable_by_mushrooms.json`：门框加入蘑菇可替换标签（由规则门控）

依赖 Fabric API（内置资源包注册、网络包）。

## 构建

JDK 21，`libs/` 放 Carpet 的 mod jar（仅编译用），然后 `./gradlew build`
→ `build/libs/carpet-ooc-addition-<version>.jar`

版本号：基线 `1.0.0`；之后**加规则**用 `pre-x`、**修 bug** 用 `hotfix-x`（x 各自递增）。

## 已验证

Fabric 1.21.11 + Carpet 1.4.194 + trade-cycling，Carpet 假玩家 + RCON 实测：

- 药箭 3/3 虚弱；附魔书满级（原版 5/5、数据包 `max_level=9`、自定义附魔 7 级）
- trade-cycling 刷新后仍满级；重启后折扣保留（`major_positive` 60 → `specialPrice -16`）
- 无头活塞：基岩 / 门框掉落，`droppable` 只掉有表方块，普通活塞不多掉活塞头
- 村庄猫规则已注册、注入生效（未等自然刷猫）；空手攻击出 `regeneration amplifier 4`
- 工具商 331 镐、盔甲匠 4 件满配（专家/大师分级）
- 巨型蘑菇圆顶覆盖门框：规则关保留、开替换
- 信标：规则关得 `regeneration`，开得 `saturation`
