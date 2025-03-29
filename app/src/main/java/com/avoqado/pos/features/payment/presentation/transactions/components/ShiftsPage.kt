package com.avoqado.pos.features.payment.presentation.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.presentation.components.InfiniteScrollList
import com.avoqado.pos.core.presentation.components.ObserverLifecycleEvents
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ColumnScope.ShiftsPage(
    items: List<Shift> = emptyList(),
    isLoading: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    onLoadMore: () -> Unit = {}
) {

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Spacer(Modifier.height(16.dp))

        InfiniteScrollList<Shift>(
            modifier = Modifier.fillMaxSize(),
            loading = isLoading,
            listState = listState,
            items = items,
            itemKey = { it.id ?: ""},
            itemContent = { ShiftItemCard(it) },
            loadingItem = { Row { CircularProgressIndicator() } },
            loadMore = onLoadMore,
        )
    }
}

@Composable
fun ShiftItemCard(shift: Shift){
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Inicio: ",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                    )

                    Text(
                        shift.startTime?.let { formatDateTime(it) } ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Fin: ",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Black)
                    )
                    Text(
                        shift.endTime?.let { formatDateTime(it) } ?: "",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row {
                Column(
                    modifier = Modifier.weight(0.75f)
                ) {
                    Text(
                        "Turno",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Black
                        )
                    )

                    Text(
                        shift.turnId?.toString() ?: "",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Venta",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Black
                        )
                    )

                    Text(
                        "$27,072.00",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        "Propina",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Black
                        )
                    )

                    Text(
                        "$2,707.20",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
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
