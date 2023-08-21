package ru.marat.words

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.marat.words.ui.game_screen.GameScreen
import ru.marat.words.ui.game_screen.GameViewModel
import ru.marat.words.network.WordsService
import ru.marat.words.ui.theme.WordsTheme
import ru.marat.words.ui.utils.AESEncyption
import ru.marat.words.ui.utils.checkLetters
import ru.marat.words.ui.utils.removeSpaces

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val word = mutableStateOf("")
        val attempts = mutableStateOf<Int?>(null)
        intent?.data?.pathSegments?.get(1)?.let {
            word.value = (AESEncyption.decrypt(it) ?: "")
        }
        attempts.value = intent?.data?.pathSegments?.get(0)?.toInt()
        val api = (applicationContext as App).api
        val db = (applicationContext as App).db

        setContent {
            val config = LocalConfiguration.current
            val pagerState = rememberLazyListState(6)


            WordsTheme {
                if (word.value.isNotBlank())
                    GameScreen(viewModel(initializer = {
                        GameViewModel(
                            attempts = attempts.value ?: 6,
                            word = word.value.replace("ё", "е").uppercase(),
                            wordsApi = WordsService(api, db.dao())
                        )
                    }))
                else {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val wordState = remember { mutableStateOf("") }
                        val countState = remember { mutableStateOf("") }
                        val clipManager = LocalClipboardManager.current
                        TextField(value = wordState.value,
                            onValueChange = {
                                wordState.value = it
                            },
                            singleLine = true,
                            maxLines = 1,
                            placeholder = {
                                Text("Слово")
                            })
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.Center) {
//                            HorizontalPager(
//                                modifier = Modifier.fillMaxSize(),
//                                state =pagerState ,
//                                pageSpacing = 10.dp,
//                                pageSize = PageSize.Fixed(30.dp),
//                                contentPadding = PaddingValues(horizontal = config.screenWidthDp.dp/1.7f)
//                            ) {
//                                Text(textAlign = TextAlign.Center,modifier = Modifier.fillMaxSize(),text = (it+1).toString(), fontSize = 25.sp)
//                            }
                            Box(modifier = Modifier
                                .fillMaxHeight()
                                .width(40.dp)
                                .border(2.dp, Color.Gray, RoundedCornerShape(4.dp)))
                        }
                        Button(onClick = {
                            val w = wordState.value.removeSpaces()
                            when {
                                (!w.checkLetters()) -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Некорректные символы в слове",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@Button
                                }

                                w.length in 3..12 -> {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "В слове должно быть от 3 до 12 букв",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    return@Button
                                }
                            }

                            val encrypted =
                                AESEncyption.encrypt(wordState.value)?.replace("/", "%2F")
                            clipManager.setText(AnnotatedString("poop.ru/${countState.value}/$encrypted"))
                            Toast.makeText(
                                this@MainActivity,
                                "Скопировано",
                                Toast.LENGTH_LONG
                            ).show()
                        }) {
                            Text("Скопировать")
                        }
                        LaunchedEffect(key1 = pagerState, block = {
//                            Log.e("TAGTAG", pagerState.currentPage.toString())
//                            countState.value = pagerState.currentPage.toString()
                        })
                    }
                }
            }
        }
    }
}