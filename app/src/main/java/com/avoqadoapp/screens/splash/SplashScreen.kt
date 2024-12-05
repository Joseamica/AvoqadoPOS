package com.avoqadoapp.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqadoapp.R
import com.avoqadoapp.ui.theme.textColor
import com.avoqadoapp.ui.theme.textlightGrayColor
import timber.log.Timber

@Composable
fun SplashScreen( viewModel: SplashViewModel ) {

    val state: SplashViewState by viewModel.state.collectAsStateWithLifecycle()
    val externalToken by viewModel.externalTokenData.getExternalToken.observeAsState()
    val masterToken by viewModel.masterKeyData.getMasterKey.observeAsState()

    LaunchedEffect(key1 = externalToken) {
        externalToken?.let {
            viewModel.submitAction(SplashAction.OnExternalToken(it))
        }
    }

    LaunchedEffect(key1 = masterToken) {
        masterToken?.let {
            viewModel.submitAction(SplashAction.OnMasterToken(it))
        } ?: run {
            Timber.d("Inyección de llaves FAILED")
        }
    }

    SplashContent(action = viewModel.submitAction, state = state)
}

@Composable
fun SplashContent(
    action: (SplashAction) -> Unit,
    state: SplashViewState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(textlightGrayColor)
    ) {

        Text(
            text = "Avoqado App",
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )

        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(120.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
            Column(
                modifier = Modifier
                    .padding(start = 24.dp, end = 24.dp, bottom = 50.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.wait_payment),
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(id = R.string.whileInitProcessFinishes),
                    fontSize = 25.sp,
                    color = textColor,
                )
            }
        }

    }
}