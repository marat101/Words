package ru.marat.words.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.marat.words.database.dao.WordsDao
import ru.marat.words.database.model.WordsData

@TypeConverters(Converter::class)
@Database(entities = [WordsData::class],version = 2)
abstract class WordsDatabase:RoomDatabase() {
    abstract fun dao(): WordsDao
}

class Converter() {
    private val gson = Gson()
    private val type = object : TypeToken<List<String>>() {}.type

    @TypeConverter
    fun fromListToJson(list: List<String>): String{
        return gson.toJson(list, type)
    }
    @TypeConverter
    fun fromJsonToList(list: String): List<String>{
        return gson.fromJson(list, type)
    }
}