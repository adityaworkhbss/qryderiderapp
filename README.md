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
│   ├── braintree/       BraintreeDropInBridge — ClientTokenProvider + DropInListener,
│   │                    constructed in MainActivity.onCreate() before setContent{}
│   ├── common/          AppResult (domain-level success/error wrapper)
│   ├── designsystem/    Compose theme, colors, typography, system-bar appearance
│   ├── di/              Hilt modules (AppConfig, DataStore, Network, Repository)
│   ├── location/        DeviceLocationResolver — GPS fix + reverse-geocode to a state code
│   ├── logging/         AppLogger — gated by isDeveloperMode, with a REST tag
│   ├── network/         OkHttp/Retrofit setup + REST request/response logging
│   ├── permissions/     Runtime (dangerous) permissions requested at Splash
│   └── utils/           AppConfig (per-flavor config resolved at DI time)
├── domain/
│   ├── model/           ServerConfig, OeRegistryValues, CommunitySiteConfig, LoginSession,
│   │                    Community, StateCommunity, NemtClientInfo, SuggestedAddresses,
│   │                    Transaction, DeviceRegistrationInfo, RecurringTrip, ...
│   ├── repository/      One interface per command family — Auth, Registration,
│   │                    ForgotPassword, DeviceRegistration, SmsVerification, Community,
│   │                    StateCommunity, PreferredCommunity, OeRegistry, ServerConfig,
│   │                    Braintree, SuggestedAddress, ClientData, Transactions,
│   │                    AvailableFunds, RecurringTrips
│   └── usecase/         Thin `operator fun invoke()` wrappers, one (or an Observe/Set pair)
│                        per repository method — see the API integrations table below
├── data/
│   ├── datastore/       One Preferences DataStore per *cached response*, not per field —
│   │                    e.g. DeviceRegistrationDataStore caches the whole 100U payload
│   │                    (currently just userFsId) rather than a single-purpose "UserFsId"
│   │                    store; same idea for Client/Community/OeRegistry/RecurringTrips
│   ├── mapper/          Wire-format → domain model parsing, one file per command, plus:
│   │                    CommunitySiteConfigMapper (generic per-community CP_SiteConfig
│   │                    key/value lookup — added instead of one mapper function per
│   │                    config field) and DeviceRegistrationRequestBuilder (shared 100U
│   │                    request builder)
│   ├── remote/
│   │   ├── socket/      ServerConfigSocketClient (discovery WebSocket)
│   │   └── rest/        QtipCommandClient (generic QTIP_API/<command> POST),
│   │                    NemtTenantApiClient (direct POST to a per-community NEMT tenant
│   │                    host, outside the QTIP_API base — used by 7GM's PT1/direct-booking
│   │                    branch)
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
    ├── main/            MainScaffold — bottom nav (Home / Trips / Payments / Profile / Vui),
    │                     BraintreeViewModel (fetches the Drop-in client token once, app-wide),
    │                     CommunitySelectionViewModel (enable-location → 20SC → pick-community
    │                     dialogs, shown only when no preferred community is resolved yet)
    ├── home/            Booking flow: search → set location on map → ride details → date/time
    │                     pickers → additional information → choose a service → select payment.
    │                     Recent-address suggestions and Home/Work quick-place shortcuts are
    │                     real (5FAT), hidden entirely when there's no data for them.
    ├── trips/           Trips list (status tabs) → Receipt, Cancel Trip. UPCOMING is backed
    │                     by real recurring-trip data (7GM / NEMT tenant API); COMPLETED and
    │                     CANCELLED are still sample data (no API given for those yet).
    ├── payments/        Balance card (8FA), Transactions tab (25RT), My QCard/Purchases tabs
    │                     (still sample), filter-by-date, Add Funds (Braintree Drop-in)
    ├── profile/         Profile options list, Edit Profile, Help, Select Language/Community
    │                     — still static/sample, no backend wiring yet
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

Started as a presentation-layer-first pass against the Figma mocks; most tabs
are now wired to the real backend (see **API integrations** below) — Profile
and Vui, and the Trips COMPLETED/CANCELLED tabs and Payments'
My QCard/Purchases tabs, are still static/sample data pending their own API.

- **Community resolution** — on entering the main screen, if no preferred
  community is resolved yet (`PreferredCommunityDataStore` empty — see 20AUC
  below), a custom **Enable your location** dialog appears; accepting it takes
  a GPS fix, reverse-geocodes it to a state, calls `20SC`, and shows a
  bottom-sheet community picker (nearest one badged). Gated by the
  `isShowCommunitySelection` flavor flag, mirroring the legacy resource-bool.
- **Home** — search destination (address suggestions appear as a dropdown
  under the search field, not a flat always-visible list, backed by `5FAT`) →
  **Set Location on Map** (a near-fullscreen sheet with a live OpenStreetMap
  view; the pin stays fixed at center while the map pans underneath it —
  drag-to-pick, same pattern as Uber/Ola/Google Maps' location pickers) →
  Ride Details (single or recurring trips) → date/time pickers (Material3's
  built-in pickers) → Additional Information (shares one `BookRideViewModel`
  with Home via a graph-scoped ViewModel) → Choose a Service → Select Payment
  Method (Braintree Drop-in, see Add Funds below) → Book.
- **Trips** — list with status tabs (Upcoming/Completed/Cancelled); Upcoming
  is real recurring-trip data (`7GM`, or the tenant NEMT API for PT1/direct-
  booking communities) → Receipt (fare breakdown) or Cancel Trip (reason +
  notes).
- **Payments** — balance card (`8FA`) + Add Funds (Braintree Drop-in SDK,
  `8BTC`/`8CTC`), Transactions tab (`25RT`) grouped by date, My
  QCard/Purchases tabs (still sample), filter-by-date bottom sheet.
- **Profile** — options list (Community, Language, App Guide, Help, About,
  Logout) with Select Language / Select Community dialogs, Edit Profile, Help
  (contact form + support info) — still static/sample, no backend wiring yet.
- **Vui** — placeholder tab; no design spec given yet beyond its icon/name.

The map (`OsmMapView`) renders real OSM tiles via osmdroid; the "current
location" pin is a plain Compose overlay (`CurrentLocationPinOverlay` — an
`Image` plus a real `Text` pill), not anything baked into a marker bitmap, so
it stays crisp and themeable.

## API integrations

Every legacy QTIP command wired up so far, with what triggers it and which
repository owns it. All requests go through `QtipCommandClient` (`POST
<qtipRestBase>/QTIP_API/<command>`) except the NEMT tenant call, which POSTs
directly to a per-community host via `NemtTenantApiClient`.

| Command | Purpose | Triggered by | Repository |
|---|---|---|---|
| discovery WS | Resolve QTIP REST/WS endpoint URLs | App startup (Splash) | `ServerConfigRepository` |
| `17CV` | OE registry values — generic key/value blob, includes per-community `CP_SiteConfig` JSON (`ClientType`, `TenantId`, `TenantUrl`, `RLClientDirectTripBooking`, ...) | After discovery succeeds (Splash) | `OeRegistryRepository` |
| `5G` | Login; response includes `requiresPasswordReset` | Login screen submit; silent-login attempt at Splash | `AuthRepository` |
| `5FP2` | Forgot password — server texts a temporary password directly, no OTP | Forgot Password screen | `ForgotPasswordRepository` |
| `100UV` | Send phone-verification SMS; the 6-digit code is client-generated and checked locally, never round-tripped | Phone Verification screen (signup + re-registration) | `SmsVerificationRepository` |
| `100ID` | Check userId availability | Create Profile screen, live on blur | `RegistrationRepository` |
| `5E` | Check email availability | Create Profile screen, live on blur | `RegistrationRepository` |
| `100U` | Create account (signup) / re-register device (post-login, retried 3x); success payload's index-3 field is `userFsId`, captured for `8FA` | Signup submit; after local OTP match on login | `RegistrationRepository` / `DeviceRegistrationRepository` |
| `20AUC` | Joined communities list; auto-resolves the preferred community from a `pref_comm="Y"` flag or a single-community fallback, unless the rider already picked one via 20SC | After login/signup succeeds | `CommunityRepository` |
| `20SC` | Communities by (reverse-geocoded) state, for the manual community picker | "Enable your location" flow on entering the main screen | `StateCommunityRepository` |
| `5CMD` | NEMT client data (medicaid #, region/portal ids); skipped without a network call when `ClientType == "PT1"` | After `20AUC` succeeds | `ClientDataRepository` |
| `5FAT` | Recent addresses + saved-trip addresses (`purpose` field drives the Home/Work quick-place shortcuts) | Home screen load | `SuggestedAddressRepository` |
| `8BTC` → `8CTC` | Braintree customer id → Drop-in client token chain | Entering the main screen (fetched in the background while the rider does other things) | `BraintreeRepository` |
| `25RT` | Transaction history | Payments screen load | `TransactionsRepository` |
| `8FA` | Available funds balance (needs `userFsId` from `100U`) | Payments screen load | `AvailableFundsRepository` |
| `7GM` | Recurring/upcoming trips, non-PT1 / non-direct-booking communities | Trips screen load | `RecurringTripsRepository` |
| NEMT tenant `getsoinfobymmis` | Recurring/upcoming trips, PT1 or `RLClientDirectTripBooking` communities — hits `TenantUrl` directly | Trips screen load | `RecurringTripsRepository` |

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
- Profile tab and Vui tab are still static/sample data — no backend commands
  wired up for them yet.
- Trips' COMPLETED/CANCELLED tabs and Payments' My QCard/Purchases tabs are
  still static/sample data — no legacy command identified for those yet.
- "Set Location on Map" saves the picked latitude/longitude as a formatted
  string, not a real street address — no reverse-geocoding service wired up
  for that specific flow (the community-selection location flow does
  reverse-geocode, just to a state code, not a street address).
- `OeRegistryValues`/`CommunitySiteConfig` expose a generic key lookup rather
  than typed fields per config value — deliberate, to avoid a one-file/one-
  function-per-key explosion as more config values are needed; only the keys
  actually consumed so far (`ClientType`, `TenantId`, `TenantUrl`,
  `RLClientDirectTripBooking`, `CP_SiteConfig`) have call sites.
- The Cardinal Commerce (3D Secure) module is excluded from the Braintree
  Drop-in dependency (`threeDSecureEnabled: false` server-side) since it needs
  a private credentialed Maven repo not available in this environment.
- Gradle cannot run in some sandboxed shells on Windows (`Unable to establish
  loopback connection`, unrelated to this project) — build/run from Android
  Studio directly to verify changes.
