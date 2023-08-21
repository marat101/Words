package ru.marat.words

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.marat.words.ui.game_screen.GameScreen
import ru.marat.words.ui.game_screen.GameViewModel
import ru.marat.words.network.WordsService
import ru.marat.words.ui.theme.WordsTheme
import ru.marat.words.ui.utils.AESEncyption

class MainActivity : ComponentActivity() {
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
            val worddd = remember { mutableStateOf("") }
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
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val wordState = remember { mutableStateOf("") }
                        val countState = remember { mutableStateOf("") }
                        val clipManager = LocalClipboardManager.current
                        TextField(value = wordState.value,
                            onValueChange = {
                            wordState.value = it
                        },
                            placeholder = {
                                Text("Слово")
                            })
                        TextField(value = countState.value,
                            onValueChange = {
                                countState.value = it
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                            placeholder = {
                                Text("Число попыток")
                            })
                        Button(onClick = {
                            val encrypted = AESEncyption.encrypt(wordState.value)?.replace("/","%2F")
                            clipManager.setText(AnnotatedString("poop.ru/${countState.value.toInt()}/$encrypted"))
                        }) {
                            Text("Скопировать")
                        }
                        Text(text = worddd.value)
                    }
                }
            }
        }
    }
}