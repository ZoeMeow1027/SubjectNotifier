package io.zoemeow.dutschedule.di

import android.annotation.TargetApi
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.LocaleList
import android.util.Log
import io.zoemeow.dutschedule.R
import java.io.File
import java.util.Locale

object LocaleService {
    fun generateContextFromLocale(context: Context, tag: String): Context {
        val locale = Locale(tag)
        Locale.setDefault(locale)
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }

    fun getCurrentLocaleTag(context: Context, oldLangSelector: Boolean = false): String {
        // Android 13 or later have built-in language selector.
        val langTag = if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU && !oldLangSelector) {
            getLocaleSystem()
        }
        // Android 12 or lower, or, we force that.
        else {
            if (getLocaleFromPrefs(context).compareTo("auto") == 0) getLocaleSystem(true)
            else getLocaleFromPrefs(context)
        }
        Log.d("Info", String.format("Selected language tag: %s", langTag))
        return langTag
    }

    fun getCurrentLocaleDisplayName(context: Context): String {
        // Android 13 or later have built-in language selector.
        val langTag = if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            Locale.getDefault().displayName
        }
        // Android 12 or lower
        else {
            if (getLocaleFromPrefs(context).compareTo("auto") == 0)
                context.getString(R.string.settings_applanguage_yoursystemlang)
            else Locale(getCurrentLocaleTag(context)).displayName
        }
        Log.d("Info", String.format("Selected language tag: %s", langTag))
        return langTag
    }


    fun getSettingsLocaleTag(context: Context, oldLangSelector: Boolean = false): String {
        // Android 13 or later have built-in language selector.
        val langTag = if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU && !oldLangSelector) {
            getLocaleSystem()
        }
        // Android 12 or lower, or, we force that.
        else {
            getLocaleFromPrefs(context)
        }
        return langTag
    }

    private fun getLocaleFromPrefs(context: Context): String {
        val langPath = "${context.filesDir.path}/language.settings"
        val file = File(langPath)
        try {
            if (file.isFile) {
                val lang = file.readText().trim()
                return lang
            } else throw Exception("File not found: language.settings.")
        } catch (ex: Exception) {
            ex.printStackTrace()
            return "auto"
        }
    }

    private fun getLocaleSystem(iso639Format: Boolean = false): String {
        return when (iso639Format) {
            false -> Locale.getDefault().toLanguageTag()
            true -> Locale.getDefault().language
        }
    }

    @TargetApi(VERSION_CODES.TIRAMISU)
    fun setLocaleA13(context: Context, tag: String) {
        context.getSystemService(LocaleManager::class.java)
            .applicationLocales = LocaleList.forLanguageTags(tag)
    }

    fun saveLocale(context: Context, langTag: String) {
        val langPath = "${context.filesDir.path}/language.settings"
        val file = File(langPath)
        try {
            val locale = Locale(langTag)
            Locale.setDefault(locale)
            file.writeText(langTag)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}