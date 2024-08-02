package io.zoemeow.dutschedule.activity

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.settings.BackgroundImageOption
import io.zoemeow.dutschedule.model.settings.ThemeMode
import io.zoemeow.dutschedule.ui.theme.DutScheduleTheme
import io.zoemeow.dutschedule.utils.BackgroundImageUtil
import io.zoemeow.dutschedule.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


abstract class BaseActivity: ComponentActivity() {
    companion object {
        private var mainViewModel: MainViewModel? = null

        private fun isMainViewModelInitialized(): Boolean {
            return mainViewModel != null
        }
    }
    private lateinit var snackBarHostState: SnackbarHostState
    private lateinit var snackBarScope: CoroutineScope
    private val loadScriptAtStartup = mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        // A surface container using the 'background' color from the theme
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        enableEdgeToEdge(
            // This app is only ever in dark mode, so hard code detectDarkMode to true.
            SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
                detectDarkMode = { true }
            )
        )

        permitAllNetworkPolicy()
        setContent {
            // Initialize MainViewModel
            if (!isMainViewModelInitialized()) {
                mainViewModel = viewModel()
            }

            // Initialize SnackBar state
            snackBarHostState = remember { SnackbarHostState() }
            snackBarScope = rememberCoroutineScope()

            DutScheduleTheme(
                darkTheme = when (getMainViewModel().appSettings.value.themeMode) {
                    ThemeMode.DarkMode -> true
                    ThemeMode.LightMode -> false
                    ThemeMode.FollowDeviceTheme -> isSystemInDarkTheme()
                },
                dynamicColor = getMainViewModel().appSettings.value.dynamicColor,
                translucentStatusBar = getMainViewModel().appSettings.value.backgroundImage != BackgroundImageOption.None,
                content = {
                    val context = LocalContext.current

                    when (getMainViewModel().appSettings.value.backgroundImage) {
                        BackgroundImageOption.None -> null
                        BackgroundImageOption.YourCurrentWallpaper -> BackgroundImageUtil.getCurrentWallpaperBackground(context)
                        BackgroundImageOption.PickFileFromMedia -> BackgroundImageUtil.getImageFromAppData(context)
                    }.also { wallpaper ->
                        if (wallpaper != null) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                bitmap = wallpaper.asImageBitmap(),
                                contentDescription = "background_image",
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    @Composable
                    fun isAppInDarkMode(): Boolean {
                        return when (getMainViewModel().appSettings.value.themeMode) {
                            ThemeMode.LightMode -> false
                            ThemeMode.DarkMode -> true
                            ThemeMode.FollowDeviceTheme -> isSystemInDarkTheme()
                        }
                    }

                    OnMainView(
                        context = context,
                        snackBarHostState = snackBarHostState,
                        appearanceState = AppearanceState(
                            containerColor = when (getMainViewModel().appSettings.value.backgroundImage) {
                                BackgroundImageOption.None -> when (getMainViewModel().appSettings.value.blackBackground) {
                                    true -> if (isAppInDarkMode()) Color.Black else MaterialTheme.colorScheme.background
                                    false -> MaterialTheme.colorScheme.background
                                }
                                BackgroundImageOption.YourCurrentWallpaper -> MaterialTheme.colorScheme.background.copy(
                                    alpha = getMainViewModel().appSettings.value.backgroundImageOpacity
                                )
                                BackgroundImageOption.PickFileFromMedia -> MaterialTheme.colorScheme.background.copy(
                                    alpha = getMainViewModel().appSettings.value.backgroundImageOpacity
                                )
                            },
                            contentColor = if (isAppInDarkMode()) Color.White else Color.Black,
                            currentAppModeState = when (getMainViewModel().appSettings.value.themeMode) {
                                ThemeMode.FollowDeviceTheme -> when (isSystemInDarkTheme()) {
                                    true -> ThemeMode.DarkMode
                                    false -> ThemeMode.LightMode
                                }
                                else -> getMainViewModel().appSettings.value.themeMode
                            },
                            backgroundOpacity = when (getMainViewModel().appSettings.value.backgroundImage != BackgroundImageOption.None) {
                                true -> getMainViewModel().appSettings.value.backgroundImageOpacity
                                false -> 1f
                            },
                            componentOpacity = when (getMainViewModel().appSettings.value.backgroundImage != BackgroundImageOption.None) {
                                true -> getMainViewModel().appSettings.value.componentOpacity
                                false -> 1f
                            }
                        )
                    )
                },
            )

            // Run startup script once
            if (loadScriptAtStartup.value) {
                loadScriptAtStartup.value = false
                OnPreloadOnce()
            }
        }
    }

    @Composable
    abstract fun OnPreloadOnce()

    @Composable
    abstract fun OnMainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        appearanceState: AppearanceState
    )

    fun getMainViewModel(): MainViewModel {
        if (!isMainViewModelInitialized()) {
            // Initialize MainViewModel if this isn't initialized before.
            mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        }
        return mainViewModel!!
    }

    fun showSnackBar(
        text: String,
        clearPrevious: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
        actionText: String? = null,
        action: (() -> Unit)? = null,
        onDismiss: (() -> Unit)? = null
    ) {
        snackBarScope.launch {
            if (clearPrevious) {
                snackBarHostState.currentSnackbarData?.dismiss()
            }
            val result = snackBarHostState
                .showSnackbar(
                    message = text,
                    actionLabel = actionText,
                    withDismissAction = false,
                    duration = duration
                )
            when (result) {
                SnackbarResult.ActionPerformed -> {
                    if (actionText != null) action?.let { it() }
                }
                SnackbarResult.Dismissed -> {
                    onDismiss?.let { it() }
                }
                else -> { }
            }
        }
    }

    fun clearSnackBar() {
        snackBarScope.launch {
            snackBarHostState.currentSnackbarData?.dismiss()
        }
    }

    /**
     * This will bypass network on main thread exception.
     * Use this at your own risk.
     * Target: OkHttp3
     *
     * Source: https://blog.cpming.top/p/android-os-networkonmainthreadexception
     */
    private fun permitAllNetworkPolicy() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
    }
}