package ru.marat.words.database.pefs

import android.content.Context


class SettingsPrefs(context: Context) {

    companion object {
        private const val THEME_KEY = "THEME"
    }

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun setTheme(isDark: Boolean){
        prefs.edit().putBoolean(THEME_KEY, isDark).apply()
    }

    fun getTheme(): Boolean {
        return prefs.getBoolean(THEME_KEY, false)
    }
}