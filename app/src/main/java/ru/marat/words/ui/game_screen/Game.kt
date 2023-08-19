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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
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
import ru.marat.words.ui.theme.LocalColors


val Int.weight: FontWeight
    get() = FontWeight(this)

@Composable
fun GameScreen(
    viewModel: GameViewModel
) {
    val density = LocalDensity.current
    val state = viewModel.state.collectAsState()
    val requester = remember { FocusRequester() }
    val focus = LocalFocusManager.current
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
                val height = remember { mutableStateOf(0.dp) }
                Row(modifier = Modifier.fillMaxWidth()) {
                    repeat(state.value.length) { iteration ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .height(height.value)
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
                                    height.value = density.run { it.size.width.toDp() }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            AutoResizeText(
                                modifier = Modifier.padding(3.dp),
                                text = kotlin.runCatching { word.word[iteration].uppercase() }
                                    .getOrDefault(" "),
                                fontWeight = 700.weight,
                                fontSizeRange = FontSizeRange(max = 90.sp, min = 1.sp),
                                maxLines = 1
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
}

@Composable
fun Letter(string: String, length: Int) {
    Text(
        text = string,
        fontSize = 35.sp,
        fontWeight = 700.weight,
        textAlign = TextAlign.Center,
        lineHeight = 0.sp,
        softWrap = false
    )
}

@Composable
fun AutoResizeText(
    text: String,
    fontSizeRange: FontSizeRange,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = LocalTextStyle.current,
) {
    var fontSizeValue by remember { mutableStateOf(fontSizeRange.max.value) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        color = color,
        maxLines = maxLines,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        style = style,
        fontSize = fontSizeValue.sp,
        onTextLayout = {
//            Timber.d("onTextLayout")
            if (it.didOverflowHeight && !readyToDraw) {
                val nextFontSizeValue = fontSizeValue - fontSizeRange.step.value
                if (nextFontSizeValue <= fontSizeRange.min.value) {
                    // Reached minimum, set minimum font size and it's readToDraw
                    fontSizeValue = fontSizeRange.min.value
                    readyToDraw = true
                } else {
                    // Text doesn't fit yet and haven't reached minimum text range, keep decreasing
                    fontSizeValue = nextFontSizeValue
                }
            } else {
                // Text fits before reaching the minimum, it's readyToDraw
                readyToDraw = true
            }
        },
        modifier = modifier.drawWithContent { if (readyToDraw) drawContent() }
    )
}

data class FontSizeRange(
    val min: TextUnit,
    val max: TextUnit,
    val step: TextUnit = DEFAULT_TEXT_STEP,
) {
    init {
        require(min < max) { "min should be less than max, $this" }
        require(step.value > 0) { "step should be greater than 0, $this" }
    }

    companion object {
        private val DEFAULT_TEXT_STEP = 1.sp
    }
}