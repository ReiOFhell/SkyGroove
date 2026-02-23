package com.skygroove.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(@PrimaryKey val trackId: Long)

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val trackId: Long,
    val playedAt: Long
)

@Entity(tableName = "play_stats")
data class PlayStatsEntity(@PrimaryKey val trackId: Long, val playCount: Int)

@Entity(tableName = "imperial_profile")
data class ImperialProfileEntity(
    @PrimaryKey val id: Int = 1,
    val xp: Long,
    val level: Int,
    val coins: Long,
    val listenedMs: Long
)

@Entity(tableName = "playlists")
data class PlaylistEntity(@PrimaryKey(autoGenerate = true) val id: Long = 0, val name: String)

@Entity(primaryKeys = ["playlistId", "trackId"], tableName = "playlist_tracks")
data class PlaylistTrackCrossRef(val playlistId: Long, val trackId: Long)
