package com.avoqado.pos.ui.screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.dp
import com.avoqado.pos.core.presentation.viewmodel.InputAmountViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MenuScreen(viewModel: InputAmountViewModel) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TextFieldAmount(
                modifier =
                    Modifier
                        .layoutId("textFieldAmount")
                        .padding(horizontal = 20.dp)
                        .pointerInput(Unit) {
                            focusRequester.requestFocus()
                        }.focusRequester(focusRequester),
                textFieldState = viewModel.textFieldAmount,
                onTextChange = {
                    viewModel.formatAmount(it)
                },
            )
        }
    }
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PrimaryButton(
                text = "Continuar",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                        .height(57.dp)
                        .align(Alignment.End),
                onClick = {
                    Log.i("", "ingreso: ${viewModel.textFieldAmount.value.textFieldValue.text}")
                    viewModel.isValidAmount(viewModel.textFieldAmount.value.textFieldValue.text)
                },
            )
        }
    }
}
