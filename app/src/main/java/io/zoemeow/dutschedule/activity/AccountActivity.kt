package io.zoemeow.dutschedule.activity

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutschedule.ui.view.account.AccountInformation
import io.zoemeow.dutschedule.ui.view.account.MainView
import io.zoemeow.dutschedule.ui.view.account.SubjectFee
import io.zoemeow.dutschedule.ui.view.account.SubjectInformation
import io.zoemeow.dutschedule.ui.view.account.TrainingResult
import io.zoemeow.dutschedule.ui.view.account.TrainingSubjectResult

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
        containerColor: Color,
        contentColor: Color
    ) {
        when (intent.action) {
            INTENT_SUBJECTINFORMATION -> {
                SubjectInformation(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            INTENT_SUBJECTFEE -> {
                SubjectFee(
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            INTENT_ACCOUNTINFORMATION -> {
                AccountInformation(
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            INTENT_ACCOUNTTRAININGSTATUS -> {
                TrainingResult(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            INTENT_ACCOUNTSUBJECTRESULT -> {
                TrainingSubjectResult(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
            else -> {
                MainView(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
            }
        }
    }
}