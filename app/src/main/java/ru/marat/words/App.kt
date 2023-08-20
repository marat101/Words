package ru.marat.words

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.marat.words.network.WordsApi

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