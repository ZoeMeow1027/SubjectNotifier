package io.zoemeow.dutschedule.ui.view.news

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.dutwrapper.dutwrapper.News
import io.dutwrapper.dutwrapper.News.NewsItem
import io.dutwrapper.dutwrapper.News.NewsSubjectItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.ui.view.news.controls.NewsDetailScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_News_NewsDetail(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    newsType: String? = null,
    newsData: String? = null,
    onLinkClicked: ((String) -> Unit)? = null,
    onMessageReceived: (String, Boolean, String?, (() -> Unit)?) -> Unit, // (msg, forceDismissBefore, actionText, action)
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            LargeTopAppBar(
                title = { Text(context.getString(R.string.news_detail_title)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
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
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            if (newsType == NewsActivity.NEWSTYPE_NEWSSUBJECT) {
                ExtendedFloatingActionButton(
                    content = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, context.getString(R.string.news_detail_addtofilter_fab))
                            Spacer(modifier = Modifier.size(5.dp))
                            Text(context.getString(R.string.news_detail_addtofilter_fab))
                        }
                    },
                    onClick = {
                        try {
                            // TODO: Develop a add news filter function for news subject detail.
                            onMessageReceived(context.getString(R.string.feature_not_ready), true, null, null)
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            onMessageReceived(context.getString(R.string.news_detail_addtofilter_failed), true, null, null)
                        }
                    }
                )
            }
        },
        content = {
            when (newsType) {
                NewsActivity.NEWSTYPE_NEWSGLOBAL -> {
                    NewsDetailScreen(
                        context = context,
                        padding = it,
                        newsItem = Gson().fromJson(newsData, object : TypeToken<NewsItem>() {}.type),
                        newsType = News.NewsType.Global,
                        linkClicked = { link ->
                            onLinkClicked?.let { it(link) }
                        }
                    )
                }
                NewsActivity.NEWSTYPE_NEWSSUBJECT -> {
                    NewsDetailScreen(
                        context = context,
                        padding = it,
                        newsItem = Gson().fromJson(newsData, object : TypeToken<NewsSubjectItem>() {}.type) as NewsItem,
                        newsType = News.NewsType.Subject,
                        linkClicked = { link ->
                            onLinkClicked?.let { it(link) }
                        }
                    )
                }
                else -> { }
            }
        }
    )
}