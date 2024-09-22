package io.zoemeow.dutschedule.ui.view.account.controls

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.ui.component.DialogBase

@Composable
fun LogoutDialog(
    context: Context,
    isVisible: Boolean = false,
    canDismiss: Boolean = false,
    logoutClicked: (() -> Unit)? = null,
    dismissClicked: (() -> Unit)? = null
) {
    DialogBase(
        modifier = Modifier.fillMaxWidth().padding(25.dp),
        isVisible = isVisible,
        title = context.getString(R.string.account_logout_title),
        canDismiss = canDismiss,
        dismissClicked = {
            dismissClicked?.let { it() }
        },
        content = {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                Text(context.getString(R.string.account_logout_description))
            }
        },
        actionButtons = {
            TextButton(
                onClick = { dismissClicked?.let { it() } },
                content = { Text(context.getString(R.string.action_cancel)) },
                modifier = Modifier.padding(start = 8.dp),
            )
            TextButton(
                onClick = { logoutClicked?.let { it() } },
                content = { Text(context.getString(R.string.account_logout_action_logout)) },
            )
        }
    )
}