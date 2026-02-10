package com.reminder.myottapp.feature.player.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.reminder.myottapp.feature.player.PlayerViewModel

@Composable
fun CustomControls(
    viewModel: PlayerViewModel,
    isVisible: Boolean,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    if (!isVisible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
    ) {

        // ▶ Center controls
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {

            IconButton(onClick = { viewModel.seekBackward() }) {
                Icon(
                    imageVector = Icons.Default.Replay10,
                    contentDescription = "Back 10s",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector =
                        if (isPlaying) Icons.Default.Pause
                        else Icons.Default.PlayArrow,
                    contentDescription = "Play / Pause",
                    tint = Color.White,
                    modifier = Modifier.size(64.dp)
                )
            }

            IconButton(onClick = { viewModel.seekForward() }) {
                Icon(
                    imageVector = Icons.Default.Forward10,
                    contentDescription = "Forward 10s",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}
