package io.zoemeow.dutschedule.ui.view.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.BuildConfig
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.MiscellaneousActivity
import io.zoemeow.dutschedule.activity.SettingsActivity
import io.zoemeow.dutschedule.di.LocaleService
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.settings.BackgroundImageOption
import io.zoemeow.dutschedule.model.settings.ThemeMode
import io.zoemeow.dutschedule.ui.components.DividerItem
import io.zoemeow.dutschedule.ui.components.OptionItem
import io.zoemeow.dutschedule.ui.components.OptionSwitchItem
import io.zoemeow.dutschedule.ui.view.settings.controls.ContentRegion
import io.zoemeow.dutschedule.ui.view.settings.controls.DialogAppThemeSettings
import io.zoemeow.dutschedule.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Settings(
    context: Context,
    snackBarHostState: SnackbarHostState? = null,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    mediaRequest: () -> Unit,
    onMessageReceived: ((String, Boolean, String?, (() -> Unit)?) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val dialogAppTheme: MutableState<Boolean> = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { snackBarHostState?.let { SnackbarHost(hostState = it) } },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.settings_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    if (onBack != null) {
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
                    }
                }
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
                        text = context.getString(R.string.settings_category_notifications),
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_outline_calendar_clock_24),
                                        context.getString(R.string.settings_option_newsschedule),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = context.getString(R.string.settings_option_newsschedule),
                                description = context.getString(R.string.settings_option_newsschedule_description),
                                onClick = {
                                    Intent(context, SettingsActivity::class.java).apply {
                                        action = SettingsActivity.INTENT_NEWSNOTIFICATIONSETTINGS
                                    }.also { intent -> context.startActivity(intent) }
                                }
                            )
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                OptionItem(
                                    modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Notifications,
                                            context.getString(R.string.settings_option_notificationoutside),
                                            modifier = Modifier.padding(end = 15.dp)
                                        )
                                    },
                                    title = context.getString(R.string.settings_option_notificationoutside),
                                    description = context.getString(R.string.settings_option_notificationoutside_description),
                                    onClick = {
                                        context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).also { intent ->
                                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
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
                        text = context.getString(R.string.settings_category_appearance),
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_dark_mode_24),
                                        context.getString(R.string.settings_option_apptheme),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = context.getString(R.string.settings_option_apptheme),
                                description = String.format(
                                    "%s %s",
                                    when (mainViewModel.appSettings.value.themeMode) {
                                        ThemeMode.FollowDeviceTheme -> context.getString(R.string.settings_option_apptheme_choice_followdevice)
                                        ThemeMode.DarkMode -> context.getString(R.string.settings_option_apptheme_choice_dark)
                                        ThemeMode.LightMode -> context.getString(R.string.settings_option_apptheme_choice_light)
                                    },
                                    if (mainViewModel.appSettings.value.dynamicColor) context.getString(R.string.settings_option_apptheme_choice_dynamiccolorenabled) else ""
                                ),
                                onClick = { dialogAppTheme.value = true }
                            )
                            OptionSwitchItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_contrast_24),
                                        context.getString(R.string.settings_option_blackbackground),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = context.getString(R.string.settings_option_blackbackground),
                                description = context.getString(R.string.settings_option_blackbackground_description),
                                isChecked = mainViewModel.appSettings.value.blackBackground,
                                onValueChanged = { value ->
                                    mainViewModel.appSettings.value =
                                        mainViewModel.appSettings.value.clone(
                                            blackBackground = value
                                        )
                                    mainViewModel.saveApplicationSettings(saveUserSettings = true)
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_image_24),
                                        context.getString(R.string.settings_option_wallpaperbackground),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = context.getString(R.string.settings_option_wallpaperbackground),
                                description = when (mainViewModel.appSettings.value.backgroundImage) {
                                    BackgroundImageOption.None -> context.getString(R.string.settings_option_wallpaperbackground_choice_none)
                                    BackgroundImageOption.YourCurrentWallpaper -> context.getString(R.string.settings_option_wallpaperbackground_choice_currentwallpaper)
                                    BackgroundImageOption.PickFileFromMedia -> context.getString(R.string.settings_option_wallpaperbackground_choice_pickedimage)
                                },
                                onClick = {
                                    val intent = Intent(context, SettingsActivity::class.java)
                                    intent.action = SettingsActivity.INTENT_WALLPAPERANDCONTROLSSETTINGS
                                    context.startActivity(intent)
                                }
                            )
                        }
                    )
                    DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
                    ContentRegion(
                        modifier = Modifier.padding(top = 10.dp),
                        textModifier = Modifier.padding(horizontal = 20.dp),
                        text = context.getString(R.string.settings_category_miscellaneous),
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.google_fonts_globe_24),
                                        context.getString(R.string.settings_option_applanguage),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = context.getString(R.string.settings_option_applanguage),
                                description = LocaleService.getCurrentLocaleDisplayName(context),
                                onClick = {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                                        intent.data = Uri.fromParts("package", context.packageName, null)
                                        context.startActivity(intent)
                                    } else {
                                        val intent = Intent(context, SettingsActivity::class.java)
                                        intent.action = SettingsActivity.INTENT_LANGUAGESETTINGS
                                        context.startActivity(intent)
                                    }
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_outline_encrypted_24),
                                        contentDescription = context.getString(R.string.settings_option_apppermission),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = context.getString(R.string.settings_option_apppermission),
                                description = context.getString(R.string.settings_option_apppermission_description),
                                onClick = {
                                    val intent = Intent(context, MiscellaneousActivity::class.java)
                                    intent.action = MiscellaneousActivity.INTENT_PERMISSIONREQUEST
                                    context.startActivity(intent)
                                }
                            )
                            OptionSwitchItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_web_24),
                                        context.getString(R.string.settings_option_openlinkinsideapp),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = context.getString(R.string.settings_option_openlinkinsideapp),
                                description = context.getString(R.string.settings_option_openlinkinsideapp_description),
                                isChecked = mainViewModel.appSettings.value.openLinkInsideApp,
                                onValueChanged = { value ->
                                    mainViewModel.appSettings.value =
                                        mainViewModel.appSettings.value.clone(
                                            openLinkInsideApp = value
                                        )
                                    mainViewModel.saveApplicationSettings(saveUserSettings = true)
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.google_fonts_science_24),
                                        context.getString(R.string.settings_option_experiemntsettings),
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = context.getString(R.string.settings_option_experiemntsettings),
                                description = context.getString(R.string.settings_option_experiemntsettings_description),
                                onClick = {
                                    val intent = Intent(context, SettingsActivity::class.java)
                                    intent.action = SettingsActivity.INTENT_EXPERIMENTSETTINGS
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
                        text = context.getString(R.string.settings_category_about),
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                leadingIcon = {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_info_24),
                                        "",
                                        modifier = Modifier.padding(end = 15.dp)
                                    )
                                },
                                title = context.getString(R.string.settings_option_about),
                                description = context.getString(
                                    R.string.settings_option_version_description,
                                    BuildConfig.VERSION_NAME,
                                    BuildConfig.VERSION_CODE
                                ),
                                onClick = {
                                    // onMessageReceived?.let { it(context.getString(R.string.feature_not_ready), true, null, null) }
                                    val intent = Intent(context, SettingsActivity::class.java)
                                    intent.action = SettingsActivity.INTENT_ABOUTACTIVITY
                                    context.startActivity(intent)
                                }
                            )
                        }
                    )
                },
            )
        }
    )
    DialogAppThemeSettings(
        context = context,
        isVisible = dialogAppTheme.value,
        themeModeValue = mainViewModel.appSettings.value.themeMode,
        dynamicColorEnabled = mainViewModel.appSettings.value.dynamicColor,
        onDismiss = { dialogAppTheme.value = false },
        onValueChanged = { themeMode, dynamicColor ->
            mainViewModel.appSettings.value = mainViewModel.appSettings.value.clone(
                themeMode = themeMode,
                dynamicColor = dynamicColor
            )
            mainViewModel.saveApplicationSettings(saveUserSettings = true)
        }
    )
    BackHandler(
        enabled = dialogAppTheme.value,
        onBack = {
            dialogAppTheme.value = false
        }
    )
}