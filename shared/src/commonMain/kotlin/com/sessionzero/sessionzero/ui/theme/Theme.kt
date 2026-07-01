package com.sessionzero.sessionzero.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily

private fun sessionZeroTypography(flavor: SystemFlavor): Typography {
    // Headlines fall back to Monospace for CoC's typewriter/report feel; every other flavor uses Serif.
    val headlineFont = if (flavor == SystemFlavor.COC) FontFamily.Monospace else FontFamily.Serif
    val base = Typography()
    return base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = headlineFont),
        displayMedium = base.displayMedium.copy(fontFamily = headlineFont),
        displaySmall = base.displaySmall.copy(fontFamily = headlineFont),
        headlineLarge = base.headlineLarge.copy(fontFamily = headlineFont),
        headlineMedium = base.headlineMedium.copy(fontFamily = headlineFont),
        headlineSmall = base.headlineSmall.copy(fontFamily = headlineFont),
        titleLarge = base.titleLarge.copy(fontFamily = headlineFont),
        titleMedium = base.titleMedium.copy(fontFamily = headlineFont),
    )
}

@Composable
fun SessionZeroTheme(
    flavor: SystemFlavor = SystemFlavor.NEUTRAL,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = when (flavor) {
        SystemFlavor.NEUTRAL -> if (darkTheme) NeutralDarkColorScheme else NeutralColorScheme
        SystemFlavor.DND -> if (darkTheme) DndDarkColorScheme else DndColorScheme
        SystemFlavor.COC -> if (darkTheme) CocDarkColorScheme else CocColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = sessionZeroTypography(flavor),
        content = content,
    )
}
