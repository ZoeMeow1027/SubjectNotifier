package io.zoemeow.dutschedule.ui.component.base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SimpleCardItem(
    title: String,
    titleStyle: TextStyle = MaterialTheme.typography.titleLarge,
    isTitleCentered: Boolean = false,
    content: @Composable (() -> Unit)? = null,
    clicked: (() -> Unit)? = null,
    padding: PaddingValues = PaddingValues(10.dp),
    opacity: Float = 1.0f
) {
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(padding)
            .clip(RoundedCornerShape(7.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = opacity))
            .run {
                if (clicked != null) return@run this.clickable { clicked() }
                else return@run this
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            horizontalArrangement = if (isTitleCentered) Arrangement.Center else Arrangement.Start,
            content = {
                Text(
                    text = title,
                    style = titleStyle,
                    textAlign = if (isTitleCentered) TextAlign.Center else TextAlign.Left,
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                )
            }
        )
        content?.let { it() }
    }
}

@Preview
@Composable
private fun Preview() {
    SimpleCardItem(
        title = "Abc",
        content = {
            Text("1")
            Text("2")
            Text("3")
            Text("4")
        }
    )
}