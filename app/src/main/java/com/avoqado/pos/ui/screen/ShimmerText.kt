package com.avoqado.pos.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerText(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .wrapContentHeight()
    ) {
        Shimmer(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
                .height(22.dp)
        )

        Shimmer(
            modifier = Modifier
                .width(200.dp)
                .height(22.dp)
        )
    }
}