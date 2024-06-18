package io.zoemeow.dutschedule.ui.component.account

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.utils.capitalized

@Composable
fun AccountInfoBanner(
    context: Context,
    padding: PaddingValues,
    isLoading: Boolean = false,
    isFailed: Boolean = false,
    name: String? = null,
    username: String? = null,
    schoolClass: String? = null,
    specialization: String? = null,
    opacity: Float = 1.0f,
    reLoginRequested: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .padding(padding)
            .clip(RoundedCornerShape(7.dp)),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = opacity),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                content = {
                    if (isLoading) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            content = {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(vertical = 30.dp)
                                )
                            }
                        )
                    }
                    else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Top,
                            content = {
                                Text(
                                    text = context.getString(R.string.account_dashboard_banner_title),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(bottom = 10.dp),
                                )
                            }
                        )
                        if (isFailed) {
                            Surface(
                                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
                                shape = RoundedCornerShape(7.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = when {
                                    opacity + 0.15f >= 1f -> 1f
                                    else -> opacity + 0.1f
                                }),
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 15.dp)
                                            .padding(top = 10.dp, bottom = 3.dp)
                                    ) {
                                        Text(context.getString(R.string.account_dashboard_banner_loginfailed))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            TextButton(onClick = { reLoginRequested?.let { it() } }) {
                                                Text(context.getString(R.string.account_dashboard_banner_relogin))
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        Icon(
                            Icons.Outlined.AccountCircle,
                            "Account Icon",
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            name?.capitalized() ?: "(unknown name)",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 7.dp)
                        )
                        Text(
                            "${username ?: "(unknown student ID)"} - ${schoolClass ?: "(unknown class)"}",
                            fontSize = 17.sp,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                        Text(
                            specialization ?: "(unknown specialization)",
                            fontSize = 17.sp,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
            )
        },
    )
}