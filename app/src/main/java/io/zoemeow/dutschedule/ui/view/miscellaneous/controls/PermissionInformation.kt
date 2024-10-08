package io.zoemeow.dutschedule.ui.view.miscellaneous.controls

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R

@Composable
    fun PermissionInformation(
    context: Context,
    title: String,
    permissionCode: String? = null,
    description: String,
    isRequired: Boolean = false,
    isGranted: Boolean = false,
    clicked: (() -> Unit)? = null,
    opacity: Float = 1f,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    Surface(
        modifier = Modifier.padding(padding)
            .fillMaxWidth()
            .wrapContentHeight()
            .border(
                border = BorderStroke(3.dp, if (isGranted) Color.Green else if (isRequired) Color.Red else Color(0xFFfc9003)),
                shape = RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE),
            )
            .clip(shape = RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE))
            .clickable { clicked?.let { it() } },
        color = MaterialTheme.colorScheme.onSecondary.copy(alpha = opacity),
        content = {
            Surface(
                modifier = Modifier.padding(15.dp),
                color = Color.Transparent,
                content = {
                    Column(
                        content = {
                            Text(
                                title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.W600,
                            )
                            permissionCode?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Surface(
                                modifier = Modifier.padding(vertical = 5.dp),
                                color = if (isGranted) Color.Green else Color.Red,
                                contentColor = if (isGranted) Color.Black else Color.White,
                                shape = RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    when (isGranted) {
                                        true -> {
                                            Icon(Icons.Default.Check, context.getString(R.string.activity_permissionrequest_status_allowed))
                                            Text(context.getString(R.string.activity_permissionrequest_status_allowed))
                                        }
                                        false -> {
                                            Icon(Icons.Default.Clear, context.getString(R.string.activity_permissionrequest_status_declined))
                                            Text(context.getString(R.string.activity_permissionrequest_status_declined))
                                        }
                                    }
                                }
                            }
                            Text(
                                description,
                                modifier = Modifier.padding(top = 10.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            )
        }
    )
}