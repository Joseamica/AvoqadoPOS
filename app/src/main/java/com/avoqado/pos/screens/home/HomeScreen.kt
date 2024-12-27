package com.avoqado.pos.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel
) {
    val tables by homeViewModel.tables.collectAsStateWithLifecycle()
    val venues by homeViewModel.venues.collectAsStateWithLifecycle()
    val selectedVenue by homeViewModel.selectedVenue.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        VenuesDropdownMenu(
            items = venues.map { venue -> venue.name ?: "" },
            selectedItem = selectedVenue?.name ?: "",
            onItemSelected = { index -> homeViewModel.setSelectedVenue(index)}
        )

        Row {
            // “Confirm” button
            Button(
                onClick = {
                    homeViewModel.fetchTables()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Refrescar",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        LazyColumn {
            items(selectedVenue?.tables ?: emptyList()) { table ->
                Card(
                    modifier = Modifier.clickable {
                        table?.let {
                            homeViewModel.onTableSelected(it)
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = "${table?.venueId} - ${table?.tableNumber}"
                        )

                        Icon(
                            painterResource(R.drawable.baseline_arrow_forward_ios_24),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenuesDropdownMenu(
    modifier: Modifier = Modifier,
    items: List<String>,
    label: String = "Select an Item",
    selectedItem: String,
    onItemSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedItem,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor() // Attach the ExposedDropdownMenu to this anchor
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEachIndexed { index,item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(index)
                        expanded = false
                    },
                    text = { Text(item) }
                )
            }
        }
    }
}