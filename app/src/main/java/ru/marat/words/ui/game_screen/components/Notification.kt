package ru.marat.words.ui.game_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun Notification(
    modifier: Modifier = Modifier,
    text: String,
    visible: Boolean = false,
    duration: Int = 1500,
    onHide: () -> Unit
) {

    //TODO colors

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(150)) + expandVertically(animationSpec = tween(350)),
        exit = fadeOut(animationSpec = tween(350)) + shrinkVertically(animationSpec = tween(350))
        ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(Color.Black, RoundedCornerShape(8.dp))
                .padding(12.dp)
                .shadow(8.dp, RoundedCornerShape(8.dp))
                .then(modifier)
        ) {
            Text(
//                modifier = Modifier.padding(vertical = 4.dp),
                text = text,
                fontSize = 20.sp,
                color = Color.White,
                lineHeight = 25.sp
            )
        }
    }
    LaunchedEffect(key1 = visible, block = {
        if (visible) {
            delay(duration.toLong())
            onHide()
        }
    })
}