package io.zoemeow.dutschedule.activity

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.ui.view.account.Activity_Account
import io.zoemeow.dutschedule.ui.view.account.Activity_Account_AccountInformation
import io.zoemeow.dutschedule.ui.view.account.Activity_Account_SubjectInformation
import io.zoemeow.dutschedule.ui.view.account.Activity_Account_SubjectFee
import io.zoemeow.dutschedule.ui.view.account.Activity_Account_TrainingResult
import io.zoemeow.dutschedule.ui.view.account.Activity_Account_TrainingSubjectResult

@AndroidEntryPoint
class AccountActivity: BaseActivity() {
    companion object {
        const val INTENT_SUBJECTINFORMATION = "subject_information"
        const val INTENT_SUBJECTFEE = "subject_fee"
        const val INTENT_ACCOUNTINFORMATION = "acc_info"
        const val INTENT_ACCOUNTTRAININGSTATUS = "acc_training_result"
        const val INTENT_ACCOUNTSUBJECTRESULT = "acc_training_result_subjectresult"
    }

    @Composable
    override fun OnPreloadOnce() {

    }

    @Composable
    override fun OnMainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        appearanceState: AppearanceState
    ) {
        when (intent.action) {
            INTENT_SUBJECTINFORMATION -> {
                Activity_Account_SubjectInformation(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    mainViewModel = getMainViewModel(),
                    onMessageReceived = { msg, forceDismissBefore, actionText, action ->
                        showSnackBar(
                            text = msg,
                            clearPrevious = forceDismissBefore,
                            actionText = actionText,
                            action = action
                        )
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
            INTENT_SUBJECTFEE -> {
                Activity_Account_SubjectFee(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    mainViewModel = getMainViewModel(),
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
            INTENT_ACCOUNTINFORMATION -> {
                Activity_Account_AccountInformation(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    mainViewModel = getMainViewModel(),
                    onMessageReceived = { msg, forceDismissBefore, actionText, action ->
                        showSnackBar(
                            text = msg,
                            clearPrevious = forceDismissBefore,
                            actionText = actionText,
                            action = action
                        )
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
            INTENT_ACCOUNTTRAININGSTATUS -> {
                Activity_Account_TrainingResult(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    mainViewModel = getMainViewModel(),
                    onMessageReceived = { msg, forceDismissBefore, actionText, action ->
                        showSnackBar(
                            text = msg,
                            clearPrevious = forceDismissBefore,
                            actionText = actionText,
                            action = action
                        )
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
            INTENT_ACCOUNTSUBJECTRESULT -> {
                Activity_Account_TrainingSubjectResult(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    mainViewModel = getMainViewModel(),
                    onMessageReceived = { msg, forceDismissBefore, actionText, action ->
                        showSnackBar(
                            text = msg,
                            clearPrevious = forceDismissBefore,
                            actionText = actionText,
                            action = action
                        )
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
            else -> {
                Activity_Account(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    mainViewModel = getMainViewModel(),
                    onMessageReceived = { text, clearPrevious, actionText, action ->
                        showSnackBar(text = text, clearPrevious = clearPrevious, actionText = actionText, action = action)
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
}