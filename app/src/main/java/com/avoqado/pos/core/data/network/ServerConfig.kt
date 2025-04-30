package com.avoqado.pos.core.data.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Singleton class to manage server URLs configuration throughout the app.
 * This provides a single source of truth for all API endpoints.
 */
object ServerConfig {
    private const val PREFS_NAME = "avoqado_server_config"
    private const val KEY_API_BASE_URL = "api_base_url"
    private const val KEY_SOCKET_URL = "socket_url"
    
    // Default URLs - can be updated at runtime if needed
    private const val DEFAULT_SERVER_URL = "https://1c87-189-203-45-177.ngrok-free.app"
    private const val DEFAULT_API_PATH = "/v1/"
    
    private var prefs: SharedPreferences? = null
    
    /**
     * Initialize ServerConfig with application context.
     * Must be called before using any other methods.
     */
    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Returns the base URL for REST API calls (including the path).
     */
    fun getApiBaseUrl(): String {
        val baseUrl = getServerUrl()
        return if (baseUrl.endsWith("/")) {
            baseUrl + "v1/"
        } else {
            "$baseUrl/v1/"
        }
    }
    
    /**
     * Returns the socket server URL (without the path)
     */
    fun getSocketUrl(): String {
        return prefs?.getString(KEY_SOCKET_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
    }
    
    /**
     * Returns the base server URL (without the path)
     */
    fun getServerUrl(): String {
        return prefs?.getString(KEY_API_BASE_URL, DEFAULT_SERVER_URL) ?: DEFAULT_SERVER_URL
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
}
