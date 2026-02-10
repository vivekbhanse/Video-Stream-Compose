package com.reminder.myottapp.feature.player

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.reminder.myottapp.core.PlayerLifecycleHandler
import com.reminder.myottapp.feature.player.screens.CustomControls
import com.reminder.myottapp.feature.player.screens.LockLandscape
import com.reminder.myottapp.feature.player.screens.PlayPauseTimelineButton
import com.reminder.myottapp.feature.player.screens.PlayerTimeline
import com.reminder.myottapp.feature.player.screens.QualitySelector
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var showQualityDialog by remember { mutableStateOf(false) }
    val position by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val playerView = remember {
        PlayerView(context).apply {
            useController = false
        }
    }
    var showOverlayControls by remember { mutableStateOf(false) }
    val selectedQuality by viewModel.selectedQuality.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    LockLandscape()
    LaunchedEffect(Unit) {
        viewModel.startPlayback(
            "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
        )

    }
    LaunchedEffect(uiState.isPlaying) {
        Log.d("PlayerUI", "isPlaying changed → ${uiState.isPlaying}")

        // Keep screen on only when playing
        playerView.keepScreenOn = uiState.isPlaying
        showOverlayControls = true
        // Optional: auto-hide controls when playing
        if (uiState.isPlaying) {
            delay(3000)
            showOverlayControls = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                showOverlayControls = !showOverlayControls
            }
    ) {

        AndroidView(
            factory = {
                playerView.apply {
                    player = viewModel.exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (showOverlayControls) {
            CustomControls(viewModel, showOverlayControls, uiState.isPlaying, {
                if (uiState.isPlaying) {
                    viewModel.setPauseVideo()
                } else {
                    viewModel.setPlayVideo()
                }
            })
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                // 🎯 Timeline overlay
                Column {
                    PlayerTimeline(
                        currentPosition = position,
                        duration = duration,
                        onSeek = viewModel::seekTo
                    )
                }
                // Extra Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlayPauseTimelineButton(uiState.isPlaying, {
                        if (uiState.isPlaying) {
                            viewModel.setPauseVideo()

                        } else {
                            viewModel.setPlayVideo()
                        }
                        showOverlayControls = true
                    })
                    IconButton(onClick = { showQualityDialog = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Quality")
                    }

                }


            }
        }
        when {
            uiState.isBuffering -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }

            uiState.isPlaying -> {
            }

            uiState.isPaused -> {
            }

            uiState.isEnded -> {
                Log.d("PlayerUI", "isEnded changed → ${uiState.isEnded}")
            }
        }




    }

    if (showQualityDialog) {
        QualitySelector(
            qualities = viewModel.getAvailableQualities(),
            selectedQuality = selectedQuality,
            onSelect = { viewModel.setQuality(it) },
            onDismiss = { showQualityDialog = false }
        )
    }
    PlayerLifecycleHandler(viewModel)

}
