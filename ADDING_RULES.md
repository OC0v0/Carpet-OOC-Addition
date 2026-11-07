# 加一条规则：资源清单与须知

> 给另一个对话/agent 的交接文档。项目：`E:\Agent\Agent\work\weak-fletcher`（Carpet OOC Addition，1.21.11，版本 1.0.0（基线；之后加规则用 pre-x、修复用 hotfix-x））。

## 一、要动/要加的文件

| # | 文件 | 作用 |
| --- | --- | --- |
| 1 | `src/main/java/carpetoocaddition/CarpetOOCAdditionSettings.java` | 规则字段（`@Rule`），所有规则默认 `false`/原版 |
| 2 | `src/main/java/carpetoocaddition/mixins/*.java` | 服务端规则逻辑，读取上面的静态字段 |
| 3 | `src/main/resources/carpet-ooc-addition.mixins.json` | 新增服务端 mixin 必须登记 |
| 4 | `src/main/resources/assets/carpet-ooc-addition/lang/en_us.json`、`zh_cn.json` | 规则名/描述（**必须**，否则启动崩溃） |
| 5 | `gradle.properties` | `mod_version`：加规则 `pre-x`、修复 `hotfix-x`（见第六节） |
| 6 | 可选：`src/main/resources/data/...` | 数据包（tag 等），随模组自动加载 |
| 7 | 可选：`src/main/java/carpetoocaddition/net/*` | 需要让客户端知道规则状态时用（见第五条） |
| 8 | 可选：`src/main/java/carpetoocaddition/client/*`、`client/mixins/*`、`carpet-ooc-addition.client.mixins.json` | 客户端渲染 / 资源包注册（见第五条） |
| 9 | 可选：`src/main/resources/resourcepacks/<pack>/` | 可勾选的内置资源包（`pack.mcmeta` + assets） |

纯数据规则只动 4/5/6。

## 二、规则字段写法

```java
@Rule(categories = {OOC, "survival", "creative"})
public static boolean myRule = false;

@Rule(categories = {OOC, "survival", "creative"})
public static MyEnum myEnumRule = MyEnum.FALSE;

public enum MyEnum { FALSE, DROPPABLE, ALL }   // 游戏内显示/输入为小写
```

- 布尔：`Options: [ true false ]`；枚举：常量名小写作为可选值（解析时自动转大写）。
- 规则在运行时读取静态字段即可，`/carpet` 改完立即生效。
- 默认值必须是原版行为（项目约定）。

## 三、翻译（最容易踩的坑）

在 `assets/carpet-ooc-addition/lang/<lang>.json` 加两条，键名格式固定：

```json
"carpetoocaddition.carpet_translations.carpet.rule.<rule>.name": "...",
"carpetoocaddition.carpet_translations.carpet.rule.<rule>.desc": "..."
```

- `CarpetOOCAdditionServer#onGameStarted` 先 `Translations.updateLanguage()` 再 `parseSettingsClass`；**缺 desc 启动会报 `No language key provided for carpet.rule.<rule>.desc` 并崩服**。
- 前缀 `carpetoocaddition.carpet_translations.` 会被剥掉，最终键是 `carpet.rule.<rule>.desc`。
- 中文显示名写在 `zh_cn.json`；规则文字语言由 `/carpet language zh_cn` 控制。

## 四、服务端 mixin 须知

- 包名 `carpetoocaddition.mixins`，方法前缀 `carpetoocaddition$`；接口目标用 `interface` mixin。
- 改非 void 方法用 MixinExtras `@WrapOperation`/`@ModifyReturnValue`；参数改写用 `@ModifyVariable(argsOnly = true, index = n)`。
- 无需 refmap：Loom 1.17.20 会把 `method = "..."` 重映射（可用 `javap -v` 确认）。
- 枚举/静态常量表不要直接改（如信标的 `BEACON_EFFECTS` 是 `List.of` 且 `VALID_EFFECTS` 由它派生）；在消费处替换，例如 `BeaconBlockEntity#applyEffects` 的参数替换。
- 原版某些上限是两处夹的：`GossipContainer#mergeValuesForAddition` 之后还有 `EntityGossips#makeSureValueIsntTooLowOrTooHigh`，只改一处不生效。
- 「改方块状态但不发出更新」看 `Level#setBlock` 的 flags：`UPDATE_CLIENTS(2)` 只同步客户端，`UPDATE_KNOWN_SHAPE(16)` 跳过邻接形状刷新，`UPDATE_SKIP_ON_PLACE(512)` 跳过 `LevelChunk` 里的 `onPlace`（否则 `PistonBaseBlock#onPlace → checkIfExtend` 会立刻把刚变成 extended 的活塞收回去）。

## 五、客户端相关（规则同步 / 资源包 / 客户端 mixin）

**规则同步**（`net/OocRulesPayload`、`net/OocAckPayload`、`net/OocRuleSync`）：

- `CustomPacketPayload` record + `StreamCodec<RegistryFriendlyByteBuf, T>`，`PayloadTypeRegistry.playS2C()/playC2S().register(...)` 在 `ModInitializer` 里执行。
- 规则变化用 Carpet 的 `SettingsManager.registerGlobalRuleObserver` 事件触发广播，**不轮询**。
- 进服无条件发一次；客户端（装了模组的）回 `carpet-ooc-addition:rules_ack`；没收到 ack 就每秒重发，最多 10 次后停止。
- **假玩家不发**：`player.getClass().getName().equals("carpet.patches.EntityPlayerMPFake")` 直接跳过（Carpet 假玩家没有真实连接）。
- **没装模组的客户端不会掉线**：原版 `ClientPacketListener#handleUnknownCustomPayload` 只打一条 `Unknown custom packet payload` 日志，且永远不回 ack，10 秒后自然停止。

**客户端资源包缓存**（`client/PackCache`、`client/ClientPackController`）：

- 缓存文件 `<gameDir>/config/carpet-ooc-addition/client-pack.properties`，所有单人存档和服务器共用一个，关闭客户端不重置。
- 启动后第一个 tick 按缓存决定是否加载资源包；进服收到同步包则覆盖缓存；连原版服务端 11 秒收不到包则卸载并写回 false。
- 缓存用临时文件 + 原子 move 写入，多客户端进程共用一个目录也不会读到半截文件；重载有去抖（同一时刻只跑一次 `reloadResourcePacks`）。

**内置资源包（可勾选，默认关闭）**：

```java
ResourceManagerHelper.registerBuiltinResourcePack(
    Identifier.fromNamespaceAndPath(MOD_ID, "my_pack"),
    FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(),
    ResourcePackActivationType.NORMAL);
```

- 包内容放 `src/main/resources/resourcepacks/my_pack/`，`pack.mcmeta` 用 `min_format`/`max_format`（1.21.11 是 75）。
- 运行时开关：`Minecraft.getInstance().options.resourcePacks` 里加/删 `"<modid>/<pack>"`，再 `reloadResourcePacks()`；id 与资源包界面显示的名字一致。

**客户端 mixin**：

- 单独一个 `carpet-ooc-addition.client.mixins.json`（`"client": [...]`），在 `fabric.mod.json` 里以 `{"config": ..., "environment": "client"}` 声明，服务端不会加载。
- 客户端入口 `"client": ["carpetoocaddition.client.OocClientInit"]`。
- 目标常是内部类，用 `@Mixin(targets = "net.minecraft.client.gui.screens.inventory.BeaconScreen$BeaconPowerButton")`；`@Shadow` 私有字段；调 `getX()/getY()` 要转成 `(AbstractWidget)(Object)this`。
- **画图标不要用 `GuiGraphics#blit` 画散图**：非图集贴图没有注册进 `TextureManager`，会显示紫黑格。用 `Gui.getMobEffectSprite(holder)` 拿到图集精灵再 `blitSprite(...)`，或走资源包覆盖。

## 六、构建 / 部署

```bash
cd E:\Agent\Agent\work\weak-fletcher
JAVA_HOME="C:\Program Files\Java\latest\jdk-21" ./gradlew build --no-daemon
# 产物 build/libs/carpet-ooc-addition-<version>.jar
```

- Gradle 9.5.0（`_tools` 有离线 zip）、Loom 1.17.20、Loader 0.19.5、MC 1.21.11。
- 编译依赖：`libs/fabric-carpet-...-1.4.194.jar`（`modCompileOnly`）+ `fabric-api`（`modImplementation`，客户端包注册/网络需要）。
- 部署两份：
  - 服务端 `E:\Minecraft\.server\1.21.11\mods\`
  - 客户端 `E:\Minecraft\.minecraft\versions\1.21.11\mods\`（PCL 版本隔离）
- **版本号约定**：基线 `1.0.0`；之后每次**加规则**用 `pre-x`（`pre-1`、`pre-2`…），**修 bug** 用 `hotfix-x`（`hotfix-1`…），x 各自递增。改完 `gradle.properties` 的 `mod_version` 后重新构建并删掉旧版本 jar。注意 Fabric Loader 对 `pre-1` 这类非语义化版本会打一条 WARN（不影响加载，只是无法参与版本比较）。
- **服务端运行中会锁 jar**，删不掉；只能把新内容覆盖写入原文件名，并确保目录里只有这一个 jar（同 id 两个 jar 冲突）。

## 七、测试环境

- 测试服：`E:\Agent\Agent\_work\wf-server`（Fabric 1.21.11 + Carpet + Fabric API + trade-cycling + 本模组），JDK 21 跑 `fabric-server-...jar nogui`；端口 25566，RCON 25575。
- 世界固定为超平坦虚空：`level-type=minecraft:flat`，`generator-settings` 只有一层 `minecraft:white_stained_glass`（y=-64），群系 `minecraft:the_void`，`spawn-protection=0`。
- **每轮测试结束必须重置存档**：`bash _work/wf-server/reset_world.sh`（删 `world/`），下次启动 1 秒内重新生成干净世界；测试一律从空世界开始，不会再出现“同一位置被上一轮方块占用”。
- 该世界默认不加载出生点区块：测试前先 `forceload add <x1> <z1> <x2> <z2>`，否则 `setblock`/`execute if block` 会失败或判据为假。
- RCON：`E:\Agent\Agent\projection-freeze\test\rcon.py`，`python rcon.py cmds.txt`，`#wait <秒>` 控间隔。
- **`say` 只进 latest.log，不回 RCON**；`/carpet`、`/data get` 才回 RCON。
- 假玩家：`/player Bot spawn at x y z facing yaw pitch`、`use once`、`attack once`。
- 假玩家刚进服的第一次 `use once` 常常空转（首个动作不生效），脚本里先空手 `use once` 热身；服务端 tick 落后时 `#wait` 不可靠（曾出现玩家晚 5 秒才加入），关键步骤之间留 2–3 秒并在日志里核对。
- 判据：`/data get entity <sel> Offers|Gossips|active_effects`、`/execute if block ... run say TAG`。
- 蘑菇要低亮度 + 菌丝；信标要 4 层金字塔 + 天空 + 等 4 秒。
- 客户端渲染/资源包开关无法在服务端验证，只能由用户实机确认。

## 八、当前 13 条规则

`fletcherWeaknessArrows`、`librarianMaxLevelBooks`、`headlessPistonDrops`(enum)、`disableVillageCats`、`attackVillagerDiscount`、`bareHandVillagerRegeneration`、`masterToolsmithSilkPickaxe`、`armorerBestArmor`、`hugeMushroomReplaceEndPortalFrame`、`beaconRegenerationToSaturation`、`netheriteUpgradeWithoutTemplate`、`axeMakesHeadlessPiston`、`disableOpOnlyCustomData`。

实现：`TradeFixes`、`PistonDrops`、`MasterTrades`、`net/OocRuleSync`、11 个服务端 mixin（`MerchantMixin`、`VillagerMixin`、`GossipContainerMixin`、`EntityGossipsMixin`、`PistonBaseBlockMixin`、`CatSpawnerMixin`、`AbstractHugeMushroomFeatureMixin`、`BeaconBlockEntityMixin`、`SmithingMenuMixin`、`AxeItemMixin`、`BlockEntityTypeMixin`）+ 客户端 `OocClientInit`、`client/mixins/BeaconPowerButtonMixin`。
