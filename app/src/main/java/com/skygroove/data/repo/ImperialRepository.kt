package com.skygroove.data.repo

import com.skygroove.data.local.dao.ImperialDao
import com.skygroove.data.local.entity.*
import com.skygroove.domain.model.ImperialProfile
import com.skygroove.domain.usecase.ProgressionEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImperialRepository @Inject constructor(private val dao: ImperialDao) {
    fun observeFavorites(): Flow<Set<Long>> = dao.observeFavorites().map { list -> list.map(FavoriteEntity::trackId).toSet() }
    fun observeProfile(): Flow<ImperialProfile> = dao.observeProfile().map {
        it?.let { p -> ImperialProfile(p.xp, p.level, p.coins, p.listenedMs) } ?: ImperialProfile()
    }
    fun observeTopTracks(limit: Int = 10) = dao.observeTopTracks(limit)
    fun observeHistory(limit: Int = 20) = dao.observeHistory(limit)
    fun observePlaylists() = dao.observePlaylists()

    suspend fun toggleFavorite(trackId: Long, on: Boolean) {
        if (on) dao.addFavorite(FavoriteEntity(trackId)) else dao.removeFavorite(trackId)
    }

    suspend fun registerPlayback(trackId: Long, durationMs: Long) {
        val current = dao.getTrackStats(trackId)
        dao.upsertPlayStats(PlayStatsEntity(trackId, (current?.playCount ?: 0) + 1))
        dao.addHistory(HistoryEntity(trackId = trackId, playedAt = System.currentTimeMillis()))

        val currentProfile = dao.getProfile() ?: ImperialProfileEntity(1, 0, 1, 0, 0)
        val gainedXp = ProgressionEngine.xpForPlayback(durationMs)
        val gainedCoins = ProgressionEngine.coinsForPlayback(durationMs)
        val totalXp = currentProfile.xp + gainedXp
        dao.upsertProfile(
            currentProfile.copy(
                xp = totalXp,
                coins = currentProfile.coins + gainedCoins,
                listenedMs = currentProfile.listenedMs + durationMs,
                level = ProgressionEngine.levelForXp(totalXp)
            )
        )
    }

    suspend fun createPlaylist(name: String) = dao.createPlaylist(PlaylistEntity(name = name))
    suspend fun renamePlaylist(id: Long, name: String) = dao.renamePlaylist(id, name)
    suspend fun deletePlaylist(id: Long) = dao.deletePlaylist(id)
    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) = dao.addToPlaylist(PlaylistTrackCrossRef(playlistId, trackId))
}
