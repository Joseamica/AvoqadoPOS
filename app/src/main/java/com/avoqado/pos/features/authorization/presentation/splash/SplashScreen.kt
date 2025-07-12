package com.avoqado.pos.features.authorization.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.RequestPermissions
import com.avoqado.pos.core.presentation.theme.primary
import com.avoqado.pos.core.presentation.theme.textColor
import com.avoqado.pos.core.presentation.theme.textlightGrayColor

import com.avoqado.pos.ui.screen.ProgressCircleSmart
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
) {
    val isConfiguring by viewModel.isConfiguring.collectAsStateWithLifecycle()
    val isTpvNotFound by viewModel.isTpvNotFound.collectAsStateWithLifecycle()
    val retryCountdown by viewModel.retryCountdown.collectAsStateWithLifecycle()

    // Animation states
    var showLogo by remember { mutableStateOf(false) }
    var showText by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (showLogo) 1f else 0.5f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "logoScale",
    )

    // Launch animation sequence
    LaunchedEffect(key1 = Unit) {
        showLogo = true
        delay(400)
        showText = true
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collectLatest {
            when (it) {
                SplashViewModel.START_CONFIG -> {}

                SplashViewModel.GET_MASTER_KEY -> {}
            }
        }
    }


    // Splash screen UI with animations
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(textlightGrayColor),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Logo with scale animation
            Image(
                painter = painterResource(id = R.drawable.isotipo),
                contentDescription = "Avoqado Logo",
                modifier =
                    Modifier
                        .size(200.dp)
                        .scale(logoScale),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated text appearance
            AnimatedVisibility(
                visible = showText,
                enter = fadeIn(animationSpec = tween(800)),
            ) {
                Text(
                    text = "Avoqado TPV",
                    fontFamily = FontFamily(Font(R.font.mulish_bold)),
                    fontSize = 35.sp,
                    color = primary,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Show loading indicator when configuring or error message when TPV not found
            if (isConfiguring || isTpvNotFound) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (isTpvNotFound) {
                        Text(
                            text = "La terminal aun no esta dada de alta",
                            fontFamily = FontFamily(Font(R.font.mulish_bold)),
                            fontSize = 20.sp,
                            color = textColor,
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Reintentando en $retryCountdown segundos...",
                            fontFamily = FontFamily(Font(R.font.mulish_regular)),
                            fontSize = 16.sp,
                            color = textColor,
                        )
                    } else {
                        ProgressCircleSmart()

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(id = R.string.whileInitProcessFinishes),
                            fontFamily = FontFamily(Font(R.font.mulish_regular)),
                            fontSize = 18.sp,
                            color = textColor,
                        )
                    }
                }
            }
        }
    }

    RequestPermissions(
        onPermissionResult = { isGranted ->
            if (isGranted) {
                viewModel.initSplash()
            }
        }
    )
}
