package io.zoemeow.dutschedule.ui.view.settings.controls

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.ui.component.DialogBase

@Composable
fun Dialog_Settings_NewsNotificationSettings_Add(
    context: Context,
    isVisible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    onDone: ((String, String, String) -> Unit)? = null
) {
    val schoolYearId = remember { mutableStateOf("") }
    val classId = remember { mutableStateOf("") }
    val subjectName = remember { mutableStateOf("") }

    fun clearAllTextField() {
        schoolYearId.value = ""
        classId.value = ""
        subjectName.value = ""
    }

    DialogBase(
        modifier = Modifier.fillMaxWidth().padding(25.dp),
        title = context.getString(R.string.settings_newsnotify_newsfilter_dialogadd_title),
        isVisible = isVisible,
        canDismiss = false,
        dismissClicked = { onDismiss?.let { it() } },
        content = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = context.getString(R.string.settings_newsnotify_newsfilter_dialogadd_description),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedTextField(
                            value = schoolYearId.value,
                            onValueChange = { if (it.length <= 2) schoolYearId.value = it },
                            label = { Text(context.getString(R.string.settings_newsnotify_newsfilter_dialogadd_schyear)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .weight(0.5f)
                        )
                        Spacer(modifier = Modifier.size(10.dp))
                        OutlinedTextField(
                            value = classId.value,
                            onValueChange = { if (it.length <= 3) classId.value = it },
                            label = { Text(context.getString(R.string.settings_newsnotify_newsfilter_dialogadd_class)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .weight(0.5f)
                        )
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    OutlinedTextField(
                        value = subjectName.value,
                        onValueChange = { subjectName.value = it },
                        label = { Text(context.getString(R.string.settings_newsnotify_newsfilter_dialogadd_schname)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        actionButtons = {
            TextButton(
                onClick = {
                    onDismiss?.let { it() }
                    clearAllTextField()
                },
                content = { Text(context.getString(R.string.action_cancel)) },
                modifier = Modifier.padding(start = 8.dp),
            )
            TextButton(
                onClick = { onDone?.let {
                    it(schoolYearId.value, classId.value,subjectName.value) }
                    clearAllTextField()
                },
                content = { Text(context.getString(R.string.action_save)) },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    )
}