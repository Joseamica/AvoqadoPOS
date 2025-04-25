package com.avoqado.pos.ui.screen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerTextFieldAmount(modifier: Modifier = Modifier) {
    Shimmer(
        modifier =
            modifier
                .fillMaxWidth()
                .height(60.dp),
    )
}
