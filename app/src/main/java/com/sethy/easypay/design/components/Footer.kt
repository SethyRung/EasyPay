package com.sethy.easypay.design.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sethy.easypay.design.*

@Composable
fun EasyPayFooter(
    columns: List<Pair<String, List<String>>>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SurfaceDark,
        contentColor = OnDarkSoft
    ) {
        Column(modifier = Modifier.padding(EasyPaySpacing.xl)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                columns.forEach { (title, links) ->
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, style = EasyPayTypography.titleSM, color = OnDark)
                        Spacer(modifier = Modifier.height(EasyPaySpacing.sm))
                        links.forEach { link ->
                            TextButton(onClick = {}) {
                                Text(text = link, style = EasyPayTypography.bodySM)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun FooterPreview() {
    EasyPayTheme {
        EasyPayFooter(
            columns = listOf(
                "Product" to listOf("Features", "Pricing", "Security"),
                "Company" to listOf("About", "Careers", "Press"),
                "Resources" to listOf("Blog", "Help Center", "Contact"),
                "Legal" to listOf("Privacy", "Terms", "Cookies")
            )
        )
    }
}
