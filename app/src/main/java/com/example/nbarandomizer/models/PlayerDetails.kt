package com.example.nbarandomizer.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AttributeRatings (
    val rating: Rating,
    val parameters: List<Rating>
)

@Serializable
data class Badge (
    val name: String,
    val type: String,
    val photoUrl: String,
    val description: String)

@Serializable
data class PlayerDetails (
    val id: Int,
    val name: String,
    val team: String,
    val overall: Rating,
    val height: Int,
    val position: String,
    val photoUrl: String,
    val attributes: List<AttributeRatings>,
    val badges: List<Badge>
) {
    @Transient
    var nickname: String? = null
        set(value) {
            field = value?.ifEmpty { null }
        }
}