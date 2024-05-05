package io.zoemeow.dutschedule.ui.view.account

import android.accounts.Account
import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.AccountActivity
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.account.AccountAuth
import io.zoemeow.dutschedule.ui.component.account.AccountInfoBanner
import io.zoemeow.dutschedule.ui.component.account.LoginBox
import io.zoemeow.dutschedule.ui.component.account.LogoutDialog
import io.zoemeow.dutschedule.ui.component.base.ButtonBase
import io.zoemeow.dutschedule.utils.openLink
import io.zoemeow.dutschedule.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun AccountActivity.MainView(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color
) {
    AccountMainView(
        context = context,
        snackBarHostState = snackBarHostState,
        containerColor = containerColor,
        contentColor = contentColor,
        componentBackgroundAlpha = getControlBackgroundAlpha(),
        mainViewModel = getMainViewModel(),
        onShowSnackBar = { text, clearPrevious, actionText, action ->
            showSnackBar(text = text, clearPrevious = clearPrevious, actionText = actionText, action = action)
        },
        onBack = {
            setResult(RESULT_CANCELED)
            finish()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountMainView(
    context: Context,
    snackBarHostState: SnackbarHostState? = null,
    containerColor: Color,
    contentColor: Color,
    componentBackgroundAlpha: Float = 1f,
    mainViewModel: MainViewModel,
    onShowSnackBar: ((String, Boolean, String?, (() -> Unit)?) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val loginDialogVisible = remember { mutableStateOf(false) }
    val loginDialogEnabled = remember { mutableStateOf(true) }
    val logoutDialogVisible = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { snackBarHostState?.let { SnackbarHost(hostState = it) } },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.account_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    if (onBack != null) {
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
                }
            )
        },
        content = {
            mainViewModel.accountSession.accountSession.processState.value.let { state ->
                LoginBox(
                    context = context,
                    modifier = Modifier
                        .padding(it)
                        .padding(horizontal = 15.dp),
                    isVisible = state != ProcessState.Successful,
                    isProcessing = state == ProcessState.Running,
                    isControlEnabled = state != ProcessState.Running,
                    isLoggedInBefore = state == ProcessState.Failed,
                    clearOnInvisible = true,
                    opacity = componentBackgroundAlpha,
                    onForgotPass = {
                        context.openLink(
                            url = GlobalVariables.LINK_FORGOT_PASSWORD,
                            customTab = mainViewModel.appSettings.value.openLinkInsideApp
                        )
                    },
                    onClearLogin = { },
                    onSubmit = { username, password, rememberLogin ->
                        run {
                            CoroutineScope(Dispatchers.IO).launch {
                                loginDialogEnabled.value = false
                                onShowSnackBar?.let { it(
                                    context.getString(R.string.account_login_loggingin),
                                    true, null, null
                                ) }
                            }
                            mainViewModel.accountSession.login(
                                accountAuth = AccountAuth(
                                    username = username,
                                    password = password,
                                    rememberLogin = rememberLogin
                                ),
                                onCompleted = {loggedIn ->
                                    when (loggedIn) {
                                        true -> {
                                            loginDialogEnabled.value = true
                                            loginDialogVisible.value = false
                                            mainViewModel.accountSession.reLogin()
                                            onShowSnackBar?.let { it(
                                                context.getString(R.string.account_login_successful),
                                                true, null, null
                                            ) }
                                        }
                                        false -> {
                                            loginDialogEnabled.value = true
                                            onShowSnackBar?.let { it(
                                                context.getString(R.string.account_login_failed),
                                                true, null, null
                                            ) }
                                        }
                                    }
                                }
                            )
                        }
                    }
                )
                if (state == ProcessState.Successful) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .verticalScroll(rememberScrollState()),
                        content = {
                            mainViewModel.accountSession.accountInformation.let { accInfo ->
                                AccountInfoBanner(
                                    context = context,
                                    opacity = componentBackgroundAlpha,
                                    padding = PaddingValues(10.dp),
                                    isLoading = accInfo.processState.value == ProcessState.Running,
                                    username = accInfo.data.value?.studentId,
                                    schoolClass = accInfo.data.value?.schoolClass,
                                    trainingProgramPlan = accInfo.data.value?.trainingProgramPlan
                                )
                            }
                            ButtonBase(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                modifierInside = Modifier.padding(vertical = 7.dp),
                                content = { Text(context.getString(R.string.account_dashboard_button_subjectinfo)) },
                                horizontalArrangement = Arrangement.Start,
                                opacity = componentBackgroundAlpha,
                                clicked = {
                                    val intent = Intent(context, AccountActivity::class.java)
                                    intent.action = AccountActivity.INTENT_SUBJECTINFORMATION
                                    context.startActivity(intent)
                                }
                            )
                            ButtonBase(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                modifierInside = Modifier.padding(vertical = 7.dp),
                                content = { Text(context.getString(R.string.account_dashboard_button_subjectfee)) },
                                horizontalArrangement = Arrangement.Start,
                                opacity = componentBackgroundAlpha,
                                clicked = {
                                    val intent = Intent(context, AccountActivity::class.java)
                                    intent.action = AccountActivity.INTENT_SUBJECTFEE
                                    context.startActivity(intent)
                                }
                            )
                            ButtonBase(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                modifierInside = Modifier.padding(vertical = 7.dp),
                                content = { Text(context.getString(R.string.account_dashboard_button_accountinfo)) },
                                horizontalArrangement = Arrangement.Start,
                                opacity = componentBackgroundAlpha,
                                clicked = {
                                    val intent = Intent(context, AccountActivity::class.java)
                                    intent.action = AccountActivity.INTENT_ACCOUNTINFORMATION
                                    context.startActivity(intent)
                                }
                            )
                            ButtonBase(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                modifierInside = Modifier.padding(vertical = 7.dp),
                                content = { Text(context.getString(R.string.account_dashboard_button_accounttrainstats)) },
                                horizontalArrangement = Arrangement.Start,
                                opacity = componentBackgroundAlpha,
                                clicked = {
                                    val intent = Intent(context, AccountActivity::class.java)
                                    intent.action = AccountActivity.INTENT_ACCOUNTTRAININGSTATUS
                                    context.startActivity(intent)
                                }
                            )
                            ButtonBase(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                modifierInside = Modifier.padding(vertical = 7.dp),
                                content = { Text(context.getString(R.string.account_dashboard_button_logout)) },
                                horizontalArrangement = Arrangement.Start,
                                opacity = componentBackgroundAlpha,
                                clicked = {
                                    logoutDialogVisible.value = true
                                }
                            )
                        }
                    )
                }
            }
        }
    )
    LogoutDialog(
        context = context,
        isVisible = logoutDialogVisible.value,
        canDismiss = true,
        logoutClicked = {
            run {
                logoutDialogVisible.value = false
                mainViewModel.accountSession.logout(
                    onCompleted = {
                        onShowSnackBar?.let { it(
                            context.getString(R.string.account_logout_loggedout),
                            true, null, null
                        ) }
                    }
                )
            }
        },
        dismissClicked = {
            logoutDialogVisible.value = false
        }
    )
    BackHandler(
        enabled = loginDialogVisible.value || logoutDialogVisible.value,
        onBack = {
            if (loginDialogVisible.value) {
                loginDialogVisible.value = false
            }
            if (logoutDialogVisible.value) {
                logoutDialogVisible.value = false
            }
        }
    )
}