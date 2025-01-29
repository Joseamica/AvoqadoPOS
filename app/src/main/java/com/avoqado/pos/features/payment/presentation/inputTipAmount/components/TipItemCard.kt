package com.avoqado.pos.features.payment.presentation.inputTipAmount.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.ui.theme.chipContainerColor
import com.avoqado.pos.ui.theme.chipContentColor

@Composable
fun TipItemCard(
    percentage: String,
    amount: String,
    isPopular: Boolean,
    onClickTip: () -> Unit = {}
) {
    Box {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(120.dp)
                .padding(top = 8.dp)
                .clickable { onClickTip() }
                .background(
                    color = if (isPopular) Color.Black else Color.White,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = percentage,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (!isPopular) Color.Black else Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = amount,
                    fontSize = 14.sp,
                    color = if (!isPopular) Color.Black else Color.White
                )
            }
        }

        if (isPopular) {
            Text(
                text = "Popular",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = chipContentColor,
                modifier = Modifier
                    .background(chipContainerColor, shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }

}