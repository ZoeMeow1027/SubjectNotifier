package io.zoemeow.dutschedule.activity

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.ui.view.news.Activity_News_NewsDetail
import io.zoemeow.dutschedule.ui.view.news.Activity_News
import io.zoemeow.dutschedule.ui.view.news.Activity_News_NewsSearch
import io.zoemeow.dutschedule.utils.openLink

@AndroidEntryPoint
class NewsActivity : BaseActivity() {
    companion object {
        const val INTENT_SEARCHACTIVITY = "activity_search"
        const val INTENT_NEWSDETAILACTIVITY = "activity_detail"
        const val NEWSTYPE_NEWSGLOBAL = "news_global"
        const val NEWSTYPE_NEWSSUBJECT = "news_subject"
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
            INTENT_SEARCHACTIVITY -> {
                Activity_News_NewsSearch(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }

            INTENT_NEWSDETAILACTIVITY -> {
                Activity_News_NewsDetail(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    newsType = intent.getStringExtra("type"),
                    newsData = intent.getStringExtra("data"),
                    onLinkClicked = { link ->
                        context.openLink(
                            url = link,
                            customTab = getMainViewModel().appSettings.value.openLinkInsideApp
                        )
                    },
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
                Activity_News(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    mainViewModel = getMainViewModel(),
                    searchRequested = {
                        val intent = Intent(context, NewsActivity::class.java)
                        intent.action = INTENT_SEARCHACTIVITY
                        context.startActivity(intent)
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
