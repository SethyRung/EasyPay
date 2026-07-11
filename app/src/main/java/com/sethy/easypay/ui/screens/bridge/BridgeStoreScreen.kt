package com.sethy.easypay.ui.screens.bridge

import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Loader
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.TriangleAlert
import com.sethy.easypay.bridge.BridgeController
import com.sethy.easypay.bridge.BridgeStatus
import com.sethy.easypay.bridge.PaymentSheetState
import com.sethy.easypay.design.Canvas
import com.sethy.easypay.design.EasyPayRadius
import com.sethy.easypay.design.EasyPaySpacing
import com.sethy.easypay.design.EasyPayTheme
import com.sethy.easypay.design.EasyPayTypography
import com.sethy.easypay.design.Error
import com.sethy.easypay.design.Ink
import com.sethy.easypay.design.Muted
import com.sethy.easypay.design.OnDark
import com.sethy.easypay.design.Primary
import com.sethy.easypay.design.Success
import com.sethy.easypay.design.SurfaceCard
import com.sethy.easypay.design.components.TopNav
import com.sethy.easypay.ui.viewmodel.BridgeStoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BridgeStoreScreen(
    viewModel: BridgeStoreViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNavigateToTopUp: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val status by viewModel.status.collectAsStateWithLifecycle()
    val storeUrl by viewModel.storeUrl.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val paymentSheetState by viewModel.paymentSheetState.collectAsStateWithLifecycle()
    val showCloseDialog by viewModel.showCloseDialog.collectAsStateWithLifecycle()

    androidx.compose.foundation.layout.Column(modifier = modifier.fillMaxSize()) {
        TopNav(
            title = "Glitch Store",
            showBackButton = true,
            onBackClick = { viewModel.onBackPressed() },
            actions = {
                BridgeStatusChip(status = status)
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Canvas)
        ) {
            BridgeWebView(
                url = storeUrl,
                onLoaded = { viewModel.onLoaded() },
                onError = { viewModel.onLoadError(it) },
                bridgeController = viewModel.bridgeController,
                modifier = Modifier.fillMaxSize()
            )

            androidx.compose.animation.AnimatedVisibility(visible = isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage?.let { message ->
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(EasyPaySpacing.xl)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(EasyPayRadius.md),
                    color = Error.copy(alpha = 0.92f),
                    contentColor = OnDark
                ) {
                    Row(
                        modifier = Modifier.padding(EasyPaySpacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.sm)
                    ) {
                        Icon(
                            imageVector = Lucide.TriangleAlert,
                            contentDescription = null,
                            tint = OnDark
                        )
                        Text(
                            text = message,
                            style = EasyPayTypography.bodySM,
                            color = OnDark
                        )
                    }
                }
            }
        }
    }

    if (paymentSheetState !is PaymentSheetState.Hidden) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        LaunchedEffect(paymentSheetState) {
            sheetState.show()
        }
        ModalBottomSheet(
            onDismissRequest = { viewModel.onPaymentSheetDismissed() },
            sheetState = sheetState,
            containerColor = Canvas
        ) {
            PaymentSheetContent(
                state = paymentSheetState,
                onConfirm = { viewModel.confirmPayment() },
                onCancel = { viewModel.declinePayment() },
                onTopUp = {
                    viewModel.dismissPaymentSheet()
                    onNavigateToTopUp()
                },
                onRetry = { viewModel.confirmPayment() },
                onDismiss = { viewModel.onPaymentSheetDismissed() }
            )
        }
    }

    if (showCloseDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissCloseDialog() },
            title = {
                Text(
                    text = "Close Glitch Store?",
                    style = EasyPayTypography.titleMD,
                    color = Ink
                )
            },
            text = {
                Text(
                    text = "Any in-progress checkout or bridge session will be cancelled.",
                    style = EasyPayTypography.bodyMD,
                    color = Muted
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.dismissCloseDialog()
                    onBackClick()
                }) {
                    Text(
                        text = "Close",
                        style = EasyPayTypography.button,
                        color = Primary
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissCloseDialog() }) {
                    Text(
                        text = "Cancel",
                        style = EasyPayTypography.button,
                        color = Muted
                    )
                }
            }
        )
    }
}

@Composable
private fun BridgeWebView(
    url: String,
    onLoaded: () -> Unit,
    onError: (String) -> Unit,
    bridgeController: BridgeController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    allowFileAccess = false
                    @Suppress("DEPRECATION")
                    setAllowFileAccessFromFileURLs(false)
                    @Suppress("DEPRECATION")
                    setAllowUniversalAccessFromFileURLs(false)
                    allowContentAccess = false
                    mediaPlaybackRequiresUserGesture = false
                }
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                webViewClient = object : android.webkit.WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        onLoaded()
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String?,
                        failingUrl: String?
                    ) {
                        onError(description ?: "Load error ($errorCode)")
                    }
                }
                bridgeController.attach(this)
                webViewRef = this
            }
        }
    )

    LaunchedEffect(webViewRef, url) {
        val wv = webViewRef
        if (wv != null && url.isNotBlank() && wv.url != url) {
            wv.loadUrl(url)
        }
    }

    DisposableEffect(webViewRef) {
        onDispose {
            bridgeController.detach()
            webViewRef?.destroy()
        }
    }
}

@Composable
private fun BridgeStatusChip(status: BridgeStatus) {
    val (label, bg, fg, icon) = when (status) {
        BridgeStatus.Initializing -> StatusVisuals("Connecting…", SurfaceCard, Muted, Lucide.Loader)
        BridgeStatus.Online -> StatusVisuals("Bridge OK", Success.copy(alpha = 0.16f), Success, Lucide.Check)
        is BridgeStatus.Offline -> StatusVisuals(
            "Bridge offline",
            Error.copy(alpha = 0.16f),
            Error,
            Lucide.TriangleAlert
        )
    }

    Surface(
        shape = RoundedCornerShape(EasyPayRadius.pill),
        color = bg,
        contentColor = fg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = EasyPaySpacing.sm, vertical = EasyPaySpacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(EasyPaySpacing.xs)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = EasyPayTypography.caption,
                color = fg
            )
        }
    }
}

private data class StatusVisuals(
    val label: String,
    val background: Color,
    val foreground: Color,
    val icon: ImageVector
)

@Preview
@Composable
private fun BridgeStoreScreenPreview() {
    EasyPayTheme {
        BridgeStoreScreen(onBackClick = {})
    }
}
