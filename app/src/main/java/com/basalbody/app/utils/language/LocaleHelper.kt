package com.basalbody.app.utils.language

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import androidx.core.content.edit
import com.basalbody.app.utils.Constants
import java.util.Locale

object LocaleHelper {
    private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"
    private const val LANG_PREFS = "LanguagePreferences"

    fun setLocale(activity: Activity, language: String) {
        persist(activity, language)
        updateResources(activity, language)
        //activity.recreate() // Restart activity to apply changes
    }

    fun loadLocale(context: Context): String {
        val prefs = context.getSharedPreferences(LANG_PREFS, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_LANGUAGE, Constants.LANG_EN) ?: Constants.LANG_EN
    }

    private fun persist(context: Context, language: String) {
        val prefs: SharedPreferences =
            context.getSharedPreferences(LANG_PREFS, Context.MODE_PRIVATE)
        prefs.edit { putString(SELECTED_LANGUAGE, language) }
    }

    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences(LANG_PREFS, Context.MODE_PRIVATE)
        return prefs.getString(SELECTED_LANGUAGE, Constants.LANG_EN) ?: Constants.LANG_EN
    }

    fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    fun isRtl(context: Context): Boolean {
        val language = getLanguage(context)
        return language == Constants.LANG_AR
    }
}