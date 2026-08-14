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
- osmdroid (OpenStreetMap tiles) for the booking-flow map
- AGP 9.2.1 / compileSdk & targetSdk 37, minSdk 24

## Module / package layout

```
app/src/main/java/com/qryde/qryderiderapp/
├── core/
│   ├── common/         AppResult (domain-level success/error wrapper)
│   ├── designsystem/   Compose theme, colors, typography
│   ├── di/             Hilt modules (AppConfig, DataStore, Network, Repository)
│   ├── logging/        AppLogger — gated by isDeveloperMode, with a REST tag
│   ├── network/        OkHttp/Retrofit setup + REST request/response logging
│   ├── permissions/     Runtime (dangerous) permissions requested at Splash
│   └── utils/           AppConfig (per-flavor config resolved at DI time)
├── domain/
│   ├── model/          ServerConfig, OeRegistryValues, LoginSession, NewAccountDetails, ...
│   ├── repository/     AuthRepository, RegistrationRepository, ForgotPasswordRepository,
│   │                   DeviceRegistrationRepository, SmsVerificationRepository,
│   │                   CommunityRepository, OeRegistryRepository, ServerConfigRepository
│   └── usecase/        LoginUseCase, AttemptSilentLoginUseCase, CreateAccountUseCase,
│                        CheckUserIdAvailableUseCase, CheckEmailAvailableUseCase,
│                        SendVerificationCodeUseCase, RegisterDeviceUseCase,
│                        RequestPasswordResetUseCase, FetchJoinedCommunitiesUseCase,
│                        FetchOeRegistryValuesUseCase, ResolveServerConfigUseCase
├── data/
│   ├── datastore/       ServerConfigDataStore, LoginSessionDataStore, LoginCredentialsDataStore
│   ├── mapper/          Wire-format → domain model parsing per command (see below),
│   │                    DeviceRegistrationRequestBuilder (shared 100U request builder)
│   ├── remote/
│   │   ├── socket/      ServerConfigSocketClient (discovery WebSocket)
│   │   └── rest/        QtipCommandClient (generic QTIP REST command POST)
│   └── repository/      Repository implementations for everything under domain/repository
└── presentation/
    ├── navigation/      AppNavGraph, AuthNavGraph, MainNavGraph (mainTabNavGraph), NavRoutes
    ├── splash/          Splash screen + ViewModel (silent-login attempt, see flow below)
    ├── auth/
    │   ├── login/           Login screen
    │   ├── verification/    Phone verification (100UV) — shared by signup and re-registration
    │   ├── otp/              OTP entry (locally verified against a client-generated code)
    │   ├── profile/          Create Profile (signup) — live userId/email availability checks
    │   ├── forgotpassword/   Forgot Password (5FP2) — userId + email only, no OTP
    │   └── resetpassword/    Set New Password (forced reset path from 5G's requiresPasswordReset)
    ├── main/            MainScaffold — bottom nav (Home / Trips / Payments / Profile / Vui)
    ├── home/            Booking flow: search → set location on map → ride details → date/time
    │                     pickers → additional information → choose a service → select payment
    ├── trips/           Trips list (status tabs) → Receipt, Cancel Trip
    ├── payments/        Balance card, My QCard/Transactions/Purchases tabs, filter-by-date,
    │                     Add Funds
    ├── profile/         Profile options list, Edit Profile, Help, Select Language/Community
    ├── vui/             Placeholder tab (no design given yet beyond the tab itself)
    └── components/      Shared Compose widgets: QrydeTextField/PasswordField/PhoneField,
                          OtpInputField, OsmMapView, CurrentLocationPinOverlay
```

## Build flavors

Single `environment` flavor dimension — no multi-tenant dimension yet:

| Flavor    | Application ID suffix | Notes                     |
|-----------|------------------------|----------------------------|
| `dev`     | `.dev`                 | `isDeveloperMode = true`  |
| `staging` | *(disabled, see below)*| |
| `prod`    | (none)                 |                            |

Each flavor has its own `res/values/config.xml` (`isDeveloperMode`, `bypassOtp`,
`isSendAnalytics`, `api_base_url`, `websocket_url`, `base_wsuri`) and
`strings.xml` (app name), read into a single `AppConfig` via Hilt.

`applicationId` is currently set to `com.QRyde.Marketplace` (not the package
namespace) and the `staging` flavor's `.stg` suffix is commented out — both
deliberate, to match the real legacy backend's expected client identity
while testing against staging.

## Current app flow

### Splash → server discovery → silent login

1. **Splash screen** requests the required runtime (dangerous) permissions
   ([RuntimePermissions.kt](app/src/main/java/com/qryde/qryderiderapp/core/permissions/RuntimePermissions.kt)).
2. It opens the discovery **WebSocket** (`base_wsuri`, e.g.
   `ws://cfg.qryde.com:18575`) and sends an environment-specific message
   (`QRALL2_T~Android` / `_B~Android` / `_L~Android` for staging/dev/prod).
   The `;`-delimited `KEY~VALUE` response is parsed generically into a
   `ServerConfig` (`Map<String, String>` of endpoint name → resolved URL) and
   persisted to DataStore.
3. On success, it calls the QTIP REST endpoint with command `17CV` (legacy "OE
   registry values") into `OeRegistryValues` — best-effort, failure is logged
   but never blocks navigation.
4. It then attempts a **silent login** with any persisted credentials (`5G`).
   On success it navigates straight to the main app; if the response's
   `requiresPasswordReset` flag is set, it routes to Set New Password instead.
   On failure (or no saved credentials) it falls through to the Login screen.
5. Any failure resolving the server config surfaces as a **Toast** —
   deliberately no in-UI error/retry/progress state; failed resolution retries
   internally (bounded retries in `ServerConfigRepositoryImpl`) but leaves no
   persistent UI feedback beyond the toast.

### Auth (legacy QTIP commands, real wire format)

- **Login** (`5G`) — also returns `requiresPasswordReset`, consumed to force a
  password reset before entering the app.
- **Forgot Password** (`5FP2`) — userId + email only; the legacy backend texts
  a temporary password directly, there is no OTP step here.
- **Sign up**, in the real order the legacy client uses — phone verification
  first, profile details last:
  1. **Phone verification** (`100UV`) sends an SMS; the 6-digit code is
     **generated client-side**, embedded in the SMS text, and never round-tripped
     to the server for verification — the OTP screen checks it locally.
  2. **Create Profile** — first/last name, then **live availability checks**
     (`100ID` for userId, `5E` for email) as each field loses focus; the submit
     button is disabled until *both* come back available.
  3. **Account creation** (`100U`) — same wire format/request builder as the
     post-login *device re-registration* call (`DeviceRegistrationRequestBuilder`),
     just with signup-time values instead of the stored session's.
- **Set New Password** — reached either from the forced-reset path above or
  from Forgot Password's flow.

### Main app (bottom nav: Home / Trips / Payments / Profile / Vui)

Built as a **presentation-layer-first pass** against the Figma mocks — real
navigation and screens, but **static/sample data**, no backend wiring yet
(explicitly scoped that way; backend wiring for these screens is a follow-up).

- **Home** — search destination (address suggestions appear as a dropdown
  under the search field, not a flat always-visible list) → **Set Location on
  Map** (a near-fullscreen sheet with a live OpenStreetMap view; the pin stays
  fixed at center while the map pans underneath it — drag-to-pick, same
  pattern as Uber/Ola/Google Maps' location pickers) → Ride Details (single or
  recurring trips) → date/time pickers (Material3's built-in pickers) →
  Additional Information (shares one `BookRideViewModel` with Home via a
  graph-scoped ViewModel) → Choose a Service → Select Payment Method → Book.
- **Trips** — list with status tabs (Upcoming/Completed/Cancelled) → Receipt
  (fare breakdown) or Cancel Trip (reason + notes).
- **Payments** — balance card + Add Funds, My QCard/Transactions/Purchases
  tabs, transactions/purchases grouped by date, filter-by-date bottom sheet.
- **Profile** — options list (Community, Language, App Guide, Help, About,
  Logout) with Select Language / Select Community dialogs, Edit Profile, Help
  (contact form + support info).
- **Vui** — placeholder tab; no design spec given yet beyond its icon/name.

The map (`OsmMapView`) renders real OSM tiles via osmdroid; the "current
location" pin is a plain Compose overlay (`CurrentLocationPinOverlay` — an
`Image` plus a real `Text` pill), not anything baked into a marker bitmap, so
it stays crisp and themeable.

## Networking / security notes

- The discovery socket host (`cfg.qryde.com`) is allow-listed for cleartext
  traffic via a scoped `network_security_config.xml` — the rest of the app is
  not exempted from Android's cleartext block.
- All REST request/response logging (including the outgoing payload, not just
  the response) goes through a custom OkHttp `RestLoggingInterceptor` tagged
  `REST`, gated by `AppLogger.isEnabled` (driven by the `isDeveloperMode`
  flavor flag).
- Legacy wire format used across every QTIP command: `char14`/`char15`
  (ASCII Shift-Out/Shift-In) as field/row delimiters, `~` splitting the
  top-level command from its payload, and the literal string `"..."` as a
  null/placeholder field.

## Building

Open in Android Studio and run the `dev`/`staging`/`prod` debug variants
directly — Gradle in some sandboxed/CI shells on Windows can fail with
`Unable to establish loopback connection` (a known JDK/Windows loopback-socket
issue unrelated to this project); Android Studio's bundled JDK does not hit it.

## Known gaps / open items

- No multi-tenant flavor dimension yet (deferred until needed).
- Trips / Payments / Profile screens are all static sample data — no backend
  commands wired up for them yet (that's the next pass, now that the
  presentation layer is in place).
- "Set Location on Map" saves the picked latitude/longitude as a formatted
  string, not a real street address — no reverse-geocoding service wired up.
- Vui tab is a placeholder only.
- `OeRegistryValues` only exposes a generic key lookup — none of the legacy
  per-community feature flags (site config, tracker config, etc.) have been
  ported to typed models since nothing in this app consumes them yet.
