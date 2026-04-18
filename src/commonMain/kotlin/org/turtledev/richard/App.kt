package org.turtledev.richard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.turtledev.richard.domain.viewmodel.AppScreen
import org.turtledev.richard.domain.viewmodel.AppViewModel
import org.turtledev.richard.domain.viewmodel.ChatState
import org.turtledev.richard.ui.screens.ChatScreen
import org.turtledev.richard.ui.screens.LoginScreen
import org.turtledev.richard.ui.screens.SetupScreen
import org.turtledev.richard.ui.theme.RichardMessengerTheme

@Composable
fun RichardMessengerApp(viewModel: AppViewModel) {
    val appScreen by viewModel.appScreen.collectAsState()
    val chatState by viewModel.chatState.collectAsState()
    val messageText by viewModel.messageText.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val language by viewModel.language.collectAsState()
    val theme by viewModel.theme.collectAsState()
    val primaryColor by viewModel.primaryColor.collectAsState()
    val backgroundColor by viewModel.backgroundColor.collectAsState()

    val darkTheme = when (theme) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    RichardMessengerTheme(
        darkTheme = darkTheme, 
        primaryColorName = primaryColor,
        backgroundColorName = backgroundColor
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            // Plain when-block: no AnimatedContent to avoid continuous frame rendering on hover
            when (appScreen) {
                is AppScreen.Setup -> SetupScreen(
                    onServerIpSubmit = viewModel::onServerIpSubmit,
                    onLanguageSelect = viewModel::onLanguageSelect,
                    initialLanguage = language
                )
                is AppScreen.Login -> LoginScreen(
                    onLoginSubmit = { u, p, error -> viewModel.onLoginSubmit(u, p, error) },
                    onGoToRegister = viewModel::goToRegister,
                    onChangeServer = viewModel::goToSetup,
                    language = language
                )
                is AppScreen.Register -> {
                    org.turtledev.richard.ui.screens.RegisterScreen(
                        onRegisterSubmit = { u, p, error -> viewModel.onRegisterSubmit(u, p, error) },
                        onBackToLogin = viewModel::goToLogin,
                        language = language
                    )
                }
                is AppScreen.Chat -> {
                    val messages = (chatState as? ChatState.Loaded)?.messages ?: emptyList()
                    ChatScreen(
                        messages = messages,
                        currentUsername = viewModel.currentUsername,
                        messageText = messageText,
                        isSending = isSending,
                        onMessageTextChange = viewModel::onMessageTextChange,
                        onSendMessage = viewModel::sendMessage,
                        onLogout = viewModel::onLogout,
                        onOpenSettings = viewModel::openSettings,
                        language = language
                    )
                }
                is AppScreen.Settings -> {
                    org.turtledev.richard.ui.screens.SettingsScreen(
                        currentUsername = viewModel.currentUsername,
                        onClose = viewModel::closeSettings,
                        onSaveProfile = viewModel::updateProfile,
                        onDeleteAccount = viewModel::deleteAccount,
                        language = language,
                        theme = theme,
                        onThemeChange = viewModel::onThemeChange,
                        primaryColor = primaryColor,
                        onPrimaryColorChange = viewModel::onPrimaryColorChange,
                        backgroundColor = backgroundColor,
                        onBackgroundColorChange = viewModel::onBackgroundColorChange
                    )
                }
            }
        }
    }
}
