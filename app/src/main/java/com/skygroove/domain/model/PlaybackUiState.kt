package com.skygroove.domain.model

import androidx.media3.common.Player

data class PlaybackUiState(
    val currentTrackId: Long? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0,
    val durationMs: Long = 0,
    val queue: List<Long> = emptyList(),
    val queueIndex: Int = 0,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val shuffleOn: Boolean = false
)
