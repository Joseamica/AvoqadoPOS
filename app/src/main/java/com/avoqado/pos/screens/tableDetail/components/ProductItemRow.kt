package com.avoqado.pos.screens.tableDetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.avoqado.pos.CURRENCY_LABEL
import com.avoqado.pos.screens.tableDetail.model.Product
import com.avoqado.pos.ui.theme.DemoandroidsdkmentaTheme

@Composable
fun ProductItemRow(product: Product) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.name,
                maxLines = 1
            )
            Text(
                text = "$CURRENCY_LABEL ${product.formattedPrice}"
            )
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(48.dp)
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp),
                )
                .border(width = 1.dp, shape = RoundedCornerShape(8.dp), color = Color.DarkGray)

        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = product.quantity.toString()
            )
        }

        Text(
            text = "$CURRENCY_LABEL ${product.formattedTotalPrice}"
        )
    }
}

@Preview
@Composable
fun PreviewProductItemRow() {
    DemoandroidsdkmentaTheme {
        Surface {
            ProductItemRow(
                product = Product(
                    id = "",
                    name = "Spicy instant noodles",
                    quantity = 3,
                    price = 3.49,
                    totalPrice = 14.0
                )
            )
        }
    }
}