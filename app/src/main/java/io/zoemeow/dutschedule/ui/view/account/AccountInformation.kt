package io.zoemeow.dutschedule.ui.view.account

import android.app.Activity.RESULT_OK
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.AccountActivity
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.component.base.OutlinedTextBox

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountActivity.AccountInformation(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            Box(
                contentAlignment = Alignment.BottomCenter,
                content = {
                    TopAppBar(
                        title = { Text(context.getString(R.string.account_accinfo_title)) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    setResult(RESULT_OK)
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
                    if (getMainViewModel().accountSession.accountInformation.processState.value == ProcessState.Running) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            )
        },
        floatingActionButton = {
            if (getMainViewModel().accountSession.accountInformation.processState.value != ProcessState.Running) {
                FloatingActionButton(
                    onClick = {
                        getMainViewModel().accountSession.fetchAccountInformation(force = true)
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
                            .fillMaxSize()
                            .padding(horizontal = 15.dp)
                            .padding(bottom = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top,
                        content = {
                            getMainViewModel().accountSession.accountInformation.data.value?.let { data ->
                                val mapPersonalInfo = mapOf(
                                    context.getString(R.string.account_accinfo_item_name) to (data.name ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_dateofbirth) to (data.dateOfBirth ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_placeofbirth) to (data.birthPlace ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_gender) to (data.gender ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_nationalcardid) to (data.nationalIdCard ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_nationalcardplaceanddate) to ("${data.nationalIdCardIssuePlace ?: context.getString(R.string.data_unknown)} on ${data.nationalIdCardIssueDate ?: context.getString(R.string.data_unknown)}"),
                                    context.getString(R.string.account_accinfo_item_citizencardid) to (data.citizenIdCard ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_citizencarddate) to (data.citizenIdCardIssueDate ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_bankcardid) to ("${data.accountBankId ?: context.getString(R.string.data_unknown)} (${data.accountBankName ?: context.getString(R.string.data_unknown)})"),
                                    context.getString(R.string.account_accinfo_item_personalemail) to (data.personalEmail ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_phonenumber) to (data.phoneNumber ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_class) to (data.schoolClass ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_specialization) to (data.specialization ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_trainingprogramplan) to (data.trainingProgramPlan ?: context.getString(R.string.data_unknown)),
                                    context.getString(R.string.account_accinfo_item_schoolemail) to (data.schoolEmail ?: context.getString(R.string.data_unknown)),
                                )
                                Text(context.getString(R.string.account_accinfo_description))
                                Spacer(modifier = Modifier.size(5.dp))
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    mapPersonalInfo.keys.forEach { title ->
                                        OutlinedTextBox(
                                            title = title,
                                            value = mapPersonalInfo[title] ?: context.getString(R.string.data_unknown),
                                            trailingIcon = {
                                                IconButton(
                                                    onClick = {
                                                        clipboardManager.setText(AnnotatedString(mapPersonalInfo[title] ?: ""))
                                                        showSnackBar(
                                                            context.getString(R.string.account_accinfo_snackbar_copied),
                                                            clearPrevious = true
                                                        )
                                                    },
                                                    content = {
                                                        Icon(
                                                            ImageVector.vectorResource(R.drawable.ic_baseline_content_copy_24),
                                                            context.getString(R.string.action_copy)
                                                        )
                                                    }
                                                )
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            )
        }
    )
}