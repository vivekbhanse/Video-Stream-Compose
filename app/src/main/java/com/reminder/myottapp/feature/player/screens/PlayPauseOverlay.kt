package com.reminder.myottapp.feature.player.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PlayPauseOverlay(
    isPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 72.dp
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier
            .size(size)
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = CircleShape
            )
    ) {
        Icon(
            imageVector = if (isPlaying)
                Icons.Default.Pause
            else
                Icons.Default.PlayArrow,
            contentDescription = "Play / Pause",
            tint = Color.White,
            modifier = Modifier.size(size / 2)
        )
    }
}

@Composable
fun PlayPauseTimelineButton(
    isPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp
) {
    IconButton(
        onClick = onToggle,
        modifier = modifier.size(size)
    ) {
        Icon(
            imageVector = if (isPlaying)
                Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = "Play / Pause",
            modifier = Modifier.size(size * 0.7f)
        )
    }
}

