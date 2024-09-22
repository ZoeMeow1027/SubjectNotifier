package io.zoemeow.dutschedule.ui.view.account

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.view.account.controls.AccountSubjectFeeInformation
import io.zoemeow.dutschedule.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Account_SubjectFee(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            Box(
                contentAlignment = Alignment.BottomCenter,
                content = {
                    TopAppBar(
                        title = { Text(context.getString(R.string.account_subjectfee_title)) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
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
                        }
                    )
                    if (mainViewModel.accountSession.subjectFee.processState.value == ProcessState.Running) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            )
        },
        floatingActionButton = {
            if (mainViewModel.accountSession.subjectFee.processState.value != ProcessState.Running) {
                FloatingActionButton(
                    onClick = {
                        mainViewModel.accountSession.fetchSubjectFee(force = true)
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .padding(vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            Text(mainViewModel.appSettings.value.currentSchoolYear.composeToString())
                        }
                    )
                    if (mainViewModel.accountSession.subjectFee.data.size == 0 && mainViewModel.accountSession.subjectFee.processState.value != ProcessState.Running) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                                .padding(horizontal = 15.dp)
                                .padding(vertical = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            content = {
                                Text(
                                    context.getString(R.string.account_subjectfee_summary_nosubjects),
                                    textAlign = TextAlign.Center
                                )
                            }
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp)
                                .padding(bottom = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            content = {
                                item {
                                    if (mainViewModel.accountSession.subjectFee.data.size > 0) {
                                        Text(context.getString(
                                            R.string.account_subjectfee_summary_main,
                                            mainViewModel.accountSession.subjectFee.data.sumOf { it.credit },
                                            mainViewModel.accountSession.subjectFee.data.sumOf { it.price }
                                        ))
                                    }
                                }
                                items(mainViewModel.accountSession.subjectFee.data) { item ->
                                    AccountSubjectFeeInformation(
                                        modifier = Modifier.padding(bottom = 10.dp),
                                        item = item,
                                        opacity = appearanceState.componentOpacity,
                                        onClick = { }
                                    )
                                }
                            }
                        )
                    }
                }
            )
        }
    )

    val hasRun = remember { mutableStateOf(false) }
    run {
        if (!hasRun.value) {
            mainViewModel.accountSession.fetchSubjectFee()
            hasRun.value = true
        }
    }
}