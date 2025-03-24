package com.avoqado.pos.features.management.presentation.home


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.TopMenuContent
import com.avoqado.pos.core.presentation.theme.AppFont
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.theme.textTitleColor
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel
) {

    val showSettings by homeViewModel.showSettings.collectAsStateWithLifecycle()

    HomeContent(
        waiterName = homeViewModel.currentSession?.name ?: "",
        showSettings = showSettings,
        onOpenSettings = {
            homeViewModel.toggleSettingsModal(true)
        },
        toggleSettingsModal = {
            homeViewModel.toggleSettingsModal(it)
        },
        onQuickPayment = homeViewModel::goToQuickPayment,
        onNewPayment = homeViewModel::goToNewPayment,
        onShowShifts = homeViewModel::goToShowShifts,
        onShowSummary = homeViewModel::goToSummary,
        onShowPayments = homeViewModel::goToShowPayments,
        onLogout = homeViewModel::logout
    )
}

@Composable
fun HomeContent(
    waiterName: String,
    showSettings: Boolean,
    toggleSettingsModal: (Boolean) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onNewPayment: () -> Unit = {},
    onQuickPayment: () -> Unit = {},
    onShowSummary: () -> Unit = {},
    onShowShifts: () -> Unit = {},
    onShowPayments: () -> Unit = {},
    onLogout: ()-> Unit = {}
) {
    TopMenuContent(
        onOpenSettings = onOpenSettings,
        showSettingsModal = showSettings,
        onDismissRequest = {
            toggleSettingsModal(false)
        },
        onToggleShift = {},
        onLogout = onLogout
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            Text(
                text = "Hola, $waiterName",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = AppFont.EffraFamily,
                    fontWeight = FontWeight.W500,
                    color = textTitleColor
                ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Card(
                modifier = Modifier.clickable {
                    onNewPayment()
                },
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                ) {
                    Spacer(Modifier.height(48.dp))
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(R.drawable.ic_card),
                        contentDescription = ""
                    )
                    Text(
                        text = "Nuevo pago",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row {
                Card(

                    modifier = Modifier.weight(1f).clickable {
                        onQuickPayment()
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(R.drawable.ic_quick_pay),
                            contentDescription = ""
                        )

                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Pago rapido",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = textTitleColor,
                                fontWeight = FontWeight.W400
                            )
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Card(
                    modifier = Modifier.weight(1f).clickable {
                        onShowSummary()
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(R.drawable.ic_resumen),
                            contentDescription = ""
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Resumen",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = textTitleColor,
                                fontWeight = FontWeight.W400
                            )
                        )
                    }
                }

            }

            Spacer(Modifier.height(10.dp))

            Row {
                Card(
                    modifier = Modifier.weight(1f).clickable {
                        onShowShifts()
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(R.drawable.ic_shifts),
                            contentDescription = ""
                        )

                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Turnos",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = textTitleColor,
                                fontWeight = FontWeight.W400
                            )
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Card(
                    modifier = Modifier.weight(1f).clickable {
                        onShowPayments()
                    },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(R.drawable.ic_summary),
                            contentDescription = ""
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Pagos",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = textTitleColor,
                                fontWeight = FontWeight.W400
                            )
                        )
                    }
                }

            }

        }
    }
}


@Urovo9100DevicePreview
@Composable
fun HomeContentPreview() {
    AvoqadoTheme {
        HomeContent(
            waiterName = "Diego",
            showSettings = false
        )
    }
}
