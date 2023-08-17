package ru.marat.words.ui.game_screen.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WordsApi {
    @GET
    suspend fun searchWords(
        @Query("q") searchString: String
    ): Words
}