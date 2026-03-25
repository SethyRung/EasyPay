package com.sethy.easypay.ui.theme

import androidx.compose.material3.MaterialTheme
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

@Composable
@Suppress("UNUSED_PARAMETER")
fun EasyPayTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
