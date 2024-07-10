package io.zoemeow.dutschedule.model.news

import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.VariableListState
import io.zoemeow.dutschedule.repository.DutRequestRepository
import io.zoemeow.dutschedule.utils.launchOnScope

/**
 * @param onEventSent Event when done:
 * 1: Done global news
 * 2: Done subject news
 */
class DUTNewsInstance(
    private val dutRequestRepository: DutRequestRepository,
    private val onEventSent: ((Int) -> Unit)? = null
) {
    val newsGlobal: VariableListState<NewsGlobalItem> = VariableListState(
        parameters = mutableMapOf("nextPage" to "1")
    )

    val newsSubject: VariableListState<NewsSubjectItem> = VariableListState(
        parameters = mutableMapOf("nextPage" to "1")
    )

    fun importNewsCache(
        globalNewsList: List<NewsGlobalItem>? = null,
        globalNewsIndex: Int? = null,
        globalNewsLastRequest: Long? = null,
        subjectNewsList: List<NewsSubjectItem>? = null,
        subjectNewsIndex: Int? = null,
        subjectNewsLastRequest: Long? = null
        ) {
        if (globalNewsList != null && globalNewsIndex != null) {
            newsGlobal.let {
                it.data.clear()
                it.data.addAll(globalNewsList)
                it.parameters["nextPage"] = globalNewsIndex.toString()
            }
        }
        globalNewsLastRequest?.let { newsGlobal.lastRequest.longValue = it }
        if (subjectNewsList != null && subjectNewsIndex != null) {
            newsSubject.let {
                it.data.clear()
                it.data.addAll(subjectNewsList)
                it.parameters["nextPage"] = subjectNewsIndex.toString()
            }
        }
        subjectNewsLastRequest?.let { newsSubject.lastRequest.longValue = it }
    }

    fun exportNewsCache(
        onDataExported: (
            List<NewsGlobalItem>,
            Int,
            Long,
            List<NewsSubjectItem>,
            Int,
            Long
        ) -> Unit
    ) {
        onDataExported(
            newsGlobal.data,
            newsGlobal.parameters["nextPage"]?.toIntOrNull() ?: 1,
            newsGlobal.lastRequest.longValue,
            newsSubject.data,
            newsSubject.parameters["nextPage"]?.toIntOrNull() ?: 1,
            newsSubject.lastRequest.longValue
        )
    }

    fun fetchGlobalNews(
        fetchType: NewsFetchType = NewsFetchType.NextPage,
        forceRequest: Boolean = true,
        onDone: ((List<NewsGlobalItem>) -> Unit)? = null
    ) {
        if (!newsGlobal.isSuccessfulRequestExpired() && !forceRequest) {
            return
        }
        if (newsGlobal.processState.value == ProcessState.Running) {
            return
        }
        newsGlobal.processState.value = ProcessState.Running

        val latestNews = arrayListOf<NewsGlobalItem>()
        launchOnScope(
            script = {
                // Get news from internet
                val newsFromInternet = dutRequestRepository.getNewsGlobal(
                    page = when (fetchType) {
                        NewsFetchType.NextPage -> newsGlobal.parameters["nextPage"]?.toIntOrNull() ?: 1
                        NewsFetchType.FirstPage -> 1
                        NewsFetchType.ClearAndFirstPage -> 1
                    }
                )

                // If requested clear old news
                if (fetchType == NewsFetchType.ClearAndFirstPage) {
                    newsGlobal.data.clear()
                }

                // - Filter latest news into a variable
                // - Remove duplicated news
                // - Update news from server
                newsFromInternet.forEach { newsTargetItem ->
                    val anyMatch = newsGlobal.data.any { newsSourceItem ->
                        newsSourceItem.date == newsTargetItem.date
                                && newsSourceItem.title == newsTargetItem.title
                                && newsSourceItem.contentString == newsTargetItem.contentString
                    }
                    val anyNeedUpdated = newsGlobal.data.any { newsSourceItem ->
                        newsSourceItem.date == newsTargetItem.date
                                && newsSourceItem.title == newsTargetItem.title
                    }

                    when {
                        // Ignore when entire match
                        anyMatch -> {}
                        // Update when match title
                        anyNeedUpdated -> {
                            newsGlobal.data.first {newsSourceItem ->
                                newsSourceItem.date == newsTargetItem.date
                                        && newsSourceItem.title == newsTargetItem.title
                            }.update(newsTargetItem)
                        }
                        // Otherwise, add to latest news collection
                        else -> {
                            val newsTemp = NewsGlobalItem()
                            newsTemp.update(newsTargetItem)
                            latestNews.add(newsTemp)
                        }
                    }
                }

                // Reverse latest news collection
                // Add all news in latestNews to global variable
                if (fetchType == NewsFetchType.FirstPage) {
                    latestNews.reverse()
                    latestNews.forEach { newsGlobal.data.add(0, it) }
                } else {
                    newsGlobal.data.addAll(latestNews)
                }

                // Adjust index
                newsGlobal.parameters.let {
                    when (fetchType) {
                        NewsFetchType.NextPage -> {
                            it["nextPage"] = ((it["nextPage"]?.toIntOrNull() ?: 1) + 1).toString()
                        }
                        NewsFetchType.FirstPage -> {
                            it["nextPage"] = (it["nextPage"]?.toIntOrNull() ?: 1).toString()
                        }
                        NewsFetchType.ClearAndFirstPage -> {
                            it["nextPage"] = 2.toString()
                        }
                    }
                }
            },
            invokeOnCompleted = {
                onDone?.let { it(latestNews) }
                newsGlobal.lastRequest.longValue = System.currentTimeMillis()
                newsGlobal.processState.value = when {
                    it == null -> ProcessState.Successful
                    else -> ProcessState.Failed
                }
                onEventSent?.let { it(1) }
            }
        )
    }

    fun fetchSubjectNews(
        fetchType: NewsFetchType = NewsFetchType.NextPage,
        forceRequest: Boolean = true,
        onDone: ((List<NewsSubjectItem>) -> Unit)? = null
    ) {
        if (!newsSubject.isSuccessfulRequestExpired() && !forceRequest) {
            return
        }
        if (newsSubject.processState.value == ProcessState.Running) {
            return
        }
        newsSubject.processState.value = ProcessState.Running

        val latestNews = arrayListOf<NewsSubjectItem>()
        launchOnScope(
            script = {
                // Get news from internet
                val newsFromInternet = dutRequestRepository.getNewsSubject(
                    page = when (fetchType) {
                        NewsFetchType.NextPage -> newsSubject.parameters["nextPage"]?.toIntOrNull() ?: 1
                        NewsFetchType.FirstPage -> 1
                        NewsFetchType.ClearAndFirstPage -> 1
                    }
                )

                // If requested clear old news
                if (fetchType == NewsFetchType.ClearAndFirstPage) {
                    newsSubject.data.clear()
                }

                // - Filter latest news into a variable
                // - Remove duplicated news
                // - Update news from server
                newsFromInternet.forEach { newsTargetItem ->
                    val anyMatch = newsSubject.data.any { newsSourceItem ->
                        newsSourceItem.date == newsTargetItem.date
                                && newsSourceItem.title == newsTargetItem.title
                                && newsSourceItem.contentString == newsTargetItem.contentString
                    }
                    val anyNeedUpdated = newsSubject.data.any { newsSourceItem ->
                        newsSourceItem.date == newsTargetItem.date
                                && newsSourceItem.title == newsTargetItem.title
                    }

                    when {
                        // Ignore when entire match
                        anyMatch -> {}
                        // Update when match title
                        anyNeedUpdated -> {
                            newsSubject.data.first {newsSourceItem ->
                                newsSourceItem.date == newsTargetItem.date
                                        && newsSourceItem.title == newsTargetItem.title
                            }.update(newsTargetItem)
                        }
                        // Otherwise, add to latest news collection
                        else -> {
                            val newsTemp = NewsSubjectItem()
                            newsTemp.update(newsTargetItem)
                            latestNews.add(newsTemp)
                        }
                    }
                }

                // Reverse latest news collection
                // Add all news in latestNews to global variable
                if (fetchType == NewsFetchType.FirstPage) {
                    latestNews.reverse()
                    latestNews.forEach { newsSubject.data.add(0, it) }
                } else {
                    newsSubject.data.addAll(latestNews)
                }

                // Adjust index
                newsSubject.parameters.let {
                    when (fetchType) {
                        NewsFetchType.NextPage -> {
                            it["nextPage"] = ((it["nextPage"]?.toIntOrNull() ?: 1) + 1).toString()
                        }
                        NewsFetchType.FirstPage -> {
                            it["nextPage"] = (if ((it["nextPage"]?.toIntOrNull() ?: 1) == 1) 2 else (it["nextPage"]?.toIntOrNull() ?: 2)).toString()
                        }
                        NewsFetchType.ClearAndFirstPage -> {
                            it["nextPage"] = 2.toString()
                        }
                    }
                }
            },
            invokeOnCompleted = {
                onDone?.let { it(latestNews) }
                newsSubject.lastRequest.longValue = System.currentTimeMillis()
                newsSubject.processState.value = when {
                    it == null -> ProcessState.Successful
                    else -> ProcessState.Failed
                }
                onEventSent?.let { it(2) }
            }
        )
    }
}