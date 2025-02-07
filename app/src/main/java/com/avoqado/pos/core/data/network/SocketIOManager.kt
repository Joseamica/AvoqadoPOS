package com.avoqado.pos.core.data.network

import android.util.Log
import com.avoqado.pos.core.data.network.models.PaymentUpdateMessage
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import java.net.URISyntaxException

object SocketIOManager {
    private var socket: Socket? = null
    private var currentRoomId: JSONObject? = null
    private val _messageFlow = MutableSharedFlow<PaymentUpdateMessage>()
    val messageFlow: SharedFlow<PaymentUpdateMessage> = _messageFlow.asSharedFlow()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val gson = Gson()

    fun connect(serverUrl: String) {
        if (socket == null) {
            try {
                socket = IO.socket(serverUrl)
                socket?.connect()
                socket?.on(Socket.EVENT_CONNECT) {
                    Log.d("SocketIO", "Connected to $serverUrl")
                }
            } catch (e: URISyntaxException) {
                Log.e("SocketIO", "Socket connection error: ${e.message}")
            }
        }
    }

    fun subscribeToTable(venueId: String, tableNumber: String) {
        val newRoomId =  JSONObject().apply {
            put("venueId", venueId)
            put("table", tableNumber)
        }

        if (currentRoomId == newRoomId) return // Avoid unnecessary re-subscriptions

//        unsubscribe()

        socket?.emit("joinRoom", newRoomId)
        currentRoomId = newRoomId
        Log.d("SocketIO", "Subscribed to room: $newRoomId")

        socket?.on("updatePos", onUpdateOrder)
    }

    fun unsubscribe() {
        if (currentRoomId != null) {
            socket?.emit("leaveRoom", currentRoomId)
            Log.d("SocketIO", "Unsubscribed from room: $currentRoomId")
            currentRoomId = null
        }
        socket?.off("updatePos", onUpdateOrder) // Remove listener
    }

    private val onUpdateOrder = Emitter.Listener { args ->
        Log.d("ScoketIO", "Received updatePos event : $args")
        if (args.isNotEmpty()) {
            val data = args[0] as JSONObject
            val paymentJson = data.getJSONObject("data")
            coroutineScope.launch {
                try {
                    val message = gson.fromJson(paymentJson.toString(), PaymentUpdateMessage::class.java)
                    _messageFlow.emit(
                        message
                    )
                } catch (e: Exception) {
                    Log.e("SocketIO", "Error parsing socket message: ",e)
                }

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
