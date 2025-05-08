package com.example.nbarandomizer.models

import kotlinx.serialization.Serializable

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
data class Player(
    var name: String,
    val team: String,
    val overall: Rating,
    val threePointRating : Rating,
    val dunkRating: Rating,
    val height: Int,
    val position: String,
    val epoch: Epoch,
    val url: String,
    val photoUrl: String
)