package org.turtledev.richard.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.turtledev.richard.ui.Strings
import org.turtledev.richard.ui.theme.*

enum class SetupStep {
    Language,
    Appearance,
    ServerIp
}

data class LanguageOption(val name: String, val code: String, val flag: String)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SetupScreen(
    onServerIpSubmit: (String) -> Unit,
    onLanguageSelect: (String) -> Unit,
    initialLanguage: String,
    theme: String,
    onThemeChange: (String) -> Unit,
    primaryColor: String,
    onPrimaryColorChange: (String) -> Unit,
    backgroundColor: String,
    onBackgroundColorChange: (String) -> Unit
) {
    var ip by remember { mutableStateOf("") }
    var visible by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(SetupStep.Language) }
    
    val languages = remember {
        listOf(
            LanguageOption("Deutsch", "de", "🇩🇪"),
            LanguageOption("English", "en", "🇺🇸"),
            LanguageOption("Français", "fr", "🇫🇷"),
            LanguageOption("Español", "es", "🇪🇸"),
            LanguageOption("Italiano", "it", "🇮🇹"),
            LanguageOption("Türkçe", "tr", "🇹🇷")
        )
    }
    
    // Sync local selected language with VM state
    var selectedLanguage by remember(initialLanguage) { 
        mutableStateOf(languages.find { it.code == initialLanguage } ?: languages[0]) 
    }

    // Animation States
    val infiniteTransition = rememberInfiniteTransition()
    
    val bgAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    val primary = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Dynamic background glow
        Box(
            modifier = Modifier
                .size(500.dp)
                .alpha(bgAlpha * 0.15f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(primary, Color.Transparent)
                    )
                )
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 4 },
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .padding(24.dp)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Back Button (Top Left)
                    if (currentStep != SetupStep.Language) {
                        IconButton(
                            onClick = { 
                                currentStep = when(currentStep) {
                                    SetupStep.Appearance -> SetupStep.Language
                                    SetupStep.ServerIp -> SetupStep.Appearance
                                    else -> SetupStep.Language
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(top = 32.dp, bottom = 32.dp, start = 24.dp, end = 24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Animated Logo
                        Box(
                            modifier = Modifier
                                .scale(logoScale)
                                .size(72.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("R", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                        }

                        Spacer(Modifier.height(24.dp))
                        
                        AnimatedContent(
                            targetState = currentStep,
                            transitionSpec = {
                                if (targetState == SetupStep.ServerIp) {
                                    (slideInHorizontally { it } + fadeIn()).togetherWith(slideOutHorizontally { -it } + fadeOut())
                                } else {
                                    (slideInHorizontally { -it } + fadeIn()).togetherWith(slideOutHorizontally { it } + fadeOut())
                                }.using(SizeTransform(clip = false))
                            },
                            label = "SetupStepTransition"
                        ) { step ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                when (step) {
                                    SetupStep.Language -> {
                                        Text(
                                            Strings.get("welcome", selectedLanguage.code),
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 1.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            Strings.get("choose_lang", selectedLanguage.code),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        Spacer(Modifier.height(32.dp))
                                        
                                        languages.forEach { lang ->
                                            LanguageItem(
                                                language = lang,
                                                isSelected = selectedLanguage.code == lang.code,
                                                onClick = { 
                                                    selectedLanguage = lang
                                                    onLanguageSelect(lang.code)
                                                }
                                            )
                                            Spacer(Modifier.height(10.dp))
                                        }
                                        
                                        Spacer(Modifier.height(24.dp))
                                        
                                        Button(
                                            onClick = { currentStep = SetupStep.Appearance },
                                            modifier = Modifier.fillMaxWidth().height(56.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text(Strings.get("next", selectedLanguage.code), fontWeight = FontWeight.Bold)
                                            Spacer(Modifier.width(8.dp))
                                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp))
                                        }
                                    }
                                    SetupStep.Appearance -> {
                                        Text(
                                            Strings.get("appearance", selectedLanguage.code),
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 1.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        
                                        Spacer(Modifier.height(24.dp))

                                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                            // Theme Selection
                                            Text(Strings.get("theme", selectedLanguage.code), style = MaterialTheme.typography.titleSmall)
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                ThemeOption(
                                                    text = Strings.get("theme_system", selectedLanguage.code),
                                                    isSelected = theme == "system",
                                                    onClick = { onThemeChange("system") },
                                                    modifier = Modifier.weight(1f)
                                                )
                                                ThemeOption(
                                                    text = Strings.get("theme_light", selectedLanguage.code),
                                                    isSelected = theme == "light",
                                                    onClick = { onThemeChange("light") },
                                                    modifier = Modifier.weight(1f)
                                                )
                                                ThemeOption(
                                                    text = Strings.get("theme_dark", selectedLanguage.code),
                                                    isSelected = theme == "dark",
                                                    onClick = { onThemeChange("dark") },
                                                    modifier = Modifier.weight(1f)
                                                )
                                            }

                                            Spacer(Modifier.height(8.dp))

                                            // Color Selection
                                            Text(Strings.get("primary_color", selectedLanguage.code), style = MaterialTheme.typography.titleSmall)
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
                                            }
                                        }

                                        Spacer(Modifier.height(32.dp))

                                        Button(
                                            onClick = { currentStep = SetupStep.ServerIp },
                                            modifier = Modifier.fillMaxWidth().height(56.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text(Strings.get("next", selectedLanguage.code), fontWeight = FontWeight.Bold)
                                            Spacer(Modifier.width(8.dp))
                                            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.size(18.dp))
                                        }
                                    }
                                    SetupStep.ServerIp -> {
                                        Text(
                                            "RICHARD",
                                            style = MaterialTheme.typography.headlineMedium.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 2.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            Strings.get("setup_title", selectedLanguage.code),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        
                                        Spacer(Modifier.height(40.dp))

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                Strings.get("server_addr", selectedLanguage.code),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                            
                                            OutlinedTextField(
                                                value = ip,
                                                onValueChange = { ip = it },
                                                placeholder = { Text("z.B. 192.168.1.50") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                singleLine = true,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                                )
                                            )
                                        }

                                        Spacer(Modifier.height(40.dp))

                                        Button(
                                            onClick = { if (ip.isNotEmpty()) onServerIpSubmit(ip) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(60.dp),
                                            shape = RoundedCornerShape(16.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                        ) {
                                            Text(
                                                Strings.get("connect", selectedLanguage.code), 
                                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LanguageItem(
    language: LanguageOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(if (isSelected) 1.03f else 1f)
    val primary = MaterialTheme.colorScheme.primary
    val backgroundColor by animateColorAsState(
        if (isSelected) primary.copy(alpha = 0.12f) else Color.Transparent
    )
    val borderColor by animateColorAsState(
        if (isSelected) primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(language.flag, fontSize = 22.sp)
            Spacer(Modifier.width(16.dp))
            Text(
                language.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) primary else MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.weight(1f))
            if (isSelected) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
