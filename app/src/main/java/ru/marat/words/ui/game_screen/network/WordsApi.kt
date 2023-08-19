package ru.marat.words.ui.game_screen.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.marat.words.ui.game_screen.network.model.Words

interface WordsApi {
    @GET("suggestions")
    suspend fun searchWords(
        @Query("q") searchString: String
    ): Words
}