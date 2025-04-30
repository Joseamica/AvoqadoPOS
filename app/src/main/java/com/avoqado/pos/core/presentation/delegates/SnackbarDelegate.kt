package com.avoqado.pos.core.presentation.delegates

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                    DismissibleSnackbar(
                        snackbarData = data,
                        onDismiss = {
                            coroutineScope?.launch {
                                snackbarHostState?.currentSnackbarData?.dismiss()
                            }
                        }
                    )
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

    @Composable
    private fun DismissibleSnackbar(
        snackbarData: SnackbarData,
        onDismiss: () -> Unit
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Snackbar(
                modifier = Modifier.padding(12.dp),
                action = snackbarData.visuals.actionLabel?.let { actionLabel ->
                    {
                        Row {
                            Text(actionLabel)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                },
                dismissAction = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss"
                        )
                    }
                }
            ) {
                Text(snackbarData.visuals.message)
            }
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
                    withDismissAction = true,  // Enable dismiss action
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
                    withDismissAction = true,  // Enable dismiss action
                )
            }
    }
}
