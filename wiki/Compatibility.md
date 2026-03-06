# Compatibility

## required mods

| mod | version | notes |
|---|---|---|
| Fabric API | latest for your MC version | required |
| Sodium | 0.6.0+ | required, config UI and block entity API live here |

---

## well-tested and works

| mod | status | notes |
|---|---|---|
| Lithium | works | no conflicts, they cover different areas. Lithium does game logic, Helium does rendering |
| Iris | works | shader compatibility is fine, Helium's GL cache and DSA features work alongside Iris |
| ImmediatelyFast | works | GL state cache auto-disables when ImmediatelyFast is detected to avoid double-tracking |
| ViaFabricPlus | works | no protocol conflicts |
| ModMenu | works | optional, adds a settings shortcut in the mod list |
| Indium | works | Sodium rendering API compatibility layer, no conflicts |
| Entity Culling | works | both can run simultaneously, Helium's culling uses a distance approach while Entity Culling does frustum/occlusion, no conflicts |
| Reese's Sodium Options | works | just a Sodium UI mod, no issues |
| Sodium Extra | works | no conflicts |

---

## platform support

| platform | status | notes |
|---|---|---|
| Fabric | full support | primary target |
| Quilt | supported | works via CrossLoaderCompat |
| Forge | not supported | Fabric/Quilt only |
| NeoForge | not supported | Fabric/Quilt only |
| Android / PojavLauncher | partial | GL state cache disables automatically, most other features may not work correctly since Helium targets desktop OpenGL, not OpenGL ES |

---

## Minecraft versions

| version | status |
|---|---|
| 1.21.1 | supported |
| 1.21.2 | supported |
| 1.21.3 | supported |
| 1.21.4 | supported |
| 1.21.11 | supported (latest) |
| older than 1.21.1 | not supported |

---

## ImmediatelyFast note

ImmediatelyFast and Helium both touch OpenGL state management. Helium detects ImmediatelyFast at startup via the Fabric mod loader API and disables its own GL state cache automatically. you don't need to do anything manually. both mods can run at the same time.

---

## Iris note

when Iris is active, Helium detects it and stays compatible. some features like temporal reprojection are less useful with shaders since Iris manages its own rendering passes, but nothing will conflict or crash.

---

## reporting incompatibilities

if you find a mod that breaks with Helium, open an issue at [github.com/qborder/Helium/issues](https://github.com/qborder/Helium/issues). include:

- the mod that conflicts
- both mod versions
- Minecraft version
- crash report or log if there is one
