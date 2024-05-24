package io.zoemeow.dutschedule.ui.component.settings.dialog

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.Utils
import io.dutwrapper.dutwrapper.model.utils.DutSchoolYearItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.SettingsActivity
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.account.SchoolYearItem
import io.zoemeow.dutschedule.ui.component.base.DialogBase
import io.zoemeow.dutschedule.ui.component.base.OutlinedTextBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogSchoolYearSettings(
    context: Context,
    isVisible: Boolean = false,
    dismissRequested: (() -> Unit)? = null,
    currentSchoolYearItem: SchoolYearItem,
    onSubmit: ((SchoolYearItem) -> Unit)? = null
) {
    val currentSettings = remember { mutableStateOf(SchoolYearItem()) }
    val dropDownSchoolYear = remember { mutableStateOf(false) }
    val dropDownSemester = remember { mutableStateOf(false) }

    LaunchedEffect(isVisible) {
        currentSettings.value = currentSchoolYearItem
        dropDownSchoolYear.value = false
        dropDownSemester.value = false
    }

    val fetchProcess = remember { mutableStateOf(ProcessState.NotRunYet) }
    fun fetchProcess() {
        CoroutineScope(Dispatchers.IO).launch {
            if (fetchProcess.value == ProcessState.Running) {
                return@launch
            }
            fetchProcess.value = ProcessState.Running

            try {
                Log.d("SchoolYearCurrent", "Getting from internet...")
                val data = Utils.getCurrentSchoolWeek()
                val schYear = SchoolYearItem(
                    year = data.schoolYearVal,
                    semester = when {
                        data.week >= 48 -> 3
                        data.week >= 27 -> 2
                        else -> 1
                    }
                )
                currentSettings.value = schYear
                Log.d("SchoolYearCurrent", "Successful! Data from internet: $schYear")
                fetchProcess.value = ProcessState.Successful
            } catch (_: Exception) {
                Log.d("SchoolYearCurrent", "Failed while getting from internet!")
                fetchProcess.value = ProcessState.Failed
            }
        }
    }

    DialogBase(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        title = context.getString(R.string.settings_dialog_schyear_title),
        isVisible = isVisible,
        canDismiss = false,
        isTitleCentered = true,
        dismissClicked = {
            dismissRequested?.let { it() }
        },
        content = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    context.getString(R.string.settings_dialog_schyear_description),
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                ExposedDropdownMenuBox(
                    expanded = dropDownSchoolYear.value,
                    onExpandedChange = { dropDownSchoolYear.value = !dropDownSchoolYear.value },
                    content = {
                        OutlinedTextBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            title = context.getString(R.string.settings_dialog_schyear_choice_schyear),
                            value = String.format(
                                Locale.ROOT,
                                "20%d-20%d",
                                currentSettings.value.year,
                                currentSettings.value.year+1
                            )
                        )
                        DropdownMenu(
                            expanded = dropDownSchoolYear.value,
                            onDismissRequest = { dropDownSchoolYear.value = false },
                            content = {
                                27.downTo(10).forEach {
                                    DropdownMenuItem(
                                        text = { Text(String.format(
                                            Locale.ROOT,
                                            "20%2d-20%2d",
                                            it,
                                            it+1
                                        )) },
                                        onClick = {
                                            currentSettings.value = currentSettings.value.clone(
                                                year = it
                                            )
                                            dropDownSchoolYear.value = false
                                        }
                                    )
                                }
                            }
                        )
                    }
                )
                ExposedDropdownMenuBox(
                    expanded = dropDownSemester.value,
                    onExpandedChange = { dropDownSemester.value = !dropDownSemester.value },
                    content = {
                        OutlinedTextBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            title = context.getString(R.string.settings_dialog_schyear_choice_semester),
                            value = String.format(
                                Locale.ROOT,
                                "%s %d%s",
                                context.getString(R.string.settings_dialog_schyear_choice_semester),
                                if (currentSettings.value.semester <= 2) currentSettings.value.semester else 2,
                                if (currentSettings.value.semester > 2) " (${context.getString(R.string.settings_dialog_schyear_choice_insummer)})" else ""
                            )
                        )
                        DropdownMenu(
                            expanded = dropDownSemester.value,
                            onDismissRequest = { dropDownSemester.value = false },
                            content = {
                                1.rangeTo(3).forEach {
                                    DropdownMenuItem(
                                        text = { Text(String.format(
                                            Locale.ROOT,
                                            "%s %d%s",
                                            context.getString(R.string.settings_dialog_schyear_choice_semester),
                                            if (it <= 2) it else 2,
                                            if (it > 2) " (${context.getString(R.string.settings_dialog_schyear_choice_insummer)})" else ""
                                        )) },
                                        onClick = {
                                            currentSettings.value = currentSettings.value.clone(
                                                semester = it
                                            )
                                            dropDownSemester.value = false
                                        }
                                    )
                                }
                            }
                        )
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 7.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    content = {
                        ElevatedButton(
                            onClick = {
                                fetchProcess()
                            },
                            content = {
                                if (fetchProcess.value == ProcessState.Running) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Row(
                                        content = {
                                            Text(context.getString(R.string.settings_dialog_schyear_action_fetch))
                                            Spacer(modifier = Modifier.size(5.dp))
                                            Icon(
                                                imageVector = when (fetchProcess.value) {
                                                    ProcessState.Successful -> Icons.Default.Check
                                                    ProcessState.Failed -> Icons.Default.Close
                                                    else -> Icons.Default.Refresh
                                                },
                                                contentDescription = ""
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    }
                )
            }
        },
        actionButtons = {
            TextButton(
                onClick = { onSubmit?.let { it(currentSettings.value) } },
                content = { Text(context.getString(R.string.action_save)) },
                modifier = Modifier.padding(start = 8.dp),
            )
            TextButton(
                onClick = { dismissRequested?.let { it() } },
                content = { Text(context.getString(R.string.action_cancel)) },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    )
}