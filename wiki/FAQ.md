# FAQ

common questions and issues.

---

## general

**does Helium work without Sodium?**

no. Sodium is required. the config UI lives inside Sodium's video settings panel and the block entity culling system uses Sodium's `BlockEntityRenderHandler` API directly.

**does Helium work on servers?**

Helium is client-side only. you can join any server with it installed. nothing runs on the server. no server-side mods or permissions needed.

**will this get me banned on servers?**

no. Helium doesn't modify any game mechanics, doesn't send unusual packets, and doesn't give any gameplay advantage. it's purely a rendering and performance mod. same category as Sodium or Lithium.

**does Helium work on Quilt?**

yes. there's a `CrossLoaderCompat` layer that handles both Fabric and Quilt.

**does it work on Forge / NeoForge?**

no. Fabric and Quilt only.

---

## performance

**I'm not seeing any improvement, why?**

depends heavily on what your bottleneck actually is. Helium helps most in these scenarios:
- lots of entities in view (mob farms, large servers, villages)
- lots of block entities in view (storage rooms, bases with many chests)
- heavy particle scenes (TNT farms, explosion effects)
- dense forests (leaf culling)

if you're bottlenecked by chunk rendering, Sodium itself is handling that and Helium won't add much on top.

**will Helium help with chunk loading lag?**

not really. chunk building is Sodium's domain. Helium handles what happens after chunks are built, so entity rendering, block entity rendering, particles, and GL overhead.

**my FPS is the same as before**

check that the mod is actually loaded by looking in the mod list. also check the log for any `helium` initialization messages. if you see `helium is disabled via config`, the master switch got turned off.

**I have a high-end GPU and Helium isn't doing much**

that's expected. the gains from GL state caching, DSA, and GPU-specific tuning are more noticeable on mid-range hardware and iGPUs. the reflex frame timing and frame pacing features are more relevant for high-end setups where latency matters more than raw FPS.

---

## config

**where is the config file?**

`.minecraft/config/helium.json`

**I broke my config and the game won't start**

delete `helium.json` and Helium will regenerate it with defaults on next launch.

**what's the difference between entity culling and Sodium's built-in culling?**

Sodium does frustum culling (skips things outside your field of view). Helium does distance culling (skips things too far away even if they're technically in view). they complement each other.

**leaf culling mode, which one should I use?**

- FAST is the default and safe for most people
- CHECK gives better results in dense jungles, small CPU cost
- DEPTH is good for very thick canopies, configurable depth
- RANDOM is the most aggressive but can cause visible leaf gaps if set too high
- if you see transparency artifacts or missing leaves, drop back to FAST

**temporal reprojection is off by default, should I turn it on?**

only if you want to experiment. it skips re-rendering distant entities on alternating frames when the camera hasn't moved much. it works but it's marked experimental for a reason. fast camera movement or certain entity animations can look slightly off. leave it off unless you're specifically trying to squeeze more performance out of entity-heavy scenes.

---

## issues

**game crashes on startup with Helium**

grab the crash report from `.minecraft/crash-reports/` and open an issue on GitHub. also attach your `latest.log`.

**game is stable but one feature seems broken**

check `latest.log` for lines starting with `helium`. each feature logs its init status and any failures. if a feature throws during init it disables itself and logs the reason.

**GL state cache is causing visual glitches**

turn off `glStateCache` in the config. also check if ImmediatelyFast is installed. if it is, Helium auto-disables its GL cache, and if you're seeing this issue it means the auto-detection may have failed. report it on GitHub.

**weird leaf rendering / missing leaves**

change `leafCullingMode` to FAST or OFF. this is almost always the cause of leaf visual issues.

**the FPS overlay is in the wrong position**

change `overlayPosition` in config to TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, or BOTTOM_RIGHT.

**the game window doesn't have the modern Windows styling**

`windowStyle` only works on Windows 10 22H2 and newer, and Windows 11. it also doesn't apply in fullscreen mode. if you're on an older Windows version or in fullscreen, it won't show.

**server list isn't pinging faster**

make sure `fastServerPing` is on in config. if your servers are on the same network or all have low latency, the difference will be minimal since the optimization matters most when some servers are slow to respond.

---

## reporting bugs

open an issue at [github.com/qborder/Helium/issues](https://github.com/qborder/Helium/issues)

include:
- Minecraft version
- Helium version
- Sodium version
- other mods installed
- `latest.log` or crash report
- steps to reproduce

the more info the faster it gets fixed.
