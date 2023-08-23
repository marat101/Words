package ru.marat.words

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.UrlAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withAnnotation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.marat.words.network.WordsService
import ru.marat.words.ui.game_creator_screen.GameCreator
import ru.marat.words.ui.game_creator_screen.NumberItem
import ru.marat.words.ui.game_screen.GameScreen
import ru.marat.words.ui.game_screen.GameViewModel
import ru.marat.words.ui.theme.WordsTheme
import ru.marat.words.ui.utils.AESEncyption
import ru.marat.words.ui.utils.checkLetters

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
                if (word.value.isNotBlank())
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

