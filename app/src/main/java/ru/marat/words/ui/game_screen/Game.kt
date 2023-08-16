package ru.marat.words.ui.game_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.marat.words.ui.game_screen.components.Keyboard
import ru.marat.words.ui.theme.LocalColors


val Int.weight: FontWeight
    get() = FontWeight(this)

@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    val state = viewModel.state.collectAsState()
    val requester = remember { FocusRequester() }
    BasicTextField(
        modifier = Modifier
            .height(1.dp)
            .focusRequester(requester),
        value = state.value.words[state.value.attempt],
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            viewModel.onEnterCLick()
        }),
        onValueChange = {
            viewModel.onTextChange(it)
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null) {
                    requester.requestFocus()
                }
        ) {
            state.value.words.forEach { word ->
                val height = remember { mutableStateOf(0.dp) }
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(5) { iteration ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .height(height.value)
                                .border(1.dp, LocalColors.current.color1)
                                .onGloballyPositioned {
                                    height.value = density.run { it.size.width.toDp() }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            kotlin.runCatching {
                                Text(
                                    modifier = Modifier.fillMaxSize(),
                                    text = word[iteration].uppercase(),
                                    fontSize = 30.sp,
                                    fontWeight = 700.weight,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
        Keyboard(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                viewModel.onTextChange(state.value.words[state.value.attempt] + it.toString())
            },
            onDelete = {viewModel.onDeleteClick()},
            onEnter = {viewModel.onEnterCLick()}
        )
        Spacer(
            modifier = Modifier
                .heightIn(min = 10.dp)
        )
    }
}
