package ru.marat.words

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.marat.words.network.WordsService
import ru.marat.words.ui.game_creator_screen.GameCreator
import ru.marat.words.ui.game_screen.GameScreen
import ru.marat.words.ui.game_screen.GameViewModel
import ru.marat.words.ui.theme.WordsTheme
import ru.marat.words.ui.utils.AESEncyption
import ru.marat.words.ui.utils.withoutWhitespaces

class MainActivity : ComponentActivity() {
    @SuppressLint("FrequentlyChangedStateReadInComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val word = mutableStateOf("")
        val attempts = mutableStateOf<Int?>(null)
        intent?.data?.pathSegments?.get(1)?.let {
            word.value = (AESEncyption.decrypt(it) ?: "")
        }
        attempts.value = intent?.data?.pathSegments?.get(0)?.toInt()
        val api = (applicationContext as App).api
        val db = (applicationContext as App).db

        setContent {
            WordsTheme {
                if (word.value.isNotBlank() && word.value.withoutWhitespaces.length in 3..12 && attempts.value in 2..20)
                    GameScreen(viewModel(initializer = {
                        GameViewModel(
                            attempts = attempts.value ?: 6,
                            word = word.value.replace("ё", "е").uppercase(),
                            wordsApi = WordsService(api, db.dao())
                        )
                    }))
                else GameCreator()
            }
        }
    }
}

