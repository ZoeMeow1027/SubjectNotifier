package io.zoemeow.dutschedule.ui.view.settings

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.model.AppearanceState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity_Settings_AppLanguageSettings(
    context: Context,
    snackBarHostState: SnackbarHostState,
    appearanceState: AppearanceState,
    onNotificationRegister: () -> Unit,
    onBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            LargeTopAppBar(
                title = { Text(context.getString(R.string.settings_applanguage_title)) },
                colors = TopAppBarDefaults.largeTopAppBarColors(
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
                },
                actions = {
                    IconButton(
                        onClick = {
                            val defaultLocale = Locale.getDefault().toLanguageTag()
                            Log.d("AppLanguage", String.format("Requested changes to %s", defaultLocale))
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                context.getSystemService(LocaleManager::class.java)
                                    .applicationLocales = LocaleList.forLanguageTags(defaultLocale)
                            } else {
                                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(defaultLocale)
                                AppCompatDelegate.setApplicationLocales(appLocale)
                            }
                            onNotificationRegister()
                        },
                        content = {
                            Icon(
                                ImageVector.vectorResource(R.drawable.ic_baseline_restore_24),
                                "",
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    )
                },
                scrollBehavior = scrollBehavior
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState()),
                content = {
                    // TODO: Replace deprecation here.
                    @Suppress("DEPRECATION")
                    val currentTag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        context.resources.configuration.locales.get(0).toLanguageTag()
                    } else{
                        context.resources.configuration.locale.toLanguageTag()
                    }

                    Log.d("LanguageTag", currentTag)
                    listOf("en-US", "vi-VN").forEach { tag ->
                        Locale.Builder().setLanguageTag(tag).build().apply {
                            LanguageItem(
                                title = this.displayName,
                                context = context,
                                selected = (currentTag.lowercase() == tag.lowercase()),
                                clicked = {
                                    Log.d("AppLanguage", String.format("Requested changes to %s", this.displayName))
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        context.getSystemService(LocaleManager::class.java)
                                            .applicationLocales = LocaleList.forLanguageTags(tag)
                                    } else {
                                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(tag)
                                        AppCompatDelegate.setApplicationLocales(appLocale)
                                    }
                                    onNotificationRegister()
                                }
                            )
                        }
                    }
                }
            )
        }
    )
}

@Composable
private fun LanguageItem(
    title: String,
    context: Context,
    selected: Boolean = false,
    clicked: (() -> Unit)? = null
) {
    Surface(
        modifier = Modifier.clickable {
            clicked?.let { it() }
        },
        color = Color.Transparent,
        content = {
            Row(
                modifier = Modifier.fillMaxWidth().height(60.dp).padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                content = {
                    Text(
                        title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 19.sp
                    )
                    if (selected) {
                        Icon(Icons.Default.Check, context.getString(R.string.tooltip_selected))
                    }
                }
            )
        }
    )
}