package com.abhinay.cleancountries.domain

import kotlinx.coroutines.flow.Flow

/**
   * Created by Abhinay on 30/09/25.
  */
interface CountryRepository {
  fun getCountries(): Flow<List<Country>>
}