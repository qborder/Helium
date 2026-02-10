<center>

# Helium

### lightweight client-side performance mod for Minecraft

[![Modrinth Downloads](https://img.shields.io/modrinth/dt/helium?color=00AF5C&logo=modrinth&label=downloads)](https://modrinth.com/mod/helium)
[![Minecraft](https://img.shields.io/badge/minecraft-1.21.x-62B47A?logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAYAAAAfSC3RAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAS5JREFUKFOV0DFKA2EQBeDNJoWFhYWFjY2NICKCYGEhKIggCKIggt5ALyCewDN4AY/gBTyBd7C2sbGxsbGwEBHxmd3f+N9mYWD+nZk382b+TUQ5s8BWHI1j8QLf+MJHfOIdb/GCp0iYxwq2Yw17sY4FTGIEw0hYxiYO4Cj2Yw49GMMI/gXHXaxF0fxJvCKB9zHfdzhBre4xhWucBm/U6ILc9iMMUxjC6axGZ3owgDGMcQ5zuEyLnCBJ5zHI0/xjLs4xyne8ICHeMRjPPGUJdqzA/PYiTGcxElM8DXOcQ5neIZXXOA1XnGPe9ziDne4xx0ecY8XypjGEpYwg02cQFcc4zi6McGGZWzENDrRhT6MYhSD+Bc52I9t2IX16ERXdGI4B8fQh24c5PgN/AKoxWTWuYxLYAAAAABJRU5ErkJggg==)](https://modrinth.com/mod/helium)
[![Requires Sodium](https://img.shields.io/badge/requires-Sodium-F58142)](https://modrinth.com/mod/sodium)

</center>

---

you know how minecraft just... doesn't use your hardware properly? entities rendering behind walls, particles going crazy 3 chunks away where nobody can see them, server list pinging one server at a time like it's 2012?

helium fixes that. no visual changes, no weird artifacts, just your game running better.

---

## ⚡ What It Does

### Rendering

**Entity Distance Culling** — stops rendering entities that are too far away to matter. configurable distance. massive FPS boost in crowded areas like farms or towns

**Block Entity Distance Culling** — same thing but for chests, signs, banners, heads, all that stuff. these are surprisingly expensive to render and you won't notice them disappearing at 48+ blocks

**Particle Distance Culling** — particles spawning 30 blocks away that you'll never see? gone. your GPU says thanks

**Animation Throttling** — reduces texture animation updates for blocks you can't even see. saves GPU time without any noticeable difference

### Engine

**Fast Math** — replaces minecraft's math functions with faster approximations. the accuracy difference is literally invisible but the FPS difference isn't

**GL State Cache** — caches OpenGL state to skip redundant GPU calls. auto-disables if you have ImmediatelyFast installed so they don't fight

**Memory Optimizations** — reuses objects and buffers instead of creating new ones constantly. less garbage collection = less stuttering

**Thread Optimizations** — prioritizes the render thread and optimizes event polling. smoother frames

**Fast Startup** — parallel class loading so the game launches faster. does nothing after startup

**Network Optimizations** — background resource processing for smoother network handling

### Multiplayer

**Fast Server Pinging** — pings all your servers at the same time instead of one by one. your entire server list loads in seconds instead of sitting there watching them pop in one at a time

**Keep Scroll on Refresh** — pressing refresh in the server list doesn't yeet you back to the top anymore. it remembers where you were scrolling

---

## 🔧 Configuration

all options are in **Sodium's video settings**. helium adds its own pages right there — Rendering, General, and Multiplayer. every single feature can be toggled individually and distances are adjustable with sliders.

if you have **ModMenu** installed, there's a master on/off switch for the whole mod there too.

---

## 📦 Installation

1. install [Fabric Loader](https://fabricmc.net/) (0.16.0+)
2. install [Fabric API](https://modrinth.com/mod/fabric-api)
3. install [Sodium](https://modrinth.com/mod/sodium) (0.6.0+)
4. launch the game, done

**optional but recommended:** [ModMenu](https://modrinth.com/mod/modmenu) for the config screen

---

## 🤝 Compatibility

designed to work alongside other performance mods:

- **Sodium** — required, helium lives inside sodium's settings
- **Lithium** — fully compatible, they optimize different things
- **Iris** — works fine, go have your shaders
- **ImmediatelyFast** — compatible, GL state cache auto-disables to avoid conflicts
- **ViaFabricPlus** — multiplayer features tested and compatible

---

## 📝 License

Licensed under the [MIT License](https://github.com/qborder/Helium/blob/HEAD/LICENSE).
