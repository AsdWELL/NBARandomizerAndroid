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
    val id: Int,
    var name: String,
    val team: String,
    val overall: Int,
    val threePointRating : Int,
    val dunkRating: Int,
    val height: Int,
    val position: String,
    val epoch: Epoch,
    val url: String,
    val photoUrl: String
) {
    override fun toString(): String {
        return "$name $position ovr: $overall, 3pt: $threePointRating, dunk: $dunkRating, height: ${height}cm - $epoch $team"
    }
}