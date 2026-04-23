package com.sethy.easypay.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = BrandBlack,
    onPrimary = BrandLight,
    primaryContainer = SurfaceSubtle,
    onPrimaryContainer = TextPrimary,

    secondary = TextSecondary,
    onSecondary = BrandLight,
    secondaryContainer = SurfaceSubtle,
    onSecondaryContainer = TextPrimary,

    tertiary = TextTertiary,
    onTertiary = BrandLight,

    background = BrandLight,
    onBackground = TextPrimary,

    surface = BrandLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceSubtle,
    onSurfaceVariant = TextSecondary,

    outline = BorderLight,
    outlineVariant = BorderSubtle,

    error = Error,
    onError = BrandLight,
    errorContainer = ErrorSoft,
    onErrorContainer = Error,

    surfaceTint = BrandBlack,
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(24.dp)
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
        shapes = AppShapes,
        content = content
    )
}
