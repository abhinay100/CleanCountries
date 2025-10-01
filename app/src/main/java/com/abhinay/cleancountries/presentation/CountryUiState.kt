package com.abhinay.cleancountries.presentation

import com.abhinay.cleancountries.domain.Country

/**
 * Created by Abhinay on 02/10/25.
 */
sealed class CountryUiState {
    object Idle: CountryUiState()
    object Loading: CountryUiState()
    data class Success(val countries: List<Country>): CountryUiState()
    data class Error(val message: String): CountryUiState()
}