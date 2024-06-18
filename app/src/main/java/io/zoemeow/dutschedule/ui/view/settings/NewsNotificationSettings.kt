package io.zoemeow.dutschedule.ui.view.settings

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.PermissionsActivity
import io.zoemeow.dutschedule.activity.SettingsActivity
import io.zoemeow.dutschedule.model.settings.SubjectCode
import io.zoemeow.dutschedule.ui.component.base.CheckboxOption
import io.zoemeow.dutschedule.ui.component.base.DividerItem
import io.zoemeow.dutschedule.ui.component.base.OptionItem
import io.zoemeow.dutschedule.ui.component.base.RadioButtonOption
import io.zoemeow.dutschedule.ui.component.base.SimpleCardItem
import io.zoemeow.dutschedule.ui.component.base.SwitchWithTextInSurface
import io.zoemeow.dutschedule.ui.component.settings.AddNewSubjectFilterDialog
import io.zoemeow.dutschedule.ui.component.settings.ContentRegion
import io.zoemeow.dutschedule.ui.component.settings.DeleteASubjectFilterDialog
import io.zoemeow.dutschedule.ui.component.settings.DeleteAllSubjectFilterDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsActivity.NewsNotificationSettings(
    context: Context,
    snackBarHostState: SnackbarHostState?,
    containerColor: Color,
    contentColor: Color
) {
    // val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val dialogAddNew = remember { mutableStateOf(false) }
    val tempDeleteItem: MutableState<SubjectCode> = remember { mutableStateOf(SubjectCode("","","")) }
    val dialogDeleteItem = remember { mutableStateOf(false) }
    val dialogDeleteAll = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            snackBarHostState?.let {
                SnackbarHost(hostState = it)
            }
        },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.settings_newsnotify_title)) },
                // colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent),
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            setResult(RESULT_OK)
                            finish()
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
                // scrollBehavior = scrollBehavior
            )
        },
    ) {
        MainView(
            context = context,
            padding = it,
            fetchNewsInBackgroundDuration = getMainViewModel().appSettings.value.newsBackgroundDuration,
            onFetchNewsStateChanged = { duration ->
                if (duration > 0) {
                    if (PermissionsActivity.checkPermissionScheduleExactAlarm(context).isGranted && PermissionsActivity.checkPermissionNotification(context).isGranted) {
                        // Fetch news in background onClick
                        val dataTemp = getMainViewModel().appSettings.value.clone(
                            fetchNewsBackgroundDuration = duration
                        )
                        getMainViewModel().appSettings.value = dataTemp
                        getMainViewModel().saveSettings(saveSettingsOnly = true)
                        showSnackBar(
                            text = context.getString(
                                R.string.settings_newsnotify_fetchnewsinbackground_enabled,
                                duration
                            ),
                            clearPrevious = true
                        )
                    } else {
                        showSnackBar(
                            text = context.getString(R.string.settings_newsnotify_snackbar_missingpermissions),
                            clearPrevious = true,
                            actionText = context.getString(R.string.action_grant),
                            action = {
                                Intent(context, PermissionsActivity::class.java).also { intent ->
                                    context.startActivity(intent)
                                }
                            }
                        )
                    }
                } else {
                    val dataTemp = getMainViewModel().appSettings.value.clone(
                        fetchNewsBackgroundDuration = 0
                    )
                    getMainViewModel().appSettings.value = dataTemp
                    getMainViewModel().saveSettings(saveSettingsOnly = true)
                    showSnackBar(
                        text = context.getString(R.string.settings_newsnotify_fetchnewsinbackground_disabled),
                        clearPrevious = true
                    )
                }
            },
            isNewSubjectNotificationParseEnabled = getMainViewModel().appSettings.value.newsBackgroundParseNewsSubject,
            onNewSubjectNotificationParseClick = {
                Intent(context, SettingsActivity::class.java).apply {
                    action = SettingsActivity.INTENT_PARSENEWSSUBJECTNOTIFICATION
                }.also { intent -> context.startActivity(intent) }
            },
            isNewsGlobalEnabled = getMainViewModel().appSettings.value.newsBackgroundGlobalEnabled,
            onNewsGlobalStateChanged = { enabled ->
                val dataTemp = getMainViewModel().appSettings.value.clone(
                    newsBackgroundGlobalEnabled = enabled
                )
                getMainViewModel().appSettings.value = dataTemp
                getMainViewModel().saveSettings(saveSettingsOnly = true)
                showSnackBar(
                    text = when (enabled) {
                        true -> context.getString(R.string.settings_newsnotify_newsglobal_enabled)
                        false -> context.getString(R.string.settings_newsnotify_newsglobal_disabled)
                    },
                    clearPrevious = true
                )
            },
            isNewsSubjectEnabled = getMainViewModel().appSettings.value.newsBackgroundSubjectEnabled,
            onNewsSubjectStateChanged = f@ { code ->
                if (code == 1) {
                    showSnackBar(
                        text = "\"Match your subject schedule\" option is in development. Check back soon.",
                        clearPrevious = true
                    )
                    return@f
                }

                val dataTemp = getMainViewModel().appSettings.value.clone(
                    newsBackgroundSubjectEnabled = code
                )
                getMainViewModel().appSettings.value = dataTemp
                getMainViewModel().saveSettings(saveSettingsOnly = true)
                showSnackBar(
                    text = when (code) {
                        -1 -> context.getString(R.string.settings_newsnotify_newssubject_notify_disabled)
                        0 -> context.getString(R.string.settings_newsnotify_newssubject_notify_all)
                        1 -> context.getString(R.string.settings_newsnotify_newssubject_notify_matchsubsch)
                        2 -> context.getString(R.string.settings_newsnotify_newssubject_notify_matchfilter)
                        // TODO: No code valid
                        else -> "----------"
                    },
                    clearPrevious = true
                )
            },
            subjectFilterList = getMainViewModel().appSettings.value.newsBackgroundFilterList,
            onSubjectFilterAdd = {
                // Add a filter
                dialogAddNew.value = true
            },
            onSubjectFilterDelete = { data ->
                // Delete a filter
                tempDeleteItem.value = data
                dialogDeleteItem.value = true
            },
            onSubjectFilterClear = {
                // Delete all filters
                dialogDeleteAll.value = true
            },
            opacity = getControlBackgroundAlpha()
        )
    }
    AddNewSubjectFilterDialog(
        context = context,
        isVisible = dialogAddNew.value,
        onDismiss = { dialogAddNew.value = false },
        onDone = { syId, cId, subName ->
            // Add item manually
            try {
                val item = SubjectCode(syId, cId, subName)
                getMainViewModel().appSettings.value.newsBackgroundFilterList.add(item)
                getMainViewModel().saveSettings(saveSettingsOnly = true)
                showSnackBar(
                    text = context.getString(
                        R.string.settings_newsnotify_newsfilter_notify_add,
                        subName,
                        syId,
                        ".Nh",
                        cId
                    ),
                    clearPrevious = true
                )
            } catch (_: Exception) { }

            dialogAddNew.value = false
        }
    )
    DeleteASubjectFilterDialog(
        context = context,
        subjectCode = tempDeleteItem.value,
        isVisible = dialogDeleteItem.value,
        onDismiss = { dialogDeleteItem.value = false },
        onDone = {
            // Clear item on tempDeleteItem.value
            try {
                getMainViewModel().appSettings.value.newsBackgroundFilterList.remove(tempDeleteItem.value)
                getMainViewModel().saveSettings(saveSettingsOnly = true)
                showSnackBar(
                    text = context.getString(
                        R.string.settings_newsnotify_newsfilter_notify_delete,
                        tempDeleteItem.value.subjectName,
                        tempDeleteItem.value.studentYearId,
                        ".Nh",
                        tempDeleteItem.value.classId
                    ),
                    clearPrevious = true
                )
            } catch (_: Exception) { }

            dialogDeleteItem.value = false
        }
    )
    DeleteAllSubjectFilterDialog(
        context = context,
        isVisible = dialogDeleteAll.value,
        onDismiss = { dialogDeleteAll.value = false },
        onDone = {
            // Clear all items
            try {
                getMainViewModel().appSettings.value.newsBackgroundFilterList.clear()
                getMainViewModel().saveSettings(saveSettingsOnly = true)
                showSnackBar(
                    text = context.getString(R.string.settings_newsnotify_newsfilter_notify_deleteall),
                    clearPrevious = true
                )
            } catch (_: Exception) { }
            dialogDeleteAll.value = false
        }
    )
    BackHandler(dialogAddNew.value || dialogDeleteItem.value || dialogDeleteAll.value) {
        if (dialogAddNew.value) {
            dialogAddNew.value = false
        }
        if (dialogDeleteItem.value) {
            dialogDeleteItem.value = false
        }
        if (dialogDeleteAll.value) {
            dialogDeleteAll.value = false
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MainView(
    context: Context,
    padding: PaddingValues = PaddingValues(0.dp),
    fetchNewsInBackgroundDuration: Int = 0,
    onFetchNewsStateChanged: ((Int) -> Unit)? = null,
    isNewSubjectNotificationParseEnabled: Boolean = false,
    onNewSubjectNotificationParseClick: (() -> Unit)? = null,
    isNewsGlobalEnabled: Boolean = false,
    onNewsGlobalStateChanged: ((Boolean) -> Unit)? = null,
    isNewsSubjectEnabled: Int = -1,
    onNewsSubjectStateChanged: ((Int) -> Unit)? = null,
    subjectFilterList: ArrayList<SubjectCode> = arrayListOf(),
    onSubjectFilterAdd: (() -> Unit)? = null,
    onSubjectFilterDelete: ((SubjectCode) -> Unit)? = null,
    onSubjectFilterClear: (() -> Unit)? = null,
    opacity: Float = 1f
) {
    val durationTemp = remember {
        mutableIntStateOf(fetchNewsInBackgroundDuration)
    }

    Column(
        modifier = Modifier
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        SwitchWithTextInSurface(
            text = context.getString(R.string.settings_newsnotify_fetchnewsinbackground),
            enabled = true,
            checked = fetchNewsInBackgroundDuration > 0,
            onCheckedChange = {
                // Refresh news state changed, default is 30 minutes
                onFetchNewsStateChanged?.let { it(when {
                    (fetchNewsInBackgroundDuration > 0) -> 0
                    else -> 30
                }) }
            }
        )
        ContentRegion(
            modifier = Modifier.padding(top = 10.dp),
            textModifier = Modifier.padding(horizontal = 20.dp),
            text = context.getString(R.string.settings_newsnotify_category_notification)
        ) {
            SimpleCardItem(
                padding = PaddingValues(horizontal = 20.4.dp, vertical = 5.dp),
                title = context.getString(R.string.settings_newsnotify_fetchnewsinbackground_duration),
                opacity = opacity,
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 15.dp)
                            .padding(top = 5.dp, bottom = 10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            context.getString(
                                R.string.settings_newsnotify_fetchnewsinbackground_value,
                                when (fetchNewsInBackgroundDuration) {
                                    0 -> context.getString(R.string.settings_newsnotify_fetchnewsinbackground_value_disabled)
                                    1 -> context.getString(R.string.settings_newsnotify_fetchnewsinbackground_value_enabled1)
                                    else -> context.getString(
                                        R.string.settings_newsnotify_fetchnewsinbackground_value_enabled2,
                                        fetchNewsInBackgroundDuration
                                    )
                                }
                            ),
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Slider(
                            valueRange = 5f..240f,
                            steps = 236,
                            value = durationTemp.intValue.toFloat(),
                            enabled = fetchNewsInBackgroundDuration > 0,
                            colors = SliderDefaults.colors(
                                activeTickColor = Color.Transparent,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTickColor = Color.Transparent,
                                inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                            ),
                            onValueChange = {
                                durationTemp.intValue = it.toInt()
                            }
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            content = {
                                Text(
                                    when (durationTemp.intValue) {
                                        0 -> context.getString(R.string.settings_newsnotify_fetchnewsinbackground_modifiedvalue_disabled)
                                        1 -> context.getString(R.string.settings_newsnotify_fetchnewsinbackground_modifiedvalue_enabled1)
                                        else -> context.getString(
                                            R.string.settings_newsnotify_fetchnewsinbackground_modifiedvalue_enabled2,
                                            durationTemp.intValue
                                        )
                                    }
                                )
                            }
                        )
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 7.dp),
                            horizontalArrangement = Arrangement.Center,
                            content = {
                                listOf(15, 30, 60).forEach { min ->
                                    SuggestionChip(
                                        modifier = Modifier.padding(horizontal = 5.dp),
                                        icon = {
                                            if (durationTemp.intValue == min) {
                                                Icon(
                                                    Icons.Default.Check,
                                                    context.getString(R.string.tooltip_selected),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            if (fetchNewsInBackgroundDuration > 0) {
                                                durationTemp.intValue = min
                                            }
                                        },
                                        label = {
                                            Text(when (min) {
                                                0 -> context.getString(R.string.settings_newsnotify_fetchnewsinbackground_option_turnoff)
                                                1 -> context.getString(R.string.settings_newsnotify_fetchnewsinbackground_option_value1)
                                                else -> context.getString(
                                                    R.string.settings_newsnotify_fetchnewsinbackground_option_value2,
                                                    min
                                                )
                                            })
                                        }
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.size(5.dp))
                        ElevatedButton(
                            onClick = {
                                if (fetchNewsInBackgroundDuration > 0) {
                                    onFetchNewsStateChanged?.let { it(durationTemp.intValue) }
                                }
                            },
                            content = {
                                Text(context.getString(R.string.action_save))
                            }
                        )
                    }
                }
            )
            OptionItem(
                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                title = context.getString(R.string.settings_parsenewssubject_title),
                description = when (isNewSubjectNotificationParseEnabled) {
                    true -> context.getString(R.string.settings_newsnotify_parsenewssubject_enabled)
                    false -> context.getString(R.string.settings_newsnotify_parsenewssubject_disabled)
                },
                onClick = { onNewSubjectNotificationParseClick?.let { it() } }
            )
        }
        DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
        ContentRegion(
            textModifier = Modifier.padding(horizontal = 20.dp),
            text = context.getString(R.string.settings_newsnotify_newsglobal_title)
        ) {
            CheckboxOption(
                title = context.getString(R.string.settings_newsnotify_newsglobal_enable),
                modifierInside = Modifier.padding(horizontal = 6.5.dp),
                isEnabled = fetchNewsInBackgroundDuration > 0,
                isChecked = isNewsGlobalEnabled,
                onClick = {
                    // Refresh news state changed
                    onNewsGlobalStateChanged?.let { it(!isNewsGlobalEnabled) }
                }
            )
        }
        ContentRegion(
            modifier = Modifier.padding(top = 10.dp),
            textModifier = Modifier.padding(horizontal = 20.dp),
            text = context.getString(R.string.settings_newsnotify_newssubject_title)
        ) {
            RadioButtonOption(
                modifierInside = Modifier.padding(horizontal = 6.5.dp),
                title = context.getString(R.string.settings_newsnotify_newssubject_disabled),
                isEnabled = fetchNewsInBackgroundDuration > 0,
                isChecked = isNewsSubjectEnabled == -1,
                onClick = {
                    // Subject news notification off - onClick
                    onNewsSubjectStateChanged?.let { it(-1) }
                }
            )
            RadioButtonOption(
                modifierInside = Modifier.padding(horizontal = 6.5.dp),
                title = context.getString(R.string.settings_newsnotify_newssubject_all),
                isEnabled = fetchNewsInBackgroundDuration > 0,
                isChecked = isNewsSubjectEnabled == 0,
                onClick = {
                    // Subject news notification all - onClick
                    onNewsSubjectStateChanged?.let { it(0) }
                }
            )
            RadioButtonOption(
                modifierInside = Modifier.padding(horizontal = 6.5.dp),
                title = context.getString(R.string.settings_newsnotify_newssubject_matchsubsch),
                isEnabled = fetchNewsInBackgroundDuration > 0,
                isChecked = isNewsSubjectEnabled == 1,
                onClick = {
                    // Subject news notification your subject schedule - onClick
                    onNewsSubjectStateChanged?.let { it(1) }
                }
            )
            RadioButtonOption(
                modifierInside = Modifier.padding(horizontal = 6.5.dp),
                title = context.getString(R.string.settings_newsnotify_newssubject_matchfilter),
                isEnabled = fetchNewsInBackgroundDuration > 0,
                isChecked = isNewsSubjectEnabled == 2,
                onClick = {
                    // Subject news notification custom list - onClick
                    onNewsSubjectStateChanged?.let { it(2) }
                }
            )
        }
        DividerItem(padding = PaddingValues(top = 5.dp, bottom = 15.dp))
        ContentRegion(
            textModifier = Modifier.padding(horizontal = 20.dp),
            text = context.getString(R.string.settings_newsnotify_newsfilter_title)
        ) {
            if (isNewsSubjectEnabled != 2) {
                SimpleCardItem(
                    padding = PaddingValues(horizontal = 20.4.dp, vertical = 7.dp),
                    title = context.getString(R.string.settings_newsnotify_newsfilter_disabledwarning_title),
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 15.dp)
                                .padding(bottom = 15.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(context.getString(R.string.settings_newsnotify_newsfilter_disabledwarning_description))
                        }
                    },
                    opacity = opacity
                )
            } else {
                SimpleCardItem(
                    padding = PaddingValues(horizontal = 20.4.dp, vertical = 5.dp),
                    title = context.getString(R.string.settings_newsnotify_newsfilter_list_title),
                    opacity = opacity,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(horizontal = 15.dp)
                                .padding(bottom = 15.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            if (subjectFilterList.size == 0) {
                                Text(context.getString(R.string.settings_newsnotify_newsfilter_list_nofilters))
                            }
                            subjectFilterList.forEach { code ->
                                OptionItem(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    modifierInside = Modifier,
                                    title = "${code.subjectName} [${code.studentYearId}.Nh${code.classId}]",
                                    onClick = { },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                if (fetchNewsInBackgroundDuration > 0) {
                                                    onSubjectFilterDelete?.let { it(code) }
                                                }
                                            },
                                            content = {
                                                Icon(Icons.Default.Delete, context.getString(R.string.action_delete))
                                            }
                                        )
                                    }
                                )
                            }
                        }
                    }
                )
                OptionItem(
                    modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                    title = context.getString(R.string.settings_newsnotify_newsfilter_add),
                    leadingIcon = { Icon(Icons.Default.Add, context.getString(R.string.settings_newsnotify_newsfilter_add)) },
                    isEnabled = isNewsSubjectEnabled == 2,
                    onClick = {
                        // Add a subject news filter
                        onSubjectFilterAdd?.let { it() }
                    }
                )
                OptionItem(
                    modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                    title = context.getString(R.string.settings_newsnotify_newsfilter_deleteall),
                    leadingIcon = { Icon(Icons.Default.Delete, context.getString(R.string.settings_newsnotify_newsfilter_deleteall)) },
                    isEnabled = isNewsSubjectEnabled == 2,
                    onClick = {
                        // Clear all subject news filter list
                        onSubjectFilterClear?.let { it() }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainViewPreview() {
    MainView(
        context = LocalContext.current,
        fetchNewsInBackgroundDuration = 30,
        onFetchNewsStateChanged = { },
        isNewsGlobalEnabled = true,
        subjectFilterList = arrayListOf(
            SubjectCode("19", "12", "Nhập môn ngành"),
            SubjectCode("19", "12", "PBL3")
        ),
        isNewsSubjectEnabled = 2
    )
}