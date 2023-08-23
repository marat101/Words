package ru.marat.words.ui.theme

import android.app.Activity
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val LocalColors = compositionLocalOf<WordsColors> { error("") }

@Composable
fun WordsTheme(
    darkTheme: Boolean,
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {

    val lightColors = WordsColors(
        color1 = Color(0xFF000000),
        color2 = Color(0xFFFFFFFF),
        color3 = Color(0xFF479C26),
        color4 = Color(0xFF9C7526),
        color5 = Color(0xFFB9B3B3),
        color6 = Color(0xFF757575),
        color7 = Color.LightGray.copy(alpha = 0.34f),
        dialogBackground = Color.White
    )

    val darkColors = WordsColors(
        color1 = Color(0xFFFFFFFF),
        color2 = Color(0xFF000000),
        color3 = Color(0xFF479C26),
        color4 = Color(0xFF9C7526),
        color5 = Color.Gray.copy(alpha = 0.34f),
        color6 = Color(0xFF757575),
        color7 = Color.Gray.copy(alpha = 0.34f),
        dialogBackground = Color.Gray
    )
    val theme = remember { ThemeAnimator(darkTheme,lightColors, darkColors) }
    val colors = theme.themeColors()
    val selectionColors = TextSelectionColors(
        Color(0xFFDCBC3D),
        Color(0xFFDCBC3D)
    )
    CompositionLocalProvider(
        LocalColors provides colors,
        LocalTextStyle provides androidx.compose.ui.text.TextStyle(color = colors.color1),
        LocalTextSelectionColors provides selectionColors,
        content = content
    )
    theme.IsDark(isDark = darkTheme)
    val view = LocalView.current
    val window = (view.context as Activity).window
    window.statusBarColor = colors.color2.toArgb()
    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
}

class ThemeAnimator(isDark: Boolean,private val theme1: WordsColors, private val theme2: WordsColors) {

    private val default = if (isDark)theme2 else theme1
    private val duration = 400

    private val color1 = Animatable(default.color1)
    private val color2 = Animatable(default.color2)
    private val color3 = Animatable(default.color3)
    private val color4 = Animatable(default.color4)
    private val color5 = Animatable(default.color5)
    private val color6 = Animatable(default.color6)
    private val color7 = Animatable(default.color7)
    private val dialog = Animatable(default.dialogBackground)

    @Composable
    fun IsDark(isDark: Boolean) {
        LaunchedEffect(key1 = isDark) {
            launch(Dispatchers.Main) { color1.animateTheme(isDark, theme2.color1, theme1.color1) }
            launch(Dispatchers.Main) { color2.animateTheme(isDark, theme2.color2, theme1.color2) }
            launch(Dispatchers.Main) { color3.animateTheme(isDark, theme2.color3, theme1.color3) }
            launch(Dispatchers.Main) { color4.animateTheme(isDark, theme2.color4, theme1.color4) }
            launch(Dispatchers.Main) { color5.animateTheme(isDark, theme2.color5, theme1.color5) }
            launch(Dispatchers.Main) { color6.animateTheme(isDark, theme2.color6, theme1.color6) }
            launch(Dispatchers.Main) { color7.animateTheme(isDark, theme2.color7, theme1.color7) }
            launch(Dispatchers.Main) {
                dialog.animateTheme(
                    isDark,
                    theme2.dialogBackground,
                    theme1.dialogBackground
                )
            }
        }
    }

    private suspend fun Animatable<Color, AnimationVector4D>.animateTheme(
        isDark: Boolean,
        to: Color,
        _else: Color
    ) {
        this.animateTo(
            if (isDark) to else _else,
            animationSpec = tween(durationMillis = duration, easing = LinearEasing)
        )
    }

    @Composable
    fun themeColors(): WordsColors {
        return WordsColors(
            color1.value,
            color2.value,
            color3.value,
            color4.value,
            color5.value,
            color6.value,
            color7.value,
            dialog.value
        )
    }
}