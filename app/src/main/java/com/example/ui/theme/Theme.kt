package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RetroDarkColorScheme = darkColorScheme(
    primary = PixelGold,
    onPrimary = Color(0xFF1E1400),
    primaryContainer = PixelGoldDark,
    onPrimaryContainer = Color(0xFFFFFAEB),
    secondary = PixelCyan,
    onSecondary = Color(0xFF001F29),
    secondaryContainer = Color(0xFF004D5E),
    onSecondaryContainer = Color(0xFFD6F6FF),
    tertiary = PixelPurple,
    onTertiary = Color(0xFF280047),
    tertiaryContainer = Color(0xFF5B1F8C),
    onTertiaryContainer = Color(0xFFF3E8FF),
    background = DungeonDarkBg,
    onBackground = RetroTextPrimary,
    surface = DungeonSurface,
    onSurface = RetroTextPrimary,
    surfaceVariant = DungeonSurfaceVariant,
    onSurfaceVariant = RetroTextSecondary,
    outline = DungeonBorder,
    error = PixelRuby,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false, // Preserve crafted retro aesthetic
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = RetroDarkColorScheme,
        typography = Typography,
        content = content
    )
}
