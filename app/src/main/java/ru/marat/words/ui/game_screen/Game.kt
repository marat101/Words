package ru.marat.words.ui.game_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.marat.words.ui.game_screen.components.Keyboard
import ru.marat.words.ui.game_screen.components.LoseDialog
import ru.marat.words.ui.game_screen.components.Notification
import ru.marat.words.ui.theme.LocalColors


val Int.weight: FontWeight
    get() = FontWeight(this)

@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    val state = viewModel.state.collectAsState()
    val requester = remember { FocusRequester() }
    val focus = LocalFocusManager.current
    val height = remember{ mutableFloatStateOf(0f) }
    val density = LocalDensity.current

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
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        focus.clearFocus()
                        requester.requestFocus()
                    },
                contentPadding = PaddingValues(6.dp)
            ) {
                itemsIndexed(state.value.words) { index, word ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        repeat(state.value.length) { iteration ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(2.dp)
                                    .height(density.run { height.floatValue.toDp() })
                                    .border(
                                        3.dp,
                                        if (state.value.attempt > index) Color.Transparent else LocalColors.current.color5
                                    )
                                    .background(
                                        if (word.isUsed) {
                                            when {
                                                word.grn.contains(iteration) -> LocalColors.current.color3
                                                word.ylw.contains(iteration) -> LocalColors.current.color4
                                                else -> LocalColors.current.color5
                                            }
                                        } else {
                                            Color.Transparent
                                        }
                                    )
                                    .onGloballyPositioned {
                                        if (height.floatValue < 5f)
                                            height.floatValue = it.size.width.toFloat()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Letter(
                                    string = if (word.word.length >= iteration + 1) word.word[iteration].uppercase() else "",
                                    size = density.run { height.floatValue.toSp() } / 1.4f
                                )
                            }
                        }
                    }
                }
            }
            Keyboard(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = {
                    if (!state.value.gameOver)
                        viewModel.onTextChange(state.value.words[state.value.attempt].word + it.toString())
                },
                onDelete = { viewModel.onDeleteClick() },
                onEnter = { viewModel.onEnterCLick() },
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

@Composable
fun Letter(string: String, size: TextUnit) {
    Text(
        text = string,
        fontSize = size,
        fontWeight = 700.weight,
        textAlign = TextAlign.Center,
        lineHeight = 0.sp,
        softWrap = false
    )
}