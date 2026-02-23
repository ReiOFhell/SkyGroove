package com.skygroove.domain.usecase

import kotlin.math.sqrt

object ProgressionEngine {
    fun xpForPlayback(durationMs: Long): Long = (10 + durationMs / 30_000L).coerceAtMost(40)
    fun coinsForPlayback(durationMs: Long): Long = (5 + durationMs / 45_000L).coerceAtMost(20)
    fun levelForXp(xp: Long): Int = maxOf(1, sqrt(xp / 120.0).toInt() + 1)
}
