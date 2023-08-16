package ru.marat.words

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ru.marat.words.ui.game_screen.GameScreen
import ru.marat.words.ui.theme.WordsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordsTheme {
                GameScreen()
            }
        }
    }
}