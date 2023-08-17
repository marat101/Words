package ru.marat.words.ui.game_screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.marat.words.ui.game_screen.components.Letters

data class GameState(
    val words: List<Word>,
    val attempt: Int = 0,
    val gameOver: Boolean = false,
    val length: Int
)

data class Word(
    val word: String = "",
    val ylw: List<Int> = listOf(),
    val grn: List<Int> = listOf(),
    val isUsed: Boolean = false
)

class GameViewModel(private val attempts: Int = 6, val word: String) : ViewModel() {
    private val _state = MutableStateFlow(GameState(length = word.length, words = createList()))
    val state = _state.asStateFlow()

    fun onTextChange(str: String) {
        if (_state.value.gameOver) return
        if (str.length <= word.length && str.uppercase().check())
            _state.update {
                val list = it.words.toMutableList()

                list[it.attempt] = list[it.attempt].copy(word = str.replace("ё", "е").uppercase())
                it.copy(words = list)
            }
    }

    fun onEnterCLick() {
        if (_state.value.gameOver) return
        _state.value.apply {
            when {
                words[attempt].word == word -> _state.update {
                    it.copy(
                        gameOver = true,
                        words = it.words.isGameOver(),
                        attempt = it.attempt + 1
                    )
                }

                words[attempt].word.length == word.length && attempt < attempts - 1 -> {
                    _state.update {
                        val list = _state.value.words.toMutableList()
                        val item = list[it.attempt].copy(isUsed = true).setFieldsColors()
                        list[it.attempt] = item
                        it.copy(attempt = it.attempt + 1, words = list)
                    }
                }

                else -> {
                    _state.update {
                        val isgameover =
                            !it.words.any { word -> word.word.isBlank() || word.word.length < this@GameViewModel.word.length }
                        it.copy(
                            words = if (isgameover) it.words.isGameOver() else it.words,
                            gameOver = isgameover
                        )
                    }
                }
            }
        }
    }

    fun onDeleteClick() {
        if (!_state.value.gameOver)
            _state.update {
                val list = it.words.toMutableList()
                list[it.attempt] = list[it.attempt].copy(word = list[it.attempt].word.dropLast(1))
                it.copy(words = list)
            }
    }

    private fun createList() = mutableListOf<Word>().apply {
        for (i in 1..attempts)
            add(Word())
    }

    private fun Word.setFieldsColors(): Word {
        val answer = this@GameViewModel.word
        val foundYlw = mutableListOf<Int>()
        val foundGrn = mutableListOf<Int>()
        val checkedLetters = mutableListOf<Int>()
        this.word.forEachIndexed { index, char ->
            if (char == answer[index]) {
                foundGrn.add(index)
                checkedLetters.add(element = index)
            } else {
                answer.forEachIndexed { aIndex, aChar ->
                    if (aChar == char && !checkedLetters.contains(element = aIndex)) {
                        foundYlw.add(index)
                        checkedLetters.add(aIndex)
                    }
                }
            }
        }
        return this.copy(ylw = foundYlw, grn = foundGrn)
    }

    private fun List<Word>.isGameOver() = _state.value.words.mapIndexed { index, word ->
        if (word.word.isNotBlank()) word.copy(isUsed = true).setFieldsColors() else word
    }
    private fun String.check(): Boolean {
        if (this.isBlank()) return true
        val letters = Letters.firstRow + Letters.secondRow + Letters.thirdRow
        val word = this.lowercase().toList()
        word.forEach { letter ->
            if (!letters.contains(letter)) return false
        }
        return true
    }
}