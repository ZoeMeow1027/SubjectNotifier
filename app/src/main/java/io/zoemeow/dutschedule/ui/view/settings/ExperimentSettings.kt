package io.zoemeow.dutschedule.ui.view.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.settings.BackgroundImageOption
import io.zoemeow.dutschedule.ui.component.base.DividerItem
import io.zoemeow.dutschedule.ui.component.base.OptionItem
import io.zoemeow.dutschedule.ui.component.base.OptionSwitchItem
import io.zoemeow.dutschedule.ui.component.settings.ContentRegion
import io.zoemeow.dutschedule.ui.component.settings.DialogSchoolYearSettings
import io.zoemeow.dutschedule.viewmodel.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Settings_ExperimentSettings(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    onMessageReceived: (String, Boolean, String?, (() -> Unit)?) -> Unit, // (msg, forceDismissBefore, actionText, action)
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val dialogSchoolYear = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            LargeTopAppBar(
                title = { Text(context.getString(R.string.settings_experiment_title)) },
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
                        text = context.getString(R.string.settings_experiment_category_globalvar),
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = context.getString(R.string.settings_experiment_option_currentschyear),
                                description = context.getString(
                                    R.string.settings_experiment_option_currentschyear_description,
                                    mainViewModel.appSettings.value.currentSchoolYear.year,
                                    mainViewModel.appSettings.value.currentSchoolYear.year + 1,
                                    when (mainViewModel.appSettings.value.currentSchoolYear.semester) {
                                        1 -> "1"
                                        2 -> "2"
                                        else -> "2"
                                    },
                                    if (mainViewModel.appSettings.value.currentSchoolYear.semester > 2) " ${context.getString(R.string.settings_experiment_option_currentschyear_insummer)}" else ""
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
                        text = context.getString(R.string.settings_experiment_category_news),
                        content = {
                            OptionSwitchItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = context.getString(R.string.settings_experiment_option_opennewsinpopup),
                                isVisible = true,
                                isEnabled = true,
                                isChecked = mainViewModel.appSettings.value.openNewsInModalBottomSheet,
                                description = context.getString(R.string.settings_experiment_option_opennewsinpopup_description),
                                onValueChanged = { value ->
                                    mainViewModel.appSettings.value =
                                        mainViewModel.appSettings.value.clone(
                                            openNewsInModalBottomSheet = value
                                        )
                                    mainViewModel.saveApplicationSettings(saveUserSettings = true)
                                }
                            )
                        }
                    )
                    DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
                    ContentRegion(
                        modifier = Modifier.padding(top = 10.dp),
                        textModifier = Modifier.padding(horizontal = 20.dp),
                        text = context.getString(R.string.settings_experiment_category_appearance),
                        content = {
                            // https://stackoverflow.com/questions/72932093/jetpack-compose-is-there-a-way-to-restart-whole-app-programmatically
                            OptionSwitchItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = context.getString(R.string.settings_experiment_option_dashboardview),
                                isVisible = true,
                                isEnabled = true,
                                isChecked = mainViewModel.appSettings.value.mainScreenDashboardView,
                                description = when (mainViewModel.appSettings.value.mainScreenDashboardView) {
                                    true -> context.getString(R.string.settings_experiment_option_dashboardview_choice_enabled)
                                    false -> context.getString(R.string.settings_experiment_option_dashboardview_choice_disabled)
                                },
                                onValueChanged = {
                                    onMessageReceived(
                                        context.getString(
                                            R.string.settings_experiment_option_dashboardview_warning,
                                            when (mainViewModel.appSettings.value.mainScreenDashboardView) {
                                                true -> context.getString(R.string.settings_experiment_option_dashboardview_warning_disable)
                                                false -> context.getString(R.string.settings_experiment_option_dashboardview_warning_enable)
                                            }
                                        ),
                                        true,
                                        context.getString(R.string.action_confirm)
                                    ) {
                                        mainViewModel.appSettings.value =
                                            mainViewModel.appSettings.value.clone(
                                                mainScreenDashboardView = !mainViewModel.appSettings.value.mainScreenDashboardView
                                            )
                                        mainViewModel.saveApplicationSettings(
                                            saveUserSettings = true,
                                            onCompleted = {
                                                val packageManager: PackageManager =
                                                    context.packageManager
                                                val intent: Intent =
                                                    packageManager.getLaunchIntentForPackage(context.packageName)!!
                                                val componentName: ComponentName =
                                                    intent.component!!
                                                val restartIntent: Intent =
                                                    Intent.makeRestartActivityTask(componentName)
                                                context.startActivity(restartIntent)
                                                Runtime.getRuntime().exit(0)
                                            }
                                        )
                                    }
                                }
                            )
                        }
                    )
                    DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
                    ContentRegion(
                        modifier = Modifier.padding(top = 10.dp),
                        textModifier = Modifier.padding(horizontal = 20.dp),
                        text = context.getString(R.string.settings_experiment_category_troubleshooting),
                        content = {
                            OptionItem(
                                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                                title = context.getString(R.string.settings_experiment_option_debuglog),
                                description = context.getString(R.string.settings_experiment_option_debuglog_description),
                                onClick = {
                                    onMessageReceived(context.getString(R.string.feature_not_ready), true, null, null)
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
        context = context,
        isVisible = dialogSchoolYear.value,
        dismissRequested = { dialogSchoolYear.value = false },
        currentSchoolYearItem = mainViewModel.appSettings.value.currentSchoolYear,
        onSubmit = {
            mainViewModel.appSettings.value = mainViewModel.appSettings.value.clone(
                currentSchoolYear = it
            )
            mainViewModel.saveApplicationSettings(saveUserSettings = true)
            dialogSchoolYear.value = false
        }
    )
}