package io.zoemeow.dutschedule.ui.component.main

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R

@Composable
fun SchoolNewsSummaryItem(
    context: Context,
    padding: PaddingValues,
    clicked: () -> Unit,
    newsToday: Int = 0,
    newsThisWeek: Int = 0,
    isLoading: Boolean = false,
    opacity: Float = 1f
) {
    SummaryItem(
        padding = padding,
        title = context.getString(R.string.main_dashboard_widget_news_title),
        isLoading = isLoading,
        opacity = opacity,
        content = {
            Text(
                text = context.getString(
                    R.string.main_dashboard_widget_news_status,
                    newsToday.toString(),
                    newsThisWeek.toString()
                ),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 15.dp).padding(bottom = 10.dp),
            )
        },
        clicked = clicked,
    )
}