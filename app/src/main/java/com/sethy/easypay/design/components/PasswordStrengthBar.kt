package com.sethy.easypay.design.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Circle
import com.composables.icons.lucide.Lucide
import com.sethy.easypay.design.*

enum class PasswordStrength {
    WEAK, MEDIUM, STRONG
}

data class PasswordRequirements(
    val minLength: Boolean = false,
    val hasUppercase: Boolean = false,
    val hasLowercase: Boolean = false,
    val hasNumber: Boolean = false
) {
    fun allMet(): Boolean = minLength && hasUppercase && hasLowercase && hasNumber
}

@Composable
fun PasswordStrengthBar(
    strength: PasswordStrength,
    requirements: PasswordRequirements,
    modifier: Modifier = Modifier
) {
    val (trackColor, label) = when (strength) {
        PasswordStrength.WEAK -> Error to "Weak"
        PasswordStrength.MEDIUM -> AccentAmber to "Medium"
        PasswordStrength.STRONG -> Success to "Strong"
    }
    val progress = when (strength) {
        PasswordStrength.WEAK -> 0.33f
        PasswordStrength.MEDIUM -> 0.66f
        PasswordStrength.STRONG -> 1f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "strengthProgress"
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Hairline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(2.dp))
                        .background(trackColor)
                )
            }
            Spacer(modifier = Modifier.width(EasyPaySpacing.sm))
            Text(text = label, style = EasyPayTypography.caption, color = trackColor)
        }
        Spacer(modifier = Modifier.height(EasyPaySpacing.sm))
        PasswordRequirementItem("At least 8 characters", requirements.minLength)
        PasswordRequirementItem("One uppercase letter", requirements.hasUppercase)
        PasswordRequirementItem("One lowercase letter", requirements.hasLowercase)
        PasswordRequirementItem("One number", requirements.hasNumber)
    }
}

@Composable
private fun PasswordRequirementItem(label: String, met: Boolean) {
    val color = if (met) Success else Muted
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = if (met) Lucide.Check else Lucide.Circle,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(EasyPaySpacing.xs))
        Text(text = label, style = EasyPayTypography.bodySM, color = color)
    }
}

@Preview
@Composable
private fun PasswordStrengthBarPreview() {
    EasyPayTheme {
        PasswordStrengthBar(
            strength = PasswordStrength.MEDIUM,
            requirements = PasswordRequirements(
                minLength = true,
                hasUppercase = true,
                hasLowercase = true,
                hasNumber = false
            )
        )
    }
}
