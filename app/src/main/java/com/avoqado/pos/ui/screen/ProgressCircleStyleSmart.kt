package com.avoqado.pos.ui.screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.unit.Dp
import com.avoqado.pos.ui.theme.dp_10
import com.avoqado.pos.ui.theme.dp_2
import com.avoqado.pos.ui.theme.dp_25
import com.avoqado.pos.ui.theme.dp_95
import com.avoqado.pos.ui.theme.primary

data class ProgressCircleStyleSmart (
    val color: Color = primary,
    val backgroundColor: Color = Transparent,
    val strokeWidth: Dp = dp_10,
    val strokeBackgroundWidth: Dp = dp_10,
    val direction: ProgressDirectionSmart = ProgressDirectionSmart.RIGHT,
    val rounderBorder: Boolean = true,
    val durationInMilliSecond: Int = 800,
    val size: Dp = dp_95
) {
    companion object {
        val search = ProgressCircleStyleSmart(
            color = primary,
            backgroundColor = Transparent,
            strokeWidth = dp_2,
            strokeBackgroundWidth = dp_2,
            direction = ProgressDirectionSmart.RIGHT,
            rounderBorder = true,
            durationInMilliSecond = 800,
            size = dp_25
        )
    }
}

enum class ProgressDirectionSmart{
    RIGHT, LEFT
}