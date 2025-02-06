package com.avoqado.pos.core.data.network

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import java.net.URISyntaxException

object SocketIOManager {
    private var socket: Socket? = null
    private var currentRoomId: String? = null
    private val _messageFlow = MutableSharedFlow<String>()
    val messageFlow: SharedFlow<String> = _messageFlow.asSharedFlow()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun connect(serverUrl: String) {
        if (socket == null) {
            try {
                socket = IO.socket(serverUrl)
                socket?.connect()
                Log.d("SocketIO", "Connected to $serverUrl")
            } catch (e: URISyntaxException) {
                Log.e("SocketIO", "Socket connection error: ${e.message}")
            }
        }
    }

    fun subscribeToTable(venueId: String, tableNumber: String) {
        val newRoomId = "venue_${venueId}_table_${tableNumber}"

        if (currentRoomId == newRoomId) return // Avoid unnecessary re-subscriptions

        unsubscribe() // Ensure we leave the previous room before joining a new one

        socket?.emit("join", newRoomId)
        currentRoomId = newRoomId
        Log.d("SocketIO", "Subscribed to room: $newRoomId")

        socket?.on("updateOrder", onUpdateOrder)
    }

    fun unsubscribe() {
        if (currentRoomId != null) {
            socket?.emit("leave", currentRoomId)
            Log.d("SocketIO", "Unsubscribed from room: $currentRoomId")
            currentRoomId = null
        }
        socket?.off("updatePos", onUpdateOrder) // Remove listener
    }

    private val onUpdateOrder = Emitter.Listener { args ->
        Log.d("ScoketIO", "Received updateOrder event : $args")
        if (args.isNotEmpty()) {
            val data = args[0] as JSONObject
            coroutineScope.launch {
                _messageFlow.emit(data.toString()) // Emit JSON response as String
            }
        }
    }

    fun disconnect() {
        unsubscribe()
        socket?.disconnect()
        socket = null
        Log.d("SocketIO", "Disconnected from WebSocket")
    }
}
