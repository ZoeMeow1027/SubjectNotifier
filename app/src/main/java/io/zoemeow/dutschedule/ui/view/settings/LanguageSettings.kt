package io.zoemeow.dutschedule.ui.view.settings

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.zoemeow.dutschedule.R
import io.zoemeow.dutschedule.di.LocaleService
import io.zoemeow.dutschedule.model.AppearanceState
import io.zoemeow.dutschedule.ui.components.DialogBase
import io.zoemeow.dutschedule.ui.view.settings.controls.LanguageItem
import io.zoemeow.dutschedule.utils.ActivityUtils
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
    val pendingTag = remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        containerColor = appearanceState.containerColor,
        contentColor = appearanceState.contentColor,
        topBar = {
            TopAppBar(
                title = { Text(context.getString(R.string.settings_applanguage_title)) },
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
                    val currentTag = LocaleService.getSettingsLocaleTag(context = context)

                    Log.d("LanguageTag", currentTag)
                    listOf("auto", "en", "vi").forEach { tag ->
                        val locale = if (tag.compareTo("auto") == 0) null
                        else Locale.Builder().setLanguageTag(tag).build()
                        LanguageItem(
                            title = when (tag.compareTo("auto")) {
                                0 -> context.getString(R.string.settings_applanguage_yoursystemlang)
                                else -> locale?.displayName ?: context.getString(R.string.data_unknown)
                            },
                            context = context,
                            selected = (currentTag.lowercase() == tag.lowercase()),
                            clicked = {
                                Log.d("AppLanguage", String.format("Requested changes to %s", tag))
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    LocaleService.setLocaleA13(context, tag)
                                } else {
                                    pendingTag.value = tag
                                    // val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(tag)
                                    // AppCompatDelegate.setApplicationLocales(appLocale)
                                }
                                onNotificationRegister()
                            }
                        )
                    }
                }
            )
            DialogBase(
                title = context.getString(R.string.settings_applanguage_restartrequired_title),
                content = {
                    Text(context.getString(R.string.settings_applanguage_restartrequired_content))
                },
                isVisible = pendingTag.value.isNotEmpty(),
                actionButtons = {
                    TextButton(
                        onClick = {
                            LocaleService.saveLocale(
                                context = context,
                                langTag = pendingTag.value
                            )
                            ActivityUtils.restartApp(context)
                        },
                        content = {
                            Text(context.getString(R.string.settings_applanguage_restartrequired_yes))
                        }
                    )
                    TextButton(
                        onClick = {
                            pendingTag.value = ""
                        },
                        content = {
                            Text(context.getString(R.string.settings_applanguage_restartrequired_no))
                        }
                    )

                }
            )
        }
    )
}
