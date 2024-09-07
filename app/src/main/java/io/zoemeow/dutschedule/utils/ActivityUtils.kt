package io.zoemeow.dutschedule.utils

import android.graphics.Color
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat

class ActivityUtils {
    companion object {
        /**
         * This will bypass network on main thread exception.
         * Use this at your own risk.
         * Target: OkHttp3
         *
         * Source: https://blog.cpming.top/p/android-os-networkonmainthreadexception
         */
        fun permitAllNetworkPolicy() {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        fun makeActivityFullScreen(activity: ComponentActivity) {
            WindowCompat.setDecorFitsSystemWindows(activity.window, true)

            activity.enableEdgeToEdge(
                // This app is only ever in dark mode, so hard code detectDarkMode to true.
                SystemBarStyle.auto(
                    Color.TRANSPARENT,
                    Color.TRANSPARENT,
                    detectDarkMode = { true }
                )
            )
        }
    }
}