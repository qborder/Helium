# Installation

## what you need

- [Fabric Loader](https://fabricmc.net/) 0.16.0 or newer
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Sodium](https://modrinth.com/mod/sodium) 0.6.0 or newer
- Java 21 or newer

Sodium is required, not optional. Helium's config screen lives inside Sodium's video settings and some culling systems use Sodium's block entity API directly.

---

## steps

1. install Fabric Loader from [fabricmc.net](https://fabricmc.net/) if you haven't already
2. grab Fabric API and Sodium from Modrinth and drop them in your mods folder
3. download Helium from [Modrinth](https://modrinth.com/mod/heliummc) and drop it in your mods folder
4. launch the game

that's it. no config required out of the box.

---

## optional mods

| mod | why you'd want it |
|---|---|
| [ModMenu](https://modrinth.com/mod/modmenu) | adds a Helium entry in the mod list with a quick settings button |
| [Lithium](https://modrinth.com/mod/lithium) | handles game logic optimizations, pairs well with Helium which focuses on rendering |
| [Iris](https://modrinth.com/mod/iris) | shader support, compatible with Helium |

---

## where is the config

**Options > Video Settings > Helium** - it's a tab inside Sodium's settings panel.

if you have ModMenu installed, there's also a shortcut from the mod list screen.

the config file itself is at `.minecraft/config/helium.json` if you want to edit it directly or back it up.

---

## java version note

Java 21 is the minimum. most modern launchers (Prism, MultiMC, official launcher) will let you set the Java version per instance. if you're on Java 17 or lower, Helium won't load.

---

## building from source

you need Java 21 installed. the Gradle wrapper handles everything else.

```bash
git clone https://github.com/qborder/Helium.git
cd Helium
./gradlew remapJar
```

output jar is in `build/libs/`. use the one without `-dev` in the name.

to run a dev client:

```bash
./gradlew runClient
```

to generate IDE sources for IntelliJ or Eclipse:

```bash
./gradlew genSources
```

then import as a Gradle project.
