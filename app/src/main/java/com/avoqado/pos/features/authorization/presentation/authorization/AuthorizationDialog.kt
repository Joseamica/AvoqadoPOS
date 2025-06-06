package com.avoqado.pos.features.authorization.presentation.authorization

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.features.authorization.presentation.splash.SplashViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AuthorizationDialog(
    viewModel: AuthorizationViewModel,
) {
    val isConfiguring by viewModel.isConfiguring.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collectLatest {
            when (it) {
                SplashViewModel.START_CONFIG -> {}

                SplashViewModel.GET_MASTER_KEY -> {}
            }
        }
    }

    Dialog(onDismissRequest = { /*TODO*/ }) {
        Box(
            modifier =
                Modifier
                    .size(200.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Actualizando token", style = MaterialTheme.typography.body1)
            }
        }
    }
}
