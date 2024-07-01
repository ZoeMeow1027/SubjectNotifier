package io.zoemeow.dutschedule.ui.view.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.BaseActivity
import io.zoemeow.dutschedule.ui.component.base.SwitchWithTextInSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Settings_ParseNewsSubjectNotification(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color,
    activity: BaseActivity,
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            LargeTopAppBar(
                title = { Text(context.getString(R.string.settings_parsenewssubject_title)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack()
                        },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                context.getString(R.string.action_back),
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState()),
                content = {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 5.dp),
                        shape = RoundedCornerShape(30.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = activity.getBackgroundAlpha()
                        ),
                        content = {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp),
                                content = {
                                    Text(
                                        when (activity.getMainViewModel().appSettings.value.newsBackgroundParseNewsSubject) {
                                            true -> context.getString(R.string.settings_parsenewssubject_preview_titleenabled)
                                            false -> context.getString(R.string.settings_parsenewssubject_preview_titledisabled)
                                        },
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 5.dp)
                                    )
                                    Text(
                                        when (activity.getMainViewModel().appSettings.value.newsBackgroundParseNewsSubject) {
                                            true -> context.getString(R.string.settings_parsenewssubject_preview_descenabled)
                                            false -> context.getString(R.string.settings_parsenewssubject_preview_descdisabled)
                                        }
                                    )
                                }
                            )
                        }
                    )
                    SwitchWithTextInSurface(
                        text = context.getString(R.string.settings_parsenewssubject_choice_enable),
                        enabled = true,
                        checked = activity.getMainViewModel().appSettings.value.newsBackgroundParseNewsSubject,
                        onCheckedChange = {
                            activity.getMainViewModel().appSettings.value = activity.getMainViewModel().appSettings.value.clone(
                                newsBackgroundParseNewsSubject = !activity.getMainViewModel().appSettings.value.newsBackgroundParseNewsSubject
                            )
                            activity.getMainViewModel().saveSettings()
                        }
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_info_24),
                            contentDescription = context.getString(R.string.tooltip_info),
                            modifier = Modifier.size(24.dp),
                        )
                        Text(context.getString(R.string.settings_parsenewssubject_info))
                    }
                }
            )
        }
    )
}