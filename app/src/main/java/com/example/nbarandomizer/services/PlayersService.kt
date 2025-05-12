package com.example.nbarandomizer.services

import com.example.nbarandomizer.R
import com.example.nbarandomizer.models.AttributeRatings
import com.example.nbarandomizer.models.Badge
import com.example.nbarandomizer.models.Epoch
import com.example.nbarandomizer.models.Player
import com.example.nbarandomizer.models.PlayerDetails
import com.example.nbarandomizer.models.Rating
import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

class PlayersService(
    private val cacheDir: File,
    private val getColor: (colorId: Int) -> Int
) : Closeable {
    private val _baseUrl = "https://www.2kratings.com/lists/top-100-"

    private val _client = HttpClient(CIO)

    var notifyProgressBar: (() -> Unit)? = null

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

    private fun getDetailsFileName(epoch: Epoch): String {
        return when(epoch) {
            Epoch.Current -> "currentPlayersDetails.json"
            Epoch.AllTime -> "allTimePlayerDetails.json"
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

    private fun getOvrColor(ovr: Int): Int {
        return when(ovr) {
            in 0..83 -> getColor(R.color.ovr8083)
            in 84..86 -> getColor(R.color.ovr8486)
            in 87..89 -> getColor(R.color.ovr8789)
            in 90..91 -> getColor(R.color.ovr9091)
            in 92..94 -> getColor(R.color.ovr9294)
            in 95..96 -> getColor(R.color.ovr9596)
            in 97..98 -> getColor(R.color.ovr9798)
            else -> getColor(R.color.ovr099)
        }
    }

    private fun getStatColor(value: Int): Int {
        return when(value) {
            in 0..59 -> getColor(R.color.stat059)
            in 60..69 -> getColor(R.color.stat6069)
            in 70..79 -> getColor(R.color.stat7079)
            in 80..89 -> getColor(R.color.stat8089)
            else -> getColor(R.color.stat9099)
        }
    }

    private suspend fun parsePlayers(content: String, epoch: Epoch) = coroutineScope {
        async(Dispatchers.IO) {
            val document = Ksoup.parse(content)

            val names = document.select(".entry-font").map { it.text().trim() }
            val info = document.select(".entry-subtext-font").map { it.text().trim() }
            val ratings = document.select(".attribute-box").map { it.text().trim().toInt() }
            val urls = document.select(".entry-font").map(::extractPlayerUrl)
            val photosUrl = document.select(".entry-bg").map(::extractPlayersPhotoUrl)

            MutableList(names.size) {
                val playerInfo = info[it].split("|")
                val overall = ratings[it * 3]
                val threePtRating = ratings[it * 3 + 1]
                val dunkRating = ratings[it * 3 + 2]

                Player(
                    id = it,
                    name = names[it],
                    team = playerInfo[2].trim(),
                    overall = Rating(overall, getOvrColor(overall)),
                    threePointRating = Rating(threePtRating, getStatColor(threePtRating)),
                    dunkRating = Rating(dunkRating, getStatColor(dunkRating)),
                    height = inchesToCm(playerInfo[1]),
                    position = playerInfo[0].replace(" /", ",").trim(),
                    epoch = epoch,
                    url = urls[it],
                    photoUrl = photosUrl[it],
                )
            }
        }
    }

    private fun parseAttribute(input: String): Rating {
        val pattern = Regex("""(\d+)\s*([+-]\d+\s+)?(.+)""")

        val matchResult = pattern.find(input)
        val (value, _, name) = matchResult!!.destructured

        return Rating(value.toInt(), getStatColor(value.toInt()), name)
    }

    private fun parseAttributeCard(element: Element): AttributeRatings {
        val header = element.select(".card-header").text()

        val attrs = element.select(".list-group")[0].children().map { parseAttribute(it.text()) }

        return AttributeRatings(parseAttribute(header), attrs)
    }

    private fun parseBadgeCard(element: Element): Badge {
        val cardBody = element.select(".card-body")[0]

        return Badge(
            name = cardBody.child(0).text(),
            type = cardBody.child(1).text(),
            description = cardBody.child(2).text(),
            photoUrl = element.child(0).child(0).attribute("data-src")?.value ?: ""
        )
    }

    private suspend fun parsePlayersDetails(player: Player, content: String): PlayerDetails {
        return withContext(Dispatchers.IO) {
            val document = Ksoup.parse(content)

            val attributesContent = document.select("#nav-attributes")

            val attributes = if (attributesContent.isEmpty())
                emptyList()
            else
                attributesContent[0].children()[0].children()
                    .flatMap { it.select(".card") }
                    .map(::parseAttributeCard)

            val badgeCards = document.select(".badge-box")

            val badges = if (badgeCards.isEmpty())
                emptyList()
            else
                badgeCards[0].select("div.badge-card").map { parseBadgeCard(it.select(".badge-card")[0]) }

            PlayerDetails(
                id = player.id,
                name = player.name,
                team = player.team,
                overall = player.overall,
                height = player.height,
                position = player.position,
                photoUrl = player.photoUrl,
                attributes = attributes,
                badges = badges
            )
        }
    }

    private suspend fun getPlayersFromCacheByEpoch(epoch: Epoch): MutableList<Player> {
        return withContext(Dispatchers.IO) {
            val fileName = getFileName(epoch)

            val file = File(cacheDir, fileName)

            if (file.exists())
                Json.decodeFromString<MutableList<Player>>(file.readText())
            else
                mutableListOf()
        }
    }

    private suspend fun savePlayersToFile(players: MutableList<Player>, epoch: Epoch) {
        withContext(Dispatchers.IO) {
            val fileName = getFileName(epoch)

            val file = File(cacheDir, fileName)

            FileOutputStream(file, false).use {
                it.write(Json.encodeToString(players).toByteArray())
            }
        }
    }

    suspend fun downloadPlayersByEpoch(epoch: Epoch): MutableList<Player> {
        val suffixUrl = getSuffixUrl(epoch)

        val content = _client.get("$_baseUrl$suffixUrl").bodyAsText()

        val trimmedContent = content.substring(content.indexOf("<tbody>"), content.indexOf("</tbody>"))

        val players = parsePlayers(trimmedContent, epoch).await()

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

    suspend fun downloadPlayerDetails(player: Player): PlayerDetails {
        return try {
            val content = _client.get(player.url).bodyAsText()

            val trimmedContent = content.substring(
                content.indexOf("<!-- Start Atrributes Tab -->"),
                content.indexOf("<!-- End Badges Tab -->")
            )

            val details =  parsePlayersDetails(player, trimmedContent)

            withContext(Dispatchers.Main) {
                notifyProgressBar?.invoke()
            }

            return details
        }
        catch (ex: CancellationException) {
            throw ex
        }
        catch (ex: Exception) {
            downloadPlayerDetails(player)
        }
    }

    suspend fun cachePlayerDetails(playerDetails: List<PlayerDetails>, epoch: Epoch) {
        withContext(Dispatchers.IO) {
            val file = File(cacheDir, getDetailsFileName(epoch))

            FileOutputStream(file, false).use {
                it.write(Json.encodeToString(playerDetails).toByteArray())
            }
        }
    }

    private suspend fun downloadAndCachePlayersDetails(players: List<Player>, epoch: Epoch) = coroutineScope {
        async(Dispatchers.IO) {
            val details = players.map {
                async(Dispatchers.IO) { downloadPlayerDetails(it) }
            }.awaitAll().toMutableList()

            cachePlayerDetails(details, epoch)

            details
        }
    }

    private suspend fun getPlayersDetailsFromCacheByEpoch(epoch: Epoch): MutableList<PlayerDetails> {
        val fileName = getDetailsFileName(epoch)

        val file = File(cacheDir, fileName)

        return if (file.exists())
            withContext(Dispatchers.IO) {
                Json.decodeFromString<MutableList<PlayerDetails>>(file.readText())
            }
        else
            mutableListOf()
    }

    suspend fun getPlayersDetails(players: List<Player>, epoch: Epoch): MutableList<PlayerDetails> {
        val details = getPlayersDetailsFromCacheByEpoch(epoch)

        if (details.isEmpty())
            return try {
                downloadAndCachePlayersDetails(players, epoch).await()
            } catch (ex: Exception) {
                mutableListOf()
            }

        return details
    }

    override fun close() = _client.close()
}