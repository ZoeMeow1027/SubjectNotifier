package io.zoemeow.dutschedule.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.model.settings.BackgroundImageOption
import io.zoemeow.dutschedule.ui.view.settings.Activity_Settings
import io.zoemeow.dutschedule.ui.view.settings.Activity_Settings_AboutApplication
import io.zoemeow.dutschedule.ui.view.settings.Activity_Settings_AppLanguageSettings
import io.zoemeow.dutschedule.ui.view.settings.Activity_Settings_ExperimentSettings
import io.zoemeow.dutschedule.ui.view.settings.Activity_Settings_NewsNotificationSettings
import io.zoemeow.dutschedule.ui.view.settings.Activity_Settings_ParseNewsSubjectNotification
import io.zoemeow.dutschedule.ui.view.settings.Activity_Settings_WallpaperAndControlsSettings
import io.zoemeow.dutschedule.utils.BackgroundImageUtil
import io.zoemeow.dutschedule.utils.NotificationsUtil
import io.zoemeow.dutschedule.utils.openLink

@AndroidEntryPoint
class SettingsActivity : BaseActivity() {
    companion object {
        const val INTENT_PARSENEWSSUBJECTNOTIFICATION = "settings_newssubjectnewparse"
        const val INTENT_EXPERIMENTSETTINGS = "settings_experimentsettings"
        const val INTENT_LANGUAGESETTINGS = "settings_languagesettings"
        const val INTENT_NEWSNOTIFICATIONSETTINGS = "settings_newsnotificaitonsettings"
        const val INTENT_WALLPAPERANDCONTROLSSETTINGS = "settings_wallpaperandcontrolssettings"
        const val INTENT_ABOUTACTIVITY = "settings_about"
    }

    @Composable
    override fun OnPreloadOnce() { }

    override fun onResume() {
        super.onResume()
        NotificationsUtil.initializeNotificationChannel(this)
    }

    // When active
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: $uri")
                getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                    backgroundImage = BackgroundImageOption.None
                )
                Log.d("PhotoPicker", "Switched to None to avoid cache!")
                BackgroundImageUtil.saveImageToAppData(this, uri)
                Log.d("PhotoPicker", "Copied image to app data!")
                getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                    backgroundImage = BackgroundImageOption.PickFileFromMedia
                )
                Log.d("PhotoPicker", "Switched to PickFileFromMedia!")
                getMainViewModel().saveApplicationSettings(
                    saveUserSettings = true,
                    onCompleted = {
                        Log.d("PhotoPicker", "Done saving settings!")
                    }
                )
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    @Composable
    override fun OnMainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        appearanceState: AppearanceState
    ) {
        when (intent.action) {
            INTENT_PARSENEWSSUBJECTNOTIFICATION -> {
                Activity_Settings_ParseNewsSubjectNotification(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    isEnabled = getMainViewModel().appSettings.value.newsBackgroundParseNewsSubject,
                    onChange = {
                        getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                            newsBackgroundParseNewsSubject = !getMainViewModel().appSettings.value.newsBackgroundParseNewsSubject
                        )
                        getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }

            INTENT_EXPERIMENTSETTINGS -> {
                Activity_Settings_ExperimentSettings(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    mainViewModel = getMainViewModel(),
                    onMessageReceived = { msg, forceDismissBefore, actionText, action ->
                        showSnackBar(
                            text = msg,
                            clearPrevious = forceDismissBefore,
                            actionText = actionText,
                            action = action
                        )
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }

            INTENT_LANGUAGESETTINGS -> {
                Activity_Settings_AppLanguageSettings(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    onNotificationRegister = {
                        NotificationsUtil.initializeNotificationChannel(this)
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }

            INTENT_NEWSNOTIFICATIONSETTINGS -> {
                Activity_Settings_NewsNotificationSettings(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    },
                    fetchNewsInBackgroundDuration = getMainViewModel().appSettings.value.newsBackgroundDuration,
                    onFetchNewsStateChanged = { duration ->
                        if (duration > 0) {
                            if (PermissionsActivity.checkPermissionScheduleExactAlarm(context).isGranted && PermissionsActivity.checkPermissionNotification(context).isGranted) {
                                // Fetch news in background onClick
                                val dataTemp = getMainViewModel().appSettings.value.clone(
                                    fetchNewsBackgroundDuration = duration
                                )
                                getMainViewModel().appSettings.value = dataTemp
                                getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                                showSnackBar(
                                    text = context.getString(
                                        R.string.settings_newsnotify_fetchnewsinbackground_enabled,
                                        duration
                                    ),
                                    clearPrevious = true
                                )
                            } else {
                                showSnackBar(
                                    text = context.getString(R.string.settings_newsnotify_snackbar_missingpermissions),
                                    clearPrevious = true,
                                    actionText = context.getString(R.string.action_grant),
                                    action = {
                                        Intent(context, PermissionsActivity::class.java).also { intent ->
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                            }
                        } else {
                            val dataTemp = getMainViewModel().appSettings.value.clone(
                                fetchNewsBackgroundDuration = 0
                            )
                            getMainViewModel().appSettings.value = dataTemp
                            getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                            showSnackBar(
                                text = context.getString(R.string.settings_newsnotify_fetchnewsinbackground_disabled),
                                clearPrevious = true
                            )
                        }
                    },
                    isNewSubjectNotificationParseEnabled = getMainViewModel().appSettings.value.newsBackgroundParseNewsSubject,
                    onNewSubjectNotificationParseClick = {
                        Intent(context, SettingsActivity::class.java).apply {
                            action = INTENT_PARSENEWSSUBJECTNOTIFICATION
                        }.also { intent -> context.startActivity(intent) }
                    },
                    isNewsGlobalEnabled = getMainViewModel().appSettings.value.newsBackgroundGlobalEnabled,
                    onNewsGlobalStateChanged = { enabled ->
                        val dataTemp = getMainViewModel().appSettings.value.clone(
                            newsBackgroundGlobalEnabled = enabled
                        )
                        getMainViewModel().appSettings.value = dataTemp
                        getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                        showSnackBar(
                            text = when (enabled) {
                                true -> context.getString(R.string.settings_newsnotify_newsglobal_enabled)
                                false -> context.getString(R.string.settings_newsnotify_newsglobal_disabled)
                            },
                            clearPrevious = true
                        )
                    },
                    isNewsSubjectEnabled = getMainViewModel().appSettings.value.newsBackgroundSubjectEnabled,
                    onNewsSubjectStateChanged = f@ { code ->
                        if (code == 1) {
                            showSnackBar(
                                text = "\"Match your subject schedule\" option is in development. Check back soon.",
                                clearPrevious = true
                            )
                            return@f
                        }

                        val dataTemp = getMainViewModel().appSettings.value.clone(
                            newsBackgroundSubjectEnabled = code
                        )
                        getMainViewModel().appSettings.value = dataTemp
                        getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                        @Suppress("KotlinConstantConditions")
                        showSnackBar(
                            text = when (code) {
                                -1 -> context.getString(R.string.settings_newsnotify_newssubject_notify_disabled)
                                0 -> context.getString(R.string.settings_newsnotify_newssubject_notify_all)
                                // TODO: Implement this branch "Match your schedule" to avoid issue
                                1 -> context.getString(R.string.settings_newsnotify_newssubject_notify_matchsubsch)
                                2 -> context.getString(R.string.settings_newsnotify_newssubject_notify_matchfilter)
                                // TODO: No code valid
                                else -> "----------"
                            },
                            clearPrevious = true
                        )
                    },
                    subjectFilterList = getMainViewModel().appSettings.value.newsBackgroundFilterList,
                    onSubjectFilterAdd = { subjectCode ->
                        // Add a filter
                        try {
                            getMainViewModel().appSettings.value.newsBackgroundFilterList.add(subjectCode)
                            getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                            showSnackBar(
                                text = context.getString(
                                    R.string.settings_newsnotify_newsfilter_notify_add,
                                    subjectCode.subjectName,
                                    subjectCode.studentYearId,
                                    ".Nh",
                                    subjectCode.classId
                                ),
                                clearPrevious = true
                            )
                        } catch (_: Exception) { }
                    },
                    onSubjectFilterDelete = { subjectCode ->
                        // Delete a filter
                        try {
                            val data = subjectCode.copy()
                            getMainViewModel().appSettings.value.newsBackgroundFilterList.remove(subjectCode)
                            getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                            showSnackBar(
                                text = context.getString(
                                    R.string.settings_newsnotify_newsfilter_notify_delete,
                                    data.subjectName,
                                    data.studentYearId,
                                    ".Nh",
                                    data.classId
                                ),
                                clearPrevious = true
                            )
                        } catch (_: Exception) { }
                    },
                    onSubjectFilterClear = {
                        // Delete all filters
                        try {
                            getMainViewModel().appSettings.value.newsBackgroundFilterList.clear()
                            getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                            showSnackBar(
                                text = context.getString(R.string.settings_newsnotify_newsfilter_notify_deleteall),
                                clearPrevious = true
                            )
                        } catch (_: Exception) { }
                    }
                )
            }

            INTENT_WALLPAPERANDCONTROLSSETTINGS -> {
                Activity_Settings_WallpaperAndControlsSettings(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    },
                    valueBackgroundState = getMainViewModel().appSettings.value.backgroundImage,
                    onValueBackgroundStateChanged = {
                        // We need to manually processing here.
                        when (it) {
                            BackgroundImageOption.None -> {
                                getMainViewModel().appSettings.value =
                                    getMainViewModel().appSettings.value.clone(
                                        backgroundImage = it
                                    )
                            }

                            BackgroundImageOption.YourCurrentWallpaper -> {
                                val compPer =
                                    PermissionsActivity.checkPermissionManageExternalStorage(context = context).isGranted
                                if (compPer) {
                                    getMainViewModel().appSettings.value =
                                        getMainViewModel().appSettings.value.clone(
                                            backgroundImage = it
                                        )
                                } else {
                                    showSnackBar(
                                        text = context.getString(R.string.permission_missing_all_file_access),
                                        clearPrevious = true,
                                        actionText = context.getString(R.string.action_grant)
                                    ) {
                                        Intent(context, PermissionsActivity::class.java).also { intent ->
                                            context.startActivity(intent)
                                        }
                                    }
                                }
                            }

                            BackgroundImageOption.PickFileFromMedia -> {
                                // Launch the photo picker and let the user choose only images.
                                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        }
                    },
                    valueBackgroundOpacity = getMainViewModel().appSettings.value.backgroundImageOpacity,
                    onValueBackgroundOpacityChanged = {
                        if (getMainViewModel().appSettings.value.backgroundImage != BackgroundImageOption.None) {
                            getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                                backgroundImageOpacity = it
                            )
                            getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                        }
                    },
                    valueComponentOpacity = getMainViewModel().appSettings.value.componentOpacity,
                    onValueComponentOpacityChanged = {
                        if (getMainViewModel().appSettings.value.backgroundImage != BackgroundImageOption.None) {
                            getMainViewModel().appSettings.value = getMainViewModel().appSettings.value.clone(
                                componentOpacity = it
                            )
                            getMainViewModel().saveApplicationSettings(saveUserSettings = true)
                        }
                    }
                )
            }

            INTENT_ABOUTACTIVITY -> {
                Activity_Settings_AboutApplication(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    onLinkClicked = { link ->
                        context.openLink(
                            url = link,
                            customTab = getMainViewModel().appSettings.value.openLinkInsideApp,
                        )
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }

            else -> {
                Activity_Settings(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    mainViewModel = getMainViewModel(),
                    onMessageReceived = { text, clearPrevious, actionText, action ->
                        showSnackBar(text = text, clearPrevious = clearPrevious, actionText = actionText, action = action)
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    },
                    mediaRequest = {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )
            }
        }
    }

}