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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.AccountInformation.SubjectFee
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.ui.components.Tag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AccountSubjectFeeInformation(
    modifier: Modifier = Modifier,
    item: SubjectFee,
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
                    FlowRow(
                        verticalArrangement = Arrangement.Center,
                        horizontalArrangement = Arrangement.Start,
                        content = {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleLarge,
                            )
                            Spacer(modifier = Modifier.size(5.dp))
                            if (item.debt) {
                                Tag(
                                    text = context.getString(R.string.account_subjectfee_status_notdoneyet),
                                    backColor = Color.Red,
                                    textColor = Color.White
                                )
                            } else {
                                Tag(
                                    text = context.getString(R.string.account_subjectfee_status_completed),
                                    backColor = Color.Green
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.size(5.dp))
                    Text(
                        text = if (item.credit == 1) {
                            context.getString(
                                R.string.account_subjectfee_summary_1credit,
                                item.credit,
                                item.price,
                                "VND"
                            )
                        } else {
                            context.getString(
                                R.string.account_subjectfee_summary_manycredit,
                                item.credit,
                                item.price,
                                "VND"
                            )
                        }
                    )
                }
            )
        }
    )
}