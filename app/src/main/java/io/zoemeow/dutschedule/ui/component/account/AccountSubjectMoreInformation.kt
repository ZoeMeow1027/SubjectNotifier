package io.zoemeow.dutschedule.ui.component.account

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.model.accounts.SubjectScheduleItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.settings.SubjectCode
import io.zoemeow.dutschedule.ui.component.base.DialogBase
import io.zoemeow.dutschedule.utils.CustomDateUtil

@Composable
fun AccountSubjectMoreInformation(
    context: Context,
    item: SubjectScheduleItem? = null,
    isVisible: Boolean = false,
    onAddToFilterRequested: ((SubjectCode) -> Unit)? = null,
    dismissClicked: (() -> Unit)? = null,
) {
    DialogBase(
        modifier = Modifier
            .fillMaxWidth()
            .padding(25.dp),
        title = "${item?.name ?: context.getString(R.string.data_unknown)}\n${item?.lecturer ?: context.getString(R.string.data_unknown)}",
        isVisible = isVisible,
        isTitleCentered = true,
        canDismiss = true,
        dismissClicked = {
            dismissClicked?.let { it() }
        },
        content = {
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                CustomText("${context.getString(R.string.account_subjectinfo_data_id)}: ${item?.id?.toString(false) ?: context.getString(R.string.data_unknown)}")
                CustomText("${context.getString(R.string.account_subjectinfo_data_credit)}: ${item?.credit ?: context.getString(R.string.data_unknown)}")
                CustomText("${context.getString(R.string.account_subjectinfo_data_ishighquality)}: ${item?.isHighQuality ?: context.getString(R.string.data_unknown)}")
                CustomText("${context.getString(R.string.account_subjectinfo_data_scoreformula)}: ${item?.pointFormula ?: context.getString(R.string.data_unknown)}")
                // Subject study
                Spacer(modifier = Modifier.size(15.dp))
                ContentInBoxWithBorder(
                    title = "Schedule Study",
                    content = {
                        var schList = ""
                        item?.let {
                            schList = it.subjectStudy.scheduleList.joinToString(
                                separator = "; ",
                                transform = { item1 ->
                                    "${CustomDateUtil.dayOfWeekInString(item1.dayOfWeek + 1)},${item1.lesson.start}-${item1.lesson.end},${item1.room}"
                                }
                            )
                        }
                        CustomText("Day of week: $schList")
                        var schWeek = ""
                        item?.let {
                            schWeek = it.subjectStudy.weekList.joinToString(
                                separator = "; ",
                                transform = { item1 ->
                                    "${item1.start}-${item1.end}"
                                }
                            )
                        }
                        CustomText("Week range: $schWeek")
                    },
                )
                // Subject examination
                Spacer(modifier = Modifier.size(15.dp))
                ContentInBoxWithBorder(
                    title = "Schedule Examination",
                    content = {
                        if (item != null) {
                            CustomText(
                                "Group: ${item.subjectExam.group}" +
                                        if (item.subjectExam.isGlobal) " (global exam)" else ""
                            )
                            CustomText(
                                "Date: ${
                                    CustomDateUtil.dateUnixToString(
                                        item.subjectExam.date,
                                        "dd/MM/yyyy HH:mm",
                                        "GMT+7"
                                    )
                                }"
                            )
                            CustomText("Room: ${item.subjectExam.room}")

                        } else {
                            CustomText("Currently no examination schedule yet for this subject.")
                        }
                    }
                )
            }
        },
        actionButtons = {
            TextButton(
                onClick = {
                    onAddToFilterRequested?.let { callBack ->
                        item?.let {  item ->
                            callBack(
                                SubjectCode(
                                    studentYearId = item.id.studentYearId,
                                    classId = item.id.classId,
                                    subjectName = item.name
                                )
                            )
                        }
                    }
                },
                content = { Text(context.getString(R.string.account_subjectinfo_addtofilter)) },
                modifier = Modifier.padding(start = 8.dp),
            )
            TextButton(
                onClick = { dismissClicked?.let { it() } },
                content = { Text(context.getString(R.string.action_ok)) },
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    )
}

@Composable
private fun CustomText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 5.dp),
    )
}

@Composable
private fun ContentInBoxWithBorder(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 10.dp),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                content = content,
            )
        }
    }
}