package io.zoemeow.dutschedule.ui.view.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.BuildConfig
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.PermissionRequestActivity
import io.zoemeow.dutschedule.activity.SettingsActivity
import io.zoemeow.dutschedule.model.settings.BackgroundImageOption
import io.zoemeow.dutschedule.model.settings.ThemeMode
import io.zoemeow.dutschedule.ui.component.base.DividerItem
import io.zoemeow.dutschedule.ui.component.base.OptionItem
import io.zoemeow.dutschedule.ui.component.base.OptionSwitchItem
import io.zoemeow.dutschedule.ui.component.settings.ContentRegion
import io.zoemeow.dutschedule.ui.component.settings.dialog.DialogAppBackgroundSettings
import io.zoemeow.dutschedule.ui.component.settings.dialog.DialogAppThemeSettings
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsActivity.MainView(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color,
    mediaRequest: () -> Unit
) {
    val dialogAppTheme: MutableState<Boolean> = remember { mutableStateOf(false) }
    val dialogBackground: MutableState<Boolean> = remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            LargeTopAppBar(
                title = { Text(getString(R.string.settings_name)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            setResult(ComponentActivity.RESULT_OK)
                            finish()
                        },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                "",
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState()),
                content = {
                    ContentRegion(
                        modifier = Modifier
                            .padding(top = 10.dp),
                        textModifier = Modifier.padding(horizontal = 20.dp),
                        text = getString(R.string.settings_category_notifications),
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_outline_calendar_clock_24),
                                        getString(R.string.settings_option_newsschedule),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = getString(R.string.settings_option_newsschedule),
                                description = getString(R.string.settings_option_newsschedule_description),
                                onClick = {
                                    Intent(context, SettingsActivity::class.java).apply {
                                        action = "settings_newsnotificaitonsettings"
                                    }.also { intent -> context.startActivity(intent) }
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "News filter settings",
                                description = "Make your filter to only receive your preferred subject news.",
                                onClick = {
                                    val intent = Intent(context, SettingsActivity::class.java)
                                    intent.action = "settings_newsfilter"
                                    context.startActivity(intent)
                                }
                            )
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                OptionItem(
                                    modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Notifications,
                                            getString(R.string.settings_option_notificationoutside),
                                            modifier = Modifier.padding(end = 15.dp)
                                        )
                                    },
                                    title = getString(R.string.settings_option_notificationoutside),
                                    description = getString(R.string.settings_option_notificationoutside_description),
                                    onClick = {
                                        context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).also { intent ->
                                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                                        })
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
                        text = getString(R.string.settings_category_appearance),
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_dark_mode_24),
                                        getString(R.string.settings_option_apptheme),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = getString(R.string.settings_option_apptheme),
                                description = String.format(
                                    "%s%s",
                                    when (getMainViewModel().appSettings.value.themeMode) {
                                        ThemeMode.FollowDeviceTheme -> "Follow device theme"
                                        ThemeMode.DarkMode -> "Dark mode"
                                        ThemeMode.LightMode -> "Light mode"
                                    },
                                    if (getMainViewModel().appSettings.value.dynamicColor) " (dynamic color enabled)" else ""
                                ),
                                onClick = { dialogAppTheme.value = true }
                            )
                            OptionSwitchItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_contrast_24),
                                        "Black background settings",
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = "Black background",
                                description = "Make app background to black color. Only in dark mode and turned off background image.",
                                isChecked = getMainViewModel().appSettings.value.blackBackground,
                                onValueChanged = { value ->
                                    getMainViewModel().appSettings.value =
                                        getMainViewModel().appSettings.value.clone(
                                            blackBackground = value
                                        )
                                    getMainViewModel().saveSettings()
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_image_24),
                                        "Background image settings",
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = "Background image",
                                description = when (getMainViewModel().appSettings.value.backgroundImage) {
                                    BackgroundImageOption.None -> "None"
                                    BackgroundImageOption.YourCurrentWallpaper -> "Your current wallpaper"
                                    BackgroundImageOption.PickFileFromMedia -> "Your picked image"
                                },
                                onClick = { dialogBackground.value = true }
                            )
                        }
                    )
                    DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
                    ContentRegion(
                        modifier = Modifier.padding(top = 10.dp),
                        textModifier = Modifier.padding(horizontal = 20.dp),
                        text = "Miscellaneous settings",
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.google_fonts_globe_24),
                                        "App language",
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = "App language",
                                description = Locale.getDefault().displayName,
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                                        intent.data = Uri.fromParts("package", context.packageName, null)
                                        context.startActivity(intent)
                                    } else {
                                        val intent = Intent(context, SettingsActivity::class.java)
                                        intent.action = "settings_languagesettings"
                                        context.startActivity(intent)
                                    }
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "Application permissions",
                                description = "Click here for allow and manage app permissions you granted.",
                                onClick = {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            PermissionRequestActivity::class.java
                                        )
                                    )
                                }
                            )
                            OptionSwitchItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_web_24),
                                        "App language",
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = "Open link inside app",
                                description = "Open clicked link without leaving this app. Turn off to open link in default browser.",
                                isChecked = getMainViewModel().appSettings.value.openLinkInsideApp,
                                onValueChanged = { value ->
                                    getMainViewModel().appSettings.value =
                                        getMainViewModel().appSettings.value.clone(
                                            openLinkInsideApp = value
                                        )
                                    getMainViewModel().saveSettings()
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.google_fonts_science_24),
                                        "Experiment settings",
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = "Experiment settings",
                                description = "Our current experiment settings before public.",
                                onClick = {
                                    val intent = Intent(context, SettingsActivity::class.java)
                                    intent.action = "settings_experimentsettings"
                                    context.startActivity(intent)
                                }
                            )
                        }
                    )
                    DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
                    ContentRegion(
                        modifier = Modifier
                            .padding(top = 10.dp),
                        textModifier = Modifier.padding(horizontal = 20.dp),
                        text = "About",
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "Version",
                                description = "Current version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\nClick here to check for update",
                                onClick = {
                                    showSnackBar("This option is in development. Check back soon.", true)
                                    /* TODO: Implement here: Check for updates */
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "Changelogs",
                                description = "Tap to view app changelog",
                                onClick = {
                                    openLink(
                                        url = "https://github.com/ZoeMeow1027/DutSchedule/blob/stable/CHANGELOG.md",
                                        context = context,
                                        customTab = getMainViewModel().appSettings.value.openLinkInsideApp,
                                    )
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "GitHub (click to open link)",
                                description = "https://github.com/ZoeMeow1027/DutSchedule",
                                onClick = {
                                    openLink(
                                        url = "https://github.com/ZoeMeow1027/DutSchedule",
                                        context = context,
                                        customTab = getMainViewModel().appSettings.value.openLinkInsideApp,
                                    )
                                }
                            )
                        }
                    )
                },
            )
        }
    )
    DialogAppThemeSettings(
        isVisible = dialogAppTheme.value,
        themeModeValue = getMainViewModel().appSettings.value.themeMode,
        dynamicColorEnabled = getMainViewModel().appSettings.value.dynamicColor,
        onDismiss = { dialogAppTheme.value = false },
        onValueChanged = { themeMode, dynamicColor ->
            getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                themeMode = themeMode,
                dynamicColor = dynamicColor
            )
            getMainViewModel().saveSettings()
        }
    )
    DialogAppBackgroundSettings(
        context = context,
        value = getMainViewModel().appSettings.value.backgroundImage,
        isVisible = dialogBackground.value,
        onDismiss = { dialogBackground.value = false },
        onValueChanged = { value ->
            when (value) {
                BackgroundImageOption.None -> {
                    getMainViewModel().appSettings.value =
                        getMainViewModel().appSettings.value.clone(
                            backgroundImage = value
                        )
                }
                BackgroundImageOption.YourCurrentWallpaper -> {
                    val compPer = PermissionRequestActivity.checkPermissionManageExternalStorage().isGranted
                    if (compPer) {
                        getMainViewModel().appSettings.value =
                            getMainViewModel().appSettings.value.clone(
                                backgroundImage = value
                            )
                    } else {
                        showSnackBar(
                            text = "You need to grant All files access in application permission to use this feature. You can use \"Choose a image from media\" without this permission.",
                            clearPrevious = true,
                            actionText = "Grant",
                            action = {
                                Intent(context, PermissionRequestActivity::class.java).also {
                                    context.startActivity(it)
                                }
                            }
                        )
                    }
                }
                BackgroundImageOption.PickFileFromMedia -> {
                    // Launch the photo picker and let the user choose only images.
                    mediaRequest.let { it() }
                }
            }

            dialogBackground.value = false
            getMainViewModel().saveSettings()
        }
    )
    BackHandler(
        enabled = dialogAppTheme.value || dialogBackground.value,
        onBack = {
            dialogAppTheme.value = false
            dialogBackground.value = false
        }
    )
}