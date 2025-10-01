##Initial Commit
1) High-level overview — what the app does

Displays a hardcoded list of countries.

Lets the user search the list using a text field.

Search input is debounced (300 ms) so filtering only runs after the user pauses typing.

Architecture: Clean Architecture (Domain → Data → Presentation → UI).

UI: Jetpack Compose, ViewModel exposes a StateFlow that Compose observes.

2) Project structure (quick reminder)
   com.example.cleancountries
   ├─ domain/
   │   ├ Country.kt
   │   ├ CountryRepository.kt
   │   └ GetCountriesUseCase.kt
   ├─ data/
   │   └ HardcodedCountryRepository.kt
   ├─ presentation/
   │   ├ CountriesViewModel.kt
   │   └ CountriesViewModelFactory.kt
   ├─ ui/
   │   └ CountriesScreen.kt
   └─ MainActivity.kt

3) Data layer — HardcodedCountryRepository

Purpose: concrete data source. Emits a Flow<List<Country>>.

Key points:

Implemented with flow { emit(list) }.

Simple and synchronous here — in real apps use flowOn(Dispatchers.IO) if reading DB or network.

Because repository returns a Flow, consumers can apply transformations/reactive operators easily.

Tip: change to flow { emit(list) }.flowOn(Dispatchers.IO) if the emission becomes expensive.

4) Domain layer — Country, CountryRepository, GetCountriesUseCase

Country: plain data class (code, name, phoneCode).

CountryRepository: interface (contract) — domain layer depends on contract, not implementation.

GetCountriesUseCase:

getAllCountries() returns Flow<List<Country>>.

searchCountries(query) returns a Flow<List<Country>> that maps repository output and filters by query.

Using map keeps filtering logic inside the domain/use-case layer (business rule), so presentation/UI stays clean.

Why this separation?

Swap data source (Room/Retrofit) without changing ViewModel/UI.

Easier unit testing: test use-case with fake repo.

5) Presentation layer — CountriesViewModel

This is where the reactive & debounce behavior lives.

Important fields:

private val _countries = MutableStateFlow<List<Country>>(emptyList())
val countries: StateFlow<List<Country>> = _countries.asStateFlow()

private val searchQuery = MutableStateFlow("")


Startup behavior:

Collect useCase.getAllCountries() once to populate initial _countries (fallback so UI shows content before any search).

Start a coroutine that listens to searchQuery and runs:

searchQuery
.debounce(300)                // wait 300ms after last keystroke
.distinctUntilChanged()
.flatMapLatest { query ->
if (query.isBlank()) useCase.getAllCountries()
else useCase.searchCountries(query)
}
.collect { filtered -> _countries.value = filtered }


Why these operators?

debounce(300): avoid processing on each keystroke.

distinctUntilChanged(): skip same query twice.

flatMapLatest: if a new query arrives while a previous search is still running, cancel the previous and switch to the new — great for responsiveness.

Notes:

Work runs in viewModelScope (UI-safe).

If repository work is expensive, add flowOn(Dispatchers.IO) inside repo or use withContext in use-case.

6) UI layer — CountriesScreen (Jetpack Compose)

Key parts:

OutlinedTextField with local text state remember { mutableStateOf("") }.

On each onValueChange, we:

text = it
viewModel.onSearchQueryChange(it)


This updates searchQuery MutableStateFlow in ViewModel.

UI collects viewModel.countries.collectAsState():

val countries by viewModel.countries.collectAsState()


LazyColumn displays countries list. When _countries updates, Compose recomposes the list.

Important Compose behavior:

collectAsState() bridges coroutines/flows to Compose. Updates are observed on the main thread and trigger recomposition.

If you want the text field to survive process death/rotation, use rememberSaveable.

7) Wiring — MainActivity

Manual Dependency Injection:

val repository = HardcodedCountryRepository()
val useCase = GetCountriesUseCase(repository)
val factory = CountriesViewModelFactory(useCase)
viewModel = ViewModelProvider(this, factory)[CountriesViewModel::class.java]
setContent { CountriesScreen(viewModel) }


Why manual DI?

Keeps sample simple and explicit for interviews. For production, use Hilt / Koin and constructor injection.

8) Runtime sequence (step-by-step flow)

App launches, MainActivity constructs viewModel and Compose shows CountriesScreen.

ViewModel collects getAllCountries() and emits initial country list → UI shows full list.

User types into OutlinedTextField. Each keystroke:

Updates text in Compose.

Calls viewModel.onSearchQueryChange(it) → updates searchQuery MutableStateFlow.

searchQuery flow:

Waits 300ms after last keystroke (debounce).

If query is unchanged or blank, handled by distinctUntilChanged() or fallback to full list.

flatMapLatest triggers useCase.searchCountries(query) which filters the original list and emits filtered list.

ViewModel receives filtered list and sets _countries.value.

Compose collectAsState() sees new countries value → recomposes LazyColumn to show filtered items.

9) Edge cases & improvements (practical)

Loading / Error state: wrap with sealed class UiState { object Loading; data class Success(...); data class Error(...) }. Expose StateFlow<UiState> instead of plain list.

flowOn(Dispatchers.IO): if filtering or data fetch is heavy, apply flowOn.

Exception handling: use .catch { e -> _uiState.value = UiState.Error(e) }.

Highlight search match: use AnnotatedString to color/bold matching substring in Compose.

Persistence: Replace HardcodedCountryRepository with Room or retrofit-backed repo — no change to ViewModel/UI.

DI: Integrate Hilt for cleaner wiring in production.

Paging: if dataset large, use Paging3 with Compose.

Accessibility: content descriptions, proper font sizes and contrast.

10) Testing strategy

Unit tests for UseCase: give it a fake repo and assert searchCountries yields expected filtered lists.

Unit tests for ViewModel:

Use kotlinx.coroutines.test (runTest, TestDispatcher) to verify debounce/flatMapLatest behavior. You can advance time to test debounce.

Assert _countries (via collectLatest or StateFlow snapshot) updates correctly.

Compose UI tests:

Use createComposeRule() to set CountriesScreen(viewModel = testViewModel) and assert UI shows expected list when ViewModel emits data.

Integration test (optional): run app and assert UI behavior with Espresso Compose interop.

11) Key interview talking points (how to explain succinctly)

Separation of concerns: UI -> ViewModel -> UseCase -> Repository. Each layer has single responsibility.

Testability: Domain + ViewModel testable without Android framework; repository can be swapped with fakes.

Reactive + efficient: StateFlow for state + Flow operators (debounce, flatMapLatest) for responsive search UX.

Compose friendly: Compose reactivity via collectAsState().

Scalable path: Replace hardcoded repo with Room/Retrofit, add Hilt — minimal change.

12) Quick code snippets to remember (copyable)

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
LazyColumn { items(countries) { CountryRow(it) } }

13) How to run locally

Open the project in Android Studio (Giraffe or later recommended for latest Compose tooling).

Make sure module build.gradle has Compose, lifecycle, coroutines dependencies.

Run on emulator / device.
