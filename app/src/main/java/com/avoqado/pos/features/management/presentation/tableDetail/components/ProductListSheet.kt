package com.avoqado.pos.features.management.presentation.tableDetail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.model.Product
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.theme.buttonGrayColor
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.core.presentation.utils.toAmountMx
import com.avoqado.pos.ui.screen.ProductRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListSheet(
    onDismissRequest: () -> Unit,
    onPrint: () -> Unit = {},
    products: List<Product>,
    modalSheetState: SheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        ),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(),
        sheetState = modalSheetState,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Productos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )

                Button(
                    onClick = onPrint,
                    modifier =
                        Modifier
                            .height(36.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = buttonGrayColor,
                        ),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_print_24),
                        contentDescription = "",
                        tint = buttonGrayColor,
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Imprimir",
                        color = buttonGrayColor,
                        fontSize = 16.sp,
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(
                    items = products,
                    key = { it.id },
                ) { product ->
                    ProductRow(
                        name = product.name,
                        price = product.price.toString(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color.Gray.copy(alpha = 0.2f), thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Total",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "\$${products.sumOf { it.totalPrice }.toString().toAmountMx()}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier.padding(
                            vertical = 16.dp,
                        ),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Urovo9100DevicePreview
@Composable
fun PreviewProductListSheet() {
    AvoqadoTheme {
        ProductListSheet(
            modalSheetState =
                rememberStandardBottomSheetState(
                    initialValue = SheetValue.Expanded,
                ),
            onDismissRequest = {},
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
        )
    }
}
