package com.avoqado.pos.core.data.network

import timber.log.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class WebSocketManager(
    private val baseUrl: String,
) {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var currentRoomId: String? = null
    private val _messageFlow = MutableSharedFlow<String>() // Flow to emit messages
    val messageFlow: SharedFlow<String> = _messageFlow.asSharedFlow() // Exposed flow

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val webSocketListener =
        object : WebSocketListener() {
            override fun onOpen(
                webSocket: WebSocket,
                response: Response,
            ) {
                Timber.d("Connected to room: $currentRoomId")
                webSocket.send("{\"action\": \"subscribe\", \"room\": \"$currentRoomId\"}") // Subscribe to table updates
            }

            override fun onMessage(
                webSocket: WebSocket,
                text: String,
            ) {
                coroutineScope.launch {
                    _messageFlow.emit(text) // Emit received message
                }
            }

            override fun onMessage(
                webSocket: WebSocket,
                bytes: ByteString,
            ) {
                coroutineScope.launch {
                    _messageFlow.emit(bytes.utf8()) // Convert bytes to string and emit
                }
            }

            override fun onClosing(
                webSocket: WebSocket,
                code: Int,
                reason: String,
            ) {
                Timber.d("Closing connection for table: $currentRoomId")
                webSocket.close(1000, null)
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?,
            ) {
                Timber.e(t, "Error: ${t.message}")
            }
        }

    fun connect(
        venueId: String,
        tableNumber: String,
    ) {
        val newRoomId = "venue_${venueId}_table_$tableNumber"
        if (currentRoomId == newRoomId) return // Prevent re-connection if already connected

        disconnect() // Ensure only one active connection
        currentRoomId = newRoomId

        val request =
            Request
                .Builder()
                .url("$baseUrl/socket.io") // Replace with your WebSocket endpoint
                .build()

        webSocket = client.newWebSocket(request, webSocketListener)
    }

    fun disconnect() {
        webSocket?.send("{\"action\": \"unsubscribe\", \"room\": \"$currentRoomId\"}")
        webSocket?.close(1000, "Leaving room $currentRoomId")
        webSocket = null
        currentRoomId = null
    }
}
