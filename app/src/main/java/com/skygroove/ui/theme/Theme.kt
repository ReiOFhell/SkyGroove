package com.skygroove.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ImperialScheme = darkColorScheme(
    primary = Color(0xFFB11226),
    secondary = Color(0xFFC6A75E),
    background = Color(0xFF0B0B0F),
    surface = Color(0xFF15151C),
    onSurface = Color(0xFFE2E2E8)
)

@Composable
fun SkyGrooveTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ImperialScheme, typography = Typography, content = content)
}
