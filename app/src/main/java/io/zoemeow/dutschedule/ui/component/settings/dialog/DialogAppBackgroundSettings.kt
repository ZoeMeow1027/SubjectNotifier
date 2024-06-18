package io.zoemeow.dutschedule.ui.component.settings.dialog

import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.PermissionsActivity
import io.zoemeow.dutschedule.model.settings.BackgroundImageOption
import io.zoemeow.dutschedule.ui.component.base.DialogBase
import io.zoemeow.dutschedule.ui.component.base.DialogRadioButton

@Composable
fun DialogAppBackgroundSettings(
    context: Context,
    isVisible: Boolean = false,
    value: BackgroundImageOption,
    onDismiss: () -> Unit,
    onValueChanged: (BackgroundImageOption) -> Unit
) {
    DialogBase(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        title = context.getString(R.string.settings_dialog_wallpaperbackground_title),
        isVisible = isVisible,
        canDismiss = true,
        isTitleCentered = true,
        dismissClicked = {
            onDismiss()
        },
        content = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    DialogRadioButton(
                        title = context.getString(R.string.settings_dialog_wallpaperbackground_choice_none),
                        isSelected = value == BackgroundImageOption.None,
                        onClick = {
                            onDismiss()
                            onValueChanged(BackgroundImageOption.None)
                        }
                    )
                    DialogRadioButton(
                        title = String.format(
                            "%s%s",
                            context.getString(R.string.settings_dialog_wallpaperbackground_choice_currentwallpaper),
                            when {
                                // This isn't unavailable for Android 14
                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) -> {
                                    "\n(${context.getString(R.string.settings_dialog_wallpaperbackground_choice_currentwallpaper_disa14)})"
                                }
                                // Permission is not granted.
                                (!PermissionsActivity.checkPermissionManageExternalStorage().isGranted) -> {
                                    "\n(${context.getString(R.string.settings_dialog_wallpaperbackground_choice_currentwallpaper_dismisperext)})"
                                }
                                // Else, no exception
                                else -> { "" }
                            }
                        ),
                        isSelected = value == BackgroundImageOption.YourCurrentWallpaper,
                        onClick = {
                            val compSdk = Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE
                            val compPer = PermissionsActivity.checkPermissionManageExternalStorage().isGranted
                            if (compSdk && compPer) {
                                onDismiss()
                                onValueChanged(BackgroundImageOption.YourCurrentWallpaper)
                            }
                        }
                    )
                    DialogRadioButton(
                        title = context.getString(R.string.settings_dialog_wallpaperbackground_choice_pickaimage),
                        isSelected = value == BackgroundImageOption.PickFileFromMedia,
                        onClick = {
                            onDismiss()
                            onValueChanged(BackgroundImageOption.PickFileFromMedia)
                        }
                    )
                }
            )
        },
        actionButtons = {
            TextButton(
                onClick = onDismiss,
                content = { Text(context.getString(R.string.action_cancel)) },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    )
}