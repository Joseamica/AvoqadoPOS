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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avoqado.pos.core.domain.models.PaymentShift
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.presentation.components.InfiniteScrollList
import com.avoqado.pos.core.presentation.utils.toAmountMx

@Composable
fun ColumnScope.PaymentsPage(
    items: List<PaymentShift> = emptyList(),
    isLoading: Boolean = false,
    listState: LazyListState = rememberLazyListState(),
    onLoadMore: () -> Unit = {}
) {
    Column(
        modifier = Modifier.weight(1f)
    ) {
        Spacer(Modifier.height(16.dp))

        InfiniteScrollList<PaymentShift>(
            modifier = Modifier.fillMaxSize(),
            loading = isLoading,
            listState = listState,
            items = items,
            itemKey = { it.id ?: "" },
            itemContent = { PaymentShiftItemCard(it) },
            loadingItem = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) { CircularProgressIndicator() }
            },
            loadMore = onLoadMore,
        )
    }
}

@Composable
fun PaymentShiftItemCard(payment: PaymentShift){
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Venta",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Black
                    )
                )
                Text(
                    text = "$${payment.totalSales.toString().toAmountMx()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.W600
                    )
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Propina",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Black
                    )
                )
                Text(
                    text = "$${payment.totalTip.toString().toAmountMx()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.W600
                    )
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = "#${payment.paymentId}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.W500
                    )
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = payment.waiterName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.W500
                    )
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = formatDateTime(payment.date.toString()),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Black,
                        fontWeight = FontWeight.W500
                    )
                )
            }
        }
    }
}