package com.sethy.easypay.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sethy.easypay.R
import com.sethy.easypay.ui.theme.AppTypography
import com.sethy.easypay.ui.theme.OffWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "EasyPay Logo",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )

                Text(text = "EasyPay", style = AppTypography.headlineSmall)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(OffWhite)
    )
}
