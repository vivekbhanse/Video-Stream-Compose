package com.reminder.myottapp.models

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val isPaused: Boolean = false,
    val isEnded: Boolean = false
)

