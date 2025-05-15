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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.ACQUIRER_NAME
import com.avoqado.pos.COUNTRY_CODE
import com.avoqado.pos.features.authorization.presentation.splash.SplashViewModel
import com.avoqado.pos.merchantApiKey
import com.avoqado.pos.merchantId
import com.menta.android.common_cross.util.StatusType
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun AuthorizationDialog(
    viewModel: AuthorizationViewModel,
    externalTokenData: ExternalTokenData,
    masterKeyData: MasterKeyData,
) {
    val externalToken by externalTokenData.getExternalToken.observeAsState()
    val masterKey by masterKeyData.getMasterKey.observeAsState()
    val isConfiguring by viewModel.isConfiguring.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.events.collectLatest {
            when (it) {
                SplashViewModel.START_CONFIG -> {
                    externalTokenData.getExternalToken(merchantApiKey)
                }

                SplashViewModel.GET_MASTER_KEY -> {
                    // Inyección de llaves
                    masterKeyData.loadMasterKey(
                        merchantId = merchantId, // TODO el cliente debe conocer su merchantId, es diferente por cada comercio que tenga
                        acquirerId = ACQUIRER_NAME,
                        countryCode = COUNTRY_CODE,
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = externalToken) {
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
