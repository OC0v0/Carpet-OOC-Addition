# Carpet OOC Addition

[![license](https://img.shields.io/github/license/OC0v0/Carpet-OOC-Addition?style=flat-square)](LICENSE)
[![issues](https://img.shields.io/github/issues/OC0v0/Carpet-OOC-Addition?style=flat-square)](https://github.com/OC0v0/Carpet-OOC-Addition/issues)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-orange?style=flat-square)

这是一个纯服务端 [Carpet mod](https://github.com/gnembon/fabric-carpet) 扩展，面向 OOC玩法：调整交易、掉落、合成与方块交互行为，让一些平时无法实现的操作变得可行。

## 文档

- [规则文档](docs/rules-zh_cn.md)
- [English rules](docs/rules.md)

## 支持版本

| 组件 | 版本 |
| --- | --- |
| Minecraft | 1.21.11 |
| Fabric Loader | ≥ 0.19.5 |
| fabric-carpet | ≥ 1.4.194 |
| Fabric API | ≥ 0.141.6 |

模组只需要安装在服务端；客户端不装也能正常游玩，装了会额外获得信标界面图标与内置资源包等客户端效果。

## 规则管理

所有规则默认关闭（保持原版行为），通过 `/carpet <规则名> [值]` 查看和修改：

```text
/carpet axeMakesHeadlessPiston          # 查看当前值与说明
/carpet axeMakesHeadlessPiston true     # 开启
```

规则在服务端即时生效，不需要重启。详细的规则行为见[规则文档](docs/rules-zh_cn.md)。

## 构建

需要 JDK 21：

```bash
./gradlew build
```

产物位于 `build/libs/carpet-ooc-addition-<version>.jar`。

Carpet 通过 [Modrinth Maven](https://api.modrinth.com/maven)（`maven.modrinth:carpet:1.4.194`）在编译期引入，构建时会自动下载，无需手动放置 jar。

## 版本号

基线 `1.0.0`；新增规则使用 `pre-x`，问题修复使用 `hotfix-x`（x 各自递增）。

## 许可证

本项目使用 **GNU Lesser General Public License v3.0 或任何后续版本**（`LGPL-3.0-or-later`）。完整许可证文本见 [LICENSE](LICENSE)。

## 反馈

遇到 Bug、版本兼容问题，或有新的规则建议，欢迎提交 [Issue](https://github.com/OC0v0/Carpet-OOC-Addition/issues)。
