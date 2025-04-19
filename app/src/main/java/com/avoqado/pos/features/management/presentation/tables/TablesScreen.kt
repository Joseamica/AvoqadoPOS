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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
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
    val selectedVenue = tablesV.venueInfo
    val showSettings by tablesV.showSettings.collectAsStateWithLifecycle()
    val isLoading by tablesV.isLoading.collectAsStateWithLifecycle()
    val shiftStarted by tablesV.shiftStarted.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        tablesV.startListeningForVenueUpdates()
    }

    DisposableEffect(Unit) {
        onDispose {
            tablesV.stopListeningForVenueUpdates()
        }
    }

    HomeContent(
        onTableSelected = tablesV::onTableSelected,
        onShowSettings = tablesV::toggleSettingsModal,
        selectedVenue = selectedVenue,
        onBackAction = tablesV::onBackAction,
        onLogout = tablesV::logout,
        tables = tables,
        showSettings = showSettings,
        onToggleShift = tablesV::toggleShift,
        shiftStarted = shiftStarted,
        isLoading = isLoading
    )
}


@Composable
fun HomeContent(
    onBackAction: () -> Unit = {},
    onTableSelected: (String) -> Unit = {},
    onShowSettings: (Boolean) -> Unit = {},
    onLogout: () -> Unit = {},
    onToggleShift: () -> Unit = {},
    tables: List<Pair<String, String>>,
    selectedVenue: NetworkVenue?,
    showSettings: Boolean = false,
    shiftStarted: Boolean = false,
    isLoading: Boolean = false
) {
    TopMenuContent(
        onBackAction = onBackAction,
        onOpenSettings = { onShowSettings(true) },
        onDismissRequest = { onShowSettings(false) },
        onToggleShift = onToggleShift,
        onLogout = onLogout,
        showSettingsModal = showSettings,
        shiftStarted = shiftStarted
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
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
            
            Divider(
                color = Color.LightGray,
                thickness = 1.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (tables.isEmpty() && !isLoading) {
                    // Mensaje cuando no hay mesas activas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No hay cuentas activas",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.DarkGray,
                                    fontSize = 18.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Las mesas con cuentas abiertas aparecerán aquí",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = Color.Gray
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else if (!isLoading) {
                    // Grid de mesas cuando hay mesas disponibles
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = tables,
                            key = { it.first }
                        ) { table ->
                            TableCard(
                                tableNumber = table.second,
                                onClick = { onTableSelected(table.first) }
                            )
                        }
                    }
                }
                
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableCard(
    tableNumber: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color.Black.copy(alpha = 0.1f)
            )
            .aspectRatio(1f)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Text(
                text = tableNumber,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Black,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
            tables = emptyList(),
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
                    // Preview tables list omitted for brevity
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