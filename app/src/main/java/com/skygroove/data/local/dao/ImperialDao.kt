package com.skygroove.data.local.dao

import androidx.room.*
import com.skygroove.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ImperialDao {
    @Query("SELECT * FROM favorites") fun observeFavorites(): Flow<List<FavoriteEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun addFavorite(entity: FavoriteEntity)
    @Query("DELETE FROM favorites WHERE trackId = :trackId") suspend fun removeFavorite(trackId: Long)

    @Insert suspend fun addHistory(item: HistoryEntity)
    @Query("SELECT * FROM history ORDER BY playedAt DESC LIMIT :limit") fun observeHistory(limit: Int = 40): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertPlayStats(entity: PlayStatsEntity)
    @Query("SELECT * FROM play_stats ORDER BY playCount DESC LIMIT :limit") fun observeTopTracks(limit: Int = 10): Flow<List<PlayStatsEntity>>
    @Query("SELECT * FROM play_stats WHERE trackId = :trackId") suspend fun getTrackStats(trackId: Long): PlayStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertProfile(profile: ImperialProfileEntity)
    @Query("SELECT * FROM imperial_profile WHERE id = 1") fun observeProfile(): Flow<ImperialProfileEntity?>
    @Query("SELECT * FROM imperial_profile WHERE id = 1") suspend fun getProfile(): ImperialProfileEntity?

    @Insert suspend fun createPlaylist(entity: PlaylistEntity): Long
    @Query("SELECT * FROM playlists ORDER BY name ASC") fun observePlaylists(): Flow<List<PlaylistEntity>>
    @Query("UPDATE playlists SET name = :name WHERE id = :id") suspend fun renamePlaylist(id: Long, name: String)
    @Query("DELETE FROM playlists WHERE id = :id") suspend fun deletePlaylist(id: Long)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun addToPlaylist(ref: PlaylistTrackCrossRef)
    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId") suspend fun removeFromPlaylist(playlistId: Long, trackId: Long)
}
