package com.avoqado.pos.features.management.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.TopMenuContent
import com.avoqado.pos.core.presentation.theme.AppFont
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.theme.textTitleColor
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview

@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    val showSettings by homeViewModel.showSettings.collectAsStateWithLifecycle()
    val isLoading by homeViewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by homeViewModel.isRefreshing.collectAsStateWithLifecycle()
    val shiftStarted by homeViewModel.shiftStarted.collectAsStateWithLifecycle()
    val venuePosName = homeViewModel.getVenuePosName()

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
        onLogout = homeViewModel::logout,
        onToggleShift = homeViewModel::toggleShift,
        onRefresh = homeViewModel::onPullToRefreshTrigger,
        isRefreshing = isRefreshing,
        shiftStarted = shiftStarted,
        venuePosName = venuePosName,
    )

    if (isLoading) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black.copy(alpha = 0.25f),
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

@Composable
fun HomeContent(
    waiterName: String,
    showSettings: Boolean,
    shiftStarted: Boolean = false,
    isRefreshing: Boolean = false,
    toggleSettingsModal: (Boolean) -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onNewPayment: () -> Unit = {},
    onQuickPayment: () -> Unit = {},
    onShowSummary: () -> Unit = {},
    onShowShifts: () -> Unit = {},
    onShowPayments: () -> Unit = {},
    onLogout: () -> Unit = {},
    onToggleShift: () -> Unit = {},
    onRefresh: () -> Unit = {},
    venuePosName: String? = null,
) {
    TopMenuContent(
        onOpenSettings = onOpenSettings,
        showSettingsModal = showSettings,
        onDismissRequest = {
            toggleSettingsModal(false)
        },
        onRefresh = onRefresh,
        isRefreshing = isRefreshing,
        onToggleShift = onToggleShift,
        onLogout = onLogout,
        shiftStarted = shiftStarted,
        venuePosName = venuePosName,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
        ) {
            Text(
                text = "Hola, $waiterName",
                style =
                    MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = AppFont.EffraFamily,
                        fontWeight = FontWeight.W500,
                        color = textTitleColor,
                    ),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                textAlign = TextAlign.Center,
            )

            // Only show the Nuevo pago card if posName is not NONE or null or empty
            if (!venuePosName.isNullOrEmpty() && venuePosName != "NONE") {
                Card(
                    modifier =
                        Modifier.clickable {
                            onNewPayment()
                        },
                    colors =
                        CardDefaults.cardColors(
                            containerColor = Color.Black,
                        ),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                    ) {
                        Spacer(Modifier.height(40.dp))
                        Image(
                            modifier = Modifier.size(40.dp),
                            painter = painterResource(R.drawable.ic_card),
                            contentDescription = "",
                        )
                        Text(
                            text = "Nuevo pago",
                            style =
                                MaterialTheme.typography.headlineMedium.copy(
                                    color = Color.White,
                                ),
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row {
Box(modifier = Modifier.weight(1f)) {  // Outer Box for absolute positioning
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable(enabled = shiftStarted) {
                onQuickPayment()
            },
        colors = CardDefaults.cardColors(
            containerColor = if (shiftStarted && venuePosName == "NONE") Color.Black else Color.White,
        ),
            elevation = CardDefaults.cardElevation(
        defaultElevation = 0.7.dp,
        pressedElevation = 8.dp
    ),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = painterResource(R.drawable.ic_quick_pay),
                contentDescription = "",
                alpha = if (shiftStarted) 1f else 0.2f,
                colorFilter = if (shiftStarted && venuePosName == "NONE") 
                    androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
                    else null
            )

            Spacer(Modifier.height(24.dp))
            Text(
                text = "Pago rápido",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = if (shiftStarted && venuePosName == "NONE") Color.White 
                           else if (shiftStarted) textTitleColor 
                           else Color(0xFFC7C5C5),
                    fontWeight = FontWeight.W400,
                ),
            )
        }
    }
    
    // Absolutely positioned text with background, outside the card's boundaries
    if (!shiftStarted) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 8.dp)  // Push it down outside the card
        ) {
            Text(
                text = "Abre el turno primero",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),  // This applies a dark gray color
                modifier = Modifier
                    .background(
                        Color(0xFFEDEDED),  // This applies a light gray background
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 1.dp)
            )
        }
    }
}

                Spacer(Modifier.width(10.dp))

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp) // Set fixed height for the card
                        .clickable {
                            onShowSummary()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.7.dp,
                        pressedElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                    ) {
                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(R.drawable.ic_resumen),
                            contentDescription = "",
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Resumen",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    color = textTitleColor,
                                    fontWeight = FontWeight.W400,
                                ),
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            Row {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp) // Set fixed height for the card
                        .clickable {
                            onShowShifts()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.7.dp,
                        pressedElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                    ) {
                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(R.drawable.ic_shifts),
                            contentDescription = "",
                        )

                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Turnos",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    color = textTitleColor,
                                    fontWeight = FontWeight.W400,
                                ),
                        )
                    }
                }

                Spacer(Modifier.width(10.dp))

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(130.dp) // Set fixed height for the card
                        .clickable {
                            onShowPayments()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.7.dp,
                        pressedElevation = 8.dp
                    ),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                    ) {
                        Image(
                            modifier = Modifier.size(30.dp),
                            painter = painterResource(R.drawable.ic_summary),
                            contentDescription = "",
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Pagos",
                            style =
                                MaterialTheme.typography.titleMedium.copy(
                                    color = textTitleColor,
                                    fontWeight = FontWeight.W400,
                                ),
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
            showSettings = false,
        )
    }
}
