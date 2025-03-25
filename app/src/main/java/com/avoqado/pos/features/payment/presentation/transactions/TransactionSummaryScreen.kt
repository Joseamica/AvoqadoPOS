package com.avoqado.pos.features.payment.presentation.transactions


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TabRow
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.features.payment.presentation.transactions.components.CreatedFilterSheet
import com.avoqado.pos.features.payment.presentation.transactions.components.PaymentsPage
import com.avoqado.pos.features.payment.presentation.transactions.components.ShiftsPage
import com.avoqado.pos.features.payment.presentation.transactions.components.SummaryPage
import com.avoqado.pos.features.payment.presentation.transactions.components.WaiterFilterSheet

enum class SummaryTabs {
    RESUMEN, PAGOS, TURNOS
}

@Composable
fun TransactionsSummaryScreen(
    viewModel: TransactionSummaryViewModel
) {

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val showWaitersSheet by viewModel.showWaiterSheet.collectAsStateWithLifecycle()
    val showCreatedSheet by viewModel.showCreatedSheet.collectAsStateWithLifecycle()
    val filteredWaiters by viewModel.filteredWaiters.collectAsStateWithLifecycle()
    val filteredDates by viewModel.filteredDates.collectAsStateWithLifecycle()

    TransactionSummaryContent(
        onNavigateBack = viewModel::navigateBack,
        selectedTab = currentTab,
        showWaitersSheet = showWaitersSheet,
        onTabSelected = viewModel::updateTab,
        onToggleWaitersSheet = viewModel::toggleWaitersSheet,
        showCreatedSheet = showCreatedSheet,
        onToggleCreatedSheet = viewModel::toggleCreatedSheet,
        onApplyWaiterFilter = viewModel::onFilterByWaiters,
        onApplyDateFilter = viewModel::onFilterByDates,
        filteredWaiters = filteredWaiters,
        filteredDates = filteredDates
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSummaryContent(
    onNavigateBack: () -> Unit = {},
    onPrintPage: () -> Unit = {},
    selectedTab: SummaryTabs = SummaryTabs.RESUMEN,
    showWaitersSheet: Boolean = false,
    onTabSelected: (SummaryTabs) -> Unit = {},
    onToggleWaitersSheet: (Boolean) -> Unit = {},
    showCreatedSheet: Boolean = false,
    onToggleCreatedSheet: (Boolean) -> Unit = {},
    filteredDates: Pair<Long?,Long?> = Pair(null,null),
    filteredWaiters: List<String> = emptyList(),
    onApplyDateFilter: (Pair<Long?,Long?>) -> Unit = {},
    onApplyWaiterFilter: (List<String>) -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                actionIconContentColor = Color.Black
            ),
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Mas Info.",
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.Black),
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack,
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.icon_back),
                            contentDescription = null
                        )
                    }
                )
            },
            actions = {
                IconButton(
                    onClick = onPrintPage,
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_print_24),
                            contentDescription = null
                        )
                    }
                )
            }
        )

        TabRow(
            containerColor = Color.White,
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = SummaryTabs.entries.indexOf(selectedTab),
            tabs = {
                Tab(
                    selected = selectedTab == SummaryTabs.RESUMEN,
                    onClick = { onTabSelected(SummaryTabs.RESUMEN) },
                    content = {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = "Resumen",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (selectedTab == SummaryTabs.RESUMEN) FontWeight.Bold else FontWeight.W400,
                                color = Color.Black
                            )
                        )
                    }
                )

                Tab(
                    selected = selectedTab == SummaryTabs.PAGOS,
                    onClick = { onTabSelected(SummaryTabs.PAGOS) },
                    content = {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = "Pagos",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (selectedTab == SummaryTabs.PAGOS) FontWeight.Bold else FontWeight.W400,
                                color = Color.Black
                            )
                        )
                    }
                )

                Tab(
                    selected = selectedTab == SummaryTabs.TURNOS,
                    onClick = { onTabSelected(SummaryTabs.TURNOS) },
                    content = {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = "Turnos",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (selectedTab == SummaryTabs.TURNOS) FontWeight.Bold else FontWeight.W400,
                                color = Color.Black
                            )
                        )
                    }
                )
            }
        )

        Row(
            modifier = Modifier.padding(
                horizontal = 24.dp,
            )
                .padding(top = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(100.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(100.dp)
                    )
                    .clickable {
                        onToggleWaitersSheet(true)
                    }
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
            ) {
                Text(
                    text = "Mesero",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Black
                    )
                )
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(100.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(100.dp)
                    )
                    .clickable {
                        onToggleCreatedSheet(true)
                    }
                    .padding(
                        vertical = 8.dp,
                        horizontal = 16.dp
                    )
            ) {
                Text(
                    text = "Creado",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Black
                    )
                )
            }
        }

        when (selectedTab) {
            SummaryTabs.RESUMEN -> SummaryPage()
            SummaryTabs.PAGOS -> PaymentsPage()
            SummaryTabs.TURNOS -> ShiftsPage()
        }
    }

    if (showWaitersSheet) {
        WaiterFilterSheet(
            onDismiss = {
                onToggleWaitersSheet(false)
            },
            onApplyFilter = onApplyWaiterFilter,
            preSelectedWaiters = filteredWaiters
        )
    }

    if (showCreatedSheet) {
        CreatedFilterSheet(
            onDismiss = {
                onToggleCreatedSheet(false)
            },
            onApplyFilter = onApplyDateFilter,
            preSelectedDates = filteredDates
        )
    }
}


@Urovo9100DevicePreview
@Composable
fun TransactionSummaryContentPreview() {
    AvoqadoTheme {
        TransactionSummaryContent()
    }
}

@Urovo9100DevicePreview
@Composable
fun PaymentsSummaryContentPreview() {
    AvoqadoTheme {
        TransactionSummaryContent(
            selectedTab = SummaryTabs.PAGOS
        )
    }
}

@Urovo9100DevicePreview
@Composable
fun ShiftsSummaryContentPreview() {
    AvoqadoTheme {
        TransactionSummaryContent(
            selectedTab = SummaryTabs.TURNOS
        )
    }
}

