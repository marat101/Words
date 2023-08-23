package ru.marat.words.ui.game_creator_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.marat.words.R
import ru.marat.words.ui.game_screen.weight
import ru.marat.words.ui.theme.LocalColors


@Composable
fun NumberItem(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    selected: Int,
    onChange: (Int) -> Unit
) {

    val conf = LocalConfiguration.current
    Row(
        modifier = Modifier.width(conf.screenWidthDp.dp / 1.5f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Количество попыток")
        Spacer(modifier = Modifier.width(55.dp))
        IconButton(
            modifier = Modifier.background(LocalColors.current.color7, CircleShape),
            onClick = onClick
        ) {
            Text(
                text = selected.toString(),
                fontSize = 25.sp,
                fontWeight = 600.weight,
                color = LocalColors.current.color1
            )
        }
        DropdownMenu(
            offset = DpOffset(Int.MAX_VALUE.dp, 0.dp),
            modifier = Modifier
                .height((conf.screenHeightDp / 3).dp)
                .background(LocalColors.current.dialogBackground, RoundedCornerShape(6.dp))
                .clip(RoundedCornerShape(6.dp)),
            expanded = expanded,
            onDismissRequest = onDismiss
        ) {
            repeat(19) {
                DropdownMenuItem(onClick = {
                    onChange(it + 2)
                    onDismiss()
                }) {
                    Text(text = (it + 2).toString())
                }
            }
        }
    }
}

@Composable
fun NumberItem(count: Int, onChange: (Int) -> Unit) {
    val config = LocalConfiguration.current
    val listState = rememberLazyListState(6)
    listState.ListAnimator(count, config.screenWidthDp / 2)
    Row(
        modifier = Modifier
            .width(200.dp)
            .height(40.dp),
    ) {
        IconButton(onClick = {
            val prev = if (count - 1 > 2) count - 1 else count
            onChange(prev)
        }) {
//            Icon(painter = painterResource(id = R.drawable.arrow_left), contentDescription = "")
        }
        LazyRow(
            modifier = Modifier.weight(1f),
            state = listState,
            contentPadding = PaddingValues(horizontal = (config.screenWidthDp / 2).dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            userScrollEnabled = false
        ) {
            items(count = 11) {
                Text(
                    textAlign = TextAlign.Center,
                    modifier = Modifier.size(30.dp),
                    text = (it + 2).toString(),
                    fontSize = 25.sp,
                    color = Color.Black
                )
            }
        }
        IconButton(onClick = {
            val next = if (count + 1 < 12) count + 1 else count
            onChange(next)
        }) {
//            Icon(painter = painterResource(id = R.drawable.arrow_right), contentDescription = "")
        }
    }
}

@Composable
fun LazyListState.ListAnimator(currentSelection: Int = 6, padding: Int) {
    val density = LocalDensity.current
    var job: Job? = remember { null }
    var prevNum = remember { currentSelection - 2 }
    val widthInPx = density.run { 30.dp.toPx() }

    LaunchedEffect(key1 = currentSelection, block = {
        job?.cancel()
        job = launch(Dispatchers.Main) {
            val listState = this@ListAnimator
            val viewportSize = listState.layoutInfo.viewportSize
            listState.animateScrollToItem(
                currentSelection - 2,
                (widthInPx / 2 - viewportSize.width / 2).toInt()
            )
        }
        job?.join()
        prevNum = currentSelection - 2
    })
}