package com.abhinay.cleancountries.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.abhinay.cleancountries.data.HardcodedCountryRepository
import com.abhinay.cleancountries.domain.GetCountriesUseCase
import com.abhinay.cleancountries.presentation.CountriesViewModel
import com.abhinay.cleancountries.presentation.CountriesViewModelFactory
import com.abhinay.cleancountries.ui.theme.CleanCountriesTheme

class MainActivity : ComponentActivity() {
    private lateinit var countriesViewModel: CountriesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manual wiring (Data -> Domain -> Presentation)
        val repository = HardcodedCountryRepository()     // data layer
        val useCase = GetCountriesUseCase(repository)     // domain layer
        val factory = CountriesViewModelFactory(useCase)  // presentation layer

        countriesViewModel = factory.create(CountriesViewModel::class.java)


        enableEdgeToEdge()
        setContent {
            CleanCountriesTheme {
                Surface {
                    CountriesScreen(countriesViewModel)
                }
            }
        }
    }
}

