package com.avoqado.pos.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.R
import com.avoqado.pos.ui.theme.textColor


@Composable
fun TextFieldAmount(
    modifier: Modifier = Modifier,
    textFieldStyle: TextFieldStyle = TextFieldType.default.textFieldStyle,
    textFieldState: MutableState<TextFieldState>,
    onTextChange: (String) -> Unit,
    shimmer: Boolean = false,
    clickOnDone: () -> Unit = {}
) {
    if (shimmer) {
        ShimmerTextFieldAmount(
            modifier = modifier
        )
        return
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = textFieldState.value.textFieldValue,
        onValueChange = {
            textFieldState.value.textFieldValue = it.copy(selection = TextRange(it.text.length))
            onTextChange(it.text)
        },
        modifier = modifier
            .onFocusChanged {
                val current = textFieldState.value
                textFieldState.value = current.copy(
                    isFocus = it.isFocused
                )
            },
        textStyle = TextStyle(
            color = textColor,
            textAlign = TextAlign.End,
            fontSize = 50.sp
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = {
            clickOnDone()
            keyboardController?.hide()
        }),
        decorationBox = { innerText ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    innerText()
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = 2.dp)
                        .background(
                            color =
                            if (textFieldState.value.isFocus)
                                textFieldStyle.onFocus
                            else
                                textFieldStyle.offFocus
                        )
                )
                Row {
                    Icon(
                        modifier = Modifier
                            .alpha(if (textFieldState.value.error.isEmpty()) 0f else 1f)
                            .width(15.dp)
                            .height(15.dp),
                        painter = painterResource(id = R.drawable.ic_info),
                        tint = Color.Unspecified,
                        contentDescription = "description"
                    )
                    Text(
                        modifier = Modifier
                            .alpha(if (textFieldState.value.error.isEmpty()) 0f else 1f),
                        textStyle = TextType.textNormalGray.textStyle,
                        text = textFieldState.value.error,
                        fontSize = 15.sp,
                    )
                }
            }
        }
    )
}