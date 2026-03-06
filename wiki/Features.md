# Features

this page covers every system Helium runs and what it actually does under the hood. if you just want to know what to turn on or off, check [[Configuration]].

---

## Distance Culling

the single biggest performance win in most scenarios.

**entity culling**

entities beyond a configurable distance from the player are skipped entirely before the render call happens. default is 64 blocks. in a mob farm with 100 zombies at 64+ blocks, none of them get rendered at all.

**block entity culling**

registered through Sodium's `BlockEntityRenderHandler` API with per-type distance predicates. covers chests, signs, hanging signs, banners, bells, campfires, enchanting tables, end portals, end gateways, decorated pots, beds, shulker boxes, skulls, and conduits. default is 48 blocks.

a storage room with 100 chests where most are beyond 48 blocks will skip all of those render calls.

**particle culling**

particles beyond 32 blocks are discarded before being submitted to the render pipeline. TNT explosions or large particle effects far away cost nothing.

**particle limiter**

hard cap on simultaneous active particles (default 1000). when the cap is hit, a priority system keeps combat-relevant particles (critical hits, explosions, damage) and drops ambient decorative ones first.

**what this affects most**: mob farms, large servers with many players visible, storage rooms, TNT explosions, particle-heavy farms.

---

## Leaf Culling

reduces geometry in dense tree canopies by skipping faces that will never be visible.

has 8 modes you can pick from:

| mode | what it does |
|---|---|
| OFF | disabled |
| FAST | skips faces between adjacent leaf blocks, handled in the isSideInvisible mixin |
| VERTICAL | same as FAST but also culls top/bottom faces |
| STATE | culls based on the leaf block's distance property (modulo 3 check) |
| CHECK | walks all 6 neighbors, culls the face if the block is fully surrounded by leaves or solid blocks |
| GAP | traces in the face direction and culls if there's a continuous gap of leaves, depth configurable |
| DEPTH | traces outward and culls if no air block exists within the configured depth |
| RANDOM | probabilistic culling using a stable position-based hash, configurable rejection rate |

default is FAST. CHECK and DEPTH give better culling in dense canopies at a small CPU cost. RANDOM is the most aggressive and will have visible artifacts in some cases.

**what this affects most**: forests, jungles, any scene with large trees.

---

## Fast Math

replaces several hot math operations with faster versions.

**sin / cos lookup tables**

65536-entry precomputed float arrays for sin and cos. instead of calling `Math.sin()` which goes through the JDK and eventually C, it converts the radian value to a table index and does an array lookup. runs every frame across the entire renderer.

**fast atan2**

polynomial minimax approximation instead of the standard library version. used in entity rotation and lighting angle calculations.

**fast inverseSqrt**

the classic bit manipulation trick (double-precision version with magic constant `0x5FE6EB50C7B537A9`) followed by two Newton-Raphson refinement steps. used in vector normalization throughout the renderer.

**branchless integer math**

min, max, and abs using bitwise shifts instead of conditional branches. avoids branch mispredictions in tight loops.

**JOML fast math**

sets `joml.fastmath=true` as a JVM system property at startup. JOML is the matrix library Minecraft uses for all transformation math, so this enables JOML's own internal fast approximations throughout the renderer with zero code changes on Helium's side.

---

## GL State Cache

OpenGL state changes cross the JVM-to-native boundary and go through the driver. even if a call doesn't actually change anything, it still has overhead. this tracks what's already set and skips the call entirely.

tracks across 16 texture units, active shader program, bound VAO, bound VBO, blend enable/disable, depth test, cull face, blend function, and depth function.

auto-disables when ImmediatelyFast is detected since that mod already handles the same thing.

on Intel iGPUs, switches to aggressive mode which is more conservative about skipping cache checks, since Intel hardware has higher relative overhead per state change.

---

## Direct State Access (DSA)

when OpenGL 4.5 or `GL_ARB_direct_state_access` is available, Helium uses DSA to modify GPU objects without binding them first.

normal OpenGL: bind object → modify it → continue. DSA: modify it directly by ID.

this removes bind calls from the hot path entirely rather than just deduplicating them. used for buffers, VAOs, textures, and framebuffers.

---

## Frame Light Cache

a 4096-slot open-addressed hash table that caches per-block-position light level lookups within a single frame.

keys are packed longs from block XYZ coordinates. the cache tag increments every frame, so stale entries are automatically invalid without needing to clear the array. collisions just overwrite.

light lookups happen thousands of times per frame during entity rendering. this eliminates most of the repeated world reads without any explicit invalidation logic needed.

---

## Shader Uniform Cache

caches `glGetUniformLocation` results per program and uniform name so repeated lookups hit a HashMap instead of crossing into the GL driver.

also caches the last submitted value per uniform location. if the value hasn't changed since the last submit, the `glUniform` call is skipped. things like projection matrix, fog color, and global game time often don't change between draw calls within the same frame.

---

## Memory Pooling

**object pool**

per-thread pools of `BlockPos.Mutable` and `Vector3f` instances. both are allocated at very high frequency during entity and chunk rendering. borrowing from the pool instead of `new` avoids heap allocation entirely.

**native memory manager**

pools of direct `ByteBuffer` objects in six size classes: 1KB, 4KB, 16KB, 64KB, 256KB, 1MB. total cap is configurable (default 64MB). freed buffers return to the pool and are reused on the next matching request.

**packet buffer pool**

256 pre-pooled 8KB byte arrays for network packet encoding. trimmed down to 192 if left unused over 200 ticks.

the combined effect is fewer objects hitting the heap per second and lower GC pause frequency. the difference shows up most in long sessions and in scenarios with high entity counts where BlockPos.Mutable gets created constantly.

---

## Object Deduplication

deduplicates repeated object instances in memory:

- String instances for namespaces, resource paths, and block state property names (identity-checked intern pool)
- int arrays for quad vertex data (hash comparison via fastutil's `Hash.Strategy`)
- `Identifier` instances from both the registry and asset path sides

reduces heap size and improves reference cache locality, particularly noticeable during world loading and chunk building where many identical strings and identifiers get created.

---

## Client Tick Cache

LRU caches for biome color and light level lookups per block position. 16384 entries each, keyed by packed long coordinates.

invalidated on block changes and world reload. reduces repeated world queries during the tick loop.

---

## Async Light Engine

light updates are throttled and managed through a background thread pool:

- pool sized at `availableProcessors / 4`, daemon threads at `NORM_PRIORITY - 2`
- max 32 light update batches per frame, excess is deferred and tracked
- queue is capped at 1024 pending updates to avoid runaway memory growth
- up to 64 completed updates are applied back per tick

this prevents sudden light update spikes (like TNT or large redstone circuits) from stalling the render thread.

---

## Thread Priority

the render thread is set to `Thread.MAX_PRIORITY` (10) via the JVM thread priority API once the window initializes. Helium's own worker threads (startup pool, light engine) are set below normal priority so they don't compete with rendering.

---

## Reflex Frame Timing

measures the CPU-to-GPU timing gap and corrects for it.

two OpenGL timer query objects alternate each frame to avoid stalling the GPU while waiting for results. uses `GL_ARB_timer_query` for absolute GPU timestamps on NVIDIA hardware, falls back to `GL_EXT_timer_query` for elapsed time on other GPUs. if neither is available, this feature disables itself.

at the start of each frame, reads back the previous frame's GPU completion timestamp and computes the delta against current CPU time. smooths the delta using exponential weighted moving average (alpha 0.15). if the CPU is running ahead, inserts a calibrated wait using `Thread.onSpinWait()` for the coarse portion and `Thread.sleep(0, 1000)` for sub-millisecond precision.

the result is a shorter render queue depth, which reduces the gap between player input and that input appearing on screen.

---

## Frame Pacing

tracks the last 60 frame times in a rolling array and computes a smoothed average frame time. at the end of each frame, checks remaining budget and sleeps for half of it if the window is between 0.5ms and 8ms.

keeps frame delivery more consistent and prevents frames bunching up in the GPU queue, which shows up as microstutter even at high average FPS.

---

## GPU-Specific Tuning

Helium detects your GPU vendor from the OpenGL renderer string and applies hardware-specific settings:

**NVIDIA**

enables `GL_KHR_parallel_shader_compile` with thread count `0xFFFFFFFF`, letting the driver use as many compilation threads as it wants. reduces shader compile stutter when loading into a world.

**AMD**

aligns vertex buffer allocations to 256-byte boundaries to match AMD hardware's preferred read stride. checks for `GL_AMD_pinned_memory` availability.

**Intel**

enables aggressive GL state caching mode since Intel iGPUs have higher relative overhead per state change than discrete GPUs.

---

## Adaptive Sync Detection

reads the monitor refresh rate via GLFW. if the rate is above 60hz on NVIDIA or AMD hardware, assumes G-Sync or FreeSync is active and sets the internal frame target to refresh rate minus 3 FPS. this keeps frames inside the VRR operating range and avoids falling back to hard vsync.

---

## Temporal Reprojection

> **off by default. experimental.**

stores the previous frame's view-projection matrix and computes a reprojection matrix by inverting the current VP and multiplying. for entities beyond 48 blocks, on even-numbered frames only, checks if the reprojected screen position from the previous frame is within a motion threshold (0.02 squared screen distance). if yes, skips re-rendering that entity.

when camera confidence is low (reprojection mismatch ratio below 30%) it disables itself automatically and lets everything render normally.

---

## Model Cache

`ConcurrentHashMap`-backed cache for baked block model lookups, keyed by a long hash of the block state. default size is 64MB (expressed as max entry count at ~512 bytes per entry). reduces repeated re-baking of the same models across multiple chunk rebuild passes.

---

## Packet Batching

buffers outgoing packets up to 32 packets or 64KB before flushing. reduces syscall frequency under burst conditions like fast movement or large inventory operations.

packet byte arrays are also pooled (see memory pooling section).

---

## Fast Startup

parallelizes game startup tasks across a thread pool sized to `availableProcessors`, daemon threads at `NORM_PRIORITY + 1`. tasks are submitted as `CompletableFuture`, pool is shut down with a configurable timeout after startup completes.

---

## Text Render Optimizer

caches glyph lookups keyed by a packed long from codepoint (upper 32 bits) and font/style hash (lower 32 bits). skips repeated glyph resolution for the same character and style. 1024 entry cache, cleared on overflow.

---

## Parallel Server Ping

pings all servers in the server list concurrently instead of sequentially. total refresh time drops from however long all your servers take added up, to roughly the latency of your slowest server.

---

## Async Resource Pack Reload

moves resource pack reloading off the main thread. reload happens in the background, results are applied back on the main thread when done. avoids the freeze that normally happens during reload.
