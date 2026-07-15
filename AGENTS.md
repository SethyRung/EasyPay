# EasyPay — Agent Notes

Single-module Android wallet demo (`:app`, package `com.sethy.easypay`). Kotlin + Jetpack Compose + Hilt.

> **README framing is roughly right.** The app is a Compose wallet demo whose `ui/screens/bridge/BridgeStoreScreen.kt` hosts a real `WebView` (via `AndroidView`) wired to a JS bridge (`bridge/BridgeController.kt`, `top.sunhy.component.jsbridge`). The README's "WebView bridge demo" description is accurate; some of its other details (file paths, build steps) may lag behind the code. When in doubt, trust the codebase.

## Build & verify

```bash
./gradlew app:build              # assemble + unit tests
./gradlew app:testDebugUnitTest  # unit tests only
./gradlew app:lint               # lint
./gradlew app:installDebug       # install debug APK to connected device/emulator

# Single test class or method:
./gradlew app:testDebugUnitTest --tests "com.sethy.easypay.ui.viewmodel.AuthViewModelTest"
./gradlew app:testDebugUnitTest --tests "*AuthViewModelTest.login_*"
```

`local.properties` (gitignored) must contain `sdk.dir=...` — the build fails without an Android SDK on this machine. `AGENTS.md` itself is also `.gitignore`d; edits here will not show in `git status` unless force-added.

## Architecture

- **Entry**: `MainActivity.kt` → `AuthGate()` → `EasyPayNavGraph()` (`navigation/EasyPayNavGraph.kt`).
- **DI**: Hilt. `EasyPayApp` is `@HiltAndroidApp`; screens get ViewModels via `hiltViewModel()`. Modules live in `di/`.
- **State**: one ViewModel per screen with a sealed `UiState` + `Event` (input) + `Effect` (one-shot) triplet in `ui/state/`.
- **Networking**: Retrofit + OkHttp + Kotlinx Serialization (not Gson/Moshi). Base URL `http://10.0.2.2:8080/api/` (emulator localhost) in `data/api/ApiProvider.kt`.
- **Data sources**: `Remote*DataSource` vs `Mock*DataSource` swapped in `di/DataSourceModule.kt` based on `BuildConfig.USE_MOCK_DATA` — `true` for `debug`, `false` for `release`. Mock fixtures are JSON in `app/src/main/assets/data/`.
- **Auth storage**: `androidx.security:security-crypto` via `data/local/AuthTokenManager.kt` (EncryptedSharedPreferences, `auth_prefs` file). Stores a single opaque better-auth session token — no refresh token, no client-side expiry (backend auto-extends via `get-session`).
- **NetworkModule vs ApiProvider**: prefer the Hilt-injected APIs from `NetworkModule`. `ApiProvider` is a legacy factory kept for compatibility — only `BASE_URL` is still imported elsewhere.

## Auth

- Better-auth contract: `POST auth/sign-in/email` + `POST auth/sign-up/email` (no body auth), `POST auth/sign-out` (bearer), `GET auth/get-session` (bearer).
- Single opaque session token in `Authorization: Bearer <token>`, valid 7 days, auto-extended by the backend on every protected call.
- The mobile **never** computes a refresh — there is no refresh. Silent re-auth probe on app cold start hits `get-session` (`AppSessionViewModel`).
- `AuthInterceptor.publicPaths` covers the three public endpoints + `auth/get-session` (so the silent probe can run without an Authorization header).

## Security Requirements

- **Never log** access tokens, passwords, or session cookies. Strip them from any debug log or test fixture before committing.
- Passwords are submitted to the server and **never persisted client-side**. The form fields clear after submit completes.
- The session token lives only inside `EncryptedSharedPreferences` via `AuthTokenManager`. Never `SharedPreferences`, never logcat, never plaintext files.
- WebView cookies (the bridge flow) are scoped to the glitch host and never persist to the app's local storage.

## Toolchain quirks

- `compileSdk = minSdk = targetSdk = 36`. Don't lower `minSdk` without checking every dependency.
- AGP 9 syntax in `app/build.gradle.kts`: `compileSdk { version = release(36) }`, not `compileSdk = 36`.
- Gradle version catalog: `gradle/libs.versions.toml`. Add deps there, not inline. Plugins use `alias(libs.plugins.*)`.
- `kotlin("plugin.serialization")` is applied directly in `app/build.gradle.kts` (not via catalog) — Kotlin version is pinned inline.
- Java 11 source/target compatibility.
- Compose BOM managed in version catalog (`androidx-compose-bom`).
- `networkSecurityConfig` is wired in `AndroidManifest.xml` — cleartext to `10.0.2.2` only works there.

## Testing

- Unit tests in `app/src/test/java/`. Instrumented tests in `app/src/androidTest/java/` (currently just the boilerplate `ExampleInstrumentedTest`).
- Stack: JUnit 4 + Turbine + `kotlinx-coroutines-test` (`runTest`) + `mockito-kotlin`. Use `mock()` for use cases / repositories / data sources in ViewModel tests.
- Test method names use snake_case (`login_email_validation_fires_on_EmailTouched`), not the Kotlin camelCase default.
- No snapshot tests, no live-network tests, no emulator-required suites. `testDebugUnitTest` is self-contained.