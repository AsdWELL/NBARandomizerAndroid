package com.example.nbarandomizer.models

data class PlayerReplacement(
    val oldPlayer: Player,
    val newPlayer: Player
)

data class Game(
    var usedPlayers: MutableList<Player>,
    val teamsCount: Int,
    val replacements: MutableList<PlayerReplacement> = mutableListOf()
)

class History {
    private companion object {
        const val TOTAL_PLAYERS_COUNT = 100
    }

    private var _games: MutableList<Game> = mutableListOf()
    val games get() = _games.toList()

    private var _usedPlayersCount = 0
    val usedPlayersCount get() = _usedPlayersCount

    val remainingPlayersCount get() = TOTAL_PLAYERS_COUNT - _usedPlayersCount

    val gamesCount get() = _games.size

    fun addPlayerReplacementToLastGame(oldPlayer: Player, newPlayer: Player) {
        if (_games.isEmpty())
            return

        _usedPlayersCount++

        with(_games.last()) {
            replacements.add(PlayerReplacement(oldPlayer, newPlayer))

            val oldPlayerIndex = usedPlayers.indexOf(oldPlayer)
            usedPlayers[oldPlayerIndex] = newPlayer
        }
    }

    fun addPlayers(players: MutableList<Player>, teamsCount: Int) {
        _usedPlayersCount += players.size

        _games.add(Game(players, teamsCount))
    }

    fun updatePlayers(newPlayers: List<Player>) {
        _games.forEach { game ->
            game.usedPlayers = game.usedPlayers.map { newPlayers[it.id] }.toMutableList()
        }
    }

    fun clear() {
        _usedPlayersCount = 0
        _games.clear()
    }
}