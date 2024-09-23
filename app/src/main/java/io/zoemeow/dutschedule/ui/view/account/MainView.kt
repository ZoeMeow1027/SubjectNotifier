package io.zoemeow.dutschedule.ui.view.account

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
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.account.AccountAuth
import io.zoemeow.dutschedule.ui.view.account.controls.AccountInfoBanner
import io.zoemeow.dutschedule.ui.view.account.controls.LoginBox
import io.zoemeow.dutschedule.ui.view.account.controls.LogoutDialog
import io.zoemeow.dutschedule.ui.components.ButtonBase
import io.zoemeow.dutschedule.utils.ExtensionUtils.Companion.openLink
import io.zoemeow.dutschedule.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Account(
    context: Context,
    snackBarHostState: SnackbarHostState? = null,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    onMessageReceived: ((String, Boolean, String?, (() -> Unit)?) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val loginDialogVisible = remember { mutableStateOf(false) }
    val loginDialogEnabled = remember { mutableStateOf(true) }
    val logoutDialogVisible = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        snackbarHost = { snackBarHostState?.let { SnackbarHost(hostState = it) } },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.account_title)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
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
                // If we have account auth in storage, directly show in main view
                // Else go to login view.
                if (mainViewModel.accountSession.accountSession.data.value?.accountAuth != null && mainViewModel.accountSession.accountSession.data.value?.accountAuth?.isValidLogin() == true) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .verticalScroll(rememberScrollState()),
                        content = {
                            mainViewModel.accountSession.accountInformation.let { accInfo ->
                                AccountInfoBanner(
                                    context = context,
                                    opacity = appearanceState.componentOpacity,
                                    padding = PaddingValues(10.dp),
                                    isLoading = mainViewModel.accountSession.accountSession.processState.value == ProcessState.Running || accInfo.processState.value == ProcessState.Running,
                                    isFailed = mainViewModel.accountSession.accountSession.processState.value == ProcessState.Failed,
                                    name = accInfo.data.value?.name,
                                    username = accInfo.data.value?.studentId,
                                    schoolClass = accInfo.data.value?.schoolClass,
                                    specialization = accInfo.data.value?.specialization,
                                    reLoginRequested = {
                                        mainViewModel.accountSession.reLogin()
                                    }
                                )
                            }
                            ButtonBase(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                modifierInside = Modifier.padding(vertical = 7.dp),
                                isEnabled = mainViewModel.accountSession.accountSession.processState.value != ProcessState.Running,
                                content = { Text(context.getString(R.string.account_dashboard_button_subjectinfo)) },
                                horizontalArrangement = Arrangement.Start,
                                opacity = appearanceState.componentOpacity,
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
                                isEnabled = mainViewModel.accountSession.accountSession.processState.value != ProcessState.Running,
                                content = { Text(context.getString(R.string.account_dashboard_button_subjectfee)) },
                                horizontalArrangement = Arrangement.Start,
                                opacity = appearanceState.componentOpacity,
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
                                isEnabled = mainViewModel.accountSession.accountSession.processState.value != ProcessState.Running,
                                content = { Text(context.getString(R.string.account_dashboard_button_accountinfo)) },
                                horizontalArrangement = Arrangement.Start,
                                opacity = appearanceState.componentOpacity,
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
                                isEnabled = mainViewModel.accountSession.accountSession.processState.value != ProcessState.Running,
                                content = { Text(context.getString(R.string.account_dashboard_button_accounttrainstats)) },
                                horizontalArrangement = Arrangement.Start,
                                opacity = appearanceState.componentOpacity,
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
                                opacity = appearanceState.componentOpacity,
                                clicked = {
                                    logoutDialogVisible.value = true
                                }
                            )
                        }
                    )
                } else {
                    LoginBox(
                        context = context,
                        modifier = Modifier
                            .padding(it)
                            .padding(horizontal = 15.dp),
                        isVisible = true,
                        isProcessing = state == ProcessState.Running,
                        isControlEnabled = state != ProcessState.Running,
                        isLoggedInBefore = state == ProcessState.Failed,
                        clearOnInvisible = true,
                        opacity = appearanceState.componentOpacity,
                        onForgotPass = {
                            context.openLink(
                                url = GlobalVariables.LINK_FORGOT_PASSWORD,
                                customTab = mainViewModel.appSettings.value.openLinkInsideApp
                            )
                        },
                        onClearLogin = {
                            // Just logout and this will clear all this session.
                            mainViewModel.accountSession.logout()
                        },
                        onSubmit = { username, password, rememberLogin ->
                            run {
                                CoroutineScope(Dispatchers.IO).launch {
                                    loginDialogEnabled.value = false
                                    onMessageReceived?.let { it(
                                        context.getString(R.string.account_login_loggingin),
                                        true, null, null
                                    ) }
                                }
                                // If previous login has failed, second chance to login
                                if (state == ProcessState.Failed) {
                                    mainViewModel.accountSession.login(
                                        onCompleted = { loggedIn ->
                                            when (loggedIn) {
                                                true -> {
                                                    loginDialogEnabled.value = true
                                                    loginDialogVisible.value = false
                                                    mainViewModel.accountSession.reLogin()
                                                    onMessageReceived?.let { it(
                                                        context.getString(R.string.account_login_successful),
                                                        true, null, null
                                                    ) }
                                                }
                                                false -> {
                                                    loginDialogEnabled.value = true
                                                    onMessageReceived?.let { it(
                                                        context.getString(R.string.account_login_failed),
                                                        true, null, null
                                                    ) }
                                                }
                                            }
                                        }
                                    )
                                }
                                // New login
                                else {
                                    mainViewModel.accountSession.login(
                                        accountAuth = AccountAuth(
                                            username = username,
                                            password = password,
                                            rememberLogin = rememberLogin
                                        ),
                                        onCompleted = { loggedIn ->
                                            when (loggedIn) {
                                                true -> {
                                                    loginDialogEnabled.value = true
                                                    loginDialogVisible.value = false
                                                    mainViewModel.accountSession.reLogin()
                                                    onMessageReceived?.let { it(
                                                        context.getString(R.string.account_login_successful),
                                                        true, null, null
                                                    ) }
                                                }
                                                false -> {
                                                    loginDialogEnabled.value = true
                                                    onMessageReceived?.let { it(
                                                        context.getString(R.string.account_login_failed),
                                                        true, null, null
                                                    ) }
                                                }
                                            }
                                        }
                                    )
                                }
                            }
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
                        onMessageReceived?.let { it(
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