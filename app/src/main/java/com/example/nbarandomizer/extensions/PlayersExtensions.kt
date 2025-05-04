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

private fun MutableList<Player>.checkPlayersCount(count: Int) {
    if (count < 2)
        throw InvalidPlayersCountException()

    if (count > size)
        throw NotEnoughPlayersException()

    if (count % 2 == 1)
        throw OddPlayersCountException()
}

/**
 * @param count Количество игроков в команде
 * @throws InvalidPlayersCountException
 * @throws NotEnoughPlayersException
 * @throws OddPlayersCountException
 */
fun MutableList<Player>.generateTeams(count: Int): List<Player> {
    val newCount = count * 2

    checkPlayersCount(newCount)

    return List(newCount) { getNextPlayer() }
}

fun MutableList<Player>.generateCompleteTeams(vararg positions: Position = Position.entries.toTypedArray()): MutableList<Player> {
    val distinctPositions = positions.distinct()

    checkPlayersCount(distinctPositions.size * 2)

    val players = mutableListOf<Player>()

    repeat(2) {
        distinctPositions.forEach { players.add(getNextPlayerByPosition(it)) }
    }

    return players
}

fun MutableList<Player>.teamOverall(): Double {
    return ((this.sumOf { it.overall }.toDouble() / this.size) * 100).roundToInt() / 100.0
}