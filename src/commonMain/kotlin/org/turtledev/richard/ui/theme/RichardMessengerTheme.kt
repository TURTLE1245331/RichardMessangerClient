package org.turtledev.richard.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.ui.graphics.compositeOver

// Brand Colors
val RichardBlue = Color(0xFF60a5fa)
val RichardGreen = Color(0xFF4ade80)
val RichardPurple = Color(0xFFc084fc)
val RichardOrange = Color(0xFFfb923c)
val RichardPink = Color(0xFFf472b6)

private fun getDynamicDarkColorScheme(primaryColor: Color, backgroundColor: Color): androidx.compose.material3.ColorScheme {
    val surfaceColor = primaryColor.copy(alpha = 0.08f).compositeOver(backgroundColor)
    val surfaceVariantColor = primaryColor.copy(alpha = 0.15f).compositeOver(backgroundColor)
    
    return darkColorScheme(
        primary = primaryColor,
        onPrimary = Color.White,
        primaryContainer = primaryColor.copy(alpha = 0.25f),
        onPrimaryContainer = Color.White,
        secondary = primaryColor.copy(alpha = 0.7f),
        onSecondary = Color.White,
        secondaryContainer = primaryColor.copy(alpha = 0.15f),
        onSecondaryContainer = Color.White,
        tertiary = primaryColor.copy(alpha = 0.5f),
        onTertiary = Color.White,
        tertiaryContainer = primaryColor.copy(alpha = 0.1f),
        onTertiaryContainer = primaryColor,
        background = backgroundColor,
        onBackground = Color(0xFFe2e8f0),
        surface = surfaceColor,
        onSurface = Color(0xFFe2e8f0),
        surfaceVariant = surfaceVariantColor,
        onSurfaceVariant = Color(0xFFcbd5e1),
        outline = primaryColor.copy(alpha = 0.3f),
        outlineVariant = primaryColor.copy(alpha = 0.15f),
        error = Color(0xFFf87171),
    )
}

private fun getDynamicLightColorScheme(primaryColor: Color, backgroundColor: Color): androidx.compose.material3.ColorScheme {
    val surfaceColor = Color.White
    val surfaceVariantColor = primaryColor.copy(alpha = 0.05f).compositeOver(Color.White)

    return lightColorScheme(
        primary = primaryColor,
        onPrimary = Color.White,
        primaryContainer = primaryColor.copy(alpha = 0.12f),
        onPrimaryContainer = primaryColor,
        secondary = primaryColor.copy(alpha = 0.6f),
        onSecondary = Color.White,
        secondaryContainer = primaryColor.copy(alpha = 0.08f),
        onSecondaryContainer = primaryColor,
        tertiary = primaryColor.copy(alpha = 0.4f),
        onTertiary = Color.White,
        tertiaryContainer = primaryColor.copy(alpha = 0.05f),
        onTertiaryContainer = primaryColor,
        background = backgroundColor,
        onBackground = Color(0xFF0f172a),
        surface = surfaceColor,
        onSurface = Color(0xFF0f172a),
        surfaceVariant = surfaceVariantColor,
        onSurfaceVariant = Color(0xFF475569),
        outline = primaryColor.copy(alpha = 0.25f),
        outlineVariant = primaryColor.copy(alpha = 0.1f),
        error = Color(0xFFef4444),
    )
}

private val RichardTypography = Typography(
    headlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, letterSpacing = (-1).sp),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
    titleLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp),
)

@Composable
fun RichardMessengerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryColorName: String = "blue",
    backgroundColorName: String = "default",
    content: @Composable () -> Unit
) {
    fun parseColor(name: String, default: Color): Color {
        return when(name) {
            "green" -> Color(0xFF22c55e)
            "purple" -> Color(0xFFa855f7)
            "orange" -> Color(0xFFf97316)
            "pink" -> Color(0xFFec4899)
            "red" -> Color(0xFFef4444)
            "blue" -> Color(0xFF2563eb)
            "black" -> Color(0xFF000000)
            "white" -> Color(0xFFFFFFFF)
            "darkblue" -> Color(0xFF0f172a)
            else -> {
                if (name.startsWith("#")) {
                    try {
                        val colorHex = name.removePrefix("#")
                        val argb = if (colorHex.length == 6) {
                            "FF$colorHex".toLong(16)
                        } else {
                            colorHex.toLong(16)
                        }
                        Color(argb)
                    } catch (e: Exception) {
                        default
                    }
                } else {
                    default
                }
            }
        }
    }

    val primaryColor = parseColor(primaryColorName, Color(0xFF2563eb))
    val backgroundColor = if (backgroundColorName == "default") {
        if (darkTheme) Color(0xFF070d1a) else Color(0xFFf8fafc)
    } else {
        parseColor(backgroundColorName, if (darkTheme) Color(0xFF070d1a) else Color(0xFFf8fafc))
    }

    val colorScheme = if (darkTheme) {
        getDynamicDarkColorScheme(primaryColor, backgroundColor)
    } else {
        getDynamicLightColorScheme(primaryColor, backgroundColor)
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = RichardTypography,
        content = content
    )
}
