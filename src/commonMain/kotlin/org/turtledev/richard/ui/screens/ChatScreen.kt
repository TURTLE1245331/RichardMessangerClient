package org.turtledev.richard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.turtledev.richard.ui.Strings
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    messages: List<String>,
    currentUsername: String,
    messageText: String,
    isSending: Boolean,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit,
    language: String,
    enterToSend: Boolean
) {
    val listState = rememberLazyListState()
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val logoBrush = remember(primary, secondary) { 
        Brush.linearGradient(listOf(primary, secondary))
    }

    var showLogoutConfirm by remember { mutableStateOf(false) }

    // Automatically stay at the bottom (index 0 in reverse layout)
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            AnimatedBackground()
            
            Column(modifier = Modifier.fillMaxSize()) {
                // ── Top Bar ──────────────────────────────────────────────────────────
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(logoBrush),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("R", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("RICHARD", style = MaterialTheme.typography.titleMedium)
                                Text(Strings.get("online", language), color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                            }
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = currentUsername,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Default.Settings, contentDescription = Strings.get("settings", language))
                        }
                        IconButton(onClick = { showLogoutConfirm = true }) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = Strings.get("logout", language))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)

                // ── Message List ─────────────────────────────────────────────────────
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = true
                ) {
                    itemsIndexed(
                        items = messages.asReversed(),
                        key = { index: Int, msg: String -> 
                            val originalIndex = messages.size - 1 - index
                            "$originalIndex-${msg.hashCode()}"
                        }
                    ) { _, msg: String ->
                        MessageBubble(
                            msg = msg, 
                            currentUsername = currentUsername,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }

        // ── Input Area ───────────────────────────────────────────────────────
        Surface(
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.windowInsetsPadding(WindowInsets.ime)
        ) {
            Column {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = onMessageTextChange,
                        placeholder = { Text(Strings.get("message_hint", language)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                        ),
                        singleLine = false,
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(imeAction = if (enterToSend) ImeAction.Send else ImeAction.Default),
                        keyboardActions = KeyboardActions(onSend = { if (enterToSend) onSendMessage() })
                    )

                    Spacer(Modifier.width(8.dp))

                    val sendButtonColor by animateColorAsState(
                        targetValue = if (messageText.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        animationSpec = tween(300)
                    )
                    val sendIconColor by animateColorAsState(
                        targetValue = if (messageText.isNotEmpty()) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(300)
                    )
                    val sendScale by animateFloatAsState(
                        targetValue = if (messageText.isNotEmpty()) 1.1f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                    )

                    FloatingActionButton(
                        onClick = onSendMessage,
                        modifier = Modifier.size(48.dp).scale(sendScale),
                        shape = CircleShape,
                        containerColor = sendButtonColor,
                        contentColor = sendIconColor,
                        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text(Strings.get("logout", language)) },
            text = { Text(Strings.get("logout_confirm_text", language)) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirm = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(Strings.get("logout", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text(Strings.get("cancel", language))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = RoundedCornerShape(28.dp)
        )
    }
}

@Composable
private fun MessageBubble(msg: String, currentUsername: String, modifier: Modifier = Modifier) {
    val isOwn = remember(msg, currentUsername) {
        msg.startsWith("$currentUsername:", ignoreCase = true) || 
        msg.startsWith("$currentUsername :", ignoreCase = true)
    }
    val isSystem = remember(msg, isOwn) {
        msg.startsWith("[Server]") || (msg.startsWith("Admin:", ignoreCase = true) && !isOwn)
    }

    val bgColor = when {
        isSystem -> MaterialTheme.colorScheme.tertiaryContainer
        isOwn    -> MaterialTheme.colorScheme.primaryContainer
        else     -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        isSystem -> MaterialTheme.colorScheme.onTertiaryContainer
        isOwn    -> MaterialTheme.colorScheme.onPrimaryContainer
        else     -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = bgColor,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isOwn) 16.dp else 4.dp,
                bottomEnd = if (isOwn) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 320.dp),
            tonalElevation = 1.dp
        ) {
            Text(
                text = msg,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                color = contentColor,
                style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 20.sp)
            )
        }
    }
}
