package com.sethy.easypay.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import com.sethy.easypay.design.EasyPayRadius
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Error
import com.sethy.easypay.design.OnPrimary

@Composable
fun EasyPaySnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        containerColor = Error,
        contentColor = OnPrimary,
        actionColor = OnPrimary
    )
}

@Composable
fun EasyPaySnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data -> EasyPaySnackbar(snackbarData = data) }
}

@Composable
fun EasyPayErrorBanner(
    message: String,
    modifier: Modifier = Modifier,
    containerAlpha: Float = 0.92f
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(EasyPayRadius.md),
        color = Error.copy(alpha = containerAlpha),
        contentColor = OnPrimary
    ) {
        Row(
            modifier = Modifier.padding(EasyPaySpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
        ) {
            Icon(
                imageVector = Lucide.TriangleAlert,
                contentDescription = null,
                tint = OnPrimary
            )
            Text(
                text = message,
                style = EasyPayTypography.bodySM,
                color = OnPrimary
            )
        }
    }
}
