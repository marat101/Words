package ru.marat.words.network

import android.util.Log
import ru.marat.words.database.dao.WordsDao
import ru.marat.words.database.model.WordsData
import ru.marat.words.ui.utils.checkLetters

class WordsService(private val api: WordsApi, private val db: WordsDao) {
    suspend fun getWords(searchWord: String): List<String> {
        val savedWords = kotlin.runCatching { db.getWordsByKey(searchWord) }.getOrElse {
            it.printStackTrace()
            null
        }
        if (savedWords != null && (System.currentTimeMillis() - 7889238000) < savedWords.date) {
            return savedWords.response
        }

        val words = api.searchWords(searchWord).result.words.map { wordX ->
            wordX.word.ru.replace("'", "").replace("ё", "е").uppercase()
        }.filter { it.checkLetters() }
        saveWithKey(searchWord, words)
        return words
    }

    private fun saveWithKey(key: String, words: List<String>) {
        kotlin.runCatching { db.insert(WordsData(id = key, words, System.currentTimeMillis())) }
            .onFailure {
                it.printStackTrace()
            }
    }
}