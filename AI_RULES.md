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
* `data/` — Repository interfaces
* `model/` — Data models
* `presentation/` — Shared ViewModel

Rules:

* MUST be platform-independent
* MUST NOT contain Android SDK, iOS, or platform-specific APIs
* MUST contain shared ViewModel

### androidApp / iosApp / webApp

* UI layer ONLY
* Bind ViewModel to platform UI
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
| UI | androidApp / iosApp | Render State, send Intent |
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

> ⚠️ Screen composable receives ONLY State and Intent lambda.

MUST:

* Screen composable signature:
```kotlin
@Composable
fun FeatureScreen(
    state: FeatureState,
    onIntent: (FeatureIntent) -> Unit
)
```

* Collect StateFlow at screen entry point only:
```kotlin
val state by viewModel.state.collectAsState()
```

* Use `collectAsState()` in shared/common code
* Android platform layer MAY use `collectAsStateWithLifecycle()` as wrapper

FORBIDDEN:

* `viewModel()` call inside child composables
* Passing ViewModel down the composable tree
* Business logic inside composable body
* Side effects outside `LaunchedEffect`

---

## 11. Dependency Injection (STRICT)

* Use **Koin** for all DI (KMP compatible)

Rules:

* DO NOT instantiate objects directly (`val x = MyClass()` ❌)
* Define DI modules in `commonMain`
* Platform entry point initializes Koin

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

---

## 13. Naming Convention

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

## 14. Code Generation Rules

When generating code, ALWAYS:

1. Show full folder structure first
2. Generate file-by-file (one file at a time)
3. Include full imports in every file
4. Respect module boundaries strictly
5. Keep production-level quality — no toy code

---

## 15. Feature Checklist

Every feature MUST include all of the following:

- [ ] `FeatureState.kt` — immutable data class
- [ ] `FeatureIntent.kt` — sealed class
- [ ] `FeatureResult.kt` — sealed class
- [ ] `FeatureReducer.kt` — pure function
- [ ] `FeatureViewModel.kt` — in commonMain
- [ ] `FeatureScreen.kt` — Compose UI (androidApp)
- [ ] DI module registration

---

## 16. Instruction for AI (CRITICAL)

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