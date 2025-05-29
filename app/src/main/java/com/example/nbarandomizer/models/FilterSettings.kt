package com.example.nbarandomizer.models

data class FilterSettings (
    var name: String = "",
    var team: String = FILTER_NONE_VALUE,
    var position: String = FILTER_NONE_VALUE
) {
    companion object {
        const val FILTER_NONE_VALUE = "None"
    }
}