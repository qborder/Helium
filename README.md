<div align="center">

# Helium

lightweight client-side performance mod for Minecraft

[![Modrinth](https://img.shields.io/modrinth/dt/heliummc?color=00AF5C&logo=modrinth&label=modrinth)](https://modrinth.com/mod/heliummc)
[![GitHub](https://img.shields.io/github/stars/qborder/Helium?style=flat&logo=github&label=stars)](https://github.com/qborder/Helium)
[![Build](https://img.shields.io/github/actions/workflow/status/qborder/Helium/build.yml?logo=github&label=build)](https://github.com/qborder/Helium/actions)
[![License](https://img.shields.io/github/license/qborder/Helium?cacheSeconds=36000)](https://github.com/qborder/Helium/blob/HEAD/LICENSE)

**[Download on Modrinth](https://modrinth.com/mod/heliummc)** · **[Report Issues](https://github.com/qborder/Helium/issues)**

</div>

---

> this is the **development repository** for Helium. if you're just looking to install the mod, grab it from [Modrinth](https://modrinth.com/mod/heliummc). the page there has all the feature descriptions and pretty screenshots and stuff.

---

## Building From Source

you'll need **Java 21** installed. that's it. gradle wrapper handles everything else.

```bash
git clone https://github.com/qborder/Helium.git
cd Helium
```

**build the mod:**
```bash
./gradlew remapJar
```

output jar lands in `build/libs/`. the one *without* `-dev` in the name is the one you want.

**run a dev client** (for testing):
```bash
./gradlew runClient
```

**generate IDE sources** (IntelliJ / Eclipse):
```bash
./gradlew genSources
```

then import as a Gradle project. IntelliJ will figure it out.

---

## Project Structure

```
src/
├── main/resources/          # mod metadata, mixins config, assets
└── client/java/com/helium/
    ├── HeliumClient.java    # entrypoint
    ├── config/              # config loading/saving + YACL screen
    ├── compat/              # sodium config integration, modmenu
    ├── data/                # custom data structures
    ├── memory/              # object pools, buffer pools
    ├── render/              # GL state cache, block entity culling
    └── mixin/
        ├── math/            # fast math replacements
        ├── render/          # entity/block entity culling, GL state
        ├── particle/        # particle distance culling
        ├── tick/            # client world tick optimizations
        ├── network/         # network buffer optimizations
        └── multiplayer/     # server list pinging, scroll preservation
```

---

## Dependencies

| Dependency | Type | Why |
|---|---|---|
| [Fabric API](https://modrinth.com/mod/fabric-api) | Required | you know why |
| [Sodium](https://modrinth.com/mod/sodium) | Required | config UI lives inside sodium's settings |
| [YACL](https://modrinth.com/mod/yacl) | Embedded | powers the ModMenu config screen |
| [ModMenu](https://modrinth.com/mod/modmenu) | Optional | adds the mod toggle in the mod list |

---

## Contributing

PRs welcome. if you're fixing a bug, please describe what was broken and why. if you're adding a feature, open an issue first so we don't waste each other's time.

the codebase uses **Yarn mappings** and targets **Minecraft 1.21.x** with **Fabric Loader 0.16+**.

fair warning: the mixin naming convention uses `helium$` prefix for all injected methods. keep it consistent.

---

## License

[MIT](LICENSE) do whatever you want with it.
