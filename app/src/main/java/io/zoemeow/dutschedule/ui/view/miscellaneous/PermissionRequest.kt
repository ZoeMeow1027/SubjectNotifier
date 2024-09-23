package io.zoemeow.dutschedule.ui.view.miscellaneous

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.ui.view.permissionrequest.controls.PermissionInformation
import io.zoemeow.dutschedule.utils.PermissionUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Miscellaneous_PermissionRequest(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    permissionStatusList: List<PermissionUtils.PermissionCheckResult>,
    fabClicked: (() -> Unit)? = null,
    permissionRequest: ((String) -> Unit)? = null,
    onMessageReceived: ((String, Boolean) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            LargeTopAppBar(
                title = { Text(text = context.getString(R.string.activity_permissionrequest_title)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack?.let { it() }
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
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = BottomAppBarDefaults.containerColor.copy(
                    alpha = appearanceState.backgroundOpacity
                ),
                floatingActionButton = {
                    ExtendedFloatingActionButton(
                        onClick = { fabClicked?.let { it() } },
                        icon = { Icon(Icons.Default.Settings, "") },
                        text = { Text(context.getString(R.string.activity_permissionrequest_action_openandroidsettings)) }
                    )
                },
                actions = {}
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(horizontal = 15.dp),
                content = {
                    Text(
                        context.getString(R.string.activity_permissionrequest_description),
                        modifier = Modifier.padding(vertical = 10.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        content = {
                            permissionStatusList.forEach { item ->
                                PermissionInformation(
                                    context = context,
                                    title = item.name,
                                    permissionCode = item.code,
                                    description = item.description,
                                    isRequired = false,
                                    isGranted = item.isGranted,
                                    padding = PaddingValues(bottom = 10.dp),
                                    opacity = appearanceState.componentOpacity,
                                    clicked = {
                                        permissionRequest?.let {
                                            if (item.isGranted) {
                                                onMessageReceived?.let { it(context.getString(R.string.activity_permissionrequest_snackbar_alreadygranted), true) }
                                            } else it(item.code)
                                        }
                                    }
                                )
                            }
                        },
                    )
                }
            )
        }
    )
}