package com.avoqado.pos.core.delegates


import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

sealed class SnackbarState {
    data object Default : SnackbarState()
    data class Success(val tag: String) : SnackbarState()
    data class Error(val tag: String) : SnackbarState()
}

class SnackbarDelegate {

    var snackbarHostState: SnackbarHostState? = null
    var coroutineScope: CoroutineScope? = null
    var isOnTop: Boolean = false

    private var snackbarState: SnackbarState = SnackbarState.Default

    val snackbar: @Composable (SnackbarData) -> Unit
        get() {
            return when (val state = snackbarState) {
                else  -> { data ->
                    Snackbar(snackbarData = data)
                }

//                is SnackbarState.Success -> { data ->
//                    CfSuccessSnackbar(
//                        data = data,
//                        tag = state.tag,
//                        isOnTop = isOnTop
//                    )
//                }
//
//                is SnackbarState.Error -> { data ->
//                    CfErrorSnackbar(
//                        title = resources.getString(R.string.generic_error),
//                        data = data,
//                        tag = state.tag,
//                        isOnTop = isOnTop
//                    )
//                }
            }
        }

    fun showSnackbar(
        state: SnackbarState = SnackbarState.Default,
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        this.snackbarState = state
        coroutineScope?.launch {
            snackbarHostState?.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        }
    }

    fun showSnackbarOnTop(
        state: SnackbarState,
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short
    ) {
        this.snackbarState = state
        this.isOnTop = true
        coroutineScope?.launch {
            snackbarHostState?.showSnackbar(
                message = message,
                actionLabel = actionLabel,
                duration = duration
            )
        }
    }
}