package ru.marat.words.ui.game_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.marat.words.ui.game_screen.GameState
import ru.marat.words.ui.game_screen.weight
import ru.marat.words.ui.theme.LocalColors

@Composable
fun Words(modifier: Modifier = Modifier, requester: FocusRequester ,state: GameState){
    val focus = LocalFocusManager.current
    val height = remember{ mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    LazyColumn(
        modifier = Modifier
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                focus.clearFocus()
                requester.requestFocus()
            }.then(modifier),
        contentPadding = PaddingValues(6.dp)
    ) {
        itemsIndexed(state.words) { index, word ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(state.length) { iteration ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .height(density.run { height.floatValue.toDp() })
                            .border(
                                3.dp,
                                if (state.attempt > index) Color.Transparent else LocalColors.current.color5
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