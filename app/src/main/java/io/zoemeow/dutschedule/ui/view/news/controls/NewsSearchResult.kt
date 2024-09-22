package io.zoemeow.dutschedule.ui.view.news.controls

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.News.NewsItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.utils.endOfListReached

@Composable
fun NewsSearchResult(
    context: Context,
    modifier: Modifier = Modifier,
    newsList: List<NewsItem>,
    lazyListState: LazyListState,
    opacity: Float = 1f,
    processState: ProcessState,
    onEndOfList: (() -> Unit)? = null,
    onItemClicked: ((NewsItem) -> Unit)? = null
) {
    Column(modifier = modifier) {
        if (processState == ProcessState.Running) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        if (newsList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (processState) {
                    ProcessState.Running -> {
                        Text(
                            context.getString(R.string.news_search_fetching),
                            textAlign = TextAlign.Center
                        )
                    }
                    ProcessState.NotRunYet -> {
                        Text(
                            context.getString(R.string.news_search_getstarted),
                            textAlign = TextAlign.Center
                        )
                    }
                    ProcessState.Failed -> {
                        Text(
                            context.getString(R.string.news_search_failed),
                            textAlign = TextAlign.Center
                        )
                    }
                    else -> {
                        Text(
                            context.getString(R.string.news_search_noavailablenews),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .endOfListReached(
                        lazyListState = lazyListState,
                        onReached = {
                            onEndOfList?.let { it() }
                        }
                    ),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
                state = lazyListState,
                content = {
                    items(newsList) { item ->
                        NewsListItem(
                            title = item.title,
                            description = item.content,
                            dateTime = item.date,
                            opacity = opacity,
                            onClick = {
                                onItemClicked?.let { it(item) }
                            }
                        )
                        Spacer(modifier = Modifier.size(3.dp))
                    }
                }
            )
        }

    }
}