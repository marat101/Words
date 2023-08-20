package ru.marat.words.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val words: List<WordX>
)