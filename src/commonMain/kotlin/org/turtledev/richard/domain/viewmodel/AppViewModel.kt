package org.turtledev.richard.domain.viewmodel

import com.russhwolf.settings.Settings
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.*

sealed class AppScreen {
    object Setup : AppScreen()
    object Login : AppScreen()
    object Chat : AppScreen()
    object Settings : AppScreen()
    object Register : AppScreen()
}

sealed class ChatState {
    object Loading : ChatState()
    data class Loaded(val messages: List<String>) : ChatState()
    data class Error(val message: String) : ChatState()
}

class AppViewModel {
    private val settings = Settings()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val httpClient = HttpClient()

    // --- State ---
    private val _appScreen = MutableStateFlow<AppScreen>(
        when {
            settings.getString("server_ip", "").isEmpty() -> AppScreen.Setup
            settings.getString("user", "").isEmpty() -> AppScreen.Login
            else -> AppScreen.Chat
        }
    )
    val appScreen: StateFlow<AppScreen> = _appScreen

    private val _language = MutableStateFlow(settings.getString("language", "de"))
    val language: StateFlow<String> = _language

    private val _theme = MutableStateFlow(settings.getString("theme", "system"))
    val theme: StateFlow<String> = _theme

    private val _primaryColor = MutableStateFlow(settings.getString("primary_color", "blue"))
    val primaryColor: StateFlow<String> = _primaryColor

    private val _backgroundColor = MutableStateFlow(settings.getString("background_color", "default"))
    val backgroundColor: StateFlow<String> = _backgroundColor

    private val _chatState = MutableStateFlow<ChatState>(ChatState.Loaded(emptyList()))
    val chatState: StateFlow<ChatState> = _chatState

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending

    private val _enterToSend = MutableStateFlow(settings.getBoolean("enter_to_send", true))
    val enterToSend: StateFlow<Boolean> = _enterToSend

    private val _chatFontSize = MutableStateFlow(settings.getString("chat_font_size", "medium"))
    val chatFontSize: StateFlow<String> = _chatFontSize

    // --- Stored values ---
    var currentUsername: String = settings.getString("user", "")
        private set
    private var currentPassword: String = settings.getString("pass", "")
    private var serverIp: String = settings.getString("server_ip", "")
    private val apiBase get() = "http://$serverIp:8000"

    private var pollingJob: Job? = null
    private var lastId = 0

    init {
        // If already logged in, start polling right away
        if (_appScreen.value == AppScreen.Chat) {
            startPolling()
        }
    }

    // --- Navigation ---
    fun openSettings() { _appScreen.value = AppScreen.Settings }
    fun closeSettings() { _appScreen.value = AppScreen.Chat }
    fun goToRegister() { _appScreen.value = AppScreen.Register }
    fun goToLogin() { _appScreen.value = AppScreen.Login }

    fun goToSetup() {
        _appScreen.value = AppScreen.Setup
    }

    // --- Actions ---

    fun onLanguageSelect(langCode: String) {
        _language.value = langCode
        settings.putString("language", langCode)
    }

    fun onThemeChange(newTheme: String) {
        _theme.value = newTheme
        settings.putString("theme", newTheme)
    }

    fun onPrimaryColorChange(newColor: String) {
        _primaryColor.value = newColor
        settings.putString("primary_color", newColor)
    }

    fun onBackgroundColorChange(newColor: String) {
        _backgroundColor.value = newColor
        settings.putString("background_color", newColor)
    }

    fun onEnterToSendChange(enabled: Boolean) {
        _enterToSend.value = enabled
        settings.putBoolean("enter_to_send", enabled)
    }

    fun onChatFontSizeChange(newSize: String) {
        _chatFontSize.value = newSize
        settings.putString("chat_font_size", newSize)
    }

    fun onServerIpSubmit(ip: String) {
        serverIp = ip.trim()
        settings.putString("server_ip", serverIp)
        _appScreen.value = AppScreen.Login
    }

    fun onLoginSubmit(username: String, password: String, onError: (String) -> Unit) {
        scope.launch {
            try {
                val res = httpClient.post("$apiBase/login") {
                    header("X-User", username)
                    header("X-Pass", password)
                }
                if (res.status == HttpStatusCode.OK) {
                    currentUsername = username
                    currentPassword = password
                    settings.putString("user", username)
                    settings.putString("pass", password)
                    _appScreen.value = AppScreen.Chat
                    startPolling()
                } else {
                    onError("Login fehlgeschlagen: Falsche Daten")
                }
            } catch (e: Exception) {
                onError("Server nicht erreichbar (${e.message})")
            }
        }
    }

    fun onRegisterSubmit(username: String, password: String, onError: (String) -> Unit) {
        scope.launch {
            try {
                val res = httpClient.post("$apiBase/register") {
                    contentType(ContentType.Application.Json)
                    setBody("{\"username\": \"$username\", \"password\": \"$password\"}")
                }
                if (res.status == HttpStatusCode.OK) {
                    _appScreen.value = AppScreen.Login
                } else {
                    onError("Registrierung fehlgeschlagen: Name eventuell vergeben")
                }
            } catch (e: Exception) {
                onError("Server nicht erreichbar (${e.message})")
            }
        }
    }

    fun onMessageTextChange(text: String) {
        _messageText.value = text
    }

    fun sendMessage() {
        val text = _messageText.value.trim()
        if (text.isEmpty()) return
        
        val fullMessage = "$currentUsername: $text"
        
        scope.launch {
            _isSending.value = true
            try {
                httpClient.post("$apiBase/send") {
                    header("X-User", currentUsername)
                    header("X-Pass", currentPassword)
                    contentType(ContentType.Application.Json)
                    setBody("{\"text\": \"$fullMessage\"}")
                }
                _messageText.value = ""
            } catch (e: Exception) { 
            }
            _isSending.value = false
        }
    }

    fun onLogout() {
        pollingJob?.cancel()
        settings.remove("user")
        settings.remove("pass")
        currentUsername = ""
        currentPassword = ""
        lastId = 0
        _chatState.value = ChatState.Loaded(emptyList())
        _appScreen.value = AppScreen.Login
    }

    fun deleteAccount() {
        if (currentUsername.equals("admin", ignoreCase = true)) return

        scope.launch {
            try {
                val res = httpClient.post("$apiBase/delete_account") {
                    header("X-User", currentUsername)
                    header("X-Pass", currentPassword)
                }
                if (res.status == HttpStatusCode.OK) {
                    onLogout()
                    settings.remove("server_ip")
                    _appScreen.value = AppScreen.Setup
                }
            } catch (e: Exception) { }
        }
    }

    fun updateProfile(newUsername: String, newPassword: String) {
        if (currentUsername.equals("admin", ignoreCase = true)) return

        scope.launch {
            try {
                // 1. Name ändern (falls geändert)
                if (newUsername != currentUsername && newUsername.isNotEmpty()) {
                    val resName = httpClient.post("$apiBase/change_username") {
                        header("X-User", currentUsername)
                        header("X-Pass", currentPassword)
                        contentType(ContentType.Application.Json)
                        setBody("{\"new_username\": \"$newUsername\"}")
                    }
                    if (resName.status == HttpStatusCode.OK) {
                        currentUsername = newUsername
                        settings.putString("user", newUsername)
                    }
                }

                // 2. Passwort ändern (falls ausgefüllt)
                if (newPassword.isNotEmpty()) {
                    val resPass = httpClient.post("$apiBase/change_password") {
                        header("X-User", currentUsername)
                        header("X-Pass", currentPassword)
                        contentType(ContentType.Application.Json)
                        setBody("{\"new_password\": \"$newPassword\"}")
                    }
                    if (resPass.status == HttpStatusCode.OK) {
                        currentPassword = newPassword
                        settings.putString("pass", newPassword)
                    }
                }
                
                _appScreen.value = AppScreen.Chat
            } catch (e: Exception) { }
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive) {
                try {
                    val res = httpClient.get("$apiBase/get?last_id=$lastId") {
                        header("X-User", currentUsername)
                        header("X-Pass", currentPassword)
                    }
                    if (res.status == HttpStatusCode.OK) {
                        val json = Json.parseToJsonElement(res.bodyAsText()).jsonObject
                        val newMsgs = json["messages"]
                            ?.jsonArray
                            ?.mapNotNull { it.jsonObject["text"]?.jsonPrimitive?.content }
                            ?: emptyList()

                        if (newMsgs.isNotEmpty()) {
                            val current = (_chatState.value as? ChatState.Loaded)?.messages ?: emptyList()
                            
                            // Deduplicate based on overlap to prevent doubling on polling glitches
                            var overlapIndex = 0
                            for (i in 1..minOf(current.size, newMsgs.size)) {
                                if (current.takeLast(i) == newMsgs.take(i)) {
                                    overlapIndex = i
                                }
                            }
                            val filteredNew = newMsgs.drop(overlapIndex)
                            
                            if (filteredNew.isNotEmpty()) {
                                _chatState.value = ChatState.Loaded(current + filteredNew)
                            }
                        }
                        
                        // Always update lastId if total is provided to stay in sync with server
                        json["total"]?.jsonPrimitive?.intOrNull?.let { total ->
                            lastId = total
                        }
                    }
                } catch (e: Exception) { }
                delay(1000)
            }
        }
    }
}