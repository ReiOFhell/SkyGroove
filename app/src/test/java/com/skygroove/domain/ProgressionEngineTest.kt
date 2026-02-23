package com.skygroove.domain

import com.google.common.truth.Truth.assertThat
import com.skygroove.domain.usecase.ProgressionEngine
import org.junit.Test

class ProgressionEngineTest {
    @Test fun `xp grows with playback time`() {
        val short = ProgressionEngine.xpForPlayback(30_000)
        val long = ProgressionEngine.xpForPlayback(240_000)
        assertThat(long).isGreaterThan(short)
    }

    @Test fun `level scales with total xp`() {
        assertThat(ProgressionEngine.levelForXp(0)).isEqualTo(1)
        assertThat(ProgressionEngine.levelForXp(20_000)).isAtLeast(10)
    }
}
