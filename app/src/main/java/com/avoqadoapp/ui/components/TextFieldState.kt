package com.avoqadoapp.ui.components

import androidx.compose.ui.text.input.TextFieldValue

data class TextFieldState(
    var textFieldValue: TextFieldValue = TextFieldValue(
        text = ""
    ),
    val hint: String = "",
    val error: String = "",
    val enable: Boolean = true,
    val isFocus: Boolean = false,
    val notifyErrorState: () -> Unit
)
