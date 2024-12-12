package com.avoqado.pos.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
fun Text(
    modifier: Modifier = Modifier,
    text: String = "DEFAULT",
    textStyle: com.avoqado.pos.ui.screen.TextStyle = TextType.textNormalBlack.textStyle,
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign = TextAlign.Start,
    shimmer: Boolean = false
) {
    if (shimmer){
        ShimmerText(
            modifier = modifier
        )
        return
    }

    androidx.compose.material.Text(
        modifier = modifier,
        text = text,
        fontFamily = textStyle.fontFamily,
        fontSize = fontSize,
        fontWeight = textStyle.fontWeight,
        color = textStyle.fontColor,
        textAlign = textAlign
    )
}