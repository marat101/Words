package ru.marat.words

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ru.marat.words.database.pefs.SettingsPrefs
import ru.marat.words.network.WordsService
import ru.marat.words.ui.game_creator_screen.GameCreator
import ru.marat.words.ui.game_screen.GameScreen
import ru.marat.words.ui.game_screen.GameViewModel
import ru.marat.words.ui.theme.LocalColors
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
            val settings = remember { SettingsPrefs(this.applicationContext) }
            val isDark = remember { mutableStateOf(settings.getTheme()) }

            WordsTheme(isDark.value) {
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth().background(LocalColors.current.color2),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            isDark.value = !isDark.value
                            settings.setTheme(isDark.value)
                        }) {
                            Icon(
                                painter = painterResource(id = if (isDark.value) R.drawable.sun else R.drawable.moon),
                                contentDescription = "",
                                tint = LocalColors.current.color1
                            )
                        }
                    }
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
}

