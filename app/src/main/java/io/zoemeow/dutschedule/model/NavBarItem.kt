package io.zoemeow.dutschedule.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector
import io.zoemeow.dutschedule.R

data class NavBarItem(
    val titleResId: Int,
    val icon: ImageVector? = null,
    val resourceIconId: Int? = null,
    val route: String
) {
    companion object {
        val dashboard = NavBarItem(
            titleResId = R.string.main_dashboard_title,
            icon = Icons.Default.Home,
            route = "dashboard"
        )

        val news = NavBarItem(
            titleResId = R.string.news_title,
            resourceIconId = R.drawable.ic_baseline_newspaper_24,
            route = "news"
        )

        val account = NavBarItem(
            titleResId = R.string.account_title,
            icon = Icons.Default.AccountCircle,
            route = "account"
        )

//        val notification = NavBarItem(
//            titleResId = R.string.notification_panel_title,
//            icon = Icons.Default.Notifications,
//            route = "notifications"
//        )

//        val settings = NavBarItem(
//            titleResId = R.string.settings_title,
//            icon = Icons.Default.Settings,
//            route = "settings"
//        )

        fun getAll(): List<NavBarItem> {
            return listOf(dashboard, news, account)
        }
    }
}