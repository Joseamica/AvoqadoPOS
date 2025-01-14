package com.avoqado.pos.screens.tableDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
    val isLoading by tableDetailViewModel.isLoading.collectAsStateWithLifecycle()

    if (isLoading) {
        // Loader mientras se cargan los datos
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título de la mesa
            Text(
                text = "Mesa ${tableDetails.name}",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Monto pendiente
            Text(
                text = "Queda por pagar",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "$CURRENCY_LABEL ${tableDetails.totalPending}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botones de selección de tipo de pago
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PaymentOptionButton("Productos") { tableDetailViewModel.goToPayment("product") }
                PaymentOptionButton("Personas") { /* Acción pendiente */ }
                PaymentOptionButton("Cantidad personalizada") { tableDetailViewModel.goToPayment("amount") }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón principal de pago
            Button(
                onClick = { tableDetailViewModel.togglePaymentPicker() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Pagar $CURRENCY_LABEL ${tableDetails.totalPending}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun PaymentOptionButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .width(110.dp)
            .height(48.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
