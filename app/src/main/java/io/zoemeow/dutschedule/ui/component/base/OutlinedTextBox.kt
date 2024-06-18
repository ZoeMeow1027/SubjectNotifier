package io.zoemeow.dutschedule.ui.component.base

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun OutlinedTextBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String? = null,
    placeHolderIfNull: String? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        modifier = modifier,
        value = if (value.isNullOrEmpty()) {
            placeHolderIfNull ?: ""
        } else value,
        readOnly = true,
        onValueChange = { },
        trailingIcon = trailingIcon,
        label = { Text(title) }
    )
}