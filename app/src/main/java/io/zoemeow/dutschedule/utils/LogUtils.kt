package io.zoemeow.dutschedule.utils

import android.util.Log

class LogUtils {
    enum class LogType(val value: String) {
        Verbose("v"),
        Debug("d"),
        Info("i"),
        Warning("w"),
        Error("e")
    }

    companion object {
        fun sendLog(type: LogType = LogType.Debug, tag: String?, content: String) {
            when (type) {
                LogType.Verbose -> {
                    Log.v(tag, content)
                }
                LogType.Debug -> {
                    Log.d(tag, content)
                }
                LogType.Info -> {
                    Log.i(tag, content)
                }
                LogType.Warning -> {
                    Log.w(tag, content)
                }
                LogType.Error -> {
                    Log.e(tag, content)
                }
            }
        }
    }
}