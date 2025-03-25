package com.avoqado.pos.features.payment.presentation.transactions.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.PaymentsPage() {
    Column(
        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
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
                        text = "$27,072.00",
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
                        text = "$2,707.20",
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
                        text = "#287202593",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W500
                        )
                    )
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Diego R. Macias",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W500
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Mar 02, 2025 14:34",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W500
                        )
                    )
                }
            }
        }
    }
}