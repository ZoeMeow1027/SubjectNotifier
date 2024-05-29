package io.zoemeow.dutschedule.ui.component.settings

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
import io.zoemeow.dutschedule.activity.SettingsActivity
import io.zoemeow.dutschedule.ui.component.base.DialogBase

@Composable
fun SettingsActivity.DeleteAllSubjectFilterDialog(
    context: Context,
    isVisible: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    DialogBase(
        modifier = Modifier.fillMaxWidth().padding(25.dp),
        title = context.getString(R.string.settings_newsnotify_newsfilter_dialogdeleteall_title),
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
                    text = context.getString(R.string.settings_newsnotify_newsfilter_dialogdeleteall_description),
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