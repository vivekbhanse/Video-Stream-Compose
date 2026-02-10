package com.reminder.myottapp.feature.player.screens

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayerTimeline(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit
) {
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableStateOf(currentPosition) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = formatTime(currentPosition),
            modifier = Modifier.padding(end = 8.dp)
        )

        Slider(
            value = if (isSeeking) seekPosition.toFloat() else currentPosition.toFloat(),
            onValueChange = {
                isSeeking = true
                seekPosition = it.toLong()
            },
            onValueChangeFinished = {
                onSeek(seekPosition)
                isSeeking = false
            },
            valueRange = 0f..duration.coerceAtLeast(1L).toFloat(),
            modifier = Modifier.weight(1f) // 🔑 slider fills space between texts
        )

        Text(
            text = formatTime(duration),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

