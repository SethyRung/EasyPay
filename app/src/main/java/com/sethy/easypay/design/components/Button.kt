package com.sethy.easypay.design.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.*

@Composable
fun ButtonPrimary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = EasyPayDimens.buttonHeight),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = OnPrimary,
            disabledContainerColor = PrimaryDisabled,
            disabledContentColor = Muted
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(text = text, style = EasyPayTypography.button)
    }
}

@Composable
fun ButtonPrimaryActive(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = EasyPayDimens.buttonHeight),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryActive,
            contentColor = OnPrimary,
            disabledContainerColor = PrimaryDisabled,
            disabledContentColor = Muted
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(text = text, style = EasyPayTypography.button)
    }
}

@Composable
fun ButtonSecondary(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = EasyPayDimens.buttonHeight),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, Hairline),
        colors = ButtonDefaults.buttonColors(
            containerColor = Canvas,
            contentColor = Ink,
            disabledContainerColor = Canvas.copy(alpha = 0.5f),
            disabledContentColor = Muted
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(text = text, style = EasyPayTypography.button)
    }
}

@Composable
fun ButtonSecondaryOnDark(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = EasyPayDimens.buttonHeight),
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, HairlineSoft),
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceDarkElevated,
            contentColor = OnDark,
            disabledContainerColor = SurfaceDarkElevated.copy(alpha = 0.5f),
            disabledContentColor = OnDarkSoft
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(text = text, style = EasyPayTypography.button)
    }
}

@Composable
fun ButtonTextLink(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = EasyPayDimens.touchTarget),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = Ink,
            disabledContentColor = Muted
        )
    ) {
        Text(text = text, style = EasyPayTypography.button)
    }
}

@Composable
fun ButtonIconCircular(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(EasyPayDimens.iconButton)
            .background(Canvas, CircleShape)
            .border(1.dp, Hairline, CircleShape),
        enabled = enabled
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = Ink
        )
    }
}

@Preview
@Composable
private fun ButtonPreview() {
    EasyPayTheme {
        Column {
            ButtonPrimary(text = "Primary", onClick = {})
            ButtonPrimaryActive(text = "Primary active", onClick = {})
            ButtonSecondary(text = "Secondary", onClick = {})
            ButtonTextLink(text = "Text link", onClick = {})
        }
    }
}
