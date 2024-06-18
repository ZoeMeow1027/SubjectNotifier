package io.zoemeow.dutschedule.ui.view.account

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.AccountActivity
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.component.base.ButtonBase
import io.zoemeow.dutschedule.ui.component.base.CheckboxOption
import io.zoemeow.dutschedule.ui.component.base.OutlinedTextBox
import io.zoemeow.dutschedule.ui.component.base.SimpleCardItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountActivity.TrainingResult(
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
                        title = { Text(context.getString(R.string.account_trainingstatus_title)) },
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
                    if (getMainViewModel().accountSession.accountTrainingStatus.processState.value == ProcessState.Running) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                floatingActionButton = {
                    if (getMainViewModel().accountSession.accountTrainingStatus.processState.value != ProcessState.Running) {
                        FloatingActionButton(
                            onClick = {
                                getMainViewModel().accountSession.fetchAccountTrainingStatus(force = true)
                            },
                            content = {
                                Icon(Icons.Default.Refresh, context.getString(R.string.action_refresh))
                            }
                        )
                    }
                },
                actions = {},
            )
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
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        content = {
                            getMainViewModel().accountSession.accountTrainingStatus.data.value?.let {
                                SimpleCardItem(
                                    title = context.getString(R.string.account_trainingstatus_trainbox_title),
                                    isTitleCentered = true,
                                    padding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 7.dp),
                                    opacity = getControlBackgroundAlpha(),
                                    content = {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp)
                                                .padding(bottom = 10.dp),
                                            content = {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    contentAlignment = Alignment.Center,
                                                    content = {
                                                        val data1 = buildAnnotatedString {
                                                            val avg4Leading = it.trainingSummary?.avgTrainingScore4.toString() ?: context.getString(R.string.data_unknown)
                                                            val avg4Trailing = "/4"
                                                            val schYear = context.getString(
                                                                R.string.account_trainingstatus_trainbox_schyear,
                                                                it.trainingSummary?.schoolYearCurrent ?: context.getString(R.string.data_unknown)
                                                            )
                                                            append(avg4Leading)
                                                            addStyle(
                                                                style = SpanStyle(
                                                                    fontSize = 44.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                ),
                                                                start = 0,
                                                                end = avg4Leading.length
                                                            )
                                                            append(avg4Trailing)
                                                            addStyle(
                                                                style = SpanStyle(fontSize = 30.sp),
                                                                start = avg4Leading.length,
                                                                end = avg4Leading.length + avg4Trailing.length
                                                            )
                                                            // Just end line here
                                                            append("\n")
                                                            append(schYear)
                                                            addStyle(
                                                                style = MaterialTheme.typography.bodyLarge.toSpanStyle(),
                                                                start = avg4Leading.length + avg4Trailing.length + 1,
                                                                end = avg4Leading.length + avg4Trailing.length + 1 + schYear.length
                                                            )
                                                        }
                                                        ClickableText(
                                                            text = data1,
                                                            style = TextStyle(textAlign = TextAlign.Center),
                                                            onClick = { _ -> }
                                                        )
                                                    }
                                                )
                                                ButtonBase(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(top = 5.dp)
                                                        .padding(vertical = 5.dp),
                                                    horizontalArrangement = Arrangement.Center,
                                                    content = {
                                                        Text(context.getString(R.string.account_trainingstatus_trainbox_schbutton))
                                                    },
                                                    clicked = {
                                                        val intent = Intent(context, AccountActivity::class.java)
                                                        intent.action = AccountActivity.INTENT_ACCOUNTSUBJECTRESULT
                                                        context.startActivity(intent)
                                                    }
                                                )
                                            }
                                        )
                                    },
                                    clicked = {}
                                )
                                Spacer(modifier = Modifier.size(5.dp))
                                SimpleCardItem(
                                    title = context.getString(R.string.account_trainingstatus_graduatebox_title),
                                    isTitleCentered = true,
                                    padding = PaddingValues(horizontal = 10.dp),
                                    opacity = getControlBackgroundAlpha(),
                                    content = {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp)
                                                .padding(bottom = 10.dp),
                                            content = {
                                                Text(
                                                    if (it.graduateStatus?.hasQualifiedGraduate != true) {
                                                        context.getString(R.string.account_trainingstatus_graduatebox_notelegibletograduate)
                                                    } else {
                                                        context.getString(R.string.account_trainingstatus_graduatebox_elegibletograduate)
                                                    },
                                                    modifier = Modifier.padding(bottom = 5.dp)
                                                )
                                                SimpleCardItem(
                                                    title = context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult),
                                                    padding = PaddingValues(vertical = 10.dp),
                                                    isTitleCentered = true,
                                                    titleStyle = MaterialTheme.typography.titleMedium,
                                                    clicked = { },
                                                    content = {
                                                        CheckboxOption(
                                                            title = context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_havepe),
                                                            isChecked = it.graduateStatus?.hasSigGDTC == true
                                                        )
                                                        CheckboxOption(
                                                            title = context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_havende),
                                                            isChecked = it.graduateStatus?.hasSigGDQP == true
                                                        )
                                                        CheckboxOption(
                                                            title = context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_haveenglish),
                                                            isChecked = it.graduateStatus?.hasSigEnglish == true
                                                        )
                                                        CheckboxOption(
                                                            title = context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_haveit),
                                                            isChecked = it.graduateStatus?.hasSigIT == true
                                                        )
                                                    }
                                                )
                                                fun copyToClipboard(s: String? = null) {
                                                    if (!s.isNullOrEmpty()) {
                                                        clipboardManager.setText(AnnotatedString(s))
                                                        showSnackBar(
                                                            context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_copied),
                                                            clearPrevious = true
                                                        )
                                                    } else {
                                                        showSnackBar(
                                                            context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_nocopy),
                                                            clearPrevious = true
                                                        )
                                                    }
                                                }
                                                OutlinedTextBox(
                                                    title = context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_rewards),
                                                    value = it.graduateStatus?.rewardsInfo,
                                                    placeHolderIfNull = context.getString(R.string.data_nodata),
                                                    trailingIcon = {
                                                        IconButton(
                                                            onClick = {
                                                                copyToClipboard(it.graduateStatus?.rewardsInfo)
                                                            },
                                                            content = {
                                                                Icon(ImageVector.vectorResource(R.drawable.ic_baseline_content_copy_24), context.getString(R.string.action_copy))
                                                            }
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 5.dp)
                                                )
                                                OutlinedTextBox(
                                                    title = context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_discipline),
                                                    value = it.graduateStatus?.discipline,
                                                    placeHolderIfNull = context.getString(R.string.data_nodata),
                                                    trailingIcon = {
                                                        IconButton(
                                                            onClick = {
                                                                copyToClipboard(it.graduateStatus?.discipline)
                                                            },
                                                            content = {
                                                                Icon(ImageVector.vectorResource(R.drawable.ic_baseline_content_copy_24), context.getString(R.string.action_copy))
                                                            }
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 5.dp)
                                                )
                                                OutlinedTextBox(
                                                    title = context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_graduationthesisapproval),
                                                    value = it.graduateStatus?.eligibleGraduationThesisStatus,
                                                    placeHolderIfNull = context.getString(R.string.data_nodata),
                                                    trailingIcon = {
                                                        IconButton(
                                                            onClick = {
                                                                copyToClipboard(it.graduateStatus?.eligibleGraduationThesisStatus)
                                                            },
                                                            content = {
                                                                Icon(ImageVector.vectorResource(R.drawable.ic_baseline_content_copy_24), context.getString(R.string.action_copy))
                                                            }
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 5.dp)
                                                )
                                                OutlinedTextBox(
                                                    title = context.getString(R.string.account_trainingstatus_graduatebox_certandgraduateresult_graduationprocessapproval),
                                                    value = it.graduateStatus?.eligibleGraduationStatus,
                                                    placeHolderIfNull = context.getString(R.string.data_nodata),
                                                    trailingIcon = {
                                                        IconButton(
                                                            onClick = {
                                                                copyToClipboard(it.graduateStatus?.eligibleGraduationStatus)
                                                            },
                                                            content = {
                                                                Icon(ImageVector.vectorResource(R.drawable.ic_baseline_content_copy_24), context.getString(R.string.action_copy))
                                                            }
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(bottom = 5.dp)
                                                )
                                            }
                                        )
                                    },
                                    clicked = {}
                                )
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                        }
                    )
                }
            )
        }
    )

    val hasRun = remember { mutableStateOf(false) }
    run {
        if (!hasRun.value) {
            getMainViewModel().accountSession.fetchAccountTrainingStatus()
            hasRun.value = true
        }
    }
}