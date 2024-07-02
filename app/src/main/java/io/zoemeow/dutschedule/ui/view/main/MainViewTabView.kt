package io.zoemeow.dutschedule.ui.view.main

import android.content.Context
import android.content.Intent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.MainActivity
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.NavBarItem
import io.zoemeow.dutschedule.ui.view.account.Activity_Account
import io.zoemeow.dutschedule.ui.view.news.Activity_News
import io.zoemeow.dutschedule.ui.view.settings.Activity_Settings

@Composable
fun MainActivity.MainViewTabbed(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState
) {
    // Initialize for NavController for main activity
    val navController = rememberNavController()
    // Nav Route
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

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
                        mainViewModel = getMainViewModel(),
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
                        mainViewModel = getMainViewModel(),
                        searchRequested = {
                            val intent = Intent(context, NewsActivity::class.java)
                            intent.action = NewsActivity.INTENT_SEARCHACTIVITY
                            context.startActivity(intent)
                        }
                    )
                }

                composable(NavBarItem.account.route) {
                    Activity_Account(
                        context = context,
                        appearanceState = appearanceState,
                        mainViewModel = getMainViewModel(),
                        onMessageReceived = { text, clearPrevious, actionText, action ->
                            showSnackBar(text = text, clearPrevious = clearPrevious, actionText = actionText, action = action)
                        }
                    )
                }

                composable(NavBarItem.notification.route) {
                    NotificationScaffold(
                        context = context,
                        itemList = getMainViewModel().notificationHistory.toList(),
                        snackBarHostState = null,
                        isVisible = true,
                        appearanceState = appearanceState,
                        onClick = { item ->
                            if (listOf(1, 2).contains(item.tag)) {
                                Intent(context, NewsActivity::class.java).also { intent ->
                                    intent.action = NewsActivity.INTENT_NEWSDETAILACTIVITY
                                    for (map1 in item.parameters) {
                                        intent.putExtra(map1.key, map1.value)
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        },
                        onClear = { item ->
                            val itemTemp = item.clone()
                            getMainViewModel().notificationHistory.remove(item)
                            getMainViewModel().saveSettings()
                            showSnackBar(
                                text = context.getString(R.string.notification_removed),
                                actionText = context.getString(R.string.action_undo),
                                action = {
                                    getMainViewModel().notificationHistory.add(itemTemp)
                                    getMainViewModel().saveSettings()
                                }
                            )
                        },
                        onClearAll = {
                            showSnackBar(
                                text = context.getString(R.string.notification_removeall_confirm),
                                actionText = context.getString(R.string.action_confirm),
                                action = {
                                    getMainViewModel().saveSettings()
                                    getMainViewModel().notificationHistory.clear()
                                    showSnackBar(
                                        text = context.getString(R.string.notification_removeall_removed),
                                        clearPrevious = true
                                    )
                                },
                                clearPrevious = true
                            )
                        }
                    )
                }

                composable(NavBarItem.settings.route) {
                    Activity_Settings(
                        context = context,
                        appearanceState = appearanceState,
                        mainViewModel = getMainViewModel(),
                        mediaRequest = {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        onMessageReceived = { text, clearPrevious, actionText, action ->
                            showSnackBar(text = text, clearPrevious = clearPrevious, actionText = actionText, action = action)
                        }
                    )
                }
            }
        }
    )
}