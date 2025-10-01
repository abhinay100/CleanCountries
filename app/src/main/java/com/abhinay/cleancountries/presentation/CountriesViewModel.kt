package com.abhinay.cleancountries.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abhinay.cleancountries.domain.Country
import com.abhinay.cleancountries.domain.GetCountriesUseCase
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 * Created by Abhinay on 01/10/25.
 */
class CountriesViewModel(
    private val getCountriesUseCase: GetCountriesUseCase
) : ViewModel() {

    // UI visible state
    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>> = _countries.asStateFlow()

    // search query flow (debounced)
    private val searchQuery = MutableStateFlow("")

    init {
        // 1) Load initial all countries once (fallback)
        viewModelScope.launch {
            getCountriesUseCase.getAllCountries().collect { list ->
                _countries.value = list

            }
        }
        // 2) React to searchQuery with debounce -> update countries list
        @OptIn(FlowPreview::class)
        viewModelScope.launch {
            searchQuery
                .debounce(300)   // wait 300ms after user stops typing
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    getCountriesUseCase.searchCountries(query)  // returns Flow<List<Country>>
                }
                .collect { filtered ->
                    _countries.value = filtered
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }


}