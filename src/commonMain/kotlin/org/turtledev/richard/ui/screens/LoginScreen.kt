package org.turtledev.richard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.turtledev.richard.ui.Strings

@Composable
fun LoginScreen(
    onLoginSubmit: (String, String, (String) -> Unit) -> Unit,
    onGoToRegister: () -> Unit,
    onChangeServer: () -> Unit,
    onOpenSettings: () -> Unit,
    language: String
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var showResetConfirm by remember { mutableStateOf(false) }
    
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val logoBrush = remember(primary, secondary) { 
        Brush.linearGradient(listOf(primary, secondary)) 
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text(Strings.get("confirm_setup_reset", language)) },
            text = { Text(Strings.get("confirm_setup_message", language)) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetConfirm = false
                        onChangeServer()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(Strings.get("confirm", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text(Strings.get("cancel", language))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = RoundedCornerShape(28.dp)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        AnimatedBackground()

        Card(
            modifier = Modifier
                .widthIn(max = 400.dp)
                .padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Settings/IP Change Icon at top right
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.align(Alignment.TopEnd)) {
                        IconButton(
                            onClick = onOpenSettings,
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = Strings.get("settings", language),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(
                            onClick = { showResetConfirm = true },
                        ) {
                            Icon(
                                Icons.Default.Refresh, // Changed icon to distinguish from general settings
                                contentDescription = Strings.get("server_ip_change", language),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Logo
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(logoBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Text("R", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(24.dp))
                Text(
                    Strings.get("welcome", language),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    Strings.get("login_to", language),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(Modifier.height(32.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it; errorText = "" },
                    label = { Text(Strings.get("username", language)) },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorText = "" },
                    label = { Text(Strings.get("password", language)) },
                    leadingIcon = { Icon(Icons.Default.Lock, null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (errorText.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        errorText,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (username.isEmpty() || password.isEmpty()) {
                            errorText = Strings.get("fill_all", language)
                        } else {
                            onLoginSubmit(username, password) { errorText = it }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(Strings.get("login", language), style = MaterialTheme.typography.labelLarge)
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = onGoToRegister) {
                    Text(Strings.get("no_account", language))
                }
            }
        }
    }
}