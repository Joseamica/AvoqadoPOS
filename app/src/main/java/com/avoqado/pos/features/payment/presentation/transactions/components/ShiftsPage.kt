package com.avoqado.pos.features.payment.presentation.transactions.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.presentation.components.PullToRefreshBox
import com.avoqado.pos.core.presentation.utils.toAmountMx
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColumnScope.ShiftsPage(
    items: List<Shift> = emptyList(),
    isLoading: Boolean = false,
    isRefreshing: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    hasMorePages: Boolean = true,
    onLoadMore: () -> Unit = {},
    onRefresh: () -> Unit = {},
) {
    Column(
        modifier = Modifier.weight(1f),
    ) {
        Spacer(Modifier.height(16.dp))

        PullToRefreshBox(
            modifier = Modifier.fillMaxSize(),
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {
            if (items.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No hay turnos para mostrar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = items,
                        key = { shift -> "${shift.id}_${shift.turnId}" }
                    ) { shift ->
                        ShiftItemCard(shift)
                    }
                    
                    if (isLoading && hasMorePages) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShiftItemCard(shift: Shift) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color.White,
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        "Inicio: ",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                    )

                    Text(
                        shift.startTime?.let { formatDateTime(it) } ?: "",
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (shift.endTime.isNullOrBlank()) {
                        // Turno abierto - mostrar indicador "Abierto"
                        Box(
                            modifier =
                                Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0x3300AA00)) // Fondo verde con transparencia
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "Abierto",
                                style =
                                    MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF00AA00), // Verde más oscuro para el texto
                                        fontWeight = FontWeight.Bold,
                                    ),
                            )
                        }
                    } else {
                        // Turno cerrado - mostrar fecha de fin
                        Text(
                            "Fin: ",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Black),
                        )
                        Text(
                            shift.endTime.let { formatDateTime(it) },
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                ),
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Row {
                Column(
                    modifier = Modifier.weight(0.75f),
                ) {
                    Text(
                        "Turno",
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = Color.Black,
                            ),
                    )

                    Text(
                        shift.turnId?.toString() ?: "",
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "Venta",
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = Color.Black,
                            ),
                    )

                    Text(
                        "$${shift.paymentSum.toString().toAmountMx()}",
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "Propina",
                        style =
                            MaterialTheme.typography.bodySmall.copy(
                                color = Color.Black,
                            ),
                    )

                    Text(
                        "$${shift.tipsSum.toString().toAmountMx()}",
                        style =
                            MaterialTheme.typography.bodyMedium.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                            ),
                    )
                }
            }
        }
    }
}

fun formatDateTime(dateString: String): String {
    // Parse the ISO-8601 date/time string (Z means UTC time)
    val instant = Instant.parse(dateString)

    // Convert instant to your desired time zone; system default shown here
    val zonedDateTime = instant.atZone(ZoneId.systemDefault())

    // Define the output format
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm", Locale.getDefault())

    // Format the ZonedDateTime
    return zonedDateTime.format(formatter)
}
