package io.zoemeow.dutschedule.ui.component.main

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.Utils
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.CustomClock
import io.zoemeow.dutschedule.utils.CustomDateUtil
import kotlinx.coroutines.delay

@Composable
fun DateAndTimeSummaryItem(
    context: Context,
    padding: PaddingValues = PaddingValues(),
    isLoading: Boolean = false,
    currentSchoolWeek: Utils.DutSchoolYearItem? = null,
    opacity: Float = 1.0f
) {
    val dateTimeString = remember { mutableStateOf("") }

    SummaryItem(
        padding = padding,
        title = context.getString(R.string.main_dashboard_widget_datetime_title),
        isLoading = false,
        opacity = opacity,
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                content = {
                    Text(
                        text = dateTimeString.value,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 15.dp)
                    )
                    when (isLoading) {
                        true -> {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = 15.dp)
                                    .padding(bottom = 10.dp),
                                contentAlignment = Alignment.Center,
                                content = {
                                    CircularProgressIndicator()
                                }
                            )
                        }
                        false -> {
                            Text(
                                text = context.getString(
                                    R.string.main_dashboard_widget_datetime_schoolstat,
                                    currentSchoolWeek?.schoolYear ?: context.getString(R.string.data_unknown),
                                    currentSchoolWeek?.week?.toString() ?: context.getString(R.string.data_unknown),
                                    CustomClock.getCurrent().toDUTLesson2().name
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .padding(horizontal = 15.dp)
                                    .padding(bottom = 10.dp)
                            )
                        }
                    }
                }
            )
        }
    )

    LaunchedEffect(Unit) {
        while (true) {
            context.getString(
                R.string.main_dashboard_widget_datetime_datetimestat,
                CustomDateUtil.getCurrentDateAndTimeToString("dd/MM/yyyy HH:mm:ss")
            ).also { dateTimeString.value = it }
            delay(1000)
        }
    }
}