package com.abhinay.cleancountries.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abhinay.cleancountries.domain.Country
import com.abhinay.cleancountries.presentation.CountriesViewModel

/**
 * Created by Abhinay on 01/10/25.
 */
@Composable
fun CountriesScreen(viewModel: CountriesViewModel) {
    val countries by viewModel.countries.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        var text by remember { mutableStateOf("") }

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                viewModel.onSearchQueryChange(it) // update query; ViewModel debounces + filters
            },
            label = { Text("Search Country") },
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(12.dp))
        if (countries.isEmpty()) {
            Text("No results", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(countries) { country ->
                    CountryRow(country)
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

                }

            }
        }

    }

}

@Composable
private fun CountryRow(country: Country) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)) {
        Text(text = country.name, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "${country.code} • ${country.phoneCode}", style = MaterialTheme.typography.bodySmall)
    }
}


