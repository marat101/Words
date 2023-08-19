package ru.marat.words.ui.utils

import ru.marat.words.ui.game_screen.components.Letters

fun String.checkLetters(): Boolean {
    if (this.isBlank()) return true
    val letters = Letters.firstRow + Letters.secondRow + Letters.thirdRow
    val word = this.lowercase().toList()
    word.forEach { letter ->
        if (!letters.contains(letter)) return false
    }
    return true
}