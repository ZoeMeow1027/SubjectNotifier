package io.zoemeow.dutschedule.ui.view.miscellaneous.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.model.HelpLinkInfo

@Composable
fun ClickableExternalLinks(
    item: HelpLinkInfo,
    modifier: Modifier = Modifier,
    opacity: Float = 1f,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .run {
                if (onClick != null) return@run this.clickable { onClick() }
                else return@run this
            },
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = opacity),
        shape = RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
        ) {
            Text(
                item.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(item.url)
            item.description?.let {
                Spacer(modifier = Modifier.size(15.dp))
                Text(item.description)
            }
        }
    }
}