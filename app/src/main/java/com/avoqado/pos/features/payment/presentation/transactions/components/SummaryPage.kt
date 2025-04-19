package com.avoqado.pos.features.payment.presentation.transactions.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avoqado.pos.core.domain.models.ShiftSummary
import com.avoqado.pos.core.presentation.utils.toAmountMx

@Composable
fun ColumnScope.SummaryPage(
    summary: ShiftSummary?,
    isLoading: Boolean = false,
) {
    Column(
        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Ventas",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                    Text(
                        text = "$${(summary?.totalSales ?: 0).toString().toAmountMx()}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W600
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Ordenes",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                    Text(
                        text = "${(summary?.ordersCount ?: 0)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W600
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Calificaciónes",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                    Text(
                        text = "${(summary?.ratingsCount ?: 0)}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W600
                        )
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Propinas",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                    Text(
                        text = "$${(summary?.totalTips ?: 0).toString().toAmountMx()}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W600
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Prom. Propinas",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black
                        )
                    )
                    Text(
                        text = "${(summary?.averageTipPercentage ?: 0.0).toString().toAmountMx()}%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Black,
                            fontWeight = FontWeight.W600
                        )
                    )
                }
            }
        }

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
                Text(
                    text = "Propinas",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.Black
                    )
                )

                if (summary?.tips.isNullOrEmpty()) {
                    // Mostrar mensaje cuando no hay propinas
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Aún no existen propinas para mostrar",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                } else {
                    // Mostrar la lista de propinas cuando hay datos
                    Row {
                        Text(
                            text = "Nombre",
                            modifier = Modifier.weight(0.75f),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = "Propina",
                            modifier = Modifier.weight(0.25f),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    summary?.tips?.forEach { tip ->
                        Row(
                            modifier = Modifier.padding(
                                bottom = 4.dp
                            )
                        ) {
                            Text(
                                text = tip.first,
                                modifier = Modifier.weight(0.75f),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Black
                                )
                            )

                            Text(
                                text = "$${tip.second}",
                                modifier = Modifier.weight(0.25f),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}