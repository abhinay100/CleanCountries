package com.abhinay.cleancountries.di

import com.abhinay.cleancountries.data.HardcodedCountryRepository
import com.abhinay.cleancountries.domain.CountryRepository
import com.abhinay.cleancountries.domain.GetCountriesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Abhinay on 08/10/25.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCountryRepository(): CountryRepository {
         return HardcodedCountryRepository()
    }

    @Provides
    @Singleton
    fun provideGetCountriesUseCase(repository: CountryRepository): GetCountriesUseCase {
         return GetCountriesUseCase(repository)
    }


}