package ru.marat.words.ui.game_screen.network

data class Sentence(
    val id: Int,
    val links: List<Link>,
    val ru: String,
    val tl: String
)