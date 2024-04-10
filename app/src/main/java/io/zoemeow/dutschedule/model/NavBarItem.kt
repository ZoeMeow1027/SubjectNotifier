package io.zoemeow.dutschedule.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import io.zoemeow.dutschedule.R

data class NavBarItem(
    val title: String,
    val icon: ImageVector? = null,
    val resourceIconId: Int? = null,
    val route: String
) {
    companion object {
        val news = NavBarItem(
            title = "News",
            resourceIconId = R.drawable.ic_baseline_newspaper_24,
            route = "news"
        )

        val account = NavBarItem(
            title = "Account",
            icon = Icons.Default.AccountCircle,
            route = "account"
        )

        val notification = NavBarItem(
            title = "Notifications",
            icon = Icons.Default.Notifications,
            route = "notifications"
        )

        val settings = NavBarItem(
            title = "Settings",
            icon = Icons.Default.Settings,
            route = "settings"
        )

        fun getAll(): List<NavBarItem> {
            return listOf(news, account, notification, settings)
        }
    }
}