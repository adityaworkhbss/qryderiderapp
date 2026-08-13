# QRyde Rider App

Android rider app rewrite, built with Kotlin + Jetpack Compose following MVVM /
Clean Architecture (Presentation → Domain → Data). Single tenant for now, with
build-time environment flavors (`dev` / `staging` / `prod`).

## Tech stack

- Kotlin 2.4.10, Jetpack Compose (BOM 2026.08.00), Navigation Compose with
  type-safe `@Serializable` routes
- Hilt (DI) + KSP
- Retrofit + OkHttp (REST), OkHttp native WebSocket (discovery socket)
- kotlinx.serialization, kotlinx.coroutines
- Jetpack Preferences DataStore (local persistence)
- AGP 9.2.1 / compileSdk & targetSdk 37, minSdk 24

## Module / package layout

```
app/src/main/java/com/qryde/qryderiderapp/
├── core/
│   ├── common/        AppResult (domain-level success/error wrapper)
│   ├── designsystem/  Compose theme, colors, typography
│   ├── di/            Hilt modules (AppConfig, DataStore, Network, Repository)
│   ├── logging/        AppLogger — gated by isDeveloperMode, with a REST tag
│   ├── network/        OkHttp/Retrofit setup + REST request/response logging
│   ├── permissions/     Runtime (dangerous) permissions requested at Splash
│   └── utils/           AppConfig (per-flavor config resolved at DI time)
├── domain/
│   ├── model/          ServerConfig, OeRegistryValues
│   ├── repository/     Repository interfaces
│   └── usecase/        ResolveServerConfigUseCase, FetchOeRegistryValuesUseCase
├── data/
│   ├── datastore/      ServerConfigDataStore (persists resolved endpoints)
│   ├── mapper/         Wire-format → domain model parsing
│   ├── remote/
│   │   ├── socket/     ServerConfigSocketClient (discovery WebSocket)
│   │   └── rest/       QtipCommandClient (generic QTIP REST command POST)
│   └── repository/     Repository implementations
└── presentation/
    ├── navigation/      AppNavGraph / MainNavGraph / NavRoutes
    ├── splash/          Splash screen + ViewModel (see flow below)
    └── home/            Placeholder Home screen
```

## Build flavors

Single `environment` flavor dimension — no multi-tenant dimension yet:

| Flavor    | Application ID suffix | Notes                     |
|-----------|------------------------|----------------------------|
| `dev`     | `.dev`                 | `isDeveloperMode = true`  |
| `staging` | `.stg`                 |                            |
| `prod`    | (none)                 |                            |

Each flavor has its own `res/values/config.xml` (`isDeveloperMode`, `bypassOtp`,
`isSendAnalytics`, `api_base_url`, `websocket_url`, `base_wsuri`) and
`strings.xml` (app name), read into a single `AppConfig` via Hilt.

## Current app flow

1. **Splash screen** requests the required runtime (dangerous) permissions
   ([RuntimePermissions.kt](app/src/main/java/com/qryde/qryderiderapp/core/permissions/RuntimePermissions.kt)).
2. Once permissions are resolved, it opens the discovery **WebSocket**
   (`base_wsuri`, e.g. `ws://cfg.qryde.com:18575`) and sends an
   environment-specific message (`QRALL2_T~Android` / `_B~Android` / `_L~Android`
   for staging/dev/prod). The `;`-delimited `KEY~VALUE` response is parsed
   generically into a `ServerConfig` (`Map<String, String>` of endpoint name →
   resolved URL) and persisted to DataStore.
3. On success, it calls the QTIP REST endpoint with command `17CV` (the legacy
   "OE registry values" call) and parses the response into `OeRegistryValues`
   (another generic key-value map — most of its keys are legacy per-community
   feature flags not yet used by this app). This call is best-effort: failure is
   logged but never blocks navigation.
4. Any failure resolving the server config surfaces as a **Toast** — there is
   deliberately no in-UI error/retry/progress state; a failed resolution retries
   internally (bounded retries in `ServerConfigRepositoryImpl`) but leaves no
   persistent UI feedback beyond the toast.
5. On success, navigates to a placeholder **Home** screen.

There is no authentication flow yet — it was intentionally removed early on in
favor of getting the Splash → server-config → registry flow working first.

## Networking / security notes

- The discovery socket host (`cfg.qryde.com`) is allow-listed for cleartext
  traffic via a scoped `network_security_config.xml` — the rest of the app is
  not exempted from Android's cleartext block.
- All REST request/response logging goes through a custom OkHttp
  `RestLoggingInterceptor` tagged `REST`, gated by `AppLogger.isEnabled`
  (driven by the `isDeveloperMode` flavor flag).

## Building

Open in Android Studio and run the `dev`/`staging`/`prod` debug variants
directly — Gradle in some sandboxed/CI shells on Windows can fail with
`Unable to establish loopback connection` (a known JDK/Windows loopback-socket
issue unrelated to this project); Android Studio's bundled JDK does not hit it.

## Known gaps / open items

- No multi-tenant flavor dimension yet (deferred until needed).
- No authentication/login flow yet.
- `OeRegistryValues` only exposes a generic key lookup — none of the legacy
  per-community feature flags (site config, tracker config, etc.) have been
  ported to typed models since nothing in this app consumes them yet.
