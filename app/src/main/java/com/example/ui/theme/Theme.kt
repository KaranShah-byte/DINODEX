package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AmberGold,
    onPrimary = Color(0xFF241604),
    primaryContainer = Color(0xFF422C0E),
    onPrimaryContainer = Color(0xFFFFDDB3),
    secondary = JungleEmerald,
    onSecondary = Color(0xFF0D2816),
    secondaryContainer = Color(0xFF22422C),
    onSecondaryContainer = Color(0xFFB8E6C4),
    tertiary = MagmaRuby,
    onTertiary = Color(0xFF3B0B04),
    background = DeepCharcoal,
    onBackground = IvoryBone,
    surface = ObsidianSurface,
    onSurface = IvoryBone,
    surfaceVariant = DarkStoneVariant,
    onSurfaceVariant = SandstoneMuted,
    outline = OutlineDark,
    outlineVariant = Color(0xFF2E3540)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB06F0B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDF9E),
    onPrimaryContainer = Color(0xFF2C1900),
    secondary = Color(0xFF386A48),
    onSecondary = Color.White,
    tertiary = Color(0xFFB83B26),
    onTertiary = Color.White,
    background = Color(0xFFF9F7F3),
    onBackground = Color(0xFF1E1C1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E1C1A),
    surfaceVariant = Color(0xFFEBE6DE),
    onSurfaceVariant = Color(0xFF4C4740),
    outline = Color(0xFF7E766D),
    outlineVariant = Color(0xFFCDC6BC)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark immersive museum experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
