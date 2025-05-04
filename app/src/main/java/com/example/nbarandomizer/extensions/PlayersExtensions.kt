package com.example.nbarandomizer.extensions

import com.example.nbarandomizer.exceptions.*
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.models.Position
import kotlin.math.roundToInt

/**
 * Возвращает новый список с элементами коллекции в случайном порядке
 */
fun MutableList<Player>.randomize(): MutableList<Player> = shuffled().toMutableList()

/**
 * Возвращает следующего игрока из коллекции
 */
fun MutableList<Player>.getNextPlayer(): Player = this.removeAt(0)

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
    val newCount = if (teamsCount == 2)
                        playersCount * 2
                   else
                       playersCount

    if (newCount > size)
        throw NotEnoughPlayersException()

    return List(newCount) { getNextPlayer() }
}

fun MutableList<Player>.generateCompleteTeams(positions: List<Position>, teamsCount: Int = 1): MutableList<Player> {
    val distinctPositions = positions.distinct()

    var playersCount = positions.size
    if (teamsCount == 2)
        playersCount *= 2

    if (playersCount > size)
        throw NotEnoughPlayersException()

    val players = mutableListOf<Player>()

    for (position in distinctPositions)
        repeat(teamsCount) {
            players.add(getNextPlayerByPosition(position))
        }

    return players
}

fun MutableList<Player>.teamOverall(): Double {
    return ((this.sumOf { it.overall }.toDouble() / this.size) * 100).roundToInt() / 100.0
}