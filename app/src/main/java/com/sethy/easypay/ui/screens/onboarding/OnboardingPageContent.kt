package com.sethy.easypay.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Sparkles
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.SurfaceCard

data class OnboardingPage(
    val icon: ImageVector,
    val title: String,
    val body: String
)

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(SurfaceCard),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(72.dp)
            )
        }

        Spacer(modifier = Modifier.height(EasyPaySpacing.xl))

        Text(
            text = page.title,
            style = EasyPayTypography.displaySM,
            color = Ink,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(EasyPaySpacing.sm))

        Text(
            text = page.body,
            style = EasyPayTypography.bodyMD,
            color = Muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = EasyPaySpacing.md)
        )
    }
}

@Preview
@Composable
private fun OnboardingPageContentPreview() {
    EasyPayTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                icon = Lucide.Sparkles,
                title = "Welcome to EasyPay",
                body = "Pay anyone, anywhere — straight from your phone."
            )
        )
    }
}