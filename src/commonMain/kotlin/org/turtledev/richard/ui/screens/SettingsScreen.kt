package org.turtledev.richard.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import org.turtledev.richard.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentUsername: String,
    onClose: () -> Unit,
    onSaveProfile: (String, String) -> Unit,
    onDeleteAccount: () -> Unit,
    language: String,
    theme: String,
    onThemeChange: (String) -> Unit,
    primaryColor: String,
    onPrimaryColorChange: (String) -> Unit,
    backgroundColor: String,
    onBackgroundColorChange: (String) -> Unit,
    enterToSend: Boolean,
    onEnterToSendChange: (Boolean) -> Unit,
    chatFontSize: String,
    onChatFontSizeChange: (String) -> Unit,
    isFullSettings: Boolean = true
) {
    var newUsername by remember { mutableStateOf(currentUsername) }
    var newPassword by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val isAdmin = currentUsername.lowercase() == "admin"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.get("settings", language), style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = Strings.get("back", language))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // --- SECTION: APPEARANCE ---
            SettingsSection(title = Strings.get("appearance", language)) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Theme Selection
                    Text(Strings.get("theme", language), style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOption(
                            text = Strings.get("theme_system", language),
                            isSelected = theme == "system",
                            onClick = { onThemeChange("system") },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOption(
                            text = Strings.get("theme_light", language),
                            isSelected = theme == "light",
                            onClick = { onThemeChange("light") },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOption(
                            text = Strings.get("theme_dark", language),
                            isSelected = theme == "dark",
                            onClick = { onThemeChange("dark") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Color Selection
                    Text(Strings.get("primary_color", language), style = MaterialTheme.typography.titleSmall)
                    var showColorDialog by remember { mutableStateOf(false) }
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ColorCircle("blue", Color(0xFF2563eb), primaryColor == "blue", onPrimaryColorChange)
                            ColorCircle("green", Color(0xFF22c55e), primaryColor == "green", onPrimaryColorChange)
                            ColorCircle("purple", Color(0xFFa855f7), primaryColor == "purple", onPrimaryColorChange)
                            ColorCircle("orange", Color(0xFFf97316), primaryColor == "orange", onPrimaryColorChange)
                            ColorCircle("pink", Color(0xFFec4899), primaryColor == "pink", onPrimaryColorChange)
                            
                            // More colors button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .clickable { showColorDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "More colors",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        if (primaryColor.startsWith("#")) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                try { Color(primaryColor.removePrefix("#").toLong(16) or 0xFF000000) }
                                                catch (e: Exception) { MaterialTheme.colorScheme.primary }
                                            )
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Custom: $primaryColor",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (showColorDialog) {
                        ColorPickerDialog(
                            title = Strings.get("primary_color", language),
                            currentLanguage = language,
                            onDismiss = { showColorDialog = false },
                            onColorSelect = { 
                                onPrimaryColorChange(it)
                                showColorDialog = false
                            }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    // Background Color Selection
                    Text("Hintergrundfarbe", style = MaterialTheme.typography.titleSmall)
                    var showBgColorDialog by remember { mutableStateOf(false) }

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ColorCircle("default", if (theme == "dark") Color(0xFF070d1a) else Color(0xFFf8fafc), backgroundColor == "default", onBackgroundColorChange)
                            ColorCircle("black", Color(0xFF000000), backgroundColor == "black", onBackgroundColorChange)
                            ColorCircle("darkblue", Color(0xFF0f172a), backgroundColor == "darkblue", onBackgroundColorChange)
                            ColorCircle("white", Color(0xFFFFFFFF), backgroundColor == "white", onBackgroundColorChange)

                            // More colors button
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .clickable { showBgColorDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "More colors",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        if (backgroundColor.startsWith("#")) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                try { Color(backgroundColor.removePrefix("#").toLong(16) or 0xFF000000) }
                                                catch (e: Exception) { MaterialTheme.colorScheme.background }
                                            )
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Custom Background: $backgroundColor",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    if (showBgColorDialog) {
                        ColorPickerDialog(
                            title = "Hintergrundfarbe",
                            currentLanguage = language,
                            onDismiss = { showBgColorDialog = false },
                            onColorSelect = {
                                onBackgroundColorChange(it)
                                showBgColorDialog = false
                            }
                        )
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    
                    // Enter to Send Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(Strings.get("enter_to_send", language), style = MaterialTheme.typography.titleSmall)
                        Switch(
                            checked = enterToSend,
                            onCheckedChange = onEnterToSendChange
                        )
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    
                    // Chat Font Size
                    Text(Strings.get("chat_font_size", language), style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeOption(
                            text = Strings.get("font_size_small", language),
                            isSelected = chatFontSize == "small",
                            onClick = { onChatFontSizeChange("small") },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOption(
                            text = Strings.get("font_size_medium", language),
                            isSelected = chatFontSize == "medium",
                            onClick = { onChatFontSizeChange("medium") },
                            modifier = Modifier.weight(1f)
                        )
                        ThemeOption(
                            text = Strings.get("font_size_large", language),
                            isSelected = chatFontSize == "large",
                            onClick = { onChatFontSizeChange("large") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // --- SECTION: PROFIL ---
            if (isFullSettings) {
                SettingsSection(title = Strings.get("profile", language)) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = newUsername,
                            onValueChange = { newUsername = it },
                            label = { Text(Strings.get("username", language)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isAdmin
                        )

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text(Strings.get("new_password", language)) },
                            placeholder = { Text(Strings.get("password_hint", language)) },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isAdmin
                        )

                        Button(
                            onClick = { onSaveProfile(newUsername, newPassword) },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isAdmin
                        ) {
                            Text(Strings.get("save", language), style = MaterialTheme.typography.labelLarge)
                        }

                        if (isAdmin) {
                            Text(
                                Strings.get("admin_info", language),
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                // --- SECTION: DANGER ZONE ---
                SettingsSection(title = Strings.get("danger_zone", language), titleColor = MaterialTheme.colorScheme.error) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            Strings.get("danger_desc", language),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Button(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isAdmin,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                            )
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(Strings.get("delete_account", language), style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }

            // --- SECTION: ABOUT ---
            SettingsSection(title = Strings.get("about", language)) {
                OutlinedButton(
                    onClick = { showAboutDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Info, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(Strings.get("about", language), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(Strings.get("delete_confirm_title", language)) },
            text = { Text(Strings.get("delete_confirm_text", language)) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteAccount()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(Strings.get("delete", language))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(Strings.get("cancel", language))
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = RoundedCornerShape(28.dp)
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("R", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Text("RICHARD", style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        Strings.get("version", language),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        Strings.get("about_desc", language),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
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
fun SettingsSection(
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.primary,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = title.uppercase(),
            color = titleColor,
            style = MaterialTheme.typography.labelLarge,
            letterSpacing = 1.sp
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerDialog(
    title: String,
    currentLanguage: String,
    onDismiss: () -> Unit,
    onColorSelect: (String) -> Unit
) {
    var hexInput by remember { mutableStateOf("") }
    val presetColors = listOf(
        "#2563eb", "#22c55e", "#a855f7", "#f97316", "#ec4899", "#ef4444",
        "#06b6d4", "#f59e0b", "#14b8a6", "#8b5cf6", "#6366f1", "#d946ef",
        "#000000", "#FFFFFF", "#0f172a", "#1e293b", "#334155", "#475569"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetColors.forEach { colorHex ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(colorHex.removePrefix("#").toLong(16) or 0xFF000000))
                                .clickable { onColorSelect(colorHex) }
                                .padding(2.dp)
                        )
                    }
                }

                HorizontalDivider()

                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { 
                        if (it.length <= 7) hexInput = if (it.startsWith("#") || it.isEmpty()) it else "#$it"
                    },
                    label = { Text("Hex Code") },
                    placeholder = { Text("#RRGGBB") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    trailingIcon = {
                        if (hexInput.length >= 6) {
                            IconButton(onClick = { onColorSelect(hexInput) }) {
                                Icon(Icons.Default.Check, null)
                            }
                        }
                    }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(Strings.get("cancel", currentLanguage))
            }
        }
    )
}
