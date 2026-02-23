package com.skygroove.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skygroove.data.local.dao.ImperialDao
import com.skygroove.data.local.entity.*

@Database(
    entities = [FavoriteEntity::class, HistoryEntity::class, PlayStatsEntity::class, ImperialProfileEntity::class, PlaylistEntity::class, PlaylistTrackCrossRef::class],
    version = 1
)
abstract class SkyGrooveDatabase : RoomDatabase() {
    abstract fun imperialDao(): ImperialDao
}
