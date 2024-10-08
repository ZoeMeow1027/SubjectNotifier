package io.zoemeow.dutschedule.ui.view.account.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.AccountInformation.SubjectInformation
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.utils.CustomDateUtils

@Composable
fun SubjectInformation(
    modifier: Modifier = Modifier,
    item: SubjectInformation,
    onClick: (() -> Unit)? = null,
    opacity: Float = 1f
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE))
            .clickable {
                onClick?.let { it() }
            },
        color = MaterialTheme.colorScheme.secondaryContainer.copy(
            alpha = opacity
        ),
        content = {
            Column(
                modifier = Modifier.padding(10.dp),
                content = {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Text(
                        text = context.getString(
                            R.string.account_subjectinfo_summary_schinfo,
                            item.lecturer,
                            item.scheduleStudy.scheduleList.joinToString(
                                separator = "\n",
                                transform = { schItem ->
                                    context.getString(
                                        R.string.account_subjectinfo_summary_schitem,
                                        CustomDateUtils.dayOfWeekInString(context, schItem.dayOfWeek, true),
                                        schItem.lesson.start,
                                        schItem.lesson.end,
                                        schItem.room
                                    )
                                }
                            )
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    )
}