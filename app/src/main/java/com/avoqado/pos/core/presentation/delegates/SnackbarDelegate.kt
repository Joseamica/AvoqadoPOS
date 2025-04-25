package com.avoqado.pos.core.presentation.delegates

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

sealed class SnackbarState {
    data object Default : SnackbarState()

    data class Success(
        val tag: String,
    ) : SnackbarState()

    data class Error(
        val tag: String,
    ) : SnackbarState()
}

class SnackbarDelegate {
    var snackbarHostState: SnackbarHostState? = null
    var coroutineScope: CoroutineScope? = null
    var isOnTop: Boolean = false

    private var snackbarState: SnackbarState = SnackbarState.Default
    private var lastShownMessage: String = ""
    private var lastShownTime: Long = 0
    private var debounceTimeMs: Long = 2000 // 2 seconds debounce
    private var currentJob: Job? = null

    // List of common messages to filter out completely
    private val messagesToFilter =
        listOf(
            "Algo salio mal...",
            "Ocurrio un error!",
            "Error de conexión",
            // Add more messages to filter as needed
        )

    val snackbar: @Composable (SnackbarData) -> Unit
        get() {
            return when (val state = snackbarState) {
                else -> { data ->
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
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        // Filter out unwanted messages completely
        if (messagesToFilter.contains(message)) {
            return
        }

        // Prevent showing the same message in quick succession
        val currentTime = System.currentTimeMillis()
        if (message == lastShownMessage && (currentTime - lastShownTime) < debounceTimeMs) {
            return
        }

        // Cancel any pending snackbar
        currentJob?.cancel()

        this.snackbarState = state
        lastShownMessage = message
        lastShownTime = currentTime

        currentJob =
            coroutineScope?.launch {
                snackbarHostState?.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = duration,
                )
            }
    }

    fun showSnackbarOnTop(
        state: SnackbarState,
        message: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) {
        // Filter out unwanted messages completely
        if (messagesToFilter.contains(message)) {
            return
        }

        // Prevent showing the same message in quick succession
        val currentTime = System.currentTimeMillis()
        if (message == lastShownMessage && (currentTime - lastShownTime) < debounceTimeMs) {
            return
        }

        // Cancel any pending snackbar
        currentJob?.cancel()

        this.snackbarState = state
        this.isOnTop = true
        lastShownMessage = message
        lastShownTime = currentTime

        currentJob =
            coroutineScope?.launch {
                snackbarHostState?.showSnackbar(
                    message = message,
                    actionLabel = actionLabel,
                    duration = duration,
                )
            }
    }
}
