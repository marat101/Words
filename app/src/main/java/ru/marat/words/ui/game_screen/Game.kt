package ru.marat.words.ui.game_screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.marat.words.ui.game_screen.components.Keyboard
import ru.marat.words.ui.game_screen.components.LoseDialog
import ru.marat.words.ui.game_screen.components.Notification
import ru.marat.words.ui.game_screen.components.Words


val Int.weight: FontWeight
    get() = FontWeight(this)

@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    val state = viewModel.state.collectAsState()
    val requester = remember { FocusRequester() }

    BasicTextField(
        modifier = Modifier
            .height(1.dp)
            .focusRequester(requester),
        value = runCatching { state.value.words[state.value.attempt].word }.getOrDefault(""),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            viewModel.onEnterCLick()
        }),
        onValueChange = {
            viewModel.onTextChange(it)
        }
    )
    LoseDialog(
        answer = viewModel.word,
        visible = state.value.dialog
    ) { viewModel.onDismissDialog() }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Words(modifier = Modifier.weight(1f), requester = requester, state = state.value)
            Keyboard(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    if (!state.value.gameOver)
                        viewModel.onTextChange(state.value.words[state.value.attempt].word + it.toString())
                },
                onDelete = {
                    viewModel.onDeleteClick()
                },
                onEnter = {
                    viewModel.onEnterCLick()
                },
                state = state.value.keyboardColors
            )
            Spacer(
                modifier = Modifier
                    .heightIn(min = 10.dp)
            )
        }
        Notification(
            modifier = Modifier.align(Alignment.TopCenter),
            text = state.value.notificationText,
            visible = state.value.notification
        ) { viewModel.onHideNotification() }
    }
}