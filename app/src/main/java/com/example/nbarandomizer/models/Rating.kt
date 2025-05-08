package com.example.nbarandomizer.models

import kotlinx.serialization.Serializable

@Serializable
data class Rating (
    val value: Int,
    val color: Int,
    val name: String? = null
)