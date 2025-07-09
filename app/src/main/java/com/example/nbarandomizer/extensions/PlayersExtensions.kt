package com.example.nbarandomizer.extensions

import com.example.nbarandomizer.exceptions.*
import com.example.nbarandomizer.models.FilterSettings
import com.example.nbarandomizer.models.FilterSettings.Companion.FILTER_NONE_VALUE
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.models.Position
import com.example.nbarandomizer.models.SortingAttrs

/**
 * Возвращает новый список с элементами коллекции в случайном порядке
 */
fun MutableList<Player>.randomize(): MutableList<Player> = shuffled().toMutableList()

/**
 * Возвращает следующего игрока из коллекции
 */
fun MutableList<Player>.getNextPlayer(): Player {
    return try {
        this.removeAt(0)
    }
    catch (ex: Exception) {
        throw EmptyPlayersListException()
    }
}

/**
 *  Возвращает следующего игрока из коллекции по позиции
 *  @param position Позиция игрока
 *  @throws PlayerNotFoundException
 */
fun MutableList<Player>.getNextPlayerByPosition(position: Position): Player {
    val player = this.find { it.position.contains(position.name) }
        ?: throw PlayerNotFoundException("с позицией $position")

    remove(player)

    return player
}

/**
 * @param count Количество игроков в команде
 * @throws InvalidPlayersCountException
 * @throws NotEnoughPlayersException
 * @throws OddPlayersCountException
 */
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

fun MutableList<Player>.applyFilterSettings(filterSettings: FilterSettings): List<Player> {
    return filter { it.name.contains(filterSettings.name, true)
            && (filterSettings.team == FILTER_NONE_VALUE || it.team.contains(filterSettings.team, true))
            && (filterSettings.position == FILTER_NONE_VALUE || it.position.contains(filterSettings.position, true))
    }.sortedWith(filterSettings.getComparator())
}