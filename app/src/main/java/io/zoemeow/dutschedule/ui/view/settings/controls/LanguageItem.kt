package io.zoemeow.dutschedule.ui.view.settings.controls

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.zoemeow.dutschedule.R

@Composable
fun LanguageItem(
    title: String,
    context: Context,
    selected: Boolean = false,
    clicked: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.clickable {
            clicked?.let { it() }
        },
        color = Color.Transparent,
        content = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Text(
                        title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 19.sp
                    )
                    if (selected) {
                        Icon(Icons.Default.Check, context.getString(R.string.tooltip_selected))
                    }
                }
            )
        }
    )
}