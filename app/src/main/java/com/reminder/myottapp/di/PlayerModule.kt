package com.reminder.myottapp.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {

    @Provides
    @Singleton
    fun provideTrackSelector( @ApplicationContext context: Context): DefaultTrackSelector{
        return DefaultTrackSelector(context)
    }



    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context,
        defaultTrackSelector: DefaultTrackSelector
    ): ExoPlayer {
        return ExoPlayer.Builder(context).setTrackSelector(defaultTrackSelector).build()
    }

}