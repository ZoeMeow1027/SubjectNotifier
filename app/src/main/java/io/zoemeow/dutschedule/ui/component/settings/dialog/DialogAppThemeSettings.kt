package io.zoemeow.dutschedule.ui.component.settings.dialog

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.SettingsActivity
import io.zoemeow.dutschedule.model.settings.ThemeMode
import io.zoemeow.dutschedule.ui.component.base.DialogBase
import io.zoemeow.dutschedule.ui.component.base.DialogCheckboxButton
import io.zoemeow.dutschedule.ui.component.base.DialogRadioButton

@Composable
fun DialogAppThemeSettings(
    context: Context,
    isVisible: Boolean = false,
    themeModeValue: ThemeMode,
    dynamicColorEnabled: Boolean,
    onDismiss: () -> Unit,
    onValueChanged: (ThemeMode, Boolean) -> Unit
) {
    DialogBase(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        title = context.getString(R.string.settings_dialog_apptheme_title),
        isVisible = isVisible,
        canDismiss = false,
        isTitleCentered = true,
        dismissClicked = onDismiss,
        content = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxWidth(),
                content = {
                    DialogRadioButton(
                        title = context.getString(R.string.settings_dialog_apptheme_choice_followdevice),
                        selected = themeModeValue == ThemeMode.FollowDeviceTheme,
                        onClick = {
                            onValueChanged(
                                ThemeMode.FollowDeviceTheme,
                                dynamicColorEnabled
                            )
                        }
                    )
                    DialogRadioButton(
                        title = context.getString(R.string.settings_dialog_apptheme_choice_light),
                        selected = themeModeValue == ThemeMode.LightMode,
                        onClick = {
                            onValueChanged(
                                ThemeMode.LightMode,
                                dynamicColorEnabled
                            )
                        }
                    )
                    DialogRadioButton(
                        title = context.getString(R.string.settings_dialog_apptheme_choice_dark),
                        selected = themeModeValue == ThemeMode.DarkMode,
                        onClick = {
                            onValueChanged(
                                ThemeMode.DarkMode,
                                dynamicColorEnabled
                            )
                        }
                    )
                    DialogCheckboxButton(
                        title = context.getString(R.string.settings_dialog_apptheme_choice_dynamiccolor),
                        isChecked = dynamicColorEnabled,
                        onValueChanged = { value ->
                            onValueChanged(themeModeValue, value)
                        }
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(top = 20.dp),
                        content = {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_info_24),
                                contentDescription = "info_icon",
                                modifier = Modifier.size(24.dp),
                                // tint = if (mainViewModel.isDarkTheme.value) Color.White else Color.Black
                            )
                            Text(context.getString(R.string.settings_dialog_apptheme_note))
                        }
                    )
                }
            )
        },
        actionButtons = {
            TextButton(
                onClick = onDismiss,
                content = { Text(context.getString(R.string.action_ok)) },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    )
}