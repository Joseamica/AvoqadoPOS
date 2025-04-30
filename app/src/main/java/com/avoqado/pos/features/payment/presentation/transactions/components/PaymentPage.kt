package com.avoqado.pos.features.payment.presentation.transactions.components

import androidx.compose.foundation.border
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avoqado.pos.core.domain.models.PaymentShift
import com.avoqado.pos.core.presentation.components.PullToRefreshBox
import com.avoqado.pos.core.presentation.utils.toAmountMx
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ColumnScope.PaymentsPage(
    items: List<PaymentShift> = emptyList(),
    isLoading: Boolean = false,
    isRefreshing: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    hasMorePages: Boolean = true,
    onLoadMore: () -> Unit = {},
    onRefresh: () -> Unit = {},
) {
    // Calculate the total from all payment items
    val totalSales = remember(items) {
        items.sumOf { it.totalSales }
    }
    
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
                        text = "No hay pagos para mostrar",
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
                        key = { payment -> "${payment.id}_${payment.date ?: ""}_${payment.paymentId}_${payment.hashCode()}" }
                    ) { payment ->
                        PaymentShiftItemCard(payment)
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
    
    // Add sticky total footer
    TotalFooter(totalAmount = totalSales)
}

@Composable
fun TotalFooter(totalAmount: Int) {
    Surface(
        color = Color.White,
        modifier = Modifier.fillMaxWidth(),
        elevation = 8.dp
    ) {
        Divider(color = Color.LightGray, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            
            Text(
                text = "$${totalAmount.toString().toAmountMx()}",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun PaymentShiftItemCard(payment: PaymentShift) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color.White,
            ),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "Venta",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = Color.Black,
                        ),
                )
                Text(
                    text = "$${payment.totalSales.toString().toAmountMx()}",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W600,
                        ),
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Propina",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = Color.Black,
                        ),
                )
                Text(
                    text = "$${payment.totalTip.toString().toAmountMx()}",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W600,
                        ),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "#${payment.paymentId}",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W500,
                        ),
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = payment.waiterName,
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W500,
                        ),
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = formatDateTime(payment.date),
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W500,
                        ),
                )
            }
        }
    }
}

fun formatDateTime(instant: Instant): String {
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
        .withZone(ZoneId.systemDefault())
        .withLocale(Locale("es", "MX"))
    
    return formatter.format(instant)
}
