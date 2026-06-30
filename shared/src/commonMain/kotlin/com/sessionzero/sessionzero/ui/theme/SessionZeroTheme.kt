package com.sessionzero.sessionzero.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SessionZeroColorScheme = lightColorScheme(
    primary = Color(0xFF1A1A2E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE8E8F0),
    onPrimaryContainer = Color(0xFF1A1A2E),
    secondary = Color(0xFF4A4A6A),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF8F8FC),
    onBackground = Color(0xFF1A1A2E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFF0F0F8),
    onSurfaceVariant = Color(0xFF5A5A7A),
    outline = Color(0xFFD0D0E0),
)

@Composable
fun SessionZeroTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SessionZeroColorScheme,
        content = content,
    )
}
