package io.zoemeow.dutschedule.ui.view.main

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.MiscellaneousActivity
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.activity.SettingsActivity
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.NavBarItem
import io.zoemeow.dutschedule.ui.component.news.NewsPopup
import io.zoemeow.dutschedule.ui.view.account.Activity_Account
import io.zoemeow.dutschedule.ui.view.news.Activity_News
import io.zoemeow.dutschedule.utils.BackgroundImageUtils
import io.zoemeow.dutschedule.utils.openLink
import io.zoemeow.dutschedule.viewmodel.MainViewModel

@Composable
fun Activity_MainView_MainViewTabView(
    context: Context,
    mainViewModel: MainViewModel,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    onMessageReceived: (String, Boolean, String?, (() -> Unit)?) -> Unit, // (msg, forceDismissBefore, actionText, action)
    onMessageClear: () -> Unit
) {
    // Initialize for NavController for main activity
    val navController = rememberNavController()
    // Nav Route
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Notification Scaffold visible
    val isNotificationOpened = remember { mutableStateOf(false) }

    // News Detail Scaffold popup visible
    val isNewsDetailScaffoldOpened = remember { mutableStateOf(false) }
    val newsDetailType = remember { mutableStateOf<String?>(null) }
    val newsDetailData = remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = Color.Transparent,
        contentColor = appearanceState.contentColor,
        // https://stackoverflow.com/questions/75328833/compose-scaffold-unnecessary-systembar-padding-due-to-windowcompat-setdecorfi
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            NavigationBar(
                containerColor = appearanceState.containerColor,
                contentColor = appearanceState.contentColor,
                content = {
                    NavBarItem.getAll().forEach(
                        action = {
                            NavigationBarItem(
                                selected = currentRoute == it.route,
                                onClick = {
                                    navController.navigate(it.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    if (it.resourceIconId != null) {
                                        Icon(painter = painterResource(id = it.resourceIconId), context.getString(it.titleResId))
                                    } else if (it.icon != null) {
                                        Icon(imageVector = it.icon, context.getString(it.titleResId))
                                    } else {
                                        Icon(imageVector = Icons.Default.Info, context.getString(it.titleResId))
                                    }
                                },
                                label = { Text(context.getString(it.titleResId)) }
                            )
                        }
                    )
                }
            )
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = NavBarItem.dashboard.route,
                enterTransition = { fadeIn(animationSpec = tween(200)) },
                exitTransition = { fadeOut(animationSpec = tween(200)) },
                popEnterTransition = { fadeIn(animationSpec = tween(200)) },
                popExitTransition = { fadeOut(animationSpec = tween(200)) },
                modifier = Modifier.padding(it)
            ) {
                composable(NavBarItem.dashboard.route) {
                    Activity_MainView_Dashboard(
                        context = context,
                        appearanceState = appearanceState,
                        mainViewModel = mainViewModel,
                        notificationCount = mainViewModel.notificationHistory.size,
                        onNotificationPanelRequested = {
                            isNotificationOpened.value = true
                        },
                        onExternalLinkClicked = {
                            val intent = Intent(context, MiscellaneousActivity::class.java)
                            intent.action = MiscellaneousActivity.INTENT_EXTERNALLINKS
                            context.startActivity(intent)
                        },
                        onSettingsRequested = {
                            context.startActivity(Intent(context, SettingsActivity::class.java))
                        },
                        onNewsOpened = {
                            navController.navigate(NavBarItem.news.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onLoginRequested = {
                            navController.navigate(NavBarItem.account.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }

                composable(NavBarItem.news.route) {
                    Activity_News(
                        context = context,
                        appearanceState = appearanceState,
                        mainViewModel = mainViewModel,
                        searchRequested = {
                            val intent = Intent(context, NewsActivity::class.java)
                            intent.action = NewsActivity.INTENT_SEARCHACTIVITY
                            context.startActivity(intent)
                        },
                        onNewsClicked = { newsType, newsData ->
                            if (mainViewModel.appSettings.value.openNewsInModalBottomSheet) {
                                newsDetailType.value = newsType
                                newsDetailData.value = newsData
                                isNewsDetailScaffoldOpened.value = true
                            } else {
                                context.startActivity(
                                    Intent(
                                        context,
                                        NewsActivity::class.java
                                    ).also { intent ->
                                        intent.action = NewsActivity.INTENT_NEWSDETAILACTIVITY
                                        intent.putExtra("type", newsType)
                                        intent.putExtra("data", newsData)
                                    })
                            }
                        }
                    )
                }

                composable(NavBarItem.account.route) {
                    Activity_Account(
                        context = context,
                        appearanceState = appearanceState,
                        mainViewModel = mainViewModel,
                        onMessageReceived = { text, clearPrevious, actionText, action ->
                            onMessageReceived(text, clearPrevious, actionText, action)
                        }
                    )
                }
            }
        }
    )
    NotificationScaffold(
        context = context,
        itemList = mainViewModel.notificationHistory,
        snackBarHostState = snackBarHostState,
        isVisible = isNotificationOpened.value,
        appearanceState = appearanceState,
        backgroundImage = BackgroundImageUtils.backgroundImageCache.value,
        onDismiss = {
            onMessageClear()
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
            mainViewModel.notificationHistory.remove(item)
            mainViewModel.saveApplicationSettings(saveNotificationCache = true)
            onMessageReceived(
                context.getString(R.string.notification_removed),
                true,
                context.getString(R.string.action_undo)
            ) {
                mainViewModel.notificationHistory.add(item1)
                mainViewModel.saveApplicationSettings(saveNotificationCache = true)
            }
        },
        onClearAll = {
            onMessageReceived(
                context.getString(R.string.notification_removeall_confirm),
                true,
                context.getString(R.string.action_confirm),
            ) {
                mainViewModel.notificationHistory.clear()
                mainViewModel.saveApplicationSettings(saveNotificationCache = true)
                onMessageReceived(
                    context.getString(R.string.notification_removeall_removed),
                    true,
                    null,
                    null
                )
            }
        }
    )
    NewsPopup(
        isVisible = isNewsDetailScaffoldOpened.value,
        context = context,
        snackBarHostState = snackBarHostState,
        appearanceState = appearanceState,
        onMessageReceived = onMessageReceived,
        newsType = newsDetailType.value,
        newsData = newsDetailData.value,
        onDismiss = { isNewsDetailScaffoldOpened.value = false },
        onLinkClicked = { link ->
            context.openLink(
                url = link,
                customTab = mainViewModel.appSettings.value.openLinkInsideApp
            )
        },
    )
    BackHandler(isNotificationOpened.value || isNewsDetailScaffoldOpened.value) {
        if (isNewsDetailScaffoldOpened.value) {
            isNewsDetailScaffoldOpened.value = false
        }
        if (isNotificationOpened.value) {
            isNotificationOpened.value = false
        }
    }
}
