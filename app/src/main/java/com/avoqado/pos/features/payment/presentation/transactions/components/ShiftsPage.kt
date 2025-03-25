package com.avoqado.pos.features.payment.presentation.transactions.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.ShiftsPage() {
    Column(
        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))

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
                            "Mar 02, 2025 14:34",
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
                            "Mar 02, 2025 14:34",
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
                            "777",
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
}