package com.avoqado.pos.ui.screen

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R


val Mulish = FontFamily(
    Font(R.font.mulish_bold, FontWeight.Bold),
    Font(R.font.mulish_light, FontWeight.Light),
    Font(R.font.mulish_regular, FontWeight.Normal)
)

val Typography = Typography(
    body1 = TextStyle(
        fontFamily = Mulish,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)