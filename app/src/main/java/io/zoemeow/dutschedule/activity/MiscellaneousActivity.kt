package io.zoemeow.dutschedule.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import dagger.hilt.android.AndroidEntryPoint
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.ui.view.miscellaneous.Activity_Miscellaneous_ExternalLinks
import io.zoemeow.dutschedule.ui.view.miscellaneous.Activity_Miscellaneous_PermissionRequest
import io.zoemeow.dutschedule.utils.ExtensionUtils.Companion.openLink
import io.zoemeow.dutschedule.utils.PermissionUtils

@AndroidEntryPoint
class MiscellaneousActivity : BaseActivity() {
    companion object {
        const val INTENT_EXTERNALLINKS = "view_externallink"
        const val INTENT_PERMISSIONREQUEST = "view_permissionrequest"
    }

    private val permissionStatusList = mutableStateListOf<PermissionUtils.PermissionCheckResult>()

    @Composable
    override fun OnPreloadOnce() {
        when (intent.action) {
            INTENT_PERMISSIONREQUEST -> {
                reloadPermissionStatus()
            }

            else -> {}
        }
    }

    private fun reloadPermissionStatus() {
        permissionStatusList.clear()
        permissionStatusList.addAll(PermissionUtils.getAllPermissions(this))
    }

    private val permissionRequestLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        // val permissionResultList = arrayListOf<Pair<String, Boolean>>()
        result.toList().forEach { _ -> // item ->
            // permissionResultList.add(Pair(item.first, item.second))
        }

        reloadPermissionStatus()
    }

    @Composable
    override fun OnMainView(
        context: Context,
        snackBarHostState: SnackbarHostState,
        appearanceState: AppearanceState
    ) {
        when (intent.action) {
            INTENT_EXTERNALLINKS -> {
                Activity_Miscellaneous_ExternalLinks(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    onLinkClicked = { link ->
                        context.openLink(
                            url = link,
                            customTab = getMainViewModel().appSettings.value.openLinkInsideApp
                        )
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }

            INTENT_PERMISSIONREQUEST -> {
                Activity_Miscellaneous_PermissionRequest(
                    context = context,
                    snackBarHostState = snackBarHostState,
                    appearanceState = appearanceState,
                    permissionStatusList = permissionStatusList,
                    onMessageReceived = { text, closePrevious ->
                        showSnackBar(text, closePrevious)
                    },
                    onBack = {
                        setResult(RESULT_CANCELED)
                        finish()
                    },
                    fabClicked = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        context.startActivity(intent)
                    },
                    permissionRequest = {
                        PermissionUtils.requestPermission(
                            context = context,
                            permissionCode = it,
                            permissionRequestLauncher = permissionRequestLauncher
                        )
                    }
                )
            }

            else -> {
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when (intent.action) {
            INTENT_PERMISSIONREQUEST -> {
                reloadPermissionStatus()
            }

            else -> {}
        }
    }
}