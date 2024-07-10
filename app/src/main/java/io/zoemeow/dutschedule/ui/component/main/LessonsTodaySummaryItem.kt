package io.zoemeow.dutschedule.ui.component.main

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.model.accounts.SubjectScheduleItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.CustomClock
import java.util.Locale

@Composable
fun LessonTodaySummaryItem(
    context: Context,
    hasLoggedIn: Boolean = false,
    isLoading: Boolean = false,
    affectedList: List<SubjectScheduleItem> = listOf(),
    padding: PaddingValues = PaddingValues(),
    clicked: () -> Unit,
    opacity: Float = 1.0f
) {
    SummaryItem(
        padding = padding,
        title = context.getString(R.string.main_dashboard_widget_lessontoday_title),
        clicked = clicked,
        isLoading = isLoading,
        opacity = opacity,
        content = {
            Text(
                text = when {
                    !hasLoggedIn -> context.getString(R.string.main_dashboard_widget_msg_notloggedin)
                    affectedList.isEmpty() -> context.getString(R.string.main_dashboard_widget_lessontoday_completed)
                    else -> {
                        run {
                            val result = arrayListOf<String>()
                            val currentLesson = CustomClock.getCurrent().toDUTLesson2()
                            affectedList.forEach { item ->
                                val childResult = String.format(
                                    Locale.ROOT,
                                    "%s (%s)",
                                    item.name,
                                    item.subjectStudy.scheduleList.filter { it.lesson.end >= currentLesson.lesson }.joinToString(
                                        separator = ", ",
                                        transform = { String.format(Locale.ROOT, "%d-%d", it.lesson.start, it.lesson.end) }
                                    )
                                )
                                result.add(childResult)
                            }

                            return@run result.joinToString(separator = "\n")
                        }.also { affectedString ->
                            if (CustomClock.getCurrent().toDUTLesson2().lesson in 1.0..14.0) {
                                if (affectedList.size == 1) {
                                    context.getString(
                                        R.string.main_dashboard_widget_lessontoday_availabletoday_1,
                                        affectedString
                                    )
                                } else {
                                    context.getString(
                                        R.string.main_dashboard_widget_lessontoday_availabletoday_other,
                                        affectedList.size,
                                        affectedString
                                    )
                                }
                            } else {
                                if (affectedList.size == 1) {
                                    context.getString(
                                        R.string.main_dashboard_widget_lessontoday_availablepending_1,
                                        affectedString
                                    )
                                } else {
                                    context.getString(
                                        R.string.main_dashboard_widget_lessontoday_availablepending_other,
                                        affectedList.size,
                                        affectedString
                                    )
                                }
                            }
                        }
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .padding(bottom = 10.dp)
            )
        }
    )
}