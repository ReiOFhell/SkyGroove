package com.skygroove.domain.model

data class Track(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val uri: String,
    val durationMs: Long,
    val artworkUri: String? = null
)

data class ImperialProfile(
    val xp: Long = 0,
    val level: Int = 1,
    val coins: Long = 0,
    val listenedMs: Long = 0
)

data class TrackStats(val trackId: Long, val playCount: Int)
