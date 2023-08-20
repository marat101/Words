package ru.marat.words.ui.game_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ru.marat.words.ui.game_screen.GameViewModel
import ru.marat.words.ui.game_screen.weight

@Composable
fun LoseDialog(
    visible: Boolean = false,
    answer: String,
    onDismissRequest: () -> Unit
) {

    if (visible)
        Dialog(onDismissRequest = onDismissRequest) {
            Column(modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp)).padding(36.dp),horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    textAlign = TextAlign.Center,
                    text = "Вы проиграли,\nправильный ответ:",
                    fontSize = 25.sp,
                    fontWeight = 500.weight
                )
                Text(text = answer.uppercase(),
                    fontSize = 30.sp,
                    fontWeight = 600.weight
                )
            }
        }
}