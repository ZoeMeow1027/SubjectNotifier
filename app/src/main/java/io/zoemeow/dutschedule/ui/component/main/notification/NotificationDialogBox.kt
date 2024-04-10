package io.zoemeow.dutschedule.ui.component.main.notification

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.NotificationHistory
import io.zoemeow.dutschedule.utils.CustomDateUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDialogBox(
    isVisible: Boolean = false,
    itemList: List<NotificationHistory>,
    containerColor: Color,
    contentColor: Color,
    backgroundImage: Bitmap? = null,
    snackBarHostState: SnackbarHostState? = null,
    onDismiss: (() -> Unit)? = null,
    onClick: ((NotificationHistory) -> Unit)? = null,
    onClear: ((NotificationHistory) -> Unit)? = null,
    onClearAll: (() -> Unit)? = null,
    height: Float = 0.7f,
    opacity: Float = 1f,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = {
                it / 2
            },
        ),
        exit = slideOutVertically(
            targetOffsetY = {
                it
            },
        ),
        content = {
            backgroundImage?.let {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = it.asImageBitmap(),
                    contentDescription = "background_image",
                    contentScale = ContentScale.Crop
                )
            }
            Scaffold(
                containerColor = containerColor,
                contentColor = contentColor,
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Notifications") },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        actions = {
                            if (itemList.isNotEmpty()) {
                                IconButton(
                                    onClick = { onClearAll?.let { it() } },
                                    content = {
                                        Icon(ImageVector.vectorResource(id = R.drawable.ic_baseline_clear_all_24), "")
                                    }
                                )
                                Spacer(modifier = Modifier.size(3.dp))
                            }
                            if (onDismiss != null) {
                                IconButton(
                                    onClick = { onDismiss() },
                                    content = {
                                        Icon(Icons.Default.Clear, "Close")
                                    }
                                )
                            }
                        }
                    )
                },
                snackbarHost = { snackBarHostState?.let { SnackbarHost(hostState = it) } },
                content = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(height)
                            .padding(it)
                            .padding(horizontal = 15.dp)
                    ) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                        ) {
                            if (itemList.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 15.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No notifications")
                                }
                            } else {
                                itemList.groupBy { p -> p.timestamp }
                                    .toSortedMap(compareByDescending { it })
                                    .forEach(action = { group ->
                                        Text(
                                            CustomDateUtil.unixToDuration(group.key),
                                            style = MaterialTheme.typography.titleMedium,
                                            modifier = Modifier.padding(top = 5.dp, bottom = 4.dp)
                                        )
                                        group.value.forEach { item ->
                                            NotificationItem(
                                                modifier = Modifier.padding(top = 2.dp, bottom = 5.dp),
                                                opacity = opacity,
                                                onClick = { onClick?.let { it(item) } },
                                                onClear = { onClear?.let { it(item) } },
                                                item = item
                                            )
                                        }
                                    })
                            }
                            Spacer(modifier = Modifier.size(12.dp))
                        }
                    }
                }
            )
        }
    )
}