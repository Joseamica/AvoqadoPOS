package com.avoqado.pos.core.data.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.concurrent.TimeUnit

/**
 * Singleton class to manage application configuration throughout the app.
 * This provides a single source of truth for all configurable values.
 */
object AppConfig {
    private const val PREFS_NAME = "avoqado_app_config"
    
    // Keys for different config types
    private const val KEY_API_BASE_URL = "api_base_url"
    private const val KEY_SOCKET_URL = "socket_url"
    private const val KEY_NETWORK_TIMEOUT = "network_timeout"
    private const val KEY_SOCKET_RECONNECT_ATTEMPTS = "socket_reconnect_attempts"
    private const val KEY_SOCKET_RECONNECT_DELAY = "socket_reconnect_delay"
    private const val KEY_SOCKET_UPDATE_INTERVAL = "socket_update_interval"
    private const val KEY_IS_DEV_MODE = "is_dev_mode"
    
    // Environment configurations
    private const val DEFAULT_DEV_MODE = true // Set default development mode
    
    // Backend URLs
    private const val DEV_SERVER_URL = "https://3feba7ca7b87.ngrok-free.app"
    private const val PROD_SERVER_URL = "https://api.avoqado.io"
    
    // Frontend URLs
    private const val DEV_WEB_FRONTEND_URL = "http://localhost:5173"
    private const val PROD_WEB_FRONTEND_URL = "https://avoqado.io"
    
    // Default values for other configurations
    private const val DEFAULT_API_PATH = "/v1/"
    private const val DEFAULT_NETWORK_TIMEOUT_SECONDS = 60L  // Reduced from 120s
    private const val DEFAULT_SOCKET_RECONNECT_ATTEMPTS = 10       
    private const val DEFAULT_SOCKET_RECONNECT_DELAY_MS = 1000L
    private const val DEFAULT_SOCKET_UPDATE_INTERVAL_MS = 1000L
    
    private var prefs: SharedPreferences? = null
    
    /**
     * Initialize AppConfig with application context.
     * Must be called before using any other methods.
     */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    // ------------------- Environment Configuration -------------------
    
    /**
     * Check if the app is running in development mode
     */
    fun isDevMode(): Boolean {
        return prefs?.getBoolean(KEY_IS_DEV_MODE, DEFAULT_DEV_MODE) ?: DEFAULT_DEV_MODE
    }
    
    /**
     * Set the app's development mode
     */
    fun setDevMode(isDev: Boolean) {
        prefs?.edit {
            putBoolean(KEY_IS_DEV_MODE, isDev)
        }
    }
    
    /**
     * Get the appropriate web frontend URL based on current environment
     */
    fun getWebFrontendUrl(): String {
        return if (isDevMode()) DEV_WEB_FRONTEND_URL else PROD_WEB_FRONTEND_URL
    }
    
    // ------------------- Server URL Configuration -------------------
    
    /**
     * Returns the base URL for REST API calls (including the path).
     */
    fun getApiBaseUrl(): String {
        val baseUrl = getServerUrl()
        return if (baseUrl.endsWith("/")) {
            baseUrl + "api/v1/"
        } else {
            "$baseUrl/api/v1/"
        }
    }
    
    /**
     * Returns the socket server URL (without the path)
     * Uses environment-specific URLs if no custom URL is set
     */
    fun getSocketUrl(): String {
        val defaultUrl = if (isDevMode()) DEV_SERVER_URL else PROD_SERVER_URL
        return prefs?.getString(KEY_SOCKET_URL, defaultUrl) ?: defaultUrl
    }
    
    /**
     * Returns the base server URL (without the path)
     * Uses environment-specific URLs if no custom URL is set
     */
    fun getServerUrl(): String {
        val defaultUrl = if (isDevMode()) DEV_SERVER_URL else PROD_SERVER_URL
        return prefs?.getString(KEY_API_BASE_URL, defaultUrl) ?: defaultUrl
    }
    
    /**
     * Set a new API base URL (without the path)
     */
    fun setServerUrl(url: String) {
        prefs?.edit {
            putString(KEY_API_BASE_URL, url)
        }
    }
    
    /**
     * Set a new socket server URL
     */
    fun setSocketUrl(url: String) {
        prefs?.edit {
            putString(KEY_SOCKET_URL, url)
        }
    }
    
    // ------------------- Network Configuration -------------------
    
    /**
     * Returns the network timeout in seconds for HTTP requests
     */
    fun getNetworkTimeoutSeconds(): Long {
        return prefs?.getLong(KEY_NETWORK_TIMEOUT, DEFAULT_NETWORK_TIMEOUT_SECONDS) 
            ?: DEFAULT_NETWORK_TIMEOUT_SECONDS
    }
    
    /**
     * Set the network timeout in seconds for HTTP requests
     */
    fun setNetworkTimeoutSeconds(seconds: Long) {
        prefs?.edit {
            putLong(KEY_NETWORK_TIMEOUT, seconds)
        }
    }
    
    // ------------------- Socket Configuration -------------------
    
    /**
     * Get the number of reconnection attempts for socket connections
     */
    fun getSocketReconnectAttempts(): Int {
        return prefs?.getInt(KEY_SOCKET_RECONNECT_ATTEMPTS, DEFAULT_SOCKET_RECONNECT_ATTEMPTS)
            ?: DEFAULT_SOCKET_RECONNECT_ATTEMPTS
    }
    
    /**
     * Set the number of reconnection attempts for socket connections
     */
    fun setSocketReconnectAttempts(attempts: Int) {
        prefs?.edit {
            putInt(KEY_SOCKET_RECONNECT_ATTEMPTS, attempts)
        }
    }
    
    /**
     * Get the delay between reconnection attempts in milliseconds
     */
    fun getSocketReconnectDelayMs(): Long {
        return prefs?.getLong(KEY_SOCKET_RECONNECT_DELAY, DEFAULT_SOCKET_RECONNECT_DELAY_MS)
            ?: DEFAULT_SOCKET_RECONNECT_DELAY_MS
    }
    
    /**
     * Set the delay between reconnection attempts in milliseconds
     */
    fun setSocketReconnectDelayMs(delayMs: Long) {
        prefs?.edit {
            putLong(KEY_SOCKET_RECONNECT_DELAY, delayMs)
        }
    }
    
    /**
     * Get the minimum interval between emitted socket updates in milliseconds
     */
    fun getSocketUpdateIntervalMs(): Long {
        return prefs?.getLong(KEY_SOCKET_UPDATE_INTERVAL, DEFAULT_SOCKET_UPDATE_INTERVAL_MS)
            ?: DEFAULT_SOCKET_UPDATE_INTERVAL_MS
    }
    
    /**
     * Set the minimum interval between emitted socket updates in milliseconds
     */
    fun setSocketUpdateIntervalMs(intervalMs: Long) {
        prefs?.edit {
            putLong(KEY_SOCKET_UPDATE_INTERVAL, intervalMs)
        }
    }
    
    // For backward compatibility with existing code using ServerConfig
    object ServerConfig {
        fun getApiBaseUrl() = AppConfig.getApiBaseUrl()
        fun getSocketUrl() = AppConfig.getSocketUrl()
        fun getServerUrl() = AppConfig.getServerUrl()
        fun setServerUrl(url: String) = AppConfig.setServerUrl(url)
        fun setSocketUrl(url: String) = AppConfig.setSocketUrl(url)
        fun initialize(context: Context) = AppConfig.initialize(context)
    }
}
