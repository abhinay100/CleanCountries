package com.abhinay.cleancountries.data

import com.abhinay.cleancountries.domain.Country
import com.abhinay.cleancountries.domain.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by Abhinay on 30/09/25.
 */
class HardcodedCountryRepository : CountryRepository {
    override fun getCountries(): Flow<List<Country>> = flow {
        val countries = listOf(
            Country("IN", "India", "+91"),
            Country("US", "United States", "+1"),
            Country("CA", "Canada", "+1"),
            Country("GB", "United Kingdom", "+44"),
            Country("AU", "Australia", "+61"),
            Country("DE", "Germany", "+49"),
            Country("FR", "France", "+33"),
            Country("JP", "Japan", "+81"),
            Country("CN", "China", "+86"),
            Country("BR", "Brazil", "+55")
        )
      emit(countries)
    }
}