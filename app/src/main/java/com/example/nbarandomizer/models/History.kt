package com.example.nbarandomizer.models

class History {
    private companion object {
        const val TOTAL_PLAYERS_COUNT = 100
    }

    private var _usedPlayersCount = 0
    val usedPlayersCount get() = _usedPlayersCount

    private var _remainingPlayersCount = TOTAL_PLAYERS_COUNT
    val remainingPlayersCount get() = _remainingPlayersCount

    private var _gamesCount = 0
    val gamesCount get() = _gamesCount

    private var _usedPlayers: MutableList<Player> = mutableListOf()
    val usedPlayers get() = _usedPlayers.toList()

    fun addPlayerReplacement(player: Player) {
        _usedPlayersCount++
        _remainingPlayersCount--
        _usedPlayers.add(player)
    }

    fun addPlayers(players: List<Player>) {
        players.forEach { addPlayerReplacement(it) }
        _gamesCount++

        /*_usedPlayersCount += players.size
        _remainingPlayersCount -= players.size
        _gamesCount++
        _usedPlayers.addAll(players)*/
    }

    fun updatePlayers(newPlayers: List<Player>) {
        _usedPlayers = _usedPlayers.map { newPlayers[it.id] }.toMutableList()
    }

    fun clear() {
        _usedPlayersCount = 0
        _gamesCount = 0
        _remainingPlayersCount = TOTAL_PLAYERS_COUNT
        _usedPlayers.clear()
    }
}