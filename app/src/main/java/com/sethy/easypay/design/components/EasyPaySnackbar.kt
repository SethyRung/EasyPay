package com.sethy.easypay.design.components

import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sethy.easypay.design.Error
import com.sethy.easypay.design.OnPrimary

@Composable
fun EasyPaySnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        snackbarData = snackbarData,
        modifier = modifier,
        containerColor = Error,
        contentColor = OnPrimary,
        actionColor = OnPrimary
    )
}

@Composable
fun EasyPaySnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier
    ) { data -> EasyPaySnackbar(snackbarData = data) }
}
