package ru.marat.words.ui.game_screen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.marat.words.ui.game_screen.components.Letters

data class GameState(
    val words: List<String> = listOf("", "", "", "", "", ""),
    val attempt: Int = 0,
    val inputIsEnabled: Boolean = true
)

class GameViewModel : ViewModel() {
    private val _state = MutableStateFlow(GameState())
    val state = _state.asStateFlow()

    fun onTextChange(str: String) {
        if (str.length <= 5 && _state.value.inputIsEnabled && str.check())
            _state.update {
                val list = it.words.toMutableList()
                list[it.attempt] = str.replace("ё", "е")
                it.copy(words = list)
            }
    }

    fun onEnterCLick() {
        _state.value.apply {
            if (words[attempt].length == 5 && attempt < 5)
                _state.update { it.copy(attempt = it.attempt + 1) }
            else
                _state.update { it.copy(inputIsEnabled = it.words.any { word -> word.isBlank() }) }
        }
    }

    fun onDeleteClick() {
        if (_state.value.inputIsEnabled)
            _state.update {
                val list = it.words.toMutableList()
                list[it.attempt] = list[it.attempt].dropLast(1)
                it.copy(words = list)
            }
    }
    private fun String.check(): Boolean {
        if (this.isBlank()) return true
        val letters = Letters.firstRow + Letters.secondRow + Letters.thirdRow
        val word = this.toList()
        word.forEach { letter ->
                if (!letters.contains(letter)) return false
        }
        return true
    }
}