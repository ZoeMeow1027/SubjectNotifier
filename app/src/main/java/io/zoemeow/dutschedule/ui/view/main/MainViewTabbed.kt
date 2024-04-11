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
import io.zoemeow.dutschedule.activity.MainActivity
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.NavBarItem
import io.zoemeow.dutschedule.ui.view.account.AccountMainView
import io.zoemeow.dutschedule.ui.view.news.NewsMainView
import io.zoemeow.dutschedule.ui.view.settings.SettingsMainView

@Composable
fun MainActivity.MainViewTabbed(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color,
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
        contentColor = contentColor,
        // https://stackoverflow.com/questions/75328833/compose-scaffold-unnecessary-systembar-padding-due-to-windowcompat-setdecorfi
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            NavigationBar(
                containerColor = containerColor,
                contentColor = contentColor,
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
                                        Icon(painter = painterResource(id = it.resourceIconId), it.title)
                                    } else if (it.icon != null) {
                                        Icon(imageVector = it.icon, it.title)
                                    } else {
                                        Icon(imageVector = Icons.Default.Info, it.title)
                                    }
                                },
                                label = { Text(it.title) }
                            )
                        }
                    )
                }
            )
        },
        content = {
            NavHost(
                navController = navController,
                startDestination = NavBarItem.news.route,
                enterTransition = { fadeIn(animationSpec = tween(300)) },
                exitTransition = { fadeOut(animationSpec = tween(300)) },
                popEnterTransition = { fadeIn(animationSpec = tween(300)) },
                popExitTransition = { fadeOut(animationSpec = tween(300)) },
                modifier = Modifier.padding(it)
            ) {
                // TODO: Add view here!
                composable(NavBarItem.news.route) {
                    NewsMainView(
                        context = context,
                        containerColor = containerColor,
                        contentColor = contentColor,
                        componentBackgroundAlpha = getControlBackgroundAlpha(),
                        mainViewModel = getMainViewModel(),
                        searchRequested = {
                            val intent = Intent(context, NewsActivity::class.java)
                            intent.action = "activity_search"
                            context.startActivity(intent)
                        }
                    )
                }

                composable(NavBarItem.account.route) {
                    AccountMainView(
                        context = context,
                        containerColor = containerColor,
                        contentColor = contentColor,
                        componentBackgroundAlpha = getControlBackgroundAlpha(),
                        mainViewModel = getMainViewModel(),
                        onShowSnackBar = { text, clearPrevious, actionText, action ->
                            showSnackBar(text = text, clearPrevious = clearPrevious, actionText = actionText, action = action)
                        }
                    )
                }

                composable(NavBarItem.notification.route) {
                    NotificationScaffold(
                        itemList = getMainViewModel().notificationHistory.toList(),
                        snackBarHostState = snackBarHostState,
                        isVisible = true,
                        containerColor = containerColor,
                        contentColor = contentColor,
                        onClick = { item ->
                            if (listOf(1, 2).contains(item.tag)) {
                                Intent(context, NewsActivity::class.java).also {
                                    it.action = "activity_detail"
                                    for (map1 in item.parameters) {
                                        it.putExtra(map1.key, map1.value)
                                    }
                                    context.startActivity(it)
                                }
                            }
                        },
                        onClear = { item ->
                            val itemTemp = item.clone()
                            getMainViewModel().notificationHistory.remove(item)
                            getMainViewModel().saveSettings()
                            showSnackBar(
                                text = "Deleted notifications!",
                                actionText = "Undo",
                                action = {
                                    getMainViewModel().notificationHistory.add(itemTemp)
                                    getMainViewModel().saveSettings()
                                }
                            )
                        },
                        onClearAll = {
                            showSnackBar(
                                text = "This action is undone! To confirm, click \"Confirm\" to clear all.",
                                actionText = "Confirm",
                                action = {
                                    getMainViewModel().notificationHistory.clear()
                                    getMainViewModel().saveSettings()
                                    showSnackBar(
                                        text = "Successfully cleared all notifications!",
                                        clearPrevious = true
                                    )
                                },
                                clearPrevious = true
                            )
                        },
                        opacity = getControlBackgroundAlpha()
                    )
                }

                composable(NavBarItem.settings.route) {
                    SettingsMainView(
                        context = context,
                        containerColor = containerColor,
                        contentColor = contentColor,
                        componentBackgroundAlpha = getControlBackgroundAlpha(),
                        mainViewModel = getMainViewModel(),
                        mediaRequest = {
                            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        onShowSnackBar = { text, clearPrevious, actionText, action ->
                            showSnackBar(text = text, clearPrevious = clearPrevious, actionText = actionText, action = action)
                        }
                    )
                }
            }
        }
    )
}