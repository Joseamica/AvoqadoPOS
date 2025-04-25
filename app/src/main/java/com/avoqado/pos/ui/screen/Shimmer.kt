package com.avoqado.pos.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
fun Shimmer(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .placeholder(
                    visible = true,
                    color = Color(0xFF1A1A1A),
                    shape = RoundedCornerShape(4),
                    highlight =
                        PlaceholderHighlight.fade(
                            // highlightColor = Gray2
                        ),
                ),
    )
}
