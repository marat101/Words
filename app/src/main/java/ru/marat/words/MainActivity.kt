package ru.marat.words

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.marat.words.network.WordsService
import ru.marat.words.ui.game_creator_screen.GameCreator
import ru.marat.words.ui.game_screen.GameScreen
import ru.marat.words.ui.game_screen.GameViewModel
import ru.marat.words.ui.theme.LocalColors
import ru.marat.words.ui.theme.WordsTheme
import ru.marat.words.ui.utils.AESEncyption
import ru.marat.words.ui.utils.LinkData
import ru.marat.words.ui.utils.withoutWhitespaces

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val word = mutableStateOf("")
        val attempts = mutableStateOf<Int?>(null)
        val linkData = kotlin.runCatching { LinkData.decode(AESEncyption.decrypt(intent?.data?.pathSegments?.get(0)!!)!!) }.getOrNull()
        linkData?.let {
            word.value = it.word
            attempts.value = it.count
        }
        val api = (applicationContext as App).api
        val db = (applicationContext as App).db
        val settings = (applicationContext as App).settings
        val savedGame = settings.getGame()
        setContent {
            val isDark = remember { mutableStateOf(settings.getTheme()) }
            val isGameStarted = (word.value.isNotBlank() && word.value.withoutWhitespaces.length in 3..12 && attempts.value in 2..20) || savedGame != null
            val isCanceled = rememberSaveable { mutableStateOf(false) }
            WordsTheme(isDark.value) {
                Column {
                    TopBar(isDark.value, onThemeClick = {
                        isDark.value = !isDark.value
                        settings.setTheme(isDark.value)
                    }, isGameStarted = isGameStarted && !isCanceled.value) {
                        isCanceled.value = true
                        settings.saveGame(null)
                    }
                    if (isGameStarted && !isCanceled.value)
                        GameScreen(viewModel(initializer = {
                            GameViewModel(
                                attempts = if (savedGame != null) savedGame?.savedAttempts!! else attempts.value ?: 6,
                                word = if (savedGame != null) savedGame?.savedAnswer!! else word.value.replace("ё", "е").uppercase(),
                                wordsApi = WordsService(api, db.dao()),
                                settings = settings
                            )
                        }))
                    else GameCreator()
                }
            }
        }
    }
}

@Composable
fun TopBar(
    isDark: Boolean,
    onThemeClick: () -> Unit,
    isGameStarted: Boolean,
    onCloseGame: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(LocalColors.current.color2),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (isGameStarted)
            IconButton(onClick = onCloseGame) {
                Icon(
                    painter = painterResource(id = R.drawable.close),
                    contentDescription = "",
                    tint = LocalColors.current.color1
                )
            }
        else Spacer(Modifier)

        IconButton(onClick = onThemeClick) {
            Icon(
                painter = painterResource(id = if (isDark) R.drawable.sun else R.drawable.moon),
                contentDescription = "",
                tint = LocalColors.current.color1
            )
        }
    }
}
