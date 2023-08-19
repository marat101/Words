package ru.marat.words.ui.game_screen.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Result(
    val words: List<WordX>
)