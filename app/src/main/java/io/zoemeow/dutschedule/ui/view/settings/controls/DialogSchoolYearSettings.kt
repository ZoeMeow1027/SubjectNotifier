package io.zoemeow.dutschedule.ui.view.settings.controls

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
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
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.account.SchoolYearItem
import io.zoemeow.dutschedule.ui.components.DataAdjuster
import io.zoemeow.dutschedule.ui.components.DialogBase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

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
                fetchProcess.value = ProcessState.Successful
                Log.d("SchoolYearCurrent", "Successful! Data from internet: $schYear")
            } catch (_: Exception) {
                fetchProcess.value = ProcessState.Failed
                Log.d("SchoolYearCurrent", "Failed while getting from internet!")
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
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(context.getString(R.string.settings_dialog_schyear_choice_schyear))
                        DataAdjuster(
                            modifier = Modifier.fillMaxWidth(),
                            text = String.format(
                                locale = Locale.ROOT,
                                "20%02d-20%02d",
                                currentSettings.value.year,
                                currentSettings.value.year+1
                            ),
                            leadingEnabled = currentSettings.value.year > 9,
                            trailingEnabled = currentSettings.value.year < 28,
                            onLeadingClicked = {
                                currentSettings.value = currentSettings.value.clone(
                                    year = currentSettings.value.year - 1
                                )
                            },
                            onTrailingClicked = {
                                currentSettings.value = currentSettings.value.clone(
                                    year = currentSettings.value.year + 1
                                )
                            }
                        )
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 7.dp),
                    shape = RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(context.getString(R.string.settings_dialog_schyear_choice_semester))
                        DataAdjuster(
                            modifier = Modifier.fillMaxWidth(),
                            text = String.format(
                                Locale.ROOT,
                                "%s %d%s",
                                context.getString(R.string.settings_dialog_schyear_choice_semester),
                                if (currentSettings.value.semester <= 2) currentSettings.value.semester else 2,
                                if (currentSettings.value.semester > 2) " (${context.getString(R.string.settings_dialog_schyear_choice_insummer)})" else ""
                            ),
                            leadingEnabled = currentSettings.value.semester > 1,
                            trailingEnabled = currentSettings.value.semester < 3,
                            onLeadingClicked = {
                                currentSettings.value = currentSettings.value.clone(
                                    semester = currentSettings.value.semester - 1
                                )
                            },
                            onTrailingClicked = {
                                currentSettings.value = currentSettings.value.clone(
                                    semester = currentSettings.value.semester + 1
                                )
                            }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp),
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
                                                contentDescription = when (fetchProcess.value) {
                                                    ProcessState.Successful -> "Successful"
                                                    ProcessState.Failed -> "Failed"
                                                    else -> context.getString(R.string.action_refresh)
                                                }
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