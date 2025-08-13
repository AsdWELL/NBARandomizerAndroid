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
    override val id: Int,
    override var name: String,
    override val team: String,
    override val overall: Rating,
    val threePointRating : Rating,
    val dunkRating: Rating,
    val height: Int,
    val position: String,
    override val url: String,
    override val photoUrl: String
) : IPlayerBase{
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