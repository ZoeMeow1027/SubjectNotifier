package io.zoemeow.dutschedule.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.service.BaseService
import io.zoemeow.dutschedule.service.NewsBackgroundUpdateService
import io.zoemeow.dutschedule.ui.view.main.Activity_MainView_MainViewTabView
import io.zoemeow.dutschedule.ui.view.main.MainViewDashboard
import io.zoemeow.dutschedule.utils.NotificationsUtil

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    @Composable
    override fun OnPreloadOnce() {
        NewsBackgroundUpdateService.cancelSchedule(
            context = this,
            onDone = {
                Log.d("NewsBackgroundService", "Cancelled schedule")
            }
        )
        NotificationsUtil.initializeNotificationChannel(this)
    }

    @Composable
    override fun OnMainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        appearanceState: AppearanceState
    ) {
        if (getMainViewModel().appSettings.value.mainScreenDashboardView) {
            MainViewDashboard(
                context = context,
                snackBarHostState = snackBarHostState,
                appearanceState = appearanceState,
                newsClicked = {
                    context.startActivity(Intent(context, NewsActivity::class.java))
                },
                accountClicked = {
                    context.startActivity(Intent(context, AccountActivity::class.java))
                },
                settingsClicked = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                },
                externalLinkClicked = {
                    val intent = Intent(context, MiscellaneousActivity::class.java)
                    intent.action = "view_externallink"
                    context.startActivity(intent)
                }
            )
        } else {
            Activity_MainView_MainViewTabView(
                context = context,
                mainViewModel = getMainViewModel(),
                snackBarHostState = snackBarHostState,
                appearanceState = appearanceState,
                onMessageReceived = { msg, forceDismissBefore, actionText, action ->
                    showSnackBar(
                        text = msg,
                        clearPrevious = forceDismissBefore,
                        actionText = actionText,
                        action = action
                    )
                },
                onMessageClear = {
                    clearSnackBar()
                }
            )
        }
    }

    override fun onStop() {
        Log.d("MainActivity", "MainActivity is being stopped")
        NewsBackgroundUpdateService.cancelSchedule(
            context = this,
            onDone = {
                Log.d("NewsBackgroundService", "Cancelled schedule")
            }
        )
        if (getMainViewModel().appSettings.value.newsBackgroundDuration > 0) {
            Log.d("NewsBackgroundService", "Started service")
            BaseService.startService(
                context = this,
                intent = Intent(applicationContext, NewsBackgroundUpdateService::class.java).also {
                    it.action = "news.service.action.fetchallpage1background"
                }
            )
        }
        super.onStop()
    }
}