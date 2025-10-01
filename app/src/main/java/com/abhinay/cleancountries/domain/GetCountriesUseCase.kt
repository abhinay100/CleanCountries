package com.abhinay.cleancountries.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Abhinay on 30/09/25.
 */
class GetCountriesUseCase(private val repository: CountryRepository) {

    fun getAllCountries(): Flow<List<Country>> = repository.getCountries()

    fun searchCountries(query: String): Flow<List<Country>> = repository.getCountries()
        .map { list ->
            if (query.isBlank()) list
            else list.filter { it.name.contains(query, ignoreCase = true) }
        }

}