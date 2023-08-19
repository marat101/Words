package ru.marat.words.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalColors = compositionLocalOf<WordsColors> { error("") }

@Composable
fun WordsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            val window = (view.context as Activity).window
//            window.statusBarColor = colorScheme.primary.toArgb()
//            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
//        }
//    }

    val lightColors = WordsColors(
        color1 = Color(0xFF000000),
        color2 = Color(0xFFFFFFFF),
        color3 = Color(0xFF479C26),
        color4 = Color(0xFF9C7526),
        color5 = Color(0xFFB9B3B3),
        color6 = Color(0xFF757575),
    )

    CompositionLocalProvider(
        LocalColors provides if (darkTheme) lightColors else lightColors,
        content = content
    )
}