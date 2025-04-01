package com.avoqado.pos.features.management.presentation.tables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.core.data.network.models.NetworkTable
import com.avoqado.pos.core.data.network.models.NetworkVenue
import com.avoqado.pos.core.presentation.components.DropdownMenuMoreActions
import com.avoqado.pos.core.presentation.components.TopMenuContent
import com.avoqado.pos.core.presentation.theme.AppFont
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview

@Composable
fun TablesScreen(
    tablesV: TablesViewModel
) {
    val tables by tablesV.tables.collectAsStateWithLifecycle()
    val venues by tablesV.venues.collectAsStateWithLifecycle()
    val selectedVenue by tablesV.selectedVenue.collectAsStateWithLifecycle()

    HomeContent(
        onTableSelected = tablesV::onTableSelected,
        onVenueSelected = tablesV::setSelectedVenue,
        onShowSettings = tablesV::toggleSettingsModal,
        venues = venues,
        selectedVenue = selectedVenue,
        onBackAction = tablesV::onBackAction,
        onLogout = tablesV::logout
    )
}


@Composable
fun HomeContent(
    onBackAction: () -> Unit = {},
    onVenueSelected: (Int) -> Unit = {},
    onTableSelected: (NetworkTable) -> Unit = {},
    onShowSettings: (Boolean) -> Unit = {},
    onLogout: () -> Unit = {},
    venues: List<NetworkVenue>,
    selectedVenue: NetworkVenue?
) {
    TopMenuContent(
        onBackAction = onBackAction,
        onOpenSettings = { onShowSettings(true) },
        onDismissRequest = { onShowSettings(false) },
        onLogout = onLogout
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            selectedVenue?.name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = AppFont.EffraFamily
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (venues.size > 1) {
                VenuesDropdownMenu(
                    items = venues.map { venue -> venue.name ?: "" },
                    selectedItem = selectedVenue?.name ?: "",
                    onItemSelected = { index -> onVenueSelected(index) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(5), // Tres columnas
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    selectedVenue?.tables
                        ?.filterNotNull()      // Quita cualquier elemento nulo
                        ?.filter { it.bill != null && it.bill.status == "OPEN" } // Filtra mesas con bill en estado "OPEN"
                        ?: emptyList()
                ) { table ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier
                            .size(50.dp) // Tamaño fijo para que sean recuadros uniformes
                            .clickable {
                                onTableSelected(table)
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "${table.tableNumber}",
                                style = MaterialTheme.typography.bodyLarge, // Parámetro nombrado 'style'
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
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
                .menuAnchor()
                .clickable { expanded = !expanded }
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEachIndexed { index, item ->
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

@Urovo9100DevicePreview
@Composable
fun HomeContentPreview() {
    AvoqadoTheme {
        HomeContent(
            venues = emptyList(),
            selectedVenue = NetworkVenue(
                address = null,
                askNameOrdering = null,
                chainId = null,
                city = null,
                colorsId = null,
                configurationId = null,
                country = null,
                createdAt = null,
                cuisine = null,
                dynamicMenu = null,
                email = null,
                googleBusinessId = null,
                id = null,
                image = null,
                instagram = null,
                language = null,
                logo = null,
                name = "Dona Simona",
                paymentMethods = null,
                phone = null,
                posName = null,
                specialPayment = null,
                specialPaymentRef = null,
                stripeAccountId = null,
                tables = listOf(
                    NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ),
                    NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ),
                    NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ),
                    NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ),
                    NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ),
                    NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ),
                    NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ),
                    NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ),
                    NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ), NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ), NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ), NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ), NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ), NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    ), NetworkTable(
                        bill = null,
                        billId = null,
                        count = null,
                        createdAt = null,
                        demo = null,
                        floorId = null,
                        locationId = null,
                        seats = null,
                        status = null,
                        tableNumber = 1,
                        updatedAt = null,
                        venueId = null
                    )
                ),
                tipPercentage1 = null,
                tipPercentage2 = null,
                tipPercentage3 = null,
                tipPercentages = null,
                type = null,
                updatedAt = null,
                utc = null,
                website = null,
                wifiName = null,
                wifiPassword = null,
                waiters = emptyList()
            ),
        )
    }
}
