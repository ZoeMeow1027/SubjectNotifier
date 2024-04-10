package io.zoemeow.dutschedule.ui.view.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import io.zoemeow.dutschedule.activity.SettingsActivity
import io.zoemeow.dutschedule.model.settings.BackgroundImageOption
import io.zoemeow.dutschedule.ui.component.base.DividerItem
import io.zoemeow.dutschedule.ui.component.base.OptionItem
import io.zoemeow.dutschedule.ui.component.base.OptionSwitchItem
import io.zoemeow.dutschedule.ui.component.settings.ContentRegion
import io.zoemeow.dutschedule.ui.component.settings.dialog.DialogSchoolYearSettings


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsActivity.ExperimentSettings(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color
) {
    val dialogSchoolYear = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            TopAppBar(
                title = { Text("Experiment settings") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            setResult(ComponentActivity.RESULT_CANCELED)
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
                        modifier = Modifier.padding(top = 10.dp),
                        textModifier = Modifier.padding(horizontal = 20.dp),
                        text = "Global variable settings",
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "Current school year settings",
                                description = String.format(
                                    "Year: 20%d-20%d, Semester: %s%s",
                                    getMainViewModel().appSettings.value.currentSchoolYear.year,
                                    getMainViewModel().appSettings.value.currentSchoolYear.year + 1,
                                    when (getMainViewModel().appSettings.value.currentSchoolYear.semester) {
                                        1 -> "1"
                                        2 -> "2"
                                        else -> "2"
                                    },
                                    if (getMainViewModel().appSettings.value.currentSchoolYear.semester > 2) " (in summer)" else ""
                                ),
                                onClick = {
                                    dialogSchoolYear.value = true
                                }
                            )
                        }
                    )
                    DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
                    ContentRegion(
                        modifier = Modifier.padding(top = 10.dp),
                        textModifier = Modifier.padding(horizontal = 20.dp),
                        text = "Appearance",
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "Background opacity",
                                description = String.format(
                                    "%2.0f%% %s",
                                    (getMainViewModel().appSettings.value.backgroundImageOpacity * 100),
                                    if (getMainViewModel().appSettings.value.backgroundImage == BackgroundImageOption.None) {
                                        "(You need enable background image to take effect)"
                                    } else ""
                                ),
                                onClick = {
                                    showSnackBar("This option is in development. Check back soon.", true)
                                    /* TODO: Implement here: Background opacity */
                                }
                            )
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "Component opacity",
                                description = String.format(
                                    "%2.0f%% %s",
                                    (getMainViewModel().appSettings.value.componentOpacity * 100),
                                    if (getMainViewModel().appSettings.value.backgroundImage == BackgroundImageOption.None) {
                                        "(You need enable background image to take effect)"
                                    } else ""
                                ),
                                onClick = {
                                    showSnackBar("This option is in development. Check back soon.", true)
                                    /* TODO: Implement here: Component opacity */
                                }
                            )
                            // https://stackoverflow.com/questions/72932093/jetpack-compose-is-there-a-way-to-restart-whole-app-programmatically
                            OptionSwitchItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "Main screen dashboard view",
                                isVisible = true,
                                isEnabled = true,
                                isChecked = getMainViewModel().appSettings.value.mainScreenDashboardView,
                                description = String.format(
                                    "%s",
                                    if (getMainViewModel().appSettings.value.mainScreenDashboardView) "Enabled" else "Disabled (tab view)"
                                ),
                                onValueChanged = {
                                    showSnackBar(
                                        text = String.format(
                                            "This will %s your dashboard view. Application will restart. To confirm, click Confirm button.",
                                            if (getMainViewModel().appSettings.value.mainScreenDashboardView) "disable" else "enable",
                                        ),
                                        clearPrevious = true,
                                        actionText = "Confirm",
                                        action = {
                                            getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                                                mainScreenDashboardView = !getMainViewModel().appSettings.value.mainScreenDashboardView
                                            )
                                            getMainViewModel().saveSettings(
                                                onCompleted = {
                                                    val packageManager: PackageManager = context.packageManager
                                                    val intent: Intent = packageManager.getLaunchIntentForPackage(context.packageName)!!
                                                    val componentName: ComponentName = intent.component!!
                                                    val restartIntent: Intent = Intent.makeRestartActivityTask(componentName)
                                                    context.startActivity(restartIntent)
                                                    Runtime.getRuntime().exit(0)
                                                }
                                            )
                                        }
                                    )
                                }
                            )
                        }
                    )
                    DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
                    ContentRegion(
                        modifier = Modifier.padding(top = 10.dp),
                        textModifier = Modifier.padding(horizontal = 20.dp),
                        text = "Troubleshooting",
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = "Debug log (not work yet)",
                                description = "Get debug log for this application to troubleshoot issues.",
                                onClick = {
                                    showSnackBar("This option is in development. Check back soon.", true)
                                    /* TODO: Implement here: Debug log */
                                }
                            )
                        }
                    )
                }
            )
        }
    )
    DialogSchoolYearSettings(
        isVisible = dialogSchoolYear.value,
        dismissRequested = { dialogSchoolYear.value = false },
        currentSchoolYearItem = getMainViewModel().appSettings.value.currentSchoolYear,
        onSubmit = {
            getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                currentSchoolYear = it
            )
            getMainViewModel().saveSettings()
            dialogSchoolYear.value = false
        }
    )
}