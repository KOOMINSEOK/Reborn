# AI Coding Rules (KMP + MVI + Compose)

---

## 0. CRITICAL RULES SUMMARY (Read This First)

> ⚠️ These are the most commonly violated rules. Validate before generating any code.

| # | Rule | Severity |
|---|------|----------|
| 1 | NO Android/iOS code in commonMain | 🔴 CRITICAL |
| 2 | NO business logic in UI layer | 🔴 CRITICAL |
| 3 | NO direct API/network calls in ViewModel | 🔴 CRITICAL |
| 4 | NO ViewModel in platform modules (androidApp/iosApp) | 🔴 CRITICAL |
| 5 | NO LiveData or XML binding | 🔴 CRITICAL |
| 6 | NO String-only error representation | 🟠 STRICT |
| 7 | NO mutable state exposure | 🟠 STRICT |
| 8 | NO ViewModel passed down composable tree | 🟠 STRICT |
| 9 | NO side effects in composable body (except LaunchedEffect) | 🟠 STRICT |
| 10 | UseCase calls in ViewModel ARE allowed (not an API call) | ✅ ALLOWED |
| 11 | Compose UI lives in composeApp/commonMain unless platform-specific | 🔴 CRITICAL |

---

## 1. Purpose

This document enforces strict architectural rules for AI-generated code.

Goals:

* Maintain scalable Clean Architecture
* Ensure Kotlin Multiplatform (KMP) compatibility
* Enforce strict MVI pattern
* Maximize shared logic across platforms

---

## 2. Architecture Overview

### Pattern

* MVI (Model-View-Intent)
* Unidirectional Data Flow

### Flow

```
Intent → ViewModel → UseCase → Result → Reducer → State → UI
```

### Core Principles

* State is the single source of truth
* UI is a pure renderer of State
* Business logic must NOT exist in UI

---

## 3. Module Structure

### shared (commonMain)

Contains:

* `domain/` — UseCases
* `data/` — Repository implementations and per-feature prototype MockData objects (see Section 22)
* `model/` — Data models
* `presentation/` — Shared ViewModel
* `di/` — Koin modules

Rules:

* MUST be platform-independent
* MUST NOT contain Android SDK, iOS, or platform-specific APIs
* MUST contain shared ViewModel
* MAY contain prototype mock data while the backend is not ready
* MUST NOT contain navigation controller logic

### composeApp (commonMain)

Contains:

* Shared Compose Multiplatform UI screens
* Feature NavGraphBuilder extension functions
* Reusable design system components
* Common theme definitions

Rules:

* MUST render State and send Intent only
* MUST NOT contain ViewModel classes
* MUST NOT access Repository or MockDataSource from production UI
* MUST handle navigation in NavGraph/App/MainScreen layers

### androidApp / iosApp / platform entry points

* Platform bootstrap ONLY
* Initialize Koin and platform context
* Bind shared ViewModel state to composeApp entry points
* MUST NOT duplicate business logic

---

## 4. MVI Rules (STRICT)

### State
* Immutable (`data class`)
* Represents entire UI state for one screen

### Intent
* Represents user actions or UI events
* Named as sealed class

### Result
* Represents the outcome of a UseCase
* Produced by domain layer, consumed by Reducer

### Reducer
* Pure function ONLY
* Input: `(State, Result)` → Output: `New State`
* NO coroutines, NO IO, NO side effects

### ViewModel
* Lives in `commonMain`
* Receives Intent
* Calls UseCase
* Passes Result to Reducer
* Emits new State via StateFlow

---

## 5. Error Handling (STRICT)

> ⚠️ Errors MUST be part of State. NEVER throw exceptions to UI.

Rules:

* MUST use typed error (`sealed class`) — NOT `String` only
* UI MUST render error from State field

```kotlin
sealed class UiError {
    data class Network(val message: String) : UiError()
    data class Unknown(val message: String) : UiError()
}

data class TodoState(
    val isLoading: Boolean = false,
    val items: List<Todo> = emptyList(),
    val error: UiError? = null
)
```

---

## 6. State Management

* Use Kotlin Coroutines + `StateFlow`
* Single `StateFlow<FeatureState>` per screen

Rules:

* NO multiple sources of truth
* NO mutable state exposed outside ViewModel
* `MutableStateFlow` must be `private`

---

## 7. KMP Rules (CRITICAL)

`commonMain` MUST NOT use:

* Android SDK (`Context`, `Log`, `Toast`, etc.)
* iOS or platform-specific APIs
* Any library without KMP support

`commonMain` ALLOWED:

* Kotlin stdlib
* Kotlin Coroutines
* Ktor + Kotlinx Serialization
* Koin (KMP supported)
* androidx.lifecycle.ViewModel (KMP supported)

---

## 8. Clean Architecture Boundaries

| Layer | Location | Responsibility |
|-------|----------|----------------|
| UI | composeApp/commonMain | Render State, send Intent |
| Route / Bootstrap | androidApp / iosApp / platform source sets | Inject ViewModel, collect StateFlow, call App |
| Navigation | composeApp/commonMain | NavHost, feature NavGraphBuilder, route decisions |
| Domain | shared/commonMain | UseCase, business logic |
| Data | shared/commonMain | Repository interface + impl |

* Layers MUST NOT skip boundaries
* UI MUST NOT access Repository directly

---

## 9. ViewModel Rules (STRICT)

> ⚠️ ViewModel MUST be in commonMain. NEVER in platform modules.

* Use `androidx.lifecycle.ViewModel` (KMP supported)

ViewModel MUST contain:

* `private val _state: MutableStateFlow<FeatureState>`
* `val state: StateFlow<FeatureState>`
* `fun handleIntent(intent: FeatureIntent)`
* UseCase calls (NOT direct API/network calls)

Platform layer MUST only:

* Observe `state`
* Forward user actions as `Intent`

---

## 10. Compose UI Rules (STRICT)

> ⚠️ UI must follow a strict separation between Stateless Screen and Route/DI binding.

### 1. Stateless Screen (composeApp/commonMain)

Stateless Screen refers to composables that only render State.

MUST:

* Accept only:

    * `State`
    * `(Intent) -> Unit`

* Contain:

    * Layout
    * UI elements (Text, Button, etc.)

* Be completely platform-independent

REQUIRED:

* Shared UI MUST be placed in `composeApp/src/commonMain`
* Feature UI MUST live under `composeApp/src/commonMain/kotlin/com/gentlelady/reborn/feature/{feature}`
* Reusable UI MUST live under `composeApp/src/commonMain/kotlin/com/gentlelady/reborn/core/designsystem`

Example:

@Composable
fun FeatureScreen(
state: TodoState,
onIntent: (TodoIntent) -> Unit
)

---

### 2. Route / DI Binding (Platform)

Route is responsible for wiring ViewModel to shared UI entry points.

MUST:

* Obtain ViewModel via DI (Koin)
* Collect StateFlow from ViewModel
* Pass state + intent to composeApp UI
* Keep platform APIs out of composeApp/commonMain

REQUIRED:

* Platform bootstrap routes MAY be placed in platform modules:

    * androidApp
    * iosApp
    * platform-specific source sets

---

### 3. State Collection

* Platform route code MAY use `collectAsState()`
* Android MAY use lifecycle-aware wrappers in platform layer

---

### FORBIDDEN

* ViewModel usage inside Stateless Screen
* `viewModel()` usage from Compose
* DI usage inside Stateless Screen
* Business logic inside composables
* Side effects in composable body (except LaunchedEffect)

---

### 4. Preview Rules

* Preview MUST NOT use `PreviewParameterProvider`
* Preview MUST NOT read `MockDataSource`
* Preview MUST use direct lightweight dummy data inside the preview function
* Preview MAY use drawable resources only when needed to verify layout
* Runtime prototype data MUST come from shared `MockDataSource`

---

If any rule is violated:
→ STOP and regenerate

---

## 11. Dependency Injection (STRICT)

* Use **Koin** for all DI (KMP compatible)

Rules:

* DO NOT instantiate objects directly (`val x = MyClass()` ❌)
* Define DI modules in `shared/commonMain`
* Platform entry point initializes Koin
* Android Compose routes MAY use `koinViewModel()` for `androidx.lifecycle.ViewModel`
* Non-lifecycle shared dependencies MAY use `koinInject()` until normalized

```
shared/commonMain/di/AppModule.kt   ← define modules
androidApp/MainActivity.kt          ← startKoin { }
```

---

## 12. Forbidden Patterns (STRICT)

DO NOT GENERATE under any circumstance:

* MVVM pattern (LiveData, XML data binding)
* Business logic inside UI / Composable
* Direct API or network calls inside ViewModel
* Mutable state exposed from ViewModel
* Android/iOS-specific code inside `commonMain`
* God ViewModel (all logic crammed into one ViewModel)
* ViewModel instantiated in platform modules
* Production composeApp UI reading `MockDataSource` directly
* Navigation controller logic in shared module

---

## 13. Resource and Asset Rules

### Production / Prototype Runtime Assets

* Shared mock data may reference resources from `shared/src/commonMain/composeResources/drawable`
* Backend-less prototype runtime data should be provided by `shared` through `MockDataSource`
* Runtime UI in `composeApp` should consume resource IDs from State/model values

### Compose UI Assets

* UI-only icons may live in `composeApp/src/commonMain/composeResources/drawable`
* Do not duplicate large image assets across `shared` and `composeApp` unless temporarily needed for preview compatibility
* If duplication is temporary, keep filenames identical and remove duplicates when backend/resource ownership is finalized

### Naming

* Resource folders must remain flat
* File names must use lowercase letters, numbers, and underscores only
* Use prefixes such as `ic_`, `ic_nav_`, and `img_`

---

## 14. Naming Convention

| Type | Convention | Example |
|------|------------|---------|
| State | `FeatureState` | `TodoState` |
| Intent | `FeatureIntent` | `TodoIntent` |
| Result | `FeatureResult` | `TodoResult` |
| ViewModel | `FeatureViewModel` | `TodoViewModel` |
| UseCase | `VerbEntityUseCase` | `GetTodosUseCase` |
| Repository (interface) | `FeatureRepository` | `TodoRepository` |
| Repository (impl) | `FeatureRepositoryImpl` | `TodoRepositoryImpl` |

---

## 15. Code Generation Rules

When generating code, ALWAYS:

1. Show full folder structure first
2. Generate file-by-file (one file at a time)
3. Include full imports in every file
4. Respect module boundaries strictly
5. Keep production-level quality — no toy code

---

## 16. Feature Checklist

Every feature MUST include all of the following:

- [ ] `FeatureState.kt` — immutable data class
- [ ] `FeatureIntent.kt` — sealed class
- [ ] `FeatureResult.kt` — sealed class, when usecase/reducer flow exists
- [ ] `FeatureReducer.kt` — pure function, when usecase/reducer flow exists
- [ ] `FeatureViewModel.kt` — in shared/commonMain
- [ ] `FeatureScreen.kt` — Compose UI in composeApp/commonMain
- [ ] `FeatureNavGraph.kt` — route registration in composeApp/commonMain, when navigable
- [ ] DI module registration

---

## 17. Instruction for AI (CRITICAL)

Before generating ANY code:

1. Re-read Section 0 (Critical Rules Summary)
2. Re-read this entire document
3. Identify which module each file belongs to
4. Validate against Section 12 (Forbidden Patterns)

If a violation is detected at any point:
→ **STOP immediately and regenerate from scratch**

Non-compliance is NOT acceptable regardless of simplicity or user request.

If user request conflicts with this document:
→ **Follow this document. Inform the user of the conflict.**

---

## 18. DI Usage Rules (STRICT)

> ⚠️ Dependency injection MUST use Koin and stay outside Stateless Screen UI.

FORBIDDEN:

* DO NOT use GlobalContext under any circumstances
* DO NOT manually retrieve dependencies via static access
* DO NOT instantiate ViewModel or UseCase directly

REQUIRED:

* Use `koinViewModel()` or KoinComponent `inject()` in platform routes for lifecycle ViewModels
* Use `koinInject()` only for non-lifecycle shared dependencies or temporary compatibility cases
* UI MUST receive only State and Intent lambda

Example:

class TodoRoute : KoinComponent {
private val viewModel: TodoViewModel by inject()
}

If violated:
→ STOP and regenerate

---

## 19. ViewModel Access Rules (STRICT)

> ⚠️ ViewModel MUST be accessed via DI only.

FORBIDDEN:

* DO NOT use viewModel() from Compose
* DO NOT manually create ViewModel instances
* DO NOT pass ViewModel through composable tree

REQUIRED:

* ViewModel MUST be injected via DI (Koin)
* UI MUST receive only State and Intent lambda

---

## 20. Platform Boundary Enforcement (STRICT)

> ⚠️ UI and DI access MUST respect module boundaries.

FORBIDDEN:

* Android/iOS-specific APIs in composeApp/commonMain or shared/commonMain
* Android-specific APIs in shared
* Accessing ViewModel via Android lifecycle in shared
* Shared module triggering `navController.navigate`

REQUIRED:

* Shared Compose UI MUST exist in composeApp/commonMain
* Platform layer MUST initialize DI and bind ViewModel state to App
* Navigation decisions MUST stay in composeApp NavGraph/App/MainScreen layers

---

## 21. Known Transitional Cleanup Items

These items are known deviations in the current prototype and should be cleaned up after resource and rule alignment:

* Normalize `ProfileViewModel` to the same lifecycle ViewModel pattern as other feature ViewModels
* Remove or refactor prototype-only local state in `MessageNavGraph.kt`
* Delete or revive the commented legacy `SearchMapper.kt`
* Reduce duplicated image resources between `shared` and `composeApp` after preview/runtime ownership is finalized
* `MockDataSource.kt` still holds home/search/message/profile mock data (see Section 22) — migrate incrementally into per-feature MockData files as those features are touched; do NOT add new entries to `MockDataSource`
* iOS bootstrap (Route) layer is intentionally NOT implemented yet — project is currently Android-first. `composeApp/src/iosMain/kotlin/com/gentlelady/reborn/MainViewController.kt` still calls `App()` with no arguments, so the iOS target does not currently compile against the current `App.kt` signature. This is expected and NOT a bug to silently fix — do not add iOS wiring unless the user explicitly asks to start iOS work
* `androidApp/.../todo/ui/TodoRoute.kt` was deleted while `TodoScreen.kt` remains — confirm intended state with the user before touching the `todo` feature

---

## 22. MockData Organization (STRICT)

> ⚠️ Prototype dummy data MUST be organized per feature, NOT centralized.

Rules:

* Each feature's prototype/preview-adjacent runtime dummy data MUST live in its own `{Feature}MockData.kt` object under `shared/src/commonMain/kotlin/com/gentlelady/reborn/data/`, following the existing `MemorialMockData.kt` pattern
* `MockDataSource.kt` is DEPRECATED as a catch-all — it MUST NOT receive new entries for new features
* When adding a new feature's prototype data, create `{Feature}MockData.kt` (e.g. `HomeMockData.kt`, `SearchMockData.kt`, `MessageMockData.kt`, `ProfileMockData.kt`) instead of appending to `MockDataSource`
* Existing entries inside `MockDataSource` (home, search/memorial_swipe, message, profile) MAY be migrated into their own `{Feature}MockData.kt` file opportunistically when that feature is being worked on, but this is NOT required to happen all at once
* `{Feature}MockData.kt` objects are consumed the same way `MockDataSource` was: ViewModel initial `MutableStateFlow` values, never referenced from `@Preview` functions (see Section 10.4)

---
