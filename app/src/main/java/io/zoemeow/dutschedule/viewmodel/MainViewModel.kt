package io.zoemeow.dutschedule.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import io.dutwrapper.dutwrapper.Utils
import io.dutwrapper.dutwrapper.model.utils.DutSchoolYearItem
import io.zoemeow.dutschedule.model.NotificationHistory
import io.zoemeow.dutschedule.model.ProcessState
import io.zoemeow.dutschedule.model.VariableState
import io.zoemeow.dutschedule.model.account.AccountSession
import io.zoemeow.dutschedule.model.account.DUTAccountInstance
import io.zoemeow.dutschedule.model.news.DUTNewsInstance
import io.zoemeow.dutschedule.model.news.NewsFetchType
import io.zoemeow.dutschedule.model.settings.AppSettings
import io.zoemeow.dutschedule.repository.DutRequestRepository
import io.zoemeow.dutschedule.repository.FileModuleRepository
import io.zoemeow.dutschedule.utils.launchOnScope
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fileModuleRepository: FileModuleRepository,
    dutRequestRepository: DutRequestRepository,
) : ViewModel() {
    val appSettings: MutableState<AppSettings> = mutableStateOf(AppSettings())

    val accountSession: DUTAccountInstance = DUTAccountInstance(
        dutRequestRepository = dutRequestRepository,
        onEventSent = { eventId ->
            when (eventId) {
                1 -> {
                    Log.d("app", "triggered saved login")
                    saveApplicationSettings(saveAccountCache = true)
                }
                2, 3, 4, 5 -> {
                    // TODO: Save account cache here!
                    saveApplicationSettings(saveAccountCache = true)
                }
            }
        }
    )

    val newsInstance: DUTNewsInstance = DUTNewsInstance(
        dutRequestRepository = dutRequestRepository,
        onEventSent = {eventId ->
            when (eventId) {
                1, 2 -> {
                    Log.d("app", "triggered saved news")
                    saveApplicationSettings(saveNewsCache = true)
                }
            }
        }
    )

    val currentSchoolYearWeek = VariableState<DutSchoolYearItem?>(
        data = mutableStateOf(null)
    )

    private fun refreshCurrentSchoolYearWeek() {
        launchOnScope(
            script = {
                if (currentSchoolYearWeek.processState.value == ProcessState.Running) {
                    return@launchOnScope
                }
                currentSchoolYearWeek.processState.value = ProcessState.Running

                currentSchoolYearWeek.data.value = Utils.getCurrentSchoolWeek()
            },
            invokeOnCompleted = {
                when {
                    it != null -> {
                        currentSchoolYearWeek.processState.value = ProcessState.Failed
                    }
                    else -> {
                        currentSchoolYearWeek.processState.value = ProcessState.Successful
                        saveCurrentSchoolYearWeekCache()
                    }
                }
                currentSchoolYearWeek.lastRequest.longValue = System.currentTimeMillis()
            }
        )
    }

    private fun saveCurrentSchoolYearWeekCache() {
        fileModuleRepository.saveCurrentSchoolYearCache(
            data = currentSchoolYearWeek.data.value,
            lastRequest = currentSchoolYearWeek.lastRequest.longValue
        )
    }

    val notificationHistory = mutableStateListOf<NotificationHistory>()

    /**
     * Save all current settings to file in storage.
     */
    fun saveApplicationSettings(
        saveNewsCache: Boolean = false,
        saveAccountCache: Boolean = false,
        saveNotificationCache: Boolean = false,
        saveUserSettings: Boolean = false,
        onCompleted: (() -> Unit)? = null
    ) {
        launchOnScope(
            script = {
                if (saveNewsCache) {
                    newsInstance.exportNewsCache(
                        onDataExported = { newsGlobalItems, i, l, newsSubjectItems, i2, l2 ->
                            fileModuleRepository.saveCacheNewsGlobal(
                                newsList = newsGlobalItems,
                                newsNextPage = i,
                                lastRequest = l
                            )
                            fileModuleRepository.saveCacheNewsSubject(
                                newsList = newsSubjectItems,
                                newsNextPage = i2,
                                lastRequest = l2
                            )
                        }
                    )
                }
                if (saveAccountCache) {
                    fileModuleRepository.saveAccountSubjectScheduleCache(ArrayList(accountSession.getSubjectScheduleCache()))

                    // Reload school year in Account
                    accountSession.setSchoolYear(appSettings.value.currentSchoolYear)
                }
                if (saveNotificationCache) {
                    fileModuleRepository.saveNotificationHistory(ArrayList(notificationHistory.toList()))
                }
                if (saveUserSettings) {
                    fileModuleRepository.saveAppSettings(appSettings.value)
                    fileModuleRepository.saveAccountSession(accountSession.getAccountSession() ?: AccountSession())
                }
            },
            invokeOnCompleted = { onCompleted?.let { it() } }
        )
    }

    /**
     * Load all cache if possible for offline reading.
     */
    private fun loadApplicationSettings(
        onCompleted: (() -> Unit)? = null
    ) {
        launchOnScope(
            script = {
                // App settings
                appSettings.value = fileModuleRepository.getAppSettings()
                accountSession.setAccountSession(fileModuleRepository.getAccountSession())
                accountSession.setSchoolYear(schoolYearItem = appSettings.value.currentSchoolYear)

                // Notification cache
                notificationHistory.clear()
                notificationHistory.addAll(fileModuleRepository.getNotificationHistory())

                // Load all cache if possible for offline reading.
                // Global news cache
                fileModuleRepository.getCacheNewsGlobal { newsGlobalItems, i, lq ->
                    newsInstance.importNewsCache(newsGlobalItems, i, lq, null, null, null)
                }
                // Subject news cache
                fileModuleRepository.getCacheNewsSubject { newsSubjectItems, i, lq ->
                    newsInstance.importNewsCache(null, null, null, newsSubjectItems, i, lq)
                }
                // Subject schedule cache
                fileModuleRepository.getAccountSubjectScheduleCache().also {
                    accountSession.setSubjectScheduleCache(it)
                }
                // Get school year cache
                fileModuleRepository.getCurrentSchoolYearCache().also {
                    if (it != null) {
                        try {
                            currentSchoolYearWeek.data.value = Gson().fromJson(
                                it["data"] ?: "",
                                (object : TypeToken<DutSchoolYearItem?>() {}.type)
                            )
                            currentSchoolYearWeek.lastRequest.longValue = (it["lastrequest"] ?: "0").toLong()
                        } catch (_: Exception) { }
                    }
                }
            },
            invokeOnCompleted = { onCompleted?.let { it() } }
        )
    }

    private val _runOnStartup = mutableStateOf(false)
    init {
        if (!_runOnStartup.value) {
            loadApplicationSettings(
                onCompleted = {
                    accountSession.reLogin(force = true)
                    refreshCurrentSchoolYearWeek()

                    launchOnScope(script = {
                        newsInstance.fetchGlobalNews(
                            fetchType = NewsFetchType.FirstPage,
                            forceRequest = true
                        )
                        newsInstance.fetchSubjectNews(
                            fetchType = NewsFetchType.FirstPage,
                            forceRequest = true
                        )
                    })
                }
            )

            _runOnStartup.value = true
        }
    }
}