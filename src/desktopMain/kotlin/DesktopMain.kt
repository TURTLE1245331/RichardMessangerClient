package org.turtledev.richard

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberWindowState
import org.turtledev.richard.domain.viewmodel.AppViewModel

fun main() {
    System.setProperty("skiko.renderApi", "OPENGL")
    application {
        val viewModel = AppViewModel()

        Window(
            onCloseRequest = ::exitApplication,
            title = "RICHARD Messenger",
            state = rememberWindowState(width = 1100.dp, height = 750.dp)
            // icon = painterResource(Res.drawable.icon)
        ) {
            RichardMessengerApp(viewModel = viewModel)
        }
    }
}
