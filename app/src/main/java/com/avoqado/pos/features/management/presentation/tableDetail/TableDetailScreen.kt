package com.avoqado.pos.features.management.presentation.tableDetail

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.R
import com.avoqado.pos.core.model.FlowStep
import com.avoqado.pos.core.model.IconAction
import com.avoqado.pos.core.model.IconType
import com.avoqado.pos.core.presentation.components.MainButton
import com.avoqado.pos.core.presentation.components.ObserverLifecycleEvents
import com.avoqado.pos.core.utils.toAmountMx
import com.avoqado.pos.features.management.presentation.tableDetail.components.GenericOptionsUI
import com.avoqado.pos.features.management.presentation.tableDetail.components.ProductListSheet
import com.avoqado.pos.features.management.presentation.tableDetail.model.TableDetail
import com.avoqado.pos.ui.screen.ProductRow
import com.avoqado.pos.ui.screen.ToolbarWithIcon
import com.avoqado.pos.ui.theme.AvoqadoTheme
import com.avoqado.pos.util.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableDetailScreen(
    tableDetailViewModel: TableDetailViewModel
) {
    val tableDetails by tableDetailViewModel.tableDetail.collectAsStateWithLifecycle()
    val isLoading by tableDetailViewModel.isLoading.collectAsStateWithLifecycle()
    var showModalSheet by rememberSaveable { mutableStateOf(false) }

    ObserverLifecycleEvents(
        onCreate = {
            tableDetailViewModel.fetchTableDetail()
        },
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
        }
    )

    if (showModalSheet) {
        ProductListSheet(
            onDismissRequest = { showModalSheet = false },
            products = tableDetails.products
        )
    }
}

@Composable
private fun TableDetailContent(
    tableDetails: TableDetail,
    onNavigateBack: () -> Unit,
    isLoading: Boolean,
    onTogglePaymentSheet: () -> Unit,
    onShowBillProducts: () -> Unit,
    onOpenPayByProduct: () -> Unit
){
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        ToolbarWithIcon(
            title = tableDetails.name,
            iconAction = IconAction(
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context,
                iconType = IconType.BACK
            ),
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
            Column(
                modifier = Modifier,
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
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GenericOptionsUI(
                        onClickProducts = {
                            onOpenPayByProduct()
                        },
                        onClickPeople = {},
                        onClickCustom = {}
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




@Preview(showSystemUi = false)
@Composable
fun AmountDisplayPreview() {
    AvoqadoTheme {
        TableDetailContent(
            isLoading = false,
            onNavigateBack = {},
            onTogglePaymentSheet = {},
            onShowBillProducts = {},
            onOpenPayByProduct = {},
            tableDetails = TableDetail(
                totalPending = 777.0
            )
        )
    }
}
