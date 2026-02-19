package com.skygroove.core

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import com.skygroove.data.local.SkyGrooveDatabase
import com.skygroove.data.local.dao.ImperialDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModule {
    @Provides @Singleton
    fun provideDb(@ApplicationContext context: Context): SkyGrooveDatabase =
        Room.databaseBuilder(context, SkyGrooveDatabase::class.java, "skygroove.db").build()

    @Provides fun provideDao(db: SkyGrooveDatabase): ImperialDao = db.imperialDao()

    @Provides @Singleton
    fun providePlayer(@ApplicationContext context: Context): ExoPlayer = ExoPlayer.Builder(context).build().apply {
        setAudioAttributes(AudioAttributes.DEFAULT, true)
        setHandleAudioBecomingNoisy(true)
    }
}
