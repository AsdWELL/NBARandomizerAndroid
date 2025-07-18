package com.example.nbarandomizer.models

enum class SortingAttrs(val title: String) {
    Overall("Overall"),
    Name("Name"),
    Team("Team"),
    ThreePoint("3pt"),
    Dunk("Dunk"),
    Height("Height"),
}

data class FilterSettings (
    var name: String = "",
    var team: String = FILTER_NONE_VALUE,
    var position: String = FILTER_NONE_VALUE,
    var sortingAttr: String = SortingAttrs.Overall.title,
    var sortAscending: Boolean = false
) {
    companion object {
        const val FILTER_NONE_VALUE = "None"
    }

    fun getComparator(): Comparator<Player> {
        val comparator: Comparator<Player> = when(sortingAttr) {
            SortingAttrs.Name.title -> compareBy { it.name }
            SortingAttrs.Team.title -> compareBy { it.team }
            SortingAttrs.ThreePoint.title -> compareBy { it.threePointRating.value }
            SortingAttrs.Dunk.title -> compareBy { it.dunkRating.value }
            SortingAttrs.Height.title -> compareBy { it.height }
            else -> compareBy { it.overall.value }
        }

        return if (sortAscending) comparator else comparator.reversed()
    }
}