package com.avoqado.pos.features.authorization.presentation.signIn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.core.presentation.components.CustomKeyboard
import com.avoqado.pos.core.presentation.components.CustomKeyboardType
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview

@Composable
fun SignInScreen(signInViewModel: SignInViewModel) {
    val otp by signInViewModel.otp.collectAsStateWithLifecycle()

    SignInContent(
        onNext = {
            signInViewModel.updateOtp(it)
        },
        onDeleteChar = {
            signInViewModel.deleteDigit()
        },
        otp = otp,
    )
}

@Composable
fun PinRow(otp: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 24.dp),
    ) {
        for (i in 1..4) {
            Text(
                text = "•",
                style =
                    MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color =
                            if (i <= otp.length) {
                                Color.Black
                            } else {
                                Color.LightGray
                            },
                    ),
                modifier = Modifier.padding(horizontal = 8.dp),
            )
        }
    }
}

@Composable
fun SignInContent(
    onNext: (String) -> Unit = {},
    onDeleteChar: () -> Unit = {},
    otp: String = "",
) {
    val otpValue = remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa tu PIN",
            style =
                MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
            modifier = Modifier.padding(bottom = 24.dp),
        )

        PinRow(otp)

        CustomKeyboard(
            modifier = Modifier.fillMaxWidth(),
            type = CustomKeyboardType.simple,
            onNumberClick = {
                if (it == -3) {
                    onNext("")
                } else {
                    onNext(it.toString())
                }
            },
            onConfirmClick = {},
            onBackspaceClick = {
                onDeleteChar()
            },
        )
    }
}

@Urovo9100DevicePreview
@Composable
fun SignInContentPreview() {
    AvoqadoTheme {
        SignInContent()
    }
}
