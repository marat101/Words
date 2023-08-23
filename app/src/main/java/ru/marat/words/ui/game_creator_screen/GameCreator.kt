package ru.marat.words.ui.game_creator_screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import ru.marat.words.ui.utils.AESEncyption
import ru.marat.words.ui.utils.checkLetters


@Composable
fun GameCreator(){
    val wordState = remember { mutableStateOf("") }
    val countState = remember { mutableIntStateOf(6) }
    val isError = remember { mutableStateOf(false) }
    val clipBoardManager = LocalClipboardManager.current
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(
            10.dp,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

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
            selected = countState.intValue,
            onChange = { countState.intValue = it })
        Button(onClick = {
            context.createGame(wordState.value, countState.intValue, clipboardManager = clipBoardManager, onError =  { isError.value = true })
        }) {
            Text("Скопировать")
        }
    }
}
private fun Context.createGame(word: String, count: Int, onError: () -> Unit, clipboardManager: ClipboardManager){
    val w = word.replace("\\s".toRegex(), "").trim()
    Log.e("TAGTAG", w)
    Log.e("TAGTAG", w.length.toString())
    if (!w.checkLetters()) {
        onError()
        Toast.makeText(
            this,
            "Некорректные символы в слове",
            Toast.LENGTH_LONG
        ).show()
        return
    }

    if (w.length !in 3..12) {
        onError()
        Toast.makeText(
            this,
            "В слове должно быть от 3 до 12 букв",
            Toast.LENGTH_LONG
        ).show()
        return
    }
    val encrypted =
        AESEncyption.encrypt(w)?.replace("/", "%2F")
    clipboardManager.setText(AnnotatedString("poop.ru/${count}/$encrypted"))
    Toast.makeText(
        this,
        "Скопировано",
        Toast.LENGTH_LONG
    ).show()

}