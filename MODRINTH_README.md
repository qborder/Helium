<div align="center">

# Helium

Client-side Fabric mod that applies a set of rendering, memory, and QoL tweaks to Minecraft.

All features are toggleable and configurable through Sodium's settings.

</div>

---

<h2>⚡ Features</h2>

### Distance Culling

Helium skips rendering entities, block entities, and particles that are beyond a configurable distance from the player. This reduces draw calls in areas with many objects.

- **Entity culling** injects into `EntityRenderer.shouldRender()` and returns false for entities past the set distance (default 64 blocks)
- **Block entity culling** registers render predicates through Sodium's `BlockEntityRenderHandler` API for 13 block entity types: chests, signs, hanging signs, banners, bells, campfires, enchanting tables, end portals, end gateways, decorated pots, beds, shulker boxes, skulls, and conduits (default 48 blocks)
- **Particle culling** intercepts `ParticleManager.addParticle()` and cancels particles spawned beyond the set distance (default 32 blocks)

All distances are adjustable per type with sliders.

### Math Optimization

Replaces `MathHelper.sin()`, `MathHelper.cos()`, `MathHelper.atan2()`, and `MathHelper.fastInverseSqrt()` with faster implementations:

- **sin/cos** use a pre-computed 65536-entry lookup table instead of `java.lang.Math`
- **atan2** uses a polynomial approximation (degree-3 minimax)
- **inverseSqrt** uses the Quake III fast inverse square root with two Newton-Raphson iterations

These are used throughout Minecraft's rendering and physics code.

### GL State Caching

Tracks the current OpenGL state (bound textures, blend mode, depth test, face culling, blend function parameters, depth function) and skips redundant `GlStateManager` calls when the state hasn't changed. Intercepts 7 different GL state methods via mixin.

Auto-disables when ImmediatelyFast is installed since it handles similar optimizations.

### Memory Management

- **Object pooling**: Thread-local pools for `BlockPos.Mutable` and `Vector3f` objects (borrow/return pattern, max 512 per pool) to reduce allocations
- **Buffer pooling**: Reuses direct `ByteBuffer` objects across 6 size buckets (256B to 256KB, max 64 per bucket)
- **String deduplication**: `WeakReference`-based intern pool to deduplicate repeated strings
- **Palette deduplication**: FNV-1a hash-based dedup for chunk palette arrays

The memory compactor runs a cleanup pass every 600 ticks to evict stale entries.

### Thread Priority

Sets the render thread to `Thread.MAX_PRIORITY` on startup. Worker threads created by Helium run at below-normal priority so they don't compete with rendering.

### Multiplayer QoL

- **Fast server pinging**: Replaces vanilla's server pinger thread pool with a larger one (sized to `max(availableProcessors, 8)` threads instead of vanilla's default). All servers ping concurrently
- **Scroll preservation**: When you press refresh in the multiplayer screen, the scroll position is saved from the old widget and restored on the new one via a `@Redirect` on `MinecraftClient.setScreen()` inside `MultiplayerScreen.refresh()`

---

<h2>📦 Installation</h2>

1. Install [Fabric Loader](https://fabricmc.net/) (0.16.0+)
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Install [Sodium](https://modrinth.com/mod/sodium) (0.6.0+)
4. Drop Helium in your mods folder, launch

**Optional:** [ModMenu](https://modrinth.com/mod/modmenu) for a master on/off toggle

---

<h2>🤝 Compatibility</h2>

- **Sodium** (required): Helium's config pages live inside Sodium's video settings
- **Lithium**: Compatible, they optimize different things (Lithium focuses on game logic, Helium on rendering/client)
- **Iris**: Compatible
- **ImmediatelyFast**: Compatible. GL state cache auto-disables via mod detection to avoid conflicts
- **ViaFabricPlus**: Compatible, multiplayer features don't interfere with protocol translation

---

<h2>⚙️ How It Works</h2>

Helium is a mixin-based mod. It injects into Minecraft's rendering pipeline, math utilities, and multiplayer UI at specific points:

| Mixin Target | What It Does |
|---|---|
| `EntityRenderer.shouldRender()` | Distance check before entity rendering |
| `BlockEntityRenderHandler` (Sodium API) | Register per-type block entity render predicates |
| `ParticleManager.addParticle()` | Distance check before particle creation |
| `MathHelper.sin/cos/atan2/fastInverseSqrt` | Replace with LUT / polynomial / fast inverseSqrt |
| `GlStateManager._bindTexture/_enableBlend/...` | Skip call if state unchanged |
| `ClientWorld.tick()` | Run memory compactor cleanup |
| `MultiplayerServerListWidget.<init>` | Replace pinger thread pool |
| `MultiplayerScreen.refresh()` → `setScreen()` | Save and restore scroll position |

Source code is available on [GitHub](https://github.com/qborder/Helium).

---

<h2>📝 License</h2>

Licensed under the [MIT License](https://github.com/qborder/Helium/blob/HEAD/LICENSE).
