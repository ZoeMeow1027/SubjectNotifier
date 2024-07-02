package io.zoemeow.dutschedule.model

import androidx.compose.ui.graphics.Color
import io.zoemeow.dutschedule.model.settings.ThemeMode

data class AppearanceState(
    val containerColor: Color,
    val contentColor: Color,
    val currentAppModeState: ThemeMode,
    val backgroundOpacity: Float = 1f,
    val componentOpacity: Float = 1f
)