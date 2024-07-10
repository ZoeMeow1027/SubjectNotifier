package io.zoemeow.dutschedule.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.gson.Gson
import io.dutwrapper.dutwrapper.model.enums.LessonStatus
import io.dutwrapper.dutwrapper.model.enums.NewsType
import io.dutwrapper.dutwrapper.model.news.NewsGlobalItem
import io.dutwrapper.dutwrapper.model.news.NewsSubjectItem
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.activity.NewsActivity
import io.zoemeow.dutschedule.activity.PermissionsActivity
import io.zoemeow.dutschedule.model.NotificationHistory
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.news.DUTNewsInstance
import io.zoemeow.dutschedule.model.news.NewsFetchType
import io.zoemeow.dutschedule.model.settings.AppSettings
import io.zoemeow.dutschedule.model.settings.SubjectCode
import io.zoemeow.dutschedule.repository.DutRequestRepository
import io.zoemeow.dutschedule.repository.FileModuleRepository
import io.zoemeow.dutschedule.utils.CustomDateUtil
import io.zoemeow.dutschedule.utils.CustomDateUtil.Companion.dateUnixToString
import io.zoemeow.dutschedule.utils.CustomDateUtil.Companion.getCurrentDateAndTimeToString
import io.zoemeow.dutschedule.utils.NotificationsUtil
import io.zoemeow.dutschedule.utils.calcMD5

class NewsBackgroundUpdateService : BaseService(
    nNotifyId = "notification.id.service",
    nTitle = "News service is running",
    nContent = "A task is running to get news list from sv.dut.udn.vn. This might take a few minutes..."
) {
    private lateinit var file: FileModuleRepository
    private lateinit var dutRequestRepository: DutRequestRepository
    private lateinit var settings: AppSettings

    override fun onInitialize() {
        file = FileModuleRepository(this)
        settings = file.getAppSettings()
        dutRequestRepository = DutRequestRepository()
    }

    override fun doWorkBackground(intent: Intent?) {
        // Fetch action:
        // - 0: Fetch current news by page number and plus 1.
        // - 1: Get page 1 but keep cache and current page number.
        // - 2: Clear cache, reset and fetch page 1.
        // Apply for fetchglobal and fetchsubject.
        val fetchType = intent?.getIntExtra("news.service.variable.fetchtype", 0)
        // Page to fetch news.
        // Apply for fetchglobal and fetchsubject with fetch action 1, 2.
        // val page = intent?.getIntExtra("news.service.variable.page", 1)
        // Fetch full news?
        // Apply for fetchglobal, fetchsubject, fetchall and fetchallinbackground.
        // val fetchFullNews = intent?.getBooleanExtra("news.service.variable.fetchfullnews", false)

        // Schedule for next run
        // Apply for fetchglobal and fetchsubject with fetch action 1
        // val schedule = intent?.getBooleanExtra("news.service.variable.schedulenextrun", false) ?: false
        val schedule = settings.newsBackgroundDuration > 0

        // Notify?
        // 0: All, 1: News global only, 2: News subject only, 3: News global and news subject with filter.
        // val nofityType = intent?.getIntExtra("news.service.variable.notifytype", 0) ?: 0

        when (intent?.action) {
            "news.service.action.fetchallpage1background" -> {
                fetchNews {
                    // Schedule next run
                    if (schedule) {
                        scheduleNextRun()
                    }
                    stopSelf()
                }
//                fetchNewsGlobal(
//                    fetchType = NewsFetchType.FirstPage
//                )
//                fetchNewsSubject(
//                    fetchType = NewsFetchType.FirstPage
//                )
            }
            "news.service.action.fetchallpage1background.skipfirst" -> {
                // Do nothing

                // Schedule next run
                if (schedule) {
                    scheduleNextRun()
                    stopSelf()
                }
            }
            else -> {}
        }
    }

    private fun fetchNews(onDone: (() -> Unit)? = null) {
        var newsGlobalDone = false
        var newsSubjectDone = false
        var notifyGlobalDone = false
        var notifySubjectDone = false

        fun returnToMain() {
            if (newsGlobalDone && newsSubjectDone && notifyGlobalDone && notifySubjectDone) {
                // TODO: Return to main here
                onDone?.let { it() }
            }
        }

        // If no notification permission, news notification must be aborted to avoid exception
        val notificationPermission = PermissionsActivity.checkPermissionNotification(this).isGranted

        val dutNewsInstance = DUTNewsInstance(
            dutRequestRepository = dutRequestRepository,
            onEventSent = { when (it) {
                1 -> {
                    newsGlobalDone = true
                    returnToMain()
                }
                2 -> {
                    newsSubjectDone = true
                    returnToMain()
                }
            } }
        )

        // Load news cache
        file.getCacheNewsGlobal { newsList, page, lastRequest ->
            dutNewsInstance.importNewsCache(
                globalNewsList = newsList,
                globalNewsIndex = page,
                globalNewsLastRequest = lastRequest
            )
        }
        file.getCacheNewsSubject { newsList, page, lastRequest ->
            dutNewsInstance.importNewsCache(
                subjectNewsList = newsList,
                subjectNewsIndex = page,
                subjectNewsLastRequest = lastRequest
            )
        }

        // Fetch news and direct notify here!
        if (dutNewsInstance.newsGlobal.lastRequest.longValue + (settings.newsBackgroundDuration * 60 * 1000) > System.currentTimeMillis()) {
            // throw Exception("Request too fast. Try again later.")
            // TODO: Throw exception here
            Log.d("NewsBackgroundService", "Request too fast in news global. Try again later.")
            newsGlobalDone = true
            notifyGlobalDone = true
            returnToMain()
        } else {
            dutNewsInstance.fetchGlobalNews(
                fetchType = NewsFetchType.FirstPage,
                forceRequest = true,
                onDone = { newsList ->
                    Log.d("NewsBackgroundService", "News global count: ${newsList.size}")

                    // If we are not have news notifications permission, stop here
                    if (!notificationPermission) {
                        notifyGlobalDone = true
                        returnToMain()
                        return@fetchGlobalNews
                    }

                    // If user turned off news global notifications, stop here
                    if (!settings.newsBackgroundGlobalEnabled) {
                        notifyGlobalDone = true
                        returnToMain()
                        return@fetchGlobalNews
                    }

                    try {
                        newsList.forEach {
                            notifyNewsGlobal(this, it)
                        }
                        Log.d("NewsBackgroundService", "Done executing function in news global.")
                    } catch (ex: Exception) {
                        Log.w("NewsBackgroundService", "An error was occurred when executing function in news global.")
                        ex.printStackTrace()
                    }

                    file.saveCacheNewsGlobal(
                        newsList = dutNewsInstance.newsGlobal.data,
                        newsNextPage = (dutNewsInstance.newsGlobal.parameters["nextPage"] ?: "0").toInt(),
                        lastRequest = dutNewsInstance.newsGlobal.lastRequest.longValue
                    )
                    notifyGlobalDone = true
                    returnToMain()
                }
            )
        }

        if (dutNewsInstance.newsSubject.lastRequest.longValue + (settings.newsBackgroundDuration * 60 * 1000) > System.currentTimeMillis()) {
            // throw Exception("Request too fast. Try again later.")
            // TODO: Throw exception here
            Log.d("NewsBackgroundService", "Request too fast in news subject. Try again later.")
            newsSubjectDone = true
            notifySubjectDone = true
            returnToMain()
        } else {
            dutNewsInstance.fetchSubjectNews(
                fetchType = NewsFetchType.FirstPage,
                forceRequest = true,
                onDone = { newsList ->
                    Log.d("NewsBackgroundService", "News subject count: ${newsList.size}")

                    // If we are not have news notifications permission, stop here
                    if (!notificationPermission) {
                        notifySubjectDone = true
                        returnToMain()
                        return@fetchSubjectNews
                    }

                    // If user turned off news subject notifications, stop here
                    if (settings.newsBackgroundSubjectEnabled == -1) {
                        notifySubjectDone = true
                        returnToMain()
                        return@fetchSubjectNews
                    }

                    try {
                        newsList.forEach { newsItem ->
                            Log.d("NewsBackgroundService", "News subject index: ${newsList.indexOf(newsItem)}")

                            // Default value is false.
                            var notifyRequired = false
                            // If enabled news filter, do following.

                            // settings.newsBackgroundSubjectEnabled == 0 -> All news enabled
                            if (settings.newsBackgroundSubjectEnabled == 0) {
                                notifyRequired = true
                            }
                            // TODO: settings.newsBackgroundSubjectEnabled == 1 action
                            // settings.newsBackgroundSubjectEnabled == 2
                            else if (settings.newsBackgroundFilterList.any { source ->
                                    newsItem.affectedClass.any { targetGroup ->
                                        targetGroup.codeList.any { target ->
                                            source.isEquals(
                                                SubjectCode(
                                                    target.studentYearId,
                                                    target.classId,
                                                    targetGroup.subjectName
                                                )
                                            )
                                        }
                                    }
                                }
                            ) notifyRequired = true

                            // TODO: If no notify/notify settings is off, continue with return@forEach.
                            // notifyRequired and notify variable

                            if (notifyRequired) {
                                notifyNewsSubject(this, newsItem)
                            }
                        }
                        Log.d("NewsBackgroundService", "Done executing function in news subject.")
                    } catch (ex: Exception) {
                        Log.w("NewsBackgroundService", "An error was occurred when executing function in news subject.")
                        ex.printStackTrace()
                    }

                    file.saveCacheNewsSubject(
                        newsList = dutNewsInstance.newsSubject.data,
                        newsNextPage = (dutNewsInstance.newsSubject.parameters["nextPage"] ?: "0").toInt(),
                        lastRequest = dutNewsInstance.newsSubject.lastRequest.longValue
                    )
                    notifySubjectDone = true
                    returnToMain()
                }
            )
        }
    }

    private fun fetchNewsGlobal(
        fetchType: NewsFetchType = NewsFetchType.NextPage
    ) {
        val newsList: ArrayList<io.zoemeow.dutschedule.model.news.NewsGlobalItem> = arrayListOf()
        var newsIndex = 0
        var lastRequest = 0L
        try {
            // Get news cache
            file.getCacheNewsGlobal(
                onDataExported = { list, index, lq ->
                    newsList.clear()
                    newsList.addAll(list)
                    newsIndex = index
                    lastRequest = lq
                }
            )

            if (lastRequest + (settings.newsBackgroundDuration * 60 * 1000) > System.currentTimeMillis()) {
                throw Exception("Request too fast. Try again later.")
            }

            // Get news from internet
            val newsFromInternet = dutRequestRepository.getNewsGlobal(
                page = when (fetchType) {
                    NewsFetchType.NextPage -> newsIndex
                    NewsFetchType.FirstPage -> 1
                    NewsFetchType.ClearAndFirstPage -> 1
                }
            )

            // If requested clear old news
            if (fetchType == NewsFetchType.ClearAndFirstPage) {
                newsList.clear()
            }

            val notifyNews = arrayListOf<io.zoemeow.dutschedule.model.news.NewsGlobalItem>()

            // - Filter latest news into a variable
            // - Remove duplicated news
            // - Update news from server
            val latestNews = arrayListOf<io.zoemeow.dutschedule.model.news.NewsGlobalItem>()
            newsFromInternet.forEach { newsTargetItem ->
                val anyMatch = newsList.any { newsSourceItem ->
                    newsSourceItem.date == newsTargetItem.date
                            && newsSourceItem.title == newsTargetItem.title
                            && newsSourceItem.contentString == newsTargetItem.contentString
                }
                val anyNeedUpdated = newsList.any { newsSourceItem ->
                    newsSourceItem.date == newsTargetItem.date
                            && newsSourceItem.title == newsTargetItem.title
                }

                when {
                    // Ignore when entire match
                    anyMatch -> {}
                    // Update when match title
                    anyNeedUpdated -> {
                        newsList.first {newsSourceItem ->
                            newsSourceItem.date == newsTargetItem.date
                                    && newsSourceItem.title == newsTargetItem.title
                        }.update(newsTargetItem)
                        val newsTemp = io.zoemeow.dutschedule.model.news.NewsGlobalItem()
                        newsTemp.update(newsTargetItem)
                        notifyNews.add(newsTemp)
                    }
                    // Otherwise, add to latest news collection
                    else -> {
                        val newsTemp = io.zoemeow.dutschedule.model.news.NewsGlobalItem()
                        newsTemp.update(newsTargetItem)
                        latestNews.add(newsTemp)
                        notifyNews.add(newsTemp)
                    }
                }
            }

            // Reverse latest news collection
            // Add all news in latestNews to global variable
            if (fetchType == NewsFetchType.FirstPage) {
                latestNews.reverse()
                latestNews.forEach { newsList.add(0, it) }
            } else {
                newsList.addAll(latestNews)
            }

            // Adjust index
            newsIndex = when (fetchType) {
                NewsFetchType.NextPage -> newsIndex + 1
                NewsFetchType.FirstPage -> if (newsIndex > 1) newsIndex else 2
                NewsFetchType.ClearAndFirstPage -> 2
            }

            lastRequest = System.currentTimeMillis()

            file.saveCacheNewsGlobal(newsList, newsIndex, lastRequest)

            // Check if any news need to be notify here using newsFiltered!
            // If no notification permission, aborting...
            if (!PermissionsActivity.checkPermissionNotification(this).isGranted) {
                return
            }

            // If user denied, no extra action needed
            if (!settings.newsBackgroundGlobalEnabled) {
                return
            }

            // TODO: Notify by notify variable...

            // Processing news global notifications for notify here!
            notifyNews.forEach {
                notifyNewsGlobal(this, it)
            }
            Log.d("NewsBackgroundService", "Done executing function in news global.")
        } catch (ex: Exception) {
            Log.w("NewsBackgroundService", "An error was occurred when executing function in news global.")
            ex.printStackTrace()
        }
    }

    private fun fetchNewsSubject(
        fetchType: NewsFetchType = NewsFetchType.NextPage
    ) {
        val newsList: ArrayList<io.zoemeow.dutschedule.model.news.NewsSubjectItem> = arrayListOf()
        var newsIndex = 0
        var lastRequest = 0L
        try {
            // Get news cache
            file.getCacheNewsSubject(
                onDataExported = { list, index, lq ->
                    newsList.clear()
                    newsList.addAll(list)
                    newsIndex = index
                    lastRequest = lq
                }
            )

            if (lastRequest + (settings.newsBackgroundDuration * 60 * 1000) > System.currentTimeMillis()) {
                throw Exception("Request too fast. Try again later.")
            }

            // Get news from internet
            val newsFromInternet = dutRequestRepository.getNewsSubject(
                page = when (fetchType) {
                    NewsFetchType.NextPage -> newsIndex
                    NewsFetchType.FirstPage -> 1
                    NewsFetchType.ClearAndFirstPage -> 1
                }
            )

            // If requested clear old news
            if (fetchType == NewsFetchType.ClearAndFirstPage) {
                newsList.clear()
            }

            val notifyNews = arrayListOf<io.zoemeow.dutschedule.model.news.NewsSubjectItem>()

            // - Filter latest news into a variable
            // - Remove duplicated news
            // - Update news from server
            val latestNews = arrayListOf<io.zoemeow.dutschedule.model.news.NewsSubjectItem>()
            newsFromInternet.forEach { newsTargetItem ->
                val anyMatch = newsList.any { newsSourceItem ->
                    newsSourceItem.date == newsTargetItem.date
                            && newsSourceItem.title == newsTargetItem.title
                            && newsSourceItem.contentString == newsTargetItem.contentString
                }
                val anyNeedUpdated = newsList.any { newsSourceItem ->
                    newsSourceItem.date == newsTargetItem.date
                            && newsSourceItem.title == newsTargetItem.title
                }

                when {
                    // Ignore when entire match
                    anyMatch -> {}
                    // Update when match title
                    anyNeedUpdated -> {
                        newsList.first {newsSourceItem ->
                            newsSourceItem.date == newsTargetItem.date
                                    && newsSourceItem.title == newsTargetItem.title
                        }.update(newsTargetItem)
                        val newsTemp = io.zoemeow.dutschedule.model.news.NewsSubjectItem()
                        newsTemp.update(newsTargetItem)
                        notifyNews.add(newsTemp)
                    }
                    // Otherwise, add to latest news collection
                    else -> {
                        val newsTemp = io.zoemeow.dutschedule.model.news.NewsSubjectItem()
                        newsTemp.update(newsTargetItem)
                        latestNews.add(newsTemp)
                        notifyNews.add(newsTemp)
                    }
                }
            }

            if (fetchType == NewsFetchType.FirstPage) {
                latestNews.reverse()
                latestNews.forEach { newsList.add(0, it) }
            } else {
                newsList.addAll(latestNews)
            }

            // Adjust index
            newsIndex = when (fetchType) {
                NewsFetchType.NextPage -> newsIndex + 1
                NewsFetchType.FirstPage -> if (newsIndex > 1) newsIndex else 2
                NewsFetchType.ClearAndFirstPage -> 2
            }

            lastRequest = System.currentTimeMillis()

            file.saveCacheNewsSubject(newsList, newsIndex, lastRequest)

            // Check if any news need to be notify here using newsFiltered!
            // If no notification permission, aborting...
            if (!PermissionsActivity.checkPermissionNotification(this).isGranted) {
                return
            }

            // If user denied, no extra action needed
            if (settings.newsBackgroundSubjectEnabled == -1) {
                return
            }

            // TODO: Notify by notify variable...

            // TODO: Processing news subject notifications for notify here!
            notifyNews.forEach { newsItem ->
                // Default value is false.
                var notifyRequired = false
                // If enabled news filter, do following.

                // settings.newsBackgroundSubjectEnabled == 0 -> All news enabled
                if (settings.newsBackgroundSubjectEnabled == 0) {
                    notifyRequired = true
                }
                // TODO: settings.newsBackgroundSubjectEnabled == 1 action
                // settings.newsBackgroundSubjectEnabled == 2
                else if (settings.newsBackgroundFilterList.any { source ->
                        newsItem.affectedClass.any { targetGroup ->
                            targetGroup.codeList.any { target ->
                                source.isEquals(
                                    SubjectCode(
                                        target.studentYearId,
                                        target.classId,
                                        targetGroup.subjectName
                                    )
                                )
                            }
                        }
                    }
                ) notifyRequired = true

                // TODO: If no notify/notify settings is off, continue with return@forEach.
                // notifyRequired and notify variable

                if (notifyRequired) {
                    notifyNewsSubject(this, newsItem)
                }
            }
            Log.d("NewsBackgroundService", "Done executing function in news subject.")
        } catch (ex: Exception) {
            Log.w("NewsBackgroundService", "An error was occurred when executing function in news subject.")
            ex.printStackTrace()
        }
    }

    private fun notifyNewsGlobal(
        context: Context,
        newsItem: NewsGlobalItem
    ) {
        // Add to notification list
        addToNotificationList(
            title = newsItem.title,
            description = newsItem.contentString,
            newsDate = newsItem.date,
            type = NewsType.Global,
            jsonData = Gson().toJson(newsItem)
        )

        // Notify here
        NotificationsUtil.showNewsNotification(
            context = context,
            channelId = "notification.id.news.global",
            newsMD5 = "${newsItem.date}_${newsItem.title}".calcMD5(),
            newsTitle = context.getString(R.string.service_newsbackgroundservice_newsglobal_title),
            newsDescription = newsItem.title,
            jsonData = Gson().toJson(newsItem)
        )
    }

    private fun notifyNewsSubject(
        context: Context,
        newsItem: NewsSubjectItem
    ) {
        if (settings.newsBackgroundParseNewsSubject) {
            // Affected classrooms
            var affectedClassrooms = ""
            newsItem.affectedClass.forEach { className ->
                if (affectedClassrooms.isEmpty()) {
                    affectedClassrooms = className.subjectName
                } else {
                    affectedClassrooms += ", ${className.subjectName}"
                }
                var first = true
                for (item in className.codeList) {
                    if (first) {
                        affectedClassrooms += " ("
                        first = false
                    } else {
                        affectedClassrooms += ", "
                    }
                    affectedClassrooms += "${item.studentYearId}.${item.classId}"
                }
                affectedClassrooms += ")"
            }

            // Title will make announcement about lecturer and subjects
            val notifyTitle = when (newsItem.lessonStatus) {
                LessonStatus.Leaving -> {
                    context.getString(
                        R.string.service_newsbackgroundservice_newssubject_title_noannouncement,
                        context.getString(R.string.service_newsbackgroundservice_newssubject_title_noannouncement_leaving),
                        newsItem.lecturerName,
                        affectedClassrooms
                    )
                }
                LessonStatus.MakeUp -> {
                    context.getString(
                        R.string.service_newsbackgroundservice_newssubject_title_noannouncement,
                        context.getString(R.string.service_newsbackgroundservice_newssubject_title_noannouncement_makeup),
                        newsItem.lecturerName,
                        affectedClassrooms
                    )
                }
                else -> {
                    context.getString(
                        R.string.service_newsbackgroundservice_newssubject_title_announcement,
                        newsItem.lecturerName,
                        affectedClassrooms
                    )
                }
            }

            val notifyContentList = arrayListOf<String>()
            // Date and lessons
            if (
                newsItem.lessonStatus == LessonStatus.Leaving ||
                newsItem.lessonStatus == LessonStatus.MakeUp
            ) {
                // Date & lessons
                notifyContentList.add(
                    context.getString(
                        R.string.service_newsbackgroundservice_newssubject_date,
                        CustomDateUtil.dateUnixToString(newsItem.affectedDate, "dd/MM/yyyy"),
                        if (newsItem.affectedLesson != null) newsItem.affectedLesson.toString() else context.getString(R.string.service_newsbackgroundservice_newssubject_lessonunknown)
                    )
                )
                // Make-up room
                if (newsItem.lessonStatus == LessonStatus.MakeUp) {
                    // Make up in room
                    notifyContentList.add(
                        context.getString(
                            R.string.service_newsbackgroundservice_newssubject_room,
                            newsItem.affectedRoom
                        )
                    )
                }
            } else {
                notifyContentList.add(newsItem.contentString)
            }

            // TODO: Add to notification list - Disabled due to not excluding here
//            addToNotificationList(
//                title = notifyTitle,
//                description = notifyContentList.joinToString("\n"),
//                newsDate = System.currentTimeMillis(),
//                type = NewsType.Subject,
//                jsonData = Gson().toJson(newsItem)
//            )

            // Notify here
            NotificationsUtil.showNewsNotification(
                context = context,
                channelId = "notification.id.news.subject",
                newsMD5 = "${newsItem.date}_${newsItem.title}".calcMD5(),
                newsTitle = notifyTitle,
                newsDescription = notifyContentList.joinToString("\n"),
                jsonData = Gson().toJson(newsItem)
            )
        } else {
            // TODO: Add to notification list - Disabled due to not excluding here
//            addToNotificationList(
//                title = newsItem.title,
//                description = newsItem.contentString,
//                newsDate = System.currentTimeMillis(),
//                type = NewsType.Subject,
//                jsonData = Gson().toJson(newsItem)
//            )

            // Notify here
            NotificationsUtil.showNewsNotification(
                context = context,
                channelId = "notification.id.news.subject",
                newsMD5 = "${newsItem.date}_${newsItem.title}".calcMD5(),
                newsTitle = newsItem.title,
                newsDescription = newsItem.contentString,
                jsonData = Gson().toJson(newsItem)
            )
        }
    }

    private fun addToNotificationList(
        title: String,
        description: String,
        newsDate: Long,
        type: NewsType,
        jsonData: String
    ) {
        // Load notification history
        val cache = file.getNotificationHistory()

        // Create and add to list
        val item = NotificationHistory(
            title = title,
            description = description,
            tag = when (type) {
                NewsType.Global -> 1
                NewsType.Subject -> 2
                else -> 0
            },
            timestamp = newsDate,
            parameters = mapOf(
                "type" to when (type) {
                    NewsType.Global -> NewsActivity.NEWSTYPE_NEWSGLOBAL
                    NewsType.Subject -> NewsActivity.NEWSTYPE_NEWSSUBJECT
                    else -> ""
                },
                "data" to jsonData
            )
        )
        cache.add(item)

        // Save notification history after add
        file.saveNotificationHistory(cache)

        // Optimal: Clear list
        cache.clear()
    }

    override fun onCompleted(result: ProcessState) {
        // TODO: We need another process result here!
        // stopSelf()
    }

    override fun onDestroying() { }

    private fun scheduleNextRun() {
        val pendingIntent = getPendingIntentForBackground(this)
        super.scheduleNextRun(
            intervalInMinute = settings.newsBackgroundDuration,
            scheduleStart = null,
            scheduleEnd = null,
            pendingIntent = pendingIntent,
            onDone = {
                Log.d(
                    "NewsBackgroundService",
                    String.format(
                        "Scheduled service to run at %s. Next run: %s.",
                        getCurrentDateAndTimeToString("dd/MM/yyyy HH:mm:ss"),
                        dateUnixToString(
                            System.currentTimeMillis() + settings.newsBackgroundDuration * 60 * 1000,
                            "dd/MM/yyyy HH:mm:ss",
                            "GMT+7"
                        )
                    )
                )
            }
        )
    }

    companion object {
        fun getPendingIntentForBackground(context: Context): PendingIntent {
            val intent = Intent(context, NewsBackgroundUpdateService::class.java).also {
                it.action = "news.service.action.fetchallpage1background"
            }
            val pendingIntent: PendingIntent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                pendingIntent = PendingIntent.getForegroundService(
                    context,
                    1234,
                    intent,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.FLAG_IMMUTABLE
                    }
                    else PendingIntent.FLAG_UPDATE_CURRENT
                )
            } else {
                pendingIntent = PendingIntent.getService(
                    context,
                    1234,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

            return pendingIntent
        }

        fun cancelSchedule(
            context: Context,
            onDone: (() -> Unit)? = null
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(getPendingIntentForBackground(context))
            onDone?.let { it() }
        }
    }
}