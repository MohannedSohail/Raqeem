package org.moh.raqeem.project.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

// Palette: Arabic Fintech (Dark Slate + Emerald Green + Coral Expense Accent)
val EmeraldPrimary = Color(0xFF10B981)
val EmeraldDark = Color(0xFF059669)
val EmeraldLight = Color(0xFF34D399)

val SlateBackgroundDark = Color(0xFF0F172A)
val SlateSurfaceDark = Color(0xFF1E293B)
val SlateCardDark = Color(0xFF334155)

val SlateBackgroundLight = Color(0xFFF8FAFC)
val SlateSurfaceLight = Color(0xFFFFFFFF)
val SlateCardLight = Color(0xFFF1F5F9)

val CoralExpense = Color(0xFFF43F5E)
val AmberPending = Color(0xFFF59E0B)
val BluePayer = Color(0xFF3B82F6)

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.Black,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = Color.White,
    secondary = BluePayer,
    background = SlateBackgroundDark,
    surface = SlateSurfaceDark,
    surfaceVariant = SlateCardDark,
    onBackground = Color(0xFFF8FAFC),
    onSurface = Color(0xFFF8FAFC),
    error = CoralExpense
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = BluePayer,
    background = SlateBackgroundLight,
    surface = SlateSurfaceLight,
    surfaceVariant = SlateCardLight,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    error = CoralExpense
)

@Composable
fun RaqeemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Enforce Right-to-Left (RTL) for Arabic-first UI
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

