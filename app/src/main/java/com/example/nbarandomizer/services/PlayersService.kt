package com.example.nbarandomizer.services

import android.content.Context
import com.example.nbarandomizer.models.Epoch
import com.example.nbarandomizer.models.Player
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class PlayersService(private val context: Context) : Closeable {
    private val _baseUrl = "https://www.2kratings.com/lists/top-100-"

    private val _client = HttpClient(CIO)

    private fun getSuffixUrl(epoch: Epoch): String {
        return when(epoch) {
            Epoch.Current -> "highest-nba-2k-ratings"
            Epoch.AllTime -> "all-time-players"
        }
    }

    private fun getFileName(epoch: Epoch): String{
        return when(epoch) {
            Epoch.Current -> "currentPlayers.json"
            Epoch.AllTime -> "allTimePlayers.json"
        }
    }

    private fun extractPlayerUrl(element: Element): String {
        return element.children().first()
            ?.attribute("href")?.value ?: ""
    }

    private fun extractPlayersPhotoUrl(element: Element): String {
        return element.children().first()
            ?.attribute("data-src")?.value?.replace("-80x80", "") ?: ""
    }

    private fun inchesToCm(value: String): Int {
        val buffer = value.replace("\"", "").split("'").map { it.trim().toInt() }
        val inches = buffer[0] * 12 + buffer[1]

        return (inches * 2.54).roundToInt()
    }

    private suspend fun parsePlayers(content: String, epoch: Epoch) = coroutineScope {
        async {
            val document = Ksoup.parse(content)

            val names = document.select(".entry-font").map { it.text().trim() }
            val info = document.select(".entry-subtext-font").map { it.text().trim() }
            val ratings = document.select(".attribute-box").map { it.text().trim().toInt() }
            val urls = document.select(".entry-font").map(::extractPlayerUrl)
            val photosUrl = document.select(".entry-bg").map(::extractPlayersPhotoUrl)

            MutableList(names.size) {
                val playerInfo = info[it].split("|")

                Player(
                    id = it,
                    name = names[it],
                    team = playerInfo[2].trim(),
                    overall = ratings[it * 3],
                    threePointRating = ratings[it * 3 + 1],
                    dunkRating = ratings[it * 3 + 2],
                    height = inchesToCm(playerInfo[1]),
                    position = playerInfo[0].replace(" /", ",").trim(),
                    epoch = epoch,
                    url = urls[it],
                    photoUrl = photosUrl[it]
                )
            }
        }
    }

    private suspend fun savePlayersToFile(players: MutableList<Player>, epoch: Epoch) {
        withContext(Dispatchers.IO) {
            val fileName = getFileName(epoch)

            val file = File(context.filesDir, fileName)

            FileOutputStream(file, false).use {
                it.write(Json.encodeToString(players).toByteArray())
            }
        }
    }

    private suspend fun getPlayersFromCacheByEpoch(epoch: Epoch): MutableList<Player> {
        val fileName = getFileName(epoch)

        val file = File(context.filesDir, fileName)

        return if (file.exists())
            withContext(Dispatchers.IO) {
                Json.decodeFromString<MutableList<Player>>(file.readText())
            }
        else
            mutableListOf()
    }

    suspend fun downloadPlayersByEpoch(epoch: Epoch): MutableList<Player> {
        val suffixUrl = getSuffixUrl(epoch)

        val content = _client.get("$_baseUrl$suffixUrl").bodyAsText()

        val players = parsePlayers(content, epoch).await()

        savePlayersToFile(players, epoch)

        return players
    }

    suspend fun getPlayersByEpoch(epoch: Epoch): MutableList<Player> {
        val players = getPlayersFromCacheByEpoch(epoch)

        if (players.isEmpty())
            return try {
                downloadPlayersByEpoch(epoch)
            } catch (ex: Exception) {
                mutableListOf()
            }

        return players
    }

    override fun close() = _client.close()
}