package com.reminder.myottapp.feature.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.reminder.myottapp.core.ExoPlayerManager
import com.reminder.myottapp.models.PlayerUiState
import com.reminder.myottapp.models.VideoQuality
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
      val exoPlayer: ExoPlayer,
    private val exoPlayerManager: ExoPlayerManager,
    private val trackSelector: DefaultTrackSelector
) : ViewModel() {

    private var _uiState = MutableStateFlow(PlayerUiState())
    val uiState : StateFlow<PlayerUiState> = _uiState

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private var lastPosition: Long = 0L
    private var playWhenReady = true
    private var mediaSet = false
    private val SEEK_INTERVAL_MS = 10_000L // 10 seconds
    private val _selectedQuality = MutableStateFlow<VideoQuality?>(VideoQuality("Auto"))
    val selectedQuality: StateFlow<VideoQuality?> = _selectedQuality
    private var listenerAttached = false
    private val playerListener = object : Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            when (state) {

                Player.STATE_BUFFERING -> {
                    _uiState.update {
                        it.copy(
                            isBuffering = true,
                            isPlaying = false,
                            isPaused = false,
                            isEnded = false
                        )
                    }
                }

                Player.STATE_READY -> {
                    _uiState.update {
                        it.copy(
                            isBuffering = false,
                            isPlaying = exoPlayer.isPlaying,
                            isPaused = !exoPlayer.isPlaying,
                            isEnded = false
                        )
                    }
                }

                Player.STATE_ENDED -> {
                    _uiState.update {
                        it.copy(
                            isPlaying = false,
                            isPaused = false,
                            isBuffering = false,
                            isEnded = true
                        )
                    }
                }

                Player.STATE_IDLE -> {
                    _uiState.update {
                        PlayerUiState() // reset
                    }
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update {
                it.copy(
                    isPlaying = isPlaying,
                    isPaused = !isPlaying,
                    isBuffering = false
                )
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            // Optional: error state
        }
    }
    init {

        attachPlayerListener()
        observePlayback()
    }
    fun startPlayback(url: String) {
        if (mediaSet) return   // 🔥 CRITICAL LINE

        val mediaItem = MediaItem.fromUri(url)
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.playWhenReady = playWhenReady
        mediaSet = true
    }

    fun setPauseVideo(){
        exoPlayer.pause()
    }
    fun setPlayVideo(){
        exoPlayer.play()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }
    fun getAvailableQualities(): List<VideoQuality> {
        val mappedTrackInfo = trackSelector.currentMappedTrackInfo ?: return emptyList()

        val qualities = mutableSetOf<VideoQuality>()
        qualities.add(VideoQuality("Auto"))

        for (rendererIndex in 0 until mappedTrackInfo.rendererCount) {
            if (mappedTrackInfo.getRendererType(rendererIndex) == C.TRACK_TYPE_VIDEO) {
                val groups = mappedTrackInfo.getTrackGroups(rendererIndex)
                for (groupIndex in 0 until groups.length) {
                    val group = groups[groupIndex]
                    for (trackIndex in 0 until group.length) {
                        val format = group.getFormat(trackIndex)
                        if (format.height > 0) {
                            qualities.add(
                                VideoQuality("${format.height}p", format.height)
                            )
                        }
                    }
                }
            }
        }
        return qualities.sortedByDescending { it.height ?: Int.MAX_VALUE }
    }

    fun setQuality(quality: VideoQuality) {
        _selectedQuality.value = quality

        val builder = trackSelector.buildUponParameters()
        if (quality.height == null) {
            builder.clearVideoSizeConstraints() // Auto
        } else {
            builder.setMaxVideoSize(Integer.MAX_VALUE, quality.height)
        }
        trackSelector.setParameters(builder)
    }

    private fun observePlayback() {
        viewModelScope.launch {
            while (true) {
                _currentPosition.value = exoPlayer.currentPosition
                _duration.value = maxOf(exoPlayer.duration, 0L)
                delay(500) // update twice per second
            }
        }
    }

    fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
    }
    fun restoreState() {
        exoPlayer.seekTo(lastPosition)
        exoPlayer.playWhenReady = playWhenReady
    }
    fun saveState() {
        lastPosition = exoPlayer.currentPosition
        playWhenReady = exoPlayer.playWhenReady
    }
    fun seekBackward() {
        val newPosition = (exoPlayer.currentPosition - SEEK_INTERVAL_MS)
            .coerceAtLeast(0L)
        exoPlayer.seekTo(newPosition)
    }

    fun seekForward() {
        val newPosition = (exoPlayer.currentPosition + SEEK_INTERVAL_MS)
            .coerceAtMost(exoPlayer.duration)
        exoPlayer.seekTo(newPosition)
    }

    fun attachPlayerListener() {
        if (listenerAttached) return
        exoPlayer.addListener(playerListener)
        listenerAttached = true
    }


}