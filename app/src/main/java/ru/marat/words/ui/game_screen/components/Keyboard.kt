package ru.marat.words.ui.game_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.marat.words.R
import ru.marat.words.ui.game_screen.KeyboardState
import ru.marat.words.ui.theme.LocalColors


@Composable
fun Keyboard(
    modifier: Modifier = Modifier,
    onClick: (Char) -> Unit,
    onDelete: () -> Unit,
    onEnter: () -> Unit,
    state: KeyboardState = KeyboardState()
) {
    val color: @Composable (Char) -> Color = {
        when {
            state.greenLetters.contains(it.uppercaseChar()) -> LocalColors.current.color3
            state.yellowLetters.contains(it.uppercaseChar()) -> LocalColors.current.color4
            state.missingLetters.contains(it.uppercaseChar()) -> LocalColors.current.color6
            else -> LocalColors.current.color5
        }
    }

    Column(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .height(150.dp)
            .then(modifier)
    ) {
        Row(Modifier.weight(1f)) {
            Letters.firstRow.forEach {
                Button(
                    onClick = { onClick(it) },
                    letter = it.toString(),
                    color = color(it)
                )
            }
        }
        Row(Modifier.weight(1f)) {
            Letters.secondRow.forEach {
                Button(
                    onClick = { onClick(it) },
                    it.toString(),
                    color = color(it)
                )
            }
        }
        Row(Modifier.weight(1f)) {
            Button(onClick = { onDelete() }, content = {
                Image(
                    modifier = Modifier
                        .width(20.dp)
                        .height(15.dp),
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "delete",
                    contentScale = ContentScale.FillBounds,
                    colorFilter = ColorFilter.tint(LocalColors.current.color1)
                )
            })
            Letters.thirdRow.forEach {
                Button(
                    onClick = { onClick(it) },
                    it.toString(),
                    color = color(it)
                )
            }
            Button(onClick = { onEnter() }, "ВВОД", fontSize = 10.sp)
        }
    }
}

@Composable
private fun RowScope.Button(
    onClick: () -> Unit,
    letter: String? = null,
    color: Color = LocalColors.current.color5,
    fontSize: TextUnit = TextUnit.Unspecified,
    content: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(1.dp)
            .background(color, RoundedCornerShape(3.dp))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = rememberRipple(color = LocalColors.current.color1)
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (content == null)
            Text(
                text = letter ?: "",
                fontSize = fontSize
            )
        else
            content()
    }
}

object Letters {
    val firstRow = listOf('й', 'ц', 'у', 'к', 'е', 'н', 'г', 'ш', 'щ', 'з', 'х', 'ъ')
    val secondRow = listOf('ф', 'ы', 'в', 'а', 'п', 'р', 'о', 'л', 'д', 'ж', 'э')
    val thirdRow = listOf('я', 'ч', 'с', 'м', 'и', 'т', 'ь', 'б', 'ю')
}


