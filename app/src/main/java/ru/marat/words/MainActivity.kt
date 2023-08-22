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
                else {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(
                            10.dp,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val wordState = remember { mutableStateOf("") }
                        val countState = remember { mutableIntStateOf(6) }
                        val isError = remember { mutableStateOf(false) }
                        val clipBoardManager = LocalClipboardManager.current
                        TextField(value = wordState.value,
                            onValueChange = {
                                isError.value = false
                                wordState.value = it
                            },
                            label = {
                                Text(text = "Слово")
                            },
                            isError =isError.value,
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                errorIndicatorColor = Color.Transparent
                            ),
                            singleLine = true,
                            maxLines = 1,
                            shape = CircleShape,
                            )
//                        NumberItem(countState.value, onChange = {countState.value = it})
                        val state = remember { mutableStateOf(false) }
                        NumberItem(
                            expanded = state.value,
                            onDismiss = { state.value = false },
                            onClick = { state.value = true },
                            selected = countState.value,
                            onChange = { countState.value = it })
                        Button(onClick = {
                            createGame(wordState.value, countState.intValue, clipboardManager = clipBoardManager, onError =  { isError.value = true })
                        }) {
                            Text("Скопировать")
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalTextApi::class)
    @SuppressLint("ServiceCast")
    private fun createGame(word: String, count: Int, onError: () -> Unit,clipboardManager: ClipboardManager){
        val w = word.replace("\\s".toRegex(), "")
        if (!w.checkLetters()) {
            onError()
            Toast.makeText(
                this@MainActivity,
                "Некорректные символы в слове",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (w.length !in 3..12) {
            onError()
            Toast.makeText(
                this@MainActivity,
                "В слове должно быть от 3 до 12 букв",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val encrypted =
            AESEncyption.encrypt(word)?.replace("/", "%2F")
        clipboardManager.setText(AnnotatedString("poop.ru/${count}/$encrypted"))
        Toast.makeText(
            this@MainActivity,
            "Скопировано",
            Toast.LENGTH_LONG
        ).show()

    }
}

