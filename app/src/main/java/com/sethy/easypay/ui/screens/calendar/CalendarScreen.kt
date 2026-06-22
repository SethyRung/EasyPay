package com.sethy.easypay.ui.screens.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.components.BrandMark
import com.sethy.easypay.design.components.ProductMockupCardDark

@Composable
fun CalendarScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProductMockupCardDark(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(EasyPaySpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BrandMark(size = 64.dp)
            Spacer(modifier = Modifier.height(EasyPaySpacing.lg))
            Text(
                text = "Coming Soon",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(EasyPaySpacing.sm))
            Text(
                text = "Transaction history calendar will be available in a future update.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
