package io.zoemeow.dutschedule.ui.view.settings

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.BuildConfig
import io.zoemeow.dutschedule.GlobalVariables
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.ui.components.OptionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Settings_AboutApplication(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    onLinkClicked: ((String) -> Unit)? = null,
    onBack: () -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.settings_about_title)) },
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
        }
    ) { paddingValues ->
        // TODO: Add function here!
        Column(
            modifier = Modifier.padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(200.dp)),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
                Image(
                    modifier = Modifier.size(192.dp),
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                context.getString(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                context.getString(
                    R.string.settings_option_version_description,
                    BuildConfig.VERSION_NAME,
                    BuildConfig.VERSION_CODE
                ),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.size(15.dp))
            OptionItem(
                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.google_fonts_device_reset_24),
                        "changelog",
                        modifier = Modifier.padding(end = 15.dp)
                    )
                },
                title = context.getString(R.string.settings_about_changelog),
                description = context.getString(R.string.settings_about_changelog_description),
                onClick = {
                    onLinkClicked?.let { it(GlobalVariables.LINK_REPOSITORY_CHANGELOG) }
                }
            )
            OptionItem(
                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_info_24),
                        "license",
                        modifier = Modifier.padding(end = 15.dp)
                    )
                },
                title = context.getString(R.string.settings_about_license),
                description = GlobalVariables.LICENSE_STRING,
                onClick = {
                    onLinkClicked?.let { it(GlobalVariables.LINK_REPOSITORY_LICENSE) }
                }
            )
            OptionItem(
                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_baseline_info_24),
                        "credit",
                        modifier = Modifier.padding(end = 15.dp)
                    )
                },
                title = context.getString(R.string.settings_about_credit),
                description = context.getString(R.string.settings_about_credit_description),
                onClick = {
                    onLinkClicked?.let { it(GlobalVariables.LINK_REPOSITORY_CREDITS) }
                }
            )
            OptionItem(
                modifierInside = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.github_mark_24),
                        "repository",
                        modifier = Modifier.padding(end = 15.dp)
                    )
                },
                title = context.getString(R.string.settings_about_github),
                description = GlobalVariables.LINK_REPOSITORY,
                onClick = {
                    onLinkClicked?.let { it(GlobalVariables.LINK_REPOSITORY) }
                }
            )
        }
    }
}