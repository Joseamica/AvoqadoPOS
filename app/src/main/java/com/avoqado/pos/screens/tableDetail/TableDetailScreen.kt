package com.avoqado.pos.screens.tableDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.core.model.FlowStep
import com.avoqado.pos.core.model.IconAction
import com.avoqado.pos.core.model.IconType
import com.avoqado.pos.screens.tableDetail.components.ProductItemRow
import com.avoqado.pos.ui.screen.Text
import com.avoqado.pos.ui.screen.ToolbarWithIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableDetailScreen(
    tableDetailViewModel: TableDetailViewModel
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val tableDetails by tableDetailViewModel.tableDetail.collectAsStateWithLifecycle()
    val showPaymentPicker by tableDetailViewModel.showPaymentPicker.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ToolbarWithIcon(
            "Detalle de mesa",
            iconAction = IconAction(
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context,
                iconType = IconType.BACK
            ),
            onAction = {
                tableDetailViewModel.navigateBack()
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Text(text = "Mesa: ${tableDetails.name}")

            LazyColumn(
                modifier = Modifier.weight(1f)
                    .fillMaxWidth()
            ) {
                items(tableDetails.products) { product ->
                    ProductItemRow(product)
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                    .background(Color.DarkGray)
            )

            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Sub total"
                )

                Text(
                    text = "$CURRENCY_LABEL ${tableDetails.totalAmount}"
                )
            }

            Row {
                Text(
                    modifier = Modifier.weight(1f),
                    text = "Pendiente de pago"
                )

                Text(
                    text = "$CURRENCY_LABEL ${tableDetails.totalPending}"
                )
            }

            Button(
                onClick = {
                    tableDetailViewModel.togglePaymentPicker()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Pagar",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

    if (showPaymentPicker) {
        ModalBottomSheet(
            onDismissRequest = {
                tableDetailViewModel.togglePaymentPicker()
            },
            sheetState = sheetState
        ) {
            Button(
                onClick = {
                    tableDetailViewModel.goToPayment("total")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Pagar Total",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                onClick = {
                    tableDetailViewModel.goToPayment("product")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Pagar por producto",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Button(
                onClick = {
                    tableDetailViewModel.goToPayment("amount")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Pagar monto",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}