# Carpet OOC Addition

[![license](https://img.shields.io/github/license/OC0v0/Carpet-OOC-Addition?style=flat-square)](LICENSE)
[![issues](https://img.shields.io/github/issues/OC0v0/Carpet-OOC-Addition?style=flat-square)](https://github.com/OC0v0/Carpet-OOC-Addition/issues)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-orange?style=flat-square)

[中文](README.md) | **English**

A [Carpet mod](https://github.com/gnembon/fabric-carpet) extension focused on OOC (out-of-context) gameplay: it adjusts trading, drops, crafting and block interaction so that operations which are normally impossible become possible.

## Documentation

- [Rules (English)](docs/rules.md)
- [规则文档（中文）](docs/rules-zh_cn.md)

## Supported versions

| Component | Version |
| --- | --- |
| Minecraft | 1.21.11 |
| Fabric Loader | ≥ 0.19.5 |
| fabric-carpet | ≥ 1.4.194 |
| Fabric API | ≥ 0.141.6 |

The mod only needs to be installed on the server. Clients without it work fine; installing it on the client additionally provides the beacon HUD icon and the built-in resource pack.

## Rule management

All rules default to off (vanilla behaviour) and are read or changed with `/carpet <rule> [value]`:

```text
/carpet axeMakesHeadlessPiston          # show the current value and description
/carpet axeMakesHeadlessPiston true     # enable it
```

Changes take effect on the server immediately, no restart required. See the [rules documentation](docs/rules.md) for the behaviour of each rule.

## Building

Requires JDK 21:

```bash
./gradlew build
```

The artifact is written to `build/libs/carpet-ooc-addition-<version>.jar`.

Carpet is resolved at compile time from [Modrinth Maven](https://api.modrinth.com/maven) (`maven.modrinth:carpet:1.4.194`), so it is downloaded automatically and no manual jar placement is needed.

## Versioning

The baseline is `1.0.0`; new rules use `pre-x` and fixes use `hotfix-x` (x incremented independently).

## License

This project is licensed under the **GNU Lesser General Public License v3.0 or any later version** (`LGPL-3.0-or-later`). See [LICENSE](LICENSE) for the full text.

## Feedback

Found a bug, a compatibility issue, or have a rule suggestion? Please open an [issue](https://github.com/OC0v0/Carpet-OOC-Addition/issues).
