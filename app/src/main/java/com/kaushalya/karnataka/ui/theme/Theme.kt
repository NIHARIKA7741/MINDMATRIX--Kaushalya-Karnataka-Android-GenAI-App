package com.kaushalya.karnataka.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = Teal40,
    onSecondary = White,
    secondaryContainer = Teal90,
    onSecondaryContainer = Green10,
    tertiary = Amber40,
    onTertiary = White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber40,
    error = ErrorRed,
    errorContainer = ErrorRedContainer,
    onError = White,
    onErrorContainer = ErrorRed,
    background = NeutralGray99,
    onBackground = NeutralGray10,
    surface = SurfaceLight,
    onSurface = NeutralGray10,
    surfaceVariant = Green95,
    onSurfaceVariant = Green30,
    outline = Green60,
    outlineVariant = Green90,
    inverseSurface = NeutralGray20,
    inverseOnSurface = NeutralGray95,
    inversePrimary = Green80,
    surfaceTint = Green40
)

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = Teal80,
    onSecondary = Green20,
    secondaryContainer = Teal40,
    onSecondaryContainer = Teal90,
    tertiary = Amber80,
    onTertiary = Amber40,
    tertiaryContainer = Amber40,
    onTertiaryContainer = Amber90,
    error = ErrorRedContainer,
    errorContainer = ErrorRed,
    onError = ErrorRed,
    onErrorContainer = ErrorRedContainer,
    background = NeutralGray10,
    onBackground = NeutralGray90,
    surface = SurfaceDark,
    onSurface = NeutralGray90,
    surfaceVariant = Green20,
    onSurfaceVariant = Green80,
    outline = Green60,
    outlineVariant = Green30,
    inverseSurface = NeutralGray90,
    inverseOnSurface = NeutralGray20,
    inversePrimary = Green40,
    surfaceTint = Green80
)

@Composable
fun KaushalyaKarnatakaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
