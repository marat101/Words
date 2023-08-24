package ru.marat.words.database.prefs

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.marat.words.ui.game_screen.GameState


class SettingsPrefs(context: Context) {

    companion object {
        private const val THEME_KEY = "THEME"
        private const val GAME_KEY = "GAME"

        private val type = object : TypeToken<GameState>(){}.type
    }

    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun setTheme(isDark: Boolean){
        prefs.edit().putBoolean(THEME_KEY, isDark).apply()
    }
    fun getTheme(): Boolean {
        return prefs.getBoolean(THEME_KEY, false)
    }
    fun saveGame(game: GameState?){
        val sGame = Gson().toJson(game, type)
        prefs.edit().putString(GAME_KEY, sGame).apply()
    }
    fun getGame(): GameState? {
        val gameStr = prefs.getString(GAME_KEY, null)
        return Gson().fromJson<GameState>(gameStr, type)
    }
}