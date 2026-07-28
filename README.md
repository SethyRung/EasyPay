# EasyPay

A modern Android wallet demo app built with **Jetpack Compose** and **Material 3**, showcasing a warm-canvas design system.

## Features

- **Onboarding** — animated welcome flow with step indicator
- **Authentication** — email/password login and multi-step registration with password strength meter
- **Home** — balance hero with animated count-up, quick actions grid, recent transactions
- **Send Money** — numeric keypad, amount display, transfer confirmation
- **Transaction Detail** — dark hero, status badge, info rows
- **Notifications** — grouped by date, tabbed by category
- **Profile** — user info, logout with confirmation

## Architecture

- **UI**: Jetpack Compose + Navigation Compose
- **State**: ViewModels with sealed `UiState` / `Event` / `Effect` per screen
- **DI**: Hilt (`@HiltViewModel`, `@Inject`)
- **Networking**: Retrofit + OkHttp + Kotlinx Serialization
- **Auth**: better-auth session token (opaque, 7-day, auto-extended) stored via AndroidX Security Crypto
- **Mock data**: JSON assets loaded at runtime (toggled via `BuildConfig.USE_MOCK_DATA`)

## Configuration

Production endpoints are wired in via two build-time values:

- `BASE_URL` — `data/api/ApiProvider.kt`, `https://easypay-backend-production-9162.up.railway.app/api/`. Single Kotlin constant; both debug and release hit the same backend.
- `GLITCH_HOST` — `app/build.gradle.kts`, `https://glitch.sethyrung.com`. Set per buildType (currently identical for debug and release).

Cleartext is whitelisted only for `10.0.2.2` / `localhost` / `127.0.0.1` in `res/xml/network_security_config.xml`; production traffic is HTTPS.

## Build

```bash
./gradlew app:build              # assemble + unit tests
./gradlew app:testDebugUnitTest  # unit tests only
./gradlew app:lint               # lint
./gradlew app:installDebug       # install to device/emulator
```

## Design System

The app uses a custom design token system (`com.sethy.easypay.design`) built on top of Material 3:
- Warm cream canvas (`#faf9f5`), coral primary (`#cc785c`), dark navy surfaces (`#181715`)
- Geist-inspired typography with negative letter-spacing on display sizes
- M3 wrappers for all standard components, custom composables for design gaps

## Key Files

| Path | Purpose |
|---|---|
| `design/` | Design tokens, theme, M3 wrappers, custom composables |
| `data/` | Models, DTOs, source abstraction, repositories |
| `domain/usecase/` | Business logic use cases |
| `di/` | Hilt modules |
| `navigation/` | Route definitions, NavGraph, AuthGate |
| `ui/screens/` | Per-screen composables |
| `ui/viewmodel/` | ViewModels per screen |
| `ui/state/` | Sealed UiState / Event / Effect per screen |
