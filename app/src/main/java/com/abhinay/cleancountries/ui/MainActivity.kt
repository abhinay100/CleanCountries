package com.abhinay.cleancountries.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import com.abhinay.cleancountries.presentation.CountriesViewModel
import com.abhinay.cleancountries.ui.theme.CleanCountriesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val  countriesViewModel : CountriesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

