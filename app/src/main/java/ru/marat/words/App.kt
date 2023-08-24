package ru.marat.words

import android.app.Application
import androidx.room.Room
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.marat.words.database.WordsDatabase
import ru.marat.words.database.prefs.SettingsPrefs
import ru.marat.words.network.WordsApi

class App : Application() {
    lateinit var api: WordsApi
    lateinit var db: WordsDatabase
    lateinit var settings: SettingsPrefs
    override fun onCreate() {
        super.onCreate()
        api = Retrofit.Builder()
            .baseUrl("https://api.openrussian.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
        db = Room
            .databaseBuilder(this,WordsDatabase::class.java,"words")
            .fallbackToDestructiveMigration()
            .build()
        settings = SettingsPrefs(this)
    }
}