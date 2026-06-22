package com.sethy.easypay.ui.screens.send

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.OnPrimary
import com.sethy.easypay.design.components.AmountDisplay
import com.sethy.easypay.design.components.ButtonPrimary
import com.sethy.easypay.design.components.ButtonTextLink
import com.sethy.easypay.design.components.CtaBandCoral
import com.sethy.easypay.design.components.PulsingCheckIcon

@Composable
fun TransferSuccessScreen(
    recipientName: String,
    amount: Double,
    onDone: () -> Unit,
    onTransferMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = Canvas
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = EasyPaySpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.lg, Alignment.CenterVertically)
        ) {
            CtaBandCoral(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(EasyPaySpacing.md)
                ) {
                    PulsingCheckIcon()
                    Text(
                        text = "Transfer successful",
                        style = EasyPayTypography.displaySM,
                        color = OnPrimary,
                        textAlign = TextAlign.Center
                    )
                    AmountDisplay(
                        amount = String.format("%.2f", amount),
                        textStyle = EasyPayTypography.displayMD,
                        prefix = "$"
                    )
                    Text(
                        text = "to $recipientName",
                        style = EasyPayTypography.bodyMD,
                        color = OnPrimary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            ButtonPrimary(
                text = "Done",
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            ButtonTextLink(
                text = "Transfer more",
                onClick = onTransferMore
            )
        }
    }
}

@Preview
@Composable
private fun TransferSuccessScreenPreview() {
    EasyPayTheme {
        TransferSuccessScreen(
            recipientName = "Nayantara V",
            amount = 50.0,
            onDone = {},
            onTransferMore = {}
        )
    }
}
