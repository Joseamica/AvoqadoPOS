package com.avoqado.pos.features.authorization.presentation.signIn

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avoqado.pos.core.presentation.theme.AvoqadoTheme
import com.avoqado.pos.core.presentation.utils.Urovo9100DevicePreview
import com.avoqado.pos.features.authorization.presentation.signIn.SignInViewModel.Companion.codeLength
import kotlin.math.sin

@Composable
fun SignInScreen(
    signInViewModel: SignInViewModel
) {

    val otp by signInViewModel.otp.collectAsStateWithLifecycle()
    val email by signInViewModel.email.collectAsStateWithLifecycle()

    SignInContent(
        otp = otp,
        updateOtp = signInViewModel::setOtp,
        email = email,
        onUpdateEmail = signInViewModel::setEmail,
        onNext = {
            signInViewModel.goToNextScreen()
        }
    )
}

@Composable
fun SignInContent(
    otp: List<String>,
    email: String,
    onUpdateEmail: (String) -> Unit,
    updateOtp: (String,Int) -> Unit,
    onNext: ()-> Unit = {}
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextField(
            value = email,
            onValueChange = onUpdateEmail,
            label = {
                Text("Email")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ingresa tu passcode",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Row of 6 circular “boxes” for OTP
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            for (i in 0 until SignInViewModel.codeLength) {
                SingleOtpBox(
                    value = otp[i],
                    onValueChange = { newValue ->
                        // Only accept digits
                        if (newValue.matches(Regex("^\\d?\$"))) {
                            // Replace the character at index i
                            updateOtp(newValue, i)
                        }
                    }
                )
            }
        }

        // “Confirm” button
        Button(
            onClick = onNext,
            enabled = otp.all { it.isNotEmpty() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray.copy(alpha = 0.2f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Confirmar",
                style = MaterialTheme.typography.titleMedium
            )
        }
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
fun SignInContentPreview(){
    AvoqadoTheme {
        SignInContent(
            otp = List(codeLength) { "" },
            email = "test@avoqado.io",
            onUpdateEmail = {},
            updateOtp = {_, _ ->}
        )
    }
}