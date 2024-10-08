package io.zoemeow.dutschedule.utils

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import io.zoemeow.dutschedule.R

class PermissionUtils {
    data class PermissionCheckResult(
        val name: String,
        val code: String,
        val description: String,
        val isGranted: Boolean
    )

    companion object {
        fun getAllPermissions(context: Context): List<PermissionCheckResult> {
            return listOf(
                checkPermissionNotification(context),
                checkPermissionManageExternalStorage(context),
                checkPermissionScheduleExactAlarm(context)
            )
        }

        fun checkPermissionNotification(context: Context): PermissionCheckResult {
            return PermissionCheckResult(
                name = context.getString(R.string.activity_permissionrequest_permission_notification_title),
                code = "android.permission.POST_NOTIFICATIONS",
                description = context.getString(R.string.activity_permissionrequest_permission_notification_description),
                isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context, Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                } else true
            )
        }

        // https://stackoverflow.com/questions/73620790/android-13-how-to-request-write-external-storage
        fun checkPermissionManageExternalStorage(context: Context): PermissionCheckResult {
            return PermissionCheckResult(
                name = context.getString(R.string.activity_permissionrequest_permission_manageexternalstorage_title),
                code = "android.permission.MANAGE_EXTERNAL_STORAGE",
                description = context.getString(R.string.activity_permissionrequest_permission_manageexternalstorage_description),
                isGranted = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ->
                        Environment.isExternalStorageManager()
                    else -> true
                }
            )
        }

        fun checkPermissionScheduleExactAlarm(context: Context): PermissionCheckResult {
            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            return PermissionCheckResult(
                name = context.getString(R.string.activity_permissionrequest_permission_scheduleexactalarm_title),
                code = "android.permission.SCHEDULE_EXACT_ALARM",
                description = context.getString(R.string.activity_permissionrequest_permission_scheduleexactalarm_description),
                isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    alarmManager.canScheduleExactAlarms()
                } else true
            )
        }

        /**
         * Request a permission.
         * @param context Android Context
         * @param permissionCode Permission code
         * @param permissionRequestLauncher Created with registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
         */
        fun requestPermission(
            context: Context,
            permissionCode: String,
            permissionRequestLauncher: ActivityResultLauncher<Array<String>>
        ) {
            when (permissionCode) {
                Manifest.permission.SCHEDULE_EXACT_ALARM -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                            Uri.fromParts("package", context.packageName, null)
                        ).also {
                            context.startActivity(it)
                        }
                    } else {
                        // TODO: Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM on Android 11 or older
                    }
                }
                // https://stackoverflow.com/questions/73620790/android-13-how-to-request-write-external-storage
                Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        Intent(
                            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                            Uri.fromParts("package", context.packageName, null)
                        ).also {
                            context.startActivity(it)
                        }
                    } else {
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        ).also {
                            context.startActivity(it)
                        }
                    }
                }
                else -> {
                    permissionRequestLauncher.launch(listOf(permissionCode).toTypedArray())
                }
            }
        }
    }
}