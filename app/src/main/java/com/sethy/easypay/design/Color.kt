/*
 * Design system colors. Currently configured for dark mode default.
 * Token names are kept stable; values map to their dark-mode appearance.
 *   - "Dark" surface tokens are intentionally LIGHT in dark mode so the
 *     elevated cards (balance hero, top-up) still pop against the canvas.
 *   - "OnDark" tokens are intentionally DARK so text on those elevated
 *     cards stays readable.
 */
package com.sethy.easypay.design

import androidx.compose.ui.graphics.Color

// Brand
val Primary = Color(0xFFCC785C)
val PrimaryActive = Color(0xFFA9583E)
val PrimaryDisabled = Color(0xFF3A322E)

// Text (now light against dark canvas)
val Ink = Color(0xFFFAF9F5)
val BodyStrong = Color(0xFFE5E0D8)
val Body = Color(0xFFC5C0B8)
val Muted = Color(0xFF8E8B82)
val MutedSoft = Color(0xFF6E6B65)

// Surfaces (now dark)
val Canvas = Color(0xFF181715)
val SurfaceSoft = Color(0xFF1F1E1B)
val SurfaceCard = Color(0xFF252320)
val SurfaceCreamStrong = Color(0xFF2D2A26)
val SurfaceDark = Color(0xFF2D2A26)
val SurfaceDarkElevated = Color(0xFF3A332E)
val SurfaceDarkSoft = Color(0xFF252320)

// On-colors
val OnPrimary = Color(0xFFFFFFFF)
val OnDark = Color(0xFFFAF9F5)
val OnDarkSoft = Color(0xFFA09D96)

// Lines
val Hairline = Color(0xFF3A3733)
val HairlineSoft = Color(0xFF2A2825)

// Accents
val AccentTeal = Color(0xFF5DB8A6)
val AccentAmber = Color(0xFFE8A55A)

// Semantic
val Success = Color(0xFF5DB872)
val Warning = Color(0xFFD4A017)
val Error = Color(0xFFE07070)