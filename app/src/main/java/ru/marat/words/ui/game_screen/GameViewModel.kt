package ru.marat.words.ui.game_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.marat.words.network.WordsService
import ru.marat.words.ui.utils.checkLetters
import java.net.UnknownHostException

data class GameState(
    val words: List<Word>,
    val attempt: Int = 0,
    val gameOver: Boolean = false,
    val length: Int,
    val keyboardColors: KeyboardState = KeyboardState(),
    val dialog: Boolean = false,
    val notification: Boolean = false,
    val notificationText: String = "",
)

data class KeyboardState(
    val greenLetters: Set<Char> = emptySet(),
    val yellowLetters: Set<Char> = emptySet(),
    val missingLetters: Set<Char> = emptySet()
)

data class Word(
    val word: String = "",
    val ylw: List<Int> = listOf(),
    val grn: List<Int> = listOf(),
    val isUsed: Boolean = false
)

class GameViewModel(
    private val attempts: Int = 6,
    val word: String,
    private val wordsApi: WordsService
) : ViewModel() {
    private val _state = MutableStateFlow(GameState(length = word.length, words = createList()))
    val state = _state.asStateFlow()
    private var checkInProgress: Boolean = false

    fun onTextChange(str: String) {
        if (_state.value.gameOver) return
        if (str.length <= word.length && str.uppercase().checkLetters())
            _state.update {
                val list = it.words.toMutableList()

                list[it.attempt] = list[it.attempt].copy(word = str.replace("ё", "е").uppercase())
                it.copy(words = list)
            }
    }

    fun onEnterCLick() {
        if (_state.value.gameOver) return
        viewModelScope.launch(Dispatchers.IO) {
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
                        if (checkInProgress) return@launch
                        kotlin.runCatching {
                            checkInProgress = true
                            if (wordsApi.getWords(words[attempt].word)
                                    .contains(element = words[attempt].word)
                            ) {
                                _state.update {
                                    val list = _state.value.words.toMutableList()
                                    val item = list[it.attempt].copy(isUsed = true).setFieldsColors()
                                    list[it.attempt] = item
                                    it.copy(attempt = it.attempt + 1, words = list)
                                }
                            } else {
                                _state.update {
                                    it.copy(
                                        notification = true,
                                        notificationText = "В словаре игры нет такого слова! Попробуйте другое."
                                    )
                                }
                            }
                            checkInProgress = false
                        }.getOrElse { throwable ->
                            _state.update {
                                it.copy(
                                    notification = true,
                                    notificationText = "Произошла ошибка. " + if (throwable is UnknownHostException) "Проверьте подключение к интернету." else ""
                                )
                            }
                            checkInProgress = false
                        }
                    }

                    else -> {
                        _state.update {
                            val isgameover =
                                !it.words.any { word -> word.word.isBlank() || word.word.length < this@GameViewModel.word.length }
                            it.copy(
                                words = if (isgameover) it.words.isGameOver() else it.words,
                                gameOver = isgameover,
                                dialog = isgameover
                            )
                        }
                    }
                }
            }
        }
    }

    fun onHideNotification() {
        _state.update { it.copy(notification = false) }
    }

    fun onDeleteClick() {
        if (!_state.value.gameOver)
            _state.update {
                val list = it.words.toMutableList()
                list[it.attempt] = list[it.attempt].copy(word = list[it.attempt].word.dropLast(1))
                it.copy(words = list)
            }
    }

    fun onDismissDialog() = _state.update { it.copy(dialog = false) }
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
        _state.update {
            val missingLetters = it.keyboardColors.missingLetters +
                    this.word.filterIndexed { index, _ -> !(foundGrn + foundYlw).contains(element = index) }
                        .toSet()
            val greenLetters = it.keyboardColors.greenLetters +
                    foundGrn.map { this.word[it] }.toSet()
            val yellowLetters = it.keyboardColors.yellowLetters +
                    foundYlw.map { this.word[it] }.toSet()

            it.copy(keyboardColors = KeyboardState(greenLetters, yellowLetters, missingLetters))
        }
        return this.copy(ylw = foundYlw, grn = foundGrn)
    }

    private fun List<Word>.isGameOver() = _state.value.words.mapIndexed { index, word ->
        if (word.word.isNotBlank()) word.copy(isUsed = true).setFieldsColors() else word
    }
}