package io.zoemeow.dutschedule.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.ViewGroup
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.settings.ThemeMode
import io.zoemeow.dutschedule.utils.openLink

@AndroidEntryPoint
class BrowserActivity : BaseActivity() {
    @Composable
    override fun OnPreloadOnce() {
        // TODO("Not yet implemented")
    }

    @Composable
    override fun OnMainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        appearanceState: AppearanceState
    ) {
        MainView(
            context = context,
            snackBarHostState = snackBarHostState,
            appearanceState = appearanceState,
            startUrl = intent.getStringExtra("url") ?: "http://sv.dut.udn.vn",
            onBack = {
                setResult(RESULT_CANCELED)
                finish()
            }
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        appearanceState: AppearanceState,
        startUrl: String,
        onBack: (() -> Unit)? = null
    ) {
        val clipboardManager: ClipboardManager = LocalClipboardManager.current

        val title = remember { mutableStateOf(context.getString(R.string.activity_browser_loading)) }
        val url = remember { mutableStateOf("") }
        val progress = remember { mutableFloatStateOf(0F) }

        var webView: WebView? = null
        val canGoBack = remember { mutableStateOf(false) }
        val isLoading = remember { mutableStateOf(false) }
        fun goBackOrClose() {
            try {
                webView?.goBack()
            } catch (_: Exception) {
                // ex.printStackTrace()
            }
        }

        val isMenuOpened = remember { mutableStateOf(false) }
        val refreshTooltipState = rememberTooltipState()
        val menuTooltipState = rememberTooltipState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
            containerColor = appearanceState.containerColor,
            contentColor = appearanceState.contentColor,
            topBar = {
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    content = {
                        TopAppBar(
                            title = {
                                Column(
                                    modifier = Modifier,
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = title.value,
                                        style = MaterialTheme.typography.titleMedium,
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                    if (url.value.isNotEmpty()) {
                                        Text(
                                            text = url.value,
                                            style = MaterialTheme.typography.bodyMedium,
                                            overflow = TextOverflow.Ellipsis,
                                            maxLines = 1
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent),
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        onBack?.let { it() }
                                    },
                                    content = {
                                        Icon(
                                            Icons.Default.Clear,
                                            context.getString(R.string.action_close),
                                            modifier = Modifier.size(25.dp)
                                        )
                                    }
                                )
                            },
                            actions = {
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(text = context.getString(R.string.action_refresh))
                                        }
                                    },
                                    state = refreshTooltipState,
                                    content = {
                                        IconButton(
                                            onClick = {
                                                if (!isLoading.value) {
                                                    webView?.reload()
                                                }
                                            },
                                            content = {
                                                if (isLoading.value) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(25.dp),
                                                        strokeWidth = 2.dp
                                                    )
                                                } else {
                                                    Icon(
                                                        Icons.Default.Refresh,
                                                        context.getString(R.string.action_refresh)
                                                    )
                                                }
                                            }
                                        )
                                    }
                                )
                                TooltipBox(
                                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                    tooltip = {
                                        PlainTooltip {
                                            Text(text = context.getString(R.string.action_menu))
                                        }
                                    },
                                    state = menuTooltipState,
                                    content = {
                                        IconButton(
                                            onClick = { isMenuOpened.value = true },
                                            content = {
                                                Icon(
                                                    Icons.Default.MoreVert,
                                                    context.getString(R.string.action_menu),
                                                    modifier = Modifier.size(25.dp)
                                                )
                                            }
                                        )
                                    }
                                )
                                DropdownMenu(
                                    expanded = isMenuOpened.value,
                                    onDismissRequest = { isMenuOpened.value = false },
                                    content = {
                                        DropdownMenuItem(
                                            leadingIcon = { Icon(
                                                ImageVector.vectorResource(id = R.drawable.ic_baseline_content_copy_24),
                                                context.getString(R.string.action_copy),
                                                modifier = Modifier.size(25.dp)
                                            ) },
                                            text = { Text(context.getString(R.string.activity_browser_copylink)) },
                                            onClick = {
                                                isMenuOpened.value = false

                                                clipboardManager.setText(AnnotatedString(url.value))
                                                showSnackBar(context.getString(R.string.activity_browser_copiedlink), true)
                                            }
                                        )
                                        DropdownMenuItem(
                                            leadingIcon = { Icon(
                                                Icons.Default.Share,
                                                context.getString(R.string.action_share),
                                                modifier = Modifier.size(25.dp)
                                            ) },
                                            text = { Text(context.getString(R.string.action_share)) },
                                            onClick = {
                                                isMenuOpened.value = false

                                                val sendIntent: Intent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, url.value)
                                                    type = "text/plain"
                                                }
                                                val shareIntent = Intent.createChooser(sendIntent, null)
                                                context.startActivity(shareIntent)
                                            }
                                        )
                                        DropdownMenuItem(
                                            leadingIcon = { Icon(
                                                ImageVector.vectorResource(id = R.drawable.ic_baseline_openinnew_24),
                                                context.getString(R.string.activity_browser_openindefaultbrowser),
                                                modifier = Modifier.size(25.dp)
                                            ) },
                                            text = { Text(context.getString(R.string.activity_browser_openindefaultbrowser)) },
                                            onClick = {
                                                isMenuOpened.value = false

                                                try {
                                                    context.openLink(
                                                        url.value,
                                                        customTab = false
                                                    )
                                                } catch (_: Exception) {}
                                            }
                                        )
                                    }
                                )
                            }
                        )
                        if (progress.floatValue != 100F) {
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth(),
                                progress = { progress.floatValue }
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            // Adding a WebView inside AndroidView
            // with layout as full screen
            AndroidView(
                modifier = Modifier.padding(paddingValues),
                factory = {
                    WebView(it).apply {
                        this.layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        this.webViewClient = object : WebViewClient() {
                            override fun doUpdateVisitedHistory(view: WebView?, urlChanged: String?, isReload: Boolean) {
                                super.doUpdateVisitedHistory(view, urlChanged, isReload)
                                url.value = urlChanged ?: ""
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                super.onPageStarted(view, url, favicon)
                                canGoBack.value = view?.canGoBack() ?: false
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                if (URLUtil.isNetworkUrl(request?.url?.toString())) {
                                    return false
                                }
                                else {
                                    // Otherwise allow the OS to handle things like tel, mailto, etc.
                                    request?.let { req ->
                                        try {
//                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(req.url?.toString()))
//                                            context.startActivity(intent)
                                            // TODO: User choice about pass this url protocol to app activity
                                        } catch (_: Exception) {}
                                    }
                                    return true
                                }
                            }
                        }
                        this.webChromeClient = object : WebChromeClient() {
                            override fun onReceivedTitle(view: WebView?, tChanged: String?) {
                                super.onReceivedTitle(view, tChanged)
                                title.value = tChanged ?: context.getString(R.string.data_unknown)
                            }

                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                progress.floatValue = newProgress.toFloat()
                                isLoading.value = newProgress != 100
                            }
                        }
                        this.settings.javaScriptEnabled = true
                        this.settings.builtInZoomControls = true
                        this.settings.displayZoomControls = false
                        this.settings.setSupportZoom(true)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            this.settings.isAlgorithmicDarkeningAllowed = appearanceState.currentAppModeState == ThemeMode.DarkMode
                        }
                        webView = this
                        loadUrl(startUrl)
                    }
                },
                update = { webView = it }
            )
        }

        BackHandler(canGoBack.value) {
            goBackOrClose()
        }
    }
}