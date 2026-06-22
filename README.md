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
- **Auth**: AndroidX Security Crypto for token storage
- **Mock data**: JSON assets loaded at runtime (toggled via `BuildConfig.USE_MOCK_DATA`)

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
