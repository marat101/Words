package ru.marat.words.network

import retrofit2.http.GET
import retrofit2.http.Query
import ru.marat.words.network.model.Words

interface WordsApi {
    @GET("suggestions")
    suspend fun searchWords(
        @Query("q") searchString: String
    ): Words
}