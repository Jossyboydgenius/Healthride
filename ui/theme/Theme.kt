package com.healthride.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Light color scheme
private val LightColorScheme = lightColorScheme(
    primary = HealthRideColors.Primary.primary,
    onPrimary = HealthRideColors.Primary.onPrimary,
    primaryContainer = HealthRideColors.Primary.primaryContainer,
    onPrimaryContainer = HealthRideColors.Primary.onPrimaryContainer,
    
    secondary = HealthRideColors.Secondary.secondary,
    onSecondary = HealthRideColors.Secondary.onSecondary,
    secondaryContainer = HealthRideColors.Secondary.secondaryContainer,
    onSecondaryContainer = HealthRideColors.Secondary.onSecondaryContainer,
    
    tertiary = HealthRideColors.Tertiary.tertiary,
    onTertiary = HealthRideColors.Tertiary.onTertiary,
    tertiaryContainer = HealthRideColors.Tertiary.tertiaryContainer,
    onTertiaryContainer = HealthRideColors.Tertiary.onTertiaryContainer,
    
    error = HealthRideColors.Error.error,
    onError = HealthRideColors.Error.onError,
    errorContainer = HealthRideColors.Error.errorContainer,
    onErrorContainer = HealthRideColors.Error.onErrorContainer,
    
    background = HealthRideColors.Neutral.background,
    onBackground = HealthRideColors.Neutral.onBackground,
    surface = HealthRideColors.Neutral.surface,
    onSurface = HealthRideColors.Neutral.onSurface,
    
    surfaceVariant = HealthRideColors.Neutral.surfaceVariant,
    onSurfaceVariant = HealthRideColors.Neutral.onSurfaceVariant,
    outline = HealthRideColors.Neutral.outline,
    outlineVariant = HealthRideColors.Neutral.outlineVariant
)

// Dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = HealthRideColors.DarkTheme.primary,
    onPrimary = HealthRideColors.DarkTheme.onPrimary,
    primaryContainer = HealthRideColors.DarkTheme.primaryContainer,
    onPrimaryContainer = HealthRideColors.DarkTheme.onPrimaryContainer,
    
    secondary = HealthRideColors.DarkTheme.secondary,
    onSecondary = HealthRideColors.DarkTheme.onSecondary,
    secondaryContainer = HealthRideColors.DarkTheme.secondaryContainer,
    onSecondaryContainer = HealthRideColors.DarkTheme.onSecondaryContainer,
    
    tertiary = HealthRideColors.DarkTheme.tertiary,
    onTertiary = HealthRideColors.DarkTheme.onTertiary,
    tertiaryContainer = HealthRideColors.DarkTheme.tertiaryContainer,
    onTertiaryContainer = HealthRideColors.DarkTheme.onTertiaryContainer,
    
    error = HealthRideColors.Error.error,
    onError = HealthRideColors.Error.onError,
    errorContainer = HealthRideColors.Error.errorContainer,
    onErrorContainer = HealthRideColors.Error.onErrorContainer,
    
    background = HealthRideColors.DarkTheme.background,
    onBackground = HealthRideColors.DarkTheme.onBackground,
    surface = HealthRideColors.DarkTheme.surface,
    onSurface = HealthRideColors.DarkTheme.onSurface,
    
    surfaceVariant = HealthRideColors.DarkTheme.surfaceVariant,
    onSurfaceVariant = HealthRideColors.DarkTheme.onSurfaceVariant,
    outline = HealthRideColors.DarkTheme.outline,
    outlineVariant = HealthRideColors.DarkTheme.outlineVariant
)

@Composable
fun HealthRideTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
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
        typography = HealthRideTypography,
        shapes = HealthRideShapes,
        content = content
    )
}