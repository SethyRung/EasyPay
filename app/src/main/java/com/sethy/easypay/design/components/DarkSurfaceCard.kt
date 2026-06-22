package com.sethy.easypay.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
fun ProductMockupCardDark(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = SurfaceDark,
            contentColor = OnDark
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(EasyPaySpacing.xl),
            content = content
        )
    }
}

@Composable
fun CodeWindowCard(
    code: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(EasyPaySpacing.lg)) {
            Text(
                text = code,
                style = EasyPayTypography.code,
                color = OnDark
            )
        }
    }
}

@Preview
@Composable
private fun DarkSurfaceCardPreview() {
    EasyPayTheme {
        Column {
            ProductMockupCardDark {
                Text("Product mockup", style = EasyPayTypography.titleMD)
                Text("Dark surface content", style = EasyPayTypography.bodySM)
            }
            CodeWindowCard(code = "val x = 42")
        }
    }
}
