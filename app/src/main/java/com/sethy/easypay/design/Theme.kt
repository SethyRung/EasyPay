/*
 * EasyPay theme built from DESIGN.md tokens.
 * Wraps Material 3 with our colors, typography and shapes.
 */
package com.sethy.easypay.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryActive,
    onPrimaryContainer = OnPrimary,
    secondary = SurfaceCard,
    onSecondary = Ink,
    secondaryContainer = SurfaceSoft,
    onSecondaryContainer = Ink,
    tertiary = AccentTeal,
    onTertiary = OnPrimary,
    background = Canvas,
    onBackground = Ink,
    surface = Canvas,
    onSurface = Ink,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = Body,
    surfaceTint = Color.Transparent,
    inverseSurface = SurfaceDark,
    inverseOnSurface = OnDark,
    error = Error,
    onError = OnPrimary,
    errorContainer = Error.copy(alpha = 0.12f),
    onErrorContainer = Error,
    outline = Hairline,
    outlineVariant = HairlineSoft,
    scrim = SurfaceDark.copy(alpha = 0.5f)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(EasyPayRadius.xs),
    small = RoundedCornerShape(EasyPayRadius.sm),
    medium = RoundedCornerShape(EasyPayRadius.md),
    large = RoundedCornerShape(EasyPayRadius.lg),
    extraLarge = RoundedCornerShape(EasyPayRadius.xl)
)

val LocalSpacing = staticCompositionLocalOf { EasyPaySpacing }
val LocalRadius = staticCompositionLocalOf { EasyPayRadius }
val LocalDimens = staticCompositionLocalOf { EasyPayDimens }

@Composable
fun EasyPayTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalSpacing provides EasyPaySpacing,
        LocalRadius provides EasyPayRadius,
        LocalDimens provides EasyPayDimens
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = Material3Typography,
            shapes = AppShapes,
            content = content
        )
    }
}
