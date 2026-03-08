package com.sethy.easypay.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = BrandBlack,
    onPrimary = BrandLight,
    primaryContainer = OffWhite,
    onPrimaryContainer = BrandBlack,

    secondary = GrayDark,
    onSecondary = BrandLight,
    secondaryContainer = OffWhite,
    onSecondaryContainer = BrandBlack,

    tertiary = GrayMedium,
    onTertiary = BrandLight,

    background = BrandLight,
    onBackground = BrandBlack,

    surface = BrandLight,
    onSurface = BrandBlack,
    surfaceVariant = OffWhite,
    onSurfaceVariant = GrayDark,

    outline = GrayDark,
    outlineVariant = GrayMedium,
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandLight,
    onPrimary = BrandBlack,
    primaryContainer = BrandBlack,
    onPrimaryContainer = BrandLight,

    secondary = GrayMedium,
    onSecondary = BrandBlack,
    secondaryContainer = BrandBlack,
    onSecondaryContainer = BrandLight,

    tertiary = GrayDark,
    onTertiary = BrandBlack,

    background = BrandBlack,
    onBackground = BrandLight,

    surface = BrandBlack,
    onSurface = BrandLight,
    surfaceVariant = GrayDark,
    onSurfaceVariant = GrayMedium,

    outline = GrayMedium,
    outlineVariant = GrayDark,
)

@Composable
fun EasyPayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to maintain brand consistency
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // Use light scheme for dynamic colors to maintain brand identity
            LightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
