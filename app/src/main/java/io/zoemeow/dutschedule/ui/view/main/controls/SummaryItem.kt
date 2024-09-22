package io.zoemeow.dutschedule.ui.view.main.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.ui.component.SimpleCardItem

@Composable
fun SummaryItem(
    title: String,
    content: @Composable () -> Unit,
    isLoading: Boolean = false,
    clicked: (() -> Unit)? = null,
    padding: PaddingValues = PaddingValues(10.dp),
    opacity: Float = 1.0f
) {
    SimpleCardItem(
        title = title,
        clicked = clicked,
        padding = padding,
        opacity = opacity,
        content = {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else content()
        }
    )
}