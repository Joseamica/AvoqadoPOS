package com.avoqado.pos.views

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avoqado.pos.core.presentation.components.ToolbarWithIcon
import com.avoqado.pos.core.presentation.model.FlowStep
import com.avoqado.pos.core.presentation.model.IconAction
import com.avoqado.pos.core.presentation.model.IconType
import com.avoqado.pos.ui.screen.PrimaryButton

class InputMailActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(
                topBar = {
                    ToolbarWithIcon(
                        "Ticket digital",
                        IconAction(
                            flowStep = FlowStep.GO_TO_MENU,
                            context = this,
                            iconType = IconType.BACK,
                        ),
                    )
                },
                content = { RegisterScreenContent() },
            )
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    fun RegisterScreenContent() {
        var textValue by remember { mutableStateOf(TextFieldValue()) }
        val focusRequester = remember { FocusRequester() }

        Column(modifier = Modifier.padding(vertical = 80.dp)) {
            TextField(
                value = textValue.text,
                onValueChange = { textValue = TextFieldValue(it) },
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .height(56.dp)
                    .background(
                        color = Color(0x0D000000),
                        shape = RoundedCornerShape(size = 16.dp),
                    ).focusRequester(focusRequester),
                label = {
                    Text(
                        text = "Ingrese el correo",
                        style =
                            TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight(400),
                                color = Color(0xFF2A3256),
                            ),
                    )
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                    ),
            )
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
                    text = "Enviar",
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                            .height(57.dp)
                            .align(Alignment.End),
                    onClick = {
                        Log.i("", "ingreso: ${textValue.text}")
                        goToSendTicket(textValue.text)
                    },
                )
            }
        }

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }

    private fun goToSendTicket(email: String) {
        Log.i("", "email: $email")
        if (isValidEmail(email)) {
            val intent = Intent(this, SendTicketActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        if (email.isNotEmpty()) {
            val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            return email.matches(emailRegex)
        }
        return false
    }
}
