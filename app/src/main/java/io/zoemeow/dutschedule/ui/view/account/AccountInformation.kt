package io.zoemeow.dutschedule.ui.view.account

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.component.base.OutlinedTextBox
import io.zoemeow.dutschedule.utils.openLink
import io.zoemeow.dutschedule.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Account_AccountInformation(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    onLinkClicked: ((String) -> Unit)? = null,
    onMessageReceived: (String, Boolean, String?, (() -> Unit)?) -> Unit, // (msg, forceDismissBefore, actionText, action)
    onBack: () -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

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
                        title = { Text(context.getString(R.string.account_accinfo_title)) },
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
                    if (mainViewModel.accountSession.accountInformation.processState.value == ProcessState.Running) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            )
        },
        bottomBar = {
            val pageInfoTooltipState = rememberTooltipState(
                isPersistent = true,
                initialIsVisible = true
            )
            BottomAppBar(
                floatingActionButton = {
                    if (mainViewModel.accountSession.accountInformation.processState.value != ProcessState.Running) {
                        FloatingActionButton(
                            onClick = {
                                mainViewModel.accountSession.fetchAccountInformation(force = true)
                            },
                            content = {
                                Icon(
                                    Icons.Default.Refresh,
                                    context.getString(R.string.action_refresh)
                                )
                            }
                        )
                    }
                },
                actions = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            RichTooltip(
                                title = { Text(context.getString(R.string.account_accinfo_editinfo)) },
                                text = {
                                    Text(context.getString(R.string.account_accinfo_description))
                                },
                                action = {
                                    TextButton(
                                        onClick = { onLinkClicked?.let { it("http://sv.dut.udn.vn") } },
                                        content = {
                                            Text(context.getString(R.string.account_accinfo_action_openlink))
                                        }
                                    )
                                }
                            )
                        },
                        state = pageInfoTooltipState,
                        enableUserInput = false,
                        content = {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        if (pageInfoTooltipState.isVisible) {
                                            pageInfoTooltipState.dismiss()
                                        }
                                        pageInfoTooltipState.show()
                                    }
                                },
                                content = {
                                    Row {
                                        Icon(Icons.Default.Info, context.getString(R.string.tooltip_info))
                                        Spacer(modifier = Modifier.size(5.dp))
                                        Text(context.getString(R.string.account_accinfo_editinfo))
                                    }
                                }
                            )
                        }
                    )
                },
                containerColor = Color.Transparent
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                content = {
                    fun copyToClipboard(s: String? = null) {
                        if (!s.isNullOrEmpty()) {
                            clipboardManager.setText(AnnotatedString(s))
                            onMessageReceived(context.getString(R.string.account_accinfo_snackbar_copied), true, null, null)
                        } else {
                            onMessageReceived(context.getString(R.string.account_accinfo_snackbar_nocopy), true, null, null)
                        }
                    }
                    mainViewModel.accountSession.accountInformation.data.value?.let { data ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp)
                                .padding(bottom = 7.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            mapOf(
                                context.getString(R.string.account_accinfo_item_name) to (data.name
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_dateofbirth) to (data.dateOfBirth
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_placeofbirth) to (data.birthPlace
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_gender) to (data.gender
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_nationalcardid) to (data.nationalIdCard
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_nationalcardplaceanddate) to ("${
                                    data.nationalIdCardIssuePlace ?: context.getString(
                                        R.string.data_unknown
                                    )
                                } on ${data.nationalIdCardIssueDate ?: context.getString(R.string.data_unknown)}"),
                                context.getString(R.string.account_accinfo_item_citizencardid) to (data.citizenIdCard
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_citizencarddate) to (data.citizenIdCardIssueDate
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_bankcardid) to ("${
                                    data.accountBankId ?: context.getString(
                                        R.string.data_unknown
                                    )
                                } (${data.accountBankName ?: context.getString(R.string.data_unknown)})"),
                                context.getString(R.string.account_accinfo_item_personalemail) to (data.personalEmail
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_phonenumber) to (data.phoneNumber
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_class) to (data.schoolClass
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_specialization) to (data.specialization
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_trainingprogramplan) to (data.trainingProgramPlan
                                    ?: context.getString(R.string.data_unknown)),
                                context.getString(R.string.account_accinfo_item_schoolemail) to (data.schoolEmail
                                    ?: context.getString(R.string.data_unknown)),
                            ).also { data ->
                                data.keys.forEach { title ->
                                    OutlinedTextBox(
                                        title = title,
                                        value = data[title]
                                            ?: context.getString(R.string.data_unknown),
                                        trailingIcon = {
                                            IconButton(
                                                onClick = {
                                                    copyToClipboard(data[title])
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
                    } ?: Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp)
                            .padding(bottom = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            context.getString(R.string.account_accinfo_noinfo),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            )
        }
    )
}