package com.example.nbarandomizer.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

enum class Position {
    PG,
    SG,
    SF,
    PF,
    C
}

enum class Epoch {
    Current,
    AllTime
}

@Serializable
data class Player (
    val id: Int,
    var name: String,
    val team: String,
    val overall: Rating,
    val threePointRating : Rating,
    val dunkRating: Rating,
    val height: Int,
    val position: String,
    val url: String,
    val photoUrl: String
) {
    @Transient
    var nickname: String? = null
        set(value) {
            field = value?.ifEmpty { null }
        }

    val playerName get() = nickname ?: name
}

@Serializable
data class PlayerNickname (
    val playerName: String,
    var nickname: String
)