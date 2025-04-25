package com.avoqado.pos.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.avoqado.pos.core.presentation.theme.dp_5

object ConstCircle {
    const val startAngle = 0f
    const val endAngle = 360f
    const val angle = 300f
}

@Composable
fun ProgressCircleSmart(
    // modifier: Modifier = Modifier,
    circularProgressStyle: ProgressCircleStyleSmart = ProgressCircleStyleSmart(),
) {
    val animatedRestart =
        animatedRestart(
            direction = circularProgressStyle.direction,
            durationInMilliSecond = circularProgressStyle.durationInMilliSecond,
        )

    Box(
        modifier =
            Modifier
                .size(circularProgressStyle.size)
                .padding(dp_5),
    ) {
        Canvas(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            drawArc(
                startAngle = ConstCircle.startAngle,
                sweepAngle = ConstCircle.endAngle,
                color = circularProgressStyle.backgroundColor,
                useCenter = false,
                size = size,
                style =
                    Stroke(
                        width = circularProgressStyle.strokeBackgroundWidth.toPx(),
                    ),
            )

            drawArc(
                color = circularProgressStyle.color,
                startAngle = animatedRestart,
                sweepAngle = ConstCircle.angle,
                useCenter = false,
                size = size,
                style =
                    Stroke(
                        width = circularProgressStyle.strokeWidth.toPx(),
                        cap = if (circularProgressStyle.rounderBorder) StrokeCap.Round else StrokeCap.Square,
                    ),
            )
        }
    }
}

@Composable
fun animatedRestart(
    direction: ProgressDirectionSmart,
    durationInMilliSecond: Int,
): Float =
    rememberInfiniteTransition()
        .animateFloat(
            initialValue = ConstCircle.startAngle,
            targetValue = if (direction == ProgressDirectionSmart.RIGHT) ConstCircle.endAngle else -ConstCircle.endAngle,
            animationSpec =
                infiniteRepeatable(
                    tween(
                        durationInMilliSecond,
                        easing = LinearEasing,
                    ),
                ),
        ).value
