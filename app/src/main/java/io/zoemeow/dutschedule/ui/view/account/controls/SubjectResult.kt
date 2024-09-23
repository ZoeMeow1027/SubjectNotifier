package io.zoemeow.dutschedule.ui.view.account.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.AccountInformation
import io.dutwrapper.dutwrapper.AccountInformation.SubjectCode
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.ui.components.Tag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubjectResult(
    subjectResult: AccountInformation.SubjectResult,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    opacity: Float = 1f
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier.clip(RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE))
            .run {
                if (onClick != null) return@run this.clickable { onClick() }
                else return@run this
            },
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = opacity),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "${subjectResult.index} - ${subjectResult.name}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    if (subjectResult.isReStudy) {
                        Tag(
                            text = context.getString(R.string.account_trainingstatus_subjectresult_restudiedsubject),
                            backColor = Color.Yellow
                        )
                    }
                }
                Spacer(modifier = Modifier.size(3.dp))
                Text(
                    "${subjectResult.resultT10} / ${subjectResult.resultT4} / ${subjectResult.resultByChar}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

@Preview
@Composable
private fun SubjectResultPreview() {
    SubjectResult(
        AccountInformation.SubjectResult(
            64, "2023-2024", false, SubjectCode("1023623.2220.19.14"), "Toán ứng dụng Công nghệ thông tin",
            2.0, "Point formula", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, "F", true
        )
    )
}