package com.avoqado.pos.features.management.presentation.tableDetail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.core.domain.models.SplitType

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenericOptionsUI(
    splitType: SplitType?,
    onClickProducts: () -> Unit,
    onClickPeople: () -> Unit = {},
    onClickCustom: () -> Unit = {}
) {
    val items = splitType?.let {
        when(it) {
            SplitType.CUSTOMAMOUNT -> null
            SplitType.FULLPAYMENT -> emptyList()
            else -> listOf(it)
        }

    } ?: listOf(SplitType.PERPRODUCT, SplitType.EQUALPARTS, SplitType.CUSTOMAMOUNT)

    Row (
        modifier = Modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach {
            GenericOptionCard(
                type = it,
                onClick = {
                    when(it) {
                        SplitType.PERPRODUCT -> onClickProducts()
                        SplitType.EQUALPARTS -> onClickPeople()
                        SplitType.CUSTOMAMOUNT -> onClickCustom()
                        SplitType.FULLPAYMENT -> {}
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RowScope.GenericOptionCard(
    type: SplitType,
    onClick: () -> Unit
) {
    when (type) {
//        SplitType.PERPRODUCT -> GenericOptionCard(
//            icon = painterResource(R.drawable.icon_products),
//            title = "Productos",
//            onClick = onClick,
//            modifier = Modifier.weight(1f)
//        )
//
//        SplitType.EQUALPARTS -> GenericOptionCard(
//            icon = painterResource(R.drawable.icon_people),
//            title = "Personas",
//            onClick = onClick,
//            modifier = Modifier.weight(1f)
//        )

        SplitType.CUSTOMAMOUNT -> GenericOptionCard(
            icon = painterResource(R.drawable.icon_edit),
            title = "Monto Personalizada",
            onClick = onClick,
            modifier = Modifier.weight(1f)
        )

        else -> {}
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
                .height(if (iscustom) 70.dp else 100.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (iscustom) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
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
                        color = Color.Black,
                        textAlign = TextAlign.Center
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
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
