package ru.marat.words.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Words(
    val result: Result
)