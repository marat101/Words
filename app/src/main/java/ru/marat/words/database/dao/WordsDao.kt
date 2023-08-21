package ru.marat.words.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.marat.words.database.model.WordsData

@Dao
interface WordsDao {
    @Query("SELECT * FROM wordsdata WHERE id IN (:k)")
    fun getWordsByKey(k: String): WordsData
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: WordsData)
}