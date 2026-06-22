package com.sethy.easypay.design.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Lucide
import com.sethy.easypay.design.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNav(
    title: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit = {},
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            if (title != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BrandMark(size = 24.dp, tint = Ink)
                    Spacer(modifier = Modifier.width(EasyPaySpacing.sm))
                    Text(text = title, style = EasyPayTypography.navLink)
                }
            } else {
                EasyPayWordmark()
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Lucide.ArrowLeft,
                        contentDescription = "Back",
                        tint = Ink
                    )
                }
            }
        },
        actions = actions,
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Canvas,
            titleContentColor = Ink,
            navigationIconContentColor = Ink,
            actionIconContentColor = Ink
        )
    )
}

@Preview
@Composable
private fun TopNavPreview() {
    EasyPayTheme {
        TopNav(title = "Profile", showBackButton = true)
    }
}
