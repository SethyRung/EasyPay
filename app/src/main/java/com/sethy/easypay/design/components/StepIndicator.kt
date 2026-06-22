package com.sethy.easypay.design.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.sethy.easypay.design.*

@Composable
fun StepIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(EasyPaySpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until totalSteps) {
            StepCircle(
                index = i,
                isActive = i == currentStep,
                isCompleted = i < currentStep
            )
            if (i < totalSteps - 1) {
                StepConnector(isCompleted = i < currentStep)
            }
        }
    }
}

@Composable
private fun StepCircle(
    index: Int,
    isActive: Boolean,
    isCompleted: Boolean
) {
    val backgroundColor by animateColorAsState(
        targetValue = when {
            isActive || isCompleted -> Primary
            else -> SurfaceCard
        },
        label = "stepCircleBackground"
    )
    val contentColor by animateColorAsState(
        targetValue = when {
            isActive || isCompleted -> OnPrimary
            else -> Muted
        },
        label = "stepCircleContent"
    )

    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(
                imageVector = Lucide.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Text(
                text = "${index + 1}",
                style = EasyPayTypography.caption.copy(fontWeight = FontWeight.SemiBold),
                color = contentColor
            )
        }
    }
}

@Composable
private fun RowScope.StepConnector(isCompleted: Boolean) {
    val color by animateColorAsState(
        targetValue = if (isCompleted) Primary else SurfaceSoft,
        label = "stepConnectorColor"
    )
    Box(
        modifier = Modifier
            .weight(1f)
            .height(3.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Preview
@Composable
private fun StepIndicatorPreview() {
    EasyPayTheme {
        StepIndicator(currentStep = 1, totalSteps = 3)
    }
}
