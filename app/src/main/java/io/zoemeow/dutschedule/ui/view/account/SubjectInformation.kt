package io.zoemeow.dutschedule.ui.view.account

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.model.accounts.SubjectScheduleItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.AccountActivity
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.component.account.AccountSubjectMoreInformation
import io.zoemeow.dutschedule.ui.component.account.SubjectInformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountActivity.SubjectInformation(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color
) {
    val subjectScheduleItem: MutableState<SubjectScheduleItem?> = remember { mutableStateOf(null) }
    val subjectDetailVisible = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.account_subjectinfo_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            setResult(RESULT_CANCELED)
                            finish()
                        },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                context.getString(R.string.action_back),
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            if (getMainViewModel().accountSession.subjectSchedule.processState.value != ProcessState.Running) {
                FloatingActionButton(
                    onClick = {
                        getMainViewModel().accountSession.fetchSubjectSchedule(force = true)
                    },
                    content = {
                        Icon(Icons.Default.Refresh, context.getString(R.string.action_refresh))
                    }
                )
            }
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                content = {
                    if (getMainViewModel().accountSession.subjectSchedule.processState.value == ProcessState.Running) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp)
                            .padding(vertical = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Text(getMainViewModel().appSettings.value.currentSchoolYear.toString())
                        }
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp)
                            .padding(bottom = 7.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        content = {
                            getMainViewModel().accountSession.subjectSchedule.data.forEach { item ->
                                SubjectInformation(
                                    modifier = Modifier.padding(bottom = 7.dp),
                                    item = item,
                                    opacity = getControlBackgroundAlpha(),
                                    onClick = {
                                        subjectScheduleItem.value = item
                                        subjectDetailVisible.value = true
                                    }
                                )
                            }
                        }
                    )
                }
            )
        }
    )
    AccountSubjectMoreInformation(
        context = context,
        item = subjectScheduleItem.value,
        isVisible = subjectDetailVisible.value,
        dismissClicked = {
            subjectDetailVisible.value = false
        },
        onAddToFilterRequested = { item ->
            if (getMainViewModel().appSettings.value.newsBackgroundFilterList.any { it.isEquals(item) }) {
                showSnackBar(
                    text = context.getString(R.string.account_subjectinfo_filter_alreadyadded),
                    clearPrevious = true
                )
            } else {
                getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                    newsFilterList = getMainViewModel().appSettings.value.newsBackgroundFilterList.also {
                        it.add(item)
                    }
                )
                getMainViewModel().saveSettings()
                showSnackBar(
                    text = context.getString(
                        R.string.account_subjectinfo_filter_added,
                        item
                    ),
                    clearPrevious = true
                )
            }
        }
    )

    val hasRun = remember { mutableStateOf(false) }
    run {
        if (!hasRun.value) {
            getMainViewModel().accountSession.fetchSubjectSchedule()
            hasRun.value = true
        }
    }
}