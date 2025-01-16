package com.avoqado.pos.screens.tableDetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import com.avoqado.pos.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.screens.tableDetail.model.Product
import com.avoqado.pos.util.Utils


@Composable
fun ProductsScreen(listProducts: List<Product>) {

    LazyColumn(modifier = Modifier.fillMaxSize()) {

        items(listProducts.size) { index ->

            AVProductItem(product = listProducts[index], modifier = Modifier)

        }
    }
}

@Composable
fun AVProductItem(
    product: Product,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(0.7f)
            ) {

                Spacer(modifier = Modifier.width(8.dp))

                Column(verticalArrangement = Arrangement.Center) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = "Menu",
                        modifier = Modifier.size(18.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = product.name,
                            color = Color.Gray,
                            fontSize = 12.sp,
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Utils.MmUtlAmountTextViewV2(
                            amount = product.price.toInt().toString(),
                            currencyType = "MXN",
                            baseSize = 15,
                            maxDecimal = 2,
                        )
                    }
                }
                if (product.price.toInt() != 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Utils.MmUtlAmountTextViewV2(
                            amount = product.price.toInt().toString(),
                            currencyType = "MXN",
                            baseSize = 12,
                            maxDecimal = 2,
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.weight(0.06f),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_arrow_forward_ios_24),
                    contentDescription = "Menu",
                    modifier = Modifier
                        .size(24.dp),
                )
            }
        }
    }
}