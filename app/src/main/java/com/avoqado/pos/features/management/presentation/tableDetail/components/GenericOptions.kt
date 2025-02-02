package com.avoqado.pos.features.management.presentation.tableDetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R

@Composable
fun GenericOptionsUI(
    onClickProducts: () -> Unit,
    onClickPeople: () -> Unit = {},
    onClickCustom: () -> Unit = {}
) {
    Column(
        modifier = Modifier,
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
            Spacer(modifier = Modifier.width(10.dp))
            GenericOptionCard(
                icon = painterResource(R.drawable.icon_edit),
                title = "Monto",
                onClick = {
                    onClickCustom()
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun GenericOptionCard(
    icon: Painter,
    title: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    iscustom: Boolean = false
) {
    Card(
        modifier = modifier
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (iscustom) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icon_edit),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
            } else {
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
}
