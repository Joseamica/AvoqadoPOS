package com.avoqado.pos.ui.screen

import androidx.compose.ui.graphics.Color
import com.avoqado.pos.core.presentation.theme.primary
import com.avoqado.pos.core.presentation.theme.textColor
import com.avoqado.pos.core.presentation.theme.textlightGrayColor

data class TextFieldType(
    val textFieldStyle: TextFieldStyle
) {
    companion object {
        val default = TextFieldType(
            textFieldStyle = TextFieldStyle(
                onFocus = primary,
                offFocus = textlightGrayColor,
                textColor = textColor,
                hintColor = textColor
            ),
        )

        val disable = TextFieldType(
            textFieldStyle = TextFieldStyle(
                onFocus = Color(0xFFC7C7C7),
                offFocus = Color(0xFFC7C7C7),
                textColor = Color.White,
                hintColor = Color(0xFFC7C7C7)
            )
        )
    }
}

data class TextFieldStyle(
    val onFocus: Color,
    val offFocus: Color,
    val textColor: Color,
    val hintColor: Color
)