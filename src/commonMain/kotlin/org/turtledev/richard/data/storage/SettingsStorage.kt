package org.turtledev.richard.data.storage

import com.russhwolf.settings.Settings

class SettingsStorage(private val settings: Settings) {

    companion object {
        private const val KEY_SERVER_IP = "server_ip"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_IS_SETUP_COMPLETE = "is_setup_complete"
    }
    
    var serverIp: String?
        get() = (settings as com.russhwolf.settings.Settings).getStringOrNull(KEY_SERVER_IP)
        set(value) {
            if (value != null) (settings as com.russhwolf.settings.Settings).putString(KEY_SERVER_IP, value)
            else (settings as com.russhwolf.settings.Settings).remove(KEY_SERVER_IP)
        }

    var username: String?
        get() = (settings as com.russhwolf.settings.Settings).getStringOrNull(KEY_USERNAME)
        set(value) {
            if (value != null) (settings as com.russhwolf.settings.Settings).putString(KEY_USERNAME, value)
            else (settings as com.russhwolf.settings.Settings).remove(KEY_USERNAME)
        }

    var password: String?
        get() = (settings as com.russhwolf.settings.Settings).getStringOrNull(KEY_PASSWORD)
        set(value) {
            if (value != null) (settings as com.russhwolf.settings.Settings).putString(KEY_PASSWORD, value)
            else (settings as com.russhwolf.settings.Settings).remove(KEY_PASSWORD)
        }

    var isSetupComplete: Boolean
        get() = (settings as com.russhwolf.settings.Settings).getBoolean(KEY_IS_SETUP_COMPLETE, false)
        set(value) {
            (settings as com.russhwolf.settings.Settings).putBoolean(KEY_IS_SETUP_COMPLETE, value)
        }

    fun clearCredentials() {
        (settings as com.russhwolf.settings.Settings).remove(KEY_USERNAME)
        (settings as com.russhwolf.settings.Settings).remove(KEY_PASSWORD)
    }
}
