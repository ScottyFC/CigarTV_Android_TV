# CigarTV — Android TV

Native Android TV app (Kotlin + Leanback + Media3/ExoPlayer), mirroring the Roku
app's architecture and design. This is a **separate native rewrite**, not a port of
the Roku BrightScript — the shared parts are the data model, the freecast API
architecture, the catalog (`assets/catalog.json`, same file the Roku app ships), and
the brand system (Poppins, `#f3d389`, key art).

## Requirements
- Android Studio (Ladybug or newer)
- JDK 17
- An Android TV emulator (Android TV 1080p device profile) or a real Android TV /
  Fire TV device with developer mode + ADB

## Open & run
1. `File → Open` this folder in Android Studio; let Gradle sync (it will download
   the AGP/Kotlin/Media3/Leanback dependencies).
2. Create an **Android TV (1080p)** emulator via Device Manager, or connect a device
   over ADB (`adb connect <tv-ip>`).
3. Run the `app` configuration. It launches into the "Originals" browse grid.

## Current state (test mode)
- Loads the bundled `assets/catalog.json` (6 series, 92 episodes) — no key needed.
- Browse grid of series (branded key art) → series detail (pinned header, season
  spinner, scrolling episode list) → fullscreen ExoPlayer (direct MP4).
- `FreecastConfig.enabled = false`, so it runs entirely offline for UI/UX testing.

## Going live (freecast API)
Everything routes through `data/FreecastConfig.kt` — set `apiKey` and flip
`enabled`, same as the Roku app. Two things are stubbed pending real API access:
- `CatalogRepository.loadFromApi()` — implement the shows→seasons→episodes chain
  once the show/episodes JSON shapes are confirmed (only `/streams` is confirmed).
- `PlaybackActivity.buildMediaItem()` — swap the direct-MP4 item for a
  `StreamResolver`-selected DASH/HLS item, wrapping Widevine `DrmConfiguration`
  when `ResolvedStream.widevineLicenseUrl` is set. The resolver itself
  (`data/StreamResolver.kt`) is done and matches the Roku selection logic.

## Security
Same rule as Roku: a client app can't hide a secret. For production, point
`FreecastConfig.baseUrl` at a backend proxy and leave `apiKey` empty. Widevine
(wired) protects the media; the proxy protects the API key.

## Not yet built (next passes)
- Startup chooser (Live vs Browse) and live playback + EPG overlay (Roku has these;
  the browse/detail/playback core came first here).
- Smoke/logo-pulse home screen treatment.
- App icon/banner are auto-generated placeholders — replace with final art.
