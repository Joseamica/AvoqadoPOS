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
import androidx.compose.runtime.livedata.observeAsState
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
import com.avoqado.pos.ACQUIRER_NAME
import com.avoqado.pos.COUNTRY_CODE
import com.avoqado.pos.R
import com.avoqado.pos.core.presentation.components.RequestPermissions
import com.avoqado.pos.core.presentation.theme.primary
import com.avoqado.pos.core.presentation.theme.textColor
import com.avoqado.pos.core.presentation.theme.textlightGrayColor

import com.avoqado.pos.ui.screen.ProgressCircleSmart
import com.menta.android.common_cross.util.StatusType
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    externalTokenData: ExternalTokenData,
    masterKeyData: MasterKeyData,
) {
    val externalToken by externalTokenData.getExternalToken.observeAsState()
    val masterKey by masterKeyData.getMasterKey.observeAsState()
    val isConfiguring by viewModel.isConfiguring.collectAsStateWithLifecycle()

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
                SplashViewModel.START_CONFIG -> {
                    viewModel.venueInfo?.let { venue ->
                        val apiKey = venue.menta?.let { mentaVenue ->
                            if (viewModel.operationPreference) {
                                mentaVenue.apiKeyA
                            } else {
                                mentaVenue.apiKeyB
                            }
                        }

                        apiKey?.let { key ->
                            externalTokenData.getExternalToken(key)
                        }
                    }
                }

                SplashViewModel.GET_MASTER_KEY -> {
                    viewModel.venueInfo?.let { venue ->
                        val merchantId = venue.menta?.let { mentaVenue ->
                            if (viewModel.operationPreference) {
                                mentaVenue.merchantIdA
                            } else {
                                mentaVenue.merchantIdB
                            }
                        }

                        if (merchantId != null) {
                            masterKeyData.loadMasterKey(
                                merchantId = merchantId,
                                acquirerId = ACQUIRER_NAME,
                                countryCode = COUNTRY_CODE,
                            )
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = externalToken) {
        Timber.i("externalToken: ${externalToken?.idToken}")
        externalToken?.let { token ->
            if (token.status.statusType != StatusType.ERROR) {
                Timber.i("Get token SUCCESS")
                viewModel.storePublicKey(token.idToken, token.tokenType)
            } else {
                Timber.i("Get token ERROR: ${token.status.message}")
            }
        }
    }

    LaunchedEffect(key1 = masterKey) {
        masterKey?.let { key ->
            viewModel.handleMasterKey(key.secretsList)
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

            // Show loading indicator when configuring
            if (isConfiguring) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
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

    RequestPermissions(
        onPermissionResult = { isGranted ->
            if (isGranted) {
                viewModel.initSplash()
            }
        }
    )
}
