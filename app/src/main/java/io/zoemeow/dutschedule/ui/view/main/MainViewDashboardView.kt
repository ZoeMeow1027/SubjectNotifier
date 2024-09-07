package io.zoemeow.dutschedule.ui.view.main

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.MainActivity
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.utils.BackgroundImageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivity.MainViewDashboard(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    newsClicked: (() -> Unit)? = null,
    accountClicked: (() -> Unit)? = null,
    settingsClicked: (() -> Unit)? = null,
    externalLinkClicked: (() -> Unit)? = null
) {
    val isNotificationOpened = remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            TopAppBar(
                title = { Text(text = context.getString(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = BottomAppBarDefaults.containerColor.copy(
                    alpha = appearanceState.backgroundOpacity
                ),
                actions = {
                    BadgedBox(
                        // modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                        badge = {
                            // Badge { }
                        }
                    ) {
                        IconButton(
                            onClick = { newsClicked?.let { it() } }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_newspaper_24),
                                context.getString(R.string.news_title),
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                    BadgedBox(
                        // modifier = Modifier.padding(end = 15.dp),
                        badge = {
                            // Badge { }
                        }
                    ) {
                        IconButton(
                            onClick = { settingsClicked?.let { it() } }
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                context.getString(R.string.settings_title),
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                    BadgedBox(
                        // modifier = Modifier.padding(end = 15.dp),
                        badge = {
                            // Badge { }
                        }
                    ) {
                        IconButton(onClick = { externalLinkClicked?.let { it() } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_web_24),
                                "External links",
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                    BadgedBox(
                        // modifier = Modifier.padding(end = 15.dp),
                        badge = {
                            if (getMainViewModel().notificationHistory.isNotEmpty()) {
                                Badge {
                                    Text(getMainViewModel().notificationHistory.size.toString())
                                }
                            }
                        },
                        content = {
                            IconButton(
                                onClick = {
                                    // Open notification bottom sheet
                                    // Notification list requested
                                    if (!isNotificationOpened.value) {
                                        isNotificationOpened.value = true
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    context.getString(R.string.notification_panel_title),
                                    modifier = Modifier.size(27.dp),
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = {
                            Column(
                                modifier = Modifier.height(60.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center,
                                content = {
                                    Text(
                                        context.getString(R.string.account_title),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    getMainViewModel().accountSession.accountSession.processState.value.let {
                                        Text(
                                            when (it) {
                                                ProcessState.NotRunYet -> context.getString(R.string.main_account_notloggedin)
                                                ProcessState.Running -> context.getString(R.string.main_account_fetching)
                                                else -> getMainViewModel().accountSession.accountSession.data.value?.accountAuth?.username ?: "unknown"
                                            },
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            )
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (getMainViewModel().accountSession.accountSession.processState.value == ProcessState.Failed) {
                                        Badge { Text("!") }
                                    }
                                },
                                content = {
                                    when (getMainViewModel().accountSession.accountSession.processState.value) {
                                        ProcessState.Running -> CircularProgressIndicator(
                                            modifier = Modifier.size(26.dp),
                                            strokeWidth = 3.dp
                                        )
                                        else -> Icon(
                                            Icons.Outlined.AccountCircle,
                                            context.getString(R.string.account_title),
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                            )
                        },
                        onClick = { accountClicked?.let { it() } }
                    )
                }
            )
        },
        content = { paddingValues ->
            Activity_MainView_Dashboard_Body(
                modifier = Modifier.padding(paddingValues),
                context = context,
                appearanceState = appearanceState,
                mainViewModel = getMainViewModel(),
                onNewsOpened = {
                    context.startActivity(Intent(context, NewsActivity::class.java))
                }
            )
        }
    )
    NotificationScaffold(
        context = context,
        itemList = getMainViewModel().notificationHistory,
        snackBarHostState = snackBarHostState,
        isVisible = isNotificationOpened.value,
        appearanceState = appearanceState,
        backgroundImage = BackgroundImageUtils.backgroundImageCache.value,
        onDismiss = {
            clearSnackBar()
            isNotificationOpened.value = false
        },
        onClick = { item ->
            if (listOf(1, 2).contains(item.tag)) {
                Intent(context, NewsActivity::class.java).also {
                    it.action = NewsActivity.INTENT_NEWSDETAILACTIVITY
                    for (map1 in item.parameters) {
                        it.putExtra(map1.key, map1.value)
                    }
                    context.startActivity(it)
                }
            }
        },
        onClear = { item ->
            val item1 = item.clone()
            getMainViewModel().notificationHistory.remove(item)
            getMainViewModel().saveApplicationSettings(saveNotificationCache = true)
            showSnackBar(
                text = context.getString(R.string.notification_removed),
                actionText = context.getString(R.string.action_undo),
                action = {
                    getMainViewModel().notificationHistory.add(item1)
                    getMainViewModel().saveApplicationSettings(saveNotificationCache = true)
                }
            )
        },
        onClearAll = {
            showSnackBar(
                text = context.getString(R.string.notification_removeall_confirm),
                actionText = context.getString(R.string.action_confirm),
                action = {
                    getMainViewModel().notificationHistory.clear()
                    getMainViewModel().saveApplicationSettings(saveNotificationCache = true)
                    showSnackBar(
                        text = context.getString(R.string.notification_removeall_removed),
                        clearPrevious = true
                    )
                },
                clearPrevious = true
            )
        }
    )

    BackHandler(isNotificationOpened.value) {
        if (isNotificationOpened.value) {
            isNotificationOpened.value = false
        }
    }
}