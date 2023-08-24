package ru.marat.words.ui.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


data class LinkData(
    val word: String,
    val count: Int
) {
    companion object {
        fun decode(jsonString: String): LinkData {
            val type = object : TypeToken<LinkData>() {}.type
            return Gson().fromJson(jsonString, type)
        }
        fun encode(count: Int, word: String): String {
            val type = object : TypeToken<LinkData>() {}.type
            return Gson().toJson(LinkData(word,count), type)
        }
    }
}
