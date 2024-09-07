package io.zoemeow.dutschedule.ui.component.news

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.dutwrapper.dutwrapper.News
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.news.NewsSearchHistory

@Composable
fun NewsSearchOptionAndHistory(
    context: Context,
    modifier: Modifier = Modifier,
    isVisible: MutableTransitionState<Boolean>,
    searchHistory: List<NewsSearchHistory>,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    query: String = "",
    newsMethod: News.NewsSearchType,
    newsType: News.NewsType,
    onSettingsChanged: ((String, News.NewsSearchType, News.NewsType) -> Unit)? = null,
    onSearchTriggered: (() -> Unit)? = null,
    onClearHistoryTriggered: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    AnimatedVisibility(
        visibleState = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        content = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = backgroundColor,
                content = {
                    Column(
                        modifier = modifier.fillMaxSize(),
                        content = {
                            Text(
                                context.getString(R.string.news_search_searchoption_method),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 5.dp),
                                content = {
                                    News.NewsSearchType.entries.forEachIndexed { index, item ->
                                        SegmentedButton(
                                            shape = SegmentedButtonDefaults.itemShape(index = index, count = News.NewsSearchType.entries.size),
                                            onClick = { onSettingsChanged?.let { it(query, item, newsType) } },
                                            selected = newsMethod == item,
                                            label = {
                                                Text(
                                                    when (item) {
                                                        News.NewsSearchType.ByTitle -> context.getString(R.string.news_search_searchoption_method_bytitle)
                                                        News.NewsSearchType.ByContent -> context.getString(R.string.news_search_searchoption_method_bycontent)
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                            )
                            Text(
                                context.getString(R.string.news_search_searchoption_type),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                            SingleChoiceSegmentedButtonRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 5.dp),
                                content = {
                                    News.NewsType.entries.forEachIndexed { index, item ->
                                        SegmentedButton(
                                            shape = SegmentedButtonDefaults.itemShape(index = index, count = News.NewsType.entries.size),
                                            onClick = {
                                                onSettingsChanged?.let {
                                                    it(query, newsMethod, item)
                                                }
                                            },
                                            selected = newsType == item,
                                            label = {
                                                Text(
                                                    when (item) {
                                                        News.NewsType.Subject -> context.getString(R.string.news_search_searchoption_type_bysubject)
                                                        News.NewsType.Global -> context.getString(R.string.news_search_searchoption_type_byglobal)
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                            )
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                content = {
                                    Row(
                                        modifier = Modifier
                                            .padding(bottom = 5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        content = {
                                            Text(
                                                context.getString(R.string.news_search_searchoption_history),
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            Spacer(modifier = Modifier.weight(1f))
                                            IconButton(
                                                content = { Icon(Icons.Default.Delete, "") },
                                                onClick = {
                                                    onClearHistoryTriggered?.let { it() }
                                                }
                                            )
                                        }
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(rememberScrollState()),
                                        content = {
                                            searchHistory.forEach { queryItem ->
                                                ListItem(
                                                    headlineContent = {
                                                        Column {
                                                            Text(queryItem.query)
                                                            Text(context.getString(
                                                                R.string.news_search_searchoption_history_data,
                                                                when (queryItem.newsMethod) {
                                                                    News.NewsSearchType.ByTitle -> context.getString(R.string.news_search_searchoption_method_bytitle)
                                                                    News.NewsSearchType.ByContent -> context.getString(R.string.news_search_searchoption_method_bycontent)
                                                                },
                                                                when (queryItem.newsType) {
                                                                    News.NewsType.Subject -> context.getString(R.string.news_search_searchoption_type_bysubject)
                                                                    News.NewsType.Global -> context.getString(R.string.news_search_searchoption_type_byglobal)
                                                                }
                                                            ))
                                                        }
                                                    },
                                                    leadingContent = {
                                                        Icon(Icons.Outlined.Search, "")
                                                    },
                                                    trailingContent = {
                                                        IconButton(
                                                            content = {
                                                                Icon(imageVector = ImageVector.vectorResource(
                                                                    R.drawable.ic_baseline_north_west_24), "")
                                                            },
                                                            onClick = {
                                                                onSettingsChanged?.let { it(
                                                                    queryItem.query,
                                                                    queryItem.newsMethod,
                                                                    queryItem.newsType
                                                                ) }
                                                            }
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            onSettingsChanged?.let {
                                                                it(
                                                                    queryItem.query,
                                                                    queryItem.newsMethod,
                                                                    queryItem.newsType
                                                                )
                                                            }
                                                            onSearchTriggered?.let { it() }
                                                            onDismiss?.let { it() }
                                                        }
                                                )
                                            }
                                        }
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    )
}