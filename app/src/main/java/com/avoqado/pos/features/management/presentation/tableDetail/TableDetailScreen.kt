package com.avoqado.pos.features.management.presentation.tableDetail

import android.os.RemoteException
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.R
import com.avoqado.pos.core.domain.models.SplitType
import com.avoqado.pos.core.presentation.components.KeyboardSheet
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.components.MainButton
import com.avoqado.pos.core.presentation.components.ObserverLifecycleEvents
import com.avoqado.pos.core.presentation.components.PullToRefreshBox
import com.avoqado.pos.core.presentation.model.TableInfo
import com.avoqado.pos.core.presentation.model.VenueInfo
import com.avoqado.pos.core.presentation.theme.AppFont
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.features.management.presentation.tableDetail.components.GenericOptionsUI
import com.avoqado.pos.features.management.presentation.tableDetail.components.ProductListSheet
import com.avoqado.pos.features.management.presentation.tableDetail.model.TableDetail
import com.avoqado.pos.ui.screen.ToolbarWithIcon
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.theme.unselectedItemColor
import com.avoqado.pos.core.presentation.utils.PrinterUtils
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.core.presentation.utils.getBitmap
import com.avoqado.pos.features.management.presentation.tableDetail.model.Payment
import com.menta.android.printer.i9100.core.DevicePrintImpl
import com.menta.android.printer.i9100.model.Align
import com.menta.android.printer.i9100.model.TextFormat
import com.menta.android.printer.i9100.util.TIP_LABEL
import com.menta.android.printer.i9100.util.TOTAL_LABEL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableDetailScreen(
    tableDetailViewModel: TableDetailViewModel,
) {
    val tableDetails by tableDetailViewModel.tableDetail.collectAsStateWithLifecycle()
    val isLoading by tableDetailViewModel.isLoading.collectAsStateWithLifecycle()
    val showCustomAmount by tableDetailViewModel.showPaymentPicker.collectAsStateWithLifecycle()
    var showModalSheet by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val isRefreshing by tableDetailViewModel.isRefreshing.collectAsStateWithLifecycle()

    ObserverLifecycleEvents(
        onCreate = {
            tableDetailViewModel.fetchTableDetail()
            tableDetailViewModel.startListeningUpdates()
        },
        onDestroy = {
            tableDetailViewModel.stopListeningUpdates()
        }
    )

    TableDetailContent(
        isLoading = isLoading,
        tableDetails = tableDetails,
        onNavigateBack = {
            tableDetailViewModel.navigateBack()
        },
        onOpenPayByProduct = {
            tableDetailViewModel.goToSplitBillByProduct()
        },
        onTogglePaymentSheet = {
            tableDetailViewModel.payTotalPendingAmount()
        },
        onShowBillProducts = {
            showModalSheet = true
        },
        onPayCustomAmount = {
            tableDetailViewModel.showPaymentPicker()
        },
        onOpenPayByPerson = {
            tableDetailViewModel.goToSplitBillByPerson()
        },
        onPullToRefresh = tableDetailViewModel::onPullToRefreshTrigger,
        isRefreshing = isRefreshing
    )

    if (showModalSheet) {
        ProductListSheet(
            onDismissRequest = { showModalSheet = false },
            onPrint = {
                PrinterUtils.printComanda(
                    context = context,
                    venue = tableDetailViewModel.venue?.let {
                        VenueInfo(
                            name = it.name ?: "",
                            id = it.id ?: "",
                            address = it.address ?: "",
                            phone = it.phone ?: "",
                            acquisition = ""
                        )
                    } ?: VenueInfo(
                        name = "",
                        id = "",
                        address = "",
                        phone = "",
                        acquisition = ""
                    ),
                    products = tableDetails.products,
                    tableInfo = TableInfo(
                        name = tableDetails.name,
                        waiterName = tableDetails.waiterName,
                        orderNumber = "",
                        folio = "",
                        timestamp = ""
                    )
                )
            },
            products = tableDetails.products
        )
    }

    if (showCustomAmount) {
        KeyboardSheet(
            title = "Cantidad personalizada",
            onDismiss = {
                tableDetailViewModel.hidePaymentPicker()
            },
            onAmountEntered = { amount, isPercentage ->
                tableDetailViewModel.hidePaymentPicker()
                tableDetailViewModel.payCustomPendingAmount(amount)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TableDetailContent(
    tableDetails: TableDetail,
    onNavigateBack: () -> Unit,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onPullToRefresh: () -> Unit,
    onTogglePaymentSheet: () -> Unit,
    onShowBillProducts: () -> Unit,
    onOpenPayByProduct: () -> Unit,
    onPayCustomAmount: () -> Unit,
    onOpenPayByPerson: () -> Unit = {}
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ToolbarWithIcon(
            title = tableDetails.name,
            iconAction = IconAction(
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context,
                iconType = IconType.CANCEL
            ),
            showSecondIcon = true,
            onAction = {
                onNavigateBack()
            },
            onActionSecond = {
                onShowBillProducts()
            }
        )

        if (isLoading) {
            // Mostrar loader mientras se carga la información
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            PullToRefreshBox(
                modifier = Modifier.weight(1f),
                isRefreshing = isRefreshing,
                onRefresh = onPullToRefresh
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Queda por pagar",
                            modifier = Modifier.padding(vertical = 8.dp),
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                        Text(
                            text = "\$${tableDetails.totalPending.toString().toAmountMx()}",
                            fontSize = 62.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        if (tableDetails.paymentsDone.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))

                            Column(
                                modifier = Modifier
                                    .border(
                                        width = 2.dp,
                                        color = Color.LightGray,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "${tableDetails.paymentsDone.size} Pagos",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = AppFont.EffraFamily
                                        ),
                                        color = Color.Black
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Text(
                                        text = "\$${tableDetails.totalPayed.toString().toAmountMx()}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = AppFont.EffraFamily
                                        ),
                                        color = unselectedItemColor
                                    )
                                }
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(2.dp)
                                        .background(Color.LightGray)
                                )
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    tableDetails.paymentsDone.groupBy { it.splitType }.forEach { paymentEntry ->
                                        val totalPayment = paymentEntry.value.sumOf { it.amount }
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                        ) {
                                            Text(
                                                text = when(paymentEntry.key) {
                                                    "PERPRODUCT" -> "Por productos"
                                                    "EQUALPARTS" -> {
                                                        val partySize = paymentEntry.value.first().equalPartsPartySize
                                                        val partySizePayed = paymentEntry.value.sumOf { it.equalPartsPayedFor?.toInt() ?: 0 }
                                                        "$partySizePayed partes de $partySize"
                                                    }
                                                    else -> "Cantidad personalizada"
                                                },
                                                modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontFamily = AppFont.EffraFamily
                                                ),
                                                color = Color.Black
                                            )

                                            Spacer(Modifier.width(8.dp))

                                            Text(
                                                text = "\$${totalPayment.toString().toAmountMx()}",
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontFamily = AppFont.EffraFamily,
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }


                    if (tableDetails.totalPending > 0) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            GenericOptionsUI(
                                splitType = tableDetails.currentSplitType,
                                onClickProducts = {
                                    onOpenPayByProduct()
                                },
                                onClickPeople = {
                                    onOpenPayByPerson()
                                },
                                onClickCustom = {
                                    onPayCustomAmount()
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            MainButton(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Pagar \$${tableDetails.totalPending.toString().toAmountMx()}",
                                onClickR = {
                                    onTogglePaymentSheet()
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}


@Urovo9100DevicePreview
@Composable
fun AmountDisplayPreview() {
    AvoqadoTheme {
        TableDetailContent(
            isLoading = false,
            onNavigateBack = {},
            onTogglePaymentSheet = {},
            onShowBillProducts = {},
            onOpenPayByProduct = {},
            onPayCustomAmount = {},
            onPullToRefresh = {},
            isRefreshing = false,
            tableDetails = TableDetail(
                name = "Mesa 1",
            )
        )
    }
}

@Urovo9100DevicePreview
@Composable
fun AmountDisplayProductsPreview() {
    AvoqadoTheme {
        TableDetailContent(
            isLoading = false,
            onNavigateBack = {},
            onTogglePaymentSheet = {},
            onShowBillProducts = {},
            onOpenPayByProduct = {},
            onPayCustomAmount = {},
            onPullToRefresh = {},
            isRefreshing = false,
            tableDetails = TableDetail(
                name = "Mesa 1",
                currentSplitType = SplitType.PERPRODUCT,
                totalAmount = 100.0,
                paymentsDone = listOf(
                    Payment(
                        amount = 20.0,
                        products = listOf(
                            "qwe123",
                            "asd123"
                        ),
                        splitType = SplitType.PERPRODUCT.value,
                        equalPartsPayedFor = null,
                        equalPartsPartySize = null
                    )
                )
            )
        )
    }
}

@Urovo9100DevicePreview
@Composable
fun AmountDisplayPartySizePreview() {
    AvoqadoTheme {
        TableDetailContent(
            isLoading = false,
            onNavigateBack = {},
            onTogglePaymentSheet = {},
            onShowBillProducts = {},
            onOpenPayByProduct = {},
            onPayCustomAmount = {},
            onPullToRefresh = {},
            isRefreshing = false,
            tableDetails = TableDetail(
                name = "Mesa 1",
                currentSplitType = SplitType.EQUALPARTS,
                totalAmount = 100.0,
                paymentsDone = listOf(
                    Payment(
                        amount = 20.0,
                        products = emptyList(),
                        splitType = SplitType.EQUALPARTS.value,
                        equalPartsPartySize = "5",
                        equalPartsPayedFor = "1"
                    )
                )
            )
        )
    }
}

@Urovo9100DevicePreview
@Composable
fun AmountDisplayCustomAmountPreview() {
    AvoqadoTheme {
        TableDetailContent(
            isLoading = false,
            onNavigateBack = {},
            onTogglePaymentSheet = {},
            onShowBillProducts = {},
            onOpenPayByProduct = {},
            onPayCustomAmount = {},
            onPullToRefresh = {},
            isRefreshing = false,
            tableDetails = TableDetail(
                name = "Mesa 1",
                currentSplitType = SplitType.CUSTOMAMOUNT,
                totalAmount = 100.0,
                paymentsDone = listOf(
                    Payment(
                        amount = 20.0,
                        products = emptyList(),
                        splitType = SplitType.CUSTOMAMOUNT.value,
                        equalPartsPartySize = "",
                        equalPartsPayedFor = ""
                    ),
                    Payment(
                        amount = 20.0,
                        products = emptyList(),
                        splitType = SplitType.CUSTOMAMOUNT.value,
                        equalPartsPartySize = "",
                        equalPartsPayedFor = ""
                    )
                )
            )
        )
    }
}
