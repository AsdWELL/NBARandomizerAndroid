package com.example.nbarandomizer.extensions

import com.example.nbarandomizer.exceptions.*
import com.example.nbarandomizer.models.FilterSettings
import com.example.nbarandomizer.models.FilterSettings.Companion.FILTER_NONE_VALUE
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.models.Position

fun MutableList<Player>.randomize(): MutableList<Player> = shuffled().toMutableList()

fun MutableList<Player>.getNextPlayer(): Player {
    return try {
        this.removeAt(0)
    }
    catch (ex: Exception) {
        throw EmptyPlayersListException()
    }
}

fun MutableList<Player>.getNextPlayerByPosition(position: Position): Player {
    val player = this.find { it.position.contains(position.name) }
        ?: throw PlayerNotFoundException("с позицией $position")

    remove(player)

    return player
}

fun MutableList<Player>.generateTeams(playersCount: Int, teamsCount: Int = 1): List<Player> {
    val newCount = playersCount * teamsCount

    if (newCount > size)
        throw NotEnoughPlayersException()

    return List(newCount) { getNextPlayer() }
}

fun MutableList<Player>.generateCompleteTeams(positions: List<Position>, teamsCount: Int): MutableList<Player> {
    val distinctPositions = positions.distinct()

    val playersCount = distinctPositions.size * teamsCount

    if (playersCount > size)
        throw NotEnoughPlayersException()

    val players = mutableListOf<Player>()

    try {
        for (position in distinctPositions)
            repeat(teamsCount) {
                players.add(getNextPlayerByPosition(position))
            }
    }
    catch (ex: Exception) {
        addAll(players)
        throw ex
    }

    return players
}

fun MutableList<Player>.applyFilterSettingsAndSort(filterSettings: FilterSettings): List<Player> {
   return filter { it.name.contains(filterSettings.name, true)
            && (filterSettings.team == FILTER_NONE_VALUE || it.team.contains(filterSettings.team, true))
            && (filterSettings.position == FILTER_NONE_VALUE || it.position.contains(filterSettings.position, true))
    }.sortedWith(filterSettings.getComparator())
}