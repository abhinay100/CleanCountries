# CleanCountries

A simple Jetpack Compose Android app built with **Clean Architecture**.  
It demonstrates searching from a **hardcoded list of countries** with a **debounced search input** using Kotlin Flows and StateFlow.

---

## High-level overview

- Displays a hardcoded list of countries.
- Lets the user search the list using a text field.
- Search input is **debounced (300 ms)** so filtering only runs after the user pauses typing.
- Architecture: **Clean Architecture** (Domain → Data → Presentation → UI).
- UI: **Jetpack Compose**, ViewModel exposes a **StateFlow** that Compose observes.

---

## Project structure

com.example.cleancountries
├─ domain/
│ ├ Country.kt
│ ├ CountryRepository.kt
│ └ GetCountriesUseCase.kt
├─ data/
│ └ HardcodedCountryRepository.kt
├─ presentation/
│ ├ CountriesViewModel.kt
│ └ CountriesViewModelFactory.kt
├─ ui/
│ └ CountriesScreen.kt
└─ MainActivity.kt

yaml
Copy code

---

## Data layer — HardcodedCountryRepository

**Purpose:** concrete data source. Emits a `Flow<List<Country>>`.

- Implemented with `flow { emit(list) }`.
- Simple and synchronous here — in real apps use `flowOn(Dispatchers.IO)` if reading DB or network.
- Because repository returns a Flow, consumers can apply transformations/reactive operators easily.

> 💡 Tip: change to `flow { emit(list) }.flowOn(Dispatchers.IO)` if the emission becomes expensive.

---

## Domain layer

- **Country**: plain data class (code, name, phoneCode).
- **CountryRepository**: interface (contract) — domain layer depends on contract, not implementation.
- **GetCountriesUseCase**:
    - `getAllCountries()` returns `Flow<List<Country>>`.
    - `searchCountries(query)` returns a `Flow<List<Country>>` that maps repository output and filters by query.

**Why this separation?**
- Swap data source (Room/Retrofit) without changing ViewModel/UI.
- Easier unit testing: test use-case with fake repo.

---

## Presentation layer — CountriesViewModel

This is where the reactive & debounce behavior lives.

### Key fields:
```kotlin
private val _countries = MutableStateFlow<List<Country>>(emptyList())
val countries: StateFlow<List<Country>> = _countries.asStateFlow()

private val searchQuery = MutableStateFlow("")
Behavior:
Collect useCase.getAllCountries() once to populate initial _countries.

Listen to searchQuery and run:


searchQuery
  .debounce(300) // wait 300ms after last keystroke
  .distinctUntilChanged()
  .flatMapLatest { query ->
      if (query.isBlank()) useCase.getAllCountries()
      else useCase.searchCountries(query)
  }
  .collect { filtered -> _countries.value = filtered }
Why these operators?
debounce(300): avoid processing on each keystroke.

distinctUntilChanged(): skip same query twice.

flatMapLatest: cancel previous search if a new query arrives.

UI layer — CountriesScreen (Jetpack Compose)
OutlinedTextField with remember { mutableStateOf("") }.

On each onValueChange → updates searchQuery in ViewModel.

UI collects state via:


val countries by viewModel.countries.collectAsState()
LazyColumn displays list → recomposes when countries updates.

Wiring — MainActivity
Manual Dependency Injection:


val repository = HardcodedCountryRepository()
val useCase = GetCountriesUseCase(repository)
val factory = CountriesViewModelFactory(useCase)
val viewModel = ViewModelProvider(this, factory)[CountriesViewModel::class.java]

setContent { CountriesScreen(viewModel) }
For production: prefer Hilt / Koin.

Runtime sequence (step-by-step)
MainActivity creates viewModel and shows CountriesScreen.

ViewModel collects getAllCountries() → emits initial list → UI shows all countries.

User types into search field:

Updates Compose text state.

Calls viewModel.onSearchQueryChange(it).

searchQuery flow:

Debounced 300ms.

If query is blank → show all countries.

Else → filter via useCase.searchCountries(query).

Filtered list updates _countries.

Compose recomposes with updated list.

Edge cases & improvements
Loading / Error state: Wrap results in a UiState sealed class (Loading, Success, Error).

Performance: Use flowOn(Dispatchers.IO) for heavy work.

Error handling: .catch { e -> ... }.

Highlight search: Use AnnotatedString in Compose.

Persistence: Replace repo with Room/Retrofit without changing ViewModel/UI.

DI: Use Hilt in production.

Paging: Use Paging3 for large data sets.

Accessibility: Add content descriptions, proper sizing.

Testing strategy
Unit tests for UseCase: fake repo, verify filtering.

Unit tests for ViewModel: use kotlinx.coroutines.test and advanceTimeBy for debounce.

Compose UI tests: createComposeRule() + assert UI list.

Integration test: Espresso Compose interop.

Key interview talking points
Separation of concerns → each layer has single responsibility.

Testability → domain + ViewModel easily testable.

Reactive + efficient → debounce + flatMapLatest for responsiveness.

Compose friendly → collectAsState() integrates Flow with UI.

Scalable → swap repo, add DI, Paging, persistence with minimal change.

Quick code snippets
Debounce in ViewModel

searchQuery
  .debounce(300)
  .distinctUntilChanged()
  .flatMapLatest { query ->
      if (query.isBlank()) useCase.getAllCountries()
      else useCase.searchCountries(query)
  }
  .collect { _countries.value = it }
Compose collection

val countries by viewModel.countries.collectAsState()
LazyColumn {
    items(countries) { CountryRow(it) }
}
How to run locally
Open in Android Studio (Giraffe or later).

Ensure build.gradle includes Compose, Lifecycle, Coroutines dependencies.

Run on emulator or device.

