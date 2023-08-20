package ru.marat.words.network

import android.util.Log
import ru.marat.words.ui.utils.checkLetters

class WordsService(private val api: WordsApi) {
    suspend fun getWords(searchWord: String): List<String> {
        val words = api.searchWords(searchWord).result.words.map { wordX ->
            wordX.word.ru.replace("'","").replace("ё","е").uppercase()
        }.filter { it.checkLetters() }
        Log.e("TAGTAG", words.toString())
        return words
    }
}