/*
 * Design system typography from DESIGN.md.
 * Uses Geist as the single type family; Geist Mono is not bundled,
 * so the system monospace family is used as a fallback for code blocks.
 */
package com.sethy.easypay.design

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sethy.easypay.R

val Geist = FontFamily(
    Font(R.font.geist_regular, FontWeight.Normal),
    Font(R.font.geist_medium, FontWeight.Medium),
    Font(R.font.geist_semibold, FontWeight.SemiBold)
)

val GeistMono = FontFamily.Monospace

object EasyPayTypography {
    val displayXL = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Normal,
        fontSize = 64.sp,
        lineHeight = 67.2.sp,
        letterSpacing = (-1.5).sp
    )
    val displayLG = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        lineHeight = 52.8.sp,
        letterSpacing = (-1).sp
    )
    val displayMD = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 41.4.sp,
        letterSpacing = (-0.5).sp
    )
    val displaySM = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 33.6.sp,
        letterSpacing = (-0.3).sp
    )
    val titleLG = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.6.sp,
        letterSpacing = 0.sp
    )
    val titleMD = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 25.2.sp,
        letterSpacing = 0.sp
    )
    val titleSM = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.4.sp,
        letterSpacing = 0.sp
    )
    val bodyMD = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.8.sp,
        letterSpacing = 0.sp
    )
    val bodySM = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.7.sp,
        letterSpacing = 0.sp
    )
    val caption = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 18.2.sp,
        letterSpacing = 0.sp
    )
    val captionUppercase = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.8.sp,
        letterSpacing = 1.5.sp
    )
    val code = TextStyle(
        fontFamily = GeistMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.4.sp,
        letterSpacing = 0.sp
    )
    val button = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp
    )
    val navLink = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 19.6.sp,
        letterSpacing = 0.sp
    )
}

val Material3Typography = Typography(
    displayLarge = EasyPayTypography.displayXL,
    displayMedium = EasyPayTypography.displayLG,
    displaySmall = EasyPayTypography.displayMD,
    headlineLarge = EasyPayTypography.displaySM,
    headlineMedium = EasyPayTypography.titleLG,
    headlineSmall = EasyPayTypography.titleMD,
    titleLarge = EasyPayTypography.titleLG,
    titleMedium = EasyPayTypography.titleMD,
    titleSmall = EasyPayTypography.titleSM,
    bodyLarge = EasyPayTypography.bodyMD,
    bodyMedium = EasyPayTypography.bodySM,
    bodySmall = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.6.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = EasyPayTypography.button,
    labelMedium = EasyPayTypography.caption,
    labelSmall = TextStyle(
        fontFamily = Geist,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.4.sp,
        letterSpacing = 0.5.sp
    )
)
