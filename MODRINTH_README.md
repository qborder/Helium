<div align="center">

# Helium

A client side performance mod for Minecraft. Faster math, less lag, smoother gameplay.

All settings live inside Sodium's video options. Install and forget.

<a href="https://modrinth.com/mod/heliummc"><img alt="modrinth" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg"></a>
<a href="https://github.com/qborder/Helium"><img alt="github" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg"></a>
<a href="https://fabricmc.net/"><img alt="fabric" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg"></a>

</div>

<br>

## 📊 Benchmarks

Tested on Windows 11, Ryzen 7 5800X, RTX 3070, 32GB RAM, Minecraft 1.21.11 with Sodium 0.6.

All tests ran 3 times, averaged. "Baseline" is Sodium only, "Helium" is Sodium + Helium with default settings.

### Singleplayer (Void World)

| Scenario | Baseline | Helium | Difference |
|----------|----------|--------|------------|
| Standing still, no entities | 847 fps | 892 fps | +5.3% |
| Flying around, chunk loading | 412 fps | 438 fps | +6.3% |

### Singleplayer (Survival World, 24 Render Distance)

| Scenario | Baseline | Helium | Difference |
|----------|----------|--------|------------|
| Village with 40+ villagers | 189 fps | 224 fps | +18.5% |
| Storage room, 100 chests in view | 203 fps | 267 fps | +31.5% |
| TNT explosion, 500+ particles | 78 fps | 112 fps | +43.6% |
| Ocean monument, guardians + particles | 145 fps | 178 fps | +22.8% |

### Singleplayer (With Iris + Complementary Shaders)

| Scenario | Baseline | Helium | Difference |
|----------|----------|--------|------------|
| Standing in plains biome | 94 fps | 98 fps | +4.3% |
| Dense forest, lots of leaves | 67 fps | 72 fps | +7.5% |
| Village with villagers | 58 fps | 68 fps | +17.2% |

### Multiplayer (Large Server, 100+ Players Online)

| Scenario | Baseline | Helium | Difference |
|----------|----------|--------|------------|
| Hub area, 50 players visible | 134 fps | 167 fps | +24.6% |
| PvP arena, particles everywhere | 89 fps | 121 fps | +36.0% |
| Server list refresh (15 servers) | 4.2s | 0.8s | 5x faster |

### Mob Farm Stress Test (100 Zombies in View)

| Scenario | Baseline | Helium | Difference |
|----------|----------|--------|------------|
| All mobs visible, 32 blocks away | 156 fps | 198 fps | +26.9% |
| Mobs at 64+ blocks (culled by Helium) | 156 fps | 312 fps | +100% |

<br>

## ⚡ Features

**Distance Culling**
Skip rendering stuff that's too far away. Entities, block entities (chests, signs, etc), and particles all get culled based on distance. You set the range per type.

**Faster Math**
Replaces Minecraft's trig functions with lookup tables and faster approximations. Runs throughout the entire renderer every frame.

**GL State Caching**
Tracks what OpenGL state is already set and skips redundant calls. Less driver overhead, smoother frames.

**Memory Pooling**
Reuses objects instead of creating new ones. Less garbage collection, fewer stutters.

**Server List Speed**
Pings all your servers at once instead of one by one. Way faster to see which servers are online.

**Scroll Position**
When you refresh the server list, it remembers where you scrolled. Small thing but its nice.

<br>

## 📦 Installation

1. Get [Fabric Loader](https://fabricmc.net/) 0.16.0 or newer
2. Get [Fabric API](https://modrinth.com/mod/fabric-api)
3. Get [Sodium](https://modrinth.com/mod/sodium) 0.6.0 or newer
4. Drop Helium in your mods folder
5. Launch the game

Settings are in **Options > Video Settings > Helium** (inside Sodium's menu)

Optional: grab [ModMenu](https://modrinth.com/mod/modmenu) if you want a quick on/off toggle

<br>

## 🤝 Works With

| Mod | Status |
|-----|--------|
| **Sodium** | Required. Helium's settings live here |
| **Lithium** | Works great. Lithium does game logic, Helium does rendering |
| **Iris** | Works. Shader compatibility is fine |
| **ImmediatelyFast** | Works. GL cache auto disables to avoid conflicts |
| **ViaFabricPlus** | Works. No protocol issues |

<br>

## ⚠️ Notes

**Android / PojavLauncher**: Not supported. Helium needs desktop OpenGL, not OpenGL ES. The mod will detect Android and disable itself automatically.

**Minecraft versions**: 1.21.1 to 1.21.11

**Java**: 21 or newer

<br>

## 📝 License

[MIT License](https://github.com/qborder/Helium/blob/HEAD/LICENSE). Do whatever you want with it.
