package com.avoqado.pos.features.management.presentation.splitProduct

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.core.presentation.components.MainButton
import com.avoqado.pos.core.presentation.components.SelectableItemRow
import com.avoqado.pos.core.presentation.components.ToolbarWithIcon
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.features.management.presentation.splitProduct.model.SplitByProductViewState

@Composable
fun SplitByProductScreen(viewmodel: SplitByProductViewModel) {
    val state by viewmodel.tableDetail.collectAsStateWithLifecycle()

    SplitByProductContent(
        state = state,
        onNavigateBack = {
            viewmodel.navigateBack()
        },
        onTapToPay = {
            viewmodel.navigateToPayment()
        },
        onItemSelected = {
            viewmodel.onProductItemTapped(it)
        },
    )
}

@Composable
fun SplitByProductContent(
    state: SplitByProductViewState,
    onNavigateBack: () -> Unit,
    onTapToPay: () -> Unit,
    onItemSelected: (Product) -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        ToolbarWithIcon(
            title = "Queda por pagar: \$${state.totalPending}",
            iconAction =
                IconAction(
                    flowStep = FlowStep.NAVIGATE_BACK,
                    context = context,
                    iconType = IconType.BACK,
                ),
            onAction = {
                onNavigateBack()
            },
            showSecondIcon = false,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        count = state.products.size,
                        key = { index -> state.products[index].id },
                    ) { index ->
                        val product = state.products[index]
                        val isPaid = state.paidProducts.contains(product.id)
                        SelectableItemRow(
                            label = product.name,
                            trailingLabel =
                                if (isPaid) {
                                    "Pagado"
                                } else {
                                    "\$${
                                        product.totalPrice.toString().toAmountMx()
                                    }"
                                },
                            isSelected = product.id in state.selectedProducts,
                            onItemTap = if (isPaid) null else onItemSelected,
                            data = product,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val buttonLabel =
                if (state.totalQuantitySelected == 0) {
                    "Selecciona productos"
                } else {
                    "Pagar ${state.totalQuantitySelected} productos • \$${state.totalSelected}"
                }

            MainButton(
                text = buttonLabel,
                enableButton = state.totalQuantitySelected > 0,
                modifier = Modifier.fillMaxWidth(),
                onClickR = onTapToPay,
            )
        }
    }
}

@Urovo9100DevicePreview
@Composable
fun PreivewSplitByProduct() {
    AvoqadoTheme {
        SplitByProductContent(
            state =
                SplitByProductViewState(
                    products =
                        listOf(
                            Product(
                                id = "1",
                                name = "Bagel con queso crema",
                                quantity = 1.0,
                                price = 70.0,
                                totalPrice = 70.0,
                            ),
                            Product(
                                id = "2",
                                name = "Pizza margherita",
                                quantity = 1.0,
                                price = 150.0,
                                totalPrice = 150.0,
                            ),
                            Product(
                                id = "3",
                                name = "Pan Francés",
                                quantity = 1.0,
                                price = 2.1,
                                totalPrice = 2.1,
                            ),
                            Product(
                                id = "4",
                                name = "Pizza Hawaiana",
                                quantity = 1.0,
                                price = 160.0,
                                totalPrice = 160.0,
                            ),
                        ),
                    selectedProducts = listOf("2", "3"),
                    totalPending = "777.00",
                ),
            onTapToPay = {},
            onNavigateBack = {},
            onItemSelected = {},
        )
    }
}
