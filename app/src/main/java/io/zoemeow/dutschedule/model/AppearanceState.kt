package io.zoemeow.dutschedule.model

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.zoemeow.dutschedule.model.settings.ThemeMode

data class AppearanceState(
    val containerColor: Color,
    val contentColor: Color,
    val currentAppModeState: ThemeMode,
    val backgroundOpacity: Float = 1f,
    val componentOpacity: Float = 1f
) {
    companion object {
        @Composable
        fun createDefault(): AppearanceState {
            return AppearanceState(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = TextFieldDefaults.colors().focusedTextColor,
                currentAppModeState = ThemeMode.LightMode
            )
        }
    }
}