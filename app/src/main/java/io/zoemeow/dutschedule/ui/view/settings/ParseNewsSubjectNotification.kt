package io.zoemeow.dutschedule.ui.view.settings

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.ui.components.SwitchWithTextInSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Settings_ParseNewsSubjectNotification(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    isEnabled: Boolean = false,
    onChange: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.settings_parsenewssubject_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBack()
                        },
                        content = {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                context.getString(R.string.action_back),
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    )
                }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState()),
                content = {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 5.dp),
                        shape = RoundedCornerShape(GlobalVariables.ROUNDED_CORNER_SHAPE_SIZE),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(
                            alpha = appearanceState.componentOpacity
                        ),
                        content = {
                            Column(
                                modifier = Modifier
                                    .padding(20.dp),
                                content = {
                                    Text(
                                        when (isEnabled) {
                                            true -> context.getString(R.string.settings_parsenewssubject_preview_titleenabled)
                                            false -> context.getString(R.string.settings_parsenewssubject_preview_titledisabled)
                                        },
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(bottom = 5.dp)
                                    )
                                    Text(
                                        when (isEnabled) {
                                            true -> context.getString(R.string.settings_parsenewssubject_preview_descenabled)
                                            false -> context.getString(R.string.settings_parsenewssubject_preview_descdisabled)
                                        }
                                    )
                                }
                            )
                        }
                    )
                    SwitchWithTextInSurface(
                        text = context.getString(R.string.settings_parsenewssubject_choice_enable),
                        enabled = true,
                        checked = isEnabled,
                        onCheckedChange = {
                            onChange()
                        }
                    )
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 20.dp)
                            .padding(top = 20.dp),
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.ic_baseline_info_24),
                            contentDescription = context.getString(R.string.tooltip_info),
                            modifier = Modifier.size(24.dp),
                        )
                        Text(context.getString(R.string.settings_parsenewssubject_info))
                    }
                }
            )
        }
    )
}