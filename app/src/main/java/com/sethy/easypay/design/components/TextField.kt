package com.sethy.easypay.design.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.sethy.easypay.design.*

@Composable
fun TextInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = EasyPayDimens.inputHeight),
        label = { Text(text = label, style = EasyPayTypography.bodySM) },
        placeholder = placeholder?.let {
            {
                Text(
                    text = it,
                    style = EasyPayTypography.bodyMD,
                    color = Muted
                )
            }
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        isError = isError,
        supportingText = errorMessage?.let {
            {
                Text(
                    text = it,
                    style = EasyPayTypography.caption,
                    color = Error
                )
            }
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        shape = MaterialTheme.shapes.medium,
        textStyle = EasyPayTypography.bodyMD,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Canvas,
            unfocusedContainerColor = Canvas,
            disabledContainerColor = Canvas.copy(alpha = 0.5f),
            focusedBorderColor = Primary,
            unfocusedBorderColor = Hairline,
            disabledBorderColor = Hairline.copy(alpha = 0.5f),
            errorBorderColor = Error,
            focusedTextColor = Ink,
            unfocusedTextColor = Ink,
            disabledTextColor = Muted,
            focusedLabelColor = Muted,
            unfocusedLabelColor = Muted,
            disabledLabelColor = MutedSoft,
            errorLabelColor = Error,
            errorSupportingTextColor = Error,
            cursorColor = Primary,
            errorCursorColor = Error
        )
    )
}

@Preview
@Composable
private fun TextInputPreview() {
    EasyPayTheme {
        TextInput(
            value = "",
            onValueChange = {},
            label = "Email",
            placeholder = "you@example.com"
        )
    }
}

@Preview
@Composable
private fun TextInputErrorPreview() {
    EasyPayTheme {
        TextInput(
            value = "",
            onValueChange = {},
            label = "Email",
            placeholder = "you@example.com",
            isError = true,
            errorMessage = "Email is required"
        )
    }
}
