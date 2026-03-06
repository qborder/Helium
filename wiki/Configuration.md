# Configuration

the config lives at `.minecraft/config/helium.json`. you can also edit everything through the in-game UI at **Options > Video Settings > Helium** (Sodium's menu), or via the ModMenu shortcut if you have that installed.

every feature has a safe fallback. if something fails to initialize it disables itself and logs a warning. nothing will crash because of a bad config value.

---

## general

| option | default | what it does |
|---|---|---|
| `modEnabled` | true | master switch, turns off all Helium features at once |
| `devMode` | false | enables dev-mode logging and extra diagnostics, not useful in normal play |

---

## rendering

| option | default | what it does |
|---|---|---|
| `entityCulling` | true | skip rendering entities beyond the cull distance |
| `entityCullDistance` | 64 | block distance beyond which entities are not rendered |
| `blockEntityCulling` | true | skip rendering block entities beyond the cull distance |
| `blockEntityCullDistance` | 48 | block distance for block entity culling |
| `particleCulling` | true | skip particles beyond the cull distance |
| `particleCullDistance` | 32 | block distance for particle culling |
| `particleLimiting` | true | enforce a hard cap on simultaneous particles |
| `maxParticles` | 1000 | max simultaneous particles before the priority system kicks in |
| `particlePriority` | true | keep combat particles, drop decorative ones when at the cap |
| `particleBatching` | true | batch particle submissions instead of one at a time |
| `particleLOD` | false | reduce particle detail at distance |
| `particleLODDistance` | 16.0 | distance at which particle LOD kicks in |
| `particleLODReduction` | 0.3 | how much particle detail is reduced in the LOD zone (0.0 to 1.0) |
| `leafCullingMode` | "FAST" | leaf culling mode: OFF, FAST, VERTICAL, STATE, CHECK, GAP, DEPTH, RANDOM |
| `leafCullingDepth` | 2 | depth used by GAP and DEPTH modes (1 to 4) |
| `leafCullingRandomRejection` | 0.2 | rejection probability for RANDOM mode |
| `leafCullingMangroveRoots` | false | apply leaf culling logic to mangrove roots |
| `animationThrottling` | true | reduce texture animation updates for blocks not in view |
| `glStateCache` | false | skip redundant OpenGL state calls (auto-disabled with ImmediatelyFast) |
| `renderPipelining` | false | adaptive frame pacing and rolling frame time smoothing |
| `fastFramebufferBlit` | true | optimized framebuffer blit operations |
| `modelCache` | true | cache baked block models to avoid repeated re-baking |
| `modelCacheMaxMb` | 64 | max size of the model cache in MB |
| `temporalReprojection` | false | experimental: skip re-rendering distant entities on alternating frames |
| `shaderUniformCache` | true | cache uniform locations and skip unchanged glUniform calls |
| `directStateAccess` | true | use DSA to modify GPU objects without binding (requires GL 4.5 or ARB_DSA) |
| `fastBambooLight` | true | faster light calculation for bamboo blocks |
| `optimizedLightEngine` | true | throttle and batch light updates through the async light engine |
| `asyncLightUpdates` | true | process light updates on a background thread pool |
| `framebufferCleaner` | true | clean up unused framebuffers to reduce VRAM waste over time |
| `fastAnimations` | false | reduce animation update frequency for entities not in direct view |
| `cachedEnumValues` | false | cache enum value arrays to avoid repeated array allocations |
| `suppressOpenGLErrors` | true | suppress non-critical OpenGL error polling |
| `screenshotLeakFix` | true | fix a vanilla memory leak when taking screenshots |
| `signTextCulling` | true | skip sign text rendering beyond view |
| `rainCulling` | true | skip rain geometry when the player can't see it |
| `beaconBeamCulling` | true | skip beacon beam rendering out of range |
| `paintingCulling` | true | distance-cull painting entities |
| `itemFrameCulling` | true | distance-cull item frame entities |
| `itemFrameLOD` | false | switch item frames to a simplified render at range |
| `itemFrameLODRange` | 128 | distance at which item frame LOD activates |
| `poseStackPooling` | true | pool PoseStack objects to reduce GC pressure |

---

## math

| option | default | what it does |
|---|---|---|
| `fastMath` | true | LUT-based sin/cos, fast atan2, fast inverseSqrt, branchless int math |
| `simdMath` | true | batch math operations, uses JVM Vector API if available |
| `jomlFastMath` | true | set `joml.fastmath=true` JVM property for JOML matrix library |
| `fastRandom` | true | faster random number generation in hot paths |

---

## memory

| option | default | what it does |
|---|---|---|
| `memoryOptimizations` | true | enables object pool (BlockPos.Mutable, Vector3f) |
| `nativeMemory` | true | enables direct ByteBuffer pooling |
| `nativeMemoryPoolMb` | 64 | max total native memory pool size in MB |
| `reducedAllocations` | true | reduce allocation rate in hot render paths |
| `objectDeduplication` | true | deduplicate strings, quad arrays, and Identifiers |

---

## GPU

| option | default | what it does |
|---|---|---|
| `nvidiaOptimizations` | true | enable NVIDIA-specific tweaks (parallel shader compile) |
| `amdOptimizations` | true | enable AMD-specific tweaks (buffer alignment, pinned memory) |
| `intelOptimizations` | true | enable Intel-specific tweaks (aggressive GL state caching) |
| `adaptiveSync` | false | detect G-Sync/FreeSync and adjust frame target accordingly |
| `displaySyncRefreshRate` | -1 | override display sync rate manually (-1 = auto-detect) |
| `glContextUpgrade` | true | request a higher OpenGL context version if supported |
| `renderbufferDepth` | false | use renderbuffer instead of texture for depth attachment |

---

## threading

| option | default | what it does |
|---|---|---|
| `threadOptimizations` | true | set render thread to MAX_PRIORITY, init startup thread pool |
| `fastStartup` | true | parallelize startup tasks across a thread pool |

---

## network

| option | default | what it does |
|---|---|---|
| `networkOptimizations` | true | enables background resource processor |
| `packetBatching` | true | buffer outgoing packets before flushing |
| `fastServerPing` | true | ping all servers in the list concurrently |
| `fastIpPing` | true | optimize IP-based server pings |
| `preserveScrollOnRefresh` | true | keep your scroll position when refreshing the server list |
| `directConnectPreview` | true | show server info preview in the direct connect screen |

---

## frame timing / reflex

| option | default | what it does |
|---|---|---|
| `enableReflex` | true | GPU timer query-based CPU-GPU sync to reduce input latency |
| `reflexOffsetNs` | 0 | manual nanosecond offset for the reflex wait calculation |
| `reflexDebug` | false | log reflex timing data each frame |

---

## idle / inactive

| option | default | what it does |
|---|---|---|
| `reduceFpsWhenInactive` | false | drop frame rate when the window loses focus |
| `inactiveFpsLimit` | 10 | FPS limit while inactive |
| `reduceRenderDistanceWhenInactive` | false | reduce render distance while inactive |
| `inactiveRenderDistance` | 4 | render distance while inactive |
| `autoPauseOnIdle` | false | pause more aggressively when idle |
| `idleTimeoutSeconds` | 60 | seconds of inactivity before idle mode activates |
| `idleFpsLimit` | 5 | FPS limit in idle mode |
| `menuFramerateLimit` | 0 | cap FPS on menu screens (0 = no cap) |

---

## UI / tweaks

| option | default | what it does |
|---|---|---|
| `fpsOverlay` | true | show the FPS overlay |
| `overlayShowFps` | true | show FPS in the overlay |
| `overlayShowFpsMinMaxAvg` | false | show min/max/avg FPS in the overlay |
| `overlayShowMemory` | false | show memory usage in the overlay |
| `overlayShowParticles` | false | show active particle count in the overlay |
| `overlayShowCoordinates` | false | show player coordinates in the overlay |
| `overlayShowBiome` | false | show current biome in the overlay |
| `overlayPosition` | "TOP_LEFT" | overlay position: TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT |
| `overlayTransparency` | 60 | overlay background transparency (0-100) |
| `overlayBackgroundColor` | "#000000" | overlay background color as hex |
| `overlayTextColor` | "#FFFFFF" | overlay text color as hex |
| `smoothScrolling` | true | smoother scroll animations in list screens |
| `windowStyle` | true | apply Windows 11 DWM styling to the game window |
| `windowMaterial` | "TABBED" | window backdrop material: TABBED, ACRYLIC, MICA, NONE |
| `windowCorner` | "ROUND" | window corner style: ROUND, ROUND_SMALL, SQUARE, DEFAULT |
| `hotbarOptimizer` | true | reduce hotbar rendering overhead |
| `hotbarMultiSwitch` | false | allow switching multiple hotbar slots at once |
| `smoothHotbar` | true | smooth hotbar slot switching animation |
| `forceSkinParts` | true | force all skin parts to always be visible |
| `fullbright` | false | enable fullbright (gamma override) |
| `fullbrightStrength` | 10 | fullbright intensity (1-10) |
| `instantLanguageChange` | true | apply language changes without a full reload |
| `asyncPackReload` | true | reload resource packs on a background thread |
| `acceleratedText` | false | enable glyph cache for text rendering |
| `oneClickCrafting` | false | craft items with a single click |
| `fastWorldLoading` | true | reduce world loading overhead |

---

## tips

if you're on a potato PC, the biggest wins in order are:

1. `entityCullDistance` - bring it down to 32 if you're in high-entity areas
2. `blockEntityCullDistance` - 32 is fine for most people
3. `leafCullingMode` - set to CHECK or DEPTH if you play in forests
4. `particleLimiting` + `maxParticles` - lower max particles if you're CPU-bottlenecked
5. `reduceFpsWhenInactive` - free performance whenever you alt-tab

if you're on a high-end machine and want lower input latency specifically:

1. make sure `enableReflex` is on
2. set `adaptiveSync` to true if you have a G-Sync or FreeSync monitor
3. `renderPipelining` helps with frame time consistency at high FPS
