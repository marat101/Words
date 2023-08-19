package ru.marat.words

import android.app.Application
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.marat.words.ui.game_screen.network.WordsApi

class App : Application() {
    lateinit var api: WordsApi

    override fun onCreate() {
        super.onCreate()
        api = Retrofit.Builder()
            .baseUrl("https://api.openrussian.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()
    }
}