# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "com.cargoexpress.app.ExampleUnitTest"

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Lint
./gradlew lint
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Architecture

The app is a single-module Android app using **Jetpack Compose + MVVM**, targeting API 34, minSdk 24. There is **no dependency injection framework** — all services and repositories are manually instantiated in `MainActivity.kt` and passed down via constructor parameters. Each Retrofit service gets its own `Retrofit` instance (no shared client).

### Layer overview

```
core/
  common/         — Constants, Resource<T>, UIState<T>, Routes, AuthInterceptor
  data/
    remote/<feature>/  — Retrofit service interfaces + DTOs with toX() extension fns
    repository/        — Repository classes wrapping API calls in Resource<T>
  domain/         — Plain Kotlin data classes (domain models)
  presentation/<feature>/  — ViewModel + Compose Screen, optional ViewModelFactory
  ui/theme/       — Compose theme (Color, Type, Theme)
  MainActivity.kt — NavHost, all Retrofit/repo/VM instantiation, Scaffold
```

### Key patterns

**Global mutable state** lives in the `Constants` singleton (`common/Constants.kt`): `TOKEN`, `USER_ID`, `USER_NAME`, `ENTREPRENEUR_ID`, `CLIENT_ID`, `USER_ROLE`, `TRIP_ID`. These are set after login and read throughout repositories.

**Two result wrappers** are used:
- `Resource<T>` (sealed class) — returned by repository suspend functions: `Success(data)` / `Error(message)`
- `UIState<T>` (data class) — held in ViewModels as `LiveData` or `StateFlow` for the UI: `isLoading`, `data`, `message`

**DTO mapping** — every DTO file contains extension functions like `TripDto.toTrip()` and `Trip.toTripDto()` that convert between the network layer and domain models. Keep mapping logic in the DTO file, not in repositories.

**Navigation** — `MainActivity.kt` owns the `NavController` and `NavHost`. Top-level routes are in the `Routes` sealed class (`common/Routes.kt`). Sub-routes are hardcoded strings in `MainActivity.kt`:
- `"trips"` — trip list (duplicate of `Routes.TripList`)
- `"vehicles"` / `"drivers"` — fleet management (ENTREPRENEUR only)
- `"statistics"` — statistics (CLIENT only)
- `"profile"` — user profile
- `"trip_details/{tripId}"`, `"edit_trip/{tripId}"`, `"gps/{tripId}"`, `"alert/{tripId}"` — trip sub-screens
- `"register_trip"`, `"register_expense/{tripId}"`, `"register_driver"`, `"register_vehicle"` — creation flows

**ViewModels** receive `NavController` as a constructor parameter directly — navigation is triggered from inside the ViewModel.

**ViewModelFactory files** — some features have a `XViewModelFactory` used with Compose's `viewModel(factory = ...)` to pass repository parameters.

**Two user roles** — `ENTREPRENEUR` and `CLIENT`, resolved after login in `LoginViewModel.fetchProfileAndPrepareOtp()`. The current role is stored in `Constants.USER_ROLE`. The bottom nav shows different tabs per role: CLIENT sees Trips + Statistics; ENTREPRENEUR sees Trips + Vehicles + Drivers.

**`GpsScreen` and `AlertScreen`** hide the top and bottom bars (checked via `isGpsOrAlert` in `MainActivity.kt`).

### Login flow

Login is a two-step process: credentials → phone OTP.
1. `LoginViewModel.signIn()` calls the backend; on success it fetches the entrepreneur/client profile to determine role and phone number.
2. `PhoneAuthHelper` (Firebase Phone Auth) sends an SMS OTP to the user's phone.
3. After OTP verification, `commitSessionAndNavigate()` sets all `Constants` values and navigates to `TripList`.

`PhoneAuthHelper` is a singleton object that holds `storedVerificationId` and `resendToken` between steps.

### API

Base URL: `https://cargoexpress-backend-production.up.railway.app/api/v1/`

Authentication uses `Bearer ${Constants.TOKEN}` passed as a header parameter on individual repository calls. `AuthInterceptor` exists but is not wired into any Retrofit instance yet.

### External services

- **Firebase Auth (Phone)** — used for SMS OTP verification during login (`PhoneAuthHelper`)
- **Firebase Storage** — for trip evidence images (`FirebaseStorageRepository`)
- **Google Maps + Maps SDK** — used in `GpsScreen` for trip tracking via `OngoingTripRepository`

### Considerations
- Always reply in Spanish (code in English) even if input is in English.
- Do not use large explanatory comments unless requested.
