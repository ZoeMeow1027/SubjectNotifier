package io.zoemeow.dutschedule.ui.view.news

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import io.dutwrapper.dutwrapper.model.enums.NewsType
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.ui.component.news.NewsSearchOptionAndHistory
import io.zoemeow.dutschedule.ui.component.news.NewsSearchResult
import io.zoemeow.dutschedule.viewmodel.NewsSearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_News_NewsSearch(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    onBack: () -> Unit
) {
    val newsSearchViewModel: NewsSearchViewModel = viewModel()
    val lazyListState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val isSearchFocused: MutableTransitionState<Boolean> = remember {
        MutableTransitionState(false).apply {
            targetState = false
        }
    }

    fun dismissFocus() {
        focusManager.clearFocus(force = true)
        isSearchFocused.targetState = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = newsSearchViewModel.query.value,
                        onValueChange = { newsSearchViewModel.query.value = it },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    isSearchFocused.targetState = true
                                }
                            },
                        placeholder = {
                            Text(context.getString(R.string.news_search_searchbox_placeholder))
                        },
                        trailingIcon = {
                            if (isSearchFocused.targetState) {
                                IconButton(
                                    content = {
                                        Icon(
                                            Icons.Default.Clear,
                                            context.getString(R.string.action_clear)
                                        )
                                    },
                                    onClick = {
                                        newsSearchViewModel.query.value = ""
                                    }
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                dismissFocus()
                                newsSearchViewModel.invokeSearch(startOver = true)
                            }
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isSearchFocused.targetState) {
                                dismissFocus()
                            } else {
                                onBack()
                            }
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
                actions = {
                    IconButton(
                        // modifier = Modifier.padding(start = 5.dp),
                        onClick = {
                            dismissFocus()
                            newsSearchViewModel.invokeSearch(startOver = true)
                        },
                        enabled = newsSearchViewModel.progress.value != ProcessState.Running,
                        content = {
                            Icon(Icons.Default.Search, context.getString(R.string.action_search))
                        }
                    )
                }
            )
        },
        content = { padding ->
            NewsSearchResult(
                context = context,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 10.dp),
                newsList = newsSearchViewModel.newsList,
                lazyListState = lazyListState,
                opacity = appearanceState.componentOpacity,
                processState = newsSearchViewModel.progress.value,
                onEndOfList = {
                    newsSearchViewModel.invokeSearch()
                },
                onItemClicked = { item ->
                    dismissFocus()
                    context.startActivity(
                        Intent(
                            context,
                            NewsActivity::class.java
                        ).also {
                            it.action = NewsActivity.INTENT_NEWSDETAILACTIVITY
                            it.putExtra("type", if (newsSearchViewModel.type.value == NewsType.Subject) NewsActivity.NEWSTYPE_NEWSSUBJECT else NewsActivity.NEWSTYPE_NEWSGLOBAL)
                            it.putExtra("data", Gson().toJson(item))
                        })
                }
            )
            NewsSearchOptionAndHistory(
                context = context,
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 10.dp)
                    .padding(top = 5.dp),
                isVisible = isSearchFocused,
                searchHistory = newsSearchViewModel.searchHistory.toList(),
                backgroundColor = MaterialTheme.colorScheme.background,
                query = newsSearchViewModel.query.value,
                newsMethod = newsSearchViewModel.method.value,
                newsType = newsSearchViewModel.type.value,
                onSettingsChanged = { query, method, type ->
                    newsSearchViewModel.let {
                        it.query.value = query
                        it.method.value = method
                        it.type.value = type
                    }
                },
                onSearchTriggered = {
                    newsSearchViewModel.invokeSearch(startOver = true)
                },
                onClearHistoryTriggered = {
                    newsSearchViewModel.clearHistory()
                },
                onDismiss = {
                    dismissFocus()
                }
            )
        }
    )

    BackHandler(
        enabled = isSearchFocused.targetState,
        onBack = {
            dismissFocus()
        }
    )
}