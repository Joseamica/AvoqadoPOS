package com.avoqado.pos.features.authorization.presentation.splash

import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.avoqado.pos.ACQUIRER_NAME
import com.avoqado.pos.COUNTRY_CODE
import com.avoqado.pos.R
import com.avoqado.pos.merchantApiKey
import com.avoqado.pos.merchantId
import com.avoqado.pos.ui.screen.ProcessingOperationScreen
import com.avoqado.pos.views.InitActivity.Companion.TAG
import com.menta.android.common_cross.util.StatusType
import com.menta.android.core.viewmodel.ExternalTokenData
import com.menta.android.core.viewmodel.MasterKeyData
import com.menta.android.restclient.core.Storage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    externalTokenData: ExternalTokenData,
    masterKeyData: MasterKeyData
){
    val externalToken by externalTokenData.getExternalToken.observeAsState()
    val masterKey by masterKeyData.getMasterKey.observeAsState()
    val isConfiguring by viewModel.isConfiguring.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val apiKey = viewModel.currentUser?.let {
        if (viewModel.operationPreference) {
            it.apiKey
        } else {
            it.secondaryApiKey
        }
    } ?: merchantApiKey

    LaunchedEffect( key1 = Unit) {
        viewModel.events.collectLatest {
            when(it) {
                SplashViewModel.START_CONFIG -> {
                    externalTokenData.getExternalToken(apiKey)
                }

                SplashViewModel.GET_MASTER_KEY -> {
                    //Inyección de llaves
                    masterKeyData.loadMasterKey(
                        merchantId = viewModel.currentUser?.primaryMerchantId ?: merchantId, //TODO el cliente debe conocer su merchantId, es diferente por cada comercio que tenga, seleccionar en caso tener varios merchantIds
                        acquirerId = ACQUIRER_NAME,
                        countryCode = COUNTRY_CODE
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = externalToken) {
        Log.i("SplashViewModel", "externalToken: ${externalToken?.idToken}")
        externalToken?.let { token ->
            if (token.status.statusType != StatusType.ERROR) {
                Log.i(TAG, "Get token SUCCESS")
                viewModel.storePublicKey(token.idToken, token.tokenType)
            } else {
                Log.i(TAG, "Get token ERROR: ${token.status.message}")
            }

        }
    }

    LaunchedEffect(key1 = masterKey) {
        masterKey?.let { key ->
            viewModel.handleMasterKey(key.secretsList)
        }
    }

    ProcessingOperationScreen(
        showLoading = isConfiguring,
        title = if (isConfiguring) stringResource(id = R.string.wait_payment) else "Avoqado POS",
        message = if (isConfiguring) stringResource(id = R.string.whileInitProcessFinishes) else ""
    )

}