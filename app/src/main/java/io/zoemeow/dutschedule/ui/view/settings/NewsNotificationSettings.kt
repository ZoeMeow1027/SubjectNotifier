package io.zoemeow.dutschedule.ui.view.settings

import android.content.Context
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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.settings.SubjectCode
import io.zoemeow.dutschedule.ui.component.base.CheckboxOption
import io.zoemeow.dutschedule.ui.component.base.DividerItem
import io.zoemeow.dutschedule.ui.component.base.OptionItem
import io.zoemeow.dutschedule.ui.component.base.RadioButtonOption
import io.zoemeow.dutschedule.ui.component.base.SimpleCardItem
import io.zoemeow.dutschedule.ui.component.base.SwitchWithTextInSurface
import io.zoemeow.dutschedule.ui.component.settings.Dialog_Settings_NewsNotificationSettings_Add
import io.zoemeow.dutschedule.ui.component.settings.ContentRegion
import io.zoemeow.dutschedule.ui.component.settings.Dialog_Settings_NewsNotificationSettings_ClearAll
import io.zoemeow.dutschedule.ui.component.settings.Dialog_Settings_NewsNotificationSettings_Delete

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Activity_Settings_NewsNotificationSettings(
    context: Context,
    snackBarHostState: SnackbarHostState?,
    appearanceState: AppearanceState,
    onBack: () -> Unit,
    fetchNewsInBackgroundDuration: Int = 0,
    onFetchNewsStateChanged: ((Int) -> Unit)? = null,
    isNewSubjectNotificationParseEnabled: Boolean = false,
    onNewSubjectNotificationParseClick: (() -> Unit)? = null,
    isNewsGlobalEnabled: Boolean = false,
    onNewsGlobalStateChanged: ((Boolean) -> Unit)? = null,
    isNewsSubjectEnabled: Int = -1,
    onNewsSubjectStateChanged: ((Int) -> Unit)? = null,
    subjectFilterList: ArrayList<SubjectCode> = arrayListOf(),
    onSubjectFilterAdd: ((SubjectCode) -> Unit)? = null,
    onSubjectFilterDelete: ((SubjectCode) -> Unit)? = null,
    onSubjectFilterClear: (() -> Unit)? = null
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val durationTemp = remember {
        mutableIntStateOf(fetchNewsInBackgroundDuration)
    }

    val dialogAddNew = remember { mutableStateOf(false) }
    val tempDeleteItem: MutableState<SubjectCode> = remember { mutableStateOf(SubjectCode("","","")) }
    val dialogDeleteItem = remember { mutableStateOf(false) }
    val dialogDeleteAll = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
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
                title = { Text(context.getString(R.string.settings_newsnotify_title)) },
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
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
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
                    opacity = appearanceState.componentOpacity,
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
                        opacity = appearanceState.backgroundOpacity
                    )
                } else {
                    SimpleCardItem(
                        padding = PaddingValues(horizontal = 20.4.dp, vertical = 5.dp),
                        title = context.getString(R.string.settings_newsnotify_newsfilter_list_title),
                        opacity = appearanceState.backgroundOpacity,
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
                                                        tempDeleteItem.value = code
                                                        dialogDeleteItem.value = true
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
                    @Suppress("KotlinConstantConditions")
                    OptionItem(
                        modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                        title = context.getString(R.string.settings_newsnotify_newsfilter_add),
                        leadingIcon = { Icon(Icons.Default.Add, context.getString(R.string.settings_newsnotify_newsfilter_add)) },
                        isEnabled = isNewsSubjectEnabled == 2,
                        onClick = {
                            // Add a subject news filter
                            dialogAddNew.value = true
                        }
                    )
                    @Suppress("KotlinConstantConditions")
                    OptionItem(
                        modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                        title = context.getString(R.string.settings_newsnotify_newsfilter_deleteall),
                        leadingIcon = { Icon(Icons.Default.Delete, context.getString(R.string.settings_newsnotify_newsfilter_deleteall)) },
                        isEnabled = isNewsSubjectEnabled == 2,
                        onClick = {
                            // Clear all subject news filter list
                            dialogDeleteAll.value = true
                        }
                    )
                }
            }
        }
    }
    Dialog_Settings_NewsNotificationSettings_Add(
        context = context,
        isVisible = dialogAddNew.value,
        onDismiss = { dialogAddNew.value = false },
        onDone = { syId, cId, subName ->
            // Add item manually
            try {
                val item = SubjectCode(syId, cId, subName)
                onSubjectFilterAdd?.let { it(item) }
            } catch (_: Exception) { }

            dialogAddNew.value = false
        }
    )
    Dialog_Settings_NewsNotificationSettings_Delete(
        context = context,
        subjectCode = tempDeleteItem.value,
        isVisible = dialogDeleteItem.value,
        onDismiss = { dialogDeleteItem.value = false },
        onDone = {
            // Clear item on tempDeleteItem.value
            try {
                onSubjectFilterDelete?.let { it(tempDeleteItem.value) }
            } catch (_: Exception) { }

            dialogDeleteItem.value = false
        }
    )
    Dialog_Settings_NewsNotificationSettings_ClearAll(
        context = context,
        isVisible = dialogDeleteAll.value,
        onDismiss = { dialogDeleteAll.value = false },
        onDone = {
            // Clear all items
            try {
                onSubjectFilterClear?.let { it() }
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

@Preview(showBackground = true)
@Composable
private fun MainViewPreview() {
//    // TODO: Fix preview for Activity_Settings_NewsNotificationSettings
//    Activity_Settings_NewsNotificationSettings(
//        context = LocalContext.current,
//        fetchNewsInBackgroundDuration = 30,
//        onFetchNewsStateChanged = { },
//        isNewsGlobalEnabled = true,
//        subjectFilterList = arrayListOf(
//            SubjectCode("19", "12", "Nhập môn ngành"),
//            SubjectCode("19", "12", "PBL3")
//        ),
//        isNewsSubjectEnabled = 2
//    )
}