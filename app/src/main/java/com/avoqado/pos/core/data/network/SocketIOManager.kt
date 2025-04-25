package com.avoqado.pos.core.data.network

import android.util.Log
import com.avoqado.pos.core.data.network.models.PaymentUpdateMessage
import com.avoqado.pos.core.data.network.models.ShiftUpdateMessage
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URISyntaxException

object SocketIOManager {
    private var socket: Socket? = null
    private var currentRoomId: JSONObject? = null
    private var currentVenueRoomId: String? = null

    private val _messageFlow = MutableSharedFlow<PaymentUpdateMessage>()
    val messageFlow: SharedFlow<PaymentUpdateMessage> = _messageFlow.asSharedFlow()

    // New flow for venue-level events
    private val _venueMessageFlow = MutableSharedFlow<PaymentUpdateMessage>()
    val venueMessageFlow: SharedFlow<PaymentUpdateMessage> = _venueMessageFlow.asSharedFlow()

    // New flow for shift events
    private val _shiftMessageFlow = MutableSharedFlow<ShiftUpdateMessage>()
    val shiftMessageFlow: SharedFlow<ShiftUpdateMessage> = _shiftMessageFlow.asSharedFlow()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val gson = Gson()
    private var serverUrl: String? = null

    private val _isWebSocketConnected = MutableStateFlow(false)
    val isWebSocketConnected: StateFlow<Boolean> = _isWebSocketConnected.asStateFlow()

    fun getServerUrl(): String {
        // If we already have a stored URL, return it
        if (!serverUrl.isNullOrEmpty()) {
            return serverUrl!!
        }

        // Otherwise return the default URL (replace with your actual server URL)
        return "https://3cee-189-203-45-177.ngrok-free.app"
    }

    fun connect(url: String) {
        serverUrl = url
        if (socket == null) {
            try {
                // Configure socket options with reconnection settings
                val options =
                    IO.Options
                        .builder()
                        .setReconnection(true)
                        .setReconnectionAttempts(10)
                        .setReconnectionDelay(1000)
                        .build()

                socket = IO.socket(url, options)
                socket?.connect()

                // Listen for connection events
                socket?.on(Socket.EVENT_CONNECT) {
                    Log.d("SocketIO", "Connected to $url")
                    _isWebSocketConnected.value = true

                    // Resubscribe to room if there was one
                    if (currentRoomId != null) {
                        socket?.emit("joinRoom", currentRoomId)
                        Log.d("SocketIO", "Resubscribed to room after reconnect: $currentRoomId")
                    }

                    // Resubscribe to venue room if there was one
                    if (currentVenueRoomId != null) {
                        socket?.emit(
                            "joinMobileRoom",
                            JSONObject().apply {
                                put("venueId", currentVenueRoomId)
                            },
                        )
                        Log.d("SocketIO", "Resubscribed to venue room after reconnect: $currentVenueRoomId")
                    }
                }

                socket?.on(Socket.EVENT_DISCONNECT) {
                    Log.d("SocketIO", "Disconnected from server")
                    _isWebSocketConnected.value = false
                }

                socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                    Log.e("SocketIO", "Connection error: ${args.firstOrNull()}")
                    _isWebSocketConnected.value = false
                }
            } catch (e: URISyntaxException) {
                Log.e("SocketIO", "Socket connection error: ${e.message}")
                _isWebSocketConnected.value = false
            }
        } else {
            Log.d("SocketIO", "Socket already initialized")
        }
    }

    fun subscribeToTable(
        venueId: String,
        billName: String,
    ) {
        if (socket == null) {
            Log.e("SocketIO", "Cannot subscribe - socket not initialized")
            serverUrl?.let { connect(it) } // Try to reconnect if we have a URL
            return
        }

        val newRoomId =
            JSONObject().apply {
                put("venueId", venueId)
                put("table", billName)
            }

        // Check if we're already subscribed to this room (proper JSONObject comparison)
        if (currentRoomId != null &&
            currentRoomId!!.optString("venueId") == venueId &&
            currentRoomId!!.optString("table") == billName
        ) {
            Log.d("SocketIO", "Already subscribed to room: $newRoomId")
            return // Already subscribed to this room
        }

        // Unsubscribe from previous room if needed
        unsubscribe()

        socket?.emit("joinRoom", newRoomId)
        currentRoomId = newRoomId
        Log.d("SocketIO", "Subscribed to room: $newRoomId")

        // Register for updatePos events
        socket?.on("updatePos", onUpdateOrder)
    }

    fun joinMobileRoom(venueId: String) {
        if (socket == null) {
            Log.e("SocketIO", "Cannot join mobile room - socket not initialized")
            serverUrl?.let { connect(it) }
            return
        }

        if (currentVenueRoomId == venueId) {
            Log.d("SocketIO", "Already joined venue room: $venueId")
            return
        }

        // Leave previous venue room if any
        if (currentVenueRoomId != null) {
            leaveMobileRoom(currentVenueRoomId!!)
        }

        val roomData =
            JSONObject().apply {
                put("venueId", venueId)
            }

        socket?.emit("joinMobileRoom", roomData)
        currentVenueRoomId = venueId
        Log.d("SocketIO", "Joined venue room: $venueId")

        // Register for venue-level updatePos events
        socket?.on("updatePos", onVenueUpdate)

        // Register for shift update events
        socket?.on("shiftUpdate", onShiftUpdate)
    }

    fun leaveMobileRoom(venueId: String) {
        if (socket == null) {
            Log.e("SocketIO", "Cannot leave mobile room - socket not initialized")
            return
        }

        val roomData =
            JSONObject().apply {
                put("venueId", venueId)
            }

        socket?.emit("leaveMobileRoom", roomData)
        socket?.off("updatePos", onVenueUpdate)
        socket?.off("shiftUpdate", onShiftUpdate)
        currentVenueRoomId = null
        Log.d("SocketIO", "Left venue room: $venueId")
    }

    fun unsubscribe() {
        if (currentRoomId != null) {
            socket?.emit("leaveRoom", currentRoomId)
            Log.d("SocketIO", "Unsubscribed from room: $currentRoomId")
            socket?.off("updatePos", onUpdateOrder) // Remove listener
            currentRoomId = null
        }
    }

    private val onUpdateOrder =
        Emitter.Listener { args ->
            Log.d("SocketIO", "Received updatePos event: $args")
            if (args.isEmpty()) {
                Log.e("SocketIO", "Empty args in updatePos event")
                return@Listener
            }

            try {
                val data = args[0] as JSONObject
                Log.d("SocketIO", "Parsed data: $data")

                // Check if data contains the expected structure
                if (data.has("data")) {
                    val paymentJson = data.getJSONObject("data")
                    Log.d("SocketIO", "Payment JSON: $paymentJson")

                    coroutineScope.launch {
                        try {
                            val message = gson.fromJson(paymentJson.toString(), PaymentUpdateMessage::class.java)
                            Log.d("SocketIO", "Successfully parsed message: $message")
                            _messageFlow.emit(message)
                        } catch (e: Exception) {
                            Log.e("SocketIO", "Error parsing socket message: ", e)
                        }
                    }
                } else {
                    Log.e("SocketIO", "Missing 'data' field in response: $data")
                }
            } catch (e: Exception) {
                Log.e("SocketIO", "Error handling socket event", e)
            }
        }

    // New listener for venue-level events
    private val onVenueUpdate =
        Emitter.Listener { args ->
            Log.d("SocketIO", "Received venue updatePos event: $args")
            if (args.isEmpty()) {
                Log.e("SocketIO", "Empty args in venue updatePos event")
                return@Listener
            }

            try {
                val data = args[0] as JSONObject
                Log.d("SocketIO", "Parsed venue data: $data")

                // Check if data contains the expected structure
                if (data.has("data")) {
                    val paymentJson = data.getJSONObject("data")
                    Log.d("SocketIO", "Venue payment JSON: $paymentJson")

                    coroutineScope.launch {
                        try {
                            val message = gson.fromJson(paymentJson.toString(), PaymentUpdateMessage::class.java)
                            Log.d("SocketIO", "Successfully parsed venue message: $message")
                            _venueMessageFlow.emit(message)
                        } catch (e: Exception) {
                            Log.e("SocketIO", "Error parsing venue socket message: ", e)
                        }
                    }
                } else {
                    Log.e("SocketIO", "Missing 'data' field in venue response: $data")
                }
            } catch (e: Exception) {
                Log.e("SocketIO", "Error handling venue socket event", e)
            }
        }

    // New listener for shift update events
    private val onShiftUpdate =
        Emitter.Listener { args ->
            Log.d("SocketIO", "Received shift update event: $args")
            if (args.isEmpty()) {
                Log.e("SocketIO", "Empty args in shift update event")
                return@Listener
            }

            try {
                val data = args[0] as JSONObject
                Log.d("SocketIO", "Parsed shift data: $data")

                // Check if data contains the expected structure
                if (data.has("data")) {
                    val shiftJson = data.getJSONObject("data")
                    Log.d("SocketIO", "Shift JSON: $shiftJson")

                    coroutineScope.launch {
                        try {
                            val message = gson.fromJson(shiftJson.toString(), ShiftUpdateMessage::class.java)
                            Log.d("SocketIO", "Successfully parsed shift message: $message")
                            _shiftMessageFlow.emit(message)
                        } catch (e: Exception) {
                            Log.e("SocketIO", "Error parsing shift socket message: ", e)
                        }
                    }
                } else {
                    Log.e("SocketIO", "Missing 'data' field in shift response: $data")
                }
            } catch (e: Exception) {
                Log.e("SocketIO", "Error handling shift socket event", e)
            }
        }

    fun disconnect() {
        unsubscribe()

        // Also leave venue room if needed
        if (currentVenueRoomId != null) {
            leaveMobileRoom(currentVenueRoomId!!)
        }

        socket?.disconnect()
        socket = null
        Log.d("SocketIO", "Disconnected from WebSocket")
    }

    // Helper method to check connection status
    fun isConnected(): Boolean = socket?.connected() == true
}
