package io.zoemeow.dutschedule.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DataAdjuster(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle? = null,
    leadingEnabled: Boolean = true,
    trailingEnabled: Boolean = true,
    leadingText: String = "<",
    trailingText: String = ">",
    onLeadingClicked: (() -> Unit)? = null,
    onTrailingClicked: (() -> Unit)? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        content = {
            TextButton(
                enabled = leadingEnabled,
                onClick = { onLeadingClicked?.let { it() } },
                content = { Text(
                    leadingText,
                    style = MaterialTheme.typography.headlineMedium
                ) }
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text,
                style = textStyle ?: MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.size(2.dp))
            TextButton(
                enabled = trailingEnabled,
                onClick = { onTrailingClicked?.let { it() } },
                content = { Text(
                    trailingText,
                    style = MaterialTheme.typography.headlineMedium
                ) }
            )
        }
    )
}

@Preview
@Composable
private fun DataAdjusterPreview() {
    DataAdjuster(text = "2021-2022")
}