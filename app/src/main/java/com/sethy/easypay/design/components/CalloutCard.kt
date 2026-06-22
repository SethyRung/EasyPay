package com.sethy.easypay.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.*

@Composable
fun CalloutCardCoral(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Primary,
            contentColor = OnPrimary
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(EasyPaySpacing.xxl),
            content = content
        )
    }
}

@Composable
fun CtaBandCoral(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Primary,
            contentColor = OnPrimary
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(64.dp),
            content = content
        )
    }
}

@Composable
fun CtaBandDark(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark,
            contentColor = OnDark
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(64.dp),
            content = content
        )
    }
}

@Preview
@Composable
private fun CalloutCardPreview() {
    EasyPayTheme {
        Column {
            CalloutCardCoral {
                Text("Coral callout", style = EasyPayTypography.titleMD)
            }
            CtaBandCoral {
                Text("CTA band coral", style = EasyPayTypography.displaySM)
            }
            CtaBandDark {
                Text("CTA band dark", style = EasyPayTypography.displaySM)
            }
        }
    }
}
