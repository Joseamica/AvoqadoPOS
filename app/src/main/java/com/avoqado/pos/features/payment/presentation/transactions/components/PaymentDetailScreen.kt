package com.avoqado.pos.features.payment.presentation.transactions.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avoqado.pos.core.domain.models.PaymentShift
import com.avoqado.pos.core.presentation.utils.toAmountMx
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailScreen(
    payment: PaymentShift,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Pago #${payment.paymentId}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Payment Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Resumen del Pago",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Payment ID and Date
                    DetailsRow(
                        label = "ID de Pago",
                        value = "#${payment.paymentId}"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    DetailsRow(
                        label = "Fecha",
                        value = formatDateTime(payment.date)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Waiter info
                    DetailsRow(
                        label = "Mesero",
                        value = payment.waiterName
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Payment amounts
                    DetailsRow(
                        label = "Venta",
                        value = "$${payment.totalSales.toString().toAmountMx()}"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    DetailsRow(
                        label = "Propina",
                        value = "$${payment.totalTip.toString().toAmountMx()}"
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val total = payment.totalSales + payment.totalTip
                    DetailsRow(
                        label = "Total",
                        value = "$${total.toString().toAmountMx()}",
                        isBold = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Additional payment information (could be expanded later)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Información Adicional",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // You can add more payment details here
                    // For example, payment method, table number, etc.
                    DetailsRow(
                        label = "Método de Pago",
                        value = payment.paymentMethod ?: "No disponible"
                    )
                    
                    if (payment.tableNumber != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DetailsRow(
                            label = "Número de Mesa",
                            value = payment.tableNumber.toString()
                        )
                    }
                    
                    // Add status if available
                    if (!payment.status.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        DetailsRow(
                            label = "Estado",
                            value = payment.status
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetailsRow(
    label: String,
    value: String,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// Use the existing formatDateTime function from PaymentPage.kt
// This function is already defined in PaymentPage.kt, so we're providing it here for reference
fun formatDetailDateTime(date: java.time.Instant): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm:ss")
        .withZone(ZoneId.systemDefault())
        .withLocale(Locale("es", "MX"))
    
    return formatter.format(date)
}
