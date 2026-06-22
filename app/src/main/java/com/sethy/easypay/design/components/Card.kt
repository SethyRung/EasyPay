package com.sethy.easypay.design.components

import androidx.compose.foundation.BorderStroke
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
fun FeatureCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(EasyPaySpacing.xl),
            content = content
        )
    }
}

@Composable
fun ModelComparisonCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Canvas),
        border = BorderStroke(1.dp, Hairline),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(EasyPaySpacing.xl),
            content = content
        )
    }
}

@Composable
fun PricingTierCard(
    featured: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (featured) SurfaceDark else Canvas,
            contentColor = if (featured) OnDark else Ink
        ),
        border = if (featured) null else BorderStroke(1.dp, Hairline),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(EasyPaySpacing.xl),
            content = content
        )
    }
}

@Composable
fun ConnectorTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Canvas),
        border = BorderStroke(1.dp, Hairline),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun ProfileInfoCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(EasyPaySpacing.lg),
            content = content
        )
    }
}

@Preview
@Composable
private fun CardsPreview() {
    EasyPayTheme {
        Column {
            FeatureCard {
                Text("Feature card", style = EasyPayTypography.titleMD)
                Text("Description", style = EasyPayTypography.bodySM, color = Body)
            }
            ModelComparisonCard {
                Text("Model card", style = EasyPayTypography.titleMD)
            }
            PricingTierCard(featured = true) {
                Text("Featured pricing", style = EasyPayTypography.titleLG, color = OnDark)
            }
            ProfileInfoCard {
                Text("Info card", style = EasyPayTypography.bodyMD)
            }
        }
    }
}
