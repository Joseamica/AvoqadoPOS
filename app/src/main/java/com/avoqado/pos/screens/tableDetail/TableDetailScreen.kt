package com.avoqado.pos.screens.tableDetail

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
import com.avoqado.pos.ui.screen.ProductRow
import com.avoqado.pos.ui.screen.Text
import com.avoqado.pos.ui.screen.ToolbarWithIcon
import com.avoqado.pos.util.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableDetailScreen(
    tableDetailViewModel: TableDetailViewModel
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val tableDetails by tableDetailViewModel.tableDetail.collectAsStateWithLifecycle()
    val showPaymentPicker by tableDetailViewModel.showPaymentPicker.collectAsStateWithLifecycle()
    val isLoading by tableDetailViewModel.isLoading.collectAsStateWithLifecycle()
    var showModalSheet by rememberSaveable { mutableStateOf(false) }
    var stateScreenProducts by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        ToolbarWithIcon(
            title = tableDetails.name,
            iconAction = IconAction(
                flowStep = FlowStep.NAVIGATE_BACK,
                context = context,
                iconType = IconType.BACK
            ),
            onAction = {
                tableDetailViewModel.navigateBack()
            },
            onActionSecond = {
                showModalSheet = true
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
                        .weight(0.6f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Queda por pagar: ",
                        modifier = Modifier.padding(vertical = 8.dp),
                        fontSize = 24.sp
                    )
                    Utils.MmUtlAmountTextViewV2(
                        amount = "${tableDetails.totalAmount}",
                        currencyType = "MXN",
                        isVisible = true,
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .weight(0.4f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    GenericOptionsUI(
                        onClickProducts = {
                            stateScreenProducts = true
                        }
                    )
                    //Para verla lista de productos descomente la linea de abajo
                    // ProductsScreen(listProducts = tableDetails.products)
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Utils.UtilButtonView(
                        text = "Pagar",
                        onClickR = {
                            tableDetailViewModel.togglePaymentPicker()
                        },
                        color = Color.Black,
                        textColor = Color.White
                    )
                }
            }
        }
    }
    if (stateScreenProducts) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .zIndex(1f)
        ) {
            ToolbarWithIcon(
                title = "Productos",
                iconAction = IconAction(
                    flowStep = FlowStep.NAVIGATE_BACK,
                    context = context,
                    iconType = IconType.BACK
                ),
                onAction = {
                    stateScreenProducts = false
                },
                showSecondIcon = true
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(46.dp))
                ProductsScreen(tableDetails.products)
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pendiente de pago:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "$CURRENCY_LABEL ${tableDetails.totalPending}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Utils.UtilButtonView(
                        text = "Pagar",
                        onClickR = {
                            tableDetailViewModel.togglePaymentPicker()
                        },
                        color = Color.Black,
                        textColor = Color.White
                    )
                }
            }
        }
    }
    if (showModalSheet) {
        ModalBottomSheet(
            onDismissRequest = { showModalSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tableDetails.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tableDetails.totalAmount.toString(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Divider(color = Color.Gray.copy(alpha = 0.5f), thickness = 1.dp)

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(tableDetails.products) { product ->
                        ProductRow(
                            number = product.quantity,
                            name = product.name,
                            price = product.price.toString()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showPaymentPicker) {
        ModalBottomSheet(
            onDismissRequest = { tableDetailViewModel.togglePaymentPicker() },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Seleccione el tipo de pago",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Button(
                    onClick = { tableDetailViewModel.goToPayment("total") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Pagar Total",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { tableDetailViewModel.goToPayment("product") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Pagar por producto",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { tableDetailViewModel.goToPayment("amount") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Pagar monto",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun GenericOptionCard(
    icon: Painter,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun GenericOptionsUI(
    onClickProducts: () -> Unit,
    onClickPeople: () -> Unit = {},
    onClickCustom: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GenericOptionCard(
                icon = painterResource(R.drawable.icon_products),
                title = "Productos",
                onClick = {
                    onClickProducts()
                },
                modifier = Modifier.weight(1f)

            )
            Spacer(modifier = Modifier.width(16.dp))
            GenericOptionCard(
                icon = painterResource(R.drawable.icon_people),
                title = "Personas",
                onClick = {
                    onClickPeople()
                },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        GenericOptionCard(
            icon = painterResource(R.drawable.icon_edit),
            title = "Cantidad personalizada",
            onClick = {
                onClickCustom()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun AmountDisplayPreview() {
    MaterialTheme {
        var visible by rememberSaveable { mutableStateOf(true) }
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary)
        ) {
            Scaffold { innerPadding ->
                Column(
                    modifier = Modifier.padding(innerPadding)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Utils.MmUtlAmountTextViewV2(
                        amount = "1598.62",
                        currencyType = "MXN",
                        isVisible = visible,
                        modifier = Modifier.clickable {
                            visible = !visible
                        }
                    )
                    Utils.MmUtlAmountTextViewV2(
                        amount = "0.0505",
                        isVisible = visible,
                        currencyType = "MXN",
                        fullVisibility = true,
                        maxDecimal = 6
                    )
                    Utils.MmUtlAmountTextViewV2(
                        amount = "1.0505",
                        isVisible = visible,
                        currencyType = "USD",
                        fullVisibility = true,
                        maxDecimal = 6
                    )
                }
            }
        }
    }
}
