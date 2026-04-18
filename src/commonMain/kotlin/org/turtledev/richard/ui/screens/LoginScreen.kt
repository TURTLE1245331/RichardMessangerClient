package org.turtledev.richard.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.turtledev.richard.ui.Strings
import org.turtledev.richard.ui.theme.*
import kotlin.math.sin

@Composable
fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition()
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Background base
        drawRect(color = Color.Transparent) // Actual background is set in Box

        // Floating blobs
        val blob1Center = Offset(
            x = width * (0.5f + 0.2f * sin(phase.toDouble()).toFloat()),
            y = height * (0.3f + 0.1f * sin(phase.toDouble() * 0.5).toFloat())
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primary.copy(alpha = 0.15f), Color.Transparent),
                center = blob1Center,
                radius = width * 0.6f
            ),
            center = blob1Center,
            radius = width * 0.6f
        )

        val blob2Center = Offset(
            x = width * (0.2f + 0.15f * sin(phase.toDouble() * 0.7).toFloat()),
            y = height * (0.7f + 0.1f * sin(phase.toDouble() * 1.2).toFloat())
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(secondary.copy(alpha = 0.1f), Color.Transparent),
                center = blob2Center,
                radius = width * 0.5f
            ),
            center = blob2Center,
            radius = width * 0.5f
        )
    }
}

@Composable
fun LoginScreen(
    onLoginSubmit: (String, String, (String) -> Unit) -> Unit,
    onGoToRegister: () -> Unit,
    onChangeServer: () -> Unit,
    language: String
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val logoBrush = remember(primary, secondary) { 
        Brush.linearGradient(listOf(primary, secondary)) 
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
                    IconButton(
                        onClick = onChangeServer,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = Strings.get("server_ip_change", language),
                            tint = MaterialTheme.colorScheme.primary
                        )
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