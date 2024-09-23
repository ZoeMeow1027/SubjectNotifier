package io.zoemeow.dutschedule.ui.view.settings

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.settings.BackgroundImageOption
import io.zoemeow.dutschedule.ui.components.DividerItem
import io.zoemeow.dutschedule.ui.components.RadioButtonOption
import io.zoemeow.dutschedule.ui.view.settings.controls.ContentRegion
import io.zoemeow.dutschedule.ui.view.settings.controls.SliderWithValue
import io.zoemeow.dutschedule.utils.PermissionUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Settings_WallpaperAndControlsSettings(
    context: Context,
    snackBarHostState: SnackbarHostState?,
    appearanceState: AppearanceState,
    onBack: () -> Unit,
    valueBackgroundState: BackgroundImageOption = BackgroundImageOption.None,
    onValueBackgroundStateChanged: ((BackgroundImageOption) -> Unit)? = null,
    valueBackgroundOpacity: Float = 1f,
    onValueBackgroundOpacityChanged: ((Float) -> Unit)? = null,
    valueComponentOpacity: Float = 1f,
    onValueComponentOpacityChanged: ((Float) -> Unit)? = null
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            snackBarHostState?.let {
                SnackbarHost(hostState = it)
            }
        },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            LargeTopAppBar(
                title = { Text(context.getString(R.string.settings_wallpaperandcontrols_title)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack()
                        },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                context.getString(R.string.action_back),
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            ContentRegion(
                modifier = Modifier
                    .padding(top = 10.dp),
                textModifier = Modifier.padding(horizontal = 20.dp),
                text = context.getString(R.string.settings_wallpaperandcontrols_category_enabled),
                content = {
                    BackgroundImageOption.entries.forEach {
                        RadioButtonOption(
                            isEnabled = when (it) {
                                BackgroundImageOption.YourCurrentWallpaper -> {
                                    (Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE) && (PermissionUtils.checkPermissionManageExternalStorage(
                                        context = context
                                    ).isGranted)
                                }

                                else -> true
                            },
                            isChecked = valueBackgroundState == it,
                            modifierInside = Modifier.padding(horizontal = 6.5.dp, vertical = 7.dp),
                            onClick = { onValueBackgroundStateChanged?.let { d -> d(it) } },
                            title = when (it) {
                                BackgroundImageOption.None -> context.getString(R.string.settings_wallpaperandcontrols_choice_none)
                                BackgroundImageOption.YourCurrentWallpaper -> context.getString(R.string.settings_wallpaperandcontrols_choice_currentwallpaper)
                                BackgroundImageOption.PickFileFromMedia ->  context.getString(R.string.settings_wallpaperandcontrols_choice_pickaimage)
                            },
                            description = when (it) {
                                BackgroundImageOption.YourCurrentWallpaper -> when {
                                    // This isn't unavailable for Android 14
                                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) -> {
                                        context.getString(R.string.settings_wallpaperandcontrols_choice_currentwallpaper_disa14)
                                    }
                                    // Permission is not granted.
                                    (!PermissionUtils.checkPermissionManageExternalStorage(
                                        context = context
                                    ).isGranted) -> {
                                        context.getString(R.string.settings_wallpaperandcontrols_choice_currentwallpaper_dismisperext)
                                    }
                                    // Else, no exception
                                    else -> null
                                }

                                else -> null
                            }
                        )
                    }
                }
            )
            DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
            ContentRegion(
                modifier = Modifier
                    .padding(top = 10.dp),
                textModifier = Modifier.padding(horizontal = 20.dp),
                text = context.getString(R.string.settings_wallpaperandcontrols_category_componentopacity),
                content = {
                    SliderWithValue(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        title = context.getString(R.string.settings_wallpaperandcontrols_option_bgopacity),
                        description = context.getString(R.string.settings_wallpaperandcontrols_option_bgopacity_description),
                        defaultValue = valueBackgroundOpacity * 100,
                        onValueChanged = { bg -> onValueBackgroundOpacityChanged?.let { it(bg / 100) } },
                        opacity = appearanceState.componentOpacity
                    )
                    SliderWithValue(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        title = context.getString(R.string.settings_wallpaperandcontrols_option_componentopacity),
                        description = context.getString(R.string.settings_wallpaperandcontrols_option_componentopacity_description),
                        defaultValue = valueComponentOpacity * 100,
                        onValueChanged = { co -> onValueComponentOpacityChanged?.let { it(co / 100) } },
                        opacity = appearanceState.componentOpacity
                    )
                }
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    val context = LocalContext.current
    Activity_Settings_WallpaperAndControlsSettings(
        context = context,
        snackBarHostState = null,
        appearanceState = AppearanceState.createDefault(),
        onBack = { },
        valueBackgroundOpacity = 0.65f,
        valueComponentOpacity = 0.65f
    )
}