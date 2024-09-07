package io.zoemeow.dutschedule.ui.view.miscellaneous

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.HelpLinkInfo
import io.zoemeow.dutschedule.ui.component.helpandexternallink.ClickableExternalLinks
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Miscellaneous_ExternalLinks(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    onLinkClicked: (String) -> Unit,
    onBack: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    val searchEnabled = remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.miscellaneous_externallinks_title)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(
                        onClick = { onBack() },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                context.getString(R.string.action_back),
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    )
                },
            )
        },
        bottomBar = {
            val pageInfoTooltipState = rememberTooltipState(
                isPersistent = true,
                initialIsVisible = true
            )
            BottomAppBar(
                actions = {
                    TextButton(
                        onClick = {
                            searchEnabled.value = !searchEnabled.value
                        },
                        content = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.size(5.dp))
                                Icon(ImageVector.vectorResource(id = R.drawable.ic_baseline_filter_list_alt_24), context.getString(R.string.action_search))
                                Spacer(modifier = Modifier.size(3.dp))
                                Text(context.getString(R.string.miscellaneous_externallinks_action_search))
                                Spacer(modifier = Modifier.size(5.dp))
                            }
                        },
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = if (!searchEnabled.value) Color.Transparent else MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                            contentColor = if (!searchEnabled.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
                        tooltip = {
                            RichTooltip(
                                title = { Text(context.getString(R.string.miscellaneous_externallinks_action_aboutrelatedlink)) },
                                text = {
                                    Text(context.getString(R.string.miscellaneous_externallinks_tooltip_aboutrelatedlink))
                                }
                            )
                        },
                        state = pageInfoTooltipState,
                        enableUserInput = false,
                        content = {
                            TextButton(
                                onClick = {
                                    scope.launch {
                                        if (pageInfoTooltipState.isVisible) {
                                            pageInfoTooltipState.dismiss()
                                        }
                                        pageInfoTooltipState.show()
                                    }
                                },
                                content = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, context.getString(R.string.tooltip_info))
                                        Spacer(modifier = Modifier.size(5.dp))
                                        Text(context.getString(R.string.miscellaneous_externallinks_action_aboutrelatedlink))
                                    }
                                }
                            )
                        }
                    )
                },
                containerColor = Color.Transparent
            )
        },
        content = { paddingValues ->
            Scaffold(
                modifier = Modifier.padding(paddingValues),
                contentWindowInsets = WindowInsets(top = 0.dp),
                containerColor = Color.Transparent,
                bottomBar = {
                    AnimatedVisibility(
                        visible = searchEnabled.value,
                        enter = slideInVertically(initialOffsetY = { it } ),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp)
                                .focusRequester(focusRequester),
                            value = searchText.value,
                            label = { Text(context.getString(R.string.miscellaneous_externallinks_searchaexternallink)) },
                            onValueChange = {
                                if (searchEnabled.value) {
                                    searchText.value = it
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus(force = true)
                                }
                            ),
                            trailingIcon = {
                                if (searchText.value.isNotEmpty()) {
                                    IconButton(
                                        onClick = { searchText.value = "" },
                                        content = {
                                            Icon(
                                                Icons.Default.Clear,
                                                context.getString(R.string.action_clear)
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            ) { paddingValues2 ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues2)
                        .fillMaxSize()
                        .padding(horizontal = 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    HelpLinkInfo.getAllExternalLink().filter { item ->
                        searchText.value.isEmpty() ||
                                item.title.lowercase().contains(searchText.value.lowercase()) ||
                                item.description?.lowercase()?.contains(searchText.value.lowercase()) ?: false ||
                                item.url.lowercase().contains(searchText.value.lowercase())
                    }.toList().forEach { item ->
                        ClickableExternalLinks(
                            item = item,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 7.dp),
                            opacity = appearanceState.componentOpacity,
                            onClick = {
                                focusManager.clearFocus(force = true)
                                try {
                                    onLinkClicked(item.url)
                                } catch (ex: Exception) {
                                    ex.printStackTrace()
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}