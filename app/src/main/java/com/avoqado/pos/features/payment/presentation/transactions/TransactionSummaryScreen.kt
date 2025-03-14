package com.avoqado.pos.features.payment.presentation.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.SimpleToolbar
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.theme.primary
import com.avoqado.pos.core.presentation.theme.red
import com.avoqado.pos.customerId
import com.avoqado.pos.merchantId
import com.menta.android.common_cross.data.datasource.local.model.Transaction
import com.menta.android.core.model.LastTrxRequest
import com.menta.android.core.utils.StringUtils.formatAmount
import com.menta.android.core.viewmodel.TrxData
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun TransactionsSummaryScreen(
    trxData: TrxData,
    viewModel: TransactionSummaryViewModel
){
    LaunchedEffect(Unit) {
        val today = LocalDate.now(ZoneOffset.UTC)
            .atTime(23, 59)
            .atZone(ZoneOffset.UTC)
            .toInstant()

        val startOfYesterday: Instant = today.minus(1, ChronoUnit.DAYS)
            .minus(23, ChronoUnit.HOURS)
            .minus(59, ChronoUnit.MINUTES)

        val formatter = DateTimeFormatter.ISO_INSTANT

        val lastTrxRequest = LastTrxRequest(
            appVersion = "",
            operationType = "",
            merchantId = viewModel.currentUser?.primaryMerchantId ?: merchantId,
            customerId = customerId,
            userEmail = null,
            start = formatter.format(startOfYesterday),
            end = formatter.format(today),
            page = 0,
            size = 20
        )
        trxData.getLastTrx(lastTrxRequest = lastTrxRequest)
    }

    val trxState by trxData.getLastTrx.observeAsState()
    val transactions by viewModel.paymentResult.collectAsStateWithLifecycle()

    LaunchedEffect(trxState) {
        trxState?.let {
            viewModel.handleTransactionResponse(it)
        }
    }

    TransactionSummaryContent(
        transactions = transactions,
        onNavigateBack = viewModel::navigateBack
    )
}

@Composable
fun TransactionSummaryContent(
    transactions: List<Transaction>,
    onNavigateBack: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        SimpleToolbar(
            title = "Imprimir Resumen",
            onActionSecond = {
                //Print resumen
            },
            iconAction = IconAction(
                iconType = IconType.BACK,
                flowStep = FlowStep.NAVIGATE_BACK,
                context = LocalContext.current
            ),
            onAction = onNavigateBack
        )

        Spacer(modifier = Modifier.height(56.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

fun getTransactionType(type: String): String {
    return when (type) {
        "PAYMENT" -> "Venta"
        else -> "Devolución"
    }
}

fun getStatus(status: String): String {
    return when (status) {
        "APPROVED" -> "Aprobada"
        else -> "Rechazada"
    }
}

fun getDateTime(datetime: String): String {
    return datetime
}

@Composable
fun TransactionItem(
    transaction: Transaction
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(size = 20.dp)
            )
            .padding(14.dp)
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_bag),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 4.dp)
                .size(18.dp),
            tint = primary
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = getTransactionType(transaction.operation.type),
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = getDateTime(transaction.operation.datetime),
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            when (transaction.operation.type) {
                "PAYMENT" -> {
                    Text(
                        text = "$${formatAmount(transaction.amount)}",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                else -> {
                    Text(
                        text = "$${formatAmount(transaction.amount)}",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

            when (transaction.operation.status) {
                "APPROVED" -> {
                    Text(
                        text = getStatus(transaction.operation.status),
                        style = TextStyle(fontSize = 14.sp, color = primary),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                else -> {
                    Text(
                        text = getStatus(transaction.operation.status),
                        style = TextStyle(fontSize = 14.sp, color = red),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }

        }
    }

}