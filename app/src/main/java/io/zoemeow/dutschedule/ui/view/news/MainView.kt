package io.zoemeow.dutschedule.ui.view.news

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import io.dutwrapper.dutwrapper.model.enums.NewsSearchType
import io.dutwrapper.dutwrapper.model.news.NewsGlobalItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.news.NewsFetchType
import io.zoemeow.dutschedule.ui.component.base.ButtonBase
import io.zoemeow.dutschedule.ui.component.news.NewsListPage
import io.zoemeow.dutschedule.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NewsActivity.MainView(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color,
    searchRequested: (() -> Unit)? = null
) {
    NewsMainView(
        context = context,
        snackBarHostState = snackBarHostState,
        containerColor = containerColor,
        contentColor = contentColor,
        searchRequested = searchRequested,
        componentBackgroundAlpha = getControlBackgroundAlpha(),
        mainViewModel = getMainViewModel(),
        onBack = {
            setResult(ComponentActivity.RESULT_OK)
            finish()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NewsMainView(
    context: Context,
    snackBarHostState: SnackbarHostState? = null,
    containerColor: Color,
    contentColor: Color,
    componentBackgroundAlpha: Float = 1f,
    mainViewModel: MainViewModel,
    searchRequested: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { snackBarHostState?.let { SnackbarHost(hostState = it) } },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            TopAppBar(
                title = { Text(text = "News") },
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
                                    "",
                                    modifier = Modifier.size(25.dp)
                                )
                            }
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            searchRequested?.let { it() }
                        },
                        content = {
                            Icon(Icons.Default.Search, "Search")
                        }
                    )
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
                        SingleChoiceSegmentedButtonRow {
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                                onClick = { scope.launch {
                                    pagerState.animateScrollToPage(0)
                                } },
                                selected = pagerState.currentPage == 0,
                                label = {
                                    Text("Global")
                                }
                            )
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                                onClick = { scope.launch {
                                    pagerState.animateScrollToPage(1)
                                } },
                                selected = pagerState.currentPage == 1,
                                label = {
                                    Text("Subject")
                                }
                            )
                        }
                    }
                },
                floatingActionButton = {
                    if (when (pagerState.currentPage) {
                            0 -> {
                                mainViewModel.newsInstance.newsGlobal.processState.value != ProcessState.Running
                            }

                            1 -> {
                                mainViewModel.newsInstance.newsSubject.processState.value != ProcessState.Running
                            }

                            else -> false
                        }
                    ) {
                        FloatingActionButton(
                            onClick = {
                                when (pagerState.currentPage) {
                                    0 -> {
                                        mainViewModel.newsInstance.fetchGlobalNews(
                                            fetchType = NewsFetchType.ClearAndFirstPage,
                                            forceRequest = true
                                        )
                                    }

                                    1 -> {
                                        mainViewModel.newsInstance.fetchSubjectNews(
                                            fetchType = NewsFetchType.ClearAndFirstPage,
                                            forceRequest = true
                                        )
                                    }

                                    else -> {}
                                }
                            },
                            content = {
                                Icon(Icons.Default.Refresh, "Refresh")
                            }
                        )
                    }
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
                            opacity = componentBackgroundAlpha,
                            itemClicked = { newsItem ->
                                context.startActivity(
                                    Intent(
                                        context,
                                        NewsActivity::class.java
                                    ).also {
                                        it.action = "activity_detail"
                                        it.putExtra("type", "news_global")
                                        it.putExtra("data", Gson().toJson(newsItem))
                                    })
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
                        @Suppress("UNCHECKED_CAST")
                        (NewsListPage(
                            newsList = mainViewModel.newsInstance.newsSubject.data.toList() as List<NewsGlobalItem>,
                            processState = mainViewModel.newsInstance.newsSubject.processState.value,
                            opacity = componentBackgroundAlpha,
                            itemClicked = { newsItem ->
                                context.startActivity(
                                    Intent(
                                        context,
                                        NewsActivity::class.java
                                    ).also {
                                        it.action = "activity_detail"
                                        it.putExtra("type", "news_subject")
                                        it.putExtra("data", Gson().toJson(newsItem))
                                    })
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