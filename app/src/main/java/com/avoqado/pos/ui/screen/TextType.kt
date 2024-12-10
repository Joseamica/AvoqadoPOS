package com.avoqado.pos.ui.screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

data class TextType(
    val textStyle: TextStyle
) {
    companion object {
        val textBoldBlack = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Bold
            )
        )
        val textBoldWhite = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Bold
            )
        )
        val textBoldGreen = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Bold
            )
        )
        val textBoldGray = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Bold
            )
        )

        val textNormalBlack = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Normal
            ),
        )
        val textNormalWhite = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Normal
            )
        )
        val textNormalGreen = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Normal
            )
        )

        val textNormalGray = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Normal
            )
        )

        val textLightBlack = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Light
            )
        )
        val textLightWhite = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Light
            )
        )
        val textLightGreen = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Light
            )
        )
        val textLightGray = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Light
            )
        )
        val textTitleBlack = TextType(
            textStyle = TextStyle(
                fontColor = Color(0xFF1A72DD),
                fontWeight = FontWeight.Light
            )
        )
    }
}

data class TextStyle(
    val fontFamily: FontFamily = Mulish,
    val fontWeight: FontWeight,
    val fontColor: Color
)