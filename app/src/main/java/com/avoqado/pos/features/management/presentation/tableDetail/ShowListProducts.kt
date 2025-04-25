package com.avoqado.pos.features.management.presentation.tableDetail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.utils.Utils

@Composable
fun ProductsScreen(
    listProducts: List<Product>,
    // totalPrice: MutableState<Double>
) {
    var selectedProducts by remember { mutableStateOf<List<Product>>(emptyList()) }
    val ctx = LocalContext.current
    val totalPriceLocal by remember(selectedProducts) {
        derivedStateOf { selectedProducts.sumOf { it.price } }
    }
    // LaunchedEffect(totalPriceLocal) { totalPrice.value = totalPriceLocal }
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(
                count = listProducts.size,
                key = { index -> listProducts[index].id },
            ) { index ->
                val product = listProducts[index]
                AVProductItem(
                    product = product,
                    isSelected = product in selectedProducts,
                    onItemClicked = {
                        selectedProducts =
                            if (product in selectedProducts) {
                                selectedProducts - product
                            } else {
                                selectedProducts + product
                            }
                    },
                )
            }
        }
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(50.dp),
        ) {
            Utils.UtilButtonView(
                text = "Pagar ${selectedProducts.size} productos $totalPriceLocal",
                onClickR = {},
                color = Color.Black,
                textColor = Color.White,
            )
        }
    }
}

@Composable
fun AVProductItem(
    product: Product,
    isSelected: Boolean,
    onItemClicked: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable {
                    onItemClicked()
                }.border(
                    width = 2.dp,
                    color = if (isSelected) Color.Black else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                ),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(0.6f),
            ) {
                Text(
                    text = product.name,
                    color = Color.Gray,
                    fontSize = 12.sp,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(0.4f),
            ) {
                Utils.MmUtlAmountTextViewV2(
                    amount = product.price.toInt().toString(),
                    currencyType = "MXN",
                    baseSize = 16,
                    maxDecimal = 2,
                )
            }
        }
    }
}
