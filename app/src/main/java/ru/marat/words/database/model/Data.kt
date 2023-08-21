package ru.marat.words.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WordsData(
    @PrimaryKey val id: String,
    val response: List<String>,
    val date: Long
)