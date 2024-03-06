package io.zoemeow.dutschedule.ui.view.main

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.AccountActivity
import io.zoemeow.dutschedule.activity.MainActivity
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.model.CustomClock
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.news.NewsCache
import io.zoemeow.dutschedule.ui.component.main.DateAndTimeSummaryItem
import io.zoemeow.dutschedule.ui.component.main.LessonTodaySummaryItem
import io.zoemeow.dutschedule.ui.component.main.SchoolNewsSummaryItem
import io.zoemeow.dutschedule.ui.component.main.UpdateAvailableSummaryItem
import io.zoemeow.dutschedule.ui.component.main.notification.NotificationHistoryBottomSheet
import io.zoemeow.dutschedule.utils.CustomDateUtil
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivity.MainViewDashboard(
    context: Context,
    snackBarHostState: SnackbarHostState,
    containerColor: Color,
    contentColor: Color,
    newsClicked: (() -> Unit)? = null,
    accountClicked: (() -> Unit)? = null,
    settingsClicked: (() -> Unit)? = null,
    externalLinkClicked: (() -> Unit)? = null
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val isNotificationOpened = remember { mutableStateOf(false) }
    val needConfirmClearAllNotifications = remember { mutableStateOf(true) }
    val notificationModalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val notificationSheetScope = rememberCoroutineScope()

    fun getNews(byWeek: Boolean = false): Int {
        var data = 0
        val today = LocalDateTime(
            Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
            LocalTime(0, 0, 0)
        ).toInstant(TimeZone.UTC)
        val before7Days = today.minus(7.days)

        if (!byWeek) {
            (getMainViewModel().newsGlobal.data.value ?: NewsCache()).newsListByDate.firstOrNull {
                // https://stackoverflow.com/questions/77368433/how-to-get-current-date-with-reset-time-0000-with-kotlinx-localdatetime
                it.date == today.toEpochMilliseconds()
            }.also {
                if (it != null) data = it.itemList.count()
            }
        } else {
            (getMainViewModel().newsGlobal.data.value ?: NewsCache()).newsListByDate.forEach {
                // https://stackoverflow.com/questions/77368433/how-to-get-current-date-with-reset-time-0000-with-kotlinx-localdatetime
                if (it.date <= today.toEpochMilliseconds() && it.date >= before7Days.toEpochMilliseconds()) {
                    data += it.itemList.count()
                }
            }
        }
        return data
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = containerColor,
        contentColor = contentColor,
        topBar = {
            LargeTopAppBar(
                title = { Text(text = "DutSchedule") },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent),
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = BottomAppBarDefaults.containerColor.copy(
                    alpha = getControlBackgroundAlpha()
                ),
                actions = {
                    BadgedBox(
                        // modifier = Modifier.padding(start = 15.dp, end = 15.dp),
                        badge = {
                            // Badge { }
                        }
                    ) {
                        IconButton(
                            onClick = { newsClicked?.let { it() } }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_newspaper_24),
                                "News",
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                    BadgedBox(
                        // modifier = Modifier.padding(end = 15.dp),
                        badge = {
                            // Badge { }
                        }
                    ) {
                        IconButton(
                            onClick = { settingsClicked?.let { it() } }
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                "Settings",
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                    BadgedBox(
                        // modifier = Modifier.padding(end = 15.dp),
                        badge = {
                            // Badge { }
                        }
                    ) {
                        IconButton(onClick = { externalLinkClicked?.let { it() } }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_web_24),
                                "External links",
                                modifier = Modifier.size(27.dp)
                            )
                        }
                    }
                    BadgedBox(
                        // modifier = Modifier.padding(end = 15.dp),
                        badge = {
                            if (getMainViewModel().notificationHistory.isNotEmpty()) {
                                Badge {
                                    Text(getMainViewModel().notificationHistory.size.toString())
                                }
                            }
                        },
                        content = {
                            IconButton(
                                onClick = {
                                    // Open notification bottom sheet
                                    // Notification list requested
                                    notificationSheetScope.launch {
                                        if (!isNotificationOpened.value) {
                                            isNotificationOpened.value = true
                                        }
                                        notificationModalBottomSheetState.expand()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    "Notifications",
                                    modifier = Modifier.size(27.dp),
                                )
                            }
                        }
                    )
                },
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        text = {
                            Column(
                                modifier = Modifier.height(60.dp),
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Center,
                                content = {
                                    Text(
                                        "Account",
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    getMainViewModel().accountSession.accountSession.processState.value.let {
                                        Text(
                                            when (it) {
                                                ProcessState.NotRunYet -> "Not logged in"
                                                ProcessState.Running -> "Fetching..."
                                                else -> getMainViewModel().accountSession.accountSession.data.value?.accountAuth?.username ?: "unknown"
                                            },
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            )
                        },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (getMainViewModel().accountSession.accountSession.processState.value == ProcessState.Failed) {
                                        Badge { Text("!") }
                                    }
                                },
                                content = {
                                    when (getMainViewModel().accountSession.accountSession.processState.value) {
                                        ProcessState.Running -> CircularProgressIndicator(
                                            modifier = Modifier.size(26.dp),
                                            strokeWidth = 3.dp
                                        )
                                        else -> Icon(
                                            Icons.Outlined.AccountCircle,
                                            "Account",
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }
                            )
                        },
                        onClick = { accountClicked?.let { it() } }
                    )
                }
            )
        },
        content = { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                color = Color.Transparent,
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        content = {
                            DateAndTimeSummaryItem(
                                padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
                                isLoading = getMainViewModel().currentSchoolWeek.processState.value == ProcessState.Running,
                                currentSchoolWeek = getMainViewModel().currentSchoolWeek.data.value,
                                opacity = getControlBackgroundAlpha()
                            )
                            LessonTodaySummaryItem(
                                padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
                                hasLoggedIn = getMainViewModel().accountSession.accountSession.processState.value == ProcessState.Successful,
                                isLoading = getMainViewModel().accountSession.accountSession.processState.value == ProcessState.Running || getMainViewModel().accountSession.subjectSchedule.processState.value == ProcessState.Running,
                                clicked = {
                                    getMainViewModel().accountSession.reLogin(
                                        onCompleted = {
                                            if (it) {
                                                val intent = Intent(context, AccountActivity::class.java)
                                                intent.action = "subject_schedule"
                                                context.startActivity(intent)
                                            }
                                        }
                                    )
                                },
                                affectedList = getMainViewModel().accountSession.subjectSchedule.data.filter { subSch ->
                                    subSch.subjectStudy.scheduleList.any { schItem -> schItem.dayOfWeek + 1 == CustomDateUtil.getCurrentDayOfWeek() } &&
                                            subSch.subjectStudy.scheduleList.any { schItem ->
                                                schItem.lesson.end >= CustomClock.getCurrent().toDUTLesson2().lesson
                                            }
                                }.toList(),
                                opacity = getControlBackgroundAlpha()
                            )
                            //                AffectedLessonsSummaryItem(
//                    padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
//                    hasLoggedIn = getMainViewModel().accountSession.value.processState == ProcessState.Successful,
//                    isLoading = getMainViewModel().accountSession.value.processState == ProcessState.Running || getMainViewModel().subjectSchedule.processState.value == ProcessState.Running,
//                    clicked = {},
//                    affectedList = arrayListOf("ie1i0921d - i029di12", "ie1i0921d - i029di12","ie1i0921d - i029di12","ie1i0921d - i029di12","ie1i0921d - i029di12"),
//                    opacity = getControlBackgroundAlpha()
//                )
                            SchoolNewsSummaryItem(
                                padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
                                newsToday = getNews(false),
                                newsThisWeek = getNews(true),
                                clicked = {
                                    context.startActivity(Intent(context, NewsActivity::class.java))
                                },
                                isLoading = getMainViewModel().newsGlobal.processState.value == ProcessState.Running,
                                opacity = getControlBackgroundAlpha()
                            )
                            UpdateAvailableSummaryItem(
                                padding = PaddingValues(bottom = 10.dp, start = 15.dp, end = 15.dp),
                                isLoading = false,
                                updateAvailable = false,
                                latestVersionString = "",
                                clicked = {
                                    openLink(
                                        url = "https://github.com/ZoeMeow1027/DutSchedule/releases",
                                        context = context,
                                        customTab = false,
                                    )
                                },
                                opacity = getControlBackgroundAlpha()
                            )
                        },
                    )
                }
            )
            NotificationHistoryBottomSheet(
                snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
                itemList = getMainViewModel().notificationHistory,
                visible = isNotificationOpened.value,
                sheetState = notificationModalBottomSheetState,
                onDismiss = { isNotificationOpened.value = false },
                onClearItem = {
                    val itemTemp = it.clone()
                    getMainViewModel().notificationHistory.remove(it)
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
                    if (needConfirmClearAllNotifications.value) {
                        needConfirmClearAllNotifications.value = false
                        showSnackBar(
                            text = "This action is undone! To confirm, click \"Clear all\" icon again.",
                            onDismiss = {
                                needConfirmClearAllNotifications.value = true
                            },
                            clearPrevious = true
                        )
                    } else {
                        needConfirmClearAllNotifications.value = true
                        getMainViewModel().notificationHistory.clear()
                        getMainViewModel().saveSettings()
                        showSnackBar(
                            text = "Successfully cleared all notifications!",
                            clearPrevious = true
                        )
                    }
                }
            )
        }
    )

    BackHandler(isNotificationOpened.value) {
        if (isNotificationOpened.value) {
            isNotificationOpened.value = false
        }
    }
}