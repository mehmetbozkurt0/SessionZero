package com.sessionzero.sessionzero.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.sessionzero.sessionzero.data.dnd5e.ClassCategory

private val WalnutBrown = Color(0xFF6B4A32)

// NEUTRAL — Library/Dashboard: matte parchment background, walnut brown accent
val NeutralColorScheme: ColorScheme = lightColorScheme(
    primary = WalnutBrown,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE6DCC8),
    onPrimaryContainer = WalnutBrown,
    secondary = Color(0xFF8A7659),
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF3EFE3),
    onBackground = Color(0xFF33291D),
    surface = Color(0xFFFAF7EF),
    onSurface = Color(0xFF33291D),
    surfaceVariant = Color(0xFFEAE3D2),
    onSurfaceVariant = Color(0xFF6B604E),
    outline = Color(0xFFD8CDB4),
)

private val Burgundy = Color(0xFF7A2333)
private val DeepEmerald = Color(0xFF1E4D3A)

// DND — Power Fantasy: cream background, burgundy accent, deep emerald secondary
val DndColorScheme: ColorScheme = lightColorScheme(
    primary = Burgundy,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFEFD9DC),
    onPrimaryContainer = Burgundy,
    secondary = DeepEmerald,
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFF5EFE0),
    onBackground = Color(0xFF2A211A),
    surface = Color(0xFFFAF5EA),
    onSurface = Color(0xFF2A211A),
    surfaceVariant = Color(0xFFEAE0CB),
    onSurfaceVariant = Color(0xFF6B5D46),
    outline = Color(0xFFD9CBA9),
)

private val Rust = Color(0xFF8A4A2E)
private val SicklyGreen = Color(0xFF5C6B3F)

// COC — Noir/Horror: aged typewriter paper background, rust accent, sickly green secondary
val CocColorScheme: ColorScheme = lightColorScheme(
    primary = Rust,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE0D4BE),
    onPrimaryContainer = Rust,
    secondary = SicklyGreen,
    onSecondary = Color(0xFFFFFFFF),
    background = Color(0xFFDEDACB),
    onBackground = Color(0xFF2B2A22),
    surface = Color(0xFFE7E3D3),
    onSurface = Color(0xFF2B2A22),
    surfaceVariant = Color(0xFFD2CCB6),
    onSurfaceVariant = Color(0xFF5A5744),
    outline = Color(0xFFB8B096),
)

// ---- Dark mode — shared "candlelit obsidian" base ----
// No pure black: obsidian/charcoal background keeps the parchment-y warmth after dark.
private val ObsidianBackground = Color(0xFF1A1C1E)
private val AnthraciteSurface = Color(0xFF232527)
private val AnthraciteSurfaceVariant = Color(0xFF2A2C2E)
private val BoneWhite = Color(0xFFE0E2E4)
private val MutedSilver = Color(0xFFB5B7B9)
private val GunmetalOutline = Color(0xFF3A3A3A)

// NEUTRAL DARK — Library/Dashboard by candlelight: obsidian background, warm walnut accent
private val WalnutBrownDark = Color(0xFF9C7148)
val NeutralDarkColorScheme: ColorScheme = darkColorScheme(
    primary = WalnutBrownDark,
    onPrimary = Color(0xFF241608),
    primaryContainer = Color(0xFF3A2A18),
    onPrimaryContainer = Color(0xFFE9C9A0),
    secondary = Color(0xFFA08F6E),
    onSecondary = Color(0xFF20190E),
    background = ObsidianBackground,
    onBackground = BoneWhite,
    surface = AnthraciteSurface,
    onSurface = BoneWhite,
    surfaceVariant = AnthraciteSurfaceVariant,
    onSurfaceVariant = MutedSilver,
    outline = GunmetalOutline,
)

// DND DARK — Power Fantasy by candlelight: obsidian background, deep saturated burgundy accent
private val BurgundyDark = Color(0xFF6B1E30)
private val DeepEmeraldDark = Color(0xFF2F6B52)
val DndDarkColorScheme: ColorScheme = darkColorScheme(
    primary = BurgundyDark,
    onPrimary = BoneWhite,
    primaryContainer = Color(0xFF3D1420),
    onPrimaryContainer = Color(0xFFE9AEBB),
    secondary = DeepEmeraldDark,
    onSecondary = BoneWhite,
    background = ObsidianBackground,
    onBackground = BoneWhite,
    surface = AnthraciteSurface,
    onSurface = BoneWhite,
    surfaceVariant = AnthraciteSurfaceVariant,
    onSurfaceVariant = MutedSilver,
    outline = GunmetalOutline,
)

// COC DARK — Noir/Horror by candlelight: obsidian background, dark rust accent
private val RustDark = Color(0xFF6B3620)
private val SicklyGreenDark = Color(0xFF44502C)
val CocDarkColorScheme: ColorScheme = darkColorScheme(
    primary = RustDark,
    onPrimary = BoneWhite,
    primaryContainer = Color(0xFF3A1D10),
    onPrimaryContainer = Color(0xFFE8B79C),
    secondary = SicklyGreenDark,
    onSecondary = BoneWhite,
    background = ObsidianBackground,
    onBackground = BoneWhite,
    surface = AnthraciteSurface,
    onSurface = BoneWhite,
    surfaceVariant = AnthraciteSurfaceVariant,
    onSurfaceVariant = MutedSilver,
    outline = GunmetalOutline,
)

// Default accent matches the current theme's primary; CharacterSheetScreen overrides it based on class category.
val LocalSessionZeroAccent = staticCompositionLocalOf<Color> { WalnutBrown }

val ClassCategory.accentColor: Color
    get() = when (this) {
        ClassCategory.PHYSICAL -> Color(0xFFAF4637) // Terracotta red
        ClassCategory.STEALTH  -> Color(0xFF2E6B5E) // Forest green
        ClassCategory.DIVINE   -> Color(0xFF8B6914) // Ancient gold
        ClassCategory.MAGIC    -> Color(0xFF4A3580) // Deep indigo
    }
