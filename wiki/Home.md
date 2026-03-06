# Helium Wiki

Helium is a client-side performance mod for Minecraft that works on top of Sodium. it targets the parts of the renderer that cause the most overhead and tries to get out of the way of everything else.

this wiki covers how everything works, how to configure it, and what to do when things go wrong.

---

## pages

- [[Installation]] - how to get it running
- [[Features]] - what every system does and why
- [[Configuration]] - every config option explained
- [[Compatibility]] - what works with what
- [[FAQ]] - common questions and issues

---

## quick summary

Helium does not replace Sodium. it builds on top of it. Sodium handles chunk rendering, Helium handles the rest, entity culling, particle systems, math, GL state, memory, GPU-specific tuning, frame timing.

most features are on by default and work without touching anything. the config lives inside **Options > Video Settings** in Sodium's menu, or in ModMenu if you have that installed.

if you just want to install and forget, that's fine. if you want to tune every knob, the [[Configuration]] page has all of it.

---

## minimum requirements

| thing | minimum |
|---|---|
| Minecraft | 1.21.1 |
| Fabric Loader | 0.16.0 |
| Java | 21 |
| Sodium | 0.6.0 |
| Fabric API | required |

---

## not supported

- Android / PojavLauncher - Helium needs desktop OpenGL, not OpenGL ES. it auto-detects Android and disables the GL state cache to avoid crashes but most features won't work right
- servers - Helium is client-only, nothing runs on the server side
- Forge / NeoForge - Fabric and Quilt only
