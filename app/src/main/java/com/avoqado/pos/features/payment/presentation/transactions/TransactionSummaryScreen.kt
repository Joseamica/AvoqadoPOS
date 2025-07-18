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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R
import com.avoqado.pos.core.domain.models.Payment
import com.avoqado.pos.core.domain.models.Shift
import com.avoqado.pos.core.domain.models.ShiftSummary
import com.avoqado.pos.core.presentation.components.ObserverLifecycleEvents
import com.avoqado.pos.core.presentation.model.VenueInfo
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.PrinterUtils
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.core.presentation.utils.toAmountMXDouble
import com.avoqado.pos.features.payment.presentation.transactions.components.CreatedFilterSheet
import com.avoqado.pos.features.payment.presentation.transactions.components.PaymentsPage
import com.avoqado.pos.features.payment.presentation.transactions.components.ShiftsPage
import com.avoqado.pos.features.payment.presentation.transactions.components.SummaryPage
import com.avoqado.pos.features.payment.presentation.transactions.components.WaiterFilterSheet
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

enum class SummaryTabs {
    RESUMEN,
    PAGOS,
    TURNOS,
}

@Composable
fun TransactionsSummaryScreen(viewModel: TransactionSummaryViewModel) {
    val context = LocalContext.current

    ObserverLifecycleEvents(
        onCreate = {
            viewModel.loadSummary()
            viewModel.loadShiftsSummary()
            viewModel.loadPaymentsSummary()
        },
    )

    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val showWaitersSheet by viewModel.showWaiterSheet.collectAsStateWithLifecycle()
    val showCreatedSheet by viewModel.showCreatedSheet.collectAsStateWithLifecycle()
    val filteredWaiters by viewModel.filteredWaiters.collectAsStateWithLifecycle()
    val filteredDates by viewModel.filteredDates.collectAsStateWithLifecycle()

    val isLoadingMore by viewModel.isLoadingMore.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val shifts by viewModel.shiftsList.collectAsStateWithLifecycle()
    val summary by viewModel.shiftSummary.collectAsStateWithLifecycle()
    val payments by viewModel.paymentsShiftList.collectAsStateWithLifecycle()
    val hasMoreShiftsPages by viewModel.hasMoreShiftsPages.collectAsStateWithLifecycle()
    val hasMorePaymentsPages by viewModel.hasMorePaymentsPages.collectAsStateWithLifecycle()

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
        filteredDates = filteredDates,
        isLoading = isLoading,
        isLoadingMore = isLoadingMore,
        shifts = shifts,
        hasMoreShiftsPages = hasMoreShiftsPages, // Add this parameter
        onLoadMore = {
            viewModel.loadShiftsSummary(nextPage = true)
        },
        summary = summary,
        payments = payments,
        onLoadMorePayments = viewModel::loadPaymentsSummary,
        onPaymentSelected = viewModel::onPaymentSelected,
        waiters = viewModel.venueInfo?.waiters?.map { Pair(it.id, it.nombre) } ?: emptyList(),
        onPrintPage = {
            val venue =
                viewModel.venue?.let {
                    VenueInfo(
                        name = it.name ?: "",
                        id = it.id ?: "",
                        address = it.address ?: "",
                        phone = it.phone ?: "",
                        acquisition = "",
                    )
                } ?: VenueInfo(
                    name = "",
                    id = "",
                    address = "",
                    phone = "",
                    acquisition = "",
                )

            when (currentTab) {
                SummaryTabs.RESUMEN -> {
                    PrinterUtils.printPeriodSummary(
                        context = context,
                        venue = venue,
                        totalSales = summary?.totalSales?.toString()?.toAmountMXDouble() ?: 0.0,
                        totalTips = summary?.totalTips?.toString()?.toAmountMXDouble() ?: 0.0,
                        orderCount = summary?.ordersCount ?: 0,
                        ratingCount = summary?.ratingsCount ?: 0,
                        avgTipPercentage = summary?.averageTipPercentage ?: 0.0,
                        tipsByUser =
                            summary?.tips?.take(10)?.map {
                                mapOf(
                                    "name" to it.first,
                                    "tip" to it.second.toAmountMXDouble(),
                                )
                            } ?: emptyList(),
                    )
                }
                SummaryTabs.PAGOS -> {
                    PrinterUtils.printPaymentsSummary(
                        context = context,
                        shiftPayments =
                            payments.take(10).map { payment ->
                                mapOf(
                                    "amount" to payment.amount.toString().toAmountMXDouble(),
                                    "tip" to payment.tipAmount.toString().toAmountMXDouble(),
                                    "folio" to payment.id,
                                    "dateTime" to
                                        LocalDateTime.ofInstant(
                                            payment.createdAt,
                                            ZoneId.systemDefault(),
                                        ),
                                )
                            },
                        venue = venue,
                    )
                }
                SummaryTabs.TURNOS -> {
                    PrinterUtils.printShiftsSummary(
                        context = context,
                        shifts =
                            shifts.take(10).map { shift ->
                                mapOf(
                                    "amount" to shift.paymentSum.toString().toAmountMXDouble(),
                                    "tip" to shift.tipsSum.toString().toAmountMXDouble(),
                                    "shift" to shift.id,
                                    "startTime" to
                                        shift.startTime?.let {
                                            try {
                                                LocalDateTime.ofInstant(
                                                    Instant.parse(it),
                                                    ZoneId.systemDefault()
                                                )
                                            } catch (e: Exception) {
                                                null
                                            }
                                        },
                                    "endTime" to
                                        if (!shift.endTime.isNullOrEmpty()) {
                                            try {
                                                LocalDateTime.ofInstant(
                                                    Instant.parse(shift.endTime),
                                                    ZoneId.systemDefault()
                                                )
                                            } catch (e: Exception) {
                                                null
                                            }
                                        } else {
                                            null
                                        },
                                )
                            },
                        venue = venue,
                    )
                }
            }
        },
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
    filteredDates: Pair<Long?, Long?> = Pair(null, null),
    waiters: List<Pair<String, String>> = emptyList(),
    filteredWaiters: List<String> = emptyList(),
    onApplyDateFilter: (Pair<Long?, Long?>) -> Unit = {},
    onApplyWaiterFilter: (List<String>) -> Unit = {},
    isLoading: Boolean = false,
    isLoadingMore: Boolean = false,
    shifts: List<Shift> = emptyList(),
    hasMoreShiftsPages: Boolean = true, // Add this parameter
    hasMorePaymentsPages: Boolean = true, // Add this parameter
    onLoadMore: () -> Unit = {},
    summary: ShiftSummary? = null,
    payments: List<Payment> = emptyList(),
    onLoadMorePayments: () -> Unit = {},
    onPaymentSelected: (Payment) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TopAppBar(
            modifier = Modifier.height(52.dp), // Decreased height
            colors =
                TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black,
                ),
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Mas Info.",
                    style = MaterialTheme.typography.titleSmall.copy(color = Color.Black),
                    textAlign = TextAlign.Center,
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack,
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.icon_back),
                            contentDescription = null,
                        )
                    },
                )
            },
            actions = {
                IconButton(
                    onClick = onPrintPage,
                    content = {
                        Icon(
                            painter = painterResource(R.drawable.baseline_print_24),
                            contentDescription = null,
                        )
                    },
                )
            },
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
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (selectedTab == SummaryTabs.RESUMEN) FontWeight.Bold else FontWeight.W400,
                                    color = Color.Black,
                                ),
                        )
                    },
                )

                Tab(
                    selected = selectedTab == SummaryTabs.PAGOS,
                    onClick = { onTabSelected(SummaryTabs.PAGOS) },
                    content = {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = "Pagos",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (selectedTab == SummaryTabs.PAGOS) FontWeight.Bold else FontWeight.W400,
                                    color = Color.Black,
                                ),
                        )
                    },
                )

                Tab(
                    selected = selectedTab == SummaryTabs.TURNOS,
                    onClick = { onTabSelected(SummaryTabs.TURNOS) },
                    content = {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = "Turnos",
                            style =
                                MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (selectedTab == SummaryTabs.TURNOS) FontWeight.Bold else FontWeight.W400,
                                    color = Color.Black,
                                ),
                        )
                    },
                )
            },
        )

        Row(
            modifier =
                Modifier
                    .padding(
                        horizontal = 24.dp,
                    ).padding(top = 8.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .background(
                            color = if (filteredWaiters.isNotEmpty()) Color.Black else Color.White,
                            shape = RoundedCornerShape(100.dp),
                        ).border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(100.dp),
                        ).clickable {
                            onToggleWaitersSheet(true)
                        }.padding(
                            vertical = 8.dp,
                            horizontal = 16.dp,
                        ),
            ) {
                Text(
                    text = "Mesero",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = if (filteredWaiters.isNotEmpty()) Color.White else Color.Black,
                        ),
                )
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier =
                    Modifier
                        .background(
                            color = if (filteredDates.let { it.first != null || it.second != null }) Color.Black else Color.White,
                            shape = RoundedCornerShape(100.dp),
                        ).border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(100.dp),
                        ).clickable {
                            onToggleCreatedSheet(true)
                        }.padding(
                            vertical = 8.dp,
                            horizontal = 16.dp,
                        ),
            ) {
                Text(
                    text = "Creado",
                    style =
                        MaterialTheme.typography.bodySmall.copy(
                            color = if (filteredDates.let { it.first != null || it.second != null }) Color.White else Color.Black,
                        ),
                )
            }
        }

        when (selectedTab) {
            SummaryTabs.RESUMEN ->
                SummaryPage(
                    isLoading = isLoading,
                    summary = summary,
                )

            SummaryTabs.PAGOS ->
                PaymentsPage(
                    isLoading = isLoadingMore,
                    items = payments,
                    onLoadMore = onLoadMorePayments,
                    hasMorePages = hasMorePaymentsPages,
                    onPaymentSelected = onPaymentSelected,
                )

            SummaryTabs.TURNOS ->
                ShiftsPage(
                    items = shifts,
                    isLoading = isLoadingMore,
                    onLoadMore = onLoadMore,
                    hasMorePages = hasMoreShiftsPages,
                )
        }
    }

    if (showWaitersSheet) {
        WaiterFilterSheet(
            onDismiss = {
                onToggleWaitersSheet(false)
            },
            waiterList = waiters,
            onApplyFilter = onApplyWaiterFilter,
            preSelectedWaiters = filteredWaiters,
        )
    }

    if (showCreatedSheet) {
        CreatedFilterSheet(
            onDismiss = {
                onToggleCreatedSheet(false)
            },
            onApplyFilter = onApplyDateFilter,
            preSelectedDates = filteredDates,
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
            selectedTab = SummaryTabs.PAGOS,
        )
    }
}

@Urovo9100DevicePreview
@Composable
fun ShiftsSummaryContentPreview() {
    AvoqadoTheme {
        TransactionSummaryContent(
            selectedTab = SummaryTabs.TURNOS,
        )
    }
}
