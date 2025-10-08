package com.abhinay.cleancountries.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhinay.cleancountries.domain.Country
import com.abhinay.cleancountries.domain.GetCountriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Abhinay on 01/10/25.
 */
@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val useCase: GetCountriesUseCase
) : ViewModel() {


    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _uiState = MutableStateFlow<CountryUiState>(CountryUiState.Idle)
    val uiState: StateFlow<CountryUiState> = _uiState.asStateFlow()


    init {

        // 2) React to searchQuery with debounce -> update countries list
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            _query
                .debounce(300)   // wait 300ms after user stops typing
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    // get a Flow<List<Country>> from useCase, then map to UiState Flow
                    val source : Flow<List<Country>> =
                        if (query.isBlank()) useCase.getAllCountries()
                        else useCase.searchCountries(query)

                    source
                        .map<List<Country>, CountryUiState> { list ->
                              CountryUiState.Success(list)
                        }
                        .onStart {
                            // Emit Loading before the first item
                            emit(CountryUiState.Loading)
                        }
                        .catch { e ->
                            emit(CountryUiState.Error(e.message ?: "Unknown error"))
                        }

                }
                .collect { state ->
                    _uiState.value = state
                }

        }
    }

    fun onSearchQueryChange(query: String) {
        _query.value = query
    }


}