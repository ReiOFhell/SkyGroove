package com.skygroove.data.media

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.skygroove.domain.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MediaScanner @Inject constructor(@ApplicationContext private val context: Context) {
    fun scan(): List<Track> {
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val tracks = mutableListOf<Track>()
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.DATE_ADDED} DESC"
        )?.use { c ->
            val idIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationIndex = c.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            while (c.moveToNext()) {
                val id = c.getLong(idIndex)
                tracks += Track(
                    id,
                    c.getString(titleIndex) ?: "Relíquia sem nome",
                    c.getString(artistIndex) ?: "Artista Desconhecido",
                    c.getString(albumIndex) ?: "Álbum Desconhecido",
                    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id).toString(),
                    c.getLong(durationIndex)
                )
            }
        }
        return tracks
    }
}
