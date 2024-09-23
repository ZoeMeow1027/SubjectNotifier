package io.zoemeow.dutschedule.ui.view.settings.controls

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.settings.SubjectCode
import io.zoemeow.dutschedule.ui.components.DialogBase

@Composable
fun Dialog_Settings_NewsNotificationSettings_Delete(
    context: Context,
    subjectCode: SubjectCode = SubjectCode("", "", ""),
    isVisible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    DialogBase(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        title = context.getString(R.string.settings_newsnotify_newsfilter_dialogdelete_title),
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
                    text = context.getString(
                        R.string.settings_newsnotify_newsfilter_dialogdelete_description,
                        String.format("%s [%s.Nh%s]", subjectCode.subjectName, subjectCode.studentYearId, subjectCode.classId)
                    ),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
        },
        actionButtons = {
            TextButton(
                onClick = { onDismiss?.let { it() } },
                content = { Text(context.getString(R.string.settings_newsnotify_newsfilter_dialogdelete_no)) },
                modifier = Modifier.padding(start = 8.dp),
            )
            ElevatedButton(
                colors = ButtonDefaults.elevatedButtonColors().copy(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                onClick = { onDone?.let { it() } },
                content = { Text(context.getString(R.string.settings_newsnotify_newsfilter_dialogdelete_yes)) },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    )
}