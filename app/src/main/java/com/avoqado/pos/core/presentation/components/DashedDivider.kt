package com.avoqado.pos.core.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashedDivider(
    color: Color = Color.LightGray,
    width: Dp = 1.dp,
    padding: PaddingValues = PaddingValues(0.dp),
) {
    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(width)
                .padding(padding),
    ) {
        val dashWidth = 10f
        val dashGap = 10f
        drawLine(
            color = color,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap), 0f),
        )
    }
}
