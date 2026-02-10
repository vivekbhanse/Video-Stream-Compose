package com.reminder.myottapp.core

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import javax.inject.Inject

class ExoPlayerManager @Inject constructor(
    private val player: ExoPlayer
) {
    fun play(url: String) {
        val mediaItem = MediaItem.fromUri(url)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true

    }

    fun pause(){
        player.pause()
    }

    fun release(){
        player.release()
    }
}