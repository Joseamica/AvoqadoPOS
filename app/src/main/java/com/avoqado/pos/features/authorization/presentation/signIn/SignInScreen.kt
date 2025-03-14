package com.avoqado.pos.features.authorization.presentation.signIn

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avoqado.pos.core.presentation.components.CustomKeyboard
import com.avoqado.pos.core.presentation.components.CustomKeyboardType
import com.avoqado.pos.core.presentation.components.OtpInputField
import com.avoqado.pos.core.presentation.components.pxToDp
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.features.authorization.presentation.signIn.SignInViewModel.Companion.codeLength
import kotlin.math.sin

@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel
) {
    SignInContent(
        onNext = {
            signInViewModel.goToNextScreen(it)
        }
    )
}

@Composable
fun SignInContent(
    onNext: (String) -> Unit = {}
) {
    val otpValue = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa tu PIN",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {

           for (i in 1..4) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (i <= otpValue.value.length) {
                            Color.Black
                        } else {
                            Color.LightGray
                        }
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
           }

        }

        CustomKeyboard(
            modifier = Modifier.fillMaxWidth(),
            type = CustomKeyboardType.simple,
            onNumberClick = {
                if (it == -3) {
                    otpValue.value = ""
                } else {
                    otpValue.value += it
                    if (otpValue.value.length == 4) {
                        onNext(otpValue.value)
                    }
                }

            },
            onConfirmClick = {},
            onBackspaceClick = {
                if (otpValue.value.isNotEmpty()) {
                    otpValue.value = otpValue.value.dropLast(1)
                }
            }
        )
    }
}

@Composable
fun SingleOtpBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        maxLines = 1,
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape),
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number
        ),
        // Hide typed digit if you want a “password” style
        visualTransformation = PasswordVisualTransformation()
    )
}

@Urovo9100DevicePreview
@Composable
fun SignInContentPreview() {
    AvoqadoTheme {
        SignInContent()
    }
}