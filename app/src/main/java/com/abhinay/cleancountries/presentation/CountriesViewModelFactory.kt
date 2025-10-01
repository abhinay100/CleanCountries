package com.abhinay.cleancountries.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abhinay.cleancountries.domain.GetCountriesUseCase

/**
 * Created by Abhinay on 01/10/25.
 */
class CountriesViewModelFactory(
    private val useCase: GetCountriesUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountriesViewModel::class.java)) {
            return CountriesViewModel(useCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}