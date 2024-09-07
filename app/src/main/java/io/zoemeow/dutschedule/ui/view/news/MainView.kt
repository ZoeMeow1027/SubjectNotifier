package io.zoemeow.dutschedule.ui.view.news

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import io.dutwrapper.dutwrapper.News.NewsItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.news.NewsFetchType
import io.zoemeow.dutschedule.ui.component.news.NewsListPage
import io.zoemeow.dutschedule.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_News(
    context: Context,
    snackBarHostState: SnackbarHostState? = null,
    appearanceState: AppearanceState,
    mainViewModel: MainViewModel,
    searchRequested: (() -> Unit)? = null,
    onNewsClicked: ((String?, String?) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { snackBarHostState?.let { SnackbarHost(hostState = it) } },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            Box(
                contentAlignment = Alignment.BottomCenter,
                content = {
                    TopAppBar(
                        title = { Text(text = context.getString(R.string.news_title)) },
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
                        },
                        actions = {
                            val newsSearchTooltipSearch = rememberTooltipState()
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                                tooltip = {
                                    PlainTooltip {
                                        Text(text = context.getString(R.string.news_action_search))
                                    }
                                },
                                state = newsSearchTooltipSearch,
                                content = {
                                    IconButton(
                                        onClick = {
                                            searchRequested?.let { it() }
                                        },
                                        content = {
                                            Icon(Icons.Default.Search, context.getString(R.string.news_action_search))
                                        }
                                    )
                                }
                            )
                        }
                    )
                    if (mainViewModel.newsInstance.newsGlobal.processState.value == ProcessState.Running && pagerState.currentPage == 0) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                    if (mainViewModel.newsInstance.newsSubject.processState.value == ProcessState.Running && pagerState.currentPage == 1) {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = BottomAppBarDefaults.containerColor.copy(
                    alpha = 0f
                ),
                actions = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp)
                        ) {
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(0)
                                    }
                                },
                                selected = pagerState.currentPage == 0,
                                label = {
                                    Text(text = context.getString(R.string.news_tabname_global))
                                }
                            )
                            SegmentedButton(
                                modifier = Modifier.wrapContentHeight(),
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(1)
                                    }
                                },
                                selected = pagerState.currentPage == 1,
                                label = {
                                    Text(
                                        text = context.getString(R.string.news_tabname_subject),
                                        overflow = TextOverflow.Visible,
                                        softWrap = false,
                                        maxLines = 1
                                    )
                                }
                            )
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            when (pagerState.currentPage) {
                                0 -> {
                                    if (mainViewModel.newsInstance.newsGlobal.processState.value != ProcessState.Running) {
                                        mainViewModel.newsInstance.fetchGlobalNews(
                                            fetchType = NewsFetchType.ClearAndFirstPage,
                                            forceRequest = true
                                        )
                                    }
                                }

                                1 -> {
                                    if (mainViewModel.newsInstance.newsSubject.processState.value != ProcessState.Running) {
                                        mainViewModel.newsInstance.fetchSubjectNews(
                                            fetchType = NewsFetchType.ClearAndFirstPage,
                                            forceRequest = true
                                        )
                                    }
                                }

                                else -> {}
                            }
                        },
                        content = {
                            if (pagerState.currentPage == 0 && mainViewModel.newsInstance.newsGlobal.processState.value == ProcessState.Running) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else if (pagerState.currentPage == 1 && mainViewModel.newsInstance.newsSubject.processState.value == ProcessState.Running) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Refresh, context.getString(R.string.action_refresh))
                            }
                        }
                    )
                }
            )
        },
        content = { padding ->
            HorizontalPager(
                modifier = Modifier.padding(padding),
                state = pagerState
            ) { pageIndex ->
                when (pageIndex) {
                    0 -> {
                        NewsListPage(
                            newsList = mainViewModel.newsInstance.newsGlobal.data.toList(),
                            processState = mainViewModel.newsInstance.newsGlobal.processState.value,
                            opacity = appearanceState.componentOpacity,
                            itemClicked = { newsItem ->
                                onNewsClicked?.let { it(
                                    NewsActivity.NEWSTYPE_NEWSGLOBAL,
                                    Gson().toJson(newsItem)
                                ) }
                            },
                            endOfListReached = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    withContext(Dispatchers.IO) {
                                        mainViewModel.newsInstance.fetchGlobalNews(
                                            fetchType = NewsFetchType.NextPage,
                                            forceRequest = true
                                        )
                                    }
                                }
                            }
                        )
                    }

                    1 -> {
                        (NewsListPage(
                            newsList = mainViewModel.newsInstance.newsSubject.data.toList() as List<NewsItem>,
                            processState = mainViewModel.newsInstance.newsSubject.processState.value,
                            opacity = appearanceState.componentOpacity,
                            itemClicked = { newsItem ->
                                onNewsClicked?.let { it(
                                    NewsActivity.NEWSTYPE_NEWSSUBJECT,
                                    Gson().toJson(newsItem)
                                ) }
                            },
                            endOfListReached = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    withContext(Dispatchers.IO) {
                                        mainViewModel.newsInstance.fetchSubjectNews(
                                            fetchType = NewsFetchType.NextPage,
                                            forceRequest = true
                                        )
                                    }
                                }
                            }
                        ))
                    }
                }
            }
        }
    )
}